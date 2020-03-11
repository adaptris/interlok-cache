package com.adaptris.core.cache.geode;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GeodeCacheLocalTest {

  private GeodeCache cache;

  @Before
  public void setup() throws Exception {
    cache = new GeodeCache();
    cache.setRegionName("region1");
    cache.setCacheBuilder(new PoolLocatorBuilder().withHostname("localhost").withPort(10334));
    cache.setClientRegionShortcut(ClientRegionShortcut.LOCAL);
    cache.init();
  }

  @Test
  public void testAdditionOfItems() throws Exception {
    // put some items
    cache.put("key1", "apple");
    cache.put("key2", "banana");
    cache.put("key3", "coconut");
    // check that they are present
    assertEquals("apple", cache.get("key1"));
    assertEquals("banana", cache.get("key2"));
    assertEquals("coconut", cache.get("key3"));
    assertEquals(3, cache.size());
    cache.clear();
    assertEquals(0, cache.size());
  }

  @Test
  public void testAddingAndRemovingKeys() throws Exception {
    cache.put("key1", "apple");
    cache.put("key2", "banana");
    cache.put("key3", "coconut");
    // check that they are present
    assertEquals("apple", cache.get("key1"));
    assertEquals("banana", cache.get("key2"));
    assertEquals("coconut", cache.get("key3"));
    assertEquals(3, cache.size());
    cache.remove("key1");
    assertEquals(2, cache.size());
    // confirm the key is removed
    assertNull(cache.get("key1"));
    assertEquals("banana", cache.get("key2"));
    assertEquals("coconut", cache.get("key3"));
    cache.clear();
    assertEquals(0, cache.size());
  }

  @Test
  public void testAddingObjectValue() throws Exception {
    cache.put("key1", new Integer(12));
    cache.put("key2", new Integer(13));
    assertEquals(2, cache.size());
    assertEquals(13, cache.get("key2"));
  }

  @After
  public void teardown() throws Exception {
    cache.close();
  }
}
