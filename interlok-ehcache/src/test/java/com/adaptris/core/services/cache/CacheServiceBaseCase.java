package com.adaptris.core.services.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.cache.Cache;
import com.adaptris.core.cache.ehcache.DefaultEhcache;
import com.adaptris.core.cache.ehcache.EhcacheFromFile;
import com.adaptris.core.cache.ehcache.EhcacheFromUrl;
import com.adaptris.util.TimeInterval;

public abstract class CacheServiceBaseCase extends CacheServiceExample {
  protected static final String HYPHEN = "-";

  protected enum CacheImps {

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
        return new EhcacheFromUrl().withConfigurationUrl("http://localhost/path/to/my/ehcache.xml").withCacheName(UUID.randomUUID().toString());
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

    public abstract Cache createCacheImplementation();

    public abstract String getXmlHeader();

    public abstract boolean matches(Cache impl);
  }

  protected AdaptrisMessage createMessage(String payload, Collection<MetadataElement> metadata) {
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage(payload);
    for (MetadataElement element : metadata) {
      msg.addMetadata(element);
    }
    return msg;
  }

  protected abstract CacheServiceBase createServiceForExamples();


  protected DefaultEhcache createCacheInstanceForTests() {
    DefaultEhcache cache = new DefaultEhcache();
    cache.setCacheCleanupInterval(new TimeInterval(5L, TimeUnit.SECONDS));
    cache.setCacheName(UUID.randomUUID().toString());
    cache.setEvictionPolicy(DefaultEhcache.MemoryStoreEvictionPolicy.LRU);
    cache.setMaxElementsInMemory(10);
    cache.setTimeToIdle(new TimeInterval(5L, TimeUnit.SECONDS));
    cache.setTimeToLive(new TimeInterval(3L, TimeUnit.SECONDS));
    return cache;
  }

  protected CacheImps getCacheImp(CacheServiceBase service) {
    CacheImps result = null;
    for (CacheImps sort : CacheImps.values()) {
      if (sort.matches(((CacheConnection) service.getConnection()).getCacheInstance())) {
        result = sort;
        break;
      }
    }
    return result;
  }

  @Override
  protected Object retrieveObjectForSampleConfig() {
    return null;
  }

  @Override
  protected List<CacheServiceBase> retrieveObjectsForSampleConfig() {
    List<CacheServiceBase> result = new ArrayList<CacheServiceBase>();
    for (CacheImps c : CacheImps.values()) {
      CacheServiceBase service = createServiceForExamples();
      service.setConnection(new CacheConnection(c.createCacheImplementation()));
      result.add(service);
    }
    return result;
  }

  @Override
  protected String getExampleCommentHeader(Object object) {
    return super.getExampleCommentHeader(object) + getCacheImp((CacheServiceBase) object).getXmlHeader();
  }

  @Override
  protected String createBaseFileName(Object object) {
    CacheServiceBase p = (CacheServiceBase) object;
    return super.createBaseFileName(object) + HYPHEN
        + ((CacheConnection) p.getConnection()).getCacheInstance().getClass().getSimpleName();
  }

}