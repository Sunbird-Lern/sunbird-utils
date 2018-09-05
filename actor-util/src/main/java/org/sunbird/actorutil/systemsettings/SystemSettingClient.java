package org.sunbird.actorutil.systemsettings;

import akka.actor.ActorRef;
import org.sunbird.models.systemsetting.SystemSetting;

/**
 * This class will provide utility methods for Location service
 *
 * @author Amit Kumar
 */
public interface SystemSettingClient {

  /**
   * @desc This method will fetch system setting details by id.
   * @param actorRef Actor reference.
   * @param id SystemSetting id.
   * @return SystemSetting details.
   */
  SystemSetting getSystemSettingByField(ActorRef actorRef, String field);
}
