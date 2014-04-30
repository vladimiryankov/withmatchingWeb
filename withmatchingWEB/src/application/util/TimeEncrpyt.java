package application.util;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

	public class TimeEncrpyt {
		public static String TimeHash() {
			Date d = new Date();
			d.getTime();
			String hashedTime = DigestUtils.sha256Hex(d.toString());
			return hashedTime;
		}
}
