package server;

public class ServerMain {
	public static void main(String[] args) {
		
		ServerThread serverThread = new ServerThread();
		serverThread.start();
	}
}
