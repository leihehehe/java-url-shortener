package com.leih.url.app.dwd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.func.VisitorMapFunction;
import com.leih.url.app.util.CommonUtil;
import com.leih.url.app.util.JsonUtil;
import com.leih.url.app.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/** Read data from ODS(Operation Data Store) and write it to DWD(Data Warehouse Detail) */
@Slf4j
public class DwdShortLinkLogApp {
  /** Define a source topic */
  public static final String SOURCE_TOPIC = "ods_link_visit_topic";
  /** Define a consumer group */
  public static final String GROUP_ID = "dwd_short_link_group";
  /** Define a sink topic */
  public static final String SINK_TOPIC = "dwd_link_visit_topic";

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //    env.setParallelism(1);
    // get input stream
    //        DataStream<String> ds = env.socketTextStream("127.0.0.1", 8088, "\n", 10000);
    FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
    // use kafka as a source
    DataStreamSource<String> ds = env.addSource(kafkaConsumer);
    //    ds.print();
    // deal with the raw data
    SingleOutputStreamOperator<ObjectNode> jsonDS =
        ds.flatMap(
            new FlatMapFunction<String, ObjectNode>() {
              @Override
              public void flatMap(String s, Collector<ObjectNode> collector) throws Exception {
                ObjectNode jsonNodes = JsonUtil.json2Obj(s, ObjectNode.class);
                String uid = getDeviceId(jsonNodes);
                jsonNodes.put("uid", uid);
                jsonNodes.put("referer", getReferer(jsonNodes));
                collector.collect(jsonNodes);
              }
            });
    // group by uid
    KeyedStream<ObjectNode, String> keyedStream =
        jsonDS.keyBy(
            (KeySelector<ObjectNode, String>) objectNode -> objectNode.get("uid").asText());
    // identify the visitor is old or new for every grouped stream
    SingleOutputStreamOperator<String> dsWithVisitorState =
        keyedStream.map(new VisitorMapFunction());
    dsWithVisitorState.print("dwd_visitors");
    // store into dwd through kafka
    FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
    dsWithVisitorState.addSink(kafkaProducer);
    env.execute();
  }

  /**
   * Generate a unique id for a device
   *
   * @param jsonNodes
   * @return
   */
  private static String getDeviceId(ObjectNode jsonNodes) {
    Map<String, String> map = new TreeMap<>();
    try {
      map.put("ip", jsonNodes.get("ip").asText());
      map.put("event", jsonNodes.get("event").asText());
      map.put("bizId", jsonNodes.get("bizId").asText());
      map.put("user-agent", jsonNodes.get("content").get("user-agent").asText());
      return CommonUtil.generateUniqueDeviceId(map);
    } catch (Exception e) {
      log.error("Failed to generate a unique device id");
      return null;
    }
  }

  public static String getReferer(ObjectNode objectNode) {
    try {
      String referer = objectNode.get("content").get("referer").asText();
      if (StringUtils.isNotBlank(referer)) {
        URL url = new URL(referer);
        return url.getHost();
      }
    } catch (Exception e) {
      log.error("failed to get the referer:{}", e.getMessage());
    }
    return "";
  }
}
