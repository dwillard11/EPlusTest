package ca.manitoulin.mtd.util;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to generate unique ID
 * @author : Bob Yu
 * @version : 1.0
 */
public class IDGenerator {
    private static AtomicInteger counter;

    static {
        Random random = new Random();
        int seed = random.nextInt(100);
        counter = new AtomicInteger(seed);
    }
    
    private IDGenerator(){
    	
    }

    /**
     * Generate unique key 
     * @return
     */
    public static long getAtomicCounter() {
        if (counter.get() > 999999) {
            counter.set(1);
        }
        long time = System.currentTimeMillis();
        long returnValue = time * 100 + counter.incrementAndGet();
        return returnValue;
    }

    /**
     * generate an UUID
     * @return
     */
    public static String getHex() {
        return UUID.randomUUID().toString();
    }
}
