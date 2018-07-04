package com.adaptris.core.cache.ehcache;

import java.util.concurrent.TimeUnit;

import com.adaptris.util.TimeInterval;

public class DefaultCacheTest extends EhcacheCacheCase {

  public DefaultCacheTest(String name) {
    super(name);
  }

  @Override
  public void setUp() throws Exception {
  }

  @Override
  public void tearDown() {

  }

  public void testSetCacheName() throws Exception {
    DefaultEhcache myCache = createCacheInstance();
    assertEquals(EhcacheCache.DEFAULT_CACHE_NAME, myCache.getCacheName());
    myCache.setCacheName("name");
    assertEquals("name", myCache.getCacheName());
    try {
      myCache.setCacheName(null);
      fail();
    }
    catch (IllegalArgumentException expected) {

    }
    assertEquals("name", myCache.getCacheName());
  }

  public void testSetCacheCleanupInterval() throws Exception {
    DefaultEhcache myCache = createCacheInstance();
    assertNull(myCache.getCacheCleanupInterval());
    assertEquals(new TimeInterval(1L, TimeUnit.MINUTES).toMilliseconds(), myCache.cacheCleanupIntervalMs());

    TimeInterval interval = new TimeInterval(1L, TimeUnit.HOURS);

    myCache.setCacheCleanupInterval(interval);
    assertEquals(interval, myCache.getCacheCleanupInterval());
    assertEquals(interval.toMilliseconds(), myCache.cacheCleanupIntervalMs());

    myCache.setCacheCleanupInterval(null);
    assertNull(myCache.getCacheCleanupInterval());
    assertEquals(new TimeInterval(1L, TimeUnit.MINUTES).toMilliseconds(), myCache.cacheCleanupIntervalMs());
  }

  public void testSetMaxElementsInMemory() throws Exception {
    DefaultEhcache myCache = createCacheInstance();
    assertNull(myCache.getMaxElementsInMemory());
    assertEquals(0, myCache.maxElementsInMemory());

    myCache.setMaxElementsInMemory(Integer.valueOf(10));
    assertEquals(new Integer(10), myCache.getMaxElementsInMemory());
    assertEquals(10, myCache.maxElementsInMemory());

    myCache.setMaxElementsInMemory(null);
    assertNull(myCache.getMaxElementsInMemory());
    assertEquals(0, myCache.maxElementsInMemory());
  }

  public void testSetTimeToIdle() throws Exception {
    DefaultEhcache myCache = createCacheInstance();
    assertNull(myCache.getTimeToIdle());
    assertEquals(-1, myCache.timeToIdleSeconds());

    TimeInterval interval = new TimeInterval(1L, TimeUnit.HOURS);

    myCache.setTimeToIdle(interval);
    assertEquals(interval, myCache.getTimeToIdle());
    assertEquals(TimeUnit.MILLISECONDS.toSeconds(interval.toMilliseconds()), myCache.timeToIdleSeconds());

    myCache.setTimeToIdle(null);
    assertNull(myCache.getCacheCleanupInterval());
    assertEquals(-1, myCache.timeToIdleSeconds());
  }

  public void testSetTimeToLive() throws Exception {
    DefaultEhcache myCache = createCacheInstance();
    assertNull(myCache.getTimeToLive());
    assertEquals(-1, myCache.timeToLiveSeconds());

    TimeInterval interval = new TimeInterval(1L, TimeUnit.HOURS);

    myCache.setTimeToLive(interval);
    assertEquals(interval, myCache.getTimeToLive());
    assertEquals(TimeUnit.MILLISECONDS.toSeconds(interval.toMilliseconds()), myCache.timeToLiveSeconds());

    myCache.setTimeToLive(null);
    assertNull(myCache.getCacheCleanupInterval());
    assertEquals(-1, myCache.timeToLiveSeconds());
  }


  @Override
  protected DefaultEhcache createCacheInstance(boolean useEhcacheXml) throws Exception {
    return configure(createCacheInstance().withCacheCleanupInterval(new TimeInterval(INTERVAL, TimeUnit.MILLISECONDS))
        .withShutdownCacheManagerOnClose(true), useEhcacheXml);
  }

  protected DefaultEhcache createCacheInstance() {
    return new DefaultEhcache();
  }
}
