package org.sunbird.validator.initialisation;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * Testcases for Initialisation Request Validator
 *
 * @author Loganathan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.security.*"})
public class InitialisationRequestValidatorTest {

  private String ORG_NAME = "org-name";
  private String CHANNEL = "channel";
  private String REMOTE_ADDRESS = "127.0.0.1";
  private InitialisationRequestValidator validator = new InitialisationRequestValidator();

  @Test
  public void testValidateCreateFirstRootOrg() {
    Request request = new Request();
    Map<String, Object> rootOrgData = new HashMap<>();
    rootOrgData.put(JsonKey.ORG_NAME, ORG_NAME);
    rootOrgData.put(JsonKey.CHANNEL, CHANNEL);
    request.setRequest(rootOrgData);
    try {
      validator.validateCreateFirstRootOrg(request);
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
  }

  @Test
  public void testValidateCreateFirstRootOrgWithoutChannel() {
    Request request = new Request();
    Map<String, Object> rootOrgData = new HashMap<>();
    rootOrgData.put(JsonKey.ORG_NAME, ORG_NAME);
    request.setRequest(rootOrgData);
    try {
      validator.validateCreateFirstRootOrg(request);
    } catch (ProjectCommonException e) {
      Assert.assertEquals(e.getMessage(),ResponseCode.channelRequiredForRootOrg.getErrorMessage());
    }
  }

  @Test
  public void testValidateCreateFirstRootOrgWithoutOrgName() {
    Request request = new Request();
    Map<String, Object> rootOrgData = new HashMap<>();
    rootOrgData.put(JsonKey.CHANNEL, CHANNEL);
    request.setRequest(rootOrgData);
    try {
      validator.validateCreateFirstRootOrg(request);
    } catch (ProjectCommonException e) {
      Assert.assertEquals(e.getMessage(),ResponseCode.organisationNameRequired.getErrorMessage());
    }
  }
}
