/** */
package org.sunbird.common.request;

import java.util.HashMap;
import java.util.Map;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;

public class HeaderBuilder {
  private Map<String, String> headers;

  public HeaderBuilder() {
    this.headers = new HashMap<String, String>();
  }

  public HeaderBuilder(Map<String, String> headers) {
    this.headers = new HashMap<String, String>();
    this.headers.putAll(headers);
  }

  public HeaderBuilder add(String key, String value) {
    this.headers.put(key, value);
    return this;
  }

  public HeaderBuilder withLogLevel(LoggerEnum logger) {
    this.headers.put(JsonKey.LOG_LEVEL_HEADER_KEY, logger.name());
    return this;
  }

  public Map<String, String> build() {
    if (ExecutionContext.getCurrent() != null
        && ExecutionContext.getCurrent().getRequestContext() != null) {
      if (ExecutionContext.getCurrent().getRequestContext().containsKey(JsonKey.DEVICE_ID)) {
        this.headers.put(
            HeaderParam.X_Device_ID.getParamName(),
            (String) ExecutionContext.getCurrent().getRequestContext().get(JsonKey.DEVICE_ID));
      }
      if (ExecutionContext.getCurrent().getRequestContext().containsKey(JsonKey.REQUEST_ID)) {
        this.headers.put(
            JsonKey.MESSAGE_ID,
            (String) ExecutionContext.getCurrent().getRequestContext().get(JsonKey.REQUEST_ID));
      }
      if (ExecutionContext.getCurrent()
              .getRequestContext()
              .containsKey(JsonKey.LOG_LEVEL_HEADER_KEY)
          && !this.headers.containsKey(JsonKey.LOG_LEVEL_HEADER_KEY)) {
        this.headers.put(
            JsonKey.LOG_LEVEL_HEADER_KEY,
            (String)
                ExecutionContext.getCurrent()
                    .getRequestContext()
                    .get(JsonKey.LOG_LEVEL_HEADER_KEY));
      }
    }
    return this.headers;
  }
}
