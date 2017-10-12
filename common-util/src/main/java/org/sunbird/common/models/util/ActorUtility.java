package org.sunbird.common.models.util;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import java.text.MessageFormat;
/**
 * 
 * @author Amit Kumar
 *
 */
public class ActorUtility {

  private static ActorSelection selection = null;
  private ActorUtility(){}
  
  public static ActorSelection getActorSelection(){
    ProjectLogger.log("ActorUtility getActorSelection method start....");
    if(null == selection){
      createConnection();
    }else{
      ProjectLogger.log("ActorSelection is not null ::"+selection);
    }
    return selection;
  }
  
  private static void createConnection() {
    ProjectLogger.log("ActorUtility createConnection method start....");
    ActorSystem system =
        ActorSystem.create("ActorApplication",ConfigFactory.load().getConfig("ActorConfig"));
    
    String path = PropertiesCache.getInstance().getProperty("remote.actor.path");
    try {
      if (!ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.SUNBIRD_ACTOR_IP))
          && !ProjectUtil.isStringNullOREmpty(System.getenv(JsonKey.SUNBIRD_ACTOR_PORT))) {
        ProjectLogger.log("value is taking from system env");
        path = MessageFormat.format(
            PropertiesCache.getInstance().getProperty("remote.actor.env.path"),
            System.getenv(JsonKey.SUNBIRD_ACTOR_IP), System.getenv(JsonKey.SUNBIRD_ACTOR_PORT));
      }
      ProjectLogger.log("Actor path is ==" + path, LoggerEnum.INFO.name());
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    selection = system.actorSelection(path);
    ProjectLogger.log("ActorUtility selection reference    : "+selection);
  }

}
