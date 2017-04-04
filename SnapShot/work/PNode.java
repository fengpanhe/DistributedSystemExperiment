package work;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PNode{
	ReceiveThreadManager receive;//接受线程管理
	Send sendc;//发送给C节点的线程
	Send send1;//发送给第一个节点的线程
	Send send2;//发送给第二个节点的线程
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();//线程池管理发送线程
	String node_name, node1, node2;//node_name为本节点名称，node1为第一个节点名称，node2同上
	String ipc, ip1, ip2;//ipc为控制节点的ip，ip1为第一个节点的IP地址，ip2同上
	HashMap<String, SnapRecord> records;//记录
	int snap_num = 0;//每有一次快照就 +1，执行完就把snap_num - 1，用于判断是否处在snap阶段
	
	public PNode(String node_name, String node1, String node2, String ipc, String ip1, String ip2) {
		this.node_name = node_name;
		this.node1 = node1;
		this.node2 = node2;
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.ipc = ipc;
		start_receive();
		set_callback();
	}
	
	private void set_callback() {
		CallBackManager.setCallBackP(new ICallBack() {
			
			@Override
			public void receive_handler(String msg) {
				/*正常资源处理
				 * 如果处在快照的通道监听阶段，还要修改SnapRecord
				 */
				
				/* 如果是快照消息
				 * 根据自己是否记录过了自己的资源来监听通道
				 */
			}
		});
		
		CallBackManager.setCallBackC(new ICallBack() {
			
			@Override
			public void receive_handler(String msg) {
				//处理从C节点来的信息
				//如果是资源转移，处理
				/* 如果是快照消息
				 * 记录当前的资源数
				 * 监听另外两条接受的通道，也就是说，这个时间点之后的接收的资源记录下来
				 * 并且向其他两条通道发送快照消息
				 */
			}
		});
		
	}
	
	public synchronized SnapRecord operate_records(Object ...objects){
		//操作队列
		return null;
	}
	
	public void start_send() {
		sendc = new Send(ipc, IConstant.portc, "c", 0);
		send1 = new Send(ip1, IConstant.portp, node1,通道延迟);
		send2 = new Send(ip2, IConstant.portp, node2,通道延迟);
	}
	
	private void start_receive() {
		receive = new ReceiveThreadManager(0, IConstant.portp);
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
		
		PNode pNode = new PNode(参数);
		pNode.start_send();
	}

}