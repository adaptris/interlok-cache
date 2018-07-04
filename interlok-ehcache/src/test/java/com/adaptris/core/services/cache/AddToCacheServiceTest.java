package com.adaptris.core.services.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.jms.JMSException;
import javax.jms.Queue;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.cache.ehcache.DefaultEhcache;
import com.adaptris.core.jms.JmsConstants;
import com.adaptris.core.services.cache.translators.JmsReplyToCacheValueTranslator;
import com.adaptris.core.services.cache.translators.MetadataCacheValueTranslator;
import com.adaptris.core.services.cache.translators.ObjectMetadataCacheValueTranslator;
import com.adaptris.core.services.cache.translators.StringPayloadCacheTranslator;
import com.adaptris.core.services.cache.translators.XpathCacheValueTranslator;

public class AddToCacheServiceTest extends CacheServiceBaseCase {
  private static final String QUEUE_NAME = "TempReplyQueue";
  private static final String CORRELATION_ID = "12345ABCDE";

  private static final String SRC_KEY = "srcKey";
  private static final String SRC_VALUE = "srcValue";


  public void testDoService_CacheManagerShutdown() throws Exception {
    AdaptrisMessage msg1 = createMessage("Hello World", Arrays.asList(new MetadataElement[]
    {
      new MetadataElement(JmsConstants.JMS_CORRELATION_ID, CORRELATION_ID)
    }));
    AdaptrisMessage msg2 = createMessage("Hello World", Arrays.asList(new MetadataElement[]
    {
      new MetadataElement(JmsConstants.JMS_CORRELATION_ID, "ABCDEFG")
    }));
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
      start(service1);
      start(service2);
      service1.doService(msg1);
      stop(service1);
      // At this point the "singleton" ehcache Manager is shutdown; service2 doService should be fine.
      service2.doService(msg2);
      Object value = cache2.get("ABCDEFG");
      assertTrue("Cached object should be a JMS Queue", value instanceof Queue);
      assertEquals(QUEUE_NAME, ((Queue) value).getQueueName());
    }
    finally {
      stop(service1);
      stop(service2);
    }
  }

  protected AddToCacheService createService() {
    return new AddToCacheService();
  }

  private AddToCacheService createServiceForTests() {
    AddToCacheService service = createService();
    CacheEntryEvaluator eval = new CacheEntryEvaluator();

    eval.setKeyTranslator(new MetadataCacheValueTranslator("JMSCorrelationID"));
    eval.setValueTranslator(new JmsReplyToCacheValueTranslator());
    service.addCacheEntryEvaluator(eval);

    return service;
  }

  @Override
  protected AddToCacheService createServiceForExamples() {
    AddToCacheService service = new AddToCacheService();
    CacheEntryEvaluator eval1 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval2 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval3 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval4 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval5 = new CacheEntryEvaluator();

    eval1.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval1.setValueTranslator(new MetadataCacheValueTranslator("Another_MetadataKey_Whose_Value_Makes_The_Cache_CacheValue"));

    eval2.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval2.setValueTranslator(new StringPayloadCacheTranslator());

    eval3.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval3.setValueTranslator(new XpathCacheValueTranslator("/some/xpath/value"));

    eval4.setKeyTranslator(new MetadataCacheValueTranslator("JMSCorrelationID"));
    eval4.setValueTranslator(new JmsReplyToCacheValueTranslator());

    eval5.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval5.setValueTranslator(new ObjectMetadataCacheValueTranslator(JmsConstants.OBJ_JMS_REPLY_TO_KEY));

    service.setCacheEntryEvaluators(new ArrayList(Arrays.asList(new CacheEntryEvaluator[]
    {
        eval1, eval2, eval3, eval4, eval5
    })));

    return service;
  }

  @Override
  protected AdaptrisMessage createMessage(String payload, Collection<MetadataElement> metadata) {
    AdaptrisMessage msg = super.createMessage(payload, metadata);
    msg.getObjectHeaders().put(JmsConstants.OBJ_JMS_REPLY_TO_KEY, new Queue() {
      @Override
      public String getQueueName() throws JMSException {
        return QUEUE_NAME;
      }
    });
    return msg;
  }

}
