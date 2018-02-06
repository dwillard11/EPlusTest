$(document).ready(function () {
	$("#username").val($.cookie("username"));
});

function login(){
	var requestURL = window.location.href;
	if($("#username").val()=="" || $("#password").val()==""){
    	$("#msgArea").show();
    	$("#msgArea").text("Username or password can not be empty");
    	return;
	}
	$("#loginSubmit").button('loading');
	$.ajax({  
        type : "GET",
        url : "login.do",
	    data:{  
			userId : $("#username").val(),
			password : $("#password").val(),
			requestURL: requestURL,
	    },  
        success : function(data) {
            if ( data.result == "success") {  
                $.cookie("username", $("#username").val(), { expires: 7 });//save username for 7 days
            	window.location.href="index.html"; 
            } else if (data.result == "share_success"){
            	$.cookie("username", $("#username").val(), { expires: 7 });//save username for 7 days
            	window.location.href = "index" + data.share_destination;
            } else {
            	$("#msgArea").show();
            	$("#msgArea").text(data.msg);
            	$("#loginSubmit").button('reset');
            }  
        }  
    });  
}

function keyLogin(e){
    e = e ? e : window.event;
    var keyCode = e.which ? e.which : e.keyCode;
    if(keyCode == 13){
    	login();
    }
}