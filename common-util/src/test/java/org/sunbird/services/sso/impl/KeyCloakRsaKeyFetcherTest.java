package org.sunbird.services.sso.impl;

import java.security.PublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;

/**
 * @author github.com/iostream04 This class will Test KeyCloakRsaKeyFetcherTest's
 *     getPublicKeyFromKeyCloak method .
 */
public class KeyCloakRsaKeyFetcherTest {
  public static final String FAIL_TEST = "fail-check";

  @Test
  public void getPublicKeyFromKeyCloakTest() {
    PublicKey key =
        new KeyCloakRsaKeyFetcher()
            .getPublicKeyFromKeyCloak(
                KeyCloakConnectionProvider.SSO_URL, KeyCloakConnectionProvider.SSO_REALM);
    Assert.assertNotNull(key);
  }

  /*
   * In this test case failure is tested by passing FAIL_TEST instead of actual SSO_REALM
   *
   */

  @Test
  public void getPublicKeyFromKeyCloakTestFail() {
    PublicKey key =
        new KeyCloakRsaKeyFetcher()
            .getPublicKeyFromKeyCloak(KeyCloakConnectionProvider.SSO_URL, FAIL_TEST);
    boolean check = false;
    if (key != null) {
      check = true;
    }
    Assert.assertFalse(check);
  }
}
