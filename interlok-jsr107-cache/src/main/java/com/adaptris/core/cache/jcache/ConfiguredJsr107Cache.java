package com.adaptris.core.cache.jcache;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.apache.commons.lang3.StringUtils;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldHint;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.ExceptionHelper;
import com.adaptris.util.URLString;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link com.adaptris.core.cache.Cache} implementation that wraps a JSR107/JCache caching provider.
 * 
 * @config configured-jsr107-cache
 *
 */
@XStreamAlias("configured-jsr107-cache")
@DisplayOrder(order =
{
    "cacheName", "configurationUrl", "cacheProviderClassname", "shutdownCacheManagerOnClose"
})
public class ConfiguredJsr107Cache extends Jsr107Cache {

  @AdvancedConfig
  @InputFieldHint(style = "BLANKABLE")
  private String cacheProviderClassname;
  @AdvancedConfig
  @InputFieldHint(style = "BLANKABLE")
  private String configurationUrl;

  public ConfiguredJsr107Cache() {

  }


  protected CacheManager getCacheManager() throws CoreException {
    CachingProvider provider = null;
    CacheManager mgr = null;
    try {
      if (!StringUtils.isEmpty(getCacheProviderClassname())) {
        provider = Caching.getCachingProvider(getCacheProviderClassname());
      } else {
        provider = Caching.getCachingProvider();
      }
      if (!StringUtils.isEmpty(getConfigurationUrl())) {
        mgr = provider.getCacheManager(new URLString(getConfigurationUrl()).getURL().toURI(), null);
      } else {
        mgr = provider.getCacheManager();
      }
    } catch (MalformedURLException | URISyntaxException e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return mgr;
  }

  public String getCacheProviderClassname() {
    return cacheProviderClassname;
  }

  /**
   * Set the cache provider classname.
   * <p>
   * The cache provider classname varies from provider to provider, if you were going to use the reference implementation
   * (not-recommended) then you would use {@code org.jsr107.ri.spi.RICachingProvider}. Some other providers might be
   * <ul>
   * <li>Redis (via Redisson {@code org.redisson:redisson:3.7.4} : org.redisson.jcache.JCachingProvider</li>
   * <li>Apache Ignite {@code org.apache.ignite:ignite-core:2.5.0} : org.apache.ignite.cache.CachingProvider</li>
   * </ul>
   * </p>
   * 
   * @param classname the full qualified class name; if not specified then {@link Caching#getCachingProvider()} will be used, which
   *          could lead to unexpected results if multiple caching providers are available.
   */
  public void setCacheProviderClassname(String classname) {
    this.cacheProviderClassname = classname;
  }


  public String getConfigurationUrl() {
    return configurationUrl;
  }

  /**
   * Specify the required configuration URL.
   * 
   * @param url the url, if blank/null then the caching provider default will be used.
   */
  public void setConfigurationUrl(String url) {
    this.configurationUrl = url;
  }

  public ConfiguredJsr107Cache withConfigurationUrl(String url) {
    setConfigurationUrl(url);
    return this;
  }

  public ConfiguredJsr107Cache withProviderClassname(String clazz) {
    setCacheProviderClassname(clazz);
    return this;
  }
}
