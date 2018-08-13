package com.adaptris.core.cache.jcache;

import java.util.concurrent.TimeUnit;

import javax.cache.CacheManager;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.util.Args;
import com.adaptris.util.TimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Configuration that will be used when no cache is found by {@link CacheManager#getCache(String, Class, Class)}.
 * 
 * @config jsr107-new-cache
 */
@DisplayOrder(order =
{
    "expiration", "eventListener"
})
@XStreamAlias("jsr107-new-cache")
public class NewCacheConfiguration {
  private static TimeInterval DEFAULT_EXPIRY = new TimeInterval(60L, TimeUnit.SECONDS);
  @Valid
  @NotNull
  @AutoPopulated
  @AdvancedConfig
  private DefaultEventListener eventListener;
  @InputFieldDefault(value = "60 seconds")
  private TimeInterval expiration;

  public NewCacheConfiguration() {
    setEventListener(new DefaultEventListener());
  }

  private static Duration wrap(TimeInterval interval) {
    TimeInterval t = (interval != null) ? interval : DEFAULT_EXPIRY;
    return new Duration(t.getUnit(), t.getInterval());
  }

  public DefaultEventListener getEventListener() {
    return eventListener;
  }

  public void setEventListener(DefaultEventListener eventListener) {
    this.eventListener = Args.notNull(eventListener, "eventListener");
  }

  public TimeInterval getExpiration() {
    return expiration;
  }

  public void setExpiration(TimeInterval expiration) {
    this.expiration = expiration;
  }

  public NewCacheConfiguration withExpiration(TimeInterval t) {
    setExpiration(t);
    return this;
  }

  public NewCacheConfiguration withEventListener(DefaultEventListener e) {
    setEventListener(e);
    return this;
  }

  public MutableConfiguration<String, Object> configure(MutableConfiguration<String, Object> config) {
    config.addCacheEntryListenerConfiguration(getEventListener().configuration());
    final Duration duration = wrap(getExpiration());
    config.setExpiryPolicyFactory(new Factory<ExpiryPolicy>() {

      @Override
      public ExpiryPolicy create() {
        return new ExpiryPolicy() {
          @Override
          public Duration getExpiryForCreation() {
            return duration;
          }

          @Override
          public Duration getExpiryForAccess() {
            return duration;
          }

          @Override
          public Duration getExpiryForUpdate() {
            return duration;
          }
        };
      }

    });
    return config;
  }
}
