package org.sunbird.common.factory;

import org.sunbird.common.ElasticSearchRestHighImpl;
import org.sunbird.common.ElasticSearchTcpImpl;
import org.sunbird.common.inf.ElasticService;

public class EsClientFactory {

  private static ElasticService tcpClient = null;
  private static ElasticService restClient = null;

  public static ElasticService getTcpClient() {
    if (tcpClient == null) {
      tcpClient = new ElasticSearchTcpImpl();
    }
    return tcpClient;
  }

  public static ElasticService getRestClient() {
    if (restClient == null) {
      restClient = new ElasticSearchRestHighImpl();
    }
    return restClient;
  }
}
