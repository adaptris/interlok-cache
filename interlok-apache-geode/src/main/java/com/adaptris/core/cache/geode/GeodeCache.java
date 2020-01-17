package com.adaptris.core.cache.geode;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.cache.Cache;
import com.adaptris.interlok.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>Interlok Apache Geode Cache Implementation.<br/>
 * You can configure this component via the following options:</p>
 * <ul>
 *     <li>Cache file, region name and region type</li>
 *     <li>Host, port, region name and region type</li>
 * </ul>
 * <p>Configuration parameters:</p>
 * <ul>
 *     <li>regionName - Name of the region where items will be cached</li>
 *     <li>hostname - geode server hostname</li>
 *     <li>port - integer,  geode server port</li>
 *     <li>clientRegionShortcut - String value region type as defined: {@link org.apache.geode.cache.client.ClientRegionShortcut}</li>
 *     <li>cacheFileName - String - simple file name of the geode config cache file which resides on the classpath</li>
 *     <li>isDurable - boolean - maintain your durable queues after the client cache is closed</li>
 * </ul>
 * <P/>
 */
@XStreamAlias("geode-cache")
@AdapterComponent
@DisplayOrder(order = {"cacheFileName", "regionName", "clientRegionShortcut", "hostname", "port", "isDurable"})
public class GeodeCache implements Cache {

    protected transient Logger log = LoggerFactory.getLogger(this.getClass());

    @NotBlank
    @AutoPopulated
    private String regionName;
    private String hostname;
    @InputFieldDefault(value = "10334")
    private int port;
    @InputFieldDefault(value = "LOCAL")
    private String clientRegionShortcut;
    @InputFieldDefault(value = "cache.xml")
    private String cacheFileName;

    // maintain your durable queues while the client cache is closed
    @InputFieldDefault(value = "false")
    private Boolean durable = false;

    private ClientCache geodeCache;
    private Region<String, Object> geodeRegion;


    @Override
    public void init() throws CoreException {
        initialiseCache();
    }

    protected void initialiseCache() throws ServiceException {
        initialiseCache(new ClientCacheFactory());
    }

    protected void initialiseCache(ClientCacheFactory clientCacheFactory) throws ServiceException {
        if (StringUtils.isBlank(getClientRegionShortcut()) || StringUtils.isBlank(getRegionName())) {
            throw new ServiceException("GeodeCache must have clientRegion and RegionName specified");
        }
        // Configure with cache file if supplied
        if (!StringUtils.isBlank(getCacheFileName())) {
            log.debug("Attempting to configure GeodeCache from supplied cacheFileName");
            this.geodeCache = clientCacheFactory.set("cache-xml-file", getCacheFileName()).create();
            this.geodeRegion = geodeCache.<String, Object>createClientRegionFactory(clientRegionShortcut()).create(getRegionName());
        }
        else {
            this.geodeCache = clientCacheFactory.addPoolLocator(getHostname(), getPort()).create();
            this.geodeRegion = geodeCache.<String, Object>createClientRegionFactory(clientRegionShortcut()).create(getRegionName());
        }
    }

    @Override
    public void close() {
        try {
            geodeCache.close(isDurable());
        } catch (CacheClosedException cce) {
            log.warn("Cache is already closed");
        }
    }

    // ------------------------------------------------------------------------
    //           Overridden methods
    // ------------------------------------------------------------------------
    @Override
    public void put(final String key, final Serializable value) throws CoreException {
        getCache().put(key, value);
    }

    @Override
    public void put(final String key, final Object value) throws CoreException {
        getCache().put(key, value);
    }

    @Override
    public Object get(final String key) throws CoreException {
        return getCache().get(key);
    }

    @Override
    public void remove(String key) throws CoreException {
        getCache().remove(key);
    }

    @Override
    public int size() throws CoreException {
        return getCache().size();
    }

    @Override
    public void clear() throws CoreException {
        getCache().clear();
    }

    // ------------------------------------------------------------------------
    //           Getter and Setter
    // ------------------------------------------------------------------------
    protected Region<String, Object> getCache() {
        return this.geodeRegion;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = Args.notBlank(regionName, "regionName");
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClientRegionShortcut() {
        return clientRegionShortcut;
    }

    public void setClientRegionShortcut(String clientRegionShortcut) {
        this.clientRegionShortcut = clientRegionShortcut;
    }

    public ClientRegionShortcut clientRegionShortcut() {
        return ClientRegionShortcut.valueOf(getClientRegionShortcut());
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    public Boolean isDurable() {
        return BooleanUtils.toBooleanDefaultIfNull(this.durable, false);
    }

    public void setDurable(Boolean durable) {
        this.durable = durable;
    }

}
