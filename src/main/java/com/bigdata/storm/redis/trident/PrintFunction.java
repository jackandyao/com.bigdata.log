 
package com.bigdata.storm.redis.trident;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.Random;

public class PrintFunction extends BaseFunction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(PrintFunction.class);

    private static final Random RANDOM = new Random();

    public void execute(TridentTuple tuple, TridentCollector tridentCollector) {
        if(RANDOM.nextInt(1000) > 995) {
            LOG.info(tuple.toString());
        }
    }
}
