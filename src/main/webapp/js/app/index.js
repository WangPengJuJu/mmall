$(function(){
	//获取用户信息
	getUserInfo();
	$(".nav").on("click",function(){
		var index = $(".nav").index(this);
		switch(index){
			case 0 :
			window.location.href = "./index.html"
			break;
			case 1 :
			window.location.href = "./productList.html"
			break;
			case 2 :
			window.location.href = "./productScene.html"
			break;
			case 3 :
			window.location.href = "./aboutUs.html"
			break;
		}
	});
	$(".pagination-point").mouseenter(function(){
		// $(".pagination-point").removeClass("active");
		// $(this).addClass("active");
		clearInterval(inreval1);
		headerIndex = $(this).index() - 0;
		if($(this).hasClass("active")){
			
		}else{
			showImg(headerIndex);
		}
	});
	$(".pagination-point").mouseleave(function(){
		initImg();
	});
	$(".section").mouseenter(function(){
		$(".section-left").addClass("animateLeft");
		$(".section-right").addClass("animateRight");
	});
	$(".section").mouseleave(function(){
		$(".section-left").removeClass("animateLeft");
		$(".section-right").removeClass("animateRight");
	});
	$(".product").mouseenter(function(){
		$(this).find('.product-img-title').addClass("animate-focus");
	});
	$(".product").mouseleave(function(){
		$(this).find('.product-img-title').removeClass("animate-focus");
	});
	$(".product-scene").mouseenter(function(){
		$(this).find('.layer-learnMore').addClass("animate-layer-learnMore");
		$(this).find('.learnMore-box').fadeIn(1000);
	});
	$(".product-scene").mouseleave(function(){
		$(this).find('.layer-learnMore').removeClass("animate-layer-learnMore");
		$(this).find('.learnMore-box').fadeOut(1000);
	});
	$(".news-left").mouseenter(function(){
		headerIndex = $(this).index() - 0;
		clearInterval(inreval1);
	});
	$(".news-left").mouseleave(function(){
		initImg();
	});
	// //检测网页滚动
	// window.onscroll = function(){ 
	// 	var t = document.documentElement.scrollTop || document.body.scrollTop; 
	// 	if( t >= 300 ) { 
	// 	  $(".header-box").css({"position":"fixed","top":"0px","left":"20%"});
	// 	} else { 
	// 		$(".header-box").css({"position":"","top":"","left":""});
	// 	} 
	//   } 
	var index = 0;
	$(".section-left").on("click",function(){
		if(index <2){
			index++;
			$(".bigBox").css({"transform":"translateX(-"+31.7*index+"rem)","-webkit-transform":"translateX(-"+31.7*index+"rem)"});
		}else{
			$(this).find(".fa-ban").fadeIn(1).delay(100).fadeOut();
		}
	});
	$(".section-right").on("click",function(){
		if(index > 0){
			index--;
			$(".bigBox").css({"transform":"translateX(-"+31.7*index+"rem)","-webkit-transform":"translateX(-"+31.7*index+"rem)"});
		}else{
			$(this).find(".fa-ban").fadeIn(1).delay(100).fadeOut();
		}	
	});
	// $(".button-learnMore").on("click",function(){
	// 	window.location.href = "hotDetails.html";
	// });
	initImg();
});
function initImg(){
	inreval1 = setInterval(function(){
		switchImg();
	},3000);
}
var headerIndex = 0;
var inreval1;
function switchImg(){
	var length = $(".header-news").find(".news-imgs").length;
	headerIndex++;
	headerIndex = (headerIndex + length)%length;
	showImg(headerIndex);
}
function showImg(headerIndex){
	$(".header-news").find(".news-imgs").hide();
	$(".header-news").find(".news-imgs").eq(headerIndex).fadeIn(1000);
	$(".news-left").hide();
	$(".news-left").eq(headerIndex).fadeIn(1000);
	$('.pagination li').removeClass('active');
	$('.pagination li').eq(headerIndex).addClass('active');
}
function login(){
	window.location.href="./login.html";
}
function getUserInfo(){
	var url = "http://localhost:8088/user/getUserInfo.do";
	$.ajax({
		url : url,
		type : "get",
		dataType : "json",
		data : "",
		async : false,
		success : function(data) {
			if(data.status == 0){
				$("#lg-bx").hide();
				$("#user-bx img").attr("src","");
				$("#user-bx span").eq(0).text(data.data.username);
				$("#user-bx").show();
			}else{
				$("#lg-bx").show();
				$("#user-bx img").attr("src","");
				$("#user-bx span").eq(0).text("");
				$("#user-bx").hide();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			
		}
	});
}
//退出登陆
function loginOut(){
	var url = "http://localhost:8088/user/loginOut.do";
	$.ajax({
		url : url,
		type : "get",
		dataType : "json",
		data : "",
		async : false,
		success : function(data) {
			if(data.status == 0){
				alert("退出登陆成功！");
				getUserInfo();
			}else{
				alert("退出登陆失败！");
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {

		}
	});
}
