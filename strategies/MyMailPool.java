
package strategies;

import automail.*;
import java.util.ArrayList;

public class MyMailPool implements IMailPool {
	
	private ArrayList <MailItem> mailItems;
	
	public MyMailPool() {
		mailItems = new ArrayList <MailItem>();
	}

	@Override
	public void addToPool(MailItem mailItem) {
		mailItems.add(mailItem);
	}

	public boolean isEmptyPool() {
        return mailItems.isEmpty();
    }
	
	public MailItem[] get() {
		return mailItems.toArray(new MailItem[0]);
	}

    public void updateMailPool (MailItem mailItem) {
    	this.remove(mailItem, mailItems);
    }
    
    public void remove(MailItem mailItem, ArrayList <MailItem> mailItems) {
        mailItems.remove(mailItem);
    }
}
