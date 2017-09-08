/**
 * 
 */
package org.sunbird.common.models.util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Manzarul.Haque
 *
 */
public class GMailAuthenticator  extends Authenticator{
	 String user;
     String pw;
     /**
      * this method is used to authenticate gmail user name and password. 
      * @param username
      * @param password
      */
     public GMailAuthenticator (String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
    /**
     *  
     */
    public PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(this.user, this.pw);
    }
}
