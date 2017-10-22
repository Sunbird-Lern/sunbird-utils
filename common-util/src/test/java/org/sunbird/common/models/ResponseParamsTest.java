/**
 * 
 */
package org.sunbird.common.models;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.response.Params;
import org.sunbird.common.responsecode.ResponseCode;

/**
 * @author Manzarul
 *
 */
public class ResponseParamsTest {
  
  @Test
  public void testResponseParamBean () {
    Params params = new Params();
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
  }

}
