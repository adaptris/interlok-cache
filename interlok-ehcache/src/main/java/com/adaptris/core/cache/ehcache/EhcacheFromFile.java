package com.adaptris.core.cache.ehcache;

import java.net.MalformedURLException;

import javax.validation.constraints.NotBlank;

import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

/**
 * Implementation of {@link com.adaptris.core.cache.Cache} based on Ehcache.
 * <p>
 * This uses the {@link CacheManager#create(String)} to create the cache manager which allows you to specify a specific ehcache.xsd
 * compliant filename as the configuration file. In the event that the cache manager does not contain a cache that matches the name
 * {@link #getCacheName()}, then a new one will be implemented based on the additional parameters that are configured. If the cache already
 * exists, then it is used as is, without any additional configuration.
 * </p>
 *
 * @config ehcache-from-file
 *
 */
@XStreamAlias("ehcache-from-file")
@DisplayOrder(order = { "xmlConfigurationFilename", "cacheName", "evictionPolicy", "maxElementsInMemory" })
public class EhcacheFromFile extends DefaultEhcache {
  @NotBlank
  private String xmlConfigurationFilename;

  public EhcacheFromFile() {
    super();
  }

  @Override
  protected CacheManager createCacheManager() throws CacheException, MalformedURLException {
    return CacheManager.newInstance(getXmlConfigurationFilename());
  }

  public String getXmlConfigurationFilename() {
    return xmlConfigurationFilename;
  }

  /**
   * Set the xml file that will form the basis of configuration.
   *
   * @see CacheManager#create(String)
   * @param filename
   *          the filename
   */
  public void setXmlConfigurationFilename(String filename) {
    xmlConfigurationFilename = Args.notBlank(filename, "xmlConfigurationFilename");
  }

  public EhcacheFromFile withXmlConfigurationFile(String f) {
    setXmlConfigurationFilename(f);
    return this;
  }
}
