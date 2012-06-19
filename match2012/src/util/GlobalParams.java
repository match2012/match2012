/**
 * Copyright (c) 2010 Ray.
 * Wen Yi West RD, Hang Zhou, Zhe Jiang, China.
 * All rights reserved.
 *
 * "GlobalParams.java is the copyrighted,
 * proprietary property of Ray which retain all right, 
 * title and interest therein."
 * 
 * Create by RayStone at 下午05:36:37.
 * RayStone [email:rayinhangzhou@gmail.com]
 * 
 * Revision History
 *
 * Date              Programmer                   Notes
 * ---------    ---------------------  -----------------------------------
 * 2010-4-14           RayStone                     initial
 **/

package util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class GlobalParams {
	public static String LoginAccount = "match_on_admin";
	public static String	DB_URL = "jdbc:mysql://localhost:3306/match_db?user=root&password=123123";
	
	public static String STATE_INIT = "00";
	public static String STATE_NORMAL = "01";
	public static String STATE_DELETE = "02";
	public static HashMap<String, String> MAP_STATE = new HashMap<String, String>();
	static {
		MAP_STATE.put(STATE_INIT, "init");
		MAP_STATE.put(STATE_NORMAL, "normal");
		MAP_STATE.put(STATE_DELETE, "delete");
	}
	
	/*************************** 各类词典 *********************************/
	public static ConcurrentHashMap<String, HashMap<String, String>> MAP_MATCH = new ConcurrentHashMap<String, HashMap<String, String>>();
	public static ConcurrentHashMap<Integer, HashMap<String, String>> MAP_CHAT = new ConcurrentHashMap<Integer, HashMap<String, String>>();
	public static ConcurrentHashMap<String, Boolean> MAP_GET_CHAT = new ConcurrentHashMap<String, Boolean>();
	
	/*************************** 各类数据类型 **********************************/
	public volatile static Integer chatIndex = 0;
	
}
