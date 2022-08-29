package com.zxl.haze.redis.cache;


import java.time.temporal.ChronoUnit;
import java.util.Random;


public enum CacheEnum {

  S5("S5", 5, ChronoUnit.SECONDS),
  S30("S30", 5, ChronoUnit.SECONDS),
  M5("M5", 5, ChronoUnit.MINUTES),
  M30("M30", 30, ChronoUnit.MINUTES),
  H5("M5", 5, ChronoUnit.HOURS),
  H30("H30", 30, ChronoUnit.HOURS),
  ;


  public final String name;
  public final int ttl;
  public final ChronoUnit chronoUnit;

  CacheEnum(String name, int ttl, ChronoUnit chronoUnit) {
    this.name = name;
    this.ttl = ttl + new Random().nextInt(ttl);
    this.chronoUnit = chronoUnit;
  }


  public static void main(String[] args) {
    System.out.println(M30.ttl);
  }

}
