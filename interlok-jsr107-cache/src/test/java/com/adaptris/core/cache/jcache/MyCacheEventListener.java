package com.adaptris.core.cache.jcache;

import com.adaptris.core.cache.CacheEventListener;

public class MyCacheEventListener implements CacheEventListener {
  int evictedItems = 0;
  int expiredItems = 0;
  int putItems = 0;
  int removedItems = 0;
  int updatedItems = 0;
  private RuntimeException exception;

  public MyCacheEventListener(RuntimeException e) {
    exception = e;
  }
  
  @Override
  public void itemEvicted(String key, Object value) {
    System.err.println(hashCode() + " itemEvicted");
    evictedItems += update();
  }

  @Override
  public void itemExpired(String key, Object value) {
    System.err.println(hashCode() + " itemExpired");
    expiredItems += update();
  }

  @Override
  public void itemPut(String key, Object value) {
    System.err.println(hashCode() + " itemPut");
    putItems += update();
  }

  @Override
  public void itemRemoved(String key, Object value) {
    System.err.println(hashCode() + " itemRemoved");
    removedItems += update();
  }

  @Override
  public void itemUpdated(String key, Object value) {
    updatedItems += update();
  }

  private int update() {
    if (exception != null) {
      throw exception;
    }
    return 1;
  }

}
