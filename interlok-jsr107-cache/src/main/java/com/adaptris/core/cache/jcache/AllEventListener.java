package com.adaptris.core.cache.jcache;

import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

public interface AllEventListener extends CacheEntryCreatedListener<String, Object>, CacheEntryExpiredListener<String, Object>,
    CacheEntryRemovedListener<String, Object>, CacheEntryUpdatedListener<String, Object> {

}
