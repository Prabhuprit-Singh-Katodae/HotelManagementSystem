<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Employees"/><jsp:param name="subtitle" value="Hotel staff records (administrators only)"/><jsp:param name="active" value="employees"/></jsp:include>

<div class="card mb">
  <h3><c:out value="${empty editEmployee ? 'Add employee' : 'Edit employee #'.concat(editEmployee.employeeId)}"/></h3>
  <form action="${ctx}/admin/employees" method="post">
    <input type="hidden" name="action" value="${empty editEmployee ? 'add' : 'update'}">
    <c:if test="${not empty editEmployee}"><input type="hidden" name="employeeId" value="${editEmployee.employeeId}"></c:if>
    <div class="form-grid">
      <div class="form-group"><label for="name">Name</label><input type="text" id="name" name="name" value="<c:out value='${editEmployee.name}'/>" maxlength="100" required></div>
      <div class="form-group"><label for="role">Job role</label><input type="text" id="role" name="role" value="<c:out value='${editEmployee.role}'/>" maxlength="50" placeholder="Receptionist, Manager..." required></div>
      <div class="form-group"><label for="phone">Phone</label><input type="tel" id="phone" name="phone" value="<c:out value='${editEmployee.phone}'/>"></div>
      <div class="form-group"><label for="salary">Monthly salary (&#8377;)</label><input type="number" id="salary" name="salary" value="<c:out value='${editEmployee.salary}'/>" min="0" step="0.01" required></div>
    </div>
    <div class="form-actions">
      <button class="btn btn-gold" type="submit"><c:out value="${empty editEmployee ? 'Add employee' : 'Save changes'}"/></button>
      <c:if test="${not empty editEmployee}"><a class="btn btn-outline" href="${ctx}/admin/employees">Cancel</a></c:if>
    </div>
  </form>
</div>

<div class="card card-flush">
  <form action="${ctx}/admin/employees" method="get" class="search-row" style="padding:1.2rem 1.4rem 0">
    <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, role or phone">
    <button class="btn" type="submit">Search</button>
    <c:if test="${not empty q}"><a class="btn btn-outline" href="${ctx}/admin/employees">Clear</a></c:if>
  </form>
  <c:choose>
    <c:when test="${empty employees}"><div class="empty">No employees found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Name</th><th>Role</th><th>Phone</th><th class="right">Salary</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="e" items="${employees}">
          <tr><td><c:out value="${e.employeeId}"/></td><td><c:out value="${e.name}"/></td><td><c:out value="${e.role}"/></td><td><c:out value="${e.phone}"/></td>
            <td class="right">&#8377;<fmt:formatNumber value="${e.salary}" pattern="#,##0.00"/></td>
            <td class="actions"><a class="btn btn-outline btn-sm" href="${ctx}/admin/employees?edit=${e.employeeId}">Edit</a>
              <form action="${ctx}/admin/employees" method="post" class="inline-form" data-confirm="Delete this employee?">
                <input type="hidden" name="action" value="delete"><input type="hidden" name="employeeId" value="${e.employeeId}">
                <button class="btn btn-danger btn-sm" type="submit">Delete</button></form></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
