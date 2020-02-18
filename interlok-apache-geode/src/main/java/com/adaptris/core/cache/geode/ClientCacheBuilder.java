package com.adaptris.core.cache.geode;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public interface ClientCacheBuilder {

  ClientCache build(ClientCacheFactory factory);
  
}
