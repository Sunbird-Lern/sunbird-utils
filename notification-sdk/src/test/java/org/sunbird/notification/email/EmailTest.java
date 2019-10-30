/** */
package org.sunbird.notification.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.sunbird.notification.beans.EmailConfig;

/** @author Manzarul */
public class EmailTest {

  private static GMailAuthenticator authenticator = null;
  private static Email emailService = null;
  private static String subject = "Test1";

  @BeforeClass
  public static void setUp() {
    authenticator = new GMailAuthenticator("test123", "test");
    EmailConfig config = new EmailConfig();
    config.setHost("localhost");
    config.setPort("3025");
    emailService = new Email(config);
    Mailbox.clearAll();
  }

  @Test
  public void createGmailAuthInstance() {
    GMailAuthenticator authenticator = new GMailAuthenticator("test123", "test");
    Assert.assertNotEquals(null, authenticator);
  }

  @Test
  public void passwordAuthTest() {
    PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
    Assert.assertEquals("test", authentication.getPassword());
  }

  @Test
  public void sendEmailTest() throws MessagingException {
    List<String> to = new ArrayList<String>();
    to.add("testmail@test.com"); to.add("test1@test.com");
    emailService.sendEmail("testmail@test.com", subject, "Hi how are you", to);
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailToMultipleUser() throws MessagingException {
    List<String> to = new ArrayList<String>();
    to.add("testmail@test.com"); to.add("test1@test.com");
		  emailService.sendMail(to, subject, "Test email body"); 
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailWithCCTest() throws MessagingException {
    List<String> emailList = new ArrayList<String>();
    emailList.add("testmail@test.com"); emailList.add("test1@test.com");
    List<String> ccEmailList = new ArrayList<String>();
    ccEmailList.add("testmailcc@test.com"); ccEmailList.add("test1cc@test.com");
    emailService.sendMail(emailList, subject, "Email body to do email test", ccEmailList);
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailTOBCC() throws MessagingException{
	  List<String> bcc = new ArrayList<String>();
	  bcc.add("testmail@test.com");
	  emailService.sendEmail("testgamil@gmail.com", subject, "Bcc email test body", bcc);
	  List<Message> inbox = Mailbox.get("testmail@test.com");
	  Assert.assertTrue(inbox.size() > 0);
	  Assert.assertEquals(subject, inbox.get(0).getSubject());	  
  }
  
  @Test
  public void sendEmailWithAttachmentTest() throws MessagingException {
	  List<String> to = new ArrayList<String>();
	    to.add("testmail@test.com"); to.add("test1@test.com");
    emailService.sendAttachment(to, "Test email as attached.", subject, "emailtemplate.vm");
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }
  
  @AfterClass
  public static void tearDown() {
    authenticator = null;
    Mailbox.clearAll();
  }
}
