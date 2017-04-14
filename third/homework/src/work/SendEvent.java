
package work;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class SendEvent implements Runnable{
    String sendId;
    int deny;
    ObjectOutputStream oos = null;
    Integer code;
    long waitTime;

    public SendEvent(ObjectOutputStream socket, String sendId, int deny,Integer code, long waitTime) {
    	this.sendId = sendId;
    	this.deny = deny;
        this.code = code;
        this.waitTime = waitTime;
        oos = socket;

    }
    
    private void sendWait(){
        try {
            Thread.sleep((long) this.waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
    	this.sendWait();
        try {
            Thread.sleep(deny);
            oos.writeObject(this.code);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}