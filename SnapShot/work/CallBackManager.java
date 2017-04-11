package work;

public class CallBackManager {
<<<<<<< HEAD
	private static ICallBack callBackp;
=======
	private static ICallBack callBack;
>>>>>>> cd237ef5ef1384b4238b029c950007393b77bfbb
	private static ICallBack callBackc;
	
	public static ICallBack getCallBackc() {
		return callBackc;
	}

<<<<<<< HEAD
	public static void setCallBackC(ICallBack callBackc) {
		CallBackManager.callBackc = callBackc;
	}

	public static void setCallBackP(ICallBack call) {
		CallBackManager.callBackp = call;
	}
	
	public static ICallBack getCallBackP() {
		return callBackp;
	}
=======
	public static void setCallBackc(ICallBack callBackc) {
		CallBackManager.callBackc = callBackc;
	}

	public static ICallBack getCallBack() {
		return callBack;
	}

	public static void setCallBack(ICallBack callBack) {
		CallBackManager.callBack = callBack;
	}
	
>>>>>>> cd237ef5ef1384b4238b029c950007393b77bfbb
}
