package com.adaptris.core.cache.ehcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.adaptris.util.TimeInterval;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;

public class EhcacheFromXmlTest extends EhcacheFromConfigCase {
  @Override
  public boolean isAnnotatedForJunit4() {
    return true;
  }
  @Test
  public void testSetCacheConfigurationFile() throws Exception {
    EhcacheFromFile myCache = createCacheInstance();
    assertNull(myCache.getXmlConfigurationFilename());
    myCache.setXmlConfigurationFilename("a filename");
    assertEquals("a filename", myCache.getXmlConfigurationFilename());
    try {
      myCache.setXmlConfigurationFilename(null);
      fail();
    }
    catch (IllegalArgumentException expected) {

    }
    assertEquals("a filename", myCache.getXmlConfigurationFilename());
  }

  @Test
  public void testDifferentConfigFiles() throws Exception {
    EhcacheFromFile myCache1 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    EhcacheFromFile myCache2 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE_ALTERNATE));
    CacheManager myCm1 = myCache1.createCacheManager();
    CacheManager myCm2 = myCache2.createCacheManager();
    assertNotSame(myCm1, myCm2);
    myCm1.shutdown();
    assertEquals(Status.STATUS_ALIVE, myCm2.getStatus());
  }

  @Test
  public void testSameConfigFile() throws Exception {
    EhcacheFromFile myCache1 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    EhcacheFromFile myCache2 = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE));
    CacheManager myCm1 = myCache1.createCacheManager();
    CacheManager myCm2 = myCache2.createCacheManager();
    assertEquals(myCm1, myCm2);
    myCm1.shutdown();
    assertEquals(Status.STATUS_SHUTDOWN, myCm2.getStatus());
  }

  protected EhcacheFromFile createCacheInstance(String filename) throws Exception {
    EhcacheFromFile cache = createCacheInstance().withXmlConfigurationFile(filename)
        .withCacheCleanupInterval(new TimeInterval(INTERVAL, TimeUnit.MILLISECONDS));
    return cache;
  }

  @Override
  protected EhcacheFromFile createCacheInstance(boolean useEhcacheXml) throws Exception {
    EhcacheFromFile cache = createCacheInstance(PROPERTIES.getProperty(EHCACHE_XML_FILE)).withShutdownCacheManagerOnClose(true);
    return configure(cache, useEhcacheXml);
  }

  @Override
  protected EhcacheFromFile createCacheInstance() {
    return new EhcacheFromFile();
  }
}
