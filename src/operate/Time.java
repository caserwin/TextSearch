package operate;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.JLabel;

/**
 * @author hduxyd
 */
public class Time extends Thread {
	JLabel jLabel;
	boolean isstop = false;

	public void init(JLabel jlb) {
		this.jLabel = jlb;
		isstop = false;
	}

	@Override
	public void run() {
		while (true) {
			if (isstop) {
				break;
			}
			Date now = new Date();
			DateFormat d8 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			String str8 = d8.format(now);
			this.jLabel.setText(str8);
		}
	}
}
