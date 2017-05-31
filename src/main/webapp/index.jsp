<%@ page import="se.tfmoney.microservice.oauth.business.ClientBean" %><%--
  Created by IntelliJ IDEA.
  User: Marcus Münger
  Date: 2017-05-31
  Time: 12:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String redirectURI = request.getParameter("redirect_uri");
    String clientID = request.getParameter("client_id");
    String scope = request.getParameter("scope");
    String clientName = new ClientBean().idToName(clientID).getEntity().toString();
%>
<html>
<head>
    <title>SSO-login</title>
</head>
<body>
<h3>
    You are authenticating against client:
    <i>
        <%=clientName%>
    </i>
</h3>
<form>
    <input type="text" placeholder="username"><br/>
    <input type="password" placeholder="password"><br/>
    <input type="submit" value="login">
</form>
</body>
</html>
