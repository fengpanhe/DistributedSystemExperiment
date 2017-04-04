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
	int level = 0;//0表示P节点，1为C节点
	
	public ReceiveThreadManager(int level, int port) {
		this.level = level;
		try {
			server = new ServerSocket(port);
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
            while (!server.isClosed()) {
            	poolExecutor.execute(new Receive(server.accept(), level));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}