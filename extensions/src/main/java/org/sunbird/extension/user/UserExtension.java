package org.sunbird.extension.user;

import java.util.Map;
import org.sunbird.common.models.response.Response;

/**
 * UserExtension is the interface for all extensions of Sunbird user profile. It defines methods for
 * performing CRUD operations on user profile.
 *
 * @author Jaikumar Soundara Rajan
 */
public interface UserExtension {

  /**
   * This method will be called before create method.
   *
   * @param userProfileMap User profile information
   */
  public void preCreate(Map<String, Object> userProfileMap);

  /**
   * Creates user profile information.
   *
   * @param userProfileMap User profile information
   * @return Response
   */
  public Response create(Map<String, Object> userProfileMap);

  /**
   * This method will be called after create method.
   *
   * @param userProfileMap User profile information
   */
  public void postCreate(Map<String, Object> userProfileMap);

  /**
   * This method will be called before update method.
   *
   * @param userProfileMap User profile information
   */
  public void preUpdate(Map<String, Object> userProfileMap);

  /**
   * Updates user profile information.
   *
   * @param userProfileMap User profile information
   * @return Response
   */
  public Response update(Map<String, Object> userProfileMap);
}
