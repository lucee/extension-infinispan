package org.lucee.extension.infinispan.client.util;

import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.type.dt.TimeSpan;
import org.lucee.extension.infinispan.TimespanGenerator;

public class LuceeTimespanGenerator implements TimespanGenerator {

	@Override
	public Object toTimespan(long millis) {
		return createTimespan(millis);
	}

	public static TimeSpan createTimespan(long millis) { 
		return CFMLEngineFactory.getInstance().getCastUtil().toTimespan(new Double(millis>0?(millis/86400000D):0),null); // days
	}
}
