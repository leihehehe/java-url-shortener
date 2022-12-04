package com.leih.url.app.util;

import com.leih.url.app.model.ShortLinkVisitStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ClickHouseUtil {
  private static String CLICK_HOUSE_SERVER = null;
  private static String CLICK_HOUSE_DRIVER = null;
  private static String CLICK_HOUSE_USERNAME=null;
  private static String CLICK_HOUSE_PASSWORD=null;
  static {
    Properties properties = new Properties();
    InputStream in = KafkaUtil.class.getClassLoader().getResourceAsStream("application.properties");
    try {
      properties.load(in);
    } catch (IOException e) {
      log.error("Failed to get ClickHouse config,{}", e.getMessage());
    }
    String clickhouse_host= System.getenv().get("CLICKHOUSE_HOST");
    CLICK_HOUSE_SERVER = properties.getProperty("clickhouse.servers").replace("${CLICKHOUSE_HOST}",clickhouse_host);
    String clickhouse_username= System.getenv().get("CLICKHOUSE_USERNAME");
    CLICK_HOUSE_USERNAME=properties.getProperty("clickhouse.username").replace("${CLICKHOUSE_USERNAME}",clickhouse_username);
    String clickhouse_password= System.getenv().get("CLICKHOUSE_PASSWORD");
    CLICK_HOUSE_PASSWORD=properties.getProperty("clickhouse.password").replace("${CLICKHOUSE_PASSWORD}",clickhouse_password);
    CLICK_HOUSE_DRIVER=properties.getProperty("clickhouse.driver");
  }

  public static SinkFunction getJdbcSink(String sql) {
    SinkFunction<ShortLinkVisitStats> sinkFunction = JdbcSink.sink(
            sql,
            new JdbcStatementBuilder<ShortLinkVisitStats>() {
              @Override
              public void accept(
                      PreparedStatement preparedStatement, ShortLinkVisitStats shortLinkVisitStats)
                      throws SQLException {
                preparedStatement.setObject(1,shortLinkVisitStats.getCode());
                preparedStatement.setObject(2,shortLinkVisitStats.getReferer());
                preparedStatement.setObject(3,shortLinkVisitStats.getIsNew());
                preparedStatement.setObject(4,shortLinkVisitStats.getAccountNo());
                preparedStatement.setObject(5,shortLinkVisitStats.getCountry());
                preparedStatement.setObject(6,shortLinkVisitStats.getIp());
                preparedStatement.setObject(7,shortLinkVisitStats.getBrowserName());
                preparedStatement.setObject(8,shortLinkVisitStats.getOs());
                preparedStatement.setObject(9,shortLinkVisitStats.getDeviceType());
                preparedStatement.setObject(10,shortLinkVisitStats.getPv());
                preparedStatement.setObject(11,shortLinkVisitStats.getUv());
                preparedStatement.setObject(12,shortLinkVisitStats.getStartTime());
                preparedStatement.setObject(13,shortLinkVisitStats.getEndTime());
                preparedStatement.setObject(14,shortLinkVisitStats.getVisitTime());

              }
            },
            //batch
            new JdbcExecutionOptions.Builder().withBatchSize(10).build(),
            new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withUrl(CLICK_HOUSE_SERVER)
                    .withDriverName(CLICK_HOUSE_DRIVER)
                    .withUsername(CLICK_HOUSE_USERNAME)
                    .withPassword(CLICK_HOUSE_PASSWORD)
                    .build());
    return sinkFunction;
  }
}
