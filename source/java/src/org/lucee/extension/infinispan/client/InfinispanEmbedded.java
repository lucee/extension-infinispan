package org.lucee.extension.infinispan.client;

import java.io.IOException;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.commons.api.BasicCache;
import org.lucee.extension.infinispan.CacheFactory;
import org.lucee.extension.infinispan.MetaUtil;
import org.lucee.extension.infinispan.TimespanGenerator;
import org.lucee.extension.infinispan.client.entry.CacheEntryEmbedded;
import org.lucee.extension.infinispan.client.entry.CacheEntryRemote;
import org.lucee.extension.infinispan.client.marshaller.LuceeMarshaller;
import org.lucee.extension.infinispan.client.util.LuceeTimespanGenerator;

import lucee.commons.io.cache.CacheEntry;
import lucee.commons.io.cache.exp.CacheException;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.config.Config;
import lucee.runtime.type.Struct;

public class InfinispanEmbedded { /* EMBEDDED extends InfinispanBasic {

	private static final TimespanGenerator TSG = new LuceeTimespanGenerator();

	//private RemoteCache<Object, Object> cache;
	private Struct args;

	private String name;
	
	
	

	public void init(Config config,String cacheName, Struct arguments) {
		arguments.put("marshaller", LuceeMarshaller.class.getName());
		long defaultLive = toTimespam(arguments,"eternalLifetime","lifetime");
		long defaultIdle = toTimespam(arguments,"eternalIdletime","idletime");
		name=CacheFactory.toString(arguments.get("name",null));
		this.args=arguments;
		super.init(defaultLive,defaultIdle);
	}
	
	private static long toTimespam(Struct arguments, String eternalkey, String timekey) {
		// Live
		boolean eternal=CacheFactory.toBooleanValue(arguments.get(eternalkey,null),false);
		if(eternal) return -1;
		
		Long l=CacheFactory.toLong(arguments.get(timekey,null));
		if(l!=null) return l.longValue()*1000L;
		return 86400000L;
	}


	public boolean remove(String key) {
		return getCache().remove(key)!=null;
	}

	

	
	@Override
	public CacheEntry getCacheEntry(String key) throws IOException {
		org.infinispan.container.entries.CacheEntry<Object, Object> ce = getCache().getAdvancedCache().getCacheEntry(key);
		
		if(ce==null) throw new CacheException("there is no cache entry for key ["+key+"]");
		return new CacheEntryEmbedded(key,ce);
	}

	@Override
	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		org.infinispan.container.entries.CacheEntry<Object, Object> ce = getCache().getAdvancedCache().getCacheEntry(key);
		if(ce==null) return defaultValue;
		return new CacheEntryEmbedded(key,ce);
	}

	
	public Struct getCustomInfo() {
		Struct info=CFMLEngineFactory.getInstance().getCreationUtil().createStruct();
		// TODO
		//RemoteCache<Object, Object> cache = getCache();
		//MetaUtil.populateProperties(cache, info, TSG);
		return info;
	}

	protected Cache<Object, Object> getCache() {
		// TODO set config
		EmbeddedCacheManager manager = new DefaultCacheManager();
		Configuration rc = manager.getCacheConfiguration(name);
		Configuration c = new ConfigurationBuilder().read(rc).clustering()
				.cacheMode(CacheMode.DIST_SYNC).l1().lifespan(60000L).build();
		 
		manager.defineConfiguration(name, c);
		Cache<Object, Object> cache = manager.getCache(name);
		
		
		return cache;
		
	}*/
}
