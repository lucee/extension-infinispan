package org.lucee.extension.infinispan.client;

import java.io.IOException;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.lucee.extension.infinispan.CacheFactory;
import org.lucee.extension.infinispan.MetaUtil;
import org.lucee.extension.infinispan.TimespanGenerator;
import org.lucee.extension.infinispan.client.entry.CacheEntryRemote;
import org.lucee.extension.infinispan.client.marshaller.LuceeMarshaller;
import org.lucee.extension.infinispan.client.util.LuceeTimespanGenerator;

import lucee.commons.io.cache.CacheEntry;
import lucee.commons.io.cache.exp.CacheException;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.config.Config;
import lucee.runtime.type.Struct;

public class InfinispanRemote extends InfinispanBasic {

	private static final TimespanGenerator TSG = new LuceeTimespanGenerator();

	private RemoteCache<Object, Object> cache;
	private Struct args;
	
	
	

	public void init(Config config,String cacheName, Struct arguments) {
		arguments.put("marshaller", LuceeMarshaller.class.getName());
		long defaultLive = toTimespam(arguments,"eternalLifetime","lifetime");
		long defaultIdle = toTimespam(arguments,"eternalIdletime","idletime");
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
		return getCache().withFlags(Flag.FORCE_RETURN_VALUE).remove(key)!=null;
	}

	

	
	@Override
	public CacheEntry getCacheEntry(String key) throws IOException {
		MetadataValue<Object> mdv = getCache().getWithMetadata(key);
		if(mdv==null) throw new CacheException("there is no cache entry for key ["+key+"]");
		return new CacheEntryRemote(key,mdv);
	}

	@Override
	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		MetadataValue<Object> mdv = getCache().getWithMetadata(key);
		if(mdv==null) return defaultValue;
		return new CacheEntryRemote(key,mdv);
	}

	
	public Struct getCustomInfo() {
		RemoteCache<Object, Object> cache = getCache();
		Struct info=CFMLEngineFactory.getInstance().getCreationUtil().createStruct();
		MetaUtil.populateProperties(cache, info, TSG);
		return info;
	}

	protected RemoteCache<Object, Object> getCache() {
		if(cache==null) cache=CacheFactory.getRemoteCacheManager(args, false).getCache();
		else if(!cache.getRemoteCacheManager().isStarted()) {
			cache.getRemoteCacheManager().start();
		}
		return cache;
	}

}
