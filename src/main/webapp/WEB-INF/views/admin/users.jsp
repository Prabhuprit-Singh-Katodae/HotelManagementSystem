<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="User accounts"/><jsp:param name="subtitle" value="Logins, roles and access (administrators only)"/><jsp:param name="active" value="users"/></jsp:include>

<div class="card mb">
  <h3>Create account</h3>
  <form action="${ctx}/admin/users" method="post">
    <input type="hidden" name="action" value="create">
    <div class="form-grid">
      <div class="form-group"><label for="username">Username</label><input type="text" id="username" name="username" required autocomplete="off"></div>
      <div class="form-group"><label for="password">Password (min 8)</label><input type="password" id="password" name="password" minlength="8" required autocomplete="new-password"></div>
      <div class="form-group"><label for="role">Role</label>
        <select id="role" name="role"><c:forEach var="r" items="${roles}"><option value="${r}" ${r == 'STAFF' ? 'selected' : ''}><c:out value="${r}"/></option></c:forEach></select></div>
      <div class="form-group"><label for="employeeId">Employee (STAFF / ADMIN)</label>
        <select id="employeeId" name="employeeId"><option value="">- none -</option>
          <c:forEach var="e" items="${employees}"><option value="${e.employeeId}"><c:out value="${e.name}"/> (<c:out value="${e.role}"/>)</option></c:forEach></select></div>
      <div class="form-group"><label for="customerId">Customer (CUSTOMER only)</label>
        <select id="customerId" name="customerId"><option value="">- none -</option>
          <c:forEach var="c" items="${customers}"><option value="${c.customerId}"><c:out value="${c.name}"/></option></c:forEach></select></div>
    </div>
    <button class="btn btn-gold" type="submit">Create account</button>
  </form>
</div>

<div class="card card-flush">
  <c:choose>
    <c:when test="${empty users}"><div class="empty">No accounts found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Linked to</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="u" items="${users}">
          <tr><td><c:out value="${u.userId}"/></td><td><strong><c:out value="${u.username}"/></strong></td>
            <td><form action="${ctx}/admin/users" method="post" class="flex" style="flex-wrap:nowrap">
                <input type="hidden" name="action" value="role"><input type="hidden" name="userId" value="${u.userId}">
                <select name="role" style="width:auto" aria-label="Role"><c:forEach var="r" items="${roles}"><option value="${r}" ${fn:toUpperCase(u.role) == r ? 'selected' : ''}><c:out value="${r}"/></option></c:forEach></select>
                <button class="btn btn-outline btn-sm" type="submit">Set</button></form></td>
            <td class="small"><c:choose>
                <c:when test="${not empty u.customer}">Customer: <c:out value="${u.customer.name}"/></c:when>
                <c:when test="${not empty u.employee}">Employee: <c:out value="${u.employee.name}"/></c:when>
                <c:otherwise><span class="muted">-</span></c:otherwise></c:choose></td>
            <td><span class="badge badge-${fn:toLowerCase(empty u.status ? 'active' : u.status)}"><c:out value="${empty u.status ? 'ACTIVE' : u.status}"/></span></td>
            <td class="actions">
              <form action="${ctx}/admin/users" method="post" class="inline-form">
                <input type="hidden" name="action" value="status"><input type="hidden" name="userId" value="${u.userId}">
                <input type="hidden" name="status" value="${fn:toUpperCase(u.status) == 'INACTIVE' ? 'ACTIVE' : 'INACTIVE'}">
                <button class="btn btn-outline btn-sm" type="submit"><c:out value="${fn:toUpperCase(u.status) == 'INACTIVE' ? 'Activate' : 'Deactivate'}"/></button></form>
              <form action="${ctx}/admin/users" method="post" class="inline-form" style="display:inline-flex;gap:.3rem" data-confirm="Reset the password for ${u.username}?">
                <input type="hidden" name="action" value="password"><input type="hidden" name="userId" value="${u.userId}">
                <input type="password" name="newPassword" minlength="8" placeholder="New password" style="width:130px" aria-label="New password" required autocomplete="new-password">
                <button class="btn btn-sm" type="submit">Reset</button></form></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
