package com.adaptris.core.cache.jcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.adaptris.util.GuidGenerator;

public class Configuredsr107CacheTest extends Jsr107CacheCase {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCache_SpecificProvider() throws Exception {
    ConfiguredJsr107Cache cache = createCacheInstance(true);
    cache.withProviderClassname(org.jsr107.ri.spi.RICachingProvider.class.getCanonicalName());
    standardTests(cache);
  }

  @Test
  public void testCache_FriendlyProvider() throws Exception {
    ConfiguredJsr107Cache cache = createCacheInstance(true);
    cache.withProviderClassname(ConfiguredJsr107Cache.ProviderNames.REFERENCE.name());
    standardTests(cache);
  }

  @Test
  public void testCache_ProviderWithURI() throws Exception {
    ConfiguredJsr107Cache cache = createCacheInstance(true);
    cache.withConfigurationUrl("http://localhost/mycache");
    standardTests(cache);
  }

  @Override
  protected ConfiguredJsr107Cache createCacheInstance(boolean uniqueCacheName) {
    ConfiguredJsr107Cache cache = new ConfiguredJsr107Cache()
        .withCacheName(uniqueCacheName ? new GuidGenerator().safeUUID() : ConfiguredJsr107Cache.class.getSimpleName());
    return cache;
  }
}
