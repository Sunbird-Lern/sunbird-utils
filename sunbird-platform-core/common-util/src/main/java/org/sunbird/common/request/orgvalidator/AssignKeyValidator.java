package org.sunbird.common.request.orgvalidator;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

import java.text.MessageFormat;
import java.util.List;

/**
 * this class is used to validate the request of the OrgAssignKeys Controller
 * @author anmolgupta
 */
public class AssignKeyValidator extends BaseRequestValidator {


    private Request request;

    private AssignKeyValidator(Request request) {
        this.request = request;
    }


    /**
     * this method should be used to get the instance of the class
     * @param request
     * @return
     */
    public static AssignKeyValidator getInstance(Request request){
        return new AssignKeyValidator(request);
    }


    /**
     * this method should be used to validate the OrgAssignKeysController request.
     */
    public void validate(){
        id();
        signKeys();
        encKeys();

    }

    private void id(){
        validateParam(
                (String) request.getRequest().get(JsonKey.ID),
                ResponseCode.mandatoryParamsMissing,
                JsonKey.ID);
    }

    private void signKeys(){
        validateKeyPresence(JsonKey.SIGN_KEYS);
        validateListTypeObject(JsonKey.SIGN_KEYS);
        validateSize(JsonKey.SIGN_KEYS);
    }

    private void encKeys(){
        validateKeyPresence(JsonKey.ENC_KEYS);
        validateListTypeObject(JsonKey.ENC_KEYS);
        validateSize(JsonKey.ENC_KEYS);
    }

    private void validateListTypeObject(String key){
        if(!(request.get(key) instanceof List)){
            throw new ProjectCommonException(
                    ResponseCode.dataTypeError.getErrorCode(),
                    MessageFormat.format(
                            ResponseCode.dataTypeError.getErrorMessage(), key, "List"),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }
    private void validateKeyPresence(String key){
        if(!request.getRequest().containsKey(key)){
            throw new ProjectCommonException(
                    ResponseCode.mandatoryParamsMissing.getErrorCode(),
                    ResponseCode.mandatoryParamsMissing.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),key);
        }
    }

    private void validateSize(String key){
        if(((List)request.get(key)).size()==0){
            throw new ProjectCommonException(
                    ResponseCode.errorInvalidParameterSize.getErrorCode(),
                    ResponseCode.errorInvalidParameterSize.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),key,">1","0");
        }

    }
}
