package com.adaptris.core.cache.ehcache;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.util.TimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;

/**
 * Implementation of {@link com.adaptris.core.cache.Cache} based on Ehcache.
 * <p>
 * By default this uses the {@link CacheManager#create()} to create the cache manager which will end up using the default XML
 * configuration (ehcache.xml or ehcache-failsafe.xml). In the event that the cache manager does not contain a cache that matches
 * the name {@link #getCacheName()}, then a new basic one will be implemented based on the additional parameters that are
 * configured. If the cache already exists, then it is used as is, without any additional configuration.
 * </p>
 * 
 * @config default-ehcache
 */
@XStreamAlias("default-ehcache")
@DisplayOrder(order ={"cacheName", "evictionPolicy", "maxElementsInMemory"})
@SuppressWarnings("unchecked")
public class DefaultEhcache extends EhcacheCache {

  private Integer maxElementsInMemory;
  private TimeInterval timeToLive;
  private TimeInterval timeToIdle;
  @NotNull
  @AutoPopulated
  private MemoryStoreEvictionPolicy evictionPolicy;

  private static final TimeInterval DEFAULT_TTL = new TimeInterval(-1L, TimeUnit.SECONDS);
  private static final TimeInterval DEFAULT_TTI = new TimeInterval(-1L, TimeUnit.SECONDS);

  /**
   * This enum describes the 3 supported cache eviction policies:
   * <ul>
   * <li>LRU - Least Recently Used</li>
   * <li>LFT - Least Frequently Used</li>
   * <li>FIFO - First In, First Out</li>
   * </ul>
   *
   */
  public enum MemoryStoreEvictionPolicy {
    LRU, LFU, FIFO
  }

  public DefaultEhcache() {
    super();
    setEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
  }

  @Override
  protected CacheManager createCacheManager() throws CacheException, MalformedURLException {
    return CacheManager.create();
  }

  @Override
  protected Ehcache createCache() {
    CacheConfiguration config = new CacheConfiguration(getCacheName(), maxElementsInMemory());
    config.setMemoryStoreEvictionPolicy(evictionPolicy.name());
    config.setEternal(false);
    if (timeToIdleSeconds() > -1) {
      config.setTimeToIdleSeconds(timeToIdleSeconds());
    }
    if (timeToLiveSeconds() > -1) {
      config.setTimeToLiveSeconds(timeToLiveSeconds());
    }
    net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(config);
    TimedExpirationCache timedCache = wrapCache(cache);
    return timedCache;
  }

  public Integer getMaxElementsInMemory() {
    return maxElementsInMemory;
  }

  int maxElementsInMemory() {
    return getMaxElementsInMemory() != null ? getMaxElementsInMemory().intValue() : 0;
  }

  /**
   * Sets a maximum number of elements to be held in memory. For a memory only cache, this is the maximum number of elements in
   * total
   * 
   * @param maxElementsInMemory default is 0 (unlimited).
   */
  public void setMaxElementsInMemory(Integer maxElementsInMemory) {
    this.maxElementsInMemory = maxElementsInMemory;
  }

  public MemoryStoreEvictionPolicy getEvictionPolicy() {
    return evictionPolicy;
  }

  /**
   * Sets the {@link MemoryStoreEvictionPolicy} to determine how items are evicted from the cache
   *
   * @param evictionPolicy
   */
  public void setEvictionPolicy(MemoryStoreEvictionPolicy evictionPolicy) {
    this.evictionPolicy = evictionPolicy;
  }

  public TimeInterval getTimeToLive() {
    return timeToLive;
  }

  /**
   * Sets the time to live for each item put into the cache. They will be evicted at some stage after this time has expired.
   *
   * @param interval the time
   */
  public void setTimeToLive(TimeInterval interval) {
    timeToLive = interval;
  }

  long timeToLiveSeconds() {
    long msValue = getTimeToLive() != null ? getTimeToLive().toMilliseconds() : DEFAULT_TTL.toMilliseconds();
    return TimeUnit.MILLISECONDS.toSeconds(msValue);
  }

  public TimeInterval getTimeToIdle() {
    return timeToIdle;
  }

  /**
   * Sets the time before an object in the cache expires if it is not accessed
   *
   * @param interval the time
   */
  public void setTimeToIdle(TimeInterval interval) {
    timeToIdle = interval;
  }

  long timeToIdleSeconds() {
    long msValue = getTimeToIdle() != null ? getTimeToIdle().toMilliseconds() : DEFAULT_TTI.toMilliseconds();
    return TimeUnit.MILLISECONDS.toSeconds(msValue);
  }

  public <T extends DefaultEhcache> T withEvictionPolicy(MemoryStoreEvictionPolicy f) {
    setEvictionPolicy(f);
    return (T) this;
  }

  public <T extends DefaultEhcache> T withMaxElementsInMemory(Integer f) {
    setMaxElementsInMemory(f);
    return (T) this;
  }

  public <T extends DefaultEhcache> T withTimeToIdle(TimeInterval f) {
    setTimeToIdle(f);
    return (T) this;
  }

  public <T extends DefaultEhcache> T withTimeToLive(TimeInterval f) {
    setTimeToLive(f);
    return (T) this;
  }
}
