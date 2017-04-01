package work;

public class CallBackManager {
	private static ICallBack callBackp;
	
	public static void setCallBackP(ICallBack call) {
		callBackp = call;
	}
	
	public static ICallBack getCallBackP() {
		return callBackp;
	}
}
