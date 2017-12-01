package org.sunbird.notification.sms;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.notification.sms.providerimpl.Msg91SmsProvider;

/**
 * 
 * @author Manzarul
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Message91GetSMSTest {
  
  @Test
  public void testSmsGetMethodSuccess() {
    Msg91SmsProvider megObj = new Msg91SmsProvider();
    boolean response = megObj.sendSmsGetMethod("4321111111", "say hai!", "91") ;
    Assert.assertTrue(response);
  }
  
  @Test
  public void testSmsGetMethodWithoutMessage() {
    Msg91SmsProvider megObj = new Msg91SmsProvider();
    boolean response = megObj.sendSmsGetMethod("4321111111", "", "91") ;
    Assert.assertFalse(response);
  }

  
  @Test
  public void testSmsGetMethodWithEmptySpace() {
    Msg91SmsProvider megObj = new Msg91SmsProvider();
    boolean response = megObj.sendSmsGetMethod("4321111111", "  ", "91") ;
    Assert.assertFalse(response);
  }
  
  
  
}
