package work;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by he on 17-4-12.
 */
public class Event {
    private char sendNode;
    private long waitTime;
    private String code;

    public Event(char sendNode, char recNode, int sourceNum,long time){
        this.waitTime = time;
        this.sendNode = sendNode;
        this.code = String.valueOf(1) + "|" + String.valueOf(recNode) + "|" + String.valueOf(sourceNum);
    }

    public Event(String snapshotId, char sendNode, long time) {
        this.waitTime = time;
        this.sendNode = sendNode;
        this.code = String.valueOf(2) + "|" + snapshotId;
    }
}
