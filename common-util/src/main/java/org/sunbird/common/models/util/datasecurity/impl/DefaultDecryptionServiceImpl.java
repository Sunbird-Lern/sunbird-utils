/**
 * 
 */
package org.sunbird.common.models.util.datasecurity.impl;

import java.security.Key;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.datasecurity.DecryptionService;

import sun.misc.BASE64Decoder;

/**
 * @author Manzarul
 *
 */
public class DefaultDecryptionServiceImpl implements DecryptionService{
  public static String salt ="";
  
  @Override
  public Map<String, Object> decryptData(Map<String, Object> data) throws Exception {
    if (data == null) {
      return data;
    } 
     Iterator<Entry<String, Object>> itr = data.entrySet().iterator();
     while (itr.hasNext()) {
       Entry<String, Object> entry = itr.next();
       if(entry.getValue() instanceof Map) {
         
       } else {
         data.put(entry.getKey(), decrypt(entry.getValue()+""));
       }
     }
    return data;
  }

  @Override
  public List<Map<String, Object>> decryptData(List<Map<String, Object>> data) throws Exception{
     if (data == null || data.size()==0) {
       return data;
     }
     
     for (Map<String,Object> map : data) {
       decryptData(map);
     }
    return data;
  }

  @Override
  public String decryptData(String data) throws Exception {
    if (ProjectUtil.isStringNullOREmpty(data)) {
      return data;
    }
    return decrypt(data);
  }
  
  /**
   * this method is used to decrypt password.
   * 
   * @param value
   *            encrypted password.
   * @param salt
   * @return decrypted password.
   * @throws Exception
   */
  public static String decrypt(String value) throws Exception {
  salt = DefaultEncryptionServivceImpl.getSalt(); 
  Key key = generateKey();
  Cipher c = Cipher.getInstance(ALGORITHM);
  c.init(Cipher.DECRYPT_MODE, key);

  String dValue = null;
  String valueToDecrypt = value.trim();
  for (int i = 0; i < ITERATIONS; i++) {
      byte[] decordedValue = new BASE64Decoder()
          .decodeBuffer(valueToDecrypt);
      byte[] decValue = c.doFinal(decordedValue);
      dValue = new String(decValue).substring(salt.length());
      valueToDecrypt = dValue;
  }
  return dValue;
  }

  private static Key generateKey() throws Exception {
  Key key = new SecretKeySpec(keyValue, ALGORITHM);
  return key;
  }
  
}
