package work;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class SendEvent implements Runnable{
    String sendId;
//    String sendIp;
//    int sendPort;
    int deny;
    ObjectOutputStream oos = null;
//    Socket socket = null;
    Integer code;
    long waitTime;

    public SendEvent(Socket socket, String sendId, int deny,Integer code, long waitTime) {
    	this.sendId = sendId;
    	this.deny = deny;
        this.code = code;
        this.waitTime = waitTime;
//    	this.sendIp = ip;
//    	this.sendPort = port;
    	try {
//			socket = new Socket(this.sendIp, this.sendPort);
			 oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private void sendWait(){
        try {
            Thread.sleep((long) this.waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
//    public void sendEvent(Integer code, long waitTime){
//        this.code = code;
//        this.waitTime = waitTime;
//    }

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