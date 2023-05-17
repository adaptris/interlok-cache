package com.adaptris.core.cache.ehcache;

import java.util.HashSet;
import java.util.Set;

import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * Implementation of {@link com.adaptris.core.cache.CacheEventListener} that proxies the Ehcache's own cache event notifications
 *
 * @config ehcache-event-listener
 *
 * @author stuellidge
 *
 */
@XStreamAlias("ehcache-event-listener")
public class EhcacheEventListener implements CacheEventListener {

  private Set<com.adaptris.core.cache.CacheEventListener> listeners;

  public EhcacheEventListener() {
    setListeners(new HashSet<com.adaptris.core.cache.CacheEventListener>());
  }

  public void addListener(com.adaptris.core.cache.CacheEventListener listener) {
    listeners.add(listener);
  }

  public boolean removeListener(com.adaptris.core.cache.CacheEventListener listener) {
    return listeners.remove(listener);
  }

  @Override
  public void dispose() {
    listeners.clear();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @Override
  public void notifyElementEvicted(Ehcache cache, Element element) {
    for (com.adaptris.core.cache.CacheEventListener listener : listeners) {
      listener.itemEvicted(element.getObjectKey().toString(), element.getObjectValue());
    }
  }

  @Override
  public void notifyElementExpired(Ehcache cache, Element element) {
    for (com.adaptris.core.cache.CacheEventListener listener : listeners) {
      listener.itemExpired(element.getObjectKey().toString(), element.getObjectValue());
    }
  }

  @Override
  public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    for (com.adaptris.core.cache.CacheEventListener listener : listeners) {
      listener.itemPut(element.getObjectKey().toString(), element.getObjectValue());
    }
  }

  @Override
  public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    for (com.adaptris.core.cache.CacheEventListener listener : listeners) {
      listener.itemRemoved(element.getObjectKey().toString(), element.getObjectValue());
    }
  }

  @Override
  public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    for (com.adaptris.core.cache.CacheEventListener listener : listeners) {
      listener.itemUpdated(element.getObjectKey().toString(), element.getObjectValue());
    }
  }

  @Override
  public void notifyRemoveAll(Ehcache cache) {
    // We don't propagate this one
  }

  public Set<com.adaptris.core.cache.CacheEventListener> getListeners() {
    return listeners;
  }

  public void setListeners(Set<com.adaptris.core.cache.CacheEventListener> listeners) {
    this.listeners = Args.notNull(listeners, "listeners");
  }

  public void withListeners(com.adaptris.core.cache.CacheEventListener... listeners) {
    for (com.adaptris.core.cache.CacheEventListener l : listeners) {
      addListener(l);
    }
  }

}
