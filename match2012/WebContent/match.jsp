<%@page import="util.GlobalParams"%>
<%@page import="models.MatchUser"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	MatchUser user = new MatchUser();
	Boolean isGuide = false;
	if(request.getSession().getAttribute("loginUser")==null){
		response.sendRedirect("login.html");
	} else {
		user = (MatchUser)request.getSession().getAttribute("loginUser");
	}
	if(request.getSession().getAttribute("guide")!=null) {
		isGuide = true;
		request.getSession().removeAttribute("guide");
	}
	Integer chatIndex = GlobalParams.chatIndex;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>MATCH</title>
<link type="text/css" href="css/css.css" rel="stylesheet" />
<script type='text/javascript' src="dwr/engine.js"></script>
<script type='text/javascript' src="dwr/util.js"></script>
<script type='text/javascript' src="dwr/interface/DataHelper.js"></script>
<script type="text/javascript" src="js/md5.js"></script>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" >
	var PARAMS = {"index": 0, "nowKey": null, "isChat": false, "chatMode": true, "chatBegin": 0, "chatEnd": 0, "data": {}, "selBgDom": null, "user": {}, "userInfoDom": null, "chatNumDom": null, "chatListDom": null};
	$(document).ready(function(){
		if(<%= isGuide %>) {
			$.showAddBg(true, 9999);
			initGuide();
			guideNext();
			return;
		}
		getMatchList(0, 20);
		PARAMS.selBgDom = $("#sel_bg");
		PARAMS.userInfoDom = $("#userInfoDiv");
		PARAMS.chatNumDom = $("#chat_num");
		PARAMS.chatListDom = $("#chat_list");
		PARAMS.userInfoDom.find(".handle").toggle(function() {
			DataHelper.getUserInfo(function(result){
				PARAMS.user.score = result.tag.userScore;
				PARAMS.user.winNum = result.tag.userWin;
				PARAMS.user.loseNum = result.tag.userLose;
				var winRate = (parseInt(PARAMS.user.winNum, 10)*100.0/(parseInt(PARAMS.user.winNum, 10)+parseInt(PARAMS.user.loseNum, 10))).toFixed(2);
				if(winRate=="NaN") {
					winRate = "0.00";
				}
				$("#userInfoDiv").find("strong").eq(0).html(PARAMS.user.score)
					.end().eq(1).html(PARAMS.user.winNum)
					.end().eq(2).html(PARAMS.user.loseNum)
					.end().eq(3).html(winRate+"%");
				PARAMS.userInfoDom.animate({top: '0px'}, "normal");
			});
		}, function() {
			PARAMS.userInfoDom.animate({top: '-174px'}, "normal");
		});
		PARAMS.chatNumDom.parent().toggle(function() {
			getChat();
			PARAMS.isChat = true;
			$("#chatListDiv").animate({right: '0px'}, "normal", function(){
				$("#chat_ipt").focus();
			}).find(".opBtn").fadeIn("normal");
		}, function() {
			PARAMS.isChat = false;
			$("#chatListDiv").animate({right: '-304px'}, "normal").find(".opBtn").fadeOut("normal");
		});
		PARAMS.chatBegin = PARAMS.chatEnd = <%= chatIndex %>;
		PARAMS.user.id = <% out.print("\""+user.getUserId()+"\""); %>;
		PARAMS.user.name = <% out.print("\""+user.getUserName()+"\""); %>;
		PARAMS.user.score = <% out.print("\""+user.getUserScore()+"\""); %>;
		PARAMS.user.winNum = <% out.print("\""+user.getUserWin()+"\""); %>;
		PARAMS.user.loseNum = <% out.print("\""+user.getUserLose()+"\""); %>;
		$("#userInfoDiv").find("label").html(PARAMS.user.name);
		var winRate = (parseInt(PARAMS.user.winNum, 10)*100.0/(parseInt(PARAMS.user.winNum, 10)+parseInt(PARAMS.user.loseNum, 10))).toFixed(2);
		if(winRate=="NaN") {
			winRate = "0.00";
		}
		$("#userInfoDiv").find("strong").eq(0).html(PARAMS.user.score)
			.end().eq(1).html(PARAMS.user.winNum)
			.end().eq(2).html(PARAMS.user.loseNum)
			.end().eq(3).html(winRate+"%");
		pollChat();
	});
	initGuide = function(){
		var html = "";
		html += "<div class=\"block\" id=\"match\">";
		html += "<div class=\"title\">";
		html += "<span style=\"float:right; line-height: 24px; margin-right: -2px;\">";
		html += "<span id=\"btn_demo_setStake\" class=\"sureBtn\" >下注</span></span>";
		html += "<strong style=\"font-size:20px\">希腊VS捷克</strong>";
		html += "<span style=\"margin-left:20px\">开始时间：<strong>2012-06-13 00:00:00</strong></span>";
		html += "</div>";
		html += "<div class=\"overview\">";
		html += "<span>甲方：<strong>希腊</strong>(<a style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">20</a>)</span>";
		html += "<span>乙方：<strong>捷克</strong>(<a id=\"btn_demo_stake\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">8</a>)</span>";
		html += "<span>平局：<strong>平局</strong>(<a style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">20</a>)</span>";
		html += "<span>赢方：<strong style=\"color: red;\">暂无</strong></span>";
		html += "<div>赛事简介：<pre style=\"color:#6495ED;\">2012欧洲杯A组第二轮希腊与捷克的比赛，赛前双方公布了各自的首发阵容，<br />"
			+"首轮比赛帮助希腊追平波兰的超级替补萨尔平吉迪斯首发，而捷克阵中，首轮表现不佳的巴罗什继续出现在锋线上。</pre></div>";
		html += "</div>";
		html += "</div>";
		$("#match_list").html(html);
		html = "";
		html+="<li >用户A*2</li>";
		html+="<li ><span>X</span>用户B*3</li>";
		html+="<li >用户C*3</li>";
		var offset = $("#btn_demo_stake").offset();
		$("#memberListDiv").css({"left": (offset.left+$("#btn_demo_stake").width()+10)+"px", "top": offset.top+"px"}).find("ul").html(html);
	};
	var guideStep = 0;
	guideNext = function(){
		var guideHandleDOM = $("#talkbubbleHandle");
		var guideDOM = $("#talkbubble");
		switch(guideStep) {
			case 0:
				var left = $(document).width()/2-80;
				var top = $(document).height()/2-40;
				guideDOM.css({"margin": "0px", "left": left+"px", "top": top+"px"}).show();
				guideStep++;
				break;
			case 1:
				var offset = $("#btn_fresh_match").offset();
				var left = offset.left+$("#btn_fresh_match").outerWidth()+26;
				var top = offset.top-6;
				guideDOM.animate({"left": left+"px", "top": top+"px"}, 1000, function(){
					$(this).find("span").html("点击此处刷新赛事列表！").end().find("a").html("知道了！");
				});
				guideHandleDOM.animate({"left": "-26px"}, 200);
				guideStep++;
				break;
			case 2: 
				guideDOM.animate({"left": "+=82"}, 500, function(){
					$(this).find("span").html("点击此处退出系统！");					
				});
				guideStep++;
				break;
			case 3: 
				var offset = $("#load_more").offset();
				var left = offset.left+$("#load_more").outerWidth()+26;
				var top = offset.top-6;
				guideDOM.animate({"left": left+"px", "top": top+"px"}, 500, function(){
					$(this).find("span").html("点击此处可获取更多的赛事列表！");
				});
				guideStep++;
				break;
			case 4: 
				var offset = $("#btn_demo_stake").offset();
				var left = offset.left;
				var top = offset.top-$("#btn_demo_stake").outerHeight()-80;
				guideDOM.animate({"left": left+"px", "top": top+"px", "height": "-=10px"}, 500, function(){
					guideHandleDOM.animate({"left": "6px", "top": "70px", 
						"border-top-width": "0px", 
						"border-right-width": "0px"}, 200, function(){
							guideHandleDOM.css({
								"border-top-color": "#00BFFF", 
								"border-right-color": "transparent"}).animate({
									"border-top-width": "26px", 
									"border-right-width": "13px"}, 200);
					});
					$(this).find("span").html("点击此处可获取当前下注的列表！");
					$("#memberListDiv").fadeIn();
				});
				guideStep++;
				break;
			case 5: 
				$("#memberListDiv").fadeOut();
				var offset = $("#btn_demo_setStake").offset();
				var left = offset.left-186;
				var top = offset.top+5;
				guideDOM.animate({"left": left+"px", "top": top+"px", "height": "+=10px"}, 500, function(){
					guideHandleDOM.animate({"left": "160px", "top": "3px", 
						"border-top-width": "0px", 
						"border-right-width": "0px"}, 200, function(){
							guideHandleDOM.css({
								"border-top-color": "transparent", 
								"border-left-color": "#00BFFF"}).animate({
									"border-left-width": "26px", 
									"border-bottom-width": "13px"}, 200);
					});
					$(this).find("span").html("点击此处可下注当前赛事！");
					$.showAddBg(true);
					$("#setWinnerDiv").show().find("span").eq(0).html("希腊")
						.end().eq(1).html("捷克");
				});
				guideStep++;
				break;
			case 6: 
				$("#setWinnerDiv").fadeOut();
				var offset = $("#userInfoDiv").find(".handle").offset();
				var left = offset.left-186;
				var top = offset.top+5;
				guideDOM.animate({"left": left+"px", "top": top+"px"}, 500, function(){
					$(this).find("span").html("点击此处可查看个人账户信息！");
					$("#userInfoDiv").animate({top: '0px'}, "normal");
				});
				guideStep++;
				break;
			case 7: 
				$("#userInfoDiv").animate({top: '-174px'}, "fast");
				guideDOM.animate({"left": "+=124px"}, 300, function(){
					$(this).find("span").html("点击此处可查进行实时评论！");
					$("#chatListDiv").animate({right: '0px'}, "normal");
				});
				guideStep++;
				break;
			case 8: 
				$("#chatListDiv").animate({right: '-304px'}, "fast");
				var left = $(document).width()/2-80;
				var top = $(document).height()/2-40;
				guideHandleDOM.animate({
					"border-left-width": "0px", 
					"border-bottom-width": "0px"}, 200);
				guideDOM.animate({"left": left+"px", "top": top+"px"}, 500, function(){
					$(this).find("span").html("赶快开始吧！").end().find("a").html("开始！");
				});
				guideStep++;
				break;
			case 9: 
				window.location.href = window.location.href;
				break;
			default:
				break;
		}
	};
	
	getMatchList = function(pageIndex, pageNum){
		DataHelper.getMatchList(pageIndex, pageNum, function(result){
			if (!result.isTrue) {
				alert(result.message);
			}
			else {
				if(pageIndex==0){
					$("#match_list").empty();
				}
				PARAMS.index = pageIndex+pageNum;
				if(result.tag.length<pageNum) {
					$("#load_more").hide();
				} else {
					$("#load_more").show();
				}
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					PARAMS.data[obj.key] = obj;
					html += "<div class=\"block\" id=\"match_"+obj.key+"\">";
					html += "<div class=\"title\">";
					if(obj.isStake=="true") {
						html += "<span style=\"float:right; line-height: 24px; margin-right: -2px;\">";
						html += "<span class=\"sureBtn\" onclick=\"javascript:selWinner('"+obj.key+"');\" >下注</span></span>";
					}
					html += "<strong style=\"font-size:20px\">"+obj.matchName+"</strong>";
					html += "<span style=\"margin-left:20px\">开始时间：<strong>"+obj.matchTime+"</strong></span>";
					html += "</div>";
					html += "<div class=\"overview\">";
					html += "<span>甲方：<strong>"+obj.matchA+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchA+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchAStake+"</a>)</span>";
					html += "<span>乙方：<strong>"+obj.matchB+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchB+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchBStake+"</a>)</span>";
					html += "<span>平局：<strong>平局</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '平局');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchABStake+"</a>)</span>";
					html += "<span>赢方：<strong style=\"color: red;\">"+obj.matchWinner+"</strong><strong style=\"margin-left:5px;\">("+obj.matchEnding+")</strong></span>";
					html += "<div>赛事简介：<pre style=\"color:#6495ED;\">"+obj.matchDesc+"</pre></div>";
					html += "</div>";
					html += "</div>";
				}
				$("#match_list").append(html);
			}
		});
	};
	
	getMore = function(){
		getMatchList(PARAMS.index, 20);
	};
	
	selWinner = function(key){
		PARAMS.nowKey = key;
		$.showAddBg(true);
		$("#setWinnerDiv").show().find("span").eq(0).html(PARAMS.data[key].matchA)
			.end().eq(1).html(PARAMS.data[key].matchB);
	};
	
	setSelBg = function(left, top){
		PARAMS.selBgDom.animate({"margin-left": left, "margin-top": top}, "fast");
	};
	
	showSetStake = function(side){
		$("#setWinnerDiv").hide();
		$("#stake_num").val("1");
		$("#stake_pwd").val("");
		$("#setStakeDiv").show().find("h3").html(side);
	};
	
	setStake = function(){
		var side = $.trim($("#setStakeDiv").show().find("h3").html());
		var stake = $.trim($("#stake_num").val());
		var pwd = hex_md5($.trim($("#stake_pwd").val()));
		if(!stake.isStake()) {
			alert("请正确下注！");
			$("#stake_num").focus();
			return;
		}
		if(pwd.length==0) {
			alert("请输入密码！");
			$("#stake_pwd").focus();
			return;
		}
		DataHelper.setStake(PARAMS.nowKey, side, stake, pwd, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	var tarDOM = $("#match_"+PARAMS.nowKey).find("strong:contains('"+side+"')").next("a");
	        	var nowStake = parseInt(tarDOM.html(), 10)+parseInt(stake, 10);
	        	tarDOM.html(nowStake);
				PARAMS.data[PARAMS.nowKey][side] = nowStake;
				$.showAddBg(false);
				$("#setStakeDiv").hide();
	        }
	    });
	};
	
	autoSel = function(){
		var end = Math.random()*3000;
		var flag = false;
		var total = 0;
		var obj = PARAMS.data[PARAMS.nowKey];
		var side = obj.matchA;
		var timer = null;
		timer = window.setInterval(function(){
			if(total< end) {
				if(flag) {
					side = obj.matchA;
					PARAMS.selBgDom.css("margin-left", "0px");
				} else {
					side = obj.matchB;
					PARAMS.selBgDom.css("margin-left", "50%");
				}
				flag = !flag;
				total += 80;
			} else {
				window.clearInterval(timer);
				showSetStake(side);
			}
		}, 80);
	};
	
	showStakeUser = function(dom, key, side){
		if(dom.html()=="0") {
			return;
		}
		var offset = dom.offset();
		DataHelper.getStakeUser(key, side, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	var html = "";
	        	for(var i=0; i<result.tag.length; i++) {
	        		var obj = result.tag[i];
	        		var opHTML = "";
	        		if(obj.stakeUser==PARAMS.user.name) {
	        			opHTML = "<span onclick=\"delStake('"+obj.key+"')\">X</span>";
	        		}
	        		html+="<li id=\"stake_"+obj.key+"\">"+opHTML+obj.stakeUser+"*<em>"+obj.stakeNum+"</em></li>";
	        	}
				$("#memberListDiv").css({"left": (offset.left+dom.width()+10)+"px", "top": offset.top+"px"}).show()
					.find("ul").html(html);
	        }
	    });
	};
	
	delStake = function(key){
		if(window.confirm("确定取消投注！")) {
			DataHelper.delStake(key, function(result){
		        if (!result.isTrue) {
		            alert(result.message);
		        }
		        else {
		        	var tarDOM = $("#match_"+PARAMS.nowKey).find("strong:contains('"+result.message+"')").next("a");
		        	var nowStake = parseInt(tarDOM.html(), 10)-parseInt($("#stake_"+key).find("em").html(), 10);
		        	tarDOM.html(nowStake);
		        	$("#stake_"+key).remove();
		        	if($("#memberListDiv").find("li").size()==0) {
		        		$("#memberListDiv").hide();
		        	}
		        }
		    });
		}
	};
	
	showMyNowMatch = function(){
		DataHelper.getMyNowMatch(function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					PARAMS.data[obj.key] = obj;
					html += "<div class=\"block\" id=\"match_"+obj.key+"\">";
					html += "<div class=\"title\">";
					html += "<strong style=\"font-size:20px\">"+obj.matchName+"</strong>";
					html += "<span style=\"margin-left:20px\">开始时间：<strong>"+obj.matchTime+"</strong></span>";
					html += "</div>";
					html += "<div class=\"overview\">";
					html += "<span>甲方：<strong>"+obj.matchA+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchA+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchAStake+"</a>)</span>";
					html += "<span>乙方：<strong>"+obj.matchB+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchB+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchBStake+"</a>)</span>";
					html += "<span>平局：<strong>平局</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '平局');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchABStake+"</a>)</span>";
					html += "<span>赢方：<strong style=\"color: red;\">"+obj.matchWinner+"</strong><strong style=\"margin-left:5px;\">("+obj.matchEnding+")</strong></span>";
					html += "<div>赛事简介：<pre style=\"color:#6495ED;\">"+obj.matchDesc+"</pre></div>";
					html += "</div>";
					html += "</div>";
				}
				$("#match_list").html(html);
	        }
	    });
	};
	
	showMyHisMatch = function(){
		DataHelper.getMyHisMatch(function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					PARAMS.data[obj.key] = obj;
					html += "<div class=\"block\" id=\"match_"+obj.key+"\">";
					html += "<div class=\"title\">";
					html += "<strong style=\"font-size:20px\">"+obj.matchName+"</strong>";
					html += "<span style=\"margin-left:20px\">开始时间：<strong>"+obj.matchTime+"</strong></span>";
					html += "</div>";
					html += "<div class=\"overview\">";
					html += "<span>甲方：<strong>"+obj.matchA+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchA+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchAStake+"</a>)</span>";
					html += "<span>乙方：<strong>"+obj.matchB+"</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '"+obj.matchB+"');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchBStake+"</a>)</span>";
					html += "<span>平局：<strong>平局</strong>(<a onclick=\"showStakeUser($(this), '"+obj.key+"', '平局');\" style=\"color:#DC143C; font-weight:bold; padding:0 2px;\">"+obj.matchABStake+"</a>)</span>";
					html += "<span>赢方：<strong style=\"color: red;\">"+obj.matchWinner+"</strong><strong style=\"margin-left:5px;\">("+obj.matchEnding+")</strong></span>";
					html += "<div>赛事简介：<pre style=\"color:#6495ED;\">"+obj.matchDesc+"</pre></div>";
					html += "</div>";
					html += "</div>";
				}
				$("#match_list").html(html);
	        }
	    });
	};
	
	sendChat = function(){
		var content = $.trim($("#chat_ipt").val());
		if(content.length==0) {
			alert("空消息！");
		}else if(content.length>80) {
			alert("废话太多啦，80字足矣！");
		} else {
			content = content.replace(/</g, "&lt").replace(/>/g, "&gt");
			DataHelper.addChat(content, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        } else {
	        	$("#chat_ipt").val("");
	        }
	    });
		}
	};
	
	getChat = function(){
		var getNum = parseInt(PARAMS.chatNumDom.html(), 10);
		getNum = getNum>20?20:getNum;
		if(getNum==0) {
			return;
		}
		DataHelper.getChat(-1, getNum, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	$("title").html("MATCH");
				PARAMS.chatNumDom.html(0);
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					html += "<div><label>"+obj.chatUser+"：</label>";
					html += "<span>"+obj.chatCon+"</span>";
					html += "<label>("+obj.chatTime+")</label></div>";
				}
				if(getNum>20) {
					PARAMS.chatListDom.html(html);
				} else {
					PARAMS.chatListDom.append(html);
				}
	        }
	    });
	};
	
	loadHisChat = function(){
		if(PARAMS.chatBegin==0) {
			alert("暂无历史记录！");
			return;
		}
		DataHelper.getChat(PARAMS.chatBegin, 20, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	PARAMS.chatBegin = result.message;
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					html += "<div><label>"+obj.chatUser+"：</label>";
					html += "<span>"+obj.chatCon+"</span>";
					html += "<label>(发表于"+obj.chatTime+")</label></div>";
				}
				PARAMS.chatListDom.prepend(html);
	        }
	    });
	};
	
	clearChat = function(){
		PARAMS.chatListDom.empty();
		PARAMS.chatBegin = PARAMS.chatEnd;
	};

	pollChat = function(){
		DataHelper.getNewChat(PARAMS.isChat, PARAMS.chatEnd, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	if(PARAMS.isChat) {
		        	PARAMS.chatEnd = result.message;
					var html ="";
					for(var i=0; i<result.tag.length; i++){
						var obj = result.tag[i];
						html += "<div><label>"+obj.chatUser+"：</label>";
						html += "<span>"+obj.chatCon+"</span>";
						html += "<label>("+obj.chatTime+")</label></div>";
					}
					PARAMS.chatListDom.append(html);
					if(PARAMS.chatMode) {
						PARAMS.chatListDom.parent().scrollTop(1000000);
					}
	        	} else {
	        		var num = parseInt(result.message, 10)-PARAMS.chatEnd+parseInt(PARAMS.chatNumDom.html(), 10);
	        		$("title").html("MATCH"+(num==0?"":"-"+num));
	        		PARAMS.chatNumDom.html(num);
		        	PARAMS.chatEnd = result.message;
	        	}
				pollChat();
	        }
	    });
	};
	
	changeChatMode = function(dom){
		if(PARAMS.chatMode) {
			PARAMS.chatMode = false;
			dom.html("锁定");
		} else {
			PARAMS.chatMode = true;
			dom.html("自动");
			PARAMS.chatListDom.parent().scrollTop(1000000);
		}
	};
	
	loginOut = function(){
		DataHelper.loginOut(function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				window.location.href="login.html";
	        }
	    });
	};
	
</script>
</head>
<body style="margin:auto; padding:30px; font-size:12px">
	<div style="float:left; width:100%;">
		<span id="btn_fresh_match" style="margin-left:0px;" class="sureBtn" onclick="getMatchList(0, 20);" >刷新赛事</span>
		<span style="margin-left:20px;" class="cancelBtn" onclick="loginOut();" >退出</span>
	</div>
	<div style="clear:both"></div>
	<div id="match_list" class="matchList">
		<!-- <div class="block" id="match_d8310b0305bd4fec875e40a5f23ea153">
			<div class="title">
				<span style="float:right; line-height: 24px; margin-right: -2px;">
					<span class="sureBtn" onclick="javascript:selWinner('d8310b0305bd4fec875e40a5f23ea153');">下注</span>
				</span><strong style="font-size:20px">希腊VS捷克</strong>
				<span style="margin-left:20px">开始时间：<strong>2012-06-13 00:00:00</strong></span>
			</div>
			<div class="overview">
				<span>甲方：<strong>希腊</strong>(<a style="color:#DC143C; font-weight:bold; padding:0 2px;">10</a>)</span>
				<span>乙方：<strong>捷克</strong>(<a id="btn_demo_stake" style="color:#DC143C; font-weight:bold; padding:0 2px;">8</a>)</span>
				<span>平局：<strong>平局</strong>(<a style="color:#DC143C; font-weight:bold; padding:0 2px;">8</a>)</span>
				<span>赢方：<strong style="color: red;">暂无</strong><strong style="margin-left:5px;">(1:1)</strong></span>
				<div>
					赛事简介：	
					<pre style="color:#6495ED;">2012欧洲杯A组第二轮希腊与捷克的比赛，赛前双方公布了各自的首发阵容，
首轮比赛帮助希腊追平波兰的超级替补萨尔平吉迪斯首发，而捷克阵中，首轮表现不佳的巴罗什继续出现在锋线上。</pre>
				</div>	
			</div>
		</div> -->
	</div>
	<span id="load_more" class="loadBtn" onclick="loadMore(20);">&gt;&gt;&nbsp;再获取20条&nbsp;&lt;&lt;</span>
	
	<div id="setWinnerDiv" class="winnerSel">
		<div style="float: left; margin-bottom: -216px; width:100%;">
			<div id="sel_bg" class="bg">&nbsp;</div>
		</div>
		<span class="left" onclick="showSetStake($(this).html());" onmouseover="setSelBg('0', '0');">球队A</span>
		<span class="right" onclick="showSetStake($(this).html());" onmouseover="setSelBg('50%', '0');">球队B</span>
		<span class="left" onclick="showSetStake($(this).html());" onmouseover="setSelBg('0', '108px');">平局</span>
		<span class="right" onclick="$.showAddBg(false);$('#setWinnerDiv').hide();" onmouseover="setSelBg('50%', '108px');">关闭</span>
	</div>
	
	<div id="setStakeDiv" class="matchEdit">
		<h3>球队A</h3>
		<div>下注：<input type="text" id="stake_num" class="date_picker" style="text-align: right;" />&nbsp;&nbsp;罐可乐</div>
		<div>密码：<input type="password" id="stake_pwd" class="date_picker" /></div>
		<div style="float:right; margin:20px 20px 0 0">
			<span class="sureBtn" onclick="setStake();">确定</span>
			<span class="cancelBtn" onclick="$('#setStakeDiv').hide();$.showAddBg(false);" >取消</span>
		</div>
	</div>
	
	<div id="userInfoDiv" class="userInfo">
		<div class="panel">
			<label>用户名</label>
			<div><span>积分：</span><strong>0.00</strong></div>
			<div><span>胜利场次：</span><strong>0</strong></div>
			<div><span>失败场次：</span><strong>0</strong></div>
			<div><span>胜率：</span><strong>0.00%</strong></div>
			<div><a onclick="showMyNowMatch();">查看当前投注赛事</a></div>
			<div><a style="color:#666;" onclick="showMyHisMatch();">查看历史投注赛事</a></div>
		</div>
		<div class="handle">账户信息</div>
	</div>
	
	<div class="chatListHandle">在线评论<a id="chat_num" class="button">0</a></div>
	<div id="chatListDiv" class="chatList">
		<div class="panel">
			<div style="text-align: center; margin-top:30px;">
				<a class="load" onclick="loadHisChat();">往前查看20条</a></div>
			<div id="chat_list" style="margin-bottom:30px;">
				<!-- <div><label>用户名：</label><span>胜利场次胜利场次胜利场次</span><label>(发表于2012-06-12 23:12:21)</label></div> -->
			</div>
		</div>
		<div class="handle">
			<span class="sureBtn" onclick="sendChat();" style="float:right; margin-left:0px;">确定</span>
			<input type="text" id="chat_ipt" style="float:left; margin-left:0px; height:18px; width:230px;" onkeyup="if(event.keyCode == 13) sendChat();" /></div>
		<div class="opBtn" onclick="clearChat();">
			清空
		</div>
		<div class="opBtn" style="margin-top:-100px;" onclick="changeChatMode($(this));">
			自动
		</div>
	</div>
	
	<div id="memberListDiv" class="memberList">
		<span class="cancelBtn" style="border:2px solid #6495ED; float:right; margin-top:-28px; margin-right:-6px;" onclick="$('#memberListDiv').hide();" >取消</span>
		<ul class="memberUl">
			<!-- <li title="报名IP: 10.10.20.146"><span>X</span>asd</li>
			<li title="报名IP: 10.10.20.146">asd</li> -->
		</ul>
	</div>
	
	
	<div id="talkbubble">
		<div id="talkbubbleHandle"></div>
		<span>用户名您好：<br />欢迎使用看球平台！</span>
		<a onclick="$(this).removeClass('on');guideNext();" onmouseover="$(this).addClass('on');" onmouseout="$(this).removeClass('on');">开始导航！</a>
	</div>
	
	
</body>
</html>