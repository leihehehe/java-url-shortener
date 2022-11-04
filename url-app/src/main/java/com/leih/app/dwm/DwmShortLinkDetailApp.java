package com.leih.app.dwm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.app.func.DeviceMapFunction;
import com.leih.app.func.IpLocationMapFunction;
import com.leih.app.model.DeviceInfo;
import com.leih.app.model.ShortLinkDetail;
import com.leih.app.util.CommonUtil;
import com.leih.app.util.JsonUtil;
import com.leih.app.util.KafkaUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

public class DwmShortLinkDetailApp {
  /** Define a source topic */
  public static final String SOURCE_TOPIC = "dwd_link_visit_topic";
  /** Define a consumer group */
  public static final String GROUP_ID = "dwm_short_link_group";
  public static final String SINK_TOPIC = "dwm_link_visit_group";

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);
    // get input stream
    //    DataStream<String> ds = env.socketTextStream("127.0.0.1", 8088, "\n", 10000);
    FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
    // use kafka as a source
    DataStreamSource<String> ds = env.addSource(kafkaConsumer);
    // device info
    SingleOutputStreamOperator<ShortLinkDetail> deviceOS = ds.map(new DeviceMapFunction());
    deviceOS.print("dwm device info added:");
    // location info(country)
    SingleOutputStreamOperator<String> detailOS = deviceOS.map(new IpLocationMapFunction());
    detailOS.print("dwm ip location added:");
    FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
    // store into dwm through kafka
    detailOS.addSink(kafkaProducer);
    env.execute();
  }
}
