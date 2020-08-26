<#macro layout script="" css="" title="JFEAT" modal="" preCss=false>
<#include "header.ftl"/>
<#include "footer.ftl"/>
<#include "menu.ftl"/>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="renderer" content="webkit">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title}</title>
    <link rel="shortcut icon" href="${base}/favicon.ico" type="image/x-icon">
    <link rel="icon" href="${base}/favicon.ico" type="image/x-icon">
    <#if preCss == true><@css/></#if>
	  <!-- BOOTSTRAP STYLES-->
    <link href="${base}/assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FONTAWESOME STYLES-->
    <link href="${base}/assets/css/font-awesome.css" rel="stylesheet" />
    <!-- CUSTOM STYLES-->
    <link href="${base}/assets/css/custom.css" rel="stylesheet" />
    <#if preCss == false><@css/></#if>
    <script>
        window.UEDITOR_SERVER_URL = "${base}/ueditor/qiniu";
    </script>
</head>
<body>
    <div id="wrapper">
        <@header/>
        <!-- /. NAV TOP  -->

        <@menu/>
        <!-- /. NAV SIDE  -->

        <div id="page-wrapper" >

                <#nested/>

             <!-- /. PAGE INNER  -->

        </div>
        <!-- /. PAGE WRAPPER  -->


    </div>
    <!-- /. WRAPPER  -->

    <@footer/>

    <@modal />

    <!-- SCRIPTS -AT THE BOTOM TO REDUCE THE LOAD TIME-->
    <!-- JQUERY SCRIPTS -->
    <script src="${base}/assets/js/jquery-1.10.2.js"></script>
    <!-- BOOTSTRAP SCRIPTS -->
    <script src="${base}/assets/js/bootstrap.min.js"></script>
    <!-- METISMENU SCRIPTS -->
    <script src="${base}/assets/js/jquery.metisMenu.js"></script>
    <!-- back to top -->
    <script src="${base}/assets/js/jquery.goup.min.js"></script>
    <!-- CUSTOM SCRIPTS -->
    <script src="${base}/assets/js/custom.js"></script>

    <script src="${base}/assets/js/jquery.qrcode.min.js"></script>
    <script src="${base}/assets/js/jquery.validate.min.js"></script>
    <script src="${base}/assets/js/messages_zh.min.js"></script>
    <#include "notification.js"/>
    <@script/>
    <script type="text/javascript">
            $(document).ready(function () {
                $.goup({
                    trigger: 200,
                    bottomOffset: 20,
                    locationOffset: 20,
                    title: 'TOP',
                    titleAsText: true
                });
            });
    </script>
</body>
</html>
</#macro>