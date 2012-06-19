package models;


import java.math.BigDecimal;
import java.util.Date;

public class MatchUser {

	private String userId;
	private String userName;
	private String userPwd;
	private BigDecimal userScore;
	private Integer userWin;
	private Integer userLose;
	private String userState;
	private Date addTime;

	public MatchUser() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return this.userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public BigDecimal getUserScore() {
		return this.userScore;
	}

	public void setUserScore(BigDecimal userScore) {
		this.userScore = userScore;
	}

	public Integer getUserWin() {
		return this.userWin;
	}

	public void setUserWin(Integer userWin) {
		this.userWin = userWin;
	}

	public Integer getUserLose() {
		return this.userLose;
	}

	public void setUserLose(Integer userLose) {
		this.userLose = userLose;
	}

	public String getUserState() {
		return this.userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public Date getAddTime() {
		return this.addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

}
