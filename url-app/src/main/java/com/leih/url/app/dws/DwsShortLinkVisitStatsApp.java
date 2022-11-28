package com.leih.url.app.dws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.model.ShortLinkVisitStats;
import com.leih.url.app.util.ClickHouseUtil;
import com.leih.url.app.util.JsonUtil;
import com.leih.url.app.util.KafkaUtil;
import com.leih.url.app.util.TimeUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
/**
 * Read data from DWM(Data Warehouse Middle) and write it to Clickhouse - DWS(Data Warehouse
 * Service)
 */
public class DwsShortLinkVisitStatsApp {
  /** Define a source topic */
  public static final String SHORT_LINK_DETAIL_SOURCE_TOPIC = "dwm_link_visit_topic";
  /** Define a consumer group */
  public static final String SHORT_LINK_DETAIL_GROUP_ID = "dws_link_visit_group";
  /** Define a source topic */
  public static final String UNIQUE_VISITOR_SOURCE_TOPIC = "dwm_unique_visitor_topic";
  /** Define a consumer group */
  public static final String UNIQUE_VISITOR_GROUP_ID = "dws_unique_visitor_group";

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //    env.setParallelism(1);
    // get data stream from dwm
    FlinkKafkaConsumer<String> shortLinkDetailConsumer =
        KafkaUtil.getKafkaConsumer(SHORT_LINK_DETAIL_SOURCE_TOPIC, SHORT_LINK_DETAIL_GROUP_ID);
    DataStreamSource<String> shortLinkDetailDS = env.addSource(shortLinkDetailConsumer);
    FlinkKafkaConsumer<String> uniqueVisitorConsumer =
        KafkaUtil.getKafkaConsumer(UNIQUE_VISITOR_SOURCE_TOPIC, UNIQUE_VISITOR_GROUP_ID);
    DataStreamSource<String> uniqueVisitorDS = env.addSource(uniqueVisitorConsumer);
    // Convert
    SingleOutputStreamOperator<ShortLinkVisitStats> shortLinkDetailMapDS =
        shortLinkDetailDS.map(
            new MapFunction<String, ShortLinkVisitStats>() {
              @Override
              public ShortLinkVisitStats map(String s) throws Exception {
                ShortLinkVisitStats shortLinkVisitStats = parseVisitStats(s);
                shortLinkVisitStats.setPv(1L);
                // do not care about unique visitor
                shortLinkVisitStats.setUv(0L);
                return shortLinkVisitStats;
              }
            });
    shortLinkDetailDS.print("short link detail conversion - pv&uv");
    SingleOutputStreamOperator<ShortLinkVisitStats> uniqueVisitorMapDS =
        uniqueVisitorDS.map(
            new MapFunction<String, ShortLinkVisitStats>() {
              @Override
              public ShortLinkVisitStats map(String s) throws Exception {
                ShortLinkVisitStats shortLinkVisitStats = parseVisitStats(s);
                // do not care about page view
                shortLinkVisitStats.setPv(0L);
                shortLinkVisitStats.setUv(1L);
                return shortLinkVisitStats;
              }
            });
    // union
      uniqueVisitorMapDS.print("unique visitor conversion - pv&uv");
    DataStream<ShortLinkVisitStats> unionDS = shortLinkDetailMapDS.union(uniqueVisitorMapDS);
    // watermark
    SingleOutputStreamOperator<ShortLinkVisitStats> watermarksDS =
        unionDS.assignTimestampsAndWatermarks(
            WatermarkStrategy.<ShortLinkVisitStats>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                .withTimestampAssigner((event, timestamp) -> event.getVisitTime()));
    // group
    KeyedStream<
            ShortLinkVisitStats,
            Tuple8<String, String, Integer, String, String, String, String, String>>
        keyedStream =
            watermarksDS.keyBy(
                new KeySelector<
                    ShortLinkVisitStats,
                    Tuple8<String, String, Integer, String, String, String, String, String>>() {
                  @Override
                  public Tuple8<String, String, Integer, String, String, String, String, String>
                      getKey(ShortLinkVisitStats obj) throws Exception {
                    return Tuple8.of(
                        obj.getCode(),
                        obj.getReferer(),
                        obj.getIsNew(),
                        obj.getCountry(),
                        obj.getIp(),
                        obj.getBrowserName(),
                        obj.getOs(),
                        obj.getDeviceType());
                  }
                });
    // window
    WindowedStream<
            ShortLinkVisitStats,
            Tuple8<String, String, Integer, String, String, String, String, String>,
            TimeWindow>
        windowedStream = keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(3)));
    // Aggregation
    SingleOutputStreamOperator<Object> reduceDS =
        windowedStream.reduce(
            new ReduceFunction<ShortLinkVisitStats>() {
              @Override
              public ShortLinkVisitStats reduce(
                  ShortLinkVisitStats shortLinkVisitStats, ShortLinkVisitStats t1)
                  throws Exception {
                shortLinkVisitStats.setPv(shortLinkVisitStats.getPv() + t1.getPv());
                shortLinkVisitStats.setUv(shortLinkVisitStats.getUv() + t1.getUv());
                return shortLinkVisitStats;
              }
            },
            new ProcessWindowFunction<
                ShortLinkVisitStats,
                Object,
                Tuple8<String, String, Integer, String, String, String, String, String>,
                TimeWindow>() {
              @Override
              public void process(
                  Tuple8<String, String, Integer, String, String, String, String, String> tuple,
                  ProcessWindowFunction<
                              ShortLinkVisitStats,
                              Object,
                              Tuple8<
                                  String, String, Integer, String, String, String, String, String>,
                              TimeWindow>
                          .Context
                      context,
                  Iterable<ShortLinkVisitStats> iterable,
                  Collector<Object> collector)
                  throws Exception {
                for (ShortLinkVisitStats visitStats : iterable) {
                  String startTime =
                      TimeUtil.format(
                          context.window().getStart(),
                          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                  String endTime =
                      TimeUtil.format(
                          context.window().getEnd(),
                          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                  visitStats.setStartTime(startTime);
                  visitStats.setEndTime(endTime);
                  collector.collect(visitStats);
                }
              }
            });
    reduceDS.print(">>>>>>");
    // output to clickhouse
    String sql = "insert into visit_stats values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    SinkFunction jdbcSink = ClickHouseUtil.getJdbcSink(sql);
    reduceDS.addSink(jdbcSink);
    env.execute();
  }

  private static ShortLinkVisitStats parseVisitStats(String value) {
    ObjectNode jsonObj = JsonUtil.json2Obj(value, ObjectNode.class);

    ShortLinkVisitStats visitStatsDO =
        ShortLinkVisitStats.builder()
            .code(jsonObj.get("code").asText())
            .accountNo(jsonObj.get("accountNo").asLong())
            .visitTime(jsonObj.get("visitTime").asLong())
            .referer(jsonObj.get("referer").asText())
            .isNew(jsonObj.get("isNew").asInt())
            .uid(jsonObj.get("uid").asText())
            .country(jsonObj.get("country").asText())
            .ip(jsonObj.get("ip").asText())
            .browserName(jsonObj.get("browserName").asText())
            .os(jsonObj.get("os").asText())
            .osVersion(jsonObj.get("osVersion").asText())
            .deviceType(jsonObj.get("deviceType").asText())
            .deviceManufacturer(jsonObj.get("deviceManufacturer").asText())
            .build();
    return visitStatsDO;
  }
}
