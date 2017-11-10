package org.lucee.extension.infinispan.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConnectionPoolConfiguration;
import org.infinispan.client.hotrod.configuration.ExecutorFactoryConfiguration;
import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.client.hotrod.configuration.ServerConfiguration;
import org.infinispan.client.hotrod.configuration.SslConfiguration;
import org.lucee.extension.infinispan.CacheFactory;
import org.lucee.extension.infinispan.InfinispanException;
import org.lucee.extension.infinispan.MetaUtil;
import org.lucee.extension.infinispan.TimespanGenerator;
import org.lucee.extension.infinispan.client.entry.CacheEntryRemote;
import org.lucee.extension.infinispan.client.marshaller.LuceeMarshaller;
import org.lucee.extension.infinispan.client.util.LuceeTimespanGenerator;

import lucee.commons.io.cache.CacheEntry;
import lucee.commons.io.cache.CacheKeyFilter;
import lucee.commons.io.cache.exp.CacheException;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.config.Config;
import lucee.runtime.type.Struct;

public class Old extends CacheSupport {

	private static final TimespanGenerator TSG = new LuceeTimespanGenerator();
	//private static final int DEFAULT_CONTROL_INTERVAL_SECONDS = 60;
	
	
	/*
	private long missCount;
	private int hitCount;
	
	private int minEntries;
	private int maxEntries;
	private long timeToLive;
	private long minTimeToLive;
	private long timeToIdle;
	private int controlInterval;
*/
	private RemoteCache<Object, Object> cache;
	private long defaultLive;
	private long defaultIdle;
	private Struct args;
	
	
	public static void init(Config config,String[] arg1,Struct[] arg2) {
		
	}
	

	public void init(Config config,String cacheName, Struct arguments) {
		arguments.put("marshaller", LuceeMarshaller.class.getName());
		defaultLive=toTimespam(arguments,"eternalLifetime","lifetime");
		defaultIdle=toTimespam(arguments,"eternalIdletime","idletime");
		this.args=arguments;
	}
	
	private static long toTimespam(Struct arguments, String eternalkey, String timekey) {
		// Live
		boolean eternal=CacheFactory.toBooleanValue(arguments.get(eternalkey,null),false);
		if(eternal) return -1;
		
		Long l=CacheFactory.toLong(arguments.get(timekey,null));
		if(l!=null) return l.longValue()*1000L;
		return 86400000L;
	}


	@Override
	public boolean contains(String key) {
		return getCache().containsKey(key);
	}

	
	




	@Override
	public long hitCount() {
		return -1;
	}

	@Override
	public long missCount() {
		return -1;
	}
	
	@Override
	public List<String> keys() {
		List<String> list=new ArrayList<String>();
		Iterator<Object> it = getCache().keySet().iterator();
		String k;
		while(it.hasNext()){
			k=CacheFactory.toString(it.next());
			if(k!=null)list.add(k);
		}
		return list;
	}
	
	@Override
	public List<Object> values() throws IOException {
		Iterator<Object> it = getCache().values().iterator();
		List<Object> list=new ArrayList<Object>();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	// there was the wrong generic type defined in the older interface, because of that we do not define a generic type at all here, just to be sure
	@Override
	public List<Object> values(CacheKeyFilter filter) throws IOException {
		if(allowAll(filter)) return values();
		
		Iterator<Entry<Object, Object>> it = getCache().entrySet().iterator();
		List<Object> list=new ArrayList<Object>();
		Entry<Object, Object> e;
		String key;
		while(it.hasNext()){
			e = it.next();
			key=CacheFactory.toString(e.getKey());
			if(filter.accept(key))list.add(e.getValue());
		}
		return list;
	}

	public void put(String key, Object value, Long idleTime, Long until) {
		long life = until==null?defaultLive:until.longValue();
		long idle=idleTime==null?defaultIdle:idleTime.longValue();
		getCache().put(key,value,life,TimeUnit.MILLISECONDS,idle,TimeUnit.MILLISECONDS);
	}
	/*public void put(String key, Object value, Long idleTime, Long until) {
		long life = until==null?-1:until.longValue();
		long idle=idleTime==null?-1:idleTime.longValue();
		CacheEntryImpl entry = (CacheEntryImpl) getCache().putIfAbsent(key, new CacheEntryImpl(key, value,idle ,life));
		
		if(entry!=null) {
			entry.update(
					value
					,idleTime==null?-1:idleTime.longValue()
					,until==null?-1:until.longValue()
				);
			getCache().put(key,entry,life,TimeUnit.MILLISECONDS,idle,TimeUnit.MILLISECONDS);
		}
	}*/

	public boolean remove(String key) {
		return getCache().withFlags(Flag.FORCE_RETURN_VALUE).remove(key)!=null;
	}

	


	@Override
	public Object getValue(String key) throws IOException {
		Object value = getCache().get(key);
		if(value==null) throw new CacheException("there is no cache entry for key ["+key+"]");
		return value;
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		Object value = getCache().get(key);
		if(value==null) return defaultValue;
		return value;
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

	private RemoteCache<Object, Object> getCache() {
		if(cache==null) cache=CacheFactory.getRemoteCacheManager(args, false).getCache();
		else if(!cache.getRemoteCacheManager().isStarted()) {
			cache.getRemoteCacheManager().start();
		}
		return cache;
	}


	@Override
	public int clear() {
		int size=getCache().size();
		getCache().clear();
		return size;
	}

}
