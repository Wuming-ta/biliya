<#macro layout script="" css="" title="JFEAT" modal="">
<#include "materialMenu.ftl"/>
<#include "materialSubMenu.ftl"/>
<!doctype html>
<html lang="en">
<head>
	<meta charset="utf-8" />
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
    <@css/>
</head>

<body>

	<div class="wrapper">
	    <div class="sidebar" data-color="purple" data-image="${base}/material-assets/assets/img/sidebar-2.jpg">
			<div class="logo text-center">
				<a href="${base}/">
				    <#if logo?? && logo != "">
				        <img src="${logo}" height="45">
				    <#else>
					    <img src="${base}/material-assets/assets/img/logo.png" height="45">
					</#if>
				</a>
			</div>

	    	<div class="sidebar-wrapper">
	    	    <@menu menus 0 />
	    	</div>
		</div>

	    <div class="main-panel">

	        <button type="button" class="navbar-toggle" data-toggle="collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

	        <div class="content">
                <div class="card">
                	<div class="card-header" data-background-color="purple">
                	    <@subMenu menus 0 />
                	</div>
                	<div class="card-content">
                        <#nested />
                    </div>
                </div>
	        </div>

			<footer class="footer">
	            <div class="container-fluid">
	                <nav class="pull-left">
                        <ul>
                            <#if productVersion??>
                            <li>
                                <a>
                                    Version: ${productVersion!}
                                </a>
                            </li>
                            </#if>
                        </ul>
                    </nav>
	                <p class="copyright pull-right">
	                    &copy; <script>document.write(new Date().getFullYear())</script> ${productName!'Kqd'} All Right Reserved.
	                </p>
	            </div>
	        </footer>
	    </div>
	</div>

    <@modal />

</body>

	<!--   Core JS Files   -->
	<script src="${base}/material-assets/assets/js/jquery-1.12.4.min.js" type="text/javascript"></script>
	<script src="${base}/material-assets/assets/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${base}/material-assets/assets/js/material.min.js" type="text/javascript"></script>

	<!-- Material Dashboard javascript methods -->
	<script src="${base}/material-assets/assets/js/material-dashboard.js"></script>

    <script src="${base}/assets/js/jquery.qrcode.min.js"></script>
    <script src="${base}/assets/js/jquery.validate.min.js"></script>
    <script src="${base}/assets/js/messages_zh.min.js"></script>
    <#include "notification.js"/>
    <@script/>
</html>
</#macro>