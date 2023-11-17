package com.adaptris.core.cache.ehcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.adaptris.util.TimeInterval;

public class EhcacheFromUrlTest extends EhcacheFromConfigCase {

  @Test
  public void testSetCacheConfigurationURL() throws Exception {
    EhcacheFromUrl myCache = createCacheInstance();
    assertNull(myCache.getConfigurationUrl());
    myCache.setConfigurationUrl("http://my.server.com/path/to/ehcache.xml");
    assertEquals("http://my.server.com/path/to/ehcache.xml", myCache.getConfigurationUrl());
    try {
      myCache.setConfigurationUrl(null);
      fail();
    } catch (IllegalArgumentException expected) {

    }
    assertEquals("http://my.server.com/path/to/ehcache.xml", myCache.getConfigurationUrl());
  }

  @Test
  public void testDifferentConfigFiles() throws Exception {
    EhcacheFromUrl myCache1 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    EhcacheFromUrl myCache2 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE_ALTERNATE));
    assertNotSame(myCache1.createCacheManager(), myCache2.createCacheManager());
  }

  @Test
  public void testSameConfigFile() throws Exception {
    EhcacheFromUrl myCache1 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    EhcacheFromUrl myCache2 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    assertEquals(myCache1.createCacheManager(), myCache2.createCacheManager());
  }

  protected EhcacheFromUrl createCacheInstance(String url) throws Exception {
    EhcacheFromUrl cache = createCacheInstance().withConfigurationUrl(createURLFrom(url).toString())
        .withCacheCleanupInterval(new TimeInterval(INTERVAL, TimeUnit.MILLISECONDS));
    return cache;
  }

  @Override
  protected EhcacheFromUrl createCacheInstance(boolean useEhcacheXml) throws Exception {
    EhcacheFromUrl cache = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE)).withShutdownCacheManagerOnClose(true);
    return configure(cache, useEhcacheXml);
  }

  @Override
  protected EhcacheFromUrl createCacheInstance() {
    return new EhcacheFromUrl();
  }

  private URL createURLFrom(String path) throws MalformedURLException {
    File f = new File(path);
    return f.toURI().toURL();
  }

}
