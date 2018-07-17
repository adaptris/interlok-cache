package com.adaptris.core.cache.jcache;

import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;

/**
 * abstract {@link com.adaptris.core.cache.Cache} implementation that wraps a JSR107/JCache caching provider.
 * 
 */
public abstract class Jsr107Cache implements com.adaptris.core.cache.Cache {


  @InputFieldDefault(value = "false")
  @AdvancedConfig
  private Boolean shutdownCacheManagerOnClose;

  @NotBlank
  @AutoPopulated
  private String cacheName;

  @InputFieldDefault(value = "null")
  @AdvancedConfig
  private NewCacheConfiguration newCacheConfiguration;

  protected transient CacheManager manager;
  protected transient Cache cache;

  public Jsr107Cache() {
  }

  @Override
  public void init() throws CoreException {
    try {
      manager = getCacheManager();
      cache = createCache();
    } catch (Exception e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
  }

  @Override
  public void start() throws CoreException {
  }

  @Override
  public void stop() {
  }

  @Override
  public void close() {
    if (shutdownCacheManagerOnClose()) manager.close();
  }

  @Override
  public void put(String key, Serializable value) throws CoreException {
    cache.put(key, value);
  }

  @Override
  public void put(String key, Object value) throws CoreException {
    // the reference implementation doesn't like non-serializable things;
    // but we do actually want to support it in this instance.
    cache.put(key, value);
  }

  @Override
  public Object get(String key) throws CoreException {
    return cache.get(key);
  }

  @Override
  public void remove(String key) throws CoreException {
    cache.remove(key);
  }

  @Override
  public void clear() throws CoreException {
    cache.removeAll();
  }

  protected abstract CacheManager getCacheManager() throws CoreException;

  protected Cache createCache() {
    Cache cache = manager.getCache(Args.notBlank(getCacheName(), "cacheName"), String.class, Object.class);
    if (cache == null) {
      MutableConfiguration config = new MutableConfiguration<String, Object>().setTypes(String.class, Object.class);
      if (getNewCacheConfiguration() != null) {
        getNewCacheConfiguration().configure(config);
      }
      cache = manager.createCache(getCacheName(), config);
    }
    return cache;
  }

  public Boolean getShutdownCacheManagerOnClose() {
    return shutdownCacheManagerOnClose;
  }

  public void setShutdownCacheManagerOnClose(Boolean b) {
    this.shutdownCacheManagerOnClose = b;
  }

  private boolean shutdownCacheManagerOnClose() {
    return BooleanUtils.toBooleanDefaultIfNull(getShutdownCacheManagerOnClose(), false);
  }

  public String getCacheName() {
    return cacheName;
  }

  /**
   * Set the cache name.
   * <p>
   * Set the cache name, if it does not exist then {@link CacheManager#createCache(String, javax.cache.configuration.Configuration)}
   * will be used to create along with any configuration specified by {@link #getNewCacheConfiguration()}.
   * </p>
   * 
   * @param name
   */
  public void setCacheName(String name) {
    this.cacheName = Args.notBlank(name, "cacheName");
  }

  public NewCacheConfiguration getNewCacheConfiguration() {
    return newCacheConfiguration;
  }

  /**
   * Set any configuration that needs to be applied to be caches that are created via
   * {@link CacheManager#createCache(String, javax.cache.configuration.Configuration)}.
   * 
   * @param newConfig any new configuration; default is null.
   */
  public void setNewCacheConfiguration(NewCacheConfiguration newConfig) {
    this.newCacheConfiguration = newConfig;
  }

  public <T extends Jsr107Cache> T withNewCacheConfiguration(NewCacheConfiguration t) {
    setNewCacheConfiguration(t);
    return (T) this;
  }

  public <T extends Jsr107Cache> T withCacheName(String name) {
    setCacheName(name);
    return (T) this;
  }

  public <T extends Jsr107Cache> T withShutdownManagerOnClose(boolean b) {
    setShutdownCacheManagerOnClose(b);
    return (T) this;
  }



}
