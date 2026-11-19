<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="My profile"/><jsp:param name="active" value="profile"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>My profile</h1><p>Keep your contact details up to date.</p></div></div>
  <div class="grid grid-2">
    <div class="card">
      <h3>Contact details</h3>
      <form action="${ctx}/customer/profile" method="post">
        <input type="hidden" name="action" value="update">
        <div class="form-group"><label for="name">Full name</label><input type="text" id="name" name="name" value="<c:out value='${customer.name}'/>" maxlength="100" required></div>
        <div class="form-group"><label for="phone">Phone</label><input type="tel" id="phone" name="phone" value="<c:out value='${customer.phone}'/>" required></div>
        <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" value="<c:out value='${customer.email}'/>"></div>
        <div class="form-group"><label for="address">Address</label><input type="text" id="address" name="address" value="<c:out value='${customer.address}'/>" maxlength="255"></div>
        <button class="btn btn-gold" type="submit">Save changes</button>
      </form>
    </div>
    <div class="card">
      <h3>Change password</h3>
      <form action="${ctx}/customer/profile" method="post">
        <input type="hidden" name="action" value="password">
        <div class="form-group"><label for="currentPassword">Current password</label><input type="password" id="currentPassword" name="currentPassword" required autocomplete="current-password"></div>
        <div class="form-group"><label for="newPassword">New password</label><input type="password" id="newPassword" name="newPassword" minlength="8" required autocomplete="new-password"></div>
        <div class="form-group"><label for="confirmPassword">Confirm new password</label><input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required autocomplete="new-password"></div>
        <button class="btn" type="submit">Change password</button>
      </form>
    </div>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
