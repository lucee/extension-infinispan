package org.lucee.extension.infinispan.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lucee.commons.io.cache.Cache;
import lucee.commons.io.cache.CacheEntry;
import lucee.commons.io.cache.CacheEntryFilter;
import lucee.commons.io.cache.CacheFilter;
import lucee.commons.io.cache.CacheKeyFilter;
import lucee.loader.engine.CFMLEngine;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.loader.util.Util;
import lucee.runtime.type.dt.TimeSpan;
import lucee.runtime.util.Cast;
import lucee.runtime.util.Creation;

public abstract class CacheSupport implements Cache {
	
	//static Cast caster;

	@Override
	public final List<String> keys(CacheKeyFilter filter) throws IOException {
		boolean all=allowAll(filter);
		
		List<String> keys = keys();
		List<String> list=new ArrayList<String>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key= it.next();
			if(all || filter.accept(key))list.add(key);
		}
		return list;
	}
	

	
	@Override
	public List keys(CacheEntryFilter filter) throws IOException {
		boolean all=allowAll(filter);
		
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getCacheEntry(key,null);
			if(all || filter.accept(entry))list.add(entry);
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries() throws IOException {
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			list.add(getCacheEntry(it.next(),null));
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries(CacheKeyFilter filter) throws IOException {
		if(allowAll(filter)) return entries();
		
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
			if(filter.accept(key))list.add(getCacheEntry(key,null));
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries(CacheEntryFilter filter) throws IOException {
		if(allowAll(filter)) return entries();
		
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		CacheEntry entry;
		while(it.hasNext()){
			entry=getCacheEntry(it.next(),null);
			if(filter.accept(entry))list.add(entry);
		}
		return list;
	}

	// there was the wrong generic type defined in the older interface, because of that we do not define a generic type at all here, just to be sure
	@Override
	public List<Object> values(CacheEntryFilter filter) throws IOException {
		if(allowAll(filter)) return values();
		
		List<String> keys = keys();
		List<Object> list=new ArrayList<Object>();
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getCacheEntry(key,null);
			if(filter.accept(entry))list.add(entry.getValue());
		}
		return list;
	}

	
	@Override
	public int remove(CacheEntryFilter filter) throws IOException {
		if(allowAll(filter)) return clear();
		
		List<String> keys = keys();
		int count=0;
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getCacheEntry(key,null);
			if(filter==null || filter.accept(entry)){
				remove(key);
				count++;
			}
		}
		return count;
	}

	@Override
	public int remove(CacheKeyFilter filter) throws IOException {
		if(allowAll(filter)) return clear();
		
		List<String> keys = keys();
		int count=0;
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
			if(filter==null || filter.accept(key)){
				remove(key);
				count++;
			}
		}
		return count;
	}
	
	public abstract int clear();

	public static boolean allowAll(CacheFilter filter) {
		if(filter==null)return true;
		String p = filter.toPattern();
		if(p==null) return true;
		p=p.trim();
		return p.equals("*") || p.equals("");
	}	
}
