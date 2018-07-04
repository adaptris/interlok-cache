package com.adaptris.core.cache.ehcache;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.CoreException;
import com.adaptris.core.cache.Cache;
import com.adaptris.core.util.Args;
import com.adaptris.util.FifoMutexLock;
import com.adaptris.util.TimeInterval;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

/**
 * Base implementation of com.adaptris.cache.Cache implemented using Ehcache.
 * <p>
 * This uses the {@link CacheManager#create()} to create the cache manager which will end up using the default XML configuration
 * (ehcache.xml or ehcache-failsafe.xml). In the event that the cache manager does not contain a cache that matches the name
 * {@link #getCacheName()}, then a new basic one will be implemented based on the additional parameters that are configured. If the
 * cache already exists, then it is used as is, without any additional configuration.
 * </p>
 * <p>
 * Take care if you are using ehcache in multicast distributed mode. Calling close on any ehcache service will by default close the
 * cacheManager, which can cause problems across the distributed adapters. In these cases the distributed adapters can no longer
 * perform operations on any of the caches related to this cache manager.
 * </p>
 * <p>
 * If you are using ehcache in distributed mode you should consider setting this classes shutdown-cache-manager-on-close to false.
 * Which will not call CacheManager.shutdown(). And instead you can set the jvm property;
 * {@code -Dnet.sf.ehcache.enableShutdownHook=true}
 * </p>
 * .
 */
public abstract class EhcacheCache implements Cache {
  private static final TimeInterval DEFAULT_CLEANUP = new TimeInterval(60L, TimeUnit.SECONDS);
  private static final transient FifoMutexLock lock = new FifoMutexLock();
  static final String DEFAULT_CACHE_NAME = "ADAPTER_DEFAULT_CACHE";

  @InputFieldDefault(value = "true")
  @AdvancedConfig
  private Boolean shutdownCacheManagerOnClose;
  @NotBlank
  @AutoPopulated
  private String cacheName;
  @Valid
  private TimeInterval cacheCleanupInterval;

  @Valid
  @NotNull
  @AutoPopulated
  @AdvancedConfig
  private EhcacheEventListener eventListener = new EhcacheEventListener();

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());
  private transient CacheManager cacheManager;
  private transient Ehcache cache;

  public EhcacheCache() {
    setCacheName(DEFAULT_CACHE_NAME);
    setEventListener(new EhcacheEventListener());
  }

  /**
   * Initialises the underlying ehcache CacheManager. If the cache definition already exists (either created in another thread or
   * else created in the ehcache.xml configuration file) then it will be reused. All cache instances are decorated by using a
   * wrapper of {@link TimedExpirationCache} in order to ensure timely eviction of entries.
   * 
   */
  @Override
  public void init() throws CoreException {
    initialiseCache();
  }

  protected abstract CacheManager createCacheManager() throws CacheException, MalformedURLException;

  protected abstract Ehcache createCache();

  private boolean cacheManagerAlive() {
    return (cacheManager != null) ? cacheManager.getStatus() == Status.STATUS_ALIVE : false;
  }

  protected void initialiseCache() throws CoreException {
    try {
      lock.acquire();
      cacheManager = createCacheManager();
      if (cacheManager.cacheExists(cacheName)) {
        cache = cacheManager.getEhcache(cacheName);
        log.debug("Reusing existing cache instance [{}]", cacheName);
        if (!(cache instanceof TimedExpirationCache)) {
          Ehcache wrappedCache = wrapCache(cache);
          wrappedCache.getCacheEventNotificationService().registerListener(eventListener);
          cacheManager.replaceCacheWithDecoratedCache(cache, wrappedCache);
        }
      }
      else {
        cache = createCache();
        log.debug("Creating new cache instance [{}]", cacheName);
        cache.getCacheEventNotificationService().registerListener(eventListener);
        cacheManager.addCache(cache);
      }
      cache = cacheManager.getEhcache(getCacheName());
    }
    catch (Exception e) {
      throw new CoreException(e);
    }
    finally {
      lock.release();
    }
  }

  protected TimedExpirationCache wrapCache(Ehcache cache) {
    TimedExpirationCache timedCache = new TimedExpirationCache(cache, cacheCleanupIntervalMs());
    return timedCache;
  }

  /**
   * @see com.adaptris.core.cache.Cache#close()
   */
  @Override
  public void close() {
    try {
      lock.acquire();
      if(shutdownCacheManagerOnClose())
        cacheManager.shutdown();
    }
    catch (InterruptedException e) {
      log.trace("Interrupted while attempting to shutdown the cache");
    }
    finally {
      lock.release();
    }
  }

  public void start() {
  }

  public void stop() {

  }

  @Override
  public void put(final String key, final Serializable value) throws CoreException {
    getCache().put(new Element(key, value));
  }

  @Override
  public void put(final String key, final Object value) throws CoreException {
    getCache().put(new Element(key, value));
  }

  @Override
  public Object get(final String key) throws CoreException {
    Element element = getCache().get(key);
    if (element != null) {
      return element.getObjectValue();
    }
    return null;
  }

  @Override
  public void remove(String key) throws CoreException {
    getCache().remove(key);
  }

  @Override
  public int size() throws CoreException {
    return getCache().getSize();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getKeys() throws CoreException {
    return getCache().getKeys();
  }

  /**
   * @see com.adaptris.core.cache.Cache#clear()
   */
  @Override
  public void clear() throws CoreException {
    getCache().removeAll();
  }

  /**
   * @return the {@link Ehcache} instance underlying this Cache
   */
  protected Ehcache getCache() throws CoreException {
    if (!cacheManagerAlive()) {
      initialiseCache();
    }
    return cache;
  }

  public String getCacheName() {
    return cacheName;
  }

  /**
   * Sets the name of the cache to be used by this Cache
   * 
   * @param cacheName
   */
  public void setCacheName(String cacheName) {
    this.cacheName = Args.notBlank(cacheName, "cacheName");
  }

  public TimeInterval getCacheCleanupInterval() {
    return cacheCleanupInterval;
  }

  /**
   * Sets an interval for the cache clean up task to run at.
   * <p>
   * This task is responsible for checking for expired items in the cache and should be configured to run as infrequently as
   * possible whilst still performing evictions in the time required. This will be a relatively heavy weight operation.
   * </p>
   * 
   * @param interval default is 60 seconds.
   */
  public void setCacheCleanupInterval(TimeInterval interval) {
    cacheCleanupInterval = interval;
  }

  public long cacheCleanupIntervalMs() {
    return getCacheCleanupInterval() != null ? getCacheCleanupInterval().toMilliseconds() : DEFAULT_CLEANUP.toMilliseconds();
  }
  
  public boolean shutdownCacheManagerOnClose() {
    return BooleanUtils.toBooleanDefaultIfNull(getShutdownCacheManagerOnClose(), true);
  }

  public Boolean getShutdownCacheManagerOnClose() {
    return shutdownCacheManagerOnClose;
  }

  /**
   * Specify whether or not the cache should be shutdown when the wrapping service changes component state to closed.
   * 
   * @since 3.0.2
   */
  public void setShutdownCacheManagerOnClose(Boolean b) {
    this.shutdownCacheManagerOnClose = b;
  }

  public EhcacheEventListener getEventListener() {
    return eventListener;
  }

  public void setEventListener(EhcacheEventListener eventListener) {
    this.eventListener = Args.notNull(eventListener, "eventListener");
  }

  public <T extends EhcacheCache> T withEventListener(EhcacheEventListener f) {
    setEventListener(f);
    return (T) this;
  }

  public <T extends EhcacheCache> T withShutdownCacheManagerOnClose(Boolean f) {
    setShutdownCacheManagerOnClose(f);
    return (T) this;
  }

  public <T extends EhcacheCache> T withCacheCleanupInterval(TimeInterval f) {
    setCacheCleanupInterval(f);
    return (T) this;
  }

  public <T extends EhcacheCache> T withCacheName(String f) {
    setCacheName(f);
    return (T) this;
  }
}
