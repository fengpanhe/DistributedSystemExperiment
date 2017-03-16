package client;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Message;

public class ClientThread extends Thread{
	private Socket client_socket1, client_socket2;
    private ObjectOutputStream os1;
    private ObjectOutputStream os2;
    private int dead_time, nums = 20, num_0 = 15, num_1 = 5;
	public ClientThread(String host1, int port1, String host2, int port2) {
		try {
			client_socket1 = new Socket(host1, port1);
//			client_socket2 = new Socket(host2, port2);
			os1 = new ObjectOutputStream(client_socket1.getOutputStream());
//			os2 = new ObjectOutputStream(client_socket2.getOutputStream());
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
