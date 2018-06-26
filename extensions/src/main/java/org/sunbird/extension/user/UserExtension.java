package org.sunbird.extension.user;

import java.util.Map;

/**
 * UserExtension is the interface for all extensions of Sunbird user profile. It defines methods for
 * performing CRUD operations on user profile.
 *
 * @author Jaikumar Soundara Rajan
 */
public interface UserExtension {

  /**
   * Creates user profile information.
   *
   * @param userProfileMap User profile information
   */
  public void create(Map<String, Object> userProfileMap);

  /**
   * Reads user profile information.
   *
   * @param userIdMap UserId Information
   * @return userProfileMap Map<String, Object>
   */
  public Map<String, Object> read(Map<String, Object> userIdMap);

  /**
   * Updates user profile information.
   *
   * @param userProfileMap User profile information
   */
  public void update(Map<String, Object> userProfileMap);

  /**
   * Deletes user profile information.
   *
   * @param userProfileMap User profile information
   */
  public void delete(Map<String, Object> userProfileMap);
}
