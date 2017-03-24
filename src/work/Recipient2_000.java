package work;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class Recipient2_000 extends Thread{

    public static void main(String[] args) {
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket=new ServerSocket(999);
            Socket socket=null;
            //记录客户端的数量
            int count=0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接

            //调用accept()方法开始监听，等待客户端的连接
            socket=serverSocket.accept();
            //创建一个新的线程
            Recipient2_000 serverThread=new Recipient2_000(socket);
            //启动线程
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 和本线程相关的Socket
    Socket socket = null;

    public Recipient2_000(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is=null;
        OutputStream os=null;
        PrintWriter pw=null;
        ObjectInputStream ois = null;

        Formatter f = new Formatter(System.out);
        String formatStr ="%-10s %-10s %-10s %-30s %-30s\n";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("端对端模式: receive");
        f.format(formatStr, "NO", "random1", "random2", "timer","localTime");
        try {
            //获取输入流，并读取客户端信息
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
            Message message = null;
            int i = 0;
            int i_1 = 0;
            int i_0 = 0;
            while ((message = (Message) ois.readObject()) != null) {//循环读取客户端的信息
                f.format(formatStr, ++i, message.getRandom1(), message.getRandom2(), message.getTimer(), df.format(new Date()));
                if (message.getRandom1() == 0) {
					i_0 ++;
				}else {
					i_1 ++;
				}
                if(i_0 == 15|| i_1 == 5 || message.getRandom1()==2){
                    break;
                }
            }

            System.out.println("receive END");
            if(message.getRandom1()==2){
                System.out.println("reason: random1 == 2");
            }else if (i_0 == 15) {
            	System.out.println("reason: num(0) == 15");
			}else {
				System.out.println("reason: num(1) == 5");
			}
                
            socket.shutdownInput();//关闭输入流
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
            //关闭资源
            try {
                if(pw!=null)
                    pw.close();
                if(os!=null)
                    os.close();
                if(ois!=null)
                    ois.close();
                if(is!=null)
                    is.close();
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
