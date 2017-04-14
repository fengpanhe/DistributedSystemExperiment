package work;
/*静态变量的通道延迟，直接可以直接在其他类中使用
如直接使用IConstant.ij，就可以得到1000的值；
*/
public interface IConstant {
	public int ij = 1000;
	public int ji = 1300;
	public int ik = 1600;
	public int ki = 1900;
	public int jk = 2100;
	public int kj = 2400;
	public int ijkdelay[][] = {
			{0,1000,1600},
			{1300,0,2100},
			{1900,2400,0}
	};
	public int portc = 8888;
	public int portp = 9999;
}
