package org.sunbird.common.request.certificatevalidator;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.BaseRequestValidator;
import org.sunbird.common.request.Request;

import java.util.ArrayList;
import java.util.List;


/**
 * this class is responsible to validate the certificate add request
 *
 * @author anmolgupta
 */
public class CertAddRequestValidator extends BaseRequestValidator {

    private Request request;
    static List<String> mandatoryParamsList = new ArrayList<>();

    static {

        mandatoryParamsList.add(JsonKey.CERT_ID);
        mandatoryParamsList.add(JsonKey.ACCESS_CODE);
        mandatoryParamsList.add(JsonKey.PDF_URL);
        mandatoryParamsList.add(JsonKey.JSON_URL);
        mandatoryParamsList.add(JsonKey.USER_ID);
    }

    private CertAddRequestValidator(Request request) {
        this.request = request;
    }

    public static CertAddRequestValidator getInstance(Request request) {
        return new CertAddRequestValidator(request);
    }

    public void validate() {
        checkMandatoryFieldsPresent(request.getRequest(), mandatoryParamsList);
    }

}
