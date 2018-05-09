package org.sunbird.actorutil.location;

import akka.actor.ActorRef;
import java.util.List;
import org.sunbird.models.location.Location;

/**
 * This class will provide utility methods for Location service
 *
 * @author Amit Kumar
 */
public interface LocationClient {

  /**
   * @desc This method will fetch location details by list of code.
   * @param actorRef Actor reference.
   * @param codeList List of location code.
   * @return List of location.
   */
  List<Location> getLocationsByCodes(ActorRef actorRef, List<String> codeList);

  /**
   * @desc This method will fetch location details by id.
   * @param actorRef Actor reference.
   * @param id Location id.
   * @return Location details.
   */
  Location getLocationById(ActorRef actorRef, String id);

  /**
   * @desc This method will fetch location details by code.
   * @param actorRef Actor reference.
   * @param locationCode location code.
   * @return Location details.
   */
  Location getLocationByCode(ActorRef actorRef, String locationCode);

  /**
   * @desc This method will create Location and returns the response.
   * @param actorRef Actor reference.
   * @param location Location details.
   * @return Location id.
   */
  String createLocation(ActorRef actorRef, Location location);

  /**
   * @desc This method will update location details.
   * @param actorRef Actor reference.
   * @param location Location details.
   * @return
   * @return void.
   */
  void updateLocation(ActorRef actorRef, Location location);
}
