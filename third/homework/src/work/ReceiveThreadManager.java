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
	int num = 0;
	public ReceiveThreadManager() {
		try {
			server = new ServerSocket(IConstant.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeAllThread() {
		while(poolExecutor.getActiveCount() != 0);
		try {
			server.close();
			poolExecutor.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try{
            while (num < 2) {
            	poolExecutor.execute(new Receive(server.accept()));
            	num ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}