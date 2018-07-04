package com.adaptris.core.services.cache;

public class RemoveFromCacheServiceTest extends RetrieveFromCacheServiceTest {

  @Override
  protected RemoveFromCacheService createService() {
    return new RemoveFromCacheService();
  }
}
