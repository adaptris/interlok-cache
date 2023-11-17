package com.adaptris.core.services.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.jms.Queue;

import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.cache.ehcache.DefaultEhcache;
import com.adaptris.core.jms.JmsConstants;
import com.adaptris.core.services.cache.translators.JmsReplyToCacheValueTranslator;
import com.adaptris.core.services.cache.translators.MetadataCacheValueTranslator;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.util.TimeInterval;

public class AddToCacheServiceTest {
  private static final String QUEUE_NAME = "TempReplyQueue";
  private static final String CORRELATION_ID = "12345ABCDE";

  @Test
  public void testDoService_CacheManagerShutdown() throws Exception {
    AdaptrisMessage msg1 = createMessage("Hello World",
        Arrays.asList(new MetadataElement(JmsConstants.JMS_CORRELATION_ID, CORRELATION_ID)));
    AdaptrisMessage msg2 = createMessage("Hello World", Arrays.asList(new MetadataElement(JmsConstants.JMS_CORRELATION_ID, "ABCDEFG")));
    DefaultEhcache cache1 = createCacheInstanceForTests();
    DefaultEhcache cache2 = createCacheInstanceForTests();
    cache2.setCacheName(cache1.getCacheName());
    AddToCacheService service1 = createServiceForTests();
    AddToCacheService service2 = createServiceForTests();
    try {
      service1.setConnection(new CacheConnection(cache1));
      service2.setConnection(new CacheConnection(cache2));

      service1.setEnforceSerializable(false);
      service2.setEnforceSerializable(false);
      LifecycleHelper.initAndStart(service1);
      LifecycleHelper.initAndStart(service2);
      service1.doService(msg1);
      LifecycleHelper.stopAndClose(service1);
      // At this point the "singleton" ehcache Manager is shutdown; service2 doService should be fine.
      service2.doService(msg2);
      Object value = cache2.get("ABCDEFG");
      assertTrue(value instanceof Queue, "Cached object should be a JMS Queue");
      assertEquals(QUEUE_NAME, ((Queue) value).getQueueName());
    } finally {
      LifecycleHelper.stopAndClose(service1);
      LifecycleHelper.stopAndClose(service2);
    }
  }

  private AddToCacheService createServiceForTests() {
    AddToCacheService service = new AddToCacheService();
    CacheEntryEvaluator eval = new CacheEntryEvaluator();

    eval.setKeyTranslator(new MetadataCacheValueTranslator("JMSCorrelationID"));
    eval.setValueTranslator(new JmsReplyToCacheValueTranslator());
    service.addCacheEntryEvaluator(eval);

    return service;
  }

  protected AdaptrisMessage createMessage(String payload, Collection<MetadataElement> metadata) {
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage(payload);
    for (MetadataElement element : metadata) {
      msg.addMetadata(element);
    }
    msg.getObjectHeaders().put(JmsConstants.OBJ_JMS_REPLY_TO_KEY, (Queue) () -> QUEUE_NAME);
    return msg;
  }

  protected DefaultEhcache createCacheInstanceForTests() {
    DefaultEhcache cache = new DefaultEhcache();
    cache.setCacheCleanupInterval(new TimeInterval(5L, TimeUnit.SECONDS));
    cache.setCacheName(UUID.randomUUID().toString());
    cache.setEvictionPolicy(DefaultEhcache.MemoryStoreEvictionPolicy.LRU);
    cache.setMaxElementsInMemory(10);
    cache.setTimeToIdle(new TimeInterval(5L, TimeUnit.SECONDS));
    cache.setTimeToLive(new TimeInterval(3L, TimeUnit.SECONDS));
    return cache;
  }
}
