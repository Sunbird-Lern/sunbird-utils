package org.sunbird.common.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * 
 * @author Manzarul
 *
 */
public class ExecutionContext {

    public static final String USER_ID = "userId";
    public static final String USER_ROLE = "userRole";
    private Stack<String> serviceCallStack = new Stack<>();

    private Map<String, Map<String, Object>> contextStackValues = new HashMap<>();
    private Map<String, Object> globalContext = new HashMap<>();
    private Map<String, Object> requestContext = new HashMap<>();

    public Map<String, Object> getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(Map<String, Object> requestContext) {
        this.requestContext = requestContext;
        initializeGlobalContext(ExecutionContext.getCurrent());
    }

    private static ThreadLocal<ExecutionContext> context = new ThreadLocal<ExecutionContext>() {

        @Override
        protected ExecutionContext initialValue() {
            ExecutionContext context = new ExecutionContext();
            return context;
        }

    };

    private static void initializeGlobalContext(ExecutionContext context) {

        Map<String , Object> map = new HashMap<>();
        String producerInstanceId = System.getenv(JsonKey.PRODUCER_INSTTANCE_ID);
        if(ProjectUtil.isStringNullOREmpty(producerInstanceId)){
            producerInstanceId = PropertiesCache.getInstance().getProperty(JsonKey.PRODUCER_INSTTANCE_ID);
        }

        String pid = System.getenv(JsonKey.PRODUCER_ID);
        if(ProjectUtil.isStringNullOREmpty(pid)) {
            pid =PropertiesCache.getInstance().getProperty(JsonKey.PRODUCER_ID);
        }
        String pdataVersion = System.getenv(JsonKey.PDATA_VERSION);
        if(ProjectUtil.isStringNullOREmpty(pdataVersion)){
            pdataVersion = PropertiesCache.getInstance().getProperty(JsonKey.PDATA_VERSION);
        }

        map.put(JsonKey.PRODUCER_ID, pid);
        map.put(JsonKey.PRODUCER_INSTTANCE_ID, producerInstanceId);
        map.put(JsonKey.PRODUCER_VERSION , pdataVersion);

        context.getGlobalContext().putAll(map);
    }

    public static ExecutionContext getCurrent() {
        return context.get();
    }
    
    public static void setRequestId(String requestId) {
        ExecutionContext.getCurrent().getGlobalContext().put(HeaderParam.REQUEST_ID.getParamName(), requestId);
    }
    
    public static String getRequestId() {
        return (String) ExecutionContext.getCurrent().getGlobalContext().get(HeaderParam.REQUEST_ID.getParamName());
    }

    public Map<String, Object> getContextValues() {
        String serviceCallStack = getServiceCallStack();
        Map<String, Object> contextValues = contextStackValues.get(serviceCallStack);
        if (contextValues == null) {
            contextValues = new HashMap<>();
            setContextValues(contextValues, serviceCallStack);
        }

        return contextStackValues.get(serviceCallStack);
    }

    public void setContextValues(Map<String, Object> currentContextValues) {
        this.contextStackValues.put(getServiceCallStack(), new HashMap<String, Object>(currentContextValues));
    }

    public void setContextValues(Map<String, Object> currentContextValues, String serviceCallStack) {
        this.contextStackValues.put(serviceCallStack, new HashMap<String, Object>(currentContextValues));
    }

    public void removeContext() {
        this.contextStackValues.remove(getServiceCallStack());
    }

    public void cleanup() {
        removeContext();
        pop();
        if (serviceCallStack.size() == 0) {
            this.globalContext.remove(HeaderParam.REQUEST_ST_ED_PATH.getParamName());
        }
    }

    // TODO move Response out of context
    public Response getResponse() {
        Response contextResponse = (Response) ExecutionContext.getCurrent().getContextValues().get("RESPONSE");
        if (contextResponse == null) {
            contextResponse = new Response();
            ExecutionContext.getCurrent().getContextValues().put("RESPONSE", contextResponse);
        }
        return contextResponse;
    }

    public void push(String methodName) {
        serviceCallStack.push(methodName);
    }

    public String pop() {
        return serviceCallStack.pop();
    }

    public String peek() {
        return serviceCallStack.peek();
    }

    public String getServiceCallStack() {
        String serviceCallPath = "";
        for (String value : serviceCallStack) {
            if ("".equals(serviceCallPath))
                serviceCallPath = value;
            else
                serviceCallPath = serviceCallPath + "/" + value;
        }

        if ("".equals(serviceCallPath)) {
            serviceCallStack.push("default");
            return "default";
        }
        return serviceCallPath;
    }

    public Map<String, Object> getGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(Map<String, Object> globalContext) {
        this.globalContext = globalContext;
    }
}
