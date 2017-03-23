package work;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author acer
 * 
 */
public class P2P_ {
	private int M = 100;
	private Receive rec_thread;
	private Send send_thread;
	enum pc_name{i, j, k};
	
	public P2P_(String ip, int send_port, int rec_port) {
		rec_thread = new Receive(rec_port);
		send_thread = new Send(ip, send_port);
		
	}
	
	/**
	 * @author acer
	 * 定义内部接受类
	 */
	class Receive implements Runnable{
		int trans;
		Socket socket = null;
		public Receive(int port) {
			Thread.currentThread();
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			while(true){
				print_source();
			}
		}
		
		private void print_source(){
			/*打印接受信息*/
		}
		
	}
	
	/**
	 * @author acer
	 * 定义内部发送类
	 */
	class Send implements Runnable{
		int trans;
		Socket socket = null;
		public Send(String ip, int port) {
			Thread.currentThread();
			try {
				socket = new Socket(ip, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			print_source();
		}
		
		private void print_source(){
			/*打印接受信息*/
		}
		
	}
	
	/**
	 * 启动接受服务,定义在P2P中
	 */
	public void start_recieve(){
		new Thread(rec_thread).start();;
	}
	
	/**
	 * 启动发送服务,定义在P2P中
	 */
	public void start_send(){
		new Thread(send_thread).start();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ip1 = null, ip2 = null;
		P2P_ p2p_1 = new P2P_(ip1, IConstant.PORT, IConstant.PORT);
		P2P_ p2p_2 = new P2P_(ip2, IConstant.PORT, IConstant.PORT);
	}
}
