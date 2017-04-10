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
	int connect_num = 0;
	boolean level = false;
	public ReceiveThreadManager(int port, boolean level) {
		try {
			server = new ServerSocket(port);
			this.level = level;
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
            while (connect_num < 3) {
            	poolExecutor.execute(new Receive(server.accept(), level));
            	connect_num ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}