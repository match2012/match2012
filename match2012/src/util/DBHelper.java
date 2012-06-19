/**
 * Copyright (c) 2011 Insigma Technology Co.Ltd.
 * No.226 TianMuShan Road, HangZhou, ZheJiang, China.
 * All rights reserved.
 *
 * "DBHelper.java is for what: "
 * 
 * Create by Ray at 2011-10-10 下午04:17:45.
 * Ray [email:shilei@insigma.com.cn]
 * 
 * Revision History
 * Date                 Programmer            Notes
 * --------------------------------------------------------------
 * 2011-10-10               Ray                     initial
 **/


package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class DBHelper {
	//logger
	private final static Logger log = Logger.getLogger(DBHelper.class);

	public static ConcurrentHashMap<String, Connection> connection_pool = new ConcurrentHashMap<String, Connection>();
	public static ConcurrentHashMap<String, Boolean> connection_state = new ConcurrentHashMap<String, Boolean>();
	public static int TIMEOUT = 15;
	private static int MAX_COUNT = 50;
	private static int NOW_COUNT = 0;

	
	public static void initConnections() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
					try {
						for (String key : connection_pool.keySet()) {
							if(!connection_pool.get(key).isValid(5)){
								connection_pool.get(key).close();
								connection_pool.remove(key);
								connection_state.remove(key);
								NOW_COUNT--;
							}
						}
						if(NOW_COUNT>30) {
							for (String key : connection_pool.keySet()) {
								if(NOW_COUNT<30) {
									break;
								}
								if(connection_state.get(key)){
									connection_pool.get(key).close();
									connection_pool.remove(key);
									connection_state.remove(key);
									NOW_COUNT--;
								}
							}
						}
					} catch (Exception e) {
						log.error("%%%% Error checkConnections %%%%", e);
					}
				}
			}, 30*60*1000, 30*60*1000);
		} catch (Exception e) {
			log.error("%%%% Error Init Connection_Service %%%%", e);
		}
	}
	
	public static String getConnection(){
		try {
			for (String key : connection_state.keySet()) {
				if(connection_state.get(key)){
					connection_state.put(key, false);
					return key;
				}
			}
			while (NOW_COUNT>=MAX_COUNT) {
				for (String key : connection_state.keySet()) {
					if(connection_state.get(key)){
						connection_state.put(key, false);
						return key;
					}
				}
			}
			String timeKey = UUID.randomUUID().toString();
			Connection connection = DriverManager.getConnection(GlobalParams.DB_URL);
			connection_state.put(timeKey, false);
			connection_pool.put(timeKey, connection);
			NOW_COUNT++;
			return timeKey;
		} catch (Exception e) {
			log.error("%%%% Error getWriteConnection %%%%", e);
			return null;
		}
	}
	
	public static void closeConnection(String key){
		try {
			connection_pool.get(key).setAutoCommit(true);
			connection_state.put(key, true);
		} catch (Exception e) {
			log.error("%%%% Error delConnection %%%%", e);
		}
	}
	
}
