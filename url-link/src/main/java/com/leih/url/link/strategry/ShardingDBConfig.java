package com.leih.url.link.strategry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShardingDBConfig {
  private static final List<String> dbPrefix = new ArrayList<>();

  static {
    dbPrefix.add("0");
    dbPrefix.add("1");
    dbPrefix.add("a");
    //more suffixes can be added here
  }

  /**
   * Get random db prefix
   *
   * @return
   */
  public static String getRandomDBPrefix(String shortLinkCode) {
    int hashCode = shortLinkCode.hashCode();
    int index = Math.abs(hashCode) % dbPrefix.size();
    return dbPrefix.get(index);
  }
}
