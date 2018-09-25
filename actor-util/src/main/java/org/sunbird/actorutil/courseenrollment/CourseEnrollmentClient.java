package org.sunbird.actorutil.courseenrollment;

import akka.actor.ActorRef;
import java.util.Map;
import org.sunbird.common.models.response.Response;

/** Created by rajatgupta on 25/09/18. */
public interface CourseEnrollmentClient {
  /**
   * @desc UnEnroll user from course.
   * @param actorRef Actor reference.
   * @param map containing unEnrollment map.
   * @return Response
   */
  Response unenroll(ActorRef actorRef, Map<String, Object> map);
}
