/** */
package org.sunbird.common.models.util.datasecurity;

/** @author Manzarul */
public interface DataMaskingService {

  /**
   * This method will allow to mask user phone number.
   *
   * @param phone String
   * @return String
   */
  String maskPhone(String phone);

  /**
   * This method will allow user to mask email.
   *
   * @param email String
   * @return String
   */
  String maskEmail(String email);

  /**
   * @param data
   * @return
   */
  String maskData(String data);
}
