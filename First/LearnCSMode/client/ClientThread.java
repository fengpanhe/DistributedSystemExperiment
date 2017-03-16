package client;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Message;

public class ClientThread extends Thread{
	private Socket client_socket;
    private ObjectOutputStream os1;
    private int dead_time, nums = 20, num_0 = 15, num_1 = 5;
	public ClientThread(String host, int port) {
		try {
			client_socket = new Socket(host, port);
			os1 = new ObjectOutputStream(client_socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDeadTime(int dead_time) {
		this.dead_time = dead_time;
	}
	
	@Override
	public void run() {
		Message message = new Message();
		message.setRandom1(100);
		Thread.currentThread();
		for (int i = 0; i < nums; i++) {
			
			try {
				Thread.sleep(1000);
				os1.writeObject(message);
//				os2.writeObject(message);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
