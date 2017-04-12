package work;

public class CallBackManager {

	private static ICallBack callBack;
	private static ICallBack callBackc;
	
	public static ICallBack getCallBackc() {
		return callBackc;
	}

	public static void setCallBackc(ICallBack callBackc) {
		CallBackManager.callBackc = callBackc;
	}

	public static ICallBack getCallBack() {
		return callBack;
	}

	public static void setCallBack(ICallBack callBack) {
		CallBackManager.callBack = callBack;
	}
	
}
