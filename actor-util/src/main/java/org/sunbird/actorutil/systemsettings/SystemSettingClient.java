package org.sunbird.actorutil.systemsettings;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.models.systemsetting.SystemSetting;

/**
 * This interface defines methods supported by System Setting service.
 *
 * @author Amit Kumar
 */
public interface SystemSettingClient {

  /**
   * Get system setting information for given field (setting) name.
   *
   * @param actorRef Actor reference
   * @param field System setting field name
   * @return System setting details
   */
  SystemSetting getSystemSettingByField(ActorRef actorRef, String field);

  /**
   * Get system setting information for given field (setting) name.
   *
   * @param actorRef Actor reference
   * @param field System setting field name
   * @param key Key inside system setting value
   * @return Value inside system setting
   */
  Map<String, Object> getSystemSettingByField(ActorRef actorRef, String field, String key);
}
