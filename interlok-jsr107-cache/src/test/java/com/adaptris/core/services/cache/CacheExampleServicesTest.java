package com.adaptris.core.services.cache;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.adaptris.core.cache.Cache;
import com.adaptris.core.cache.jcache.BasicJsr107Cache;
import com.adaptris.core.cache.jcache.ConfiguredJsr107Cache;
import com.adaptris.core.cache.jcache.NewCacheConfiguration;
import com.adaptris.interlok.junit.scaffolding.services.BasicCacheExampleGenerator;
import com.adaptris.interlok.junit.scaffolding.services.CacheServiceExample;
import com.adaptris.util.TimeInterval;

// Generates example services that use ehcache as the cache connection
public class CacheExampleServicesTest extends CacheServiceExample {

  private enum CacheImps implements CacheExampleImplementation {

    BasicJsr107() {
      @Override
      public Cache createCacheImplementation() {
        return new BasicJsr107Cache().withCacheName(UUID.randomUUID().toString())
            .withNewCacheConfiguration(new NewCacheConfiguration().withExpiration(new TimeInterval(60L, TimeUnit.SECONDS)));
      }

      @Override
      public String getXmlHeader() {
        return "<!--\n\nThe cache will inherit defaults from default Caching provider"
            + "\nany new caches will have an expiration interval of 60 seconds\n\n-->\n";
      }

      @Override
      public boolean matches(Cache impl) {
        return BasicJsr107Cache.class.equals(impl.getClass());
      }
    },
    ConfiguredJsr107() {
      @Override
      public Cache createCacheImplementation() {
        return new ConfiguredJsr107Cache().withConfigurationUrl("/path/to/my/ehcache.xml")
            .withProviderClassname(ConfiguredJsr107Cache.ProviderNames.EHCACHE3.name())
            .withCacheName(UUID.randomUUID().toString());
      }

      @Override
      public String getXmlHeader() {
        return "<!--\n\nThe CacheManager will be configured the named xml configuration file"
            + "\nany new caches will have the default behaviour based on the configuration file\n\n-->\n";
      }

      @Override
      public boolean matches(Cache impl) {
        return ConfiguredJsr107Cache.class.equals(impl.getClass());
      }
    };

    public abstract boolean matches(Cache impl);
  }

  @Override
  protected Iterable<CacheExampleImplementation> getExampleCacheImplementations() {
    return Arrays.asList(CacheImps.values());
  }

  @Override
  protected Iterable<CacheExampleServiceGenerator> getExampleGenerators() {
    return BasicCacheExampleGenerator.generators();
  }

  @Override
  protected CacheExampleImplementation getImplementation(CacheServiceBase service) {
    CacheImps result = null;
    for (CacheImps sort : CacheImps.values()) {
      if (sort.matches(((CacheConnection) service.getConnection()).getCacheInstance())) {
        result = sort;
        break;
      }
    }
    return result;
  }

}
