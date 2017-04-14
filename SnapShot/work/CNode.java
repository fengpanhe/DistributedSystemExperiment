
package work;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CNode{
	ThreadPoolExecutor tPoolExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
	ReceiveThreadManager receive;
	HashMap<String, Snap> snap_all;
	static String ip_i, ip_j, ip_k;
	ObjectOutputStream oos_i, oos_j, oos_k;
	private Event events[] = null;
	CNodeSnapshot cNodeSnapshot[];

	Formatter fPrint = new Formatter(System.out);
	String formatStr = "%-30s %-30s\n";

	public CNode(String ip1, String ip2, String ip3) {
		ip_i = ip1;
		ip_j = ip2;
		ip_k = ip3;
		fPrint.format(formatStr,"标准快照","接收到的快照");
		start_receive();
		set_callbackc();
	}
	private static int ijkIndex(char a){
		char ijk[] = {'i','j','k'};
		for(int i=0;i<ijk.length;i++){
			if(a == ijk[i]){
				return i;
			}
		}
		return -1;
	}
	public void setEvents(int source_times, int snapshot_times, int randomSeed){
		this.events = new Event[source_times + snapshot_times + 1];
		this.cNodeSnapshot = new CNodeSnapshot[snapshot_times + 1];


		Random random = new Random(randomSeed);
		double R;
		double T;
		double source_times_rate = (double)source_times/(source_times + snapshot_times);
		long timeSum = 0;
		//产生所有事件
		char nodes[] = {'i','j','k'};
		for(int i = 0,j = 0, k = 0; k < source_times + snapshot_times; k++){
			R = random.nextDouble();
			T = -Math.log(R) * 5000;
			timeSum = timeSum + (long) T;
			Random random1 = new Random((long)T);
			if((R < source_times_rate || j >= snapshot_times) && i < source_times){
				// 资源转移事件
				char sendNode = nodes[random1.nextInt(3)];
				char recNode = 'i';
				int sourceNum = 10;
				if(sendNode == 'i'){
					char nodes1[] = {'j','k'};
					recNode = nodes1[random1.nextInt(2)];
				} else if(sendNode == 'j'){
					char nodes1[] = {'i','k'};
					recNode = nodes1[random1.nextInt(2)];
				} else if(sendNode == 'k'){
					char nodes1[] = {'i','j'};
					recNode = nodes1[random1.nextInt(2)];
				}
				this.events[k] = new Event(sendNode, recNode,10, timeSum);
				i++;
			} else if(j < snapshot_times){
				// 快照事件
				this.events[k] = new Event(String.valueOf(j),nodes[random1.nextInt(3)], 'c',timeSum);
				this.cNodeSnapshot[j] = new CNodeSnapshot(String.valueOf(j));
				j++;
			}
		}
		this.events[this.events.length - 1] = new Event(timeSum);
		this.setcNodeSnapshot();
	}
	private void setcNodeSnapshot(){
		PriorityQueue<Event> eventQueue = new PriorityQueue<Event>(this.events.length, new ComparatorBytime());
		//把事件加入到优先级队列
		for(int i = 0; i < this.events.length; i++){
			eventQueue.add(this.events[i]);
//			System.out.println("#" + i + "time:" + this.events[i].getWaitTime() +
//					"  sendId:" + this.events[i].getSendNode() +
//					"  code:" + this.events[i].getCode());
		}

		//模拟快照算法
		int ijkSourceVal[] = {300,300,300};
		Event currentEvent = null;
		char sendNodetmp;
		char recNodetmp;
		currentEvent = eventQueue.poll();
		while (currentEvent != null) {
//			System.out.println("time:" + currentEvent.getWaitTime() +
//					"  sendId:" + currentEvent.getSendNode() +
//					"  code:" + currentEvent.getCode());
			String code[] = currentEvent.getCode().split("\\|");

			int codeHead = Integer.valueOf(code[0]);
			switch (codeHead){
				case 1:
					//资源转移事件
					sendNodetmp = currentEvent.getSendNode();
					char sourceAction = currentEvent.getSourceAction();
					recNodetmp = code[1].charAt(0);
					int sourceVal = Integer.valueOf(code[2]);
					//更新资源量
					if(sourceAction == 's'){
						ijkSourceVal[ijkIndex(sendNodetmp)] -= sourceVal;
						eventQueue.add(new Event(sendNodetmp,
								recNodetmp, sourceVal,
								currentEvent.getWaitTime() +
										IConstant.ijkdelay[ijkIndex(sendNodetmp)][ijkIndex(recNodetmp)],'r'));
					} else {
						ijkSourceVal[ijkIndex(recNodetmp)] += sourceVal;
						//更新快照
						for(int i = 0; i < this.cNodeSnapshot.length; i++){
							if(this.cNodeSnapshot[i] != null) {
								this.cNodeSnapshot[i].sourceEvent(sendNodetmp, recNodetmp, sourceVal);
							}
						}
					}

					break;
				case 2:
					//快照事件
					char snapPrveNode = currentEvent.getSnapPrevNode();
					sendNodetmp = currentEvent.getSendNode();
					String snapID = code[1];
					int nodeSourceVal = 0;
					nodeSourceVal = ijkSourceVal[this.ijkIndex(sendNodetmp)];
//					for(int i = 0; i < ijkNode.length; i++){
//						if(sendNodetmp == ijkNode[i]){
//							nodeSourceVal = ijkSourceVal[i];
//						}
//					}
					//快照
					String strtmp[] = null;
					for(int i = 0; i < this.cNodeSnapshot.length; i++){
						if(this.cNodeSnapshot[i].isSnapshotId(snapID)){
							strtmp = this.cNodeSnapshot[i].snapshotEvent(snapPrveNode, sendNodetmp, nodeSourceVal);
//							System.out.println(this.cNodeSnapshot[i].getStandardSnapShot());
							if(strtmp != null){
								eventQueue.add(
										new Event(snapID,
												strtmp[0].charAt(0),
												strtmp[4].charAt(0),
												currentEvent.getWaitTime() + Integer.valueOf(strtmp[1]))
								);
								eventQueue.add(
										new Event(snapID,
												strtmp[2].charAt(0),
												strtmp[4].charAt(0),
												currentEvent.getWaitTime() + Integer.valueOf(strtmp[3]))
								);
							}
							break;
						}
					}
					break;
				case 5:
					break;
			}
			currentEvent = eventQueue.poll();
		}
		for(int i = 0; i < this.cNodeSnapshot.length; i++){
			if(this.cNodeSnapshot[i] != null){
				System.out.println(this.cNodeSnapshot[i].getStandardSnapShot());
			}
		}
	}
	private void start_send() {
		try {
			oos_i = new ObjectOutputStream(new Socket(ip_i, IConstant.portc).getOutputStream());
			oos_j = new ObjectOutputStream(new Socket(ip_j, IConstant.portp).getOutputStream());
			oos_k = new ObjectOutputStream(new Socket(ip_k, IConstant.portp).getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		long prveTime = 0;
		Event eventTmp = null;
		for(int i = 0; i < this.events.length; i++){
			eventTmp = this.events[i];
			try {
				Thread.sleep(eventTmp.getWaitTime() - prveTime);
				prveTime = eventTmp.getWaitTime();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				switch (this.events[i].getSendNode()){
					case 'i':
						oos_i.writeChars(eventTmp.getCode());
						break;
					case 'j':
						oos_j.writeChars(eventTmp.getCode());
						break;
					case 'k':
						oos_k.writeChars(eventTmp.getCode());
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void set_callbackc() {
		CallBackManager.setCallBackc(new ICallBack() {
			
			@Override
			public void receive_handler(String node, String msg) {
				String[] src = msg.split("\\|");
				/*这边接收是需要收到3个节点的信息之后才能打印，所以我建议声明一个记录的类，
				 * 然后要使用map<key, 类>来记录，
				 * 每收到一个节点送回的快照就按快照编号key修改对应类中的一个标记，
				 * 到时候只要从map中取出来就可以了*/
				for(int i = 0; i < cNodeSnapshot.length; i++){
					if(cNodeSnapshot[i].isSnapshotId(src[0])){
						if(cNodeSnapshot[i].setRecSnapSource(node.charAt(0),msg)){
							fPrint.format(formatStr,
									cNodeSnapshot[i].getStandardSnapShot(),
									cNodeSnapshot[i].getRecSnapShot());
						}
					}
				}
//				if (snap_all.containsKey(src[0])) {
//					if (node.equals("i")) {
//						//加入i的队列
//					}else if(node.equals("j")){
//						//加入j的队列
//					}else {
//						//加入k的队列
//					}
//				}else{
//					if (node.equals("i")) {
//						//加入i的队列
//					}else if(node.equals("j")){
//						//加入j的队列
//					}else {
//						//加入k的队列
//					}
//				}
				
			}
		});
	}
	
	class Snap{
		/*
		 * 具体要使用那些你要考虑清楚，我只写一些我觉得需要的
		 *  不够你再加
		 */
		String snap_i, snap_j, snap_k;
		boolean record_i = false, record_j = false, record_k = false;
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

		Scanner in = new Scanner(System.in);
		String ip[] = new String[3];
		int source_times;
		int snapshot_times;
        int randomSeed;
		System.out.print("请输入i的ip： ");
		ip[0] = in.next();
		System.out.print("请输入j的ip： ");
		ip[1] = in.next();
		System.out.print("请输入k的ip： ");
		ip[2] = in.next();
		System.out.print("资源转移次数：");
		source_times = in.nextInt();
		System.out.print("快照次数：");
		snapshot_times = in.nextInt();
		System.out.print("随机数种子：");
		randomSeed = in.nextInt();
		CNode cNode = new CNode(ip[0], ip[1], ip[2]);
        cNode.setEvents(source_times, snapshot_times, randomSeed);
		System.out.print("输入y启动send： ");
		String make_sure = in.next();
		if (!make_sure.equals("y")) {
			return;
		}
		cNode.start_send();
	}
}