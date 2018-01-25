package org.sunbird.telemetry.util.lmaxdisruptor;

import com.lmax.disruptor.EventHandler;
import java.util.List;
import java.util.Map;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.telemetry.collector.TelemetryAssemblerFactory;
import org.sunbird.telemetry.collector.TelemetryDataAssembler;
import org.sunbird.telemetry.util.TelemetryFlush;

/**
 * Created by arvind on 10/1/18.
 */
public class WriteEventHandler implements EventHandler<Request> {


  private TelemetryFlush telemetryFlush = TelemetryFlush.getInstance();
  private TelemetryDataAssembler telemetryDataAssembler = TelemetryAssemblerFactory.get();

  @Override
  public void onEvent(Request request, long l, boolean b) throws Exception {
    String eventType = (String) request.getRequest().get(JsonKey.TELEMETRY_EVENT_TYPE);

    if("AUDIT".equalsIgnoreCase(eventType)){
      System.out.println("TELEMETRY EVENT IS "+eventType);
      processAuditEvent(request);
    }else if("SEARCH".equalsIgnoreCase(eventType)){
      processSearchEvent(request);
    }else if("ERROR".equalsIgnoreCase(eventType)){
      processErrorEvent(request);
    }else if("LOG".equalsIgnoreCase(eventType)){
      processLogEvent(request);
    }else{
      // logg error not an valid telemetry event ...
    }

  }

  private void processLogEvent(Request request) {

    Map<String , Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
    Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
    String telemetry= telemetryDataAssembler.log(context, params);
    telemetryFlush.flushTelemetry(telemetry);

  }

  private void processErrorEvent(Request request) {

    Map<String , Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
    Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
    String telemetry= telemetryDataAssembler.error(context, params);
    telemetryFlush.flushTelemetry(telemetry);

  }

  private void processSearchEvent(Request request) {

    Map<String , Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
    Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
    String telemetry= telemetryDataAssembler.search(context, params);
    telemetryFlush.flushTelemetry(telemetry);

  }

  private void processAuditEvent(Request request) {
    Map<String , Object> context = (Map<String, Object>) request.get(JsonKey.CONTEXT);
    Map<String , Object> targetObject = (Map<String, Object>) request.get(JsonKey.TARGET_OBJECT);
    List<Map<String , Object>> correlatedObjects = (List<Map<String, Object>>) request.get(JsonKey.CORRELATED_OBJECTS);
    Map<String, Object> params = (Map<String, Object>) request.get(JsonKey.PARAMS);
    params.put(JsonKey.TARGET_OBJECT, targetObject);
    params.put(JsonKey.CORRELATED_OBJECTS, correlatedObjects);
    String telemetry= telemetryDataAssembler.audit(context, params);
    telemetryFlush.flushTelemetry(telemetry);
  }
}
