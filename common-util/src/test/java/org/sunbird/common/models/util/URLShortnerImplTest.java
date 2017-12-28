package org.sunbird.common.models.util;

import org.junit.Assert;
import org.junit.Test;
import org.sunbird.common.models.util.url.URLShortner;
import org.sunbird.common.models.util.url.URLShortnerImpl;

public class URLShortnerImplTest {
     
	@Test
 	public void urlShortTest() {
		URLShortner shortner = new URLShortnerImpl();
		String url = shortner.shortUrl("https://staging.open-sunbird.org/");
		Assert.assertNotNull(url);
	}

	@Test 
	public void getShortUrlTest() {
		URLShortnerImpl shortnerImpl = new URLShortnerImpl();
		String url = shortnerImpl.getUrl();
		Assert.assertEquals(url, "sunbird_web_url");
	}
}
