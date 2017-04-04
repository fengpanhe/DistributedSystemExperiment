package work;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReceiveThreadManager implements Runnable{
	
	ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	ServerSocket server;
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
            while (!server.isClosed()) {
            	poolExecutor.execute(new Receive(server.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}