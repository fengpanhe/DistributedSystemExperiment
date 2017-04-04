package work;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class Send implements Runnable{
    String sendId;
    String sendIp;
    int sendPort, deny;
    ObjectOutputStream oos = null;
    Socket socket = null;

    public Send(String ip, int port, String sendId, int deny) {
    	this.sendId = sendId;
    	this.deny = deny;
    	this.sendIp = ip;
    	this.sendPort = port;
    	try {
			socket = new Socket(this.sendIp, this.sendPort);
			 oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
    }


    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}