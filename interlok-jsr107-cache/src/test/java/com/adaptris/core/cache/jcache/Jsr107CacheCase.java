package com.adaptris.core.cache.jcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.util.TimeInterval;

public abstract class Jsr107CacheCase {

  @Test
  public void testCache_WithConfiguration() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    MyCacheEventListener listener = new MyCacheEventListener(null);
    cache.withNewCacheConfiguration(new NewCacheConfiguration().withExpiration(new TimeInterval(1L, TimeUnit.MINUTES))
        .withEventListener(new DefaultEventListener().withListeners(listener)));
    standardTests(cache);
    assertEquals(6, listener.putItems);
    assertEquals(1, listener.removedItems);
    assertEquals(1, listener.updatedItems);
  }

  @Test
  public void testCache_DefaultProvider() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    standardTests(cache);
  }

  @Test
  public void testCache_DefaultProvider_ShutdownOnClose() throws Exception {
    Jsr107Cache cache = createCacheInstance(true).withShutdownManagerOnClose(true);
    standardTests(cache);
  }

  @Test
  public void testCache_DefaultProvider_SameCacheName() throws Exception {
    Jsr107Cache cache1 = LifecycleHelper.initAndStart(createCacheInstance(false));
    Jsr107Cache cache2 = LifecycleHelper.initAndStart(createCacheInstance(false));
    cache1.put("1", "one");
    assertEquals("one", cache2.get("1"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetKeys() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    LifecycleHelper.initAndStart(cache);
    cache.getKeys();
  }

  @Test
  public void testClear() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    LifecycleHelper.initAndStart(cache);
    cache.clear();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSize() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    LifecycleHelper.initAndStart(cache);
    cache.size();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNotSerializable() throws Exception {
    Jsr107Cache cache = createCacheInstance(true);
    LifecycleHelper.initAndStart(cache);
    cache.put("1", new Object());
  }

  protected void standardTests(Jsr107Cache cache) throws Exception {
    LifecycleHelper.initAndStart(cache);
    try {
      cache.put("one", "1");
      cache.put("two", "2");
      cache.put("three", "3");
      cache.put("four", "4");
      cache.put("five", "5");
      cache.put("hello", "world");

      assertEquals("1", cache.get("one"));
      assertEquals("3", cache.get("three"));
      assertEquals("5", cache.get("five"));
      assertNull(cache.get("XXX"));
      assertEquals("world", cache.get("hello"));

      cache.remove("five");
      assertNull(cache.get("five"));
      cache.put("hello", "goodbye");
      assertEquals("goodbye", cache.get("hello"));

    } finally {
      LifecycleHelper.stopAndClose(cache);
    }
  }

  protected abstract Jsr107Cache createCacheInstance(boolean uniqueCacheName);

}
