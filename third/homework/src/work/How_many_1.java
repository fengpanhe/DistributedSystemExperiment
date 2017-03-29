package work;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by he on 17-3-29.
 */
public class How_many_1 implements Runnable{
    private Integer M = 100;
    private Send send_thread1;
    private String sendIp1;
    private String sendId1;
    private Send send_thread2;
    private String sendIp2;
    private String sendId2;
    private static String pc_id;
    private int recport = 999;
    enum  pc_name{i, j, k};
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Formatter f = new Formatter(System.out);
    private String formatStr = "%-10s %-10s %-10s %-10s %-30s\n";

    public How_many_1(String ip1, String ip2, int send_port1, int send_port2, int rec_port, String target_id1, String target_id2) {
        this.sendIp1 = ip1;
        this.sendId1 = target_id1;
        this.sendIp2 = ip2;
        this.sendId2 = target_id2;
        this.recport = rec_port;
        send_thread1 = new Send(ip1, send_port1, target_id1);
        send_thread2 = new Send(ip2, send_port2, target_id2);
    }

    //修改M值的函数,加锁
    private synchronized Integer changeRes(char action,Integer res){
        if(action == '-'){
            this.M = this.M - res;
        }else{
            this.M = this.M + res;
        }
        return this.M;
    }
    /**
     * 启动接受服务,定义在P2P中
     */
    public void start_recieve(){
        new Thread(this).start();
    }

    /**
     * 服务端的监听
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.recport);
            Socket socket=null;
            int count=0;
            //监听两次,创建两个SOCKET
            while (count<2){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();
                //创建一个新的线程
                Receive serverThread=new Receive(socket);
                //启动线程
                new Thread(serverThread).start();
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 启动发送服务,定义在P2P中
     */
    public void send_event(int seed) {
        send_thread1.createSocket();
        send_thread2.createSocket();
        Random random = new Random(seed);
        int counterOne = 0,counterZero = 0;
        int result;
        long time1 = System.currentTimeMillis();
        long time2 = System.currentTimeMillis();
        double R;
        double T;
        for(int i = 0,j=0; i < 100 | j<10;) {
            if(time1 <= System.currentTimeMillis()){
                R = random.nextDouble();
                T = Math.log(R) * 1000;
                time1 += T;
                /*
                **result
                */
                if (counterZero == 50) {
                    result = 1;
                    counterOne++;
                } else if (counterOne == 50) {
                    result = 0;
                    counterZero++;
                } else {
                    if (R < 0.5) {
                        result = 1;
                        counterOne++;
                    } else {
                        result = 0;
                        counterZero++;
                    }
                }
                if(result == 1){
                    Integer code = 10;
                    Integer tmp = changeRes('-', code);
                    f.format(formatStr, 0, this.sendId1, code, tmp, df.format(new Date()));
                    send_thread1.sendEvent(code,(long) T);
                }else{
                    Integer code = 10;
                    Integer tmp = changeRes('-', code);
                    f.format(formatStr, 0, this.sendId1, code, tmp, df.format(new Date()));
                    send_thread2.sendEvent(code,(long) T);
                }
                i++;
            }
            if(time2 <= System.currentTimeMillis()){
                R = random.nextDouble();
                T = 9 * Math.log(R) * 1000;
                time2 += T;
                Integer code = 10;
                Integer tmp = changeRes('-', code);
                f.format(formatStr, 0, this.sendId1, code, tmp, df.format(new Date()));
                send_thread1.sendEvent(code,(long) T);
                send_thread2.sendEvent(code,(long) T);
                j++;
            }

        }
    }

    /**
     * @author acer
     * 定义内部接受类
     */
    class Receive implements Runnable{
        String clientId;
        Integer trans;
        String receive_id = null;
        Socket socket = null;
        ObjectInputStream ois = null;


        public Receive(Socket socket) {
            this.socket = socket;
            String clientip = this.socket.getRemoteSocketAddress().toString().split(":|/")[1];
            this.clientId = clientip.equals(sendIp1)?  sendId1 :  sendId2;
        }
        private void print_source(){
			/*打印接受信息*/
            Integer tmp = changeRes('+',this.trans);
            f.format(formatStr, 1, this.clientId, trans, tmp, df.format(new Date()));
        }
        @Override
        public void run() {
            int receive_num = 0;

            try {
                ois =  new ObjectInputStream(socket.getInputStream());
                while(true){
                    Integer temp = (Integer) ois.readObject();
                    this.trans = temp;
                    receive_num++;
                    print_source();

                    if (receive_num == 5) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally{
                //关闭资源
                try {
                    if(ois!=null)
                        ois.close();
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author acer
     * 定义内部发送类
     */
    class Send implements Runnable{
        Integer code;  //要发送的资源数
        long waitTime;
        String sendId;
        String sendIp;
        int sendPort;
        ObjectOutputStream oos = null;
        Socket socket = null;

        public Send(String ip, int port, String sendId) {
            this.sendId = sendId;
            this.sendIp = ip;
            this.sendPort = port;

        }

        public  void createSocket(){
            Thread.currentThread();
            try {
                socket = new Socket(this.sendIp, this.sendPort);
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendEvent(Integer code, long waitTime){
            this.code = code;
            this.waitTime = waitTime;
            this.run();
        }
        private void sendWait(){
            try {
                Thread.sleep((long) this.waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
                this.sendWait();
//            print_source();
            try {
                Thread.sleep(500);
                oos.writeObject(this.code);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

//        private void print_source() {
//			/*打印发送信息*/
//            this.trans = M / 4;
//            Integer tmp = changeRes('-', this.trans);
//            f.format(formatStr, 0, this.sendId, this.trans, tmp, df.format(new Date()));
//        }
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
            i++;
        }
        How_many_1 p2p = new How_many_1(ip[0], ip[1], IConstant.sendPORT1,IConstant.sendPORT2, IConstant.receivePORT, target[0], target[1]);
        System.out.println("参数输入完成，启动recevie");
        p2p.start_recieve();
        System.out.println("recevie启动完成");

        System.out.print("输入y启动send： ");
        String make_sure = in.next();
        if (!make_sure.equals("y")) {
            return;
        }
        Formatter f = new Formatter(System.out);
        String formatStr = "%-10s %-10s %-10s %-10s %-30s\n";
        f.format(formatStr, "code", "name", "trans", "total", "timer");
        p2p.send_event(random);

    }


}
