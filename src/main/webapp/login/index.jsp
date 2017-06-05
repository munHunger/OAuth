<%@ page import="se.tfmoney.microservice.oauth.business.ClientBean" %><%--
  Created by IntelliJ IDEA.
  User: Marcus Münger
  Date: 2017-05-17
  Time: 09:10
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SSO login</title>
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Lobster">
</head>
<body class="w3-light-gray">
<div class="w3-panel w3-orange" style="margin-top: 0px;">
    <span class="w3-lobster w3-opacity"><h2>24Money microservice authentication</h2></span>
</div>
<div class="w3-card-4 w3-white" style="width: 33%; margin: auto; margin-top:150px;">
    <div class="w3-padding-large">
        <h3>
            You are authenticating towards the service
            <i>
                <%= new ClientBean().idToName(request.getParameter("client_id")).getEntity().toString() %>
            </i>
        </h3>
    </div>
    <form method="POST" class="w3-container w3-padding-large" action="/api/oauth/authz">
        <input type="text"
               class="w3-input w3-animate-input"
               style="width:50%"
               name="username"
               id="username"
               placeholder="username"/><br/>
        <input type="password"
               class="w3-input w3-animate-input"
               style="width:50%"
               name="password"
               id="password"
               placeholder="password"/><br/>
        <input type="hidden" name="redirect_uri" value="<%= request.getParameter("redirect_uri") %>"/>
        <input type="hidden" name="client_id" value="<%= request.getParameter("client_id") %>"/>
        <input type="hidden" name="response_type" value="code"/>
        <input type="submit" class="w3-btn w3-padding w3-orange" value="login"/>
    </form>
</div>
</body>
</html>
