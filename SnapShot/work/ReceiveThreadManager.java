package work;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReceiveThreadManager implements Runnable{
	
	ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	ServerSocket server;
<<<<<<< HEAD
	int level = 0;//0表示P节点，1为C节点
	
	public ReceiveThreadManager(int level, int port) {
		this.level = level;
		try {
			server = new ServerSocket(port);
=======
	int connect_num = 0;
	boolean level = false;
	public ReceiveThreadManager(int port, boolean level) {
		try {
			server = new ServerSocket(port);
			this.level = level;
>>>>>>> cd237ef5ef1384b4238b029c950007393b77bfbb
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeAllThread() {
		try {
			server.close();
			poolExecutor.shutdownNow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try{
<<<<<<< HEAD
            while (!server.isClosed()) {
            	poolExecutor.execute(new Receive(server.accept(), level));
=======
            while (connect_num < 3) {
            	poolExecutor.execute(new Receive(server.accept(), level));
            	connect_num ++;
>>>>>>> cd237ef5ef1384b4238b029c950007393b77bfbb
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}