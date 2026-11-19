<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Customers"/><jsp:param name="subtitle" value="Add, search, update and remove guests"/><jsp:param name="active" value="customers"/></jsp:include>

<div class="card mb">
  <h3><c:out value="${empty editCustomer ? 'Add customer' : 'Edit customer #'.concat(editCustomer.customerId)}"/></h3>
  <form action="${ctx}/admin/customers" method="post">
    <input type="hidden" name="action" value="${empty editCustomer ? 'add' : 'update'}">
    <c:if test="${not empty editCustomer}"><input type="hidden" name="customerId" value="${editCustomer.customerId}"></c:if>
    <div class="form-grid">
      <div class="form-group"><label for="name">Name</label><input type="text" id="name" name="name" value="<c:out value='${editCustomer.name}'/>" maxlength="100" required></div>
      <div class="form-group"><label for="phone">Phone</label><input type="tel" id="phone" name="phone" value="<c:out value='${editCustomer.phone}'/>" required></div>
      <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" value="<c:out value='${editCustomer.email}'/>"></div>
      <div class="form-group"><label for="address">Address</label><input type="text" id="address" name="address" value="<c:out value='${editCustomer.address}'/>" maxlength="255"></div>
    </div>
    <div class="form-actions">
      <button class="btn btn-gold" type="submit"><c:out value="${empty editCustomer ? 'Add customer' : 'Save changes'}"/></button>
      <c:if test="${not empty editCustomer}"><a class="btn btn-outline" href="${ctx}/admin/customers">Cancel</a></c:if>
    </div>
  </form>
</div>

<div class="card card-flush">
  <form action="${ctx}/admin/customers" method="get" class="search-row" style="padding:1.2rem 1.4rem 0">
    <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, phone or email">
    <button class="btn" type="submit">Search</button>
    <c:if test="${not empty q}"><a class="btn btn-outline" href="${ctx}/admin/customers">Clear</a></c:if>
  </form>
  <c:choose>
    <c:when test="${empty customers}"><div class="empty">No customers found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Name</th><th>Phone</th><th>Email</th><th>Address</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${customers}">
          <tr><td><c:out value="${c.customerId}"/></td><td><c:out value="${c.name}"/></td><td><c:out value="${c.phone}"/></td>
            <td><c:out value="${c.email}"/></td><td><c:out value="${c.address}"/></td>
            <td class="actions">
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/customers?edit=${c.customerId}">Edit</a>
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/bookings?newBooking=1&amp;customerId=${c.customerId}">New booking</a>
              <c:if test="${sessionScope.loggedUser.admin}">
                <form action="${ctx}/admin/customers" method="post" class="inline-form" data-confirm="Delete this customer?">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="customerId" value="${c.customerId}">
                  <button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
              </c:if>
            </td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
