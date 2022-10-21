<!-- 
 MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 Data and content created by government employees within the scope of their employment
 are not subject to domestic copyright protection. 17 U.S.C. 105.
 -->
<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <meta name="name" content="loginerrorpage">
        <title>Login Error</title>
        <link href="resources/styles/bootstrap.min.css" rel="stylesheet" >
        <link href="resources/styles/bootstrap-theme.min.css" rel="stylesheet" >
        <link rel="icon" href="resources/images/maxds.ico">
    </head>
    <body>
        <h1 class="text-center login-title">Sign In Error</h1>
        <c:if test="${not empty error}">
		<div class="error">Error Message : ${error}</div>
        </c:if>
                
        <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
            <div class="text-center">
      <font color="red">
        Your login attempt was not successful due to <br/><br/>
        <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </font>
      </div>
        </c:if>
                
        <p class="text-center">Click <a href="Login"><b>here</b></a> to login again.</p>
    </body>
</html>
