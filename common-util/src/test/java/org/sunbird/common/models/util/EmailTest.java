/** */
package org.sunbird.common.models.util;

import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import org.apache.velocity.VelocityContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.sunbird.common.models.util.mail.GMailAuthenticator;
import org.sunbird.common.models.util.mail.SendMail;

/** @author Manzarul */
public class EmailTest {

  private static GMailAuthenticator authenticator = null;

  @BeforeClass
  public static void setUp() {
    authenticator = new GMailAuthenticator("test123", "test");
    // clear Mock JavaMail box
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
    String subject = "Test1";
    VelocityContext context = new VelocityContext();
    context.put(JsonKey.NAME, "Manzarul");
    SendMail.sendMail(new String[] {"testmail@test.com"}, subject, context, "emailtemplate.vm");
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailTestWithError() throws MessagingException {
    String subject = "Test1";
    VelocityContext context = new VelocityContext();
    context.put(JsonKey.NAME, "Manzarul");
    boolean response =
        SendMail.sendMail(
            new String[] {"testmail@test.com"}, subject, context, "emailtemplate1.vm");
    Assert.assertFalse(response);
  }

  @Test
  public void sendEmailWithCCTest() throws MessagingException {
    String subject = "Test1";
    VelocityContext context = new VelocityContext();
    context.put(JsonKey.NAME, "Manzarul");
    SendMail.sendMail(
        new String[] {"testmail@test.com"},
        subject,
        context,
        "emailtemplate.vm",
        new String[] {"testmail@test.com"});
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailWithCCFailureTest() throws MessagingException {
    String subject = "Test1";
    VelocityContext context = new VelocityContext();
    context.put(JsonKey.NAME, "Manzarul");
    SendMail.sendMail(
        new String[] {"testmail@test.com"},
        subject,
        context,
        "emailtempl.vm",
        new String[] {"testmail@test.com"});
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void sendEmailWithAttachmentTest() throws MessagingException {
    String subject = "Test1";
    VelocityContext context = new VelocityContext();
    context.put(JsonKey.NAME, "Manzarul");
    SendMail.sendAttachment(
        new String[] {"testmail@test.com"}, "test email", subject, "emailtemplate.vm");
    List<Message> inbox = Mailbox.get("testmail@test.com");
    Assert.assertTrue(inbox.size() > 0);
    Assert.assertEquals(subject, inbox.get(0).getSubject());
  }

  @Test
  public void initialiseFromPropertyTest() {
    SendMail.initialiseFromProperty();
    Assert.assertTrue(true);
  }

  @AfterClass
  public static void tearDown() {
    authenticator = null;
    Mailbox.clearAll();
  }
}
