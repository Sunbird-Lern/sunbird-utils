package org.sunbird.services.sso.impl;

import java.security.PublicKey;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.util.KeyCloakConnectionProvider;

/**
 * @author   github.com/iostream04
 *  This class will Test KeyCloakRsaKeyFetcherTest's getPublicKeyFromKeyCloak method .
 */

public class KeyCloakRsaKeyFetcherTest {

  @Test
  public void getPublicKeyFromKeyCloakTest() {
    PublicKey key = new KeyCloakRsaKeyFetcher().getPublicKeyFromKeyCloak(KeyCloakConnectionProvider.SSO_URL,
			KeyCloakConnectionProvider.SSO_REALM);
    Assert.assertNotNull(key);
  }
	
}
