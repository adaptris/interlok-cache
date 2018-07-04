package com.adaptris.core.services.cache;

import java.util.ArrayList;
import java.util.Arrays;

import com.adaptris.core.services.cache.translators.JmsReplyToCacheValueTranslator;
import com.adaptris.core.services.cache.translators.MetadataCacheValueTranslator;
import com.adaptris.core.services.cache.translators.StringPayloadCacheTranslator;

public class RetrieveFromCacheServiceTest extends CacheServiceBaseCase {

  protected RetrieveFromCacheService createService() {
    return new RemoveFromCacheService();
  }

  @Override
  protected RetrieveFromCacheService createServiceForExamples() {
    RetrieveFromCacheService service = createService();
    CacheEntryEvaluator eval1 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval2 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval3 = new CacheEntryEvaluator();

    eval1.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Is_The_Cache_key"));
    eval1.setValueTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Which_Will_Contain_What_We_Find_in_The_Cache"));

    eval2.setKeyTranslator(new MetadataCacheValueTranslator(
        "MetadataKey_Whose_Value_Is_The_Cache_Key_And_This_Key_Contains_A_Payload"));
    eval2.setValueTranslator(new StringPayloadCacheTranslator());

    eval3.setKeyTranslator(new MetadataCacheValueTranslator("JMSCorrelationID"));
    eval3.setValueTranslator(new JmsReplyToCacheValueTranslator());

    service.setCacheEntryEvaluators(new ArrayList(Arrays.asList(new CacheEntryEvaluator[]
    {
        eval1, eval2, eval3
    })));
    return service;
  }
}
