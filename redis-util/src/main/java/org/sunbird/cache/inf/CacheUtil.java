package org.sunbird.cache.inf;

import java.util.Map;

public interface CacheUtil {

  public  Map<String, String> readConfig();

  public  String get(String mapName , String key);

  public boolean put(String mapName , String key , String value);

  public boolean clear(String mapName);

  public void clearAll();
  
  boolean setMapExpiry(String name, long seconds);

}
