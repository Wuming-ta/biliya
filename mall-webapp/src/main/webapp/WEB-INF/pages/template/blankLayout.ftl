<#macro layout script="" css="" title="JFEAT">

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title}</title>
    <link rel="shortcut icon" href="${base}/favicon.ico" type="image/x-icon">
    <link rel="icon" href="${base}/favicon.ico" type="image/x-icon">
	  <!-- BOOTSTRAP STYLES-->
    <link href="${base}/assets/css/bootstrap.css" rel="stylesheet" />
    <!-- FONTAWESOME STYLES-->
    <link href="${base}/assets/css/font-awesome.css" rel="stylesheet" />
    <@css/>
</head>
<body>
    <div>
        <#nested/>
    </div>
    <!-- /. WRAPPER  -->

    <!-- SCRIPTS -AT THE BOTOM TO REDUCE THE LOAD TIME-->
    <!-- JQUERY SCRIPTS -->
    <script src="${base}/assets/js/jquery-1.10.2.js"></script>
    <!-- BOOTSTRAP SCRIPTS -->
    <script src="${base}/assets/js/bootstrap.min.js"></script>
    <!-- METISMENU SCRIPTS -->
    <script src="${base}/assets/js/jquery.metisMenu.js"></script>

    <@script/>
</body>
</html>
</#macro>