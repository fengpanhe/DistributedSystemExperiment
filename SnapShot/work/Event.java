package work;

/**
 * Created by he on 17-4-12.
 */
public class Event {
    private String eventType;
    private char sendNode;
    private long waitTime;
    private String code;
    private char snapPrevNode;
    private char sourceAction;

    // 资源转移事件
    public Event(char sendNode, char recNode, int sourceNum,long time){
        this.eventType = "sourceSend";
        this.sourceAction = 's';
        this.waitTime = time;
        this.sendNode = sendNode;
        this.code = "1" + "|" + String.valueOf(recNode) + "|" + String.valueOf(sourceNum);
    }
    public Event(char sendNode, char recNode, int sourceNum,long time,char sourceAction){
        this.eventType = "sourceSend";
        this.sourceAction = sourceAction;
        this.waitTime = time;
        this.sendNode = sendNode;
        this.code = "1" + "|" + String.valueOf(recNode) + "|" + String.valueOf(sourceNum);
    }
    //快照事件
    public Event(String snapshotId, char sendNode, char snapPrevNode, long time) {
        this.snapPrevNode = snapPrevNode;
        this.eventType = "snapshot";
        this.waitTime = time;
        this.sendNode = sendNode;
        this.code = "2" + "|" + snapshotId;
    }
    //结束事件
    public Event(long time) {
        this.eventType = "end";
        this.waitTime = time;
        this.sendNode = 'a';
        this.code = "5";
    }
    public String getEventType(){
        return this.eventType;
    }

    public char getSourceAction() {
        return sourceAction;
    }

    public char getSnapPrevNode() {
        return snapPrevNode;
    }

    public char getSendNode(){
        return this.sendNode;
    }
    public long getWaitTime(){
        return this.waitTime;
    }
    public String getCode(){
        return this.code;
    }
}
