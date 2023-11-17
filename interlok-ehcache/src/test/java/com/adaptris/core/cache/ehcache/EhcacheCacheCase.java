package com.adaptris.core.cache.ehcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.adaptris.core.cache.CacheEventListener;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.util.TimeInterval;

public abstract class EhcacheCacheCase extends BaseCase {

  static final String EHCACHE_XML_CACHE_NAME = "unitTestCache";
  static final long TTL_INTERVAL = 5500L;
  static final long INTERVAL = 500L;

  @Test
  public void testRedmine7064() throws Exception {
    testCacheManagerShutdown_CacheStillActive();
  }

  @Test
  public void testCacheManagerShutdown_CacheStillActive() throws Exception {
    EhcacheCache cache1 = createCacheInstance(true);
    EhcacheCache cache2 = createCacheInstance(true);
    // These should now be functionally equivalent caches.
    start(cache1);
    start(cache2);
    try {
      // Both caches are started.
      cache1.put("one", "1");
      cache1.put("two", "2");
      assertEquals(2, cache1.size());
      assertEquals(2, cache2.size());
      stop(cache1);

      // What happens now?
      cache2.put("three", "3");
      cache2.put("four", "4");
    } finally {
      stop(cache1);
      stop(cache2);
    }
  }

  @Test
  public void testPut() throws Exception {
    doPutTests(createCacheInstance(false));
  }

  @Test
  public void testPut_UsingEhCacheXml() throws Exception {
    doPutTests(createCacheInstance(true));
  }

  private void doPutTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      MyCacheEventListener listener = new MyCacheEventListener();
      cache.getEventListener().addListener(listener);
      cache.put("one", "1");
      cache.put("two", "2");
      // Just to put something non-serializable in there.
      cache.put("three", new Object());
      cache.put("four", "4");
      cache.put("five", "5");
      assertEquals(5, cache.size());
      assertEquals(5, listener.putItems);
      cache.put("six", "6");
      assertEquals(5, cache.size());
      assertEquals(6, listener.putItems);
      assertEquals(1, listener.evictedItems);
      cache.getEventListener().removeListener(listener);
    } finally {
      stop(cache);
    }
  }

  @Test
  public void testUpdate() throws Exception {
    doUpdateTests(createCacheInstance(false));
  }

  @Test
  public void testUpdate_UsingEhCacheXml() throws Exception {
    doUpdateTests(createCacheInstance(true));
  }

  private void doUpdateTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      MyCacheEventListener listener = new MyCacheEventListener();
      cache.getEventListener().addListener(listener);
      cache.put("one", "1");
      cache.put("one", "one");
      assertEquals(1, cache.size());
      assertEquals(1, listener.putItems);
      assertEquals(1, listener.updatedItems);
      cache.getEventListener().removeListener(listener);

    } finally {
      stop(cache);
    }
  }

  @Test
  public void testClear() throws Exception {
    doClearTests(createCacheInstance(false));
  }

  @Test
  public void testClear_UsingEhCacheXml() throws Exception {
    doClearTests(createCacheInstance(true));
  }

  private void doClearTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");
      assertEquals(5, cache.size());
      cache.clear();
      assertEquals(0, cache.size());
    } finally {
      stop(cache);
    }
  }

  @Test
  public void testGet() throws Exception {
    doGetTests(createCacheInstance(false));
  }

  @Test
  public void testGet_UsingEhCacheXml() throws Exception {
    doGetTests(createCacheInstance(true));
  }

  private void doGetTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");

      assertEquals("1", cache.get("one"));
      assertEquals("3", cache.get("three"));
      assertEquals("5", cache.get("five"));
      assertNull(cache.get("XXX"));
      assertEquals(5, cache.size());
    } finally {
      stop(cache);
    }
  }

  @Test
  public void testRemove() throws Exception {
    doRemoveTests(createCacheInstance(false));
  }

  @Test
  public void testRemove_UsingEhCacheXml() throws Exception {
    doRemoveTests(createCacheInstance(true));
  }

  private void doRemoveTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      MyCacheEventListener listener = new MyCacheEventListener();
      cache.getEventListener().addListener(listener);
      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");

      cache.remove("one");
      cache.remove("two");
      cache.remove("three");
      cache.remove("something");

      assertEquals(2, cache.size());
      // Our EventListener impl doesn't check if an item exists before it gets removed...
      // So technically we have attempted to remove 4 items, even though one
      // of them doesn't exist.
      assertEquals(4, listener.removedItems);
      assertTrue(cache.getKeys().contains("five"), "Should contain five");
      assertTrue(cache.getKeys().contains("four"), "Should contain four");
      cache.getEventListener().removeListener(listener);

    } finally {
      stop(cache);
    }
  }

  @Test
  public void testGetKeys() throws Exception {
    doGetKeysTests(createCacheInstance(false));
  }

  @Test
  public void testGetKeys_UsingEhCacheXml() throws Exception {
    doGetKeysTests(createCacheInstance(true));
  }

  private void doGetKeysTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();

      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");

      assertEquals(5, cache.size());
      assertTrue(cache.getKeys().contains("five"), "Should contain five");
      assertTrue(cache.getKeys().contains("four"), "Should contain four");
      assertTrue(cache.getKeys().contains("three"), "Should contain three");
      assertTrue(cache.getKeys().contains("two"), "Should contain two");
      assertTrue(cache.getKeys().contains("one"), "Should contain one");
    } finally {
      stop(cache);
    }
  }

  @Test
  public void testListener() throws Exception {
    doListenerTests(createCacheInstance(false));
  }

  @Test
  public void testListener_UsingEhcacheXml() throws Exception {
    doListenerTests(createCacheInstance(true));
  }

  private void doListenerTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();
      MyCacheEventListener listener = new MyCacheEventListener();
      MyCacheEventListener removedListener = new MyCacheEventListener();
      cache.getEventListener().withListeners(listener, removedListener);
      cache.getEventListener().removeListener(removedListener);
      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");
      assertEquals(5, listener.putItems);
      assertEquals(0, removedListener.putItems);
      cache.getEventListener().removeListener(listener);
    } finally {
      stop(cache);
    }
  }

  @Test
  public void testExpiration() throws Exception {
    doExpirationTests(createCacheInstance(false));
  }

  @Test
  public void testExpiration_UsingEhCacheXml() throws Exception {
    doExpirationTests(createCacheInstance(true));
  }

  private void doExpirationTests(EhcacheCache cache) throws Exception {
    start(cache);
    try {
      cache.clear();
      MyCacheEventListener listener = new MyCacheEventListener();
      cache.getEventListener().addListener(listener);
      String key1 = UUID.randomUUID().toString();
      String key2 = UUID.randomUUID().toString();
      String key3 = UUID.randomUUID().toString();
      String key4 = UUID.randomUUID().toString();
      String key5 = UUID.randomUUID().toString();

      cache.put(key1, "1");
      cache.put(key2, "2");
      cache.put(key3, "3");
      cache.put(key4, "4");
      cache.put(key5, "5");

      TimeUnit.MILLISECONDS.sleep(TTL_INTERVAL);

      assertEquals(5, listener.expiredItems);
      assertNull(cache.get(key1));
      assertNull(cache.get(key2));
      assertNull(cache.get(key3));
      assertNull(cache.get(key4));
      assertNull(cache.get(key5));
      cache.getEventListener().removeListener(listener);
    } finally {
      stop(cache);
    }
  }

  protected abstract EhcacheCache createCacheInstance(boolean useEhCacheXml) throws Exception;

  protected <T extends DefaultEhcache> T configure(T cache, boolean useEhcacheXml) {
    if (useEhcacheXml) {
      cache.setCacheName(EHCACHE_XML_CACHE_NAME);
    } else {
      cache.withEvictionPolicy(DefaultEhcache.MemoryStoreEvictionPolicy.LRU).withMaxElementsInMemory(5)
          .withTimeToIdle(new TimeInterval(1L, TimeUnit.SECONDS)).withTimeToLive(new TimeInterval(10L, TimeUnit.SECONDS))
          .withCacheName(UUID.randomUUID().toString());
    }
    return cache;
  }

  private class MyCacheEventListener implements CacheEventListener {
    private int evictedItems = 0, expiredItems = 0, putItems = 0, removedItems = 0, updatedItems = 0;

    @Override
    public void itemEvicted(String key, Object value) {
      evictedItems++;
    }

    @Override
    public void itemExpired(String key, Object value) {
      expiredItems++;
    }

    @Override
    public void itemPut(String key, Object value) {
      putItems++;
    }

    @Override
    public void itemRemoved(String key, Object value) {
      removedItems++;
    }

    @Override
    public void itemUpdated(String key, Object value) {
      updatedItems++;
    }
  }
}
