<%--
  Created by IntelliJ IDEA.
  User: Marcus Münger
  Date: 2017-05-17
  Time: 09:10
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SSO login</title>
</head>
<body>
Redirect url:<br/>
<%= request.getParameter("redirect_uri")%><br/><br/>
Authenticating towards:<br/>
<%= request.getParameter("client_id")%><br/>
<input type="text" name="username" id="username" placeholder="username"/><br/>
<input type="password" name="password" id="password" placeholder="password"/><br/>
<input type="hidden" name="redirect_uri" value="<%= request.getParameter("redirect_uri") %>"/>
<input type="hidden" name="client_id" value="<%= request.getParameter("client_id") %>"/>
<input type="hidden" name="response_type" value="code"/>
<input type="button" onclick="submit()"/>
</body>
<script>
    function submit() {
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;
        var redirectURI = "<%= request.getParameter("redirect_uri") %>";
        var clientID = "<%= request.getParameter("client_id") %>";
        var request = new XMLHttpRequest();
        request.onload = function () {
            window.location = request.responseURL;
        };
        request.open("POST", "/api/authz/validate?username=" + username + "&password=" + password + "&redirect_uri=" + redirectURI + "&client_id=" + clientID + "&response_type=code", true);
        request.send("");
    }
</script>
</html>
