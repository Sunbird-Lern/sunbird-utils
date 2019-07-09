package org.sunbird.externaliddecryption.decryption;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

public class DefaultDecryptionServiceImpl implements DecryptionService {
  private static Logger logger = Logger.getLogger(DefaultDecryptionServiceImpl.class);
  private String encryptionKey = "";
  private String sunbirdEncryption = "ON";
  private final String ON = "ON";
  private Cipher c;

  public DefaultDecryptionServiceImpl(String sunbirdEncryption) {
    try {
      encryptionKey = sunbirdEncryption;
      Key key = generateKey();
      c = Cipher.getInstance(ALGORITHM);
      c.init(Cipher.DECRYPT_MODE, key);
    } catch (Exception e) {
      logger.error(
          "DefaultDecryptionServiceImpl:DefaultDecryptionServiceImpl:Exception occurred with error message = "
              + e.getMessage());
    }
  }

  @Override
  public String decryptData(String data) {
    return decrypt(data);
  }

  private String decrypt(String value) {
    try {
      String dValue = null;
      String valueToDecrypt = value.trim();
      for (int i = 0; i < ITERATIONS; i++) {
        byte[] decodedValue = new sun.misc.BASE64Decoder().decodeBuffer(valueToDecrypt);
        byte[] decValue = c.doFinal(decodedValue);
        dValue = new String(decValue, StandardCharsets.UTF_8).substring(encryptionKey.length());
        valueToDecrypt = dValue;
      }
      return dValue;
    } catch (Exception ex) {
      return value;
    }
  }

  private static Key generateKey() {
    return new SecretKeySpec(keyValue, ALGORITHM);
  }
}
