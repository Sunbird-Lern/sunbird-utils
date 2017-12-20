package org.sunbird.notification.sms;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.notification.sms.provider.ISmsProvider;
import org.sunbird.notification.sms.providerimpl.Msg91SmsProvider;
import org.sunbird.notification.utils.SMSFactory;

/**
 * 
 * @author Manzarul
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Message91Test {
  
  @BeforeClass
  public static void setUp() {
    
  }
  
  @Test
  public void testSuccessInit() {
     boolean response = Msg91SmsProvider.init();
     Assert.assertTrue(response);
  }
  
  @Test
  public void acreateObjectTest() {
    ISmsProvider object = SMSFactory.getInstance(null);
    Assert.assertTrue(object instanceof Msg91SmsProvider );
  }
  
  @Test
  public void acreateObjectWithObjectNameTest() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    Assert.assertTrue(object instanceof Msg91SmsProvider );
  }
  
  @Test
  public void sendSMSTest () {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("9666666666", "test sms");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendSMSWithFormattedPhoneTest () {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("(966) 3890-445", "test sms 122");
    Assert.assertFalse(response);
  }
 
  @Test
  public void sendSMSWithcountryCode() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("919666666666", "test sms 122");
    Assert.assertTrue(response);
  }
 
  @Test
  public void sendSMSWithcountryCodeWithPlus() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("+919666666666", "test sms 122");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendSMSWithEmptyPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("", "test sms 122");
    Assert.assertFalse(response);
  }
  
  @Test
  public void sendSMSWithEmptyMessage() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("9663890445", "");
    Assert.assertFalse(response);
  }
  
  @Test
  public void sendSMSWithEmptyMessageAndPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("", "");
    Assert.assertFalse(response);
  }
  
  @Test
  public void sendSMSWithIncorrectPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("981se12345", "some message");
    Assert.assertFalse(response);
  }
 

  @Test
  public void sendSMSWithInvalidPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("1111111111", "some message");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendSMSWithWithCountryCode() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("1234567898","91", "some message");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendSMSWithWithCountryCodeWithPrefixPlus() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    boolean response = object.send("0000000000","+91", "some message");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendBulkSMSSuccess() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    List<String> phones = new ArrayList<>();
    phones.add("1234567898");
    phones.add("1111111111");
    boolean response = object.send(phones, "some message");
    Assert.assertTrue(response);
  }
  
  @Test
  public void sendBulkSMSWithWrongLsit() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    List<String> phones = new ArrayList<>();
    phones.add("12345678");
    phones.add("11111");
    boolean response = object.send(phones, "some message");
    Assert.assertFalse(response);
  }
  
  @Test
  public void sendBulkSMSWithWrongLsitAndEMptyMsg() {
    ISmsProvider object = SMSFactory.getInstance("91SMS");
    List<String> phones = new ArrayList<>();
    phones.add("12345678");
    phones.add("11111");
    boolean response = object.send(phones, " ");
    Assert.assertFalse(response);
  }
}
