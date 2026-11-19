<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Staff / admin sign-in page. Guests use /login instead. --%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Staff sign in | Grandstay</title>
  <link rel="icon" href="${ctx}/assets/images/logo.svg" type="image/svg+xml">
  <link rel="stylesheet" href="${ctx}/assets/css/common.css">
  <link rel="stylesheet" href="${ctx}/assets/css/auth.css">
</head>
<body class="staff-body">
  <main class="staff-panel">
    <a class="brand" href="${ctx}/home"><img src="${ctx}/assets/images/logo.svg" alt="" style="width:30px;height:30px;background:var(--wine);border-radius:5px">Grandstay</a>
    <h1>Staff sign in</h1>
    <p class="muted small">For hotel staff and administrators.</p>
    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>
    <form action="${ctx}/admin/login" method="post">
      <input type="hidden" name="next" value="<c:out value='${next}'/>">
      <div class="form-group"><label for="username">Username</label><input type="text" id="username" name="username" value="<c:out value='${param.username}'/>" required autofocus autocomplete="username"></div>
      <div class="form-group"><label for="password">Password</label><input type="password" id="password" name="password" required autocomplete="current-password"></div>
      <button class="btn btn-block" type="submit">Sign in</button>
    </form>
    <p class="small muted mt">Guest? <a href="${ctx}/login">Guest login</a> &middot; <a href="${ctx}/home">Back to website</a></p>
  </main>
</body>
</html>
