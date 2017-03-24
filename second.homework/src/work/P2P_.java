package work;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author acer
 * 
 */
public class P2P_ {
	private int M = 100;
	private Receive rec_thread1;
	private Send send_thread1;
	private Receive rec_thread2;
	private Send send_thread2;
	private static String pc_id;
	enum pc_name{i, j, k};
	
	public P2P_(String ip1,String ip2, int send_port, int rec_port, String target_id1, String target_id2) {
		rec_thread1 = new Receive(rec_port, target_id1);
		send_thread1 = new Send(ip1, send_port, target_id1);
		rec_thread2 = new Receive(rec_port, target_id2);
		send_thread2 = new Send(ip2, send_port, target_id2);
	}
	
	/**
	 * 启动接受服务,定义在P2P中
	 */
	public void start_recieve(){
		new Thread(rec_thread1).start();
		new Thread(rec_thread2).start();
	}
	
	/**
	 * 启动发送服务,定义在P2P中
	 */
	public void send_event() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @author acer
	 * 定义内部接受类
	 */
	class Receive implements Runnable{
		Integer trans;
		String receive_id = null;
		Socket socket = null;
		ObjectInputStream ois = null;
		public Receive(int port, String receive_id) {
			this.receive_id = receive_id;
			Thread.currentThread();
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				socket = serverSocket.accept();
				ois =  new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			int receive_num = 0;
			
			while(true){
				
				try {
					Integer temp = (Integer) ois.readObject();
					trans = temp;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				receive_num++;
				print_source();
				if (receive_num == 5) {
					break;
				}
	              
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
		Integer trans;
		ObjectOutputStream oos = null;
		String send_id;
		Socket socket = null;
		public Send(String ip, int port, String send_id) {
			this.send_id = send_id;
			Thread.currentThread();
			try {
				socket = new Socket(ip, port);
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			print_source();
			try {
				Thread.sleep(500);
				oos.writeObject(trans);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void print_source(){
			/*打印接受信息*/
		}
		
		public void setTrans(Integer trans) {
			this.trans = trans;
		}

		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String ip[] = new String[2];
		String target[] = new String[2];
		System.out.print("请输入随机数种子： ");
        int random = in.nextInt();
        System.out.print("请输入本机编号：");
        pc_id = in.next();
        int i = 0;
        for (pc_name pc : pc_name.values()) {
			if (pc_id.equals(pc.toString())) {
				continue;
			}
			System.out.print("请输入接受者"+ pc.toString() + "的ip： ");
			ip[i] = in.next();
			target[i] = pc.toString(); 
		}
        P2P_ p2p = new P2P_(ip[0], ip[1], IConstant.PORT, IConstant.PORT, target[0], target[1]);
        System.out.println("参数输入完成，启动recevie");
        p2p.start_recieve();
        System.out.println("recevie启动完成");
        
        System.out.print("输入y启动send： ");
        String make_sure = in.next();
        if (!make_sure.equals("y")) {
			return;
		}
        
        p2p.send_event();
        
	}


}
