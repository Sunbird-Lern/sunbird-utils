package org.sunbird.migration;


public class RequestParams {

    private String cassandraHost;
    private String cassandraKeyspaceName;
    private String cassandraPort;
    private String sunbirdEncryptionKey;

    public RequestParams() {}

    public RequestParams(
            String cassandraHost,
            String cassandraKeyspaceName,
            String cassandraPort,
            String sunbirdEncryptionKey) {
        this.cassandraHost = cassandraHost;
        this.cassandraKeyspaceName = cassandraKeyspaceName;
        this.cassandraPort = cassandraPort;
        this.sunbirdEncryptionKey = sunbirdEncryptionKey;
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

    public String getSunbirdEncryptionKey() {
        return sunbirdEncryptionKey;
    }

    public void setSunbirdEncryptionKey(String sunbirdEncryptionKey) {
        this.sunbirdEncryptionKey = sunbirdEncryptionKey;
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
