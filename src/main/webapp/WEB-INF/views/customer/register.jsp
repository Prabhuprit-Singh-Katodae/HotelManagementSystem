<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Register"/><jsp:param name="active" value="login"/></jsp:include>
<div class="auth-wrap">
  <div class="auth-art"><h2>Create your guest account</h2><p>Book rooms online, see your invoices and leave feedback after your stay.</p></div>
  <div class="auth-form">
    <h1>Register</h1>
    <form action="${ctx}/register" method="post">
      <div class="form-group"><label for="name">Full name</label><input type="text" id="name" name="name" value="<c:out value='${param.name}'/>" maxlength="100" required></div>
      <div class="form-grid">
        <div class="form-group"><label for="phone">Phone</label><input type="tel" id="phone" name="phone" value="<c:out value='${param.phone}'/>" required></div>
        <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" value="<c:out value='${param.email}'/>" required></div>
      </div>
      <div class="form-group"><label for="address">Address (optional)</label><input type="text" id="address" name="address" value="<c:out value='${param.address}'/>" maxlength="255"></div>
      <div class="form-group"><label for="username">Username</label><input type="text" id="username" name="username" value="<c:out value='${param.username}'/>" required autocomplete="username"><span class="hint">3-30 letters, digits, dot or underscore.</span></div>
      <div class="form-grid">
        <div class="form-group"><label for="password">Password</label><input type="password" id="password" name="password" minlength="8" required autocomplete="new-password"></div>
        <div class="form-group"><label for="confirm">Confirm password</label><input type="password" id="confirm" name="confirm" minlength="8" required autocomplete="new-password"></div>
      </div>
      <button class="btn btn-gold btn-block" type="submit">Create account</button>
    </form>
    <p class="switch">Already registered? <a href="${ctx}/login">Log in</a></p>
  </div>
</div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
