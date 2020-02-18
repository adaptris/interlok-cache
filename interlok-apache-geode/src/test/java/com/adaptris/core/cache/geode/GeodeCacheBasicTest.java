package com.adaptris.core.cache.geode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.apache.commons.lang3.StringUtils;
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
  public void testCache() throws Exception {

  }

  @Test
  public void testRegion() throws Exception {
    String givenRegionName = "regionX";
    sut.getRegionName();
    sut.setRegionName(givenRegionName);
  }

  @Test
  public void testHostname() throws Exception {
    assertTrue(StringUtils.isBlank(sut.getHostname()));
    String myHostname = "myHost";
    sut.setHostname(myHostname);
    assertEquals(myHostname, sut.getHostname());
  }

  @Test
  public void testPort() throws Exception {
    assertEquals(0, sut.getPort());
    sut.setPort(999);
    assertEquals(999, sut.getPort());
  }

  @Test
  public void testClientRegionShortcut() throws Exception {
    StringUtils.isBlank(sut.getClientRegionShortcut());
    sut.setClientRegionShortcut(ClientRegionShortcut.LOCAL.toString());
    assertEquals(ClientRegionShortcut.LOCAL.toString(), sut.getClientRegionShortcut());
    assertEquals(ClientRegionShortcut.LOCAL, sut.clientRegionShortcut());
  }

  @Test
  public void testCacheFileName() throws Exception {
    assertTrue(StringUtils.isBlank(sut.getCacheFileName()));
    String cacheFileName = "cache-file.xml";
    sut.setCacheFileName(cacheFileName);
    assertEquals(cacheFileName, sut.getCacheFileName());
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
      assertEquals("GeodeCache must have clientRegion and RegionName specified", se.getMessage());
    }
  }

  @Test
  public void testMandatoryItemsRegionTypeSet() throws Exception {
    try {
      sut.setClientRegionShortcut("LOCAL");
      sut.init();
      fail();
    } catch (ServiceException se) {
      assertEquals("GeodeCache must have clientRegion and RegionName specified", se.getMessage());
    }
  }

  @Test
  public void testMandatoryItemsRegionNameSet() throws Exception {
    try {
      sut.setRegionName("regionB");
      sut.init();
      fail();
    } catch (ServiceException se) {
      assertEquals("GeodeCache must have clientRegion and RegionName specified", se.getMessage());
    }
  }
}
