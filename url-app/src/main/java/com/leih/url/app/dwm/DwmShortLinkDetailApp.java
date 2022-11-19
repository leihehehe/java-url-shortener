package com.leih.url.app.dwm;

import com.leih.url.app.func.AsyncIpLocationFunction;
import com.leih.url.app.func.DeviceMapFunction;
import com.leih.url.app.model.ShortLinkDetail;
import com.leih.url.app.util.KafkaUtil;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.concurrent.TimeUnit;

/** Read data from DWD(Data Warehouse Detail) and write it to DWM(Data Warehouse Middle) */
public class DwmShortLinkDetailApp {
  /** Define a source topic */
  public static final String SOURCE_TOPIC = "dwd_link_visit_topic";
  /** Define a consumer group */
  public static final String GROUP_ID = "dwm_short_link_group";

  public static final String SINK_TOPIC = "dwm_link_visit_topic";

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //    env.setParallelism(1);
    // get input stream
    //    DataStream<String> ds = env.socketTextStream("127.0.0.1", 8088, "\n", 10000);
    FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);
    // use kafka as a source
    DataStreamSource<String> ds = env.addSource(kafkaConsumer);
    // device info
    SingleOutputStreamOperator<ShortLinkDetail> deviceDS = ds.map(new DeviceMapFunction());
    deviceDS.print("dwm device info added:");
    // location info(country)
    SingleOutputStreamOperator<String> detailDS =
        AsyncDataStream.unorderedWait(
            deviceDS, new AsyncIpLocationFunction(), 4000, TimeUnit.MILLISECONDS, 100);
    // SingleOutputStreamOperator<String> detailDS = deviceDS.map(new IpLocationMapFunction());
    detailDS.print("dwm ip location added:");
    FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);
    // store into dwm through kafka
    detailDS.addSink(kafkaProducer);
    env.execute();
  }
}
