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
  }

  /**
   * Get random table suffix
   *
   * @return
   */
  public static String getRandomTableSuffix() {
    int index = random.nextInt(tableSuffix.size());
    return tableSuffix.get(index);
  }
}
