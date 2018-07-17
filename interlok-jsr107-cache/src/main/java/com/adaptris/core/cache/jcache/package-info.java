/**
 * <p>
 * {@link com.adaptris.core.cache.Cache} implementations that wrap any JSR107 caching provider.
 * </p>
 * <p>
 * This package allows you to use any JSR107 compliant cache provider with the {@code com.adaptris.core.service.cache} services. If
 * there is only a single cache provider registered (and pre-configured to your use-cases), then you can use
 * {@link com.adaptris.core.cache.jcache.BasicJsr107Cache}; if you have multiple cache providers available, then you should use
 * {@link com.adaptris.core.cache.jcache.ConfiguredJsr107Cache} and specify the fully qualified cache provider. The cache provider
 * classname varies from provider to provider, if you were going to use the reference implementation (not-recommended) then you
 * would use {@code org.jsr107.ri.spi.RICachingProvider}. Some other providers might be
 * <ul>
 * <li>Redis (via Redisson {@code org.redisson:redisson:x.y.z}) : org.redisson.jcache.JCachingProvider</li>
 * <li>Apache Ignite ({@code org.apache.ignite:ignite-core:x.y.z}) : org.apache.ignite.cache.CachingProvider</li>
 * <li>ehcache3 ({@code org.ehcache:ehcache:3.x.y}) : org.ehcache.jsr107.EhcacheCachingProvider</li>
 * <li>hazelcast ({@code com.hazelcast:hazelcast:x.y.z}) : com.hazelcast.cache.HazelcastCachingProvider</li>
 * </p>
 *
 * <img alt="UML" src="package.svg"/>
 */
package com.adaptris.core.cache.jcache;