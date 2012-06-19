/**
 * Copyright (c) 2010 TZ.NET.
 * Zheda Kejiyuan, Hang Zhou, Zhe Jiang, China.
 * All rights reserved.
 *
 * "DataHelper.java is the copyrighted,
 * proprietary property of Ray which retain all right, 
 * title and interest therein."
 * 
 * Create by RayStone at 2011-3-28 下午03:09:59.
 * Ray [email:rayinhangzhou@gmail.com]
 * 
 * Revision History
 *
 * Date            Programmer                   Notes
 * ---------    ---------------------  -----------------------------------
 * 2011-3-28            Ray                       initial
 **/

package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import models.MatchUser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;


import commonTool.DateToolkit;
import commonTool.ExecuteResult;



public class DataHelper {

	static Log log = LogFactory.getLog(DataHelper.class);
	
	public static String getHostIP() {
		try {
			WebContext webContext = WebContextFactory.get();
			HttpServletRequest request = webContext.getHttpServletRequest();
			return request.getRemoteAddr();
		} catch (Exception e) {
			log.error(e);
			return "";
		}
	}
	
	public HttpSession getSession(){
		WebContext webContext = WebContextFactory.get();
		return webContext.getHttpServletRequest().getSession();
	}
	
	public String getIP(){
		WebContext webContext = WebContextFactory.get();
		return webContext.getHttpServletRequest().getRemoteAddr();
	}
	
	public ExecuteResult getUserInfo(){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		Statement statement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
			statement = connection.createStatement();
			statement.setQueryTimeout(DBHelper.TIMEOUT);
		    String sql = "select * from match_user where user_id = '"+user.getUserId()+"'";
		    ResultSet rs = statement.executeQuery(sql);
		    rs.last();
		    user = new MatchUser();
			user.setAddTime(rs.getDate("add_time"));
			user.setUserId(rs.getString("user_id"));
			user.setUserLose(rs.getInt("user_lose"));
			user.setUserName(rs.getString("user_name"));
			user.setUserPwd(rs.getString("user_pwd"));
			user.setUserScore(rs.getBigDecimal("user_score"));
			user.setUserState(rs.getString("user_state"));
			user.setUserWin(rs.getInt("user_win"));
			getSession().setAttribute("loginUser", user);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("userId", user.getUserId());
			map.put("userName", user.getUserName());
			map.put("userLose", user.getUserLose()+"");
			map.put("userWin", user.getUserWin()+"");
			map.put("userScore", user.getUserScore()+"");
			return new ExecuteResult(true, "", map);
		} catch (Exception e) {
			log.error("getUserInfo-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(statement!=null) {
					statement.close();
				}
			} catch (Exception e2) {
				statement = null;
			}
			DBHelper.closeConnection(key);
		}
	} 
	
	public ExecuteResult getMatchList(Integer index, Integer size){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "select * from match_match where match_state != ? order by match_time desc limit ?, ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, GlobalParams.STATE_DELETE);
			preparedStatement.setInt(2, index);
			preparedStatement.setInt(3, size);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				list.add(map);
				GlobalParams.MAP_MATCH.put(resultSet.getString("match_id"), map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getMatchList-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult setStake(String matchId, String stakeSide, Integer stakeNum, String userPwd){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			if(!GlobalParams.MAP_MATCH.containsKey(matchId)) {
				return new ExecuteResult(false, "该赛事不存在！");
			}
			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
			HashMap<String, String> match = GlobalParams.MAP_MATCH.get(matchId);
			if(!user.getUserPwd().equals(userPwd)) {
				return new ExecuteResult(false, "密码错误！");
			}
			if(Calendar.getInstance().getTime().after(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", match.get("matchTime")))) {
				return new ExecuteResult(false, "比赛已开始！");
			}
			if(match.get("state").equals(GlobalParams.STATE_DELETE)) {
				return new ExecuteResult(false, "该赛事已取消！");
			}
//			String sql = "select * from match_stake where stake_match=? and stake_user=? and stake_state=? and stake_side=? ";
//		    preparedStatement = connection.prepareStatement(sql);
//			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
//			preparedStatement.setString(1, matchId);
//			preparedStatement.setString(2, user.getUserId());
//			preparedStatement.setString(3, GlobalParams.STATE_INIT);
//			preparedStatement.setString(4, stakeSide);
//			ResultSet rs = preparedStatement.executeQuery();
//			rs.last();
//			if(rs.getRow()>0) {
//				return new ExecuteResult(false, "您已有下赌注！");
//			}
			
			connection.setAutoCommit(false);
		    String sql = "update match_match set match_a_stake=(match_a_stake+?), match_b_stake=(match_b_stake+?), match_ab_stake=(match_ab_stake+?) where match_id =? ";
		    preparedStatement = connection.prepareStatement(sql);
			if(stakeSide.equals(match.get("matchA"))) {
				preparedStatement.setInt(1, stakeNum);
				preparedStatement.setInt(2, 0);
				preparedStatement.setInt(3, 0);
			} else if(stakeSide.equals(match.get("matchB"))) {
				preparedStatement.setInt(1, 0);
				preparedStatement.setInt(2, stakeNum);
				preparedStatement.setInt(3, 0);
			} else if(stakeSide.equals("平局")) {
				preparedStatement.setInt(1, 0);
				preparedStatement.setInt(2, 0);
				preparedStatement.setInt(3, stakeNum);
			} else {
				return new ExecuteResult(false, "所选队伍不存在！");
			}
			preparedStatement.setString(4, matchId);
			preparedStatement.executeUpdate();
			
			sql = "insert into match_stake (" +
		    		"stake_id, " +
		    		"stake_match, " +
		    		"stake_match_name, " +
		    		"stake_user, " +
		    		"stake_user_name, " +
		    		"stake_side," +
		    		"stake_num, " +
		    		"stake_state," +
		    		"add_time) " + "values(?, ?, ?, ?, ?, ?, ?, ?, ?) ";
			preparedStatement = connection.prepareStatement(sql);
			String stakeId = UUID.randomUUID().toString().replaceAll("-", "");
			preparedStatement.setString(1, stakeId);
			preparedStatement.setString(2, matchId);
			preparedStatement.setString(3, match.get("matchName"));
			preparedStatement.setString(4, user.getUserId());
			preparedStatement.setString(5, user.getUserName());
			preparedStatement.setString(6, stakeSide);
			preparedStatement.setInt(7, stakeNum);
			preparedStatement.setString(8, GlobalParams.STATE_INIT);
			preparedStatement.setString(9, DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss"));
			preparedStatement.executeUpdate();
			
			sql = "insert into match_chat (" +
		    		"chat_id, " +
		    		"chat_user, " +
		    		"chat_user_name, " +
		    		"chat_content, " +
		    		"chat_state," +
		    		"add_time) " + "values(?, ?, ?, ?, ?, ?) ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			String chatId = UUID.randomUUID().toString().replaceAll("-", "");
			String content = "<font style=\"color:red;\">"+user.getUserName()+"在["+match.get("matchName")+"]中对["+stakeSide+"]下注"+stakeNum+"罐可乐！</font>";
			String addTime = DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss");
			preparedStatement.setString(1, chatId);
			preparedStatement.setString(2, "0000");
			preparedStatement.setString(3, "<font style=\"color:red;\">系统</font>");
			preparedStatement.setString(4, content);
			preparedStatement.setString(5, GlobalParams.STATE_NORMAL);
			preparedStatement.setString(6, addTime);
			preparedStatement.executeUpdate();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("chatUser",  "<font style=\"color:red;\">系统</font>");
			map.put("chatTime", addTime);
			map.put("chatCon", content);
			synchronized (GlobalParams.chatIndex) {
				GlobalParams.chatIndex++;
				GlobalParams.MAP_CHAT.put(GlobalParams.chatIndex, map);
			}
			connection.commit();
			return new ExecuteResult(true, "");
		} catch (Exception e) {
			log.error("setStake-->", e);
			return new ExecuteResult(false, e.toString());
		} finally {
			try {
				connection.rollback();
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult delStake(String stakeId){
		return new ExecuteResult(false, "已屏蔽撤注功能！");
//		String key = DBHelper.getConnection();
//		Connection connection = DBHelper.connection_pool.get(key);
//		PreparedStatement preparedStatement = null;
//		try {
//			if(getSession().getAttribute("loginUser")==null) {
//				return new ExecuteResult(false, "登录已失效，请重新登录！");
//			}
//			
//		    String sql = "select * from match_stake where stake_id=? and stake_state=? ";
//		    preparedStatement = connection.prepareStatement(sql);
//			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
//			preparedStatement.setString(1, stakeId);
//			preparedStatement.setString(2, GlobalParams.STATE_INIT);
//			ResultSet rs = preparedStatement.executeQuery();
//			rs.last();
//			if(rs.getRow()==0) {
//				return new ExecuteResult(false, "您暂无此赌注！");
//			}
//			Integer stakeNum = rs.getInt("stake_num");
//			String matchId = rs.getString("stake_match");
//			String userId = rs.getString("stake_user");
//			String stakeSide = rs.getString("stake_side");
//			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
//			if(!user.getUserId().equals(userId)) {
//				return new ExecuteResult(false, "您暂无此赌注！");
//			}
//			if(!GlobalParams.MAP_MATCH.containsKey(matchId)) {
//				return new ExecuteResult(false, "该赛事不存在！");
//			}
//			HashMap<String, String> match = GlobalParams.MAP_MATCH.get(matchId);
//			if(Calendar.getInstance().getTime().after(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", match.get("matchTime")))) {
//				return new ExecuteResult(false, "比赛已开始！");
//			}
//			if(match.get("state").equals(GlobalParams.STATE_DELETE)) {
//				return new ExecuteResult(false, "该赛事已取消！");
//			}
//
//			connection.setAutoCommit(false);
//		    sql = "update match_match set match_a_stake=(match_a_stake-?), match_b_stake=(match_b_stake-?), match_ab_stake=(match_ab_stake-?) where match_id =? ";
//		    preparedStatement = connection.prepareStatement(sql);
//		    if(stakeSide.equals(match.get("matchA"))) {
//				preparedStatement.setInt(1, stakeNum);
//				preparedStatement.setInt(2, 0);
//				preparedStatement.setInt(3, 0);
//			} else if(stakeSide.equals(match.get("matchB"))) {
//				preparedStatement.setInt(1, 0);
//				preparedStatement.setInt(2, stakeNum);
//				preparedStatement.setInt(3, 0);
//			} else if(stakeSide.equals("平局")) {
//				preparedStatement.setInt(1, 0);
//				preparedStatement.setInt(2, 0);
//				preparedStatement.setInt(3, stakeNum);
//			} else {
//				return new ExecuteResult(false, "所选队伍不存在！");
//			}
//			preparedStatement.setString(4, matchId);
//			preparedStatement.executeUpdate();
//			
//			sql = "update match_stake set stake_state=? where stake_id=? ";
//		    preparedStatement = connection.prepareStatement(sql);
//			preparedStatement.setString(1, GlobalParams.STATE_DELETE);
//			preparedStatement.setString(2, stakeId);
//			preparedStatement.executeUpdate();
//			connection.commit();
//			return new ExecuteResult(true, stakeSide);
//		} catch (Exception e) {
//			log.error("delStake-->", e);
//			return new ExecuteResult(false, "删除失败！"+e);
//		} finally {
//			try {
//				connection.rollback();
//				if(preparedStatement!=null) {
//					preparedStatement.close();
//				}
//			} catch (Exception e2) {
//				preparedStatement = null;
//			}
//			DBHelper.closeConnection(key);
//		}
	}
	
	public ExecuteResult getStakeUser(String matchId, String side){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "select * from match_stake where stake_match = ? and stake_side= ? and stake_state!=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, matchId);
			preparedStatement.setString(2, side);
			preparedStatement.setString(3, GlobalParams.STATE_DELETE);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("stake_id"));
				map.put("stakeUser", resultSet.getString("stake_user_name"));
				map.put("stakeNum", resultSet.getString("stake_num"));
				list.add(map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getStakeUser-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult getMyNowMatch(){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		Statement statement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
		    String sql = "select * from match_match as tm, match_stake as ts " +
		    		"where tm.match_id = ts.stake_match and ts.stake_user = '"+user.getUserId()+"' and ts.stake_state='"+GlobalParams.STATE_INIT+"' group by tm.match_id";
			statement = connection.createStatement();
			statement.setQueryTimeout(DBHelper.TIMEOUT);
		    ResultSet resultSet = statement.executeQuery(sql);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				list.add(map);
				GlobalParams.MAP_MATCH.put(resultSet.getString("match_id"), map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getMyNowStake-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(statement!=null) {
					statement.close();
				}
			} catch (Exception e2) {
				statement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult getMyHisMatch(){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		Statement statement = null;
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
		    String sql = "select * from match_match as tm, match_stake as ts " +
		    		"where tm.match_id = ts.stake_match and ts.stake_user = '"+user.getUserId()+"' and ts.stake_state='"+GlobalParams.STATE_NORMAL+"' group by tm.match_id";
			statement = connection.createStatement();
			statement.setQueryTimeout(DBHelper.TIMEOUT);
		    ResultSet resultSet = statement.executeQuery(sql);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				list.add(map);
				GlobalParams.MAP_MATCH.put(resultSet.getString("match_id"), map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getMyNowStake-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(statement!=null) {
					statement.close();
				}
			} catch (Exception e2) {
				statement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult addChat(String content){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(content.length()==0) {
				return new ExecuteResult(false, "空消息！");
			}
			if(content.length()>80) {
				return new ExecuteResult(false, "废话太多啦，80字足矣！");
			}
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			MatchUser user = (MatchUser)getSession().getAttribute("loginUser");
		    String sql = "insert into match_chat (" +
		    		"chat_id, " +
		    		"chat_user, " +
		    		"chat_user_name, " +
		    		"chat_content, " +
		    		"chat_state," +
		    		"add_time) " + "values(?, ?, ?, ?, ?, ?) ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			String chatId = UUID.randomUUID().toString().replaceAll("-", "");
			content = content.replaceAll("<", "&lt").replaceAll(">", "&gt");
			String addTime = DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss");
			preparedStatement.setString(1, chatId);
			preparedStatement.setString(2, user.getUserId());
			preparedStatement.setString(3, user.getUserName());
			preparedStatement.setString(4, content);
			preparedStatement.setString(5, GlobalParams.STATE_NORMAL);
			preparedStatement.setString(6, addTime);
			preparedStatement.executeUpdate();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("chatUser", user.getUserName());
			map.put("chatTime", addTime);
			map.put("chatCon", content);
			synchronized (GlobalParams.chatIndex) {
				GlobalParams.chatIndex++;
				GlobalParams.MAP_CHAT.put(GlobalParams.chatIndex, map);
			}
			return new ExecuteResult(true, "", map);
		} catch (Exception e) {
			log.error("addChat-->", e);
			return new ExecuteResult(false, "评论失败！"+e);
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult getChat(Integer startIndex, Integer page){
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			if(page==0) {
				return new ExecuteResult(true, "", list);
			} else if(startIndex<0) {
				int end = GlobalParams.chatIndex+1;
				int start = end-page<0?0:end-page;
				for(int i=start; i<end; i++){
					list.add(GlobalParams.MAP_CHAT.get(i));
				}
				return new ExecuteResult(true, "", list);
			} else {
				int end = startIndex+1;
				int start = end-page<0?0:end-page;
				for(int i=start; i<end; i++){
					list.add(GlobalParams.MAP_CHAT.get(i));
				}
				return new ExecuteResult(true, start+"", list);
			}
		} catch (Exception e) {
			log.error("getChat-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		}
	}
	
	public ExecuteResult getChatIndex(){
		return new ExecuteResult(true, ""+GlobalParams.chatIndex);
	}
	
	public void returnNewChat(){
		GlobalParams.MAP_GET_CHAT.put(getSession().getId(), true);
	}
	
	public ExecuteResult getNewChat(Boolean isGet, Integer startIndex){
		try { 
			if(getSession().getAttribute("loginUser")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			int times = 0;
			int nowIndex = 0;
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while(times<150) {
				nowIndex = GlobalParams.chatIndex;
				if(!startIndex.equals(nowIndex)) {
					for(int i=startIndex+1; i<=nowIndex; i++){
						list.add(GlobalParams.MAP_CHAT.get(i));
					}
					return new ExecuteResult(true, nowIndex+"", list);
				}
				Thread.sleep(200);
				times++;
			}
			return new ExecuteResult(true, nowIndex+"", list);
		} catch (Exception e) {
			log.error("getNewChat-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		}
	}
	
	public ExecuteResult reg(String account, String pwd){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(!(account.length()>0 && account.length()<4 && account.matches("[\u4e00-\u9fa5]+"))){
				return new ExecuteResult(false, "用户名为不大于3个字的汉字！");
			}
		    String sql = "select * from match_user where user_name = ? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, account);
			ResultSet rs = preparedStatement.executeQuery();
			rs.last();
			if(rs.getRow()==0) {
				sql = "insert into match_user (" +
			    		"user_id, " +
			    		"user_name, " +
			    		"user_pwd, " +
			    		"user_ip, " +
			    		"user_state," +
			    		"add_time) " + "values(?, ?, ?, ?, ?, ?) ";
			    preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
				String userId = UUID.randomUUID().toString().replaceAll("-", "");
				preparedStatement.setString(1, userId);
				preparedStatement.setString(2, account);
				preparedStatement.setString(3, pwd);
				preparedStatement.setString(4, getIP());
				preparedStatement.setString(5, GlobalParams.STATE_NORMAL);
				preparedStatement.setString(6, DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss"));
				preparedStatement.executeUpdate();
				MatchUser user = new MatchUser();
				user.setAddTime(Calendar.getInstance().getTime());
				user.setUserId(userId);
				user.setUserLose(0);
				user.setUserName(account);
				user.setUserPwd(pwd);
				user.setUserScore(new BigDecimal("0.00"));
				user.setUserState(GlobalParams.STATE_NORMAL);
				user.setUserWin(0);
				getSession().setAttribute("loginUser", user);
				getSession().setAttribute("guide", true);
				return new ExecuteResult(true, "");
			} else {
				return new ExecuteResult(false, "该用户名已被注册！");
			}
		} catch (Exception e) {
			log.error("reg-->", e);
			return new ExecuteResult(false, "RP太差，注册失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	} 
	
	public ExecuteResult login(String account, String pwd){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
		    String sql = "select * from match_user where user_name = ? and user_pwd=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, account);
			preparedStatement.setString(2, pwd);
			ResultSet rs = preparedStatement.executeQuery();
			rs.last();
			if(rs.getRow()==1) {
				if(rs.getString("user_state").equals(GlobalParams.STATE_DELETE)) {
					return new ExecuteResult(false, "帐号已被删除，请联系管理员！");
				}
//				if(rs.getString("user_state").equals(GlobalParams.STATE_INIT)) {
//					getSession().setAttribute("guide", true);
//					sql = "update match_user set user_state = ? where user_id=? ";
//					preparedStatement = connection.prepareStatement(sql);
//					preparedStatement.setString(1, GlobalParams.STATE_NORMAL);
//					preparedStatement.setString(2, rs.getString("user_id"));
//					preparedStatement.executeUpdate();
//				}
				MatchUser user = new MatchUser();
				user.setAddTime(rs.getDate("add_time"));
				user.setUserId(rs.getString("user_id"));
				user.setUserLose(rs.getInt("user_lose"));
				user.setUserName(rs.getString("user_name"));
				user.setUserPwd(rs.getString("user_pwd"));
				user.setUserScore(rs.getBigDecimal("user_score"));
				user.setUserState(rs.getString("user_state"));
				user.setUserWin(rs.getInt("user_win"));
				getSession().setAttribute("loginUser", user);
				return new ExecuteResult(true, "");
			} else {
				return new ExecuteResult(false, "帐号或密码错误！");
			}
		} catch (Exception e) {
			log.error("login-->", e);
			return new ExecuteResult(false, "RP太差，登录失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	} 
	
	public ExecuteResult loginOut(){
		try {
			getSession().removeAttribute("loginUser");
			return new ExecuteResult(true, "");
		} catch (Exception e) {
			log.error("loginOut-->", e);
			return new ExecuteResult(false, e.toString());
		}
	} 
	
	public ExecuteResult loginAdmin(String account){
		if(GlobalParams.LoginAccount.equals(account)){
			getSession().setAttribute("loginAdmin", true);
			return new ExecuteResult(true, "");
		} else {
			return new ExecuteResult(false, "帐号错误！");
		}
	} 
	
	public ExecuteResult loginOutAdmin(){
		try {
			getSession().removeAttribute("loginAdmin");
			return new ExecuteResult(true, "");
		} catch (Exception e) {
			log.error("loginOutAdmin-->", e);
			return new ExecuteResult(false, e.toString());
		}
	} 
	
	public ExecuteResult getMatchListByAdmin(Integer index, Integer size){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "select * from match_match order by match_time desc limit ?, ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setInt(1, index);
			preparedStatement.setInt(2, size);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				list.add(map);
				GlobalParams.MAP_MATCH.put(resultSet.getString("match_id"), map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getMatchListByAdmin-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult getStakeUserByAdmin(String matchId, String side){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "select * from match_stake where stake_match = ? and stake_side= ? and stake_state=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, matchId);
			preparedStatement.setString(2, side);
			preparedStatement.setString(3, GlobalParams.STATE_INIT);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("stake_id"));
				map.put("stakeUser", resultSet.getString("stake_user_name"));
				map.put("stakeNum", resultSet.getString("stake_num"));
				list.add(map);
			}
			return new ExecuteResult(true, "", list);
		} catch (Exception e) {
			log.error("getStakeUser-->", e);
			return new ExecuteResult(false, "RP太差，获取失败！");
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult addMatch(String matchName, String matchTime, String matchA, String matchB, String matchDesc){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "insert into match_match (" +
		    		"match_id, " +
		    		"match_name, " +
		    		"match_time, " +
		    		"match_winner," +
		    		"match_ending, " +
		    		"match_a," +
		    		"match_b, " +
		    		"match_desc, " +
		    		"match_state," +
		    		"add_time) " + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			String matchId = UUID.randomUUID().toString().replaceAll("-", "");
			matchTime = DateToolkit.formatDate(DateToolkit.strToDate("yyyyMMddHHmm", matchTime), "yyyy-MM-dd HH:mm:ss");
			preparedStatement.setString(1, matchId);
			preparedStatement.setString(2, matchName);
			preparedStatement.setString(3, matchTime);
			preparedStatement.setString(4, "暂无");
			preparedStatement.setString(5, "暂无");
			preparedStatement.setString(6, matchA);
			preparedStatement.setString(7, matchB);
			preparedStatement.setString(8, matchDesc);
			preparedStatement.setString(9, GlobalParams.STATE_INIT);
			preparedStatement.setString(10, DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss"));
			preparedStatement.executeUpdate();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("key", matchId);
			map.put("state", GlobalParams.STATE_INIT);
			map.put("matchName", matchName);
			map.put("matchTime", matchTime);
			if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", matchTime))){
				map.put("isStake", "true");
			} else {
				map.put("isStake", "false");
			}
			map.put("addTime",  DateToolkit.formatDate("yyyy-MM-dd HH:mm:ss"));
			map.put("matchA", matchA);
			map.put("matchB", matchB);
			map.put("matchAStake", "0");
			map.put("matchBStake","0");
			map.put("matchABStake", "0");
			map.put("matchWinner", "暂无");
			map.put("matchEnding", "暂无");
			map.put("matchDesc", matchDesc);
			GlobalParams.MAP_MATCH.put(matchId, map);
			return new ExecuteResult(true, "", map);
		} catch (Exception e) {
			log.error("addMatch-->", e);
			return new ExecuteResult(false, "添加失败！"+e);
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult setWinner(String matchId, String matchWinner, String matchEnding){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			connection.setAutoCommit(false);
		    String sql = "update match_match set match_winner=?, match_ending=?, match_state=? where match_id=? ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, matchWinner);
			preparedStatement.setString(2, matchEnding);
			preparedStatement.setString(3, GlobalParams.STATE_NORMAL);
			preparedStatement.setString(4, matchId);
			preparedStatement.executeUpdate();
			
//			if(!matchWinner.equals("平局")) {
				sql = "select * from match_stake where stake_match = ? and stake_state = ?";
				List<HashMap<String, String>> stakeList = new ArrayList<HashMap<String,String>>();
				Integer totalStakeWin = 0;
				Integer totalStakeLose = 0;
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, matchId);
				preparedStatement.setString(2, GlobalParams.STATE_INIT);
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("user", resultSet.getString("stake_user"));
					map.put("side", resultSet.getString("stake_side"));
					map.put("num", resultSet.getString("stake_num"));
					stakeList.add(map);
					if(resultSet.getString("stake_side").equals(matchWinner)){
						totalStakeWin += resultSet.getInt("stake_num");
					} else {
						totalStakeLose += resultSet.getInt("stake_num");
					}
				}
				if(totalStakeLose.equals(0) || totalStakeWin.equals(0)) {
					sql = "update match_user set user_win = (user_win + ?), user_lose = (user_lose +?) where user_id=? ";
					preparedStatement = connection.prepareStatement(sql);
					for(int i=0; i<stakeList.size(); i++) {
						HashMap<String, String> map = stakeList.get(i);
						if(map.get("side").equals(matchWinner)) {
							preparedStatement.setInt(1, 1);
							preparedStatement.setInt(2, 0);
							preparedStatement.setString(3, map.get("user"));
						} else {
							preparedStatement.setInt(1, 0);
							preparedStatement.setInt(2, 1);
							preparedStatement.setString(3, map.get("user"));
						}
						preparedStatement.executeUpdate();
					}
				} else {
					double winRate = totalStakeLose*1.0/totalStakeWin;
					sql = "update match_user set user_score = (user_score +?), user_win = (user_win +?), user_lose = (user_lose +?) where user_id=? ";
					preparedStatement = connection.prepareStatement(sql);
					for(int i=0; i<stakeList.size(); i++) {
						HashMap<String, String> map = stakeList.get(i);
						if(map.get("side").equals(matchWinner)) {
							preparedStatement.setBigDecimal(1, BigDecimal.valueOf(winRate*Double.valueOf(map.get("num"))).setScale(2, RoundingMode.HALF_DOWN));
							preparedStatement.setInt(2, 1);
							preparedStatement.setInt(3, 0);
							preparedStatement.setString(4, map.get("user"));
						} else {
							preparedStatement.setInt(1, -1*Integer.valueOf(map.get("num")));
							preparedStatement.setInt(2, 0);
							preparedStatement.setInt(3, 1);
							preparedStatement.setString(4, map.get("user"));
						}
						preparedStatement.executeUpdate();
					}
				}
//			}
			
			sql = "update match_stake set stake_state = ? where stake_match=? and stake_state=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, GlobalParams.STATE_NORMAL);
			preparedStatement.setString(2, matchId);
			preparedStatement.setString(3, GlobalParams.STATE_INIT);
			preparedStatement.executeUpdate();
			connection.commit();
			refreshMatchMap(matchId);
			return new ExecuteResult(true, "", GlobalParams.MAP_MATCH.get(matchId));
		} catch (Exception e) {
			log.error("setWinner-->", e);
			return new ExecuteResult(false, e.toString());
		} finally {
			try {
				connection.rollback();
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult editMatch(String matchId, String matchName, String matchTime, String matchA, String matchB, String matchDesc){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
		    String sql = "update match_match set " +
		    		"match_name=?, " +
		    		"match_time=?, " +
		    		"match_a=?," +
		    		"match_b=?, " +
		    		"match_desc=? where match_id=? ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, matchName);
			preparedStatement.setString(2, matchTime);
			preparedStatement.setString(3, matchA);
			preparedStatement.setString(4, matchB);
			preparedStatement.setString(5, matchDesc);
			preparedStatement.setString(6, matchId);
			preparedStatement.executeUpdate();
			refreshMatchMap(matchId);
			return new ExecuteResult(true, "", GlobalParams.MAP_MATCH.get(matchId));
		} catch (Exception e) {
			log.error("editMatch-->", e);
			return new ExecuteResult(false, "修改失败！"+e);
		} finally {
			try {
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public ExecuteResult delMatch(String matchId){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		PreparedStatement preparedStatement = null;
		try { 
			if(getSession().getAttribute("loginAdmin")==null) {
				return new ExecuteResult(false, "登录已失效，请重新登录！");
			}
			connection.setAutoCommit(false);
			String sql = "update match_match set match_state=? where match_id=? ";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setQueryTimeout(DBHelper.TIMEOUT); 
			preparedStatement.setString(1, GlobalParams.STATE_DELETE);
			preparedStatement.setString(2, matchId);
			preparedStatement.executeUpdate();
			
			sql = "update match_stake set stake_state = ? where stake_match=? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, GlobalParams.STATE_DELETE);
			preparedStatement.setString(2, matchId);
			preparedStatement.executeUpdate();
			connection.commit();
			refreshMatchMap(matchId);
			return new ExecuteResult(true, "");
		} catch (Exception e) {
			log.error("delMatch-->", e);
			return new ExecuteResult(false, "删除失败！"+e);
		} finally {
			try {
				connection.rollback();
				if(preparedStatement!=null) {
					preparedStatement.close();
				}
			} catch (Exception e2) {
				preparedStatement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	

	public void refreshMatchMap(String matchId){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		Statement statement = null;
		try { 
			statement = connection.createStatement();
			statement.setQueryTimeout(DBHelper.TIMEOUT);
		    String sql = "select * from match_match where match_id = '"+matchId+"'";
		    ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				GlobalParams.MAP_MATCH.put(matchId, map);
			}
		} catch (Exception e) {
			log.error("refreshMatchMap-->", e);
		} finally {
			try {
				if(statement!=null) {
					statement.close();
				}
			} catch (Exception e2) {
				statement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	public static void initMapData(){
		String key = DBHelper.getConnection();
		Connection connection = DBHelper.connection_pool.get(key);
		Statement statement = null;
		try { 
			statement = connection.createStatement();
			statement.setQueryTimeout(DBHelper.TIMEOUT);
		    String sql = "select * from match_match ";
		    ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", resultSet.getString("match_id"));
				map.put("state", GlobalParams.MAP_STATE.get(resultSet.getString("match_state")));
				map.put("matchName", resultSet.getString("match_name"));
				map.put("matchTime", resultSet.getString("match_time").split("\\.")[0]);
				if(Calendar.getInstance().getTime().before(DateToolkit.strToDate("yyyy-MM-dd HH:mm:ss", map.get("matchTime")))){
					map.put("isStake", "true");
				} else {
					map.put("isStake", "false");
				}
				map.put("addTime", resultSet.getString("add_time").split("\\.")[0]);
				map.put("matchA", resultSet.getString("match_a"));
				map.put("matchB", resultSet.getString("match_b"));
				map.put("matchAStake", resultSet.getInt("match_a_stake")+"");
				map.put("matchBStake", resultSet.getInt("match_b_stake")+"");
				map.put("matchABStake", resultSet.getInt("match_ab_stake")+"");
				map.put("matchWinner", resultSet.getString("match_winner"));
				map.put("matchEnding", resultSet.getString("match_ending"));
				map.put("matchDesc", resultSet.getString("match_desc"));
				GlobalParams.MAP_MATCH.put(resultSet.getString("match_id"), map);
			}
			sql = "select * from match_chat order by add_time ";
		    resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("chatUser", resultSet.getString("chat_user_name"));
				map.put("chatTime",  resultSet.getString("add_time").split("\\.")[0]);
				map.put("chatCon", resultSet.getString("chat_content"));
				GlobalParams.MAP_CHAT.put(GlobalParams.chatIndex, map);
				GlobalParams.chatIndex++;
			}
			GlobalParams.chatIndex--;
		} catch (Exception e) {
			log.error("initMapData-->", e);
		} finally {
			try {
				if(statement!=null) {
					statement.close();
				}
			} catch (Exception e2) {
				statement = null;
			}
			DBHelper.closeConnection(key);
		}
	}
	
	
	public static void main(String[] args) {
		try {
			Date treDate = DateToolkit.strToDate("yyyy.MM.dd.HH.mm", "2011.99.99.99.9.99999");
			System.out.println("result-111->"+treDate);
		} catch (Exception e) {
			// TODO: handle exception
			e.toString();
		}
	}
	
}
