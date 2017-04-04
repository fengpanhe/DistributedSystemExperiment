package work;

public class CNode{
	Send send;
	ReceiveThreadManager receive;
	static String ip_i, ip_j, ip_k;
	
	public CNode(String ip1, String ip2, String ip3) {
		ip_i = ip1;
		ip_j = ip2;
		ip_k = ip3;
		start_receive();
	}
	
	private void start_receive() {
		receive = new ReceiveThreadManager(0, IConstant.portc);
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