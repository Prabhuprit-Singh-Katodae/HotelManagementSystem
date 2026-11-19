<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Hotel services"/><jsp:param name="subtitle" value="Breakfast, laundry and other extras with their prices"/><jsp:param name="active" value="services"/></jsp:include>

<div class="card mb">
  <h3><c:out value="${empty editService ? 'Add service' : 'Edit service'}"/></h3>
  <form action="${ctx}/admin/services" method="post">
    <input type="hidden" name="action" value="${empty editService ? 'add' : 'update'}">
    <c:if test="${not empty editService}"><input type="hidden" name="serviceId" value="${editService.serviceId}"></c:if>
    <div class="form-grid">
      <div class="form-group"><label for="serviceName">Service name</label><input type="text" id="serviceName" name="serviceName" value="<c:out value='${editService.serviceName}'/>" maxlength="100" required></div>
      <div class="form-group"><label for="price">Price (&#8377;)</label><input type="number" id="price" name="price" value="<c:out value='${editService.price}'/>" min="0" step="0.01" required></div>
    </div>
    <div class="form-actions">
      <button class="btn btn-gold" type="submit"><c:out value="${empty editService ? 'Add service' : 'Save changes'}"/></button>
      <c:if test="${not empty editService}"><a class="btn btn-outline" href="${ctx}/admin/services">Cancel</a></c:if>
    </div>
  </form>
</div>

<div class="card card-flush">
  <c:choose>
    <c:when test="${empty services}"><div class="empty">No services yet.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Service</th><th class="right">Price</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${services}">
          <tr><td><c:out value="${s.serviceId}"/></td><td><c:out value="${s.serviceName}"/></td>
            <td class="right">&#8377;<fmt:formatNumber value="${s.price}" pattern="#,##0.00"/></td>
            <td class="actions"><a class="btn btn-outline btn-sm" href="${ctx}/admin/services?edit=${s.serviceId}">Edit</a>
              <c:if test="${sessionScope.loggedUser.admin}">
                <form action="${ctx}/admin/services" method="post" class="inline-form" data-confirm="Delete this service?">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="serviceId" value="${s.serviceId}">
                  <button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
              </c:if></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
