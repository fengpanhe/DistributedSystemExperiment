package work;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Receive implements Runnable{
	
    String send_ip = null;
    Socket socket = null;
    ObjectInputStream ois = null;
    int level = 0;//0表示P节点，1为C节点
    public static boolean isrunning = true;

    public Receive(Socket socket, int level) {
        this.socket = socket;
        this.level = level;
        send_ip = this.socket.getRemoteSocketAddress().toString().split(":|/")[1];
    }
  
    @Override
    public void run() {
    	/*
    	 * 判断level
    	 */
        while(isrunning){
        	CallBackManager.getCallBackP().receive_handler();
        }
    }
}