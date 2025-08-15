package org.lucee.extension.infinispan;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConnectionPoolConfiguration;
import org.infinispan.client.hotrod.configuration.ExecutorFactoryConfiguration;
import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.client.hotrod.configuration.SecurityConfiguration;
import org.infinispan.client.hotrod.configuration.ServerConfiguration;
import org.infinispan.client.hotrod.configuration.SslConfiguration;
import org.infinispan.client.hotrod.impl.transport.tcp.FailoverRequestBalancingStrategy;

public class MetaUtil {

    /* EMBEDDED public static boolean isEternal(InternalCacheEntry<Object, Object> ce) {
		return ce.getCreated()==-1;
	}
    
	public static Date created(InternalCacheEntry<Object, Object> ce) {
		if(isEternal(ce)) return null;
		return new Date(ce.getCreated());
	}
	
	public static Date lastModified(InternalCacheEntry<Object, Object> ce) {
		if(ce.getLastUsed()!=-1) return new Date(ce.getLastUsed());
		if(isEternal(ce)) return null;
		return created(ce);
	}*/
	
    public static boolean isEternal(MetadataValue<Object> mdv) {
		return mdv.getCreated()==-1;
	}
    
	public static Date created(MetadataValue<Object> mdv) {
		if(isEternal(mdv)) return null;
		return new Date(mdv.getCreated());
	}

	
	public static Date lastModified(MetadataValue<Object> mdv) {
		if(mdv.getLastUsed()!=-1) return new Date(mdv.getLastUsed());
		if(isEternal(mdv)) return null;
		return created(mdv);
	}
	
	public static long liveTimeSpan(MetadataValue<Object> mdv) {
		if(mdv.getLifespan()==-1) return 0;
		return mdv.getLifespan()*1000;
		//if(isEternal()) return 0;
		//return mdv.getCreated()+(mdv.getLifespan()*1000);
	}
	
	public static long idleTimeSpan(MetadataValue<Object> mdv) {
		if(mdv.getMaxIdle()==-1) return 0;
		return mdv.getMaxIdle()*1000;
	}
	
	public static void populateProperties(RemoteCache<Object, Object> cache,Map info, TimespanGenerator tsg) {
		RemoteCacheManager rcm = cache.getRemoteCacheManager();
		Configuration config = rcm.getConfiguration();
		ExecutorFactoryConfiguration aef = config.asyncExecutorFactory();
		
		info.put("name", cache.getName()==null?"":cache.getName());
		info.put("async_executor_factory", aef.factoryClass().getName());
		info.put("balancing_strategy", config.balancingStrategyClass().getName());
		
		// Connection Pool
		ConnectionPoolConfiguration cp = config.connectionPool();
		ExhaustedAction ea = cp.exhaustedAction();
		if(ea==ExhaustedAction.CREATE_NEW)
			info.put("connection_pool_exhaust_action", "create");
		else if(ea==ExhaustedAction.EXCEPTION)
			info.put("connection_pool_exhaust_action", "exception");
		if(ea==ExhaustedAction.WAIT)
			info.put("connection_pool_exhaust_action", "wait");
		info.put("connection_pool_max_active", Double.valueOf(cp.maxActive()));
		info.put("connection_pool_max_idle", Double.valueOf(cp.maxIdle()));
		info.put("connection_pool_max_total", Double.valueOf(cp.maxTotal()));
		info.put("connection_pool_max_wait", Double.valueOf(cp.maxWait()));
		
		info.put("connection_pool_min_evictable_idle_time",tsg.toTimespan(cp.minEvictableIdleTime()));
		info.put("connection_pool_min_idle", Double.valueOf(cp.minIdle()));
		info.put("connection_pool_tests_per_eviction_run", Double.valueOf(cp.numTestsPerEvictionRun()));
		info.put("connection_pool_time_between_eviction_runs", tsg.toTimespan(cp.timeBetweenEvictionRuns()));
		info.put("connection_pool_lifo", cp.lifo());
		info.put("connection_pool_test_on_borrow", cp.testOnBorrow());
		info.put("connection_pool_test_on_return", cp.testOnReturn());
		
		
		info.put("connection_timeout", tsg.toTimespan(config.connectionTimeout()));
		info.put("socket_timeout",  tsg.toTimespan(config.socketTimeout()));
		info.put("force_return_values", config.forceReturnValues());
		info.put("key_size_estimate", Double.valueOf(config.keySizeEstimate()));
		info.put("value_size_estimate", Double.valueOf(config.valueSizeEstimate()));
		info.put("marshaller", config.marshallerClass().getName());
		info.put("ping_on_startup", config.pingOnStartup());
		info.put("protocol_version", config.protocolVersion());
		
		Iterator<ServerConfiguration> it = config.servers().iterator();
		ServerConfiguration sc;
		StringBuilder sb=new StringBuilder();
		while(it.hasNext()){
			sc=it.next();
			if(sb.length()>0) sb.append(';');
			sb.append(sc.host()).append(':').append(sc.port());
		}
		info.put("servers", sb.toString());
		info.put("tcp_no_delay", config.tcpNoDelay());
		info.put("transport_factory", config.transportFactory().getName());
		
		SslConfiguration ssl = config.security().ssl();
		info.put("ssl_enabled", ssl.enabled());
		
		
	}
}
