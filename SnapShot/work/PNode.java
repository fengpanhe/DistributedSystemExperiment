package work;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PNode{
	int M = 300;
	ReceiveThreadManager receive;//接受线程管理
	int deny1, deny2;
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();//线程池管理发送线程
	static String node_name, node1, node2;//node_name为本节点名称，node1为第一个节点名称，node2同上
	static String ipc, ip1, ip2;//ipc为控制节点的ip，ip1为第一个节点的IP地址，ip2同上
	HashMap<String, SnapRecord> records;//记录
	int snap_num = 0;//每有一次快照就 +1，执行完就把snap_num - 1，用于判断是否处在snap阶段
	private Logger logger = Logger.getLogger("log");
	private FileHandler fileHandler;
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
	
	public PNode(String node_name, String node1, String node2, String ipc, String ip1, String ip2) {
		PNode.node_name = node_name;
		PNode.node1 = node1;
		PNode.node2 = node2;
		PNode.ip1 = ip1;
		PNode.ip2 = ip2;
		PNode.ipc = ipc;
		set_deny();
		start_receive();
		set_callback();
		set_log();
	}
	
	private void set_deny() {
		switch (node_name) {
		case "i":
			deny1 = IConstant.ij;
			deny2 = IConstant.ik;
			break;
		case "j":
			deny1 = IConstant.ji;
			deny2 = IConstant.jk;
			break;
		case "k":
			deny1 = IConstant.ki;
			deny2 = IConstant.kj;
			break;
		default:
			break;
		}
	}
	
	private void set_log() {
    	logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		try {
			fileHandler = new FileHandler("source.log");
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new java.util.logging.Formatter() {
				@Override
				public String format(LogRecord record) {
					String rStrings = record.getMessage() + "		" + df.format(new Date()) + "\r\n" ;
					return rStrings;
				}
			});
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		};
	}

	private void set_callback() {
		CallBackManager.setCallBack(new ICallBack() {
			
			@Override
			public void receive_handler(String node, String msg) {
				String[] src = msg.split("|");
				print_logger(msg);
				if (src[0].equals("1") ) {
					handle_srcc(src[1], src[2]);
				}else if (src[0].equals("2")) {
					handle_snapc(msg);
				}else if (src[0].equals("3")) {
					/*正常资源处理
					 * 如果处在快照的通道监听阶段，还要修改SnapRecord
					 */
					handle_source(src[1]);
					if (snap_num != 0) handle_snap(node, msg);
				}else if (src[0].equals("4")) {
					/* 如果是快照消息
					 * 根据自己是否记录过了自己的资源来监听通道
					 */
					handle_snap(node, msg);
				}else {
					handle_end();
				}
			}
		});
	}
	
	private void handle_end() {
		// TODO Auto-generated method stub
		while(tPoolExecutor.getActiveCount() != 0);
		tPoolExecutor.shutdown();
		receive.closeAllThread();
	}
	
	private void handle_snapc(String msg) {
		// TODO Auto-generated method stub
		
	}
	
	private void handle_snap(String node, String msg) {
		// TODO Auto-generated method stub
		
	}
	
	private void handle_srcc(String node, String src) {
		if (node.equals(node1)) {
			change_source(false, Integer.valueOf(src).intValue());
			tPoolExecutor.execute(new Send(ip, port, sendId, deny));
		}else{
			change_source(false, Integer.valueOf(src).intValue());
			tPoolExecutor.execute(new Send(ip, port, sendId, deny));
		}
	}

	private void handle_source(String src) {
		change_source(true, Integer.valueOf(src).intValue());
	}
	
	private synchronized void change_source(boolean op, int intValue) {
		if (op) {
			M += intValue;
		}else {
			M -= intValue;
		}
	}

	private synchronized void print_logger(String msg) {
		logger.info(msg);
	}

	
	public synchronized SnapRecord operate_records(Object ...objects){
		//操作队列
		return null;
	}
	
	
	private void start_receive() {
		receive = new ReceiveThreadManager(IConstant.portp, false);
		new Thread(receive).start();
	}
	
	class SnapRecord{
		String id_snap;
		int src, src_1, src_2;
		
		public SnapRecord(String id_snap, int src) {
			this.src = src;
			this.id_snap = id_snap;
		}
		public String getId_snap() {
			return id_snap;
		}
		public void setId_snap(String id_snap) {
			this.id_snap = id_snap;
		}
		public int getSrc() {
			return src;
		}
		public void setSrc(int src) {
			this.src = src;
		}
		public int getSrc_1() {
			return src_1;
		}
		public void setSrc_1(int src_1) {
			this.src_1 = src_1;
		}
		public int getSrc_2() {
			return src_2;
		}
		public void setSrc_2(int src_2) {
			this.src_2 = src_2;
		}
	}
	
	public static void main(String[] args) {
		/*接受输入:
			node_name节点名
			ipc节点C的IP
			ip1第一个P节点的IP
			ip2第二个P节点的IP
		*/
		Scanner in = new Scanner(System.in);
        String ip[] = new String[3];
        String target[] = new String[2];
        System.out.print("请输入本机编号：");
        String pc_id = in.next();
        System.out.println("请输入控制节点C的ip：");
        ip[0] = in.next();
        switch (pc_id) {
		case "i":
			System.out.print("请输入接受者j的ip： ");
			ip[1] = in.next();target[0] = "j";
			System.out.print("请输入接受者k的ip： ");
			ip[2] = in.next();target[1] = "k";
			break;
		case "j":
			System.out.print("请输入接受者i的ip： ");
			ip[1] = in.next();target[0] = "i";
			System.out.print("请输入接受者k的ip： ");
			ip[2] = in.next();target[1] = "k";
			break;
		case "k":
			System.out.print("请输入接受者i的ip： ");
			ip[1] = in.next();target[0] = "i";
			System.out.print("请输入接受者j的ip： ");
			ip[2] = in.next();target[1] = "j";
			break;
		default:
			break;
		}
        PNode pNode = new PNode(pc_id, target[0], target[1], ip[0], ip[1], ip[2]);
        System.out.println("参数输入完成，启动recevie");
        System.out.println("recevie启动完成");
        System.out.print("输入y启动send： ");
        String make_sure = in.next();
        if (!make_sure.equals("y")) {
            return;
        }
	}

}