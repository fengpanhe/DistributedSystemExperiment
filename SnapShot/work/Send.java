package work;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class Send implements Runnable{
    String msg;
    int deny;
    ObjectOutputStream oos = null;

    public Send(ObjectOutputStream oos, String msg, int deny) {
    	this.msg = msg;
    	this.deny = deny;
    	this.oos = oos;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(deny);
            oos.writeObject(msg);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}