package com.adaptris.core.services.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adaptris.core.BranchingServiceCollection;
import com.adaptris.core.Service;
import com.adaptris.core.services.LogMessageService;
import com.adaptris.core.services.cache.translators.MetadataCacheValueTranslator;

public class CheckAndRetrieveCacheTest extends CacheServiceBaseCase {
  @Override
  protected CheckAndRetrieve createServiceForExamples() {
    CheckAndRetrieve service = new CheckAndRetrieve();
    CacheEntryEvaluator eval1 = new CacheEntryEvaluator();
    CacheEntryEvaluator eval2 = new CacheEntryEvaluator();

    eval1.setKeyTranslator(new MetadataCacheValueTranslator("A_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval1.setValueTranslator(new MetadataCacheValueTranslator("WhereToStoreTheCache_Value"));
    eval2.setKeyTranslator(new MetadataCacheValueTranslator("Another_MetadataKey_Whose_Value_Makes_The_Cache_Key"));
    eval1.setValueTranslator(new MetadataCacheValueTranslator("WhereToStoreTheCache_Value_2"));

    service.setCacheEntryEvaluators(new ArrayList(Arrays.asList(new CacheEntryEvaluator[]
    {
        eval1, eval2
    })));
    service.setKeysFoundServiceId("AllKeysFoundInCache");
    service.setKeysNotFoundServiceId("Not_All_Keys_In_Cache");
    return service;
  }

  @Override
  protected String getExampleCommentHeader(Object object) {
    CheckAndRetrieve service = getService((BranchingServiceCollection) object);
    return super.getExampleCommentHeader(service);
  }

  @Override
  protected String createBaseFileName(Object object) {
    CheckAndRetrieve service = getService((BranchingServiceCollection) object);
    return super.createBaseFileName(service);
  }

  CheckAndRetrieve getService(BranchingServiceCollection coll) {
    CheckAndRetrieve baseService = null;
    for (Service s : coll.getServices()) {
      if (s.getUniqueId().equals(coll.getFirstServiceId())) {
        baseService = (CheckAndRetrieve) s;
        break;
      }
    }
    return baseService;
  }


  @Override
  protected List retrieveObjectsForSampleConfig() {
    List<BranchingServiceCollection> result = new ArrayList<BranchingServiceCollection>();
    for (CacheImps c : CacheImps.values()) {
      CacheServiceBase cacheService = createServiceForExamples();
      cacheService.setConnection(new CacheConnection(c.createCacheImplementation()));
      BranchingServiceCollection bsc = new BranchingServiceCollection();
      cacheService.setUniqueId("checkCache");
      bsc.setFirstServiceId(cacheService.getUniqueId());
      bsc.addService(cacheService);
      bsc.addService(new LogMessageService("AllKeysFoundInCache"));
      bsc.addService(new LogMessageService("Not_All_Keys_In_Cache"));
      result.add(bsc);
    }
    return result;
  }
}
