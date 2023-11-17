package com.adaptris.core.services.cache;

import java.util.Arrays;
import java.util.UUID;

import com.adaptris.core.cache.Cache;
import com.adaptris.core.cache.ehcache.DefaultEhcache;
import com.adaptris.core.cache.ehcache.EhcacheFromFile;
import com.adaptris.core.cache.ehcache.EhcacheFromUrl;
import com.adaptris.interlok.junit.scaffolding.services.BasicCacheExampleGenerator;
import com.adaptris.interlok.junit.scaffolding.services.CacheServiceExample;

// Generates example services that use ehcache as the cache connection
public class CacheExampleServicesTest extends CacheServiceExample {

  private enum CacheImps implements CacheExampleImplementation {

    DefEhcache() {
      @Override
      public Cache createCacheImplementation() {
        return new DefaultEhcache().withCacheName(UUID.randomUUID().toString());
      }

      @Override
      public String getXmlHeader() {
        return "<!--\n\nThe configured cache will inherit defaults from the standard"
            + "\nehcache defaults; either from ehcache.xml or ehcache-failsafe.xml in the classpath\n\n-->\n";
      }

      @Override
      public boolean matches(Cache impl) {
        return DefaultEhcache.class.equals(impl.getClass());
      }
    },
    XmlEhcache() {
      @Override
      public Cache createCacheImplementation() {
        return new EhcacheFromFile().withXmlConfigurationFile("/path/to/my/ehcache.xml").withCacheName(UUID.randomUUID().toString());
      }

      @Override
      public String getXmlHeader() {
        return "<!--\n\nThe configured cache will inherit defaults from the the named xml configuration file"
            + "\nrather than from ehcache.xml or ehcache-failsafe.xml in the classpath\n\n-->\n";
      }

      @Override
      public boolean matches(Cache impl) {
        return EhcacheFromFile.class.equals(impl.getClass());
      }
    },
    UrlEhcache() {
      @Override
      public Cache createCacheImplementation() {
        return new EhcacheFromUrl().withConfigurationUrl("http://localhost/path/to/my/ehcache.xml")
            .withCacheName(UUID.randomUUID().toString());
      }

      @Override
      public String getXmlHeader() {
        return "<!--\n\nThe configured cache will inherit defaults from the the named xml configuration file"
            + "\nrather than from ehcache.xml or ehcache-failsafe.xml in the classpath\n\n-->\n";
      }

      @Override
      public boolean matches(Cache impl) {
        return EhcacheFromUrl.class.equals(impl.getClass());
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
