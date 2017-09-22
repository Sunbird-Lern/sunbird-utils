/**
 * 
 */
package org.sunbird.common.models.util.datasecurity.impl;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.datasecurity.DataMaskingService;

/**
 * @author Manzarul
 *
 */
public class DefaultDataMaskServiceImpl implements DataMaskingService {

  @Override
  public String maskPhone(String phone) {
    if (ProjectUtil.isStringNullOREmpty(phone) || phone.length() < 10) {
      return phone;
    }
    StringBuilder builder = new StringBuilder();
     phone = phone.trim().replace("-", "");
    int length = phone.length();
    for (int i = 0; i < length; i++) {
      if (i < length - 4) {
        builder.append(JsonKey.REPLACE_WITH_X);
      } else {
        builder.append(phone.charAt(i));
      }
    }
    return builder.toString();
  }

  @Override
  public String maskEmail(String email) {
    if((ProjectUtil.isStringNullOREmpty(email)) || (!ProjectUtil.isEmailvalid(email))) {
      return email;
    }
    StringBuilder builder = new StringBuilder();
    String emails [] = email.split("@");
    int length = emails[0].length();
    for (int i=0 ; i< email.length();i++) {
      if (i<2 || i>=length) {
        builder.append(email.charAt(i));
      }  else {
        builder.append(JsonKey.REPLACE_WITH_X);
      }
    }
    return builder.toString();
  }

  @Override
  public String maskData(String data) {
     if (ProjectUtil.isStringNullOREmpty(data) || data.length()<=3) {
       return data;
     }
     int lenght = data.length()-4;
     StringBuilder builder = new StringBuilder();
     for (int i =0 ; i< data.length();i++) {
          if (i<lenght) {
             builder.append(JsonKey.REPLACE_WITH_X);
          } else {
            builder.append(data.charAt(i));
          }
     }
    return builder.toString();
  }
  
  public static void main(String[] args) {
    System.out.println(ServiceFactory.getMaskingServiceInstance(null).maskData("qwerty"));
  }
}
