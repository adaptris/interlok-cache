package com.adaptris.core.cache.geode;

import org.apache.commons.lang3.StringUtils;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import com.adaptris.annotation.InputFieldDefault;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Uses {@link ClientCacheFactory#set(String, value)} with {@code cache-xml-file} to build the client cache.
 * 
 * @config geode-client-cache-from-file
 */
@XStreamAlias("geode-client-cache-from-file")
public class ClientCacheFromFile implements ClientCacheBuilder {
  @InputFieldDefault(value = "cache.xml")
  private String cacheFilename;

  @Override
  public ClientCache build(ClientCacheFactory factory) {
    return factory.set("cache-xml-file", cacheFilename()).create();
  }


  public String getCacheFilename() {
    return cacheFilename;
  }

  public void setCacheFilename(String cacheFileName) {
    this.cacheFilename = cacheFileName;
  }

  public ClientCacheFromFile withCacheFilename(String h) {
    setCacheFilename(h);
    return this;
  }

  private String cacheFilename() {
    return StringUtils.defaultIfBlank(getCacheFilename(), "cache.xml");
  }
}
