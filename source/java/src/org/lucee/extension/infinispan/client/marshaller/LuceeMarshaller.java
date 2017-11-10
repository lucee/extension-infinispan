package org.lucee.extension.infinispan.client.marshaller;

import org.infinispan.commons.marshall.jboss.AbstractJBossMarshaller;
import org.infinispan.commons.marshall.jboss.DefaultContextClassResolver;

import lucee.loader.engine.CFMLEngineFactory;

public class LuceeMarshaller extends AbstractJBossMarshaller {
		public LuceeMarshaller() {
			baseCfg.setClassResolver(
				new DefaultContextClassResolver(CFMLEngineFactory.getInstance().getCreationUtil().createArray().getClass().getClassLoader()));
		}
	}
