<cfcomponent extends="Cache">


	
    <cfset fields=array(
		field("Servers","servers","127.0.0.1:11222",true,"This is the initial list of Servers to connect to, specified in the following format: host1:port1;host2:port2... At least one host:port must be specified.","text"),
		field("Cache Name","cacheName","",false,"Name of a specific cache, leave empty to use the default Cache.","text"),
		
		group("Default Times","Default times used when no specific time was defined",3),
		field("Life time","lifetime","86400",true,"Default time to live for an element before it expires.","time"),
		field("Eternal life time","eternalLifetime","false",false,"Eternal life time, if set the time defined above is ignored","checkbox","true"),
		field("Idle time","idletime","86400",true,"Default time to idle for an element before it expires.","time"),
		field("Eternal idle time","eternalIdletime","false",false,"Eternal idle time, if set the time defined above is ignored","checkbox","true"),
		
		group("Timeouts","Timeout for the socket and the connection.",3),
		field("Connection Timeout","connectionTimeout","60",true,"This defines the maximum socket connect timeout in milli seconds before giving up connecting to the server.","time"),
		field("Socket Timeout","socketTimeout","60",true,        "This defines the maximum socket read timeout in milli seconds before giving up waiting for bytes from the server.","time"),
		

		group("General Setting","",3),
		//field("Force return values","forceReturnValues","true",false,"Force return values","checkbox","true"),
		field("Ping on Startup","pingOnStartup","true",true,"If true, a ping request is sent to a back end server in order to fetch cluster's topology.","checkbox","true"),
		field("TCP no delay","tcpNoDelay","true",false,"Affects TCP NODELAY on the TCP stack.","checkbox","true"),
		

		group("Element Size Estimate","This hint allows sizing of byte buffers when serializing and deserializing elements (keys and values), to minimize array resizing.",3),
		field("Key","keySizeEstimate","64",true, "Estimate size of a average key","text"),
		field("Value","valueSizeEstimate","512",true,"Estimate size of a average value","text"),
		
		group("Connection Pooling","The following settings are related to connection pooling.",3),
		field("Maximal active Connections (One Server)","maxActive","-1",true, "The maximum number of connections that are allocated at one time to one server. 
 When non-positive, there is no limit to the number of connections per server. 
 When the number is reached, the connection pool for that server is said to be exhausted.","text"),
		field("Maximal  active Connections (All Servers)","maxTotal","-1",true, "The global limit on the number persistent connections that can be in circulation within the combined set of servers. 
When non-positive, there is no limit to the total number of persistent connections in circulation. 
When maxTotal is exceeded, all connections pools are exhausted.","text"),
		
		field("Minimal idle Connections (One Server)","minIdle","1",true, "The minimum number of idle connections (per server) that should always be available.
			If this parameter is set to a positive number, each time the idle connection eviction thread runs (if you have a eviction thread; see below), 
		it will try to create enough idle instances so that there will be minIdle idle instances available for each server.","text"),
		

		field("Maximal idle Connections (One Server)","maxIdle","-1",true, "The maximum number of idle persistent connections, per server, at any time. 
		When negative, there is no limit to the number of connections that may be idle per server.","text"),
		
		field("Exhausted Action","exhaustedAction","wait",true,
			{
			wait:"the caller will block (invoke waits until a new or idle connections is available",
			create:"a new persistent connection will be created and returned (essentially making maxActive meaningless.)",
			exception:"an exception will be thrown to the calling user",
			_top:"Specifies what happens when asking for a connection from a server's pool, and that pool is exhausted."},"radio","create,exception,wait"),

		field("Time between eviction runs","timeBetweenEvictionRuns","120",true, "Indicates how long the eviction thread should sleep before ""runs"" of examining idle connections. 
			When non-positive, no eviction thread will be launched.","time"),
		
		field("Minimal Evictable Idle Time","minEvictableIdleTime","1800",true, "Specifies the minimum amount of time that an connection may sit idle in the pool before it is eligible for eviction due to idle time.
When non-positive, no connection will be dropped from the pool due to idle time alone. 
This setting has only effect when a eviction thread is started (see above).","time"),
		
		field("Test when idle","testWhileIdle","true",true,"Indicates whether or not idle connections should be validated by sending an TCP packet to the server, during idle connection eviction runs. 
		Connections that fail to validate will be dropped from the pool. 
		This setting has only effect when a eviction thread is started (see above).","checkbox")
	)>

	<cffunction name="getClass" returntype="string">
    	<cfreturn "{class}">
    </cffunction>
	<cffunction name="getBundleName" returntype="string">
    	<cfreturn "{bundlename}">
    </cffunction>
	<cffunction name="getBundleVersion" returntype="string">
    	<cfreturn "{bundleversion}">
    </cffunction>
    
	<cffunction name="getLabel" returntype="string" output="no">
    	<cfreturn "{label}">
    </cffunction>
	<cffunction name="getDescription" returntype="string" output="no">
    	<cfsavecontent variable="local.c">{desc}</cfsavecontent>
    
    
    	<cfreturn c>
    </cffunction>
</cfcomponent>