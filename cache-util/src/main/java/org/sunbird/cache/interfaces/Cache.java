package org.sunbird.cache.interfaces;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Cache {

  static final String CACHE_CONFIG_FILE = "cache.conf";

  default Map<String, String> readConfig() {
    Map<String, String> props = new HashMap<>();
    com.typesafe.config.Config config = ConfigFactory.load(CACHE_CONFIG_FILE);
    Set<Map.Entry<String, ConfigValue>> configSet = config.entrySet();
    for (Map.Entry<String, ConfigValue> confEntry : configSet) {
      props.put(confEntry.getKey(), confEntry.getValue().unwrapped().toString());
    }
    return props;
  }

  public String get(String mapName, String key);

  public boolean put(String mapName, String key, String value);

  public boolean clear(String mapName);

  public void clearAll();

  boolean setMapExpiry(String name, long seconds);
}
