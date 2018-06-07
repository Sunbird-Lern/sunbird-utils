package org.sunbird.common.models.util.mail;

import java.io.StringWriter;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;

/**
 * this api is used to sending mail.
 *
 * @author Manzarul.Haque
 */
public class SendMail {

  private static Properties props = null;
  private static String host;
  private static String port;
  private static String userName;
  private static String password;
  private static String fromEmail;
  private static Session session;
  private static VelocityEngine engine;
  private static Transport transport;

  static {
    // collecting setup value from ENV
    host = System.getenv(JsonKey.EMAIL_SERVER_HOST);
    port = System.getenv(JsonKey.EMAIL_SERVER_PORT);
    userName = System.getenv(JsonKey.EMAIL_SERVER_USERNAME);
    password = System.getenv(JsonKey.EMAIL_SERVER_PASSWORD);
    fromEmail = System.getenv(JsonKey.EMAIL_SERVER_FROM);
    if (StringUtils.isBlank(host)
        || StringUtils.isBlank(port)
        || StringUtils.isBlank(userName)
        || StringUtils.isBlank(password)
        || StringUtils.isBlank(fromEmail)) {
      ProjectLogger.log(
          "Email setting value is not provided by Env variable=="
              + host
              + " "
              + port
              + " "
              + fromEmail,
          LoggerEnum.INFO.name());
      initialiseFromProperty();
    }
    props = System.getProperties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.socketFactory.port", port);
    /*
     * props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
     */
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", port);
    
    try {
    	session = Session.getInstance(props, new GMailAuthenticator(userName, password));
    	
        engine = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty(
            "class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.init(p);
        
    	transport = session.getTransport("smtp");
        transport.connect(host, userName, password);
        registerShutDownHook();
    } catch (Exception e) {
        ProjectLogger.log(e.toString(), e);
    }
  }

  /** This method will initialize values from property files. */
  public static void initialiseFromProperty() {
    host = PropertiesCache.getInstance().getProperty(JsonKey.EMAIL_SERVER_HOST);
    port = PropertiesCache.getInstance().getProperty(JsonKey.EMAIL_SERVER_PORT);
    userName = PropertiesCache.getInstance().getProperty(JsonKey.EMAIL_SERVER_USERNAME);
    password = PropertiesCache.getInstance().getProperty(JsonKey.EMAIL_SERVER_PASSWORD);
    fromEmail = PropertiesCache.getInstance().getProperty(JsonKey.EMAIL_SERVER_FROM);
  }
  
  private static Template getTemplate(String templateName) {
	  Template template = null;
	  try {
		  template = engine.getTemplate(templateName);
	  } catch (Exception e) {
	      ProjectLogger.log(e.toString(), e);
	  }
	  return template;
  }

  /**
   * this method is used to send email.
   *
   * @param receipent email to whom we send mail
   * @param context VelocityContext
   * @param templateName String
   * @param subject subject
   */
  public static boolean sendMail(
      String[] receipent, String subject, VelocityContext context, String templateName) {
    ProjectLogger.log("Mail Template name - " + templateName, LoggerEnum.INFO.name());
    boolean flag = true;
    if (context != null) {
      context.put(JsonKey.FROM_EMAIL, fromEmail);
    }
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      int size = receipent.length;
      int i = 0;
      while (size > 0) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent[i]));
        i++;
        size--;
      }
      message.setSubject(subject);
      
      Template template = getTemplate(templateName);
      if (null != template) {
    	  StringWriter writer = new StringWriter();
          template.merge(context, writer);
          message.setContent(writer.toString(), "text/html");
          transport.sendMessage(message, message.getAllRecipients());
      } else {
    	  flag = false;
      }
    } catch (Exception e) {
      flag = false;
      ProjectLogger.log(e.toString(), e);
    }
    return flag;
  }

  /**
   * this method is used to send email along with CC Recipients list.
   *
   * @param receipent email to whom we send mail
   * @param context VelocityContext
   * @param templateName String
   * @param subject subject
   * @param ccList String
   */
  public static void sendMail(
      String[] receipent,
      String subject,
      VelocityContext context,
      String templateName,
      String[] ccList) {
    ProjectLogger.log("Mail Template name - " + templateName, LoggerEnum.INFO.name());
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      int size = receipent.length;
      int i = 0;
      while (size > 0) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent[i]));
        i++;
        size--;
      }
      size = ccList.length;
      i = 0;
      while (size > 0) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccList[i]));
        i++;
        size--;
      }
      message.setSubject(subject);
      
      Template template = getTemplate(templateName);
      if (null != template) {
    	  StringWriter writer = new StringWriter();
          template.merge(context, writer);
          message.setContent(writer.toString(), "text/html");
          transport.sendMessage(message, message.getAllRecipients());
      }
    } catch (Exception e) {
      ProjectLogger.log(e.toString(), e);
    }
  }

  /**
   * this method is used to send email as an attachment.
   *
   * @param receipent email to whom we send mail
   * @param mail mail body.
   * @param subject subject
   * @param filePath String
   */
  public static void sendAttachment(
      String[] receipent, String mail, String subject, String filePath) {
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      int size = receipent.length;
      int i = 0;
      while (size > 0) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipent[i]));
        i++;
        size--;
      }
      message.setSubject(subject);
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(mail, "text/html");
      // messageBodyPart.setText(mail);
      // Create a multipar message
      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);
      DataSource source = new FileDataSource(filePath);
      messageBodyPart = null;
      messageBodyPart = new MimeBodyPart();
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(filePath);
      multipart.addBodyPart(messageBodyPart);
      message.setSubject(subject);
      message.setContent(multipart);
      transport.sendMessage(message, message.getAllRecipients());
    } catch (Exception e) {
      ProjectLogger.log(e.toString(), e);
    }
  }
  
  /** Register the hook for resource clean up. this will be called when jvm shut down. */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    ProjectLogger.log("Cassandra ShutDownHook registered.");
  }

  /**
   * This class will be called by registerShutDownHook to register the call inside jvm , when jvm
   * terminate it will call the run method to clean up the resource.
   */
  static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      ProjectLogger.log("started resource cleanup SMTP clients.");
      try {
    	  if (null != transport)
    		  transport.close();
      } catch (Exception e) {
          ProjectLogger.log(e.toString(), e);
      }
      ProjectLogger.log("completed resource cleanup SMTP clients.");
    }
  }
  
}
