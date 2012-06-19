/**
 * Copyright (c) 2010 Ray.
 * Wen Yi West RD, Hang Zhou, Zhe Jiang, China.
 * All rights reserved.
 *
 * "DateToolkit.java is the copyrighted,
 * proprietary property of Ray which retain all right, 
 * title and interest therein."
 * 
 * Create by RayStone at 下午04:19:39.
 * RayStone [email:rayinhangzhou@gmail.com]
 * 
 * Revision History
 *
 * Date              Programmer                   Notes
 * ---------    ---------------------  -----------------------------------
 * 2010-4-14           RayStone                     initial
 **/

package commonTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateToolkit {
	
	public static String getHello() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(hour<5){
			return "凌晨好";
		} else if(hour < 11) {
			return "上午好";
		} else if(hour < 13) {
			return "中午好";
		} else if (hour < 19) {
			return "下午好";
		} else {
			return "晚上好";
		}
	}

	/**
	 * 获取当前时间的format格式
	 * 
	 * @param Format
	 * @return
	 */
	public static String formatDate(String Format) {
		Date d = Calendar.getInstance().getTime();
		try {
			SimpleDateFormat f = new SimpleDateFormat(Format);
			return f.format(d);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取指定日期的Format格式
	 * 
	 * @param date
	 * @param Format
	 * @return
	 */
	public static String formatDate(Date date, String Format) {
		try {
			if(date==null){
				return "";
			}
			SimpleDateFormat f = new SimpleDateFormat(Format);
			return f.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 生成时间
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static Date strToDate(String Formate, String date) {
		try {
			SimpleDateFormat f = new SimpleDateFormat(Formate);
			return f.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成时间
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static Date strToDate(int year, int month, int date) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DATE, date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 生成时间
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date strToDate(int year, int month, int date, int hour,
			int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DATE, date);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	/**
	 * 获取指定日期的月份天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonthNum(Date date) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.DATE, 1);
		c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH) + 1, 1);
		return c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取当前时间的星期值
	 * 
	 * @return
	 */
	public static String getWeek() {
		String weekinfo = "";
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		switch (week) {
		case 1:
			weekinfo = "星期日";
			break;
		case 2:
			weekinfo = "星期一";
			break;
		case 3:
			weekinfo = "星期二";
			break;
		case 4:
			weekinfo = "星期三";
			break;
		case 5:
			weekinfo = "星期四";
			break;
		case 6:
			weekinfo = "星期五";
			break;
		case 7:
			weekinfo = "星期六";
			break;
		default:
			weekinfo = "";
		}
		return weekinfo;
	}

	/**
	 * 获取当天的开始时间
	 * 
	 * @return
	 */
	public static Date getCurrentDateStart() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取当天时间的结束时间
	 * 
	 * @return
	 */
	public static Date getCurrentDateEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 获取指定时间当天的开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateStart(Date date) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.set(Calendar.YEAR, _calendar.get(Calendar.YEAR));
		_calendar.set(Calendar.MONTH, _calendar.get(Calendar.MONTH));
		_calendar.set(Calendar.DATE, _calendar.get(Calendar.DATE));
		_calendar.set(Calendar.HOUR_OF_DAY, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.SECOND, 0);
		return _calendar.getTime();
	}

	/**
	 * 获取指定时间当天的结束时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateEnd(Date date) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.set(Calendar.YEAR, _calendar.get(Calendar.YEAR));
		_calendar.set(Calendar.MONTH, _calendar.get(Calendar.MONTH));
		_calendar.set(Calendar.DATE, _calendar.get(Calendar.DATE));
		_calendar.set(Calendar.HOUR_OF_DAY, 23);
		_calendar.set(Calendar.MINUTE, 59);
		_calendar.set(Calendar.SECOND, 59);
		return _calendar.getTime();
	}

	/**
	 * 获取指定时间当月的开始时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthStart(Date date) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.set(Calendar.YEAR, _calendar.get(Calendar.YEAR));
		_calendar.set(Calendar.MONTH, _calendar.get(Calendar.MONTH));
		_calendar.set(Calendar.DATE, 1);
		_calendar.set(Calendar.HOUR_OF_DAY, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.SECOND, 0);
		return _calendar.getTime();
	}

	/**
	 * 获取指定时间当月的结束时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Date date) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.set(Calendar.YEAR, _calendar.get(Calendar.YEAR));
		_calendar.set(Calendar.MONTH, _calendar.get(Calendar.MONTH)+1);
		_calendar.set(Calendar.DATE, 0);
		_calendar.set(Calendar.HOUR_OF_DAY, 23);
		_calendar.set(Calendar.MINUTE, 59);
		_calendar.set(Calendar.SECOND, 59);
		return _calendar.getTime();
	}

	/**
	 * 获取当前时间offset秒后的时间
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getDateAfterSec(Date date, int offset) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.add(Calendar.SECOND, offset);
		return _calendar.getTime();
	}

	/**
	 * 获取几天后的时间
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date getDateByDay(Date date, int offset) {
		Calendar _calendar = Calendar.getInstance();
		_calendar.setTime(date);
		_calendar.add(Calendar.DATE, offset);
		return _calendar.getTime();
	}
}
