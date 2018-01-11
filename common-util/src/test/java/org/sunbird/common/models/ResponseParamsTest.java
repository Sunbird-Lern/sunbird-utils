/**
 * 
 */
package org.sunbird.common.models;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.response.ResponseParams;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * @author Manzarul
 *
 */
public class ResponseParamsTest {
  
  @Test
  public void testResponseParamBean () {
    ResponseParams params = new ResponseParams();
    params.setErr(ResponseCode.addressError.getErrorCode());
    params.setErrmsg(ResponseCode.addressError.getErrorMessage());
    params.setMsgid("test");
    params.setResmsgid("test-1");
    params.setStatus("OK");
    Assert.assertEquals(params.getErr(), ResponseCode.addressError.getErrorCode());
    Assert.assertEquals(params.getErrmsg(), ResponseCode.addressError.getErrorMessage());
    Assert.assertEquals(params.getMsgid(), "test");
    Assert.assertEquals(params.getResmsgid(), "test-1");
    Assert.assertEquals(params.getStatus(), "OK");
    Assert.assertEquals(ResponseParams.StatusType.failed.name(), "failed");
    Assert.assertEquals(ResponseParams.StatusType.successful.name(), "successful");
    Assert.assertEquals(ResponseParams.StatusType.warning.name(), "warning");
    Assert.assertNotNull(params.toString());
  }
}
