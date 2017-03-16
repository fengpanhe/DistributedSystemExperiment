package First.LearnCSMode.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by he on 17-3-4.
 */
public class SenderAll_310 {

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        SenderAll_310 test = new SenderAll_310();

        System.out.print("请输入随机数种子： ");
        int random = in.nextInt();
        System.out.print("请输入超时阀植： ");
        int limitTime = in.nextInt();
        System.out.print("请输入第一个接受者的ip： ");
        String ip1= in.nextLine();
        System.out.print("请输入第二个接受者的ip： ");
        String ip2= in.nextLine();
        test.eventCreator(random,limitTime,ip1,ip2);
    }

    public void eventCreator(int seed,int limitTime,String ip1,String ip2){
        Random random = new Random(seed);
        int counterOne = 0,counterZero = 0;

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        System.out.println("广播模式-send");
        long startTime=System.currentTimeMillis();   //获取开始时间

        Formatter f = new Formatter(System.out);
        f.format("%-9s %-10s %-19s %-10s \n", "序号", "1/0", "时刻", "间隔（ms）");
        int result;
        for(int i = 0; i < 20; i ++) {
            double R = random.nextDouble();
            double T = (-6) * Math.log(R) * 1000;
            try {
                Thread.sleep((long) T);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (counterZero == 15) {
                result = 1;
                counterOne++;
            } else if (counterOne == 5) {
                result = 0;
                counterZero++;
            } else {
                if (R < 0.25) {
                    result = 1;
                    counterOne++;
                } else {
                    result = 0;
                    counterZero++;
                }
            }
            f.format("%-10d %-10d %-20s %-10d \n",i,result,df.format(new Date()),(int)T);
        }
        System.out.println("结束时间：" + df.format(new Date()));
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("运行时间（ms）： "+(endTime-startTime));
    }
}