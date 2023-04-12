package org.sunbird.decryptionUtil.decryption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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
  public String decryptData(String data) throws BadPaddingException, IOException, IllegalBlockSizeException {
    return decrypt(data);
  }

  private String decrypt(String value) throws IOException, BadPaddingException, IllegalBlockSizeException {
      String dValue = null;
      String valueToDecrypt = value.trim();
      for (int i = 0; i < ITERATIONS; i++) {
        byte[] decodedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);
        byte[] decValue = c.doFinal(decodedValue);
        dValue = new String(decValue, StandardCharsets.UTF_8).substring(encryptionKey.length());
        valueToDecrypt = dValue;
      }
      return dValue;
  }

  private static Key generateKey() {
    return new SecretKeySpec(keyValue, ALGORITHM);
  }
}
