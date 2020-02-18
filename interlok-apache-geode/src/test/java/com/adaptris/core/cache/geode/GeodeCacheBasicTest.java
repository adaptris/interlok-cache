package com.adaptris.core.cache.geode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Before;
import org.junit.Test;
import com.adaptris.core.ServiceException;

public class GeodeCacheBasicTest {
  GeodeCache sut;

  @Before
  public void init() {
    sut = new GeodeCache();
  }

  @Test
  public void testRegion() throws Exception {
    String givenRegionName = "regionX";
    assertNull(sut.getRegionName());
    sut.setRegionName(givenRegionName);
    assertEquals("regionX", sut.getRegionName());
  }

  @Test
  public void testClientRegionShortcut() throws Exception {
    sut.setClientRegionShortcut(ClientRegionShortcut.LOCAL);
    assertEquals(ClientRegionShortcut.LOCAL, sut.getClientRegionShortcut());
    assertEquals(ClientRegionShortcut.LOCAL, sut.clientRegionShortcut());
  }

  @Test
  public void testDurable() throws Exception {
    assertNull(sut.getDurable());
    sut.setDurable(true);
    assertEquals(true, sut.getDurable());
    sut.setDurable(false);
    assertEquals(false, sut.getDurable());
  }

  @Test
  public void testMandatoryItemsNoneSet() throws Exception {
    try {
      sut.init();
      fail();
    } catch (ServiceException se) {
      assertEquals("GeodeCache must have RegionName specified", se.getMessage());
    }
  }

  @Test
  public void testClose() throws Exception {
    GeodeCache cache = new GeodeCache();
    cache.close();
    cache.setRegionName("region1");
    cache.setCacheBuilder(new PoolLocatorBuilder().withHostname("localhost").withPort(10334));
    cache.setClientRegionShortcut(ClientRegionShortcut.LOCAL);
    cache.init();
    cache.close();
  }
}
