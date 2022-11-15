package com.leih.url.app.func;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leih.url.app.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;

import java.time.format.DateTimeFormatter;

/**
 *
 *  Check if the user has visited (using stateTtlConfig)
 *
 */
public class UniqueVisitorFilterFunction extends RichFilterFunction<ObjectNode> {
  private ValueState<String> lastVisitorDateState = null;

  @Override
  public void open(Configuration parameters) throws Exception {
    ValueStateDescriptor<String> visitDateStateDes =
        new ValueStateDescriptor<>("visitDateState", String.class);
    StateTtlConfig stateTtlConfig = StateTtlConfig.newBuilder(Time.days(1)).build();
    visitDateStateDes.enableTimeToLive(stateTtlConfig);
    ValueState<String> state = getRuntimeContext().getState(visitDateStateDes);
    lastVisitorDateState = state;
  }

  @Override
  public void close() throws Exception {
    super.close();
  }

  @Override
  public boolean filter(ObjectNode jsonNodes) throws Exception {
    Long visitTime = jsonNodes.get("visitTime").asLong();
    String uid = jsonNodes.get("uid").asText();
    String currentVisitDate = TimeUtil.format(visitTime, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    // get previous visit time
    String lastVisitDate = lastVisitorDateState.value();
    if (StringUtils.isNotBlank(lastVisitDate) && currentVisitDate.equalsIgnoreCase(lastVisitDate)) {
      System.out.println(uid + " has visited the link on " + currentVisitDate);
      return false;
    } else {
      System.out.println(uid + "'s first visit on " + currentVisitDate);
      lastVisitorDateState.update(currentVisitDate);
      return true;
    }
  }
}
