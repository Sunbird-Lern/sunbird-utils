package org.sunbird.telemetry.collector;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.telemetry.util.TelemetryGenerator;

/**
 * Created by arvind on 5/1/18.
 */
public class TelemetryDataAssemblerImpl implements  TelemetryDataAssembler{

  public TelemetryDataAssemblerImpl(){

  }

  @Override
  public String audit(Map<String, Object> context, Map<String, Object> params) {
    String event = TelemetryGenerator.audit(context, params);
    return event;
  }

  @Override
  public String search(Map<String, Object> context, Map<String, Object> params) {
    String event = TelemetryGenerator.search(context, params);
    return event;
  }

  @Override
  public String log(Map<String, Object> context, Map<String, Object> params) {
    String event = TelemetryGenerator.log(context, params);
    return event;
  }

  @Override
  public String error(Map<String, Object> context, Map<String, Object> params) {
    String event = TelemetryGenerator.error(context, params);
    return event;
  }

  private static String getContextValue(String key, String defaultValue) {
    String value = (String) ExecutionContext.getCurrent().getGlobalContext().get(key);
    if (StringUtils.isBlank(value))
      return defaultValue;
    else
      return value;
  }

}
