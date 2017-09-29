<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>抓取数据</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0,user-scalable=no" name="viewport" />
	<meta name="keywords" content="抓取数据" />
	<meta name="renderer" content="webkit|ie-comp|ie-stand" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	
	<link rel="stylesheet" href="${ctxPath}/res/css/jquery.treegrid.css"/>
	<link rel="stylesheet" href="${ctxPath}/res/css/core.css"/>
	
	<script type="text/javascript" src="${ctxPath}/res/jslib/jquery-1.8.3.min.js" ></script>
	<script type="text/javascript" src="${ctxPath}/res/jslib/jquery.treegrid.min.js"></script>
    
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
		
		var getDownloadTaskHeadTpls = function (type, size)  {
			var tpl = {
				'downloading': ['<tr class="treegrid-downloading">',
					'<td><b>正在下载资源</b></td>',
					'<td colspan="9"><i class="downloading">总共( <span style="color: red;">', size, '</span> )个</i></td>',
				'</tr>'].join(""),
				
				'download-finish': ['<tr class="treegrid-download-finish">',
					'<td><b>下载完成资源</b></td>',
					'<td colspan="9"><i class="download-finish">总共( <span style="color: red;">', size, '</span> )个</i></td>',
				'</tr>'].join(""),
				
				'download-error': ['<tr class="treegrid-download-error">',
					'<td><b>下载错误资源</b></td>',
					'<td colspan="9"><i class="download-error">总共( <span style="color: red;">', size, '</span> )个</i></td>',
				'</tr>'].join("")
			};
			
			return tpl[type];
		};
		
		function downloadTaskViewTemplate(downloadInfo, index, type) {
			return [
				"<tr align='center' class='treegrid-", index, " treegrid-parent-", type, "'>",
					"<td colspan='2'>", (~~index + 1), "</td>",
					"<td>", downloadInfo.worksTitle, "</td>",
					"<td>", downloadInfo.fileName, "</td>",
					"<td>", downloadInfo.type, "</td>",
					
					"<td>", humanFileSize(downloadInfo.size), "</td>",
					"<td>", downloadInfo.usedTime, "</td>",
					"<td>", dateFormat(downloadInfo.beginTime), "<br/>", dateFormat(downloadInfo.endTime), "</td>",
					"<td>", downloadInfo.url, "</td>",
				"</tr>"
			].join("");
		};
		
		function spiderTaskViewTemplate(spiderInfo) {
			return [
				"<tr align='center'>",
					"<td>", spiderInfo.site, "</td>",
					"<td> <img src='", spiderInfo.avatar, "' height='50'/> </td>",
					"<td> <a href='", spiderInfo.link, "' target='_blank'>", spiderInfo.title, "</a> </td>",
					"<td> <a href='", spiderInfo.blog, "' target='_blank'>", spiderInfo.author, "</a> </td>",
					"<td>", spiderInfo.type, "</td>",
					
					"<td>", spiderInfo.date, "</td>",
					"<td>", (spiderInfo.finish ? '已完成' : '未完成'), " (", spiderInfo.resources.length, "/", spiderInfo.downloadCompletedNum, ")</td>",
					"<td align='left'><p>热度：", spiderInfo.attract, "</p> <p>描述：", spiderInfo.comment, "</p> </td>",
				"</tr>"
			].join("");
		};
		
		function renderDownloadTaskView(data) {

			$("#downloadState").text(data.downloadState.desc);
			
			data = data.downloadHolder;
			var renderTpls = [];
			for (var type in data) {
				
				if (data[type]) {
					var tpls = [ getDownloadTaskHeadTpls(type, data[type].length) ];
					for (var i in data[type]) {
						var downloadInfo = data[type][i];
						tpls.push(downloadTaskViewTemplate(downloadInfo, i, type));
					}
					
					renderTpls.push(tpls.join(""));
				}
			}
			
			$(".download-task-view").find("tbody").html(renderTpls.join(""));
			$('.download-task-view').treegrid({ treeColumn: 1 });
		};
		
		function renderSpiderTaskView(data) {

			$("#spiderState").text(data.spiderState.desc);
			
			data = data.spiderHolder;
			for (var type in data) {
				
				if (data[type]) {
					var tpls = [];
					for (var i in data[type]) {
						var spiderInfo = data[type][i];
						tpls.push(spiderTaskViewTemplate(spiderInfo));
					}
					$("#" + type).find("tbody").html(tpls.join(""));
					$("." + type).find("span").text(data[type].length);
				}
			}
		};

		function queryDownloadTask (refreshMode) {
			refreshMode = refreshMode || "auto";

			if (!$("#taskKey").val()) {
				alert("未提取到执行key");
				return;
			}
			
			var params = {
				spiderKey: $("#taskKey").val(), 
				showDownloadHolder: $("#show-download-task-view").is(":checked"),
				showDownloadFinish: $("#show-download-finish-view").is(":checked"),
				showDownloading: $("#show-downloading-view").is(":checked"),
				showDownloadError: $("#show-download-error-view").is(":checked")
			};
			
			$.ajax({
				url: "${pageContext.request.contextPath}/single-task-progress",
				type: "POST",
				async: false,
				dataType: "json",      
	            data: params, 
				success: function(data) {
					if (data) {
						renderSpiderTaskView(data);
						renderDownloadTaskView(data);
					} else {
						alert("操作失败.");
					}
					
					if (refreshMode == "auto") {
						refreshDownloadTask();
					}
				},
				error: function(data) {
					if (~data.responseText.indexOf("<html>")) {
						alert("操作失败.");						
					} else {
						alert(data.responseText);
					}
					
					if (refreshMode == "auto") {
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
					if (data && !~data.indexOf("操作失败")) {
						$("#taskKey").val(data);
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
				
				window.setTimeout("queryDownloadTask()", intval);
			}
		};
		
		function bindData() {
			$(".choose-task-type").val("${taskType}");
			
			$("select[name='pathMode']").val("${taskType.options.pathMode}");
			$("select[name='namedMode']").val("${taskType.options.namedMode}");
			$("select[name='fileNameMode']").val("${taskType.options.fileNameMode}");
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
			
			$(".choose-task-type").change(function () {
				window.location = "${ctxPath}/console/single-task?taskType=" + $(this).val();
			});
			
			$(".go-onekey-task").click(function () {
				window.location = "${ctxPath}/console/onekey-task";
			});
			
			bindData();
			
			$('.download-task-view').treegrid({ treeColumn: 1 });
			
			$('#show-download-task-view').click(function () {
				if (this.checked) {
					$(".download-task-view").show();
				} else {
					$(".download-task-view").hide();
				}	
			});
		});
	</script>
</head>

<body>
	<div style="margin: 50px auto 50px;" align="center">
		<form action="${pageContext.request.contextPath}/run" method="post">
		
			<table width="1050">
				<thead>
					<tr>
						<th colspan="4">图片抓取器</th>
					</tr>
				</thead>
				
				<tr align="center">
					<td>任务类型:</td>
					<td align="left" style="padding-left: 2em;" colspan="3">
						<select name="taskType" class="choose-task-type">
							<option value="">选择任务类型</option>
							
							<c:forEach items="${taskTypes }" var="taskType">
								<option value="${taskType }">${taskType.desc }</option>
							</c:forEach>
						</select>
						
						<input type="text" name="spiderClazz" placeholder="目标Class" value="${taskType.clazz }" style="width: 450px;"/>
						<input type="text" id="taskKey" />
					</td>
				</tr>
					
				<tr align="center" valign="middle">
					<td width="20%">目标网址: </td>
					<td colspan="3" width="75%" align="left" style="padding-left: 2em;">
						<input type="text" name="spiderURL" placeholder="目标网址" value="${taskType.options.spiderURL }" style="width: 650px;"/>
					</td>
				</tr>
				
				<tr align="center">
					<td>站点域名:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="site" placeholder="站点域名" value="${taskType.options.site }" style="width: 250px;"/>
					</td>
					<td>同文件是否覆盖:</td>
					<td align="left" style="padding-left: 2em;">
						<label> <input type="checkbox" name="override" value="true"/>同文件覆盖</label>
					</td>
				</tr>
				
				<tr align="center">
					<td>任务名称:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="spiderName" placeholder="输入任务名称" value="${taskType.desc }" style="width: 250px;"/>
					</td>
					<td>匹配字符串:</td>
					<td align="left" style="padding-left: 2em;">
						<input type="text" name="match" placeholder="输入匹配字符串" value="${taskType.options.match }" style="width: 250px;"/>
					</td>
				</tr>
				
				<tr align="center" valign="middle">
					<td width="20%">开始页: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="number" name="beginPage" placeholder="输入开始页" value="${taskType.options.beginPage }" style="width: 250px;"/>
					</td>
					<td width="20%">抓取页数: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="number" name="pageNum" placeholder="输入页数" value="${taskType.options.pageNum }" style="width: 250px;"/>
					</td>
				</tr>

				<tr align="center" valign="middle">
					<td width="20%">最大抓取数: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="number" name="maxSpiderWorksNum" placeholder="抓取博文数" value="${taskType.options.maxSpiderWorksNum }" style="width: 250px;"/>
					</td>
					<td width="20%">下载任务数: </td>
					<td width="30%" align="left" style="padding-left: 2em;">
						<input type="number" name="maxDownloadTaskNum" placeholder="下载任务数" value="${taskType.options.maxDownloadTaskNum }" style="width: 250px;"/>
					</td>
				</tr>
				
				<tr align="center">
					<td width="20%">路径命名方式:</td>
					<td align="left" style="padding-left: 2em;" colspan="3">
						<select name="pathMode">
							<option value="">存储路径命名方式</option>
							
							<c:forEach items="${pathModes }" var="pathMode">
								<option value="${pathMode }">${pathMode.desc }</option>
							</c:forEach>
						</select>
						&nbsp;&nbsp;
						<select name="namedMode">
							<option value="">目录命名方式</option>
							
							<c:forEach items="${namedModes }" var="namedMode">
								<option value="${namedMode }">${namedMode.desc }</option>
							</c:forEach>
						</select>
						&nbsp;&nbsp;
						<select name="fileNameMode">
							<option value="">文件命名方式</option>
							
							<c:forEach items="${namedModes }" var="namedMode">
								<option value="${namedMode }">${namedMode.desc }</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				
				<tr align="right">
					<td colspan="4">
						<input type="reset" value="&nbsp;&nbsp;清 空&nbsp;&nbsp;"/>&nbsp;&nbsp;
						<input type="button" class="submit" value="&nbsp;&nbsp;执 行&nbsp;&nbsp;"/>
						
						<input type="button" class="go-onekey-task" value="&nbsp;&nbsp;一键任务&nbsp;&nbsp;" style="margin-right: 40px; margin-left: 300px;"/>
					</td>
				</tr>
			</table>
		</form>
		
		<hr/>

		<div class="tabBox">
			<div style="color: red; text-align: center; font-weight: bold; padding: 10px 0;">
			</div>
			
			<div class="tool-bar">
				<table width="100%" border="0">
					
					<tr align="center">
						<td align="left" style="padding-left: 2em; display: ;" width="20%">
							抓取状态：<b id="spiderState"></b>；
							&nbsp;&nbsp;&nbsp;&nbsp;
							下载状态：<b id="downloadState">${error }</b>；
						</td>
						
						<td align="right" style="padding-right: 2em; display: ;">
							<label> <input type="checkbox" id="show-download-finish-view" checked="checked" />显示下载完成</label>
							&nbsp;&nbsp;
							<label> <input type="checkbox" id="show-downloading-view" checked="checked" />显示正在下载</label>
							&nbsp;&nbsp;
							<label> <input type="checkbox" id="show-download-error-view" checked="checked" />显示等待下载</label>
							&nbsp;&nbsp;
							<label> <input type="checkbox" id="show-download-task-view" checked="checked" />显示下载情况</label>
						</td>
						
						<td width="35%" align="right">
							<label> 刷新间隔：<input type="text" class="refreshInterval" value="5" style="width: 50px;"/>(ms)</label>
							<input type="button" value="&nbsp;&nbsp;重启监视&nbsp;&nbsp;" class="restart-refresh"/>
							<input type="button" value="&nbsp;&nbsp;停止刷新&nbsp;&nbsp;" class="stop-refresh"/>
							<input type="button" value="&nbsp;&nbsp;手动刷新&nbsp;&nbsp;" onclick="queryDownloadTask('unAuto')"/>
						</td>
					</tr>
				</table>
			</div>

			<div class="tabMenu">
	            <ul>
					<li onclick="tabChange(this, 'tabContent')" class="active"><b>正在抓取图文</b><br/><i class="spidering">总共( <span style='color: red;'>0</span> )个</i></li>
					<li onclick="tabChange(this, 'tabContent')"><b>抓取完成图文</b><br/><i class="spider-finish">总共( <span style='color: red;'>0</span> )个</i></li>
					<li onclick="tabChange(this, 'tabContent')"><b>等待抓取图文</b><br/><i class="spider-wait">总共( <span style='color: red;'>0</span> )个</i></li>
	            </ul>
	        </div>
			
	        <div id="tabContent">
	        	<table width="100%" id="spidering">
					<thead>
						<tr>
							<th width="10%">站点</th>
							<th width="8%">博文封面</th>
							<th width="20%">博文名称</th>
							<th width="10%">作者</th>
							
							<th width="10%">类型</th>
							<th width="10%">发布时间</th>
							<th width="10%">下载情况</th>
							<th>其他</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
	        	
	        	<table width="100%" id="spider-finish" class="hidden">
					<thead>
						<tr>
							<th width="10%">站点</th>
							<th width="8%">博文封面</th>
							<th width="20%">博文名称</th>
							<th width="10%">作者</th>
							
							<th width="10%">类型</th>
							<th width="10%">发布时间</th>
							<th width="10%">下载情况</th>
							<th>其他</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				
				<table width="100%" id="spider-wait" class="hidden">
					<thead>
						<tr>
							<th width="10%">站点</th>
							<th width="8%">博文封面</th>
							<th width="20%">博文名称</th>
							<th width="10%">作者</th>
							
							<th width="10%">类型</th>
							<th width="10%">发布时间</th>
							<th width="10%">下载情况</th>
							<th>其他</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
	        	
	        </div>
	        
	        <table class="download-task-view" width="100%" style="margin-top: 10px;">
				<thead>
					<tr>
						<th width="10%">类型</th>
						<th width="10%">资源数量</th>
						<th width="20%">所属博文</th>
						<th width="20%">文件名称</th>
						<th width="5%">文件类型</th>

						<th width="5%">文件大小</th>
						<th width="10%">使用时间</th>
						<th width="10%">开始结束时间</th>
						<th width="15%">URL</th>
					</tr>
				</thead>

				<tbody>
				</tbody>
			</table>
	    </div>
	</div>
	
</body>
</html>
