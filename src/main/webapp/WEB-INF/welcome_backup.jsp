<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Multiple Application XBRL Delivery System</title>
		<link href="../static/ext/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet" />	
		<link href="../static/app/images/maxds.ico" rel="Shortcut Icon" type="image/x-icon" />
	</head>
	<body>
	<!--  <nav class="navbar navbar-light" style="background-color: #e3f2fd;">
		  <div class="container-fluid">
		    <div class="col-sm-1" style="height: 20px; left: 2px;width: 60px;" >
                        <img src="../static/app/images/global-banner-seal-sm1.png" alt="Securties and Exchange Commission" />
                    </div>
		    <div class="navbar-header">
		      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
		        <span class="sr-only">Toggle navigation</span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		        <span class="icon-bar"></span>
		      </button>
		      <a class="navbar-brand" href="/#/home" target="outputFrame">MAXDS</a>
		    </div>
            <div id="navbar" class="navbar-collapse collapse">
            
                <ul class="nav navbar-nav">
                  <li><a href="/#/home" target="outputFrame" title="MAXDS Home">Hello ${ userDisplayName } &nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-home"
                         aria-hidden="true"></span> Home</a></li>
                  <li><a href="/#/export" target="outputFrame" title="Export"><span class="glyphicon glyphicon-cloud-upload"
                         aria-hidden="true"></span> Export</a></li>
                  <li><a href="/#/accuracyTest" target="outputFrame" title="Accuracy test"><span
                         aria-hidden="true"></span> Accuracy test</a></li>
                         <li><a href="/#/processTerm" target="outputFrame" title="Processing Status"><span
                         aria-hidden="true"></span> Process All Terms</a></li>
                          <li><a href="/#/manageTerm" target="outputFrame" title="Manage Term Maps"><span
                         aria-hidden="true"></span> Manage Term Maps</a></li>
                          <li><a href="/#/status" target="outputFrame" title="Processing Status"><span
                         aria-hidden="true"></span> Status</a></li>
                          <li><a href="/#/admin" target="outputFrame" title="Administration"><span
                         aria-hidden="true"></span> Administration</a></li>
                         <li><a href="logout" target="outputFrame" title="Log Off"><span class="glyphicon glyphicon glyphicon-off" aria-hidden="true"></span> Logoff</a></li>
                         
                          <li><a title="Help" href="https://confluence.ad.sec.gov/display/DOS/SEC+MAXDS+4.2+Web+User%27s+Guide" target="_blank">
                     <span class="glyphicon glyphicon-info-sign" aria-hidden="true" ></span> Help</a></li>
                     <li><a href="/#/profile" target="outputFrame" title="Administration"><span class="glyphicon glyphicon-user" aria-hidden="true" ></span> Profile</a></li>                     
                    
                </ul>
                <ul class="nav navbar-nav navbar-right">
                	 <li  ><a ui-sref="preferenceFeedback" title="Submit Feedback" href="/#/submit">
	                   		 <span class="glyphicon glyphicon-log-in" aria-hidden="true" ></span> Submit Feedback
	                     </a>
                    </li>
                </ul>
                
            </div>   
                      
        </div>

   
    </nav> -->  
		<article>
            <iframe width="100%" height="900" name="outputFrame" frameborder="0" src="/maxds"></iframe>
        </article>
       <%--  <footer>
            <a target="article" href="http://www2.sec.gov" style="color: black;">U.S. Securities and Exchange Commission</a>  (EMA-Version-${version}-${timestamp } )
        </footer> --%>
		<script src="../static/ext/jquery-1.12.1/js/jquery-1.12.1.min.js"></script>
		 <script src="../static/ext/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
	<script>
		function handle(e) {
			if (e.keyCode === 13) {
				e.preventDefault(); 
				document.getElementById("link1").click();
			}
		}
	</script>
</body>
</html>