/**
 * 
 */
package org.sunbird.common.models.util.datasecurity.impl;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.models.util.datasecurity.DecryptionService;
import org.sunbird.common.models.util.datasecurity.EncryptionService;
import org.sunbird.common.responsecode.ResponseCode;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;



/**
 * Default data encryption service
 * @author Manzarul
 *
 */
public class DefaultEncryptionServivceImpl implements EncryptionService{
  
  private static String salt ="";
  
  @Override
  public Map<String, Object> encryptData(Map<String, Object> data) throws Exception{
   if(data == null) {
       return data;
   }
    Iterator<Entry<String,Object>> itr = data.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String,Object> entry = itr.next();
      if (entry.getValue() instanceof Map) {
          Map<String,Object> innerMap = (Map<String,Object>) entry.getValue();
            innerMap.entrySet().iterator();
      } else {
          data.put(entry.getKey(), encrypt(entry.getValue()+""));
      }
    }
    return data;
  }

  @Override
  public List<Map<String, Object>> encryptData(List<Map<String, Object>> data)
      throws Exception {
    if (data == null || data.size() == 0) {
      return data;
    }
    for (Map<String, Object> map : data) {
      encryptData(map);
    }
    return data;
  }

  @Override
  public String encryptData(String data) throws Exception {
    if (ProjectUtil.isStringNullOREmpty(data)) {
      return data;
    }
    return encrypt(data);
  }
  
  
  
  /**
   * this method is used to encrypt the password.
   * 
   * @param value
   *            String password
   * @param salt
   * @return encrypted password.
   * @throws Exception
   */
  public static String encrypt(String value) throws Exception {
  salt = getSalt();
  Key key = generateKey();
  Cipher c = Cipher.getInstance(ALGORITHM);
  c.init(Cipher.ENCRYPT_MODE, key);

  String valueToEnc = null;
  String eValue = value;
  for (int i = 0; i < ITERATIONS; i++) {
      valueToEnc = salt + eValue;
      byte[] encValue = c.doFinal(valueToEnc.getBytes());
      eValue =   new BASE64Encoder().encode(encValue);
  }
  return eValue;
  }

  private static Key generateKey() throws Exception {
    Key key = new SecretKeySpec(keyValue, ALGORITHM);
    return key;
  }
  
  
  /**
   * 
   * @return
   */
  public static String getSalt() {
    if (!ProjectUtil.isStringNullOREmpty(salt)) {
      return salt;
    } else {
      salt = System.getenv(JsonKey.SALT);
      if (ProjectUtil.isStringNullOREmpty(salt)) {
        ProjectLogger.log("Salt value is not provided by Env");
        salt = PropertiesCache.getInstance().getProperty(JsonKey.SALT);
      }
    }
    if (ProjectUtil.isStringNullOREmpty(salt)) {
      ProjectLogger.log("throwing exception for invalid salt==",
          LoggerEnum.INFO.name());
      throw new ProjectCommonException(ResponseCode.saltValue.getErrorCode(),
          ResponseCode.saltValue.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    return salt;
  } 
  
  public static void main(String[] args)  throws Exception{
    List<Map<String,Object>> list = new ArrayList<>();
    Map<String,Object> map =  new HashMap<String, Object>();
    map.put("a", "test");
    map.put("b", 10);
    map.put("c", "2015-21-12 30:40:60");
    map.put("d", 12.45);
    list.add(map);
    map =  new HashMap<String, Object>();
    map.put("e", "manzarul@09");
    map.put("f", "test1233");
    map.put("g", "2015-21-12 30:40:60");
    map.put("h", 12.45);
    list.add(map);
    EncryptionService service = ServiceFactory.getEncryptionServiceInstance(null);
    list = service.encryptData(list);
    System.out.println(list);
    DecryptionService service2 = ServiceFactory.getDecryptionServiceInstance("");
    list=service2.decryptData(list);
     System.out.println("-------------------------------------------");
     System.out.println(list);
  }
  
}
