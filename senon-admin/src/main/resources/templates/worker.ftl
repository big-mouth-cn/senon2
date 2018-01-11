<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>worker - senon</title>
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
                            <th>IP</th>
                            <th>PORT</th>
                            <th>REGISTER_TIME</th>
                        </tr>
                        </thead>
                        <tbody id="worker-list-tbody"></tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-sm-2">
        </div>
    </div>

</body>
</html>
<#include "/modules/footer.ftl"/>

<script type="text/javascript" src="${ctx.contextPath}/js/workers.js"></script>
