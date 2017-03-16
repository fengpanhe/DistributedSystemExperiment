package First.LearnCSMode.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.Message;

public class ServerThread extends Thread{
	private ServerSocket srv_socket;
	private Socket socket;
	private ObjectInputStream ois;
	
	public ServerThread() {
		try {
			srv_socket = new ServerSocket(999);
			socket = srv_socket.accept();
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		Message message = new Message();
		while (true) {
			try {
				 message = (Message) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(message.getRandom1());
		}
		
		
	}
	
}
