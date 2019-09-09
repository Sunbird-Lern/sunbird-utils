package org.sunbird.statevalidateutil;

public class RequestParams {

  private String cassandraHost;
  private String cassandraKeyspaceName;
  private String cassandraPort;

  public RequestParams() {}

  public RequestParams(
      String cassandraHost,
      String cassandraKeyspaceName,
      String cassandraPort) {
    this.cassandraHost = cassandraHost;
    this.cassandraKeyspaceName = cassandraKeyspaceName;
    this.cassandraPort = cassandraPort;
  }

  public String getCassandraHost() {
    return cassandraHost;
  }

  public void setCassandraHost(String cassandraHost) {
    this.cassandraHost = cassandraHost;
  }

  public String getCassandraKeyspaceName() {
    return cassandraKeyspaceName;
  }

  public void setCassandraKeyspaceName(String cassandraKeyspaceName) {
    this.cassandraKeyspaceName = cassandraKeyspaceName;
  }

  public String getCassandraPort() {
    return cassandraPort;
  }

  public void setCassandraPort(String cassandraPort) {
    this.cassandraPort = cassandraPort;
  }


  @Override
  public String toString() {
    return "RequestParams{" +
            "cassandraHost='" + cassandraHost + '\'' +
            ", cassandraKeyspaceName='" + cassandraKeyspaceName + '\'' +
            ", cassandraPort='" + cassandraPort + '\'' +
            '}';
  }
}
