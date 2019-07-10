package org.sunbird.decryptionUtil.decryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

/** DecryptionService provides methods for decryption of data. */
public interface DecryptionService {

  String ALGORITHM = "AES";
  int ITERATIONS = 3;
  byte[] keyValue =
      new byte[] {'T', 'h', 'i', 's', 'A', 's', 'I', 'S', 'e', 'r', 'c', 'e', 'K', 't', 'e', 'y'};

  /**
   * Decrypt given data (string).
   *
   * @param data Input data
   * @return Decrypted data
   */
  String decryptData(String data) throws BadPaddingException, IOException, IllegalBlockSizeException;
}
