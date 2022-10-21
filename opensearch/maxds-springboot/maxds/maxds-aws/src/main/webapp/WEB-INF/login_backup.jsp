<!-- 
 MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 Data and content created by government employees within the scope of their employment
 are not subject to domestic copyright protection. 17 U.S.C. 105.
 -->

<html xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">
    <head>
        <meta name="name" content="loginpage">
        <title>MaxDS Login</title>
        <link href="../static/ext/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet" />
		<link href="../static/app/images/em.ico" rel="Shortcut Icon" type="image/x-icon" />
        <link rel="icon" href="resources/images/maxds.ico">
    </head>
    <body>      
        <div class="container">
            <div class="row">
                <div>
                    <h1 class="text-center login-title">Sign In</h1>

                    
                    <h5 style="font: bold;color: blue;text-align: right;margin-right: 20px">MaxDS (Build Version 2.0.0.2)</h5>
                    <p class="text-center" style="font-size: medium;">Please enter your SEC Username and Password. Username must exist in Active Directory to be a valid user to login.</p>

                </div>
                <div class="col-sm-6 col-md-4 col-md-offset-4">
                    <div class="account-wall">
                        <form role="form" action="login" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <div class="form-group">
                                <label for="username">Username:</label>
                                <input name="username" type="text" class="form-control" placeholder="Username" required autofocus>
                            </div>
                            <div class="form-group">
                                <label for="pwd">Password:</label>
                                <input id="pwd" name="password" type="password" class="form-control" placeholder="Password" required>
                            </div>                
                            <div class="form-group">
                                <button type="submit" class="btn btn-default">Login</button>
                            </div>
                            <div class="form-group">
                                <label for="checkbox1" >Invalidate existing login session</label>
                                <input  type="checkbox"	name="checkbox1" 
                                        value='0' 
                                        title='Invalidate existing login by user' onclick='javascript:if (this.checked) {
                                                    this.value = "1"
                                                } else {
                                                    this.value = "0"
                                                }'> 
                            </div>
                        </form>               
                    </div>


                </div>
            </div>
        </div>                    
    </body>
</html>


