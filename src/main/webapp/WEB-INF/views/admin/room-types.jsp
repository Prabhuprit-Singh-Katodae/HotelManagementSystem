<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Room types"/><jsp:param name="subtitle" value="Types and nightly prices"/><jsp:param name="active" value="room-types"/></jsp:include>

<div class="card mb">
  <h3><c:out value="${empty editType ? 'Add room type' : 'Edit room type'}"/></h3>
  <form action="${ctx}/admin/room-types" method="post">
    <input type="hidden" name="action" value="${empty editType ? 'add' : 'update'}">
    <c:if test="${not empty editType}"><input type="hidden" name="typeId" value="${editType.typeId}"></c:if>
    <div class="form-grid">
      <div class="form-group"><label for="typeName">Type name</label><input type="text" id="typeName" name="typeName" value="<c:out value='${editType.typeName}'/>" maxlength="50" required></div>
      <div class="form-group"><label for="price">Price per night (&#8377;)</label><input type="number" id="price" name="price" value="<c:out value='${editType.price}'/>" min="1" step="0.01" required></div>
    </div>
    <div class="form-actions">
      <button class="btn btn-gold" type="submit"><c:out value="${empty editType ? 'Add type' : 'Save changes'}"/></button>
      <c:if test="${not empty editType}"><a class="btn btn-outline" href="${ctx}/admin/room-types">Cancel</a></c:if>
    </div>
  </form>
</div>

<div class="card card-flush">
  <c:choose>
    <c:when test="${empty roomTypes}"><div class="empty">No room types yet.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Type</th><th>Price / night</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="t" items="${roomTypes}">
          <tr><td><c:out value="${t.typeId}"/></td><td><c:out value="${t.typeName}"/></td>
            <td>&#8377;<fmt:formatNumber value="${t.price}" pattern="#,##0.00"/></td>
            <td class="actions">
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/room-types?edit=${t.typeId}">Edit</a>
              <c:if test="${sessionScope.loggedUser.admin}">
                <form action="${ctx}/admin/room-types" method="post" class="inline-form" data-confirm="Delete this room type?">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="typeId" value="${t.typeId}">
                  <button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
              </c:if></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
