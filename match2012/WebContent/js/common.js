$.showAddBg = function(op, zIndex){
	if(op){
		if($("#bgAddDiv").length==0) {
			$("body").append("<div id=\"bgAddDiv\" class=\"bgAddCloud\"></div>");
		}
        $("#bgAddDiv").css({
            "height": $(document).height() + "px",
            "width": $(document).width() + "px"
        }).show();
        if (zIndex != null) {
            $("#bgAddDiv").css("zIndex", zIndex);
        }
	}
	else {
		$("#bgAddDiv").hide();
	}		
};

waitLoading = function(){
	alert("正在执行，请稍后...");
	return;
};

String.prototype.trim = function(){
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
};

String.prototype.isEmpty = function(){
    return (this == null || this.trim() == "");
};

String.prototype.isEnglish = function(){
	var reg = new RegExp(/^[a-z_][a-z0-9_]{0,}$/ig);
	return reg.test(this);
};

String.prototype.isStake= function(){
    var match = this.match(new RegExp(/^([1-9])|([1-9])([0-9]+)$/gi));
    return match != null;
};




