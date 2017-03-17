package First.LearnCSMode.client;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import First.LearnCSMode.Message;

public class SocketThread extends Thread{
	private Socket client_socket = null;
    private ObjectOutputStream os = null;
    private Message message;
	public SocketThread(String host, int port) {
		try {
			client_socket = new Socket(host, port);
			os = new ObjectOutputStream(client_socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setMessage(Message message){
		this.message = message;
	}
	public  Message getMessage(){
		return message;
	}
	
	@Override
	public void run() {
		try {
			Message message = getMessage();
			os.writeObject(message);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  void socketClose(){
		try {
			os.close();
			client_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
