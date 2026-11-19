<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Log in"/><jsp:param name="active" value="login"/></jsp:include>
<div class="auth-wrap">
  <div class="auth-art"><h2>Welcome back</h2><p>Log in to see your bookings, invoices and feedback.</p></div>
  <div class="auth-form">
    <h1>Log in</h1>
    <form action="${ctx}/login" method="post">
      <input type="hidden" name="next" value="<c:out value='${next}'/>">
      <div class="form-group"><label for="username">Username</label><input type="text" id="username" name="username" value="<c:out value='${param.username}'/>" required autofocus autocomplete="username"></div>
      <div class="form-group"><label for="password">Password</label><input type="password" id="password" name="password" required autocomplete="current-password"></div>
      <button class="btn btn-gold btn-block" type="submit">Log in</button>
    </form>
    <p class="switch">New guest? <a href="${ctx}/register">Create an account</a><br>Hotel staff? <a href="${ctx}/admin/login">Staff login</a></p>
  </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
