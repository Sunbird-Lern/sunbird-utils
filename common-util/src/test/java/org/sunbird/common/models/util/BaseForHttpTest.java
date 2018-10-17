package org.sunbird.common.models.util;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.mockito.PowerMockito.doThrow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.management.Notification;

import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.services.sso.impl.KeyCloakServiceImpl;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "javax.security.*"})
@PrepareForTest({
  OutputStreamWriter.class,
  URL.class,
  BufferedReader.class,
  HttpUtil.class,
  HttpClients.class,
  Notification.class,
  KeyCloakConnectionProvider.class,
  KeyCloakServiceImpl.class
})
public abstract class BaseForHttpTest {

  @Before
  public void addMockRules() {

    httpRules("content/v3/list", "not-empty-output");
    httpRules("/search/health", "not-empty-output");
    httpRules("/content/wrong/v3/list", null, true, null);
    httpRules("v1/issuer/issuers", "{\"message\":\"success\"}");
    httpRules("https://dev.ekstep.in/api/data/v3", "{\"message\":\"success\"}");
  }

  protected void httpRules(String urlContains, String outputexpected) {
    httpRules(urlContains, outputexpected, false, null);
  }

  protected void httpRules(
      String urlContains, String outputexpected, boolean throwError, String paramContains) {
    URL url = mock(URL.class);
    HttpURLConnection connection = mock(HttpURLConnection.class);
    OutputStream outStream = mock(OutputStream.class);
    OutputStreamWriter outStreamWriter = mock(OutputStreamWriter.class);
    InputStream inStream = mock(InputStream.class);
    BufferedReader reader = mock(BufferedReader.class);
    try {

      whenNew(URL.class).withArguments(Mockito.contains(urlContains)).thenReturn(url);
      whenNew(OutputStreamWriter.class).withAnyArguments().thenReturn(outStreamWriter);
      when(url.openConnection()).thenReturn(connection);
      when(connection.getOutputStream()).thenReturn(outStream);
      if (paramContains != null && throwError) {
        doThrow(new FileNotFoundException()).when(outStreamWriter).write(Mockito.anyString());
      }
      if (throwError) {
        when(connection.getInputStream()).thenThrow(FileNotFoundException.class);
      } else {
        when(connection.getInputStream()).thenReturn(inStream);
      }
      whenNew(BufferedReader.class).withAnyArguments().thenReturn(reader);
      when(reader.readLine()).thenReturn(outputexpected, null);
    } catch (Exception e) {
      Assert.fail("Mock rules addition failed " + e.getMessage());
    }
  }
}
