<#macro layout script="" css="" title="JFEAT" width=500>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="renderer" content="webkit">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title!}</title>
    <link rel="shortcut icon" href="${base}/favicon.ico" type="image/x-icon">
    <link rel="icon" href="${base}/favicon.ico" type="image/x-icon">
    <!-- BOOTSTRAP STYLES-->
    <link href="${base}/assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FONTAWESOME STYLES-->
    <link href="${base}/assets/css/font-awesome.css" rel="stylesheet" />
    <!-- CUSTOM STYLES-->
    <link href="${base}/assets/css/custom.css" rel="stylesheet" />
    <style>
        html {
            height: 100%;
        }
        body {
            height: 100%;
            padding-top: 0px;
            display: flex;
            justify-content:center;
            align-items:Center;
        }
        .mask {
            <#if bgImage??>
            background: url('${bgImage}') no-repeat;
            <#else>
            background: url('${base}/assets/img/bg.jpeg') no-repeat;
            </#if>
            background-size: cover;
            opacity: 0.6;
            height: 100vh;
            width: 100%;
            position: absolute;
            top: 0px;
            left: 0px;
        }

    </style>
    <@css/>
</head>
<body>
    <div id="wrapper">
        <div class="mask"></div>
        <div class="container" >
           <#nested />
        </div>
    </div>


    <!-- SCRIPTS -AT THE BOTOM TO REDUCE THE LOAD TIME-->
    <!-- JQUERY SCRIPTS -->
    <script src="${base}/assets/js/jquery-1.10.2.js"></script>
    <!-- BOOTSTRAP SCRIPTS -->
    <script src="${base}/assets/js/bootstrap.min.js"></script>
    <!-- METISMENU SCRIPTS -->
    <script src="${base}/assets/js/jquery.metisMenu.js"></script>

    <!-- CUSTOM SCRIPTS -->
    <script src="${base}/assets/js/custom.js"></script>

    <script src="${base}/assets/js/jquery.validate.min.js"></script>
    <script src="${base}/assets/js/messages_zh.min.js"></script>
    <@script/>
</body>
</html>
</#macro>