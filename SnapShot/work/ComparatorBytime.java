package work;

import java.util.Comparator;

/**
 * Created by he on 17-4-13.
 */
public class ComparatorBytime implements Comparator {
    public int compare(Object object1, Object object2) {
        Event event1 = (Event) object1;
        Event event2 = (Event) object2;
        if(event1.getWaitTime() > event2.getWaitTime()){
            return 1;
        } else if(event1.getWaitTime() < event2.getWaitTime()){
            return -1;
        } else {
            return 0;
        }
    }
}
