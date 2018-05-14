package org.sunbird.models.location.apirequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.sunbird.models.location.Location;

/**
 * Class to represent the location api request object.
 *
 * @author arvind.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LocationRequest extends Location {

  private static final long serialVersionUID = 1L;

  private String parentCode;

  public String getParentCode() {
    return parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }
}
