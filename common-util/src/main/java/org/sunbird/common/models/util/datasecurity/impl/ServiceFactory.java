/**
 * 
 */
package org.sunbird.common.models.util.datasecurity.impl;

import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.datasecurity.DecryptionService;
import org.sunbird.common.models.util.datasecurity.EncryptionService;

import com.sun.org.apache.regexp.internal.recompile;

/**
 * This factory will provide encryption service instance and decryption
 * service instance with default implementation.
 * @author Manzarul
 *
 */
public class ServiceFactory {

  private static EncryptionService encryptionService;
  private static DecryptionService decryptionService;

  static {
    encryptionService = new DefaultEncryptionServivceImpl();
    decryptionService = new DefaultDecryptionServiceImpl();
  }


  /**
   * this method will provide encryptionServiceImple instance. by default it will provide
   * DefaultEncryptionServiceImpl instance to get a particular service impl instance , need to
   * change the object creation and provided logic.
   * 
   * @param val String ( pass null or empty in case of defaultImple object.)
   * @return EncryptionService
   */
  public static EncryptionService getEncryptionServiceInstance(String val) {
    if(ProjectUtil.isStringNullOREmpty(val)) {
      return encryptionService;
    }
    switch (val) {
      case "defaultEncryption":
        return encryptionService;
      default:
        return encryptionService;
    }
  }


  /**
   * this method will provide decryptionServiceImple instance. by default it will provide
   * DefaultDecryptionServiceImpl instance to get a particular service impl instance , need to
   * change the object creation and provided logic.
   * 
   * @param val String ( pass null or empty in case of defaultImple object.)
   * @return DecryptionService
   */
  public static DecryptionService getDecryptionServiceInstance(String val) {
    if(ProjectUtil.isStringNullOREmpty(val)) {
      return decryptionService;
    }
    switch (val) {
      case "defaultDecryption":
        return decryptionService;
      default:
        return decryptionService;
    }
  }
}
