package org.lucee.extension.infinispan.client.entry;

import java.util.Date;

import org.infinispan.client.hotrod.MetadataValue;
import org.lucee.extension.infinispan.MetaUtil;
import org.lucee.extension.infinispan.client.util.LuceeTimespanGenerator;

import lucee.commons.io.cache.CacheEntry;
import lucee.loader.engine.CFMLEngine;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.type.Struct;
import lucee.runtime.util.Cast;

public class CacheEntryEmbedded {/* EMBEDDED implements CacheEntry {

	private String key;
	private org.infinispan.container.entries.CacheEntry<Object, Object> ce;


	public CacheEntryEmbedded(String key, org.infinispan.container.entries.CacheEntry<Object, Object> ce) {
		this.key=key;
		this.ce=ce;
		
	}

	@Override
	public Struct getCustomInfo() {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		Struct info=engine.getCreationUtil().createStruct();
		info.setEL("key", getKey());
		
		info.setEL("eternal", isEternal());
		if(isEternal()==Boolean.FALSE) {
			info.setEL("created", created());
			info.setEL("last_hit", lastHit());
			info.setEL("last_modified", lastModified());
		}

		info.setEL("changed", ce.isChanged());
		info.setEL("created", ce.isCreated());
		info.setEL("evicted", ce.isEvicted());
		info.setEL("loaded", ce.isLoaded());
		info.setEL("null", ce.isNull());
		info.setEL("removed", ce.isRemoved());
		info.setEL("valid", ce.isValid());
		

		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		
		info.setEL("idle_time_span", LuceeTimespanGenerator.createTimespan(idleTimeSpan()));		
		info.setEL("live_time_span", LuceeTimespanGenerator.createTimespan(liveTimeSpan()));
		info.setEL("version", caster.toString(version(),null));
		
		
		return info;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return ce.getValue();
	}

	@Override
	public int hitCount() {
		return -1; // TODO
	}

	public String version() {
		return ce.getMetadata().version().toString();
	}
	
	@Override
	public Date created() {
		if(ce instanceof InternalCacheEntry) {
			return MetaUtil.lastModified((InternalCacheEntry<Object,Object>)ce);
		}
		return null;
	}
	
	@Override
	public Date lastModified() {
		if(ce instanceof InternalCacheEntry) {
			return MetaUtil.lastModified((InternalCacheEntry<Object,Object>)ce);
		}
		return null;
	}

	@Override
	public Date lastHit() {
		return lastModified();
	}

	@Override
	public long liveTimeSpan() {
		return ce.getLifespan();
	}
	
	@Override
	public long idleTimeSpan() {
		return ce.getMaxIdle();
	}
	
	public Boolean isEternal() {
		if(ce instanceof InternalCacheEntry) {
			return MetaUtil.isEternal((InternalCacheEntry<Object,Object>)ce);
		}
		return null;
	}

	public long size() {
		String v=CFMLEngineFactory.getInstance().getCastUtil().toString(ce.getValue(), null);
		if(v!=null) return v.length();
		return 0;
	}*/
}
