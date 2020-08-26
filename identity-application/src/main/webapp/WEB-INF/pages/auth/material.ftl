<#macro layout script="" css="" title="JFEAT" width=500>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="renderer" content="webkit">
    <link rel="shortcut icon" href="${base}/favicon.ico" type="image/x-icon">
    <link rel="icon" href="${base}/favicon.ico" type="image/x-icon">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

    <title>${title}</title>

    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
    <meta name="viewport" content="width=device-width" />

    <!-- Bootstrap core CSS     -->
    <link href="${base}/material-assets/assets/css/bootstrap.min.css" rel="stylesheet" />

    <!--  Material Dashboard CSS    -->
    <link href="${base}/material-assets/assets/css/material-dashboard.css" rel="stylesheet"/>

    <!--     Fonts and icons     -->
    <link href="${base}/assets/css/font-awesome.css" rel="stylesheet" />

    <link href="${base}/material-assets/assets/css/custom.css" rel="stylesheet" />
    <style>

        .mask {
            background: url(${base}/material-assets/assets/img/bg.jpeg) no-repeat;
            background-size: cover;
            opacity: 0.6;
            height: 100vh;
            width: 100%;
            position: absolute;
        }
        .container {
            top: 100px;
            width: ${width}px;
            position: relative;
        }
    </style>
    <@css/>
</head>
<body>

    <div id="wrapper">
        <div class="mask"></div>
        <div class="container">
            <div class="card">
                <div class="card-header" data-background-color="purple">
                    <div class="row">
                        <#if logo??>
                            <div class="col-xs-6">
                                <img src="${logo!}" height="40">
                            </div>
                        </#if>
                        <div class="col-xs-6">
                            <span style="line-height: 40px">${productName!'KQD'}</span>
                        </div>
                    </div>
                 </div>
                <div class="card-content">
                    <#nested/>
                </div>
            </div>
        </div>
    </div>

    <!--   Core JS Files   -->
	<script src="${base}/material-assets/assets/js/jquery-1.12.4.min.js" type="text/javascript"></script>
	<script src="${base}/material-assets/assets/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="${base}/material-assets/assets/js/material.min.js" type="text/javascript"></script>

    <!-- Material Dashboard javascript methods -->
    <script src="${base}/material-assets/assets/js/material-dashboard.js"></script>

    <script src="${base}/assets/js/jquery.validate.min.js"></script>
    <script src="${base}/assets/js/messages_zh.min.js"></script>
    <@script/>
</body>

</html>
</#macro>