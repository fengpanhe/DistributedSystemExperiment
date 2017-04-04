package work;

public class CallBackManager {
	private static ICallBack callBackp;
	private static ICallBack callBackc;
	
	public static ICallBack getCallBackc() {
		return callBackc;
	}

	public static void setCallBackC(ICallBack callBackc) {
		CallBackManager.callBackc = callBackc;
	}

	public static void setCallBackP(ICallBack call) {
		CallBackManager.callBackp = call;
	}
	
	public static ICallBack getCallBackP() {
		return callBackp;
	}
}
