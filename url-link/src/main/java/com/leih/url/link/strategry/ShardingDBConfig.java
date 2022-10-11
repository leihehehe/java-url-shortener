package com.leih.url.link.strategry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShardingDBConfig {
  private static final List<String> dbPrefix = new ArrayList<>();
  private static final Random random = new Random();

  static {
    dbPrefix.add("0");
    dbPrefix.add("1");
    dbPrefix.add("a");
  }

  /**
   * Get random db prefix
   *
   * @return
   */
  public static String getRandomDBPrefix() {
    int index = random.nextInt(dbPrefix.size());
    return dbPrefix.get(index);
  }
}
