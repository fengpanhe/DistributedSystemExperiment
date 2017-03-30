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
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
    private String formatStr = "%-5s %-1s:%-5s %-1s:%-5s %-1s:%-5s"
			+ "%-5s:%-5s startTime:%-15s endTime:%-15s\n";
    private Logger logger = Logger.getLogger("test");
	private FileHandler fileHandler;
	private Queue<Date> startTime;
	private boolean lock = false;
	private Integer recordNum = 0;
	private int num = 0;
	private String clientid;

    public How_many_1(String ip1, String ip2, int send_port1, int send_port2, int rec_port, String target_id1, String target_id2) {
        this.sendIp1 = ip1;
        this.sendId1 = target_id1;
        this.sendIp2 = ip2;
        this.sendId2 = target_id2;
        this.recport = rec_port;
        send_thread1 = new Send(ip1, send_port1, target_id1);
        send_thread2 = new Send(ip2, send_port2, target_id2);
        setlog();
    }

    private void setlog() {
    	logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		try {
			fileHandler = new FileHandler("source.log");
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new java.util.logging.Formatter() {
				@Override
				public String format(LogRecord record) {
					String[] rStrings = record.getMessage().split(" ");
					String format = "%-3s %-3s %-4s %-5s %-5s %-15s\n";
					return String.format(format, rStrings[0], rStrings[1],
							rStrings[4],rStrings[2], rStrings[5], rStrings[3]);
				}
			});
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		};
	}

    private synchronized Integer changeRes(char action,Integer res){
        if(action == '-'){
            this.M = this.M - res;
        }else{
            this.M = this.M + res;
        }
        return this.M;
    }
    
    private synchronized void print_log(Date date, String pc_name, String action, String code, Integer trans, Integer M) {
		/*打印日志*/
    	if (trans < 1024) {
    		logger.info(action + " " + pc_name + " " + code + " " + df.format(date) + " " + trans.toString() + " " + M.toString());
		}else if (trans > 2048) {
			trans -= 2048;
			logger.info(action + " " + pc_name + " " + code + " " + df.format(date) + " " + trans.toString() + " " + "0");
		}else {
			trans -= 1024;
			logger.info(action + " " + pc_name + " " + code + " " + df.format(date) + " " + "0" + " " + "0");
		}
		
	}
    
    private synchronized void print_source(Integer trans, String id) {
		if (!lock) {
			lock = true;
			recordNum = trans;
			clientid = id;
		}else {
			lock = false;
			f.format(formatStr,num, pc_id, M.toString(),
					clientid, recordNum.toString(), id, trans.toString(),
					startTime.poll(), df.format(new Date()));
			num ++;
		}
	}
    public void start_recieve(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.recport);
            Socket socket=null;
            int count=0;
            while (count<2){
                socket=serverSocket.accept();
                Receive serverThread=new Receive(socket);
                new Thread(serverThread).start();
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send_event(int seed) {
        send_thread1.createSocket();
        send_thread2.createSocket();
        Integer tmp, code;
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
                    code = 10;
                    tmp = changeRes('-', code);
                    print_log(new Date(), this.sendId1, "0", "00", code, tmp);
                    send_thread1.sendEvent(code,(long) T);
                }else{
                    code = 10;
                    tmp = changeRes('-', code);
                    print_log(new Date(), this.sendId2, "0", "00", code, tmp);
                    send_thread2.sendEvent(code,(long) T);
                }
                i++;
            }
            if(time2 <= System.currentTimeMillis()){
                R = random.nextDouble();
                T = 9 * Math.log(R) * 1000;
                time2 += T;
                code = 1024;
                startTime.add(new Date());
                send_thread1.sendEvent(code,(long) T);
                send_thread2.sendEvent(code,(long) T);
                j++;
            }

        }
    }

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
      
        @Override
        public void run() {
            int receive_num = 0;

            try {
                ois =  new ObjectInputStream(socket.getInputStream());
                while(true){
                    Integer temp = (Integer) ois.readObject();
                    this.trans = temp;
                    if (trans < 1024) {
                    	temp = changeRes('+',this.trans);
                    	print_log(new Date(), this.clientId, "1", "00", trans, temp);
					}else if (trans >= 2048) {
						print_source(trans, this.clientId);
					}else {
						print_log(new Date(), this.clientId, "1", "01", trans, 0);
					}
                    receive_num++;
                    if (receive_num == 140) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally{
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
            try {
                Thread.sleep(500);
                oos.writeObject(this.code);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



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
        How_many_1 how = new How_many_1(ip[0], ip[1], IConstant.sendPORT1,IConstant.sendPORT2, IConstant.receivePORT, target[0], target[1]);
        System.out.println("参数输入完成，启动recevie");
        how.start_recieve();
        System.out.println("recevie启动完成");

        System.out.print("输入y启动send： ");
        String make_sure = in.next();
        if (!make_sure.equals("y")) {
            return;
        }
        how.send_event(random);

    }


}
