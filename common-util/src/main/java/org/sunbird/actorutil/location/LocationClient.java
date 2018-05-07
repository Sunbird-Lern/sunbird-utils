package org.sunbird.actorutil.location;

import akka.actor.ActorRef;
import java.util.List;
import java.util.Map;
import org.sunbird.common.models.response.Response;

/**
 * This class will provide utility methods for Location service
 *
 * @author Amit Kumar
 */
public interface LocationClient {

  /**
   * @desc This method will fetch location details by list of code.
   * @param CodeList list of code.
   * @param ActorRef actor reference.
   * @return List of location.
   */
  List<Map<String, Object>> getLocationsByCodes(ActorRef actorRef, List<String> codeList);

  /**
   * @desc This method will fetch location details by id.
   * @param String locationId.
   * @param ActorRef actor reference.
   * @return Location details.
   */
  Map<String, Object> getLocationById(ActorRef actorRef, String id);

  /**
   * @desc This method will fetch location details by code.
   * @param String locationId.
   * @param ActorRef actor reference.
   * @return Location details.
   */
  Response getLocationByCode(ActorRef actorRef, String locationCode);

  /**
   * @desc This method will create Location and returns the response.
   * @param String locationId.
   * @param ActorRef actor reference.
   * @return Location details.
   */
  Response createLocation(ActorRef actorRef, Map<String, Object> location);

  /**
   * @desc This method will update location details.
   * @param String locationId.
   * @param ActorRef actor reference.
   * @return Location details.
   */
  Response updateLocation(ActorRef actorRef, Map<String, Object> location);
}
