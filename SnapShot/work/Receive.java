package work;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Receive implements Runnable{
	
    String receive_ip = null;
    Socket socket = null;
    ObjectInputStream ois = null;
    String receive_node = null;

    public Receive(Socket socket, boolean level) {
        this.socket = socket;
        receive_ip = this.socket.getRemoteSocketAddress().toString().split(":|/")[1];
        if (level) {
        	if (receive_ip == CNode.ip_i) {
				receive_node = "i";
			}else if (receive_ip == CNode.ip_j) {
				receive_node = "j";
			}else {
				receive_node = "k";
			}
		}else {
			if (socket.getPort() == IConstant.portc) {
				receive_node = "c";
			}else if (receive_ip == PNode.ip1) {
				receive_node = PNode.node1;
			}else {
				receive_node = PNode.node2;
			}
		}
        try {
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
  
    @Override
    public void run() {
        while(true){
        	try {
				String msg = (String)ois.readObject();
				CallBackManager.getCallBack().receive_handler(receive_node, msg);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}