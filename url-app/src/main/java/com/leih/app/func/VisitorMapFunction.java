package com.leih.app.func;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.app.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.codehaus.jackson.JsonNode;

@Slf4j
public class VisitorMapFunction extends RichMapFunction<ObjectNode, String> {
  private ValueState<String> visitorDateState;

  @Override
  public void open(Configuration parameters) throws Exception {
    // initialize value state
    visitorDateState =
        getRuntimeContext().getState(new ValueStateDescriptor<String>("", String.class));
  }

  @Override
  public String map(ObjectNode objectNode) throws Exception {
    // get previous date state
    String previousDateState = visitorDateState.value();
    // get current timestamp
    Long timestamp = objectNode.get("timestamp").asLong();
    String currentDate = TimeUtil.format(timestamp);

    if (StringUtils.isNotBlank(previousDateState)
        && previousDateState.equalsIgnoreCase(currentDate)) {
      objectNode.put("isNew", 0);
      log.info("A old visitor:{}", currentDate);
    } else {
      objectNode.put("isNew", 1);
      visitorDateState.update(currentDate);
      log.info("A new visitor:{}", currentDate);
    }
    return objectNode.toString();
  }
}
