package com.adaptris.core.cache.ehcache;

import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.validator.constraints.NotBlank;

import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

/**
 * Implementation of {@link com.adaptris.core.cache.Cache} based on Ehcache.
 * <p>
 * This uses the {@link CacheManager#create(URL)} to create the cache manager which allows you to specify a specific configuration
 * URL. In the event that the cache manager does not contain a cache that matches the name {@link #getCacheName()}, then a new one
 * will be implemented based on the additional parameters that are configured. If the cache already exists, then it is used as is,
 * without any additional configuration.
 * </p>
 * 
 * @config ehcache-from-url
 * 
 */
@XStreamAlias("ehcache-from-url")
@DisplayOrder(order ={"configurationUrl", "cacheName", "evictionPolicy", "maxElementsInMemory"})
public class EhcacheFromUrl extends DefaultEhcache {
  @NotBlank
  private String configurationUrl;

  public EhcacheFromUrl() {
    super();
  }

  @Override
  protected CacheManager createCacheManager() throws CacheException, MalformedURLException {
    return CacheManager.newInstance(new URL(getConfigurationUrl()));
  }

  public String getConfigurationUrl() {
    return configurationUrl;
  }

  /**
   * Set the url that will form the basis of configuration.
   * 
   * @see CacheManager#create(URL)
   * @param url the URL
   */
  public void setConfigurationUrl(String url) {
    configurationUrl = Args.notBlank(url, "configurationUrl");
  }

  public EhcacheFromUrl withConfigurationUrl(String f) {
    setConfigurationUrl(f);
    return this;
  }
}
