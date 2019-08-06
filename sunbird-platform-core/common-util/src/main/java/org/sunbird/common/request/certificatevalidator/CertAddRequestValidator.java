package org.sunbird.common.request.certificatevalidator;

import org.apache.commons.validator.UrlValidator;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

import java.util.ArrayList;
import java.util.List;


/**
 * this class is responsible to validate the certificate add request
 *
 * @author anmolgupta
 */
public class CertAddRequestValidator extends BaseRequestValidator {

    private Request request;
    private static UrlValidator urlValidator=new UrlValidator();
    static List<String> mandatoryParamsList = new ArrayList<>();

    static {

        mandatoryParamsList.add(JsonKey.ID);
        mandatoryParamsList.add(JsonKey.ACCESS_CODE);
        mandatoryParamsList.add(JsonKey.PDF_URL);
        mandatoryParamsList.add(JsonKey.JSON_URL);
        mandatoryParamsList.add(JsonKey.USER_ID);
    }

    private CertAddRequestValidator(Request request) {
        this.request = request;
    }


    /**
     * this method we should use to get the instance of the validator class
     * @param request
     * @return
     */
    public static CertAddRequestValidator getInstance(Request request) {
        return new CertAddRequestValidator(request);
    }

    /**
     * this method should be call to validate the request
     */
    public void validate() {
        checkMandatoryFieldsPresent(request.getRequest(), mandatoryParamsList);
        validateUrls();

    }

    private void validateUrl(String url){
        if(!urlValidator.isValid(url)){
            ProjectCommonException.throwClientErrorException(ResponseCode.invalidUrl);
        }
    }
    private void validateUrls(){
        validateUrl((String) request.get(JsonKey.PDF_URL));
        validateUrl((String)request.get(JsonKey.JSON_URL));
    }

}
