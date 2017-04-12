package work;

public interface ICallBack {
	/**
	 * @param node 节点名
	 * @param msg 消息内容
	 */
	public void receive_handler(String node, String msg);
}
