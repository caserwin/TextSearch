package operate;

import java.text.SimpleDateFormat;
import java.util.Date;

/******************************************
 * @author hduxyd
 * @data:2013-10-14下午06:13:08
 * @note:
 ******************************************/
public class GetTime {
	public static String getNowtime() {
		Date now=new Date();
		 SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
		return dateFormat.format(now);
	}
	public static String getNowDay() {
		Date now=new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(now);
	}
}
