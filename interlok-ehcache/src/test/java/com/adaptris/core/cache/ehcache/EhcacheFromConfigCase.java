package com.adaptris.core.cache.ehcache;


public abstract class EhcacheFromConfigCase extends DefaultCacheTest {

  public static final String EHCACHE_XML_FILE = "cache.ehcache.xml";
  public static final String EHCACHE_XML_FILE_ALTERNATE = "cache.ehcache.xml.alternate";

  public EhcacheFromConfigCase(String name) {
    super(name);
  }

  @Override
  public void setUp() throws Exception {
  }

  @Override
  public void tearDown() {

  }

}
