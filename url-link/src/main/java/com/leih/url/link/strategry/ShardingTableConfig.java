package com.leih.url.link.strategry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShardingTableConfig {
  private static final List<String> tableSuffix = new ArrayList<>();
  private static final Random random = new Random();

  static {
    tableSuffix.add("0");
    tableSuffix.add("a");
    //more suffixes can be added here
  }

  /**
   * Get random table suffix
   *
   * @return
   */
  public static String getRandomTableSuffix(String code) {
    int hashCode = code.hashCode();
    int index = Math.abs(hashCode) % tableSuffix.size();
    return tableSuffix.get(index);
  }
}
