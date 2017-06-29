<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>抓取数据</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no" name="viewport" />
	<meta name="keywords" content="抓取数据" />
	<meta name="renderer" content="webkit|ie-comp|ie-stand" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/jslib/jquery-1.8.3.min.js" ></script>
	<style type="text/css">
		td {
			border: 1px solid black;
			background-color: #eeeeee;
			padding: 5px;
		}
		
		table {
			border-collapse: collapse;
			border-spacing: 5px;
		}
		
		th {
			border: 1px solid black;
			background: #9DACBF;
			color: #FFF;
			height: 20px;
			line-height: 20px
		}
		
		tfoot th {
			background: #cfcfcf;
			color: #000;
		}
		
		tfoot th em {
			color: #f40;
			font-size: 14px;
		}
		
		body {
			font-family: "宋体", "Arial", "Helvetica";
			font-size: 12px;
			font-style: normal;
			font-weight: lighter;
		}
		
		.head {
			background-color: #ccc;
			font-weight: bold;
		}
		
		.head b {
			color: #337ab7;
		}
		
		.odd td {
			background-color: white;
		}
		
		.even td {
			background-color: lavender;
		}
		
		.own {
			margin-left: 4em;
		}
			
		ul {
			list-style: none;
			padding: 0;
			margin: 0;
			font-size: 12px;
		}
		
	    .tabBox{
	        width: 92%;
	        margin: 35px auto;
	    }
		
	    .tabMenu{
	        height: 45px;
	        width: 100%;
	        border-left: 1px solid #ccc;
	        border-top: 1px solid #ccc;
	    }
	    
	    .tabMenu li {
	        float: left;
	        height: 45px;
	        width: 33.2%;
	        border-right: 1px solid #ccc;
	        border-bottom: 1px solid #ccc;
	        text-align: center;
	        line-height: 25px;
	        background: #eee;
	        cursor: pointer;
	    }
	    
	    .tabMenu .active {
	        background: #9dacbf;
	        color: white;
	    }
	    
	    #tabContent {
	        border:1px solid #ccc;
	        border-top-width:0;
	        width: 100% - 4;
	    }
	    
	    #tabContent .hidden {
	        display: none;
	    }
	    
	    b {
	    	font-weight: bold;
	    }
	    
	    a {
		    cursor: pointer;
		    display: inline-block;
		    height: 25px;
		    margin-left: 5px;
		    padding: 0 11px;
		    
		    color: blue;
		    outline: 0 none;
		    text-decoration: none;
		}
	    
	    a.hover {
	    	color: red;
	    }
	</style>
	
	<script type="text/javascript">
		
		function FormatNumberLength(num, length) {
			var r = "" + num;
			while (r.length < length) {
				r = "0" + r;
			}
			return r;
		}
	
		function humanFileSize(bytes) {
			var thresh = 1024;
			if (bytes < thresh)
				return bytes + ' B';
			var units = [ 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB' ];
			var u = -1;
			do {
				bytes /= thresh;
				++u;
			} while (bytes >= thresh);
			return bytes.toFixed(1) + ' ' + units[u];
		};
		
		function dateFormat(t) {
			//var now = new Date();
			//var date = new Date(t * 1000 - now.getTimezoneOffset() * 60000);
			//return date.toLocaleString();
			var date = new Date(t);
			return date.getFullYear() + "-" + FormatNumberLength(date.getMonth() + 1, 2)
					+ "-" + FormatNumberLength(date.getDate(), 2) + " "
					+ FormatNumberLength(date.getHours(), 2) + ":"
					+ FormatNumberLength(date.getMinutes(), 2) + ":"
					+ FormatNumberLength(date.getSeconds(), 2);
		}
	
		function tabChange(obj, id){
		    var tabLi = obj.parentNode.getElementsByTagName("li");
		    var tabContents = document.getElementById(id).getElementsByTagName("table");
		    for(var i=0; i<tabContents.length; i++){
		        if(obj == tabLi[i]){
		            tabLi[i].className = "active";
		            tabContents[i].className = "";
		        } else {
		            tabLi[i].className = "";
		            tabContents[i].className = "hidden";
		        }
		    }
		};
		
		function taskViewTemplate(downloadInfo) {
			return [
				"<tr align='center'>",
					"<td>", downloadInfo.fileName, "</td>",
					"<td>", downloadInfo.type, "</td>",
					
					"<td>", humanFileSize(downloadInfo.size), "</td>",
					"<td>", downloadInfo.usedTimed, "</td>",
					"<td>", dateFormat(downloadInfo.beginTime), "</td>",
					"<td>", dateFormat(downloadInfo.endTime), "</td>",
					"<td>", downloadInfo.targetURL, "</td>",
				"</tr>"
			].join("");
		};
		
		function renderTaskView(data) {
			
			for (var type in data) {
				
				if (data[type]) {
					var tpls = [];
					for (var i in data[type]) {
						var downloadInfo = data[type][i];
						tpls.push(taskViewTemplate(downloadInfo));
					}
					$("#" + type).find("tbody").html(tpls.join(""));
					$("." + type).find("span").text(data[type].length);
				}
			}
		};

		function queryDownloadTask (auto) {
			auto = auto || true
			
			$.ajax({
				url: "${pageContext.request.contextPath}/task-info",
				type: "POST",
				async: false,
				dataType: "json",      
	            data: {}, 
				success: function(data) {
					if (data) {
						renderTaskView(data);
					} else {
						alert("操作失败.");
					}
					
					if (auto) {
						refreshDownloadTask();
					}
				},
				error: function(data) {
					if (~data.responseText.indexOf("<html>")) {
						alert("操作失败.");						
					} else {
						alert(data.responseText);
					}
					
					if (auto) {
						refreshDownloadTask();
					}
				}
			});
		};
		
		function runDownloadTask () {
			
			var param = $("form").serialize();
			
			$.ajax({
				url: "${pageContext.request.contextPath}/exec-task",
				type: "POST",
				async: false,
				dataType: "text",      
	            data: param, 
				success: function(data) {
					if (data) {
						
						queryDownloadTask();
					} else {
						alert("操作失败.");
					}
				},
				error: function(data) {
					if (~data.responseText.indexOf("<html>")) {
						alert("操作失败.");						
					} else {
						alert(data.responseText);
					}
				}
			});
		};
		
		var refreshed = true;
		function refreshDownloadTask() {

			if (refreshed) {
				var intval = ~~$(".refreshInterval").val();
				intval = intval * 1000;
				
				window.setTimeout("queryDownloadTask ()", intval);
			}
		};
		
		$(function () {
			
			$(".restart-refresh").click(function () {
				refreshed = true;
				refreshDownloadTask();
			});
			
			$(".stop-refresh").click(function () {
				refreshed = false;
			});
			
			$(".submit").click(function () {
				runDownloadTask();
			});
		});
	</script>
</head>

<body>
	<div style="margin: 50px auto 50px;" align="center">
		<form action="${pageContext.request.contextPath}/run" method="post">
		
			<table width="950">
				<thead>
					<tr>
						<th colspan="4">图片抓取器</th>
					</tr>
				</thead>
					
				<tr align="center" valign="middle">
					<td width="20%">目标网址: </td>
					<td colspan="3" width="75%" align="left" style="padding-left: 2em;">
						<input type="text" name="targetURL" placeholder="目标网址" value="${param.targetURL }" style="width: 650px;"/>
					</td>
				</tr>
				
				<tr align="center" valign="middle">
					<td width="20%">开始页: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="text" name="startPage" placeholder="输入开始页" value="${param.startPage }" style="width: 250px;"/>
					</td>
					<td width="20%">结束页: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="text" name="endPage" placeholder="输入结束页" value="${param.endPage }" style="width: 250px;"/>
					</td>
				</tr>
				
				<tr align="center">
					<td>保存路径:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="saveImagePath" placeholder="输入保存路径" value="${param.saveImagePath }" style="width: 250px;"/>
					</td>
					<td>路径匹配字符串:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="match" placeholder="输入路径匹配字符串" value="${param.match }" style="width: 250px;"/>
					</td>
				</tr>
				
				<tr align="center">
					<td width="20%">最大下载数:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="maxDownloadNum" placeholder="输入最大下载数" value="${param.maxDownloadNum }" style="width: 250px;"/>
					</td>
					<td align="center" style="padding-left: 2em;" colspan="2">
						<label> <input type="checkbox" name="overwrite" value="true"/>相同文件覆盖</label>&nbsp;&nbsp;&nbsp;&nbsp;
						<label> <input type="checkbox" name="usedPageDir" checked="checked" value="true"/>采用页码路径规则</label>
					</td>
				</tr>
				
				<tr align="center">
					<td colspan="4">
						<input type="reset" value="&nbsp;&nbsp;清 空&nbsp;&nbsp;"/>&nbsp;&nbsp;
						<input type="button" class="submit" value="&nbsp;&nbsp;执 行&nbsp;&nbsp;"/>
					</td>
				</tr>
			</table>
		</form>
		
		<hr/>
		
		<div class="tabBox">
			<div id="loading" style="color: red; text-align: center; font-weight: bold; padding: 10px 0;">${error }</div>
			
			<div class="tool-bar">
				<table width="100%" border="0">
					
					<tr align="center">
						<td align="left" style="padding-left: 2em; display: none;" width="20%">
						</td>
						
						<td align="right" style="padding-right: 2em; display: none;">
						</td>
						
						<td width="25%" align="right">
							<label> 刷新间隔：<input type="text" class="refreshInterval" value="5" />(ms)</label>
							<input type="button" value="&nbsp;&nbsp;重启监视&nbsp;&nbsp;" class="restart-refresh"/>
							<input type="button" value="&nbsp;&nbsp;停止刷新&nbsp;&nbsp;" class="stop-refresh"/>
							<input type="button" value="&nbsp;&nbsp;手动刷新&nbsp;&nbsp;" onclick="queryDownloadTask(false)"/>
						</td>
					</tr>
				</table>
			</div>
			
			<div class="tabMenu">
	            <ul>
					<li onclick="tabChange(this, 'tabContent')" class="active"><b>正在下载资源</b><br/><i class="downloading">总共( <span style='color: red;'>0</span> )个</i></li>
					<li onclick="tabChange(this, 'tabContent')"><b>下载完成资源</b><br/><i class="download-finish">总共( <span style='color: red;'>0</span> )个</i></li>
					<li onclick="tabChange(this, 'tabContent')"><b>等待下载资源</b><br/><i class="wait-download">总共( <span style='color: red;'>0</span> )个</i></li>
	            </ul>
	        </div>
			
	        <div id="tabContent">
	        	<table width="100%" id="downloading">
					<thead>
						<tr>
							<th width="20%">文件名称</th>
							<th width="5%">文件类型</th>
							
							<th width="5%">文件大小</th>
							<th width="10%">使用时间</th>
							<th width="15%">任务开始时间</th>
							<th width="15%">任务结束时间</th>
							<th>URL</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				
				<table width="100%" id="download-finish" class="hidden">
					<thead>
						<tr>
							<th width="20%">文件名称</th>
							<th width="5%">文件类型</th>
							
							<th width="5%">文件大小</th>
							<th width="10%">使用时间</th>
							<th width="15%">任务开始时间</th>
							<th width="15%">任务结束时间</th>
							<th>URL</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				
				<table width="100%" id="wait-download" class="hidden">
					<thead>
						<tr>
							<th width="20%">文件名称</th>
							<th width="5%">文件类型</th>
							
							<th width="5%">文件大小</th>
							<th width="10%">使用时间</th>
							<th width="15%">任务开始时间</th>
							<th width="15%">任务结束时间</th>
							<th>URL</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
	        </div>
	    </div>
	</div>
</body>
</html>
