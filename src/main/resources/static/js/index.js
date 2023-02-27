$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题与内容
	var title = $("#recipient-name").val();
	var context = $("#message-text").val();

	//发布异步请求(post)
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"context":context},
		function (data){
			data = $.parseJSON(data);
			//提示框内显示返回信息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//2秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新网页
				if(code.data==0){
					window.location.reload();
				}
			}, 2000);
		}
	)



}