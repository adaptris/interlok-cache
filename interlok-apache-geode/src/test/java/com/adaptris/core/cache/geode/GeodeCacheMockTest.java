package com.adaptris.core.cache.geode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.jupiter.api.Test;

import com.adaptris.core.CoreException;

public class GeodeCacheMockTest<testCacheInitWithFile> {
  private Region<Object, Object> objectRegion;
  private DummyGeodeCache sut;

  private ClientCacheFactory createMock() {
    ClientCacheFactory geodeCacheMock = mock(ClientCacheFactory.class);
    ClientCache clientCache = mock(ClientCache.class);
    Region<Object, Object> geodeRegionMock = mock(Region.class);
    ClientRegionFactory<Object, Object> crfMock = mock(ClientRegionFactory.class);

    // ClientCacheFactory set(String name, String value)
    when(geodeCacheMock.set(isA(String.class), isA(String.class))).thenReturn(geodeCacheMock);
    when(geodeCacheMock.addPoolLocator(isA(String.class), isA(Integer.class))).thenReturn(geodeCacheMock);
    // ClientCache create()
    when(geodeCacheMock.create()).thenReturn(clientCache);
    // <K, V> ClientRegionFactory<K, V> createClientRegionFactory(ClientRegionShortcut shortcut);
    when(clientCache.createClientRegionFactory(isA(ClientRegionShortcut.class))).thenReturn(crfMock);

    // Region<K, V> create(String name) throws RegionExistsException;
    when(crfMock.create(isA(String.class))).thenReturn(geodeRegionMock);

    // put
    // get
    when(geodeRegionMock.get(isA(String.class))).thenReturn("mockedValue");
    // remove
    // size
    when(geodeRegionMock.size()).thenReturn(0);
    // clear

    return geodeCacheMock;
  }

  @Test
  public void testCache() throws Exception {
    objectRegion = createMock().create().createClientRegionFactory(ClientRegionShortcut.LOCAL).create("regionA");
    objectRegion.put("myKey", "myValue");
    verify(objectRegion, times(1)).put("myKey", "myValue");

    assertEquals("mockedValue", objectRegion.get("myKey"));
    verify(objectRegion, times(1)).get("myKey");

    objectRegion.size();
    verify(objectRegion, times(1)).size();

    objectRegion.clear();
    verify(objectRegion, times(1)).clear();
  }

  @Test
  public void testCacheInitWithFile() throws Exception {
    sut = new DummyGeodeCache(createMock(), new ClientCacheFromFile().withCacheFilename("cache.xml"));
    sut.init();
  }

  @Test
  public void testCacheInitWithoutFile() throws Exception {
    sut = new DummyGeodeCache(createMock(), new PoolLocatorBuilder().withHostname("localhost").withPort(10334));
    sut.init();
  }

  class DummyGeodeCache extends GeodeCache {
    ClientCacheFactory clientCacheFactory;

    public DummyGeodeCache(ClientCacheFactory clientCacheFactory, ClientCacheBuilder builder) {
      this.clientCacheFactory = clientCacheFactory;
      setCacheBuilder(builder);
      setRegionName("region1");
    }

    @Override
    public void init() throws CoreException {
      initialiseCache(clientCacheFactory);
    }
  }

}
