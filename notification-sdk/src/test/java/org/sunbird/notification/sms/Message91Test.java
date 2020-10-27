package org.sunbird.notification.sms;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.sunbird.notification.beans.OTPRequest;
import org.sunbird.notification.beans.SMSConfig;
import org.sunbird.notification.sms.provider.ISmsProvider;
import org.sunbird.notification.sms.providerimpl.Msg91SmsProviderImpl;
import org.sunbird.notification.utils.SMSFactory;

public class Message91Test extends BaseMessageTest {
  SMSConfig config = new SMSConfig(null, "TESTSU");

  @Test
  public void testInitSuccess() {
    Msg91SmsProviderImpl service = new Msg91SmsProviderImpl("sms-auth-key", "TESTSU");
    boolean response = service.init();
    Assert.assertTrue(response);
  }

  @Test
  public void testGetInstanceSuccessWithoutName() {
    ISmsProvider object = SMSFactory.getInstance(null, config);
    Assert.assertTrue(object instanceof Msg91SmsProviderImpl);
  }

  @Test
  public void testGetInstanceSuccessWithName() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    Assert.assertTrue(object instanceof Msg91SmsProviderImpl);
  }

  @Test
  public void testSendSuccess() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("9666666666", "test sms");
    Assert.assertTrue(response);
  }

  /*
    @Test
    public void testSendFailureWithFormattedPhone() {
      ISmsProvider object = SMSFactory.getInstance("91SMS", config);
      boolean response = object.sendSms("(966) 3890-445", "test sms 122");
      Assert.assertFalse(response);
    }
  */

  @Test
  public void testSendSuccessWithoutCountryCodeArg() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("919666666666", "test sms 122");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendSuccessWithoutCountryCodeArgAndPlus() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("+919666666666", "test sms 122");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendFailureWithEmptyPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("", "test sms 122");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendFailureWithEmptyMessage() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("9663890445", "");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendWithEmptyPhoneAndMessage() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("", "");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendFailureWithInvalidPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("981se12345", "some message");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendSuccessWithValidPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("1111111111", "some message");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendSuccessWithCountryCode() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("1234567898", "91", "some message");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendSuccessWithCountryCodeAndPlus() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    boolean response = object.sendSms("0000000000", "+91", "some message");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendSuccessWithMultiplePhones() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    List<String> phones = new ArrayList<>();
    phones.add("1234567898");
    phones.add("1111111111");
    boolean response = object.bulkSms(phones, "some message");
    Assert.assertTrue(response);
  }

  @Test
  public void testSendFailureWithMultipleInvalidPhones() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    List<String> phones = new ArrayList<>();
    phones.add("12345678");
    phones.add("11111");
    boolean response = object.bulkSms(phones, "some message");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendFailureWithMultipleInvalidPhonesAndEmptyMsg() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    List<String> phones = new ArrayList<>();
    phones.add("12345678");
    phones.add("11111");
    boolean response = object.bulkSms(phones, " ");
    Assert.assertFalse(response);
  }

  @Test
  public void testSendOtpSuccess() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("9663845334", "91", 5, 10, null, null);
    boolean response = object.sendOtp(request);
    Assert.assertTrue(response);
  }

  @Test
  public void testSendOtpFailureWithIncorrectPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("96638453", "91", 5, 10, null, null);
    boolean response = object.sendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testSendOtpFailureWithPhoneLengthExceed() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("9663845354321", "91", 5, 10, null, null);
    boolean response = object.sendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testSendOtpFailureDueTOMinOtpLength() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("9663845354", "91", 3, 10, null, null);
    boolean response = object.sendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testSendOtpFailureDueTOMaxOtpLength() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("9663845354", "91", 10, 10, null, null);
    boolean response = object.sendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testresendOtpFailure() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("96638453", "91", 0, 10, null, null);
    boolean response = object.resendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testresendOtpFailureWithInvalidPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("96638453234", "91", 0, 10, null, null);
    boolean response = object.resendOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testverifyOtpFailureWithInvalidPhone() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("96638453234", "91", 0, 10, null, null);
    boolean response = object.verifyOtp(request);
    Assert.assertFalse(response);
  }

  @Test
  public void testverifyOtpFailureWithInvalidOtpLength() {
    ISmsProvider object = SMSFactory.getInstance("91SMS", config);
    OTPRequest request = new OTPRequest("96638453234", "91", 0, 10, null, "234");
    boolean response = object.verifyOtp(request);
    Assert.assertFalse(response);
  }
}
