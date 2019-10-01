package org.sunbird.cache.interfaces;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Cache {
  static final String CACHE_CONFIG_FILE = "cache.conf";

  default Map<String, String> readConfig() {
    Map<String, String> properties = new HashMap<>();

    Config config = ConfigFactory.load(CACHE_CONFIG_FILE);

    Set<Map.Entry<String, ConfigValue>> configEntrySet = config.entrySet();

    for (Map.Entry<String, ConfigValue> configEntry : configEntrySet) {
      properties.put(configEntry.getKey(), configEntry.getValue().unwrapped().toString());
    }

    return properties;
  }

  public String get(String mapName, String key);

  public Object get(String mapName, String key, Class<?> cls);

  public boolean put(String mapName, String key, String value);

  public boolean put(String mapName, String key, Object value);

  public boolean clear(String mapName);

  public void clearAll();

  boolean setMapExpiry(String name, long seconds);
}
