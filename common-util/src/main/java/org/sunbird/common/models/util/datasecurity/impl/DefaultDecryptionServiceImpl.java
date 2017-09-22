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
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.models.util.datasecurity.DecryptionService;

/**
 * @author Manzarul
 *
 */
public class DefaultDecryptionServiceImpl implements DecryptionService{
  public static String salt ="";
  
  private String sunbirdEncryption = "";
  
  public DefaultDecryptionServiceImpl() {
    sunbirdEncryption = System.getenv(JsonKey.SUNBIRD_ENCRYPTION);
    if(ProjectUtil.isStringNullOREmpty(sunbirdEncryption)){
      sunbirdEncryption = PropertiesCache.getInstance().getProperty(JsonKey.SUNBIRD_ENCRYPTION);
    }
  }
  
  @Override
  public Map<String, Object> decryptData(Map<String, Object> data) throws Exception {
    if(JsonKey.ON.equalsIgnoreCase(sunbirdEncryption)){
      if (data == null) {
        return data;
      } 
       Iterator<Entry<String, Object>> itr = data.entrySet().iterator();
       while (itr.hasNext()) {
         Entry<String, Object> entry = itr.next();
         if(entry.getValue() instanceof Map || entry.getValue() instanceof List) {
           // Do Nothing
         } else {
           if(null != entry.getValue()){
            data.put(entry.getKey(), decrypt(entry.getValue()+""));
           }
         }
       }
    }
    return data;
  }

  @Override
  public List<Map<String, Object>> decryptData(List<Map<String, Object>> data) throws Exception{
    if(JsonKey.ON.equalsIgnoreCase(sunbirdEncryption)){
     if (data == null || data.isEmpty()) {
       return data;
     }
     
     for (Map<String,Object> map : data) {
       decryptData(map);
     }
    }
    return data;
  }

  @Override
  public String decryptData(String data) throws Exception {
    if(JsonKey.ON.equalsIgnoreCase(sunbirdEncryption)){
      if (ProjectUtil.isStringNullOREmpty(data)) {
        return data;
      }
      if(null != data){
       return decrypt(data);
      }else{
        return data;
      }
    } else {
      return data;
    }
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
          byte[] decordedValue = new sun.misc.BASE64Decoder()
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
