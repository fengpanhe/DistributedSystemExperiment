package work;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CNode{
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
	ReceiveThreadManager receive;
	HashMap<String, Snap> snap_all;
	static String ip_i, ip_j, ip_k;
	ObjectOutputStream oos_i, oos_j, oos_k;

	public CNode(String ip1, String ip2, String ip3) {
		ip_i = ip1;
		ip_j = ip2;
		ip_k = ip3;
		start_receive();
		set_callbackc();
	}
	
	private void start_send() {
		try {
			oos_i = new ObjectOutputStream(new Socket(ip_i, IConstant.portc).getOutputStream());
			oos_j = new ObjectOutputStream(new Socket(ip_j, IConstant.portp).getOutputStream());
			oos_k = new ObjectOutputStream(new Socket(ip_k, IConstant.portp).getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void set_callbackc() {
		CallBackManager.setCallBackc(new ICallBack() {
			
			@Override
			public void receive_handler(String node, String msg) {
				String[] src = msg.split("|");
				/*这边接收是需要收到3个节点的信息之后才能打印，所以我建议声明一个记录的类，
				 * 然后要使用map<key, 类>来记录，
				 * 每收到一个节点送回的快照就按快照编号key修改对应类中的一个标记，
				 * 到时候只要从map中取出来就可以了*/
				if (snap_all.containsKey(src[0])) {
					if (node.equals("i")) {
						//加入i的队列
					}else if(node.equals("j")){
						//加入j的队列
					}else {
						//加入k的队列
					}
				}else{
					if (node.equals("i")) {
						//加入i的队列
					}else if(node.equals("j")){
						//加入j的队列
					}else {
						//加入k的队列
					}
				}
				
			}
		});
	}
	
	class Snap{
		/*
		 * 具体要使用那些你要考虑清楚，我只写一些我觉得需要的
		 *  不够你再加
		 */
		String snap_i, snap_j, snap_k;
		public String getSnap_i() {
			return snap_i;
		}
		public void setSnap_i(String snap_i) {
			this.snap_i = snap_i;
		}
		public String getSnap_j() {
			return snap_j;
		}
		public void setSnap_j(String snap_j) {
			this.snap_j = snap_j;
		}
		public String getSnap_k() {
			return snap_k;
		}
		public void setSnap_k(String snap_k) {
			this.snap_k = snap_k;
		}
		public boolean isRecord_i() {
			return record_i;
		}
		public void setRecord_i(boolean record_i) {
			this.record_i = record_i;
		}
		public boolean isRecord_j() {
			return record_j;
		}
		public void setRecord_j(boolean record_j) {
			this.record_j = record_j;
		}
		public boolean isRecord_k() {
			return record_k;
		}
		public void setRecord_k(boolean record_k) {
			this.record_k = record_k;
		}
		boolean record_i = false, record_j = false, record_k = false;
	}
	
	public void send_event(String node, String msg) {
		/*在main函数中调用该函数向node，发送消息msg*/
		if (node.equals("i")) {
			tPoolExecutor.execute(new Send(oos_i, msg, 0));
		}else if(node.equals("j")){
			tPoolExecutor.execute(new Send(oos_j, msg, 0));
		}else {
			tPoolExecutor.execute(new Send(oos_k, msg, 0));
		}
	}
	
	private void start_receive() {
		receive = new ReceiveThreadManager(IConstant.portc, true);
		new Thread(receive).start();
	}
	
	public static void main(String[] args) {
		/*接受输入:
			ip结点的IP
			source_times资源转移数
			snapshot_times快照次数
			R随机种子
		*/

		// 事件间隔-5ln(R)产生事件序列，方法如下
		// 当随机数 < source_times/(source_times+snapshot_times)时，产生资源转移事件
		// 否则，产生快照事件。
		// 建议自己生成一个叫Event的内部类来表示，使用Event event[]数组保存事件信息

		/*
			这部分需要使用已经生成的event[]，向PNode发送事件，P、C结点通信使用Socket。
			注意的是，这里的C结点会与其他任何结点(ijk)连接，不仅仅是与它所在的机器上的结点相连
			通信消息格式请见PPT
		*/

		// 生成快照标准答案
		// 以及接受从P来的结果
	}
}