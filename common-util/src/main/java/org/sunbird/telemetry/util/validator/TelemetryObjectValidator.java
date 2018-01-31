package org.sunbird.telemetry.util.validator;

/**
 * Created by arvind on 30/1/18.
 */
public interface TelemetryObjectValidator {

  public boolean validateAudit(String jsonString);
  public boolean validateSearch(String jsonString);
  public boolean validateLog(String jsonString);
  public boolean validateError(String jsonString);

}
