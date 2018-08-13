package com.adaptris.core.cache.jcache;

import java.util.HashSet;
import java.util.Set;

import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;

import com.adaptris.core.cache.CacheEventListener;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Implementation of {@link com.adaptris.core.cache.CacheEventListener} that proxies javax.cache event notifications.
 * 
 * @config default-jsr107-event-listener
 */
@XStreamAlias("default-jsr107-event-listener")
public class DefaultEventListener implements AllEventListener, Factory<CacheEntryListener<String, Object>> {

  private static final long serialVersionUID = 2018071701L;

  private enum CacheNotification {
    PUT() {
      @Override
      void notify(CacheEventListener l, CacheEntryEvent e) {
        l.itemPut(e.getKey().toString(),e.getValue());
      }
    },
    EXPIRED() {
      @Override
      void notify(CacheEventListener l, CacheEntryEvent e) {
        l.itemExpired(e.getKey().toString(),e.getValue());
      }
    },
    REMOVED() {
      @Override
      void notify(CacheEventListener l, CacheEntryEvent e) {
        l.itemRemoved(e.getKey().toString(),e.getValue());
      }
    },
    UPDATED() {
      @Override
      void notify(CacheEventListener l, CacheEntryEvent e) {
        l.itemUpdated(e.getKey().toString(),e.getValue());
      }
    };
    abstract void notify(CacheEventListener l, CacheEntryEvent e);
  }

  private Set<CacheEventListener> listeners;

  public DefaultEventListener() {
    setListeners(new HashSet<CacheEventListener>());
  }

  public void addListener(CacheEventListener listener) {
    listeners.add(listener);
  }

  public boolean removeListener(CacheEventListener listener) {
    return listeners.remove(listener);
  }


  public Set<CacheEventListener> getListeners() {
    return listeners;
  }

  public void setListeners(Set<CacheEventListener> listeners) {
    this.listeners = Args.notNull(listeners, "listeners");
  }

  public DefaultEventListener withListeners(CacheEventListener... listeners) {
    for (CacheEventListener l : listeners) {
      addListener(l);
    }
    return this;
  }

  @Override
  public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends Object>> events) throws CacheEntryListenerException {
    sendNotifications(CacheNotification.PUT, events);
  }

  @Override
  public void onExpired(Iterable<CacheEntryEvent<? extends String, ? extends Object>> events) throws CacheEntryListenerException {
    sendNotifications(CacheNotification.EXPIRED, events);
  }

  @Override
  public void onRemoved(Iterable<CacheEntryEvent<? extends String, ? extends Object>> events) throws CacheEntryListenerException {
    sendNotifications(CacheNotification.REMOVED, events);

  }

  @Override
  public void onUpdated(Iterable<CacheEntryEvent<? extends String, ? extends Object>> events) throws CacheEntryListenerException {
    sendNotifications(CacheNotification.UPDATED, events);
  }

  public MutableCacheEntryListenerConfiguration<String, Object> configuration() {
    return new MutableCacheEntryListenerConfiguration<String, Object>(this, null, true, true);
  }

  private void sendNotifications(final CacheNotification notifier,
                                 Iterable<CacheEntryEvent<? extends String, ? extends Object>> events)
      throws CacheEntryListenerException {
    try {
      events.forEach(e -> {
        listeners.forEach(l -> {
          notifier.notify(l, e);
        });
      });
    } catch (Exception e) {
      throw wrapAsListenerException(e);
    }
  }

  private static CacheEntryListenerException wrapAsListenerException(Throwable e) {
    if (e instanceof CacheEntryListenerException) {
      return (CacheEntryListenerException) e;
    }
    return new CacheEntryListenerException(e);
  }

  @Override
  public CacheEntryListener<String, Object> create() {
    return this;
  }
}
