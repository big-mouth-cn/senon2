<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>Doggie</title>
	<#include "/modules/header.ftl"/>
    <style type="text/css">
    </style>
</head>
<body>
<#include "/modules/menu.ftl"/>

<div class="container-fluid">
 	<div class="row main">
 		<div class="col-sm-2">
 		</div>
 		<div class="col-sm-8">
 			<div class="panel panel-default">
			  <div class="panel-body">
			    <table class="table">
		      <thead>
		        <tr>
		          <th>名称</th>
		          <th>基础地址</th>
		          <th>操作</th>
		        </tr>
		      </thead>
		      <tbody id="worker-list-tbody">
		         <tr>
		          <td><input type="text" class="form-control" id="name-add-input" placeholder="名称"></td>
		          <td><input type="text" class="form-control" id="url-add-input" placeholder="http://127.0.0.1:8082"></td>
		          <td>
		          	<button type="button" class="save-btn btn btn-default">添加</button>
		          </td>
		        </tr>
		      </tbody>
		    </table>
			  </div>
			</div>
 		</div>
 		<div class="col-sm-2">
 		</div>
 	</div>

<input type="hidden" id="head_menu_index_input" value="2">
<input type="hidden" id="status" value="${status}">
<input type="hidden" id="message" value="${message}">
<form id="add-form" action="${ctx.contextPath}/workers/save" method="POST">
<input type="hidden" name="name"/>
<input type="hidden" name="url"/>
</form>

<form id="update-form" action="${ctx.contextPath}/workers/save" method="POST">
<input type="hidden" name="id"/>
<input type="hidden" name="name"/>
<input type="hidden" name="url"/>
</form>

<form id="delete-form" action="${ctx.contextPath}/workers/delete" method="POST">
<input type="hidden" name="id"/>
</form>

<#include "/modules/footer.ftl"/>
<script type="text/javascript" src="${ctx.contextPath}/resources/js/workers.js"></script>
</body>
</html>
