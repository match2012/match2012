/**
 * Copyright (c) 2010 Ray.
 * Wen Yi West RD, Hang Zhou, Zhe Jiang, China.
 * All rights reserved.
 *
 * "MD5Encrypt.java is the copyrighted,
 * proprietary property of Ray which retain all right, 
 * title and interest therein."
 * 
 * Create by RayStone at 下午04:15:55.
 * RayStone [email:rayinhangzhou@gmail.com]
 * 
 * Revision History
 *
 * Date              Programmer                   Notes
 * ---------    ---------------------  -----------------------------------
 * 2010-4-14           RayStone                     initial
 **/

package commonTool;

import java.security.MessageDigest;

public class MD5Encrypt {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String md5Encry(String strSrc) {
		String returnStr = null;
		if (strSrc.isEmpty()) {
			return null;
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			returnStr = byteArrayToHexString(md5.digest(strSrc.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			;
		}
		return returnStr;
	}
}
