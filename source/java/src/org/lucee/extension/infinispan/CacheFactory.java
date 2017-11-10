package org.lucee.extension.infinispan;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ConnectionPoolConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ExecutorFactoryConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.client.hotrod.exceptions.TransportException;

public class CacheFactory {
	
	private static Map<Integer, RemoteCacheManager> managers=new ConcurrentHashMap<Integer, RemoteCacheManager>();
	

	/* *
	 * returns a Cache that is matching giving arguments
	 * @param arguments arguments for the cache
	 * @return
	 * @throws TransportException 
	 * /
	public static RemoteCache<Object, Object> getRemoteCache(Map arguments, boolean forceReturnValue) throws TransportException {
		RemoteCacheManager cacheManager = getRemoteCacheManager(arguments, forceReturnValue);
		String cacheName=getCacheName(arguments, null);
		return cacheName!=null?cacheManager.getCache(cacheName):cacheManager.getCache();
	}
	
	public static RemoteCache<Object, Object> getRemoteCache(Map arguments, String cacheName, boolean forceReturnValue) throws TransportException {
		RemoteCacheManager cacheManager = getRemoteCacheManager(arguments, forceReturnValue);
		cacheName=getCacheName(arguments, cacheName);
		return cacheName!=null?cacheManager.getCache(cacheName):cacheManager.getCache();
	}*/
	
	public static String getCacheName(Map arguments, String cacheName) throws TransportException {

		// CACHE NAME (optional)
		if(cacheName==null || cacheName.trim().length()==0) {
			cacheName=toString(arguments.get("cacheName"));
			
		}
		if(cacheName==null || cacheName.trim().length()==0) cacheName=null;
		else cacheName=cacheName.trim();
		
		return cacheName;
	}
	
	public static RemoteCacheManager getRemoteCacheManager(Map arguments, boolean forceReturnValue) throws TransportException {
		/*
		 CONFIGURATION

		 */
		
		
		ConfigurationBuilder cb=new ConfigurationBuilder();
		
		/* SERVERS (127.0.0.1:xxx)
		  This is the initial list of Hot Rod servers to connect to, specified in the following format: host1:port1;host2:port2... At least one host:port must be specified.*/
		String servers=toString(arguments.get("servers"));
		if(!isEmpty(servers)) cb.addServers(servers);

		/* FORCE RETURN VALUES (default:false)
		  Whether or not to implicitly Flag.FORCE_RETURN_VALUE for all calls.*/
		//boolean b=toBooleanValue(arguments.get("forceReturnValues"),false);
		cb.forceReturnValues(forceReturnValue);
		
		/*
		 * ProtoStreamMarshaller
		 *  MARSHALLER (default:"org.infinispan.marshall.jboss.GenericJBossMarshaller")
		  Allows you to specify a custom Marshaller implementation to serialize and deserialize user objects.
		For portable serialization payloads, you should configure the marshaller to be ApacheAvroMarshaller. */
		String str=toString(arguments.get("marshaller"));
		if(!isEmpty(str))cb.marshaller(str);
		
		/* BALANCING STRATEGY (default:"org.infinispan.client.hotrod.impl.transport.tcp.RoundRobinBalancingStrategy")
		  For replicated (vs distributed) Hot Rod server clusters, 
		  the client balances requests to the servers according to this strategy.*/
		str=toString(arguments.get("balancingStrategy"));
		if(!isEmpty(str))cb.balancingStrategy(str);

		/* TCP NO DELAY (default:true)
		  Affects TCP NODELAY on the TCP stack. */
		boolean b=toBooleanValue(arguments.get("tcpNoDelay"),false);
		cb.tcpNoDelay(b);

		/* PING ON STARTUP (default:true)
		  If true, a ping request is sent to a back end server in order to fetch cluster's topology. */
		b=toBooleanValue(arguments.get("pingOnStartup"),false);
		cb.pingOnStartup(b);
		
		/* TRANSPORT FACTORY (default:"org.infinispan.client.hotrod.impl.transport.tcp.TcpTransportFactory")
		  controls which transport to use. Currently only the TcpTransport is supported. */
		str=toString(arguments.get("transportFactory"));
		if(!isEmpty(str))cb.transportFactory(str);
		
		ExecutorFactoryConfigurationBuilder aef = cb.asyncExecutorFactory();
		/* ASYNC EXECUTOR FACTORY (default:"org.infinispan.client.hotrod.impl.async.DefaultAsyncExecutorFactory")
		  Allows you to specify a custom asynchroous executor for async calls. */
		 str=toString(arguments.get("asyncExecutorFactory"));
		 if(!isEmpty(str))aef.factoryClass(str);
		
		/* DEFAULT EXECUTOR FACTORY POOL SIZE (default:10)
		  If the default executor is used, this configures the number of threads to initialize the executor with. */
		//str=toString(arguments.get("asyncExecutorFactory",null),null);
		//if(!isEmpty(str))aef..asyncExecutorFactory().psetDe;
		
		// TODO infinispan.client.hotrod.default_executor_factory.queue_size, default = 100000. If the default executor is used, this configures the queue size to initialize the executor with.
		// TODO infinispan.client.hotrod.hash_function_impl.1, default = It uses the hash function specified by the server in the responses as indicated in ConsistentHashFactory.) This specifies the version of the hash function and consistent hash algorithm in use, and is closely tied with the HotRod server version used.

		/* KEY SIZE (default:64)
		  This hint allows sizing of byte buffers when serializing and deserializing keys, to minimize array resizing. */
		Integer i=toInteger(arguments.get("keySizeEstimate"));
		if(i!=null)cb.keySizeEstimate(i);
		
		/* VALUE SIZE (default:512)
		  This hint allows sizing of byte buffers when serializing and deserializing values, to minimize array resizing. */
		i=toInteger(arguments.get("valueSizeEstimate"));
		if(i!=null)cb.valueSizeEstimate(i);
		
		/* SOCKET TIMEOUT (default:60000 (ms))
		  This defines the maximum socket read timeout in milli seconds before giving up waiting for bytes from the server.. */
		i=toInteger(arguments.get("socketTimeout"));
		if(i!=null)cb.socketTimeout(i*1000);
		
		/* CONNECT TIMEOUT (default:60000 (ms))
		  This defines the maximum socket connect timeout in milli seconds before giving up connecting to the server. */
		i=toInteger(arguments.get("connectionTimeout"));
		if(i!=null)cb.connectionTimeout(i*1000);
		
		
		/* PROTOCOL VERSION (default:"1.1")
		  This property defines the protocol version that this client should use. Other valid values include 1.0.*/
		str=toString(arguments.get("protocolVersion"));
		if(!isEmpty(str))cb.protocolVersion(str);

	// CONNECTION POOL
		ConnectionPoolConfigurationBuilder cp=cb.connectionPool();
		
		/* MAX ACTIVE (default:no limit == -1)
		  controls the maximum number of connections per server that are allocated (checked out to client threads, or idle in the pool) at one time. 
			When non-positive, there is no limit to the number of connections per server. 
			When maxActive is reached, the connection pool for that server is said to be exhausted. 
			The default setting for this parameter is -1, i.e. there is no limit. */
		i=toInteger(arguments.get("maxActive"));
		if(i!=null)cp.maxActive(i);
		
		/* MAX TOTAL (default:no limit == -1)
		  sets a global limit on the number persistent connections that can be in circulation within the combined set of servers. 
			When non-positive, there is no limit to the total number of persistent connections in circulation. 
			When maxTotal is exceeded, all connections pools are exhausted. 
			The default setting for this parameter is -1 (no limit). */
		i=toInteger(arguments.get("maxTotal"));
		if(i!=null)cp.maxTotal(i);
		
		/* MAX IDLE (default:no limit == -1)
		  controls the maximum number of idle persistent connections, per server, at any time. 
		When negative, there is no limit to the number of connections that may be idle per server. 
		The default setting for this parameter is -1. */
		i=toInteger(arguments.get("maxIdle"));
		if(i!=null)cp.maxIdle(i);
		
		/* EXHAUST ACTION (default:)
		  specifies what happens when asking for a connection from a server's pool, and that pool is exhausted. Possible values:
		0 - an exception will be thrown to the calling user
		1 - the caller will block (invoke waits until a new or idle connections is available.
		2 - a new persistent connection will be created and returned (essentially making maxActive meaningless.)
		The default whenExhaustedAction setting is 1.
		Optionally, one may configure the pool to examine and possibly evict connections as they sit idle in the pool and to ensure that a minimum number of idle connections is maintained for each server. This is performed by an "idle connection eviction" thread, which runs asynchronously. The idle object evictor does not lock the pool throughout its execution. The idle connection eviction thread may be configured using the following attributes:
		*/
		str=toString(arguments.get("exhaustedAction"));
		if(!isEmpty(str)) {
			str=str.trim().toLowerCase();
			if(str.equals("create")) cp.exhaustedAction(ExhaustedAction.CREATE_NEW);
			else if(str.equals("exception")) cp.exhaustedAction(ExhaustedAction.EXCEPTION);
			else if(str.equals("wait")) cp.exhaustedAction(ExhaustedAction.WAIT);
			else System.err.println("invalid exhaust action ["+str+"], valid actions are [create,exception,wait]");
		}
		
		/* TIME BETWEEN EV... (default:2 minutes)
		  indicates how long the eviction thread should sleep before "runs" of examining idle connections. 
			When non-positive, no eviction thread will be launched. 
			The default setting for this parameter is 2 minutes */
		Long l=toLong(arguments.get("timeBetweenEvictionRuns"));
		if(l!=null)cp.timeBetweenEvictionRuns(l*1000);
		
		/* MIN EVIC IDLE ... (default:2 minutes)
			specifies the minimum amount of time that an connection may sit idle in the pool before it is eligible for eviction due to idle time.
			When non-positive, no connection will be dropped from the pool due to idle time alone. 
			This setting has no effect unless timeBetweenEvictionRunsMillis > 0. 
			The default setting for this parameter is 1800000(30 minutes).*/
		l=toLong(arguments.get("minEvictableIdleTime"));
		if(l!=null)cp.minEvictableIdleTime(l*1000);
		
		/* TEST WHEN IDLE (default:true)
		indicates whether or not idle connections should be validated by sending an TCP packet to the server, during idle connection eviction runs. 
		Connections that fail to validate will be dropped from the pool. 
		This setting has no effect unless timeBetweenEvictionRunsMillis > 0. 
		The default setting for this parameter is true.*/
		b=toBooleanValue(arguments.get("testWhileIdle"),false);
		cp.testWhileIdle(b);
	
		/* MIN IDLE (default: 1)
		sets a target value for the minimum number of idle connections (per server) that should always be available. 
		If this parameter is set to a positive number and timeBetweenEvictionRunsMillis > 0, 
		each time the idle connection eviction thread runs, 
		it will try to create enough idle instances so that there will be minIdle idle instances available for each server. 
		The default setting for this parameter is 1. */
		i=toInteger(arguments.get("minIdle"));
		if(i!=null)cp.minIdle(i);
		
		
		Configuration c = cb.build(true);
		str=c.toString();
		int hash=str.hashCode(); // TODO better hash
		
		// existing cache manager
		RemoteCacheManager cacheManager = managers.get(hash);
		if(cacheManager!=null) {
			if(!cacheManager.isStarted()) cacheManager.start();
			
		}
		// new cache manager
		else {
			cacheManager = new RemoteCacheManager(c,true);
			managers.put(hash, cacheManager);
		}
		
		return cacheManager;
	}

	public static boolean isEmpty(String str) {
		return str==null || str.length()==0;
	}

	public static Long toLong(Object obj) {
		if(obj instanceof Number) return ((Number)obj).longValue();
		if(obj instanceof Boolean) return ((Boolean)obj).booleanValue()?1L:0L;
		if(obj instanceof CharSequence) {
			try{
				return Long.parseLong(((CharSequence)obj).toString());
			}
			catch (Exception t){}
		}
		return null;
	}
	
	public static Integer toInteger(Object obj) {
		if(obj instanceof Number) return ((Number)obj).intValue();
		if(obj instanceof Boolean) return ((Boolean)obj).booleanValue()?1:0;
		if(obj instanceof CharSequence) {
			try{
				return Integer.parseInt(((CharSequence)obj).toString());
			}
			catch (Exception t){}
		}
		return null;
	}

	public static String toString(Object obj) {
		if(obj==null) return null;
		return obj.toString();
	}
	
	public static Boolean toBoolean(Object obj) {
		if(obj instanceof Boolean) return ((Boolean)obj);
		if(obj instanceof Number) return ((Number)obj).doubleValue()!=0.0d;
		if(obj instanceof CharSequence) return stringToBoolean(((CharSequence)obj).toString());
		return null;
	}
	
	public static boolean toBooleanValue(Object obj, boolean defaultValue) {
		if(obj instanceof Boolean) return ((Boolean)obj).booleanValue();
		if(obj instanceof Number) return ((Number)obj).doubleValue()!=0.0d;
		if(obj instanceof CharSequence) {
			Boolean res= stringToBoolean(((CharSequence)obj).toString());
			if(res==null) return defaultValue;
			return res.booleanValue();
		}
		return defaultValue;
	}
	
	private static Boolean stringToBoolean(String str) {
        if(str.length()<2) return null;
        switch(str.charAt(0)) {
            case 't':
            case 'T': return str.equalsIgnoreCase("true")?Boolean.TRUE:null;
            case 'f':
            case 'F': return str.equalsIgnoreCase("false")?Boolean.FALSE:null;
            case 'y':
            case 'Y': return str.equalsIgnoreCase("yes")?Boolean.TRUE:null;
            case 'n':
            case 'N': return str.equalsIgnoreCase("no")?Boolean.FALSE:null;
        }
        return null;
    }

}
