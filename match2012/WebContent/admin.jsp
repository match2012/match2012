<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	if(request.getSession().getAttribute("loginAdmin")==null){
		response.sendRedirect("loginforadmin.html");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Admin</title>
<link type="text/css" href="css/css.css" rel="stylesheet" />
<script type='text/javascript' src="dwr/engine.js"></script>
<script type='text/javascript' src="dwr/util.js"></script>
<script type='text/javascript' src="dwr/interface/DataHelper.js"></script>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" >
	var PARAMS = {"index": 0, "nowKey": null, "data": {}, "selBgDom": null};
	$(document).ready(function(){
		PARAMS.selBgDom = $("#sel_bg");
		getMatchList(0, 20);
	});
	
	getMatchList = function(pageIndex, pageNum){
		DataHelper.getMatchListByAdmin(pageIndex, pageNum, function(result){
			if (!result.isTrue) {
				alert(result.message);
			}
			else {
				if(pageIndex==0){
					$("#match_list").empty();
				}
				var html ="";
				for(var i=0; i<result.tag.length; i++){
					var obj = result.tag[i];
					PARAMS.data[obj.key] = obj;
					html += "<div class=\"block\" id=\"match_"+obj.key+"\">";
					html += "<div class=\"title\">";
					html += "<span style=\"float:right; line-height: 24px; margin-right: -2px;\">";
					if(obj.state=="init") {
						html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchA+"');\">甲方赢</span>";
						html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchB+"');\">乙方赢</span>";
						html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '平局');\">平局</span>";
						html += "<span class=\"sureBtn\" onclick=\"javascript:showEditMatch('"+obj.key+"');\">编辑</span>";
						html += "<span class=\"delBtn\" onclick=\"javascript:delMatch('"+obj.key+"');\">删除</span></span>";
					} else if(obj.state=="normal") {
						html += "<span class=\"delBtn\" onclick=\"javascript:delMatch('"+obj.key+"');\">删除</span></span>";
					} else {
						html += "<span class=\"delBtn delete\">已删除</span></span>";
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
	
	showAddMatch = function(){
		PARAMS.nowKey = "";
		$("#match_name").prop("readonly", false).val("");
		$("#match_begin").val("");
		$("#match_a").val("");
		$("#match_b").val("");
		$("#match_desc").val("");
		$.showAddBg(true);
		$("#addMatchDiv").show();
	};
	
	addMatch = function(){
		if(PARAMS.nowKey!="") {
			editMatch();
			return;
		}
		var matchName = $.trim($("#match_name").val());
		var matchTime = $.trim($("#match_begin").val());
		var matchA = $.trim($("#match_a").val());
		var matchB = $.trim($("#match_b").val());
		var matchDesc = $.trim($("#match_desc").val());
		if(matchName.isEmpty() || matchTime.isEmpty() || matchA.isEmpty() || matchB.isEmpty() || matchDesc.isEmpty()){
			alert("各填写项目均不能为空！");
			return;
		}
		if(!/^(2012)([0-1][0-9])([0-3][0-9])([0-2][0-9])([0-5][0-9])?$/.test(matchTime)) {
			alert("开始时间格式错误！");
			return;
		}
		DataHelper.addMatch(matchName, matchTime, matchA, matchB, matchDesc, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				var obj = result.tag;
				PARAMS.data[obj.key] = obj;
				var html = "";
				html += "<div class=\"block\" id=\"match_"+obj.key+"\">";
				html += "<div class=\"title\">";
				html += "<span style=\"float:right; line-height: 24px;\">";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchA+"');\">甲方赢</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchB+"');\">乙方赢</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '平局');\">平局</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showEditMatch('"+obj.key+"');\">编辑</span>";
				html += "<span class=\"delBtn\" onclick=\"javascript:delMatch('"+obj.key+"');\">删除</span></span>";
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
				$("#match_list").prepend(html);
				$("#addMatchDiv").hide();
				$.showAddBg(false);
				alert("添加成功！");
	        }
	    });
	};
	
	showSetWinner = function(key){
		PARAMS.nowKey = key;
		$.showAddBg(true);
		$("#setWinnerDiv").show().find("span").eq(0).html(PARAMS.data[key].matchA)
			.end().eq(1).html(PARAMS.data[key].matchB);
	};
	
	showSetEnding = function(key, side){
		PARAMS.nowKey = key;
		$("#match_ending").val("");
		$("#setStakeDiv").show().find("h3").html(side);
	};
	
	setSelBg = function(flag){
		PARAMS.selBgDom.animate({"margin-left": flag}, "fast");
	};
	
	setWinner = function(){
		var side = $("#setStakeDiv").find("h3").html();
		var ending = $.trim($("#match_ending").val());
		if(ending.length==0) {
			alert("请填写最后比分！");
			return;
		}
		DataHelper.setWinner(PARAMS.nowKey, side, ending, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				$("#match_"+PARAMS.nowKey).find("span")
					.first().html("<span class=\"delBtn\" onclick=\"javascript:delMatch('"+PARAMS.nowKey+"');\">删除</span>").end()
					.last().html("赢方：<strong style=\"color: red;\">"+side+"</strong><strong style=\"margin-left:5px;\">("+ending+")</strong>");
				$.showAddBg(false);
				$("#setStakeDiv").hide();
	        }
	    });
	};
	
	showEditMatch = function(key){
		PARAMS.nowKey = key;
		$("#match_name").prop("readonly", true);
		var obj = PARAMS.data[key];
		$("#match_name").val(obj.matchName);
		$("#match_begin").val(obj.matchTime.substring(0, obj.matchTime.length-3));
		$("#match_a").val(obj.matchA);
		$("#match_b").val(obj.matchB);
		$("#match_desc").val(obj.matchDesc);
		$.showAddBg(true);
		$("#addMatchDiv").show();
	};
	
	editMatch = function(){
		var matchName = $.trim($("#match_name").val());
		var matchTime = $.trim($("#match_begin").val());
		var matchA = $.trim($("#match_a").val());
		var matchB = $.trim($("#match_b").val());
		var matchDesc = $.trim($("#match_desc").val());
		if(matchName.isEmpty() || matchTime.isEmpty() || matchA.isEmpty() || matchB.isEmpty() || matchDesc.isEmpty()){
			alert("各填写项目均不能为空！");
			return;
		}
		if(!/^(2012)([0-1][0-9])([0-3][0-9])([0-2][0-9])([0-5][0-9])?$/.test(matchTime)) {
			alert("开始时间格式错误！");
			return;
		}
		DataHelper.editMatch(PARAMS.nowKey, matchName, matchTime, matchA, matchB, matchDesc, function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
	        	var obj = result.tag;
				PARAMS.data[obj.key] = obj;
				var html = "";
				html += "<div class=\"title\">";
				html += "<span style=\"float:right; line-height: 24px;\">";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchA+"');\">甲方赢</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '"+obj.matchB+"');\">乙方赢</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showSetEnding('"+obj.key+"', '平局');\">平局</span>";
				html += "<span class=\"sureBtn\" onclick=\"javascript:showEditMatch('"+obj.key+"');\">编辑</span>";
				html += "<span class=\"delBtn\" onclick=\"javascript:delMatch('"+obj.key+"');\">删除</span></span>";
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
				$("#match_"+obj.key).html(html);
				$("#addMatchDiv").hide();
				$.showAddBg(false);
				alert("修改成功！");
	        }
	    });
	};
	
	delMatch = function(key){
		if(window.confirm("确定删除赛事！")) {
			DataHelper.delMatch(key, function(result){
		        if (!result.isTrue) {
		            alert(result.message);
		        }
		        else {
					$("#match_"+key).find("span").eq(0).html("<span class=\"delBtn delete\">已删除</span>");
		        }
		    });
	   }
	};
	
	showStakeUser = function(dom, key, side){
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
	        		html+="<li id=\"stake_"+obj.key+"\">"+opHTML+obj.stakeUser+"*"+obj.stakeNum+"</li>";
	        	}
				$("#memberListDiv").css({"left": (offset.left+dom.width()+10)+"px", "top": offset.top+"px"}).show()
					.find("ul").html(html);
	        }
	    });
	};
	
	loginOut = function(){
		DataHelper.loginOutAdmin(function(result){
	        if (!result.isTrue) {
	            alert(result.message);
	        }
	        else {
				window.location.href="loginforadmin.html";
	        }
	    });
	};
	
</script>
</head>
<body style="margin:auto; padding:30px; font-size:12px">
	<div style="float:left; margin:0 0 0px -20px;">
		<span class="sureBtn" onclick="getMatchList(0, 20);">刷新</span>
		<span class="sureBtn" onclick="showAddMatch();">创建</span>
		<span class="cancelBtn" onclick="loginOut();">退出</span>
	</div>
	<div style="clear:both"></div>
	<div id="match_list" class="matchList">
		<!-- <div class="block" id="match_d8310b0305bd4fec875e40a5f23ea153">
			<div class="title">
				<span style="float:right; line-height: 24px; margin-right: -2px;">
					<span class="sureBtn" >赢家</span>
					<span class="sureBtn" >编辑</span>
					<span class="delBtn" >删除</span>
				</span>
				<strong style="font-size:20px">希腊VS捷克</strong>
				<span style="margin-left:20px">开始时间：<strong>2012-06-13 00:00:00</strong></span>
			</div>
			<div class="overview">
				<span>甲方：<strong>希腊</strong>(<a style="color:#DC143C; font-weight:bold; padding:0 2px;">10</a>)</span>
				<span>乙方：<strong>捷克</strong>(<a id="btn_demo_stake" style="color:#DC143C; font-weight:bold; padding:0 2px;">8</a>)</span>
				<span>赢方：<strong style="color: red;">暂无</strong></span>
				<div>
					赛事简介：	
					<pre style="color:#6495ED;">2012欧洲杯A组第二轮希腊与捷克的比赛，赛前双方公布了各自的首发阵容，
首轮比赛帮助希腊追平波兰的超级替补萨尔平吉迪斯首发，而捷克阵中，首轮表现不佳的巴罗什继续出现在锋线上。</pre>
				</div>	
			</div>
		</div> -->
	</div>
	
	
	<div id="addMatchDiv" class="matchEdit">
		<h3>赛事：<input type="text" id="match_name" style="width:400px;" /></h3>
		<div>比赛时间：<input type="text" id="match_begin" class="date_picker" maxlength="12" />(格式yyyyMMddHHmm, 例如201110010530)</div>
		<div>比赛甲方：<input type="text" id="match_a" /></div>
		<div>比赛乙方：<input type="text" id="match_b" /></div>
		<div>
			赛事简介：
			<textarea id="match_desc" style="border:1px solid #c0c0c0; width:350px; height:125px"></textarea>
		</div>
		<div style="float:right; margin:20px 20px 0 0">
			<span class="sureBtn" onclick="addMatch();">确定</span>
			<span class="cancelBtn" onclick="$('#addMatchDiv').hide();$.showAddBg(false);">取消</span>
		</div>
	</div>
	
	<div id="setWinnerDiv" class="winnerSel">
		<div style="float: left; margin-bottom: -108px; width:100%;">
			<div id="sel_bg" class="bg">&nbsp;</div>
		</div>
		<span class="left" onclick="showSetEnding($(this).html());" onmouseover="setSelBg('0');">球队A</span>
		<span class="right" onclick="showSetEnding($(this).html());" onmouseover="setSelBg('50%');">球队B</span>
		<div style="float: left; margin-top: -134px; width:100%;">
			<span class="cancelBtn" style="float: right; margin-right: -4px; " onclick="$.showAddBg(false);$('#setWinnerDiv').hide();" >关闭</span>
		</div>
	</div>
	
	<div id="setStakeDiv" class="matchEdit">
		<h3>球队A</h3>
		<div>比分：<input type="text" id="match_ending" class="date_picker" style="text-align: center;" /></div>
		<div style="float:right; margin:20px 20px 0 0">
			<span class="sureBtn" onclick="setWinner();">确定</span>
			<span class="cancelBtn" onclick="$('#setStakeDiv').hide();$.showAddBg(false);" >取消</span>
		</div>
	</div>
	
	<div id="memberListDiv" class="memberList">
		<span class="cancelBtn" style="border:2px solid #6495ED; float:right; margin-top:-28px; margin-right:-6px;" onclick="$('#memberListDiv').hide();" >取消</span>
		<ul class="memberUl">
			<!-- <li title="报名IP: 10.10.20.146"><span>X</span>asd</li>
			<li title="报名IP: 10.10.20.146">asd</li> -->
		</ul>
	</div>
	
	
</body>
</html>