package com.adaptris.core.cache.jcache;

import com.adaptris.util.GuidGenerator;

public class BasicJsr107CacheTest extends Jsr107CacheCase {

  @Override
  protected Jsr107Cache createCacheInstance(boolean uniqueCacheName) {
    BasicJsr107Cache cache = new BasicJsr107Cache()
        .withCacheName(uniqueCacheName ? new GuidGenerator().safeUUID() : BasicJsr107Cache.class.getSimpleName());
    return cache;
  }

}
