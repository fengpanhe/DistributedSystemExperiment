package work;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class How_many_1{
	ReceiveThreadManager receive;
	Send send1 = null;
	Send send2 = null;
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();//线程池管理发送线程
	static String node_name, node1, node2;
	static String ip1, ip2;
	Integer M = 300, recordNum;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Formatter f = new Formatter(System.out);
    private String formatStr = "%-5s %-1s:%-5s %-1s:%-5s %-1s:%-5s"
			+ " total:%-5s startTime:%-15s endTime:%-15s\n";
    private Logger logger = Logger.getLogger("test");
	private FileHandler fileHandler;
	private Queue<String> startTime = new LinkedList<String>();
	private int num = 0, num_print = 0;
	private String pre_id;
	
	public How_many_1(String node_name, String node1, String node2, String ip1, String ip2) {
	    How_many_1.node_name = node_name;
		How_many_1.node1 = node1;
		How_many_1.node2 = node2;
		How_many_1.ip1 = ip1;
		How_many_1.ip2 = ip2;
		CallBackManager.setCallBackP(new ICallBack() {
			
			@Override
			public void receive_handler(Integer src, String id) {
				if(send1 == null) send1 = new Send(ip1, IConstant.PORT, node1, IConstant.DENY);
				if(send2 == null) send2 = new Send(ip2, IConstant.PORT, node2, IConstant.DENY);
				if (src < 1024) {
                	changeRes('+',src);
                	print_log(new Date(), id, "1", "00", src, M);
				}else if (src >= 2048) {
					print_source(src, id);
				}else {
					print_log(new Date(), id, "1", "01", src, 0);
					if (id.equals(node1)) {
						send1.sendEvent(M+2048, 0);
						tPoolExecutor.execute(send1);
					}
					else {
						send2.sendEvent(M+2048, 0);
						tPoolExecutor.execute(send2);
					}
						
				}
			}
		});
		setlog();
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
		}else if (trans >= 2048) {
			trans -= 2048;
			logger.info(action + " " + pc_name + " " + code + " " + df.format(date) + " " + trans.toString() + " " + "0");
		}else {
			trans -= 1024;
			logger.info(action + " " + pc_name + " " + code + " " + df.format(date) + " " + "0" + " " + "0");
		}
		
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
					String format = "%-3s %-3s %-4s %-5s %-5s %-25s\r\n";
					return String.format(format, rStrings[0], rStrings[1],
							rStrings[2],rStrings[4], rStrings[5], rStrings[3]);
				}
			});
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		};
	}
	
	private synchronized void print_source(Integer trans, String id) {
		num++;
		if (num != 2) {
			recordNum = trans - 2048;
			pre_id = id;
			return;
		}
		num_print++;
		trans -= 2048;
		Integer all = M+recordNum+trans;
		f.format(formatStr, num_print, node_name, M.toString(),
				pre_id, recordNum.toString(), id, trans.toString(), all.toString(),
				startTime.poll(), df.format(new Date()));
		num = 0;
	}
	
	private void start_receive() {
		receive = new ReceiveThreadManager();
		new Thread(receive).start();
	}
	
	public void closeThread() {
		while(this.tPoolExecutor.getActiveCount() != 0);
		tPoolExecutor.shutdown();
	}
	
	public void start_send(int seed) {
		if(send1 == null) send1 = new Send(ip1, IConstant.PORT, node1, IConstant.DENY);
		if(send2 == null) send2 = new Send(ip2, IConstant.PORT, node2, IConstant.DENY);

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
                T = -Math.log(R) * 1000;
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
                    code = M / 5;
                    tmp = changeRes('-', code);
                    print_log(new Date(), node1, "0", "00", code, tmp);
                    send1.sendEvent(code,(long) T);
                    tPoolExecutor.execute(send1);
                }else{
                    code = M / 4;
                    tmp = changeRes('-', code);
                    print_log(new Date(), node2, "0", "00", code, tmp);
                    send2.sendEvent(code,(long) T);
                    tPoolExecutor.execute(send2);
                }
                i++;
            }
            if(time2 <= System.currentTimeMillis()){
                R = random.nextDouble();
                T = -9 * Math.log(R) * 1000;
                time2 += T;
                code = 1024;
                startTime.add(df.format(new Date()));
                send1.sendEvent(code,(long) T);
                send2.sendEvent(code,(long) T);
                tPoolExecutor.execute(send1);
                tPoolExecutor.execute(send2);
                j++;
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
        String pc_id = in.next();
        
        switch (pc_id) {
		case "i":
			System.out.print("请输入接受者j的ip： ");
			ip[0] = in.next();target[0] = "j";
			System.out.print("请输入接受者k的ip： ");
			ip[1] = in.next();target[1] = "k";
			break;
		case "j":
			System.out.print("请输入接受者i的ip： ");
			ip[0] = in.next();target[0] = "i";
			System.out.print("请输入接受者k的ip： ");
			ip[1] = in.next();target[1] = "k";
			break;
		case "k":
			System.out.print("请输入接受者i的ip： ");
			ip[0] = in.next();target[0] = "i";
			System.out.print("请输入接受者j的ip： ");
			ip[1] = in.next();target[1] = "j";
			break;
		default:
			break;
		}
        How_many_1 how = new How_many_1(pc_id, target[0], target[1], ip[0], ip[1]);
        System.out.println("参数输入完成，启动recevie");
        how.start_receive();
        System.out.println("recevie启动完成");

        System.out.print("输入y启动send： ");
        String make_sure = in.next();
        if (!make_sure.equals("y")) {
            return;
        }
        how.start_send(random);
        how.closeThread();
        how.receive.closeAllThread();
	}

}