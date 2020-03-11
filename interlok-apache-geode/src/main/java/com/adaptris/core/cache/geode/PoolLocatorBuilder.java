package com.adaptris.core.cache.geode;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.util.NumberUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Uses {@link ClientCacheFactory#addPoolLocator(String, int)} to build a client cache.
 * 
 * @config "geode-client-cache-from-pool-locator"
 */
@XStreamAlias("geode-client-cache-from-pool-locator")
public class PoolLocatorBuilder implements ClientCacheBuilder {
  private String hostname;
  @InputFieldDefault(value = "10334")
  private Integer port;

  @Override
  public ClientCache build(ClientCacheFactory factory) {
    return factory.addPoolLocator(getHostname(), port()).create();
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public PoolLocatorBuilder withHostname(String h) {
    setHostname(h);
    return this;
  }

  public PoolLocatorBuilder withPort(Integer i) {
    setPort(i);
    return this;
  }

  private int port() {
    return NumberUtils.toIntDefaultIfNull(getPort(), 10334);
  }
}
