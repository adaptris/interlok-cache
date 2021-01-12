# Interlok Cache

[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-cache.svg)](https://github.com/adaptris/interlok-cache/tags) ![license](https://img.shields.io/github/license/adaptris/interlok-cache.svg) [![codecov](https://codecov.io/gh/adaptris/interlok-cache/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-cache) [![Total alerts](https://img.shields.io/lgtm/alerts/g/adaptris/interlok-cache.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/adaptris/interlok-cache/alerts/) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/adaptris/interlok-cache.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/adaptris/interlok-cache/context:java)

## Apache Geode Cache

You can configure this component via the following options:

* Cache file, region name and region type
* Host, port, region name and region type

### Configuration parameters

* regionName `String` Name of the region where items will be cached
* hostname `String` Geode server hostname
* port `Integer` Geode server port
* clientRegionShortcut `String` Region type as defined in `org.apache.geode.cache.client.ClientRegionShortcut`
* cacheFileName `String` Simple file name of the geode config cache file which resides on the classpath
* isDurable `Boolean` Maintain your durable queues after the client cache is closed

## Ehcache

This uses the `CacheManager#create()` to create the cache manager which
will end up using the default XML configuration (ehcache.xml or
ehcache-failsafe.xml). In the event that the cache manager does not
contain a cache that matches the name  `#getCacheName()`, then a new
basic one will be implemented based on the additional parameters that
are configured. If the cache already exists, then it is used as is,
without any additional configuration.

Take care if you are using ehcache in multicast distributed mode.
Calling close on any ehcache service will by default close the
cacheManager, which can cause problems across the distributed adapters.
In these cases the distributed adapters can no longer perform operations
on any of the caches related to this cache manager.

If you are using ehcache in distributed mode you should consider setting
this classes shutdown-cache-manager-on-close to false, which will not
call CacheManager.shutdown(). Instead, you can set the jvm property:
`-Dnet.sf.ehcache.enableShutdownHook=true`

## Jsr107Cache

JSR107 (JCACHE) is a Java API for caching.
