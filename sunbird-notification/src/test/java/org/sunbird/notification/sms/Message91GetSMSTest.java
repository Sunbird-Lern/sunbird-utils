package org.sunbird.notification.sms;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.notification.sms.providerimpl.Msg91SmsProvider;

/** @author Manzarul */

public class Message91GetSMSTest extends BaseMessageTest {

	@Test
	public void testSmsGetMethodSuccess() {
		Msg91SmsProvider megObj = new Msg91SmsProvider();
		boolean response = megObj.sendSmsGetMethod("4321111111", "say hai!");
		Assert.assertTrue(response);
	}

	@Test
	public void testSmsGetMethodWithoutMessage() {
		Msg91SmsProvider megObj = new Msg91SmsProvider();
		boolean response = megObj.sendSmsGetMethod("4321111111", "");
		Assert.assertFalse(response);
	}

	@Test
	public void testSmsGetMethodWithEmptySpace() {
		Msg91SmsProvider megObj = new Msg91SmsProvider();
		boolean response = megObj.sendSmsGetMethod("4321111111", "  ");
		Assert.assertFalse(response);
	}
}
