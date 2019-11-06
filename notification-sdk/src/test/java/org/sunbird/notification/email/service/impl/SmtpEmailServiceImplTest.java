package org.sunbird.notification.email.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.notification.beans.EmailConfig;
import org.sunbird.notification.beans.EmailRequest;
import org.sunbird.notification.email.service.IEmailFactory;
import org.sunbird.notification.email.service.IEmailService;

public class SmtpEmailServiceImplTest {
	
	private static IEmailService service = null;
	
	@BeforeClass
	public static void init() {
		IEmailFactory factory = new IEmailProviderFactory();
		EmailConfig config = new EmailConfig("test@test.com", "testabc", "password", "localhost", "3025");
		service = factory.create(config);
	}
	
	@Test
	public void instanceCreationsuccess() {
		assertNotNull(service);
	}
	
	@Test
	public void sendEmailFailure () {
		List<String> to = new ArrayList<String>();
		Map<String,String> param = new HashMap<String, String>();
		param.put("name", "test");
		EmailRequest emailReq = new EmailRequest("testEmail", to, null, null, "emailtemplate", null, param);
		boolean response = service.sendEmail(emailReq);
		assertFalse(response);
		
	}
 
}
