
package strategies;

import exceptions.TubeFullException;
import automail.MailItem;
import automail.IMailSorter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import automail.Building;
import automail.Clock;
import automail.StorageTube;

public class MyMailSorter implements IMailSorter{

	MyMailPool myMailPool;
	
	public MyMailSorter(MyMailPool myMailPool) {
		this.myMailPool = myMailPool;
	}
	
	private final static int MAX_STORAGE_CAPACITY = 4;
	private final static int MEDIUM_MAILITEM_PROCESS_DELAY = 28;
	private final static int LOW_MAILITEM_PROCESS_DELAY = 35;
	private final static int MIN_MAILITEMS_LENGTH = 7;
	
	private ArrayList <MailItem> selectedMailItems = new ArrayList <MailItem>();
	
    @Override
    public boolean fillStorageTube(StorageTube tube) {
    
        try{
            if (!myMailPool.isEmptyPool()) {
            	//System.out.println(Clock.Time());
            	MailItem[] mailItems = this.fetchMailItems();
            	selectMailItems(mailItems);
            	for (MailItem mail : selectedMailItems) {
            		//System.out.println(mail.getArrivalTime() +" "+ mail.getPriorityLevel() +" "+ mail.getSize() +" " + mail.getDestFloor());
            		tube.addItem(mail);
            		myMailPool.updateMailPool(mail);
            	}
            	//System.out.println("~!~!~!~!~!~!");
            	selectedMailItems.removeAll(selectedMailItems);
            	return true;
            }
        }

        catch(TubeFullException e){
        	return true;
        }      
        if(Clock.Time() > Clock.LAST_DELIVERY_TIME && myMailPool.isEmptyPool() && !tube.isEmpty()){
            return true;
        }
        return false;
    }

    public MailItem[] fetchMailItems() {
    	MailItem[] mailItems = myMailPool.get();
    	int mailItemLength = mailItems.length;
    	ArrayList <MailItem> mailStorage = new ArrayList <MailItem>();
		
		for (MailItem mail : mailItems) {
			if (mail.getPriorityLevel().equals("HIGH")) {
				mailStorage.add(mail);
			}
			else if (mail.getPriorityLevel().equals("MEDIUM") && Clock.Time() - mail.getArrivalTime() >= MEDIUM_MAILITEM_PROCESS_DELAY) {
				mailStorage.add(mail);
			}
			else if (mail.getPriorityLevel().equals("LOW") && Clock.Time() - mail.getArrivalTime() >= LOW_MAILITEM_PROCESS_DELAY) {
				mailStorage.add(mail);
			}
			else if (mailItemLength < MIN_MAILITEMS_LENGTH) {
				mailStorage.add(mail);
			}
		}
		return mailStorage.toArray(new MailItem[0]);
    }
    
    public void selectMailItems(MailItem[] mailItems) {
    	int mailStorageSize = MAX_STORAGE_CAPACITY;
    	
    	mailItemsInOrder(mailItems);
    	int destFloor = mailItems[0].getDestFloor();
    	
    	for (MailItem mail : mailItems) {
    		if (mailStorageSize - mail.getSize() >= 0 && Math.abs(mail.getDestFloor() - destFloor) <= 1) {
    			selectedMailItems.add(mail);
				mailStorageSize -= mail.getSize();
				if (mailStorageSize == 0) {
					return;
				}
    		}
    	}
    }
    
    public void mailItemsInOrder(MailItem[] mailItems) {
    	if (mailItems.length < 2) {
			return;
		}
		Arrays.sort(mailItems, new Comparator<MailItem>(){	
			public int compare(MailItem o1, MailItem o2) {
				int result = Math.abs(Building.MAILROOM_LOCATION - o1.getDestFloor()) - Math.abs(Building.MAILROOM_LOCATION - o2.getDestFloor());
				if (result == 0) {
					return o1.getSize() - o2.getSize();
				}
				return result;
			}
		});
    }
}
