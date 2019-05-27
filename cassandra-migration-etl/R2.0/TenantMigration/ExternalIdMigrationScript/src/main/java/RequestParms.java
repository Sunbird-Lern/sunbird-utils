public class RequestParms {

  private String csvFileInput;
  private String baseUrl;
  private String realm;
  private String clientId;
  private String apiKey;

  public RequestParms(
      String csvFileInput, String baseUrl, String realm, String clientId, String apiKey) {
    this.csvFileInput = csvFileInput;
    this.baseUrl = baseUrl;
    this.realm = realm;
    this.clientId = clientId;
    this.apiKey = apiKey;
  }

  public String getCsvFileInput() {
    return csvFileInput;
  }

  public void setCsvFileInput(String csvFileInput) {
    this.csvFileInput = csvFileInput;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getRealm() {
    return realm;
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
