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
<form method="POST" action="/api/oauth/authz">
    <input type="text" name="username" id="username" placeholder="username"/><br/>
    <input type="password" name="password" id="password" placeholder="password"/><br/>
    <input type="hidden" name="redirect_uri" value="<%= request.getParameter("redirect_uri") %>"/>
    <input type="hidden" name="client_id" value="<%= request.getParameter("client_id") %>"/>
    <input type="hidden" name="response_type" value="code"/>
    <input type="submit" value="login"/>
</form>
</body>
</html>
