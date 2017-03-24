package work;
import java.io.Serializable;

public class Source implements Serializable{
	int code, trans, total;
	String name;
	String timer;
	
	public Source() {

	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTrans() {
		return trans;
	}
	public void setTrans(int trans) {
		this.trans = trans;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getTimer() {
		return timer;
	}
	public void setTimer(String timer) {
		this.timer = timer;
	}
	
	
}
