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

public class CacheEntryRemote implements CacheEntry {

	private String key;
	private MetadataValue<Object> mdv;


	public CacheEntryRemote(String key, MetadataValue<Object> mdv) {
		this.mdv=mdv;
		this.key=key;
	}

	@Override
	public Struct getCustomInfo() {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		Struct info=engine.getCreationUtil().createStruct();
		info.setEL("key", getKey());
		
		info.setEL("eternal", isEternal());
		if(!isEternal()) {
			info.setEL("created", created());
			info.setEL("last_hit", lastHit());
			info.setEL("last_modified", lastModified());
		}
		
		
		//info.setEL("hit_count", new Double(hitCount()));
		//info.setEL("size", new Double(size()));
		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		
		info.setEL("idle_time_span", LuceeTimespanGenerator.createTimespan(MetaUtil.idleTimeSpan(mdv)));		
		info.setEL("live_time_span", LuceeTimespanGenerator.createTimespan(MetaUtil.liveTimeSpan(mdv)));
		info.setEL("version", caster.toString(version(),null));
		
		
		return info;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return mdv.getValue();
	}

	@Override
	public int hitCount() {
		return -1; // TODO
	}

	public long version() {
		return mdv.getVersion();
	}
	
	@Override
	public Date created() {
		return MetaUtil.created(mdv);
	}
	
	@Override
	public Date lastModified() {
		return MetaUtil.lastModified(mdv);
	}

	@Override
	public Date lastHit() {
		return lastModified();
	}

	@Override
	public long liveTimeSpan() {
		return MetaUtil.liveTimeSpan(mdv)/1000;
	}
	
	@Override
	public long idleTimeSpan() {
		return MetaUtil.idleTimeSpan(mdv)/1000;
	}
	
	public boolean isEternal() {
		return MetaUtil.isEternal(mdv);
	}

	public long size() {
		String v=CFMLEngineFactory.getInstance().getCastUtil().toString(mdv.getValue(), null);
		if(v!=null) return v.length();
		return 0;
	}
}
