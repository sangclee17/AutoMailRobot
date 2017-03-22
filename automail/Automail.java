package automail;

import strategies.*;

public class Automail {
	      
    public Robot robot;
    public IMailPool mailPool;
    
    Automail(IMailDelivery delivery) {
 
    	MyMailPool myMailPool = new MyMailPool();
    	mailPool = myMailPool;
    	
    	IMailSorter sorter = new MyMailSorter(myMailPool);
    	
    	/** Initialize robot */
    	robot = new Robot(sorter, delivery);
    	
    }
    
}
