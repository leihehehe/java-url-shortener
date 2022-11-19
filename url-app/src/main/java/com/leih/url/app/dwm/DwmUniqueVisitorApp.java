package com.leih.url.app.dwm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.func.UniqueVisitorFilterFunction;
import com.leih.url.app.util.JsonUtil;
import com.leih.url.app.util.KafkaUtil;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

public class DwmUniqueVisitorApp {
  /** Define a source topic */
  public static final String SOURCE_TOPIC = "dwm_link_visit_topic";
  /** Define a consumer group */
  public static final String GROUP_ID = "dwm_link_visit_group";

  public static final String SINK_TOPIC = "dwm_unique_visitor_topic";

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //        env.setParallelism(1);
    // get input stream
    //    DataStream<String> ds = env.socketTextStream("127.0.0.1", 8088, "\n", 10000);
    FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
    DataStreamSource<String> ds = env.addSource(kafkaConsumer);
    // convert json data to ObjectNode
    SingleOutputStreamOperator<ObjectNode> jsonDS =
        ds.map(jsonStr -> JsonUtil.json2Obj(jsonStr, ObjectNode.class));
    // group
    KeyedStream<ObjectNode, String> keyedStream =
        jsonDS.keyBy(
            new KeySelector<ObjectNode, String>() {
              @Override
              public String getKey(ObjectNode jsonNodes) throws Exception {
                return jsonNodes.get("uid").textValue();
              }
            });
    // remove duplicates
    SingleOutputStreamOperator<ObjectNode> filterDS =
        keyedStream.filter(new UniqueVisitorFilterFunction());
    filterDS.print("unique visitor");
    // write to kafka
    SingleOutputStreamOperator<String> uniqueVisitorDS = filterDS.map(JsonUtil::obj2Json);
    FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
    uniqueVisitorDS.addSink(kafkaProducer);
    env.execute();
  }
}
