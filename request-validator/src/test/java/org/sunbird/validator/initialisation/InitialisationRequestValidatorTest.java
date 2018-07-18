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
    boolean response = false;
    Map<String, Object> rootOrgData = new HashMap<>();
    rootOrgData.put(JsonKey.ORG_NAME, ORG_NAME);
    rootOrgData.put(JsonKey.CHANNEL, CHANNEL);
    request.setRequest(rootOrgData);
    request.put(JsonKey.REMOTE_ADDRESS, REMOTE_ADDRESS);
    try {
      validator.validateCreateFirstRootOrg(request);
      response = true;
    } catch (ProjectCommonException e) {
      Assert.assertNull(e);
    }
  }

  @Test(expected = ProjectCommonException.class)
  public void testValidateCreateFirstRootOrgWithoutData() {
    Request request = new Request();
    Map<String, Object> rootOrgData = new HashMap<>();
    request.setRequest(rootOrgData);
    request.put(JsonKey.REMOTE_ADDRESS, REMOTE_ADDRESS);
    try {
      validator.validateCreateFirstRootOrg(request);
    } catch (ProjectCommonException e) {
      Assert.assertNotNull(e);
    }
  }

  @Test(expected = ProjectCommonException.class)
  public void testvalidateRemoteAddress() {
    Boolean result = validator.validateRemoteAddress(REMOTE_ADDRESS);
    Assert.assertEquals(result, true);
  }

  @Test(expected = ProjectCommonException.class)
  public void testvalidateRemoteAddresswithInvalidAddress() {
    Boolean result = validator.validateRemoteAddress("");
    Assert.assertEquals(result, false);
  }
}
