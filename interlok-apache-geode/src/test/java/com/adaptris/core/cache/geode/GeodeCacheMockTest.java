package com.adaptris.core.cache.geode;

import com.adaptris.core.CoreException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class GeodeCacheMockTest<testCacheInitWithFile> {
    private Region objectRegion;
    private DummyGeodeCache sut;

    private ClientCacheFactory createMock() {
        ClientCacheFactory geodeCacheMock = mock(ClientCacheFactory.class);
        ClientCache clientCache = mock(ClientCache.class);
        Region<String, Object> geodeRegionMock = mock(Region.class);
        ClientRegionFactory crfMock = mock(ClientRegionFactory.class);

        // ClientCacheFactory set(String name, String value)
        when(geodeCacheMock.set(isA(String.class), isA(String.class))).thenReturn(geodeCacheMock);
        when(geodeCacheMock.addPoolLocator(isA(String.class), isA(Integer.class))).thenReturn(geodeCacheMock);
        // ClientCache create()
        when(geodeCacheMock.create()).thenReturn(clientCache);
        // <K, V> ClientRegionFactory<K, V> createClientRegionFactory(ClientRegionShortcut shortcut);
        when((clientCache).createClientRegionFactory(isA(ClientRegionShortcut.class))).thenReturn(crfMock);

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
        sut = new DummyGeodeCache(createMock());
        sut.setCacheFileName("cache.xml");
        sut.init();
    }

    @Test
    public void testCacheInitWithoutFile() throws Exception {
        sut = new DummyGeodeCache(createMock());
        sut.init();
    }

    class DummyGeodeCache extends GeodeCache {
        ClientCacheFactory clientCacheFactory;

        public DummyGeodeCache(ClientCacheFactory clientCacheFactory) {
            this.clientCacheFactory = clientCacheFactory;
            setHostname("localhost");
            setPort(10334);
            setRegionName("region1");
            setClientRegionShortcut("LOCAL");
        }

        @Override
        public void init() throws CoreException {
            initialiseCache(this.clientCacheFactory);
        }
    }
}
