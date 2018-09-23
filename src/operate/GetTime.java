/**
 * 
 */
package operate;

import java.text.SimpleDateFormat;
import java.util.Date;

/******************************************
 * @function：
 * @author hduxyd
 * @data:2013-10-14下午06:13:08
 * @department：杭州电子科技大学通信工程学院
 * @note:
 ******************************************/
public class GetTime {
	public static String getNowtime() {
		Date now=new Date();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
		 String nowtime = dateFormat.format(now);
		return nowtime;
	}
	public static String getNowDay() {
		Date now=new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowtime = dateFormat.format(now);
		return nowtime;
	}
}
