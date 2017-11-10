package org.lucee.extension.infinispan.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.infinispan.commons.api.BasicCache;
import org.lucee.extension.infinispan.CacheFactory;

import lucee.commons.io.cache.CacheKeyFilter;
import lucee.commons.io.cache.exp.CacheException;
import lucee.runtime.config.Config;
import lucee.runtime.type.Struct;

public abstract class InfinispanBasic extends CacheSupport {

	private long defaultLive;
	private long defaultIdle;
	
	public static void init(Config config,String[] arg1,Struct[] arg2) {
		
	}

	public void init(long defaultLive, long defaultIdle) {
		this.defaultIdle=defaultIdle;
		this.defaultLive=defaultLive;
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
	public final int clear() {
		int size=getCache().size();
		getCache().clear();
		return size;
	}
	
	protected abstract BasicCache<Object, Object> getCache();




}
