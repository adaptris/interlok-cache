package com.adaptris.core.cache.jcache;

import javax.cache.CacheManager;
import javax.cache.Caching;

import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.CoreException;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link com.adaptris.core.cache.Cache} implementation that uses JSR107 defaults.
 * <p>
 * Note that if you have multiple JSR107 providers available, then this might have unexpected behaviours; generally you should use
 * {@link ConfiguredJsr107Cache} instead.
 * </p>
 *
 * @config basic-jsr107-cache
 *
 */
@XStreamAlias("basic-jsr107-cache")
@DisplayOrder(order = { "cacheName", "shutdownCacheManagerOnClose" })
public class BasicJsr107Cache extends Jsr107Cache {

  @Override
  protected CacheManager getCacheManager() throws CoreException {
    return Caching.getCachingProvider().getCacheManager();
  }

}
