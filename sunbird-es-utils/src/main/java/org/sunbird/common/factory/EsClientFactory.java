package org.sunbird.common.factory;

import org.sunbird.common.ElasticSearchRestHighImpl;
import org.sunbird.common.ElasticSearchTcpImpl;
import org.sunbird.common.inf.ElasticSearchUtil;

public class EsClientFactory {

  private static ElasticSearchUtil tcpClient = null;
  private static ElasticSearchUtil restClient = null;

  public static ElasticSearchUtil getTcpClient() {
    if (tcpClient == null) {
      tcpClient = new ElasticSearchTcpImpl();
    }
    return tcpClient;
  }

  public static ElasticSearchUtil getRestClient() {
    if (restClient == null) {
      restClient = new ElasticSearchRestHighImpl();
    }
    return restClient;
  }
}
