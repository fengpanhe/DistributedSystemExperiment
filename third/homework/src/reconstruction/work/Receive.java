package work;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Receive implements Runnable{
    String receive_id = null;
    Socket socket = null;
    ObjectInputStream ois = null;

    public Receive(Socket socket) {
        this.socket = socket;
        String clientip = this.socket.getRemoteSocketAddress().toString().split(":|/")[1];
        this.receive_id = clientip.equals(How_many_1.ip1)?  How_many_1.node1 :  How_many_1.node2;
    }
    
    @Override
    public void run() {
    	int receive_num = 0;
    	try {
			ois = new ObjectInputStream(socket.getInputStream());
			while(true){
				Integer src;
				src = (Integer) ois.readObject();
				CallBackManager.getCallBackP().receive_handler(src, receive_id);
				receive_num++;
                if (receive_num == 140) {
                    break;
                }
	    	}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
            try {
                if(ois!=null)ois.close();
                if(socket!=null)socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
    	
    }
}