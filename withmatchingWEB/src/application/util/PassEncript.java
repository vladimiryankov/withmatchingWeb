package application.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PassEncript {
	
	public static String PassHash(String password) {
		String hashedPass;
		/*Date d = new Date();
		d.g*/
		hashedPass = DigestUtils.sha256Hex(password);
		return hashedPass;
	}

}
