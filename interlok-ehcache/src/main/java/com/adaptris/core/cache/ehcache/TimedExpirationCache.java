package com.adaptris.core.cache.ehcache;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;

/**
 * {@link EhcacheDecoratorAdapter} that adds a cleanup thread to the underlying cache.
 * <p>
 * This thread periodically calls {@link Ehcache#getKeysWithExpiryCheck()} in order to force a check for any expired entries. If any
 * are found, they will be expired at that point and any event listeners will receive notifications.
 * </p>
 */
class TimedExpirationCache extends EhcacheDecoratorAdapter {

  private transient final Logger log = LoggerFactory.getLogger(DefaultEhcache.class);

  private transient Timer timer;

  public TimedExpirationCache(Ehcache underlyingCache) {
    this(underlyingCache, 30000);
  }

  public TimedExpirationCache(Ehcache underlyingCache, long maintenanceInterval) {
    super(underlyingCache);

    timer = new Timer(underlyingCache.getName() + ".cleaner", true);
    TimerTask cacheCleaner = new TimerTask() {
      @Override
      public void run() {
        // if (log.isTraceEnabled()) {
        // log.trace("Running cache maintenance thread");
        // }
        // This forces an expiration check
        if (getStatus() == Status.STATUS_ALIVE) {
          getKeysWithExpiryCheck();
        }
      }
    };

    log.debug("Scheduling maintenance task to execute every {}ms", maintenanceInterval);
    timer.schedule(cacheCleaner, maintenanceInterval, maintenanceInterval);
  }

  @Override
  public void initialise() {
    super.initialise();
  }

  @Override
  public void dispose() throws IllegalStateException {
    super.dispose();
    timer.cancel();
  }

}
