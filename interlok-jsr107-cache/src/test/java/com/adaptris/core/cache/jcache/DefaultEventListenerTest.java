package com.adaptris.core.cache.jcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.EventType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DefaultEventListenerTest {

  private static Cache<String, Object> cache;

  @BeforeAll
  public static void init() throws Exception {
    cache = Caching.getCachingProvider().getCacheManager().createCache(DefaultEventListenerTest.class.getSimpleName(),
        new MutableConfiguration<String, Object>().setTypes(String.class, Object.class));
  }

  @Test
  public void testListeners() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    MyCacheEventListener l2 = new MyCacheEventListener(null);
    listener.withListeners(l1, l2);
    assertEquals(2, listener.getListeners().size());
    listener.addListener(l1);
    // didn't add. it's a set.
    assertEquals(2, listener.getListeners().size());
    assertTrue(listener.removeListener(l1));
    assertEquals(1, listener.getListeners().size());
    assertFalse(listener.removeListener(new MyCacheEventListener(null)));
  }

  @Test
  public void testConfiguration() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    MyCacheEventListener l2 = new MyCacheEventListener(null);
    listener.withListeners(l1, l2);
    assertNotNull(listener.configuration());
  }

  @Test
  public void testNotifyCreate() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.CREATED, "key", "value"));
    listener.onCreated(list);
    assertEquals(1, l1.putItems);
  }

  @Test
  public void testNotifyExpired() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.EXPIRED, "key", "value"));
    listener.onExpired(list);
    assertEquals(1, l1.expiredItems);

  }

  @Test
  public void testNotifyRemoved() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.REMOVED, "key", "value"));
    listener.onRemoved(list);
    assertEquals(1, l1.removedItems);
  }

  @Test
  public void testNotifyUpdated() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(null);
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.UPDATED, "key", "value"));
    listener.onUpdated(list);
    assertEquals(1, l1.updatedItems);
  }

  @Test
  public void testNotifyWithException() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(new CacheEntryListenerException());
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.UPDATED, "key", "value"));

    assertThrows(CacheEntryListenerException.class, () -> listener.onUpdated(list));
  }

  @Test
  public void testNotifyWithException_GenericRuntime() throws Exception {
    DefaultEventListener listener = new DefaultEventListener();
    MyCacheEventListener l1 = new MyCacheEventListener(new RuntimeException());
    listener.withListeners(l1);
    List<CacheEntryEvent<? extends String, ? extends Object>> list = new ArrayList<>();
    list.add(new MyCacheEntryEvent(EventType.UPDATED, "key", "value"));

    assertThrows(CacheEntryListenerException.class, () -> listener.onUpdated(list));
  }

  private class MyCacheEntryEvent extends CacheEntryEvent<String, Object> {
    private static final long serialVersionUID = 5300240032976089079L;

    private String key;
    private Object value;

    // Don't actually care about the cache, so let's re-use the same name.
    public MyCacheEntryEvent(EventType eventType, String key, Object value) {
      super(cache, eventType);
      this.key = key;
      this.value = value;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
      return null;
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public Object getOldValue() {
      return null;
    }

    @Override
    public boolean isOldValueAvailable() {
      return false;
    }

  }
}
