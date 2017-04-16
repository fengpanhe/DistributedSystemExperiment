
package work;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PNode {
	int M = 300;
	ReceiveThreadManager receive;// 接受线程管理
	int deny1, deny2;
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();// 线程池管理发送线程
	static String node_name, node1, node2;// node_name为本节点名称，node1为第一个节点名称，node2同上
	static String ipc, ip1, ip2;// ipc为控制节点的ip，ip1为第一个节点的IP地址，ip2同上
	ObjectOutputStream oos_c, oos_1, oos_2;
	HashMap<String, SnapRecord> records = new HashMap<>();// 记录
	int snap_num = 0;// 每有一次快照就 +1，执行完就把snap_num - 1，用于判断是否处在snap阶段
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

	private void start_send() {
		try {
			oos_c = new ObjectOutputStream(new Socket(ipc, IConstant.portc).getOutputStream());
			oos_1 = new ObjectOutputStream(new Socket(ip1, IConstant.portp).getOutputStream());
			oos_2 = new ObjectOutputStream(new Socket(ip2, IConstant.portp).getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					String rStrings = record.getMessage() + "		" + df.format(new Date()) + "\r\n";
					return rStrings;
				}
			});
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		;
	}

	private void set_callback() {
		CallBackManager.setCallBack(new ICallBack() {

			@Override
			public void receive_handler(String node, String msg) {
				String[] src = msg.split("\\|");
				print_logger(msg);
				if (src[0].equals("1")) {
					handle_srcc(src[1], src[2]);
				} else if (src[0].equals("2")) {
					handle_snapc(src[1]);
				} else if (src[0].equals("3")) {
					/*
					 * 正常资源处理 如果处在快照的通道监听阶段，还要修改SnapRecord
					 */
					handle_source(src[1]);
					if (snap_num != 0)
						handle_snap(node, msg);
				} else if (src[0].equals("4")) {
					/*
					 * 如果是快照消息 根据自己是否记录过了自己的资源来监听通道
					 */
					handle_snap(node, msg);
				} else if (src[0].equals("5")) {
					handle_end();
				}
			}
		});
	}

	private void handle_end() {
		while (tPoolExecutor.getActiveCount() != 0)
			;
		tPoolExecutor.shutdown();
		receive.closeAllThread();
	}

	private void handle_snap_p(String id) {
		records.get(id).src = M;
		String msg = "4|" + id;
		tPoolExecutor.execute(new Send(oos_1, msg, deny1));
		tPoolExecutor.execute(new Send(oos_2, msg, deny2));
	}

	private void handle_snap_sendc(String id) {
		System.out.println("sendc");
		String src, src_1, src_2, send;
		src = String.valueOf(records.get(id).src);
		src_1 = String.valueOf(records.get(id).src_1);
		src_2 = String.valueOf(records.get(id).src_2);

		if (node_name.equals("i")) {
			send = id + "|" + src + "|0|0|0|" + src_1 + "|0|" + src_2 + "|0|0";
		} else if (node_name.equals("j")) {
			send = id + "|0|" + src + "|0|" + src_1 + "|0|0|0|0|" + src_2;
		} else {
			send = id + "|0|0|" + src + "|0|0|" + src_1 + "|0|" + src_2 + "|0";
		}
		tPoolExecutor.execute(new Send(oos_c, send, 0));
		records.remove(id);
		System.out.println("send end");
	}

	private void handle_snapc(String id) {
		records.put(id, new SnapRecord(id));
		records.get(id).listen_1 = true;
		records.get(id).listen_2 = true;
		handle_snap_p(id);
	}

	private void handle_snap(String node, String msg) {
		String[] src = msg.split("\\|");
		if (src[0].equals("4")) {
			String id = src[1];
			if (!records.containsKey(id)) {
				records.put(id, new SnapRecord(id));
				if (node.equals(node1))
					records.get(id).listen_2 = true;
				if (node.equals(node2))
					records.get(id).listen_1 = true;
				handle_snap_p(id);
			} else {
				if (node.equals(node1))
					records.get(id).listen_1 = false;
				if (node.equals(node2))
					records.get(id).listen_2 = false;
			}
			if (!records.get(id).listen_1 && !records.get(id).listen_2)
				handle_snap_sendc(id);
		}
		if (src[0].equals("3")) {
			int num = Integer.parseInt(src[1]);
			operate_records(node, num);
		}
	}

	private void handle_srcc(String node, String src) {
		if (node.equals(node1)) {
			change_source(false, Integer.valueOf(src).intValue());
			tPoolExecutor.execute(new Send(oos_1, "3|" + src, deny1));
		} else {
			change_source(false, Integer.valueOf(src).intValue());
			tPoolExecutor.execute(new Send(oos_2, "3|" + src, deny1));
		}
	}

	private void handle_source(String src) {
		change_source(true, Integer.valueOf(src).intValue());
	}

	private synchronized void change_source(boolean op, int intValue) {
		if (op) {
			M += intValue;
		} else {
			M -= intValue;
		}
	}

	private synchronized void print_logger(String msg) {
		logger.info(msg);
	}

	public synchronized void operate_records(String node, int num) {
		// 操作队列
		for (String key : records.keySet()) {
			if (node == node1) {
				if (records.get(key).listen_1)
					records.get(key).src_1 += num;
			}
			if (node == node2) {
				if (records.get(key).listen_2)
					records.get(key).src_2 += num;
			}
		}
	}

	private void start_receive() {
		receive = new ReceiveThreadManager(IConstant.portp, false);
		new Thread(receive).start();
	}

	class SnapRecord {
		String id_snap;
		int src, src_1, src_2;
		boolean listen_1, listen_2;

		public SnapRecord(String id_snap) {
			this.src = 0;
			this.src_1 = 0;
			this.src_2 = 0;
			this.id_snap = id_snap;
			this.listen_1 = false;
			this.listen_2 = false;
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
		/*
		 * 接受输入: node_name节点名 ipc节点C的IP ip1第一个P节点的IP ip2第二个P节点的IP
		 */
		Scanner in = new Scanner(System.in);
		String ip[] = new String[3];
		String target[] = new String[2];
		System.out.print("请输入本机编号：");
		String pc_id = in.next();
		System.out.println("请输入控制节点C的ip：");
//		ip[0] = in.next();
		ip[0] = "192.168.1.235";
		switch (pc_id) {
		case "i":
			System.out.print("请输入接受者j的ip： ");
//			ip[1] = in.next();
			ip[1] = "192.168.1.146";
			target[0] = "j";
			System.out.print("请输入接受者k的ip： ");
//			ip[2] = in.next();
			ip[2] = "192.168.1.235";
			target[1] = "k";
			break;
		case "j":
			System.out.print("请输入接受者i的ip： ");
//			ip[1] = in.next();
			ip[1] = "192.168.1.167";
			target[0] = "i";
			System.out.print("请输入接受者k的ip： ");
//			ip[2] = in.next();
			ip[2] = "192.168.1.235";
			target[1] = "k";
			break;
		case "k":
			System.out.print("请输入接受者i的ip： ");
//			ip[1] = in.next();
			ip[1] = "192.168.1.167";
			target[0] = "i";
			System.out.print("请输入接受者j的ip： ");
//			ip[2] = in.next();
			ip[2] = "192.168.1.146";
			target[1] = "j";
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
		pNode.start_send();
	}

}