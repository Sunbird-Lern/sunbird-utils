package org.sunbird.actorutil.location;

import java.util.List;
import java.util.Map;

/**
 * This class will provide utility methods for Location service
 *
 * @author Amit Kumar
 */
public interface LocationClient {

  /**
   * @desc This method will fetch location details by list of code
   * @param codeList list of code
   * @param actorRef actor reference
   * @return list of location
   */
  List<Map<String, Object>> getLocationsByCodes(List<String> codeList, Object actorRef);

  /**
   * @desc This method will fetch location details by id
   * @param String locationId
   * @param actorRef actor reference
   * @return Map<String,Object> location details
   */
  Map<String, Object> getLocationById(String id, Object actorRef);
}
