package com.adaptris.core.cache.geode;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.cache.Cache;
import com.adaptris.interlok.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * Interlok Apache Geode Cache Implementation.<br/>
 * You can configure this component via the following options:
 * </p>
 * <ul>
 * <li>Cache file, region name and region type</li>
 * <li>Host, port, region name and region type</li>
 * </ul>
 * <p>
 * Configuration parameters:
 * </p>
 * <ul>
 * <li>regionName - Name of the region where items will be cached</li>
 * <li>hostname - geode server hostname</li>
 * <li>port - integer, geode server port</li>
 * <li>clientRegionShortcut - String value region type as defined: {@link org.apache.geode.cache.client.ClientRegionShortcut}</li>
 * <li>cacheFileName - String - simple file name of the geode config cache file which resides on the classpath</li>
 * <li>isDurable - boolean - maintain your durable queues after the client cache is closed</li>
 * </ul>
 * <P/>
 */
@XStreamAlias("geode-cache")
@AdapterComponent
@DisplayOrder(order = {"cacheFileName", "regionName", "clientRegionShortcut", "hostname", "port", "isDurable"})
public class GeodeCache implements Cache {

  @NotBlank
  @AutoPopulated
  private String regionName;
  @InputFieldDefault(value = "LOCAL")
  private ClientRegionShortcut clientRegionShortcut = ClientRegionShortcut.LOCAL;
  @Valid
  @NotNull
  private ClientCacheBuilder cacheBuilder;

  // maintain your durable queues while the client cache is closed
  @InputFieldDefault(value = "false")
  private Boolean durable;

  private transient ClientCache geodeCache;
  private transient Region<String, Object> geodeRegion;


  public GeodeCache() {
    setCacheBuilder(new ClientCacheFromFile());
  }

  @Override
  public void init() throws CoreException {
    initialiseCache();
  }

  protected void initialiseCache() throws ServiceException {
    initialiseCache(new ClientCacheFactory());
  }

  protected void initialiseCache(ClientCacheFactory clientCacheFactory) throws ServiceException {
    if (StringUtils.isBlank(getRegionName())) {
      throw new ServiceException("GeodeCache must have RegionName specified");
    }
    this.geodeCache = getCacheBuilder().build(clientCacheFactory);
    this.geodeRegion = geodeCache.<String, Object>createClientRegionFactory(clientRegionShortcut()).create(getRegionName());
  }

  @Override
  public void close() {
    closeQuietly(geodeCache, durable());
  }

  // ------------------------------------------------------------------------
  // Overridden methods
  // ------------------------------------------------------------------------
  @Override
  public void put(final String key, final Serializable value) throws CoreException {
    getCache().put(key, value);
  }

  @Override
  public void put(final String key, final Object value) throws CoreException {
    getCache().put(key, value);
  }

  @Override
  public Object get(final String key) throws CoreException {
    return getCache().get(key);
  }

  @Override
  public void remove(String key) throws CoreException {
    getCache().remove(key);
  }

  @Override
  public int size() throws CoreException {
    return getCache().size();
  }

  @Override
  public void clear() throws CoreException {
    getCache().clear();
  }

  // ------------------------------------------------------------------------
  // Getter and Setter
  // ------------------------------------------------------------------------
  protected Region<String, Object> getCache() {
    return this.geodeRegion;
  }

  public String getRegionName() {
    return regionName;
  }

  public void setRegionName(String regionName) {
    this.regionName = Args.notBlank(regionName, "regionName");
  }

  public ClientRegionShortcut getClientRegionShortcut() {
    return clientRegionShortcut;
  }

  public void setClientRegionShortcut(ClientRegionShortcut clientRegionShortcut) {
    this.clientRegionShortcut = clientRegionShortcut;
  }

  public ClientRegionShortcut clientRegionShortcut() {
    return ObjectUtils.defaultIfNull(getClientRegionShortcut(), ClientRegionShortcut.LOCAL);
  }

  public Boolean getDurable() {
    return this.durable;
  }

  public void setDurable(Boolean durable) {
    this.durable = durable;
  }

  private boolean durable() {
    return BooleanUtils.toBooleanDefaultIfNull(getDurable(), false);
  }

  public ClientCacheBuilder getCacheBuilder() {
    return cacheBuilder;
  }
  
  public void setCacheBuilder(ClientCacheBuilder cacheBuilder) {
    this.cacheBuilder = Args.notNull(cacheBuilder, "cache-builder");
  }
  

  private static void closeQuietly(ClientCache cache, boolean keepAlive) {
    try {
      if (cache != null) {
        cache.close(keepAlive);
      }
    } catch (Exception cce) {
    }
  }
}
