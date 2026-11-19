<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Rooms"/><jsp:param name="subtitle" value="Manage rooms and their current status"/><jsp:param name="active" value="rooms"/></jsp:include>

<div class="card mb">
  <h3><c:out value="${empty editRoom ? 'Add room' : 'Edit room '.concat(editRoom.roomNumber)}"/></h3>
  <form action="${ctx}/admin/rooms" method="post">
    <input type="hidden" name="action" value="${empty editRoom ? 'add' : 'update'}">
    <c:if test="${not empty editRoom}"><input type="hidden" name="roomId" value="${editRoom.roomId}"></c:if>
    <div class="form-grid">
      <div class="form-group"><label for="roomNumber">Room number</label><input type="text" id="roomNumber" name="roomNumber" value="<c:out value='${editRoom.roomNumber}'/>" maxlength="20" required></div>
      <div class="form-group"><label for="typeId">Room type</label>
        <select id="typeId" name="typeId" required>
          <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}" ${editRoom.roomType.typeId == t.typeId ? 'selected' : ''}><c:out value="${t.typeName}"/> (&#8377;<fmt:formatNumber value="${t.price}" pattern="#,##0"/>)</option></c:forEach>
        </select></div>
      <div class="form-group"><label for="status">Status</label>
        <select id="status" name="status">
          <c:forEach var="s" items="${roomStatuses}"><option value="${s}" ${fn:toLowerCase(editRoom.status) == fn:toLowerCase(s) ? 'selected' : ''}><c:out value="${s}"/></option></c:forEach>
        </select></div>
    </div>
    <div class="form-actions">
      <button class="btn btn-gold" type="submit"><c:out value="${empty editRoom ? 'Add room' : 'Save changes'}"/></button>
      <c:if test="${not empty editRoom}"><a class="btn btn-outline" href="${ctx}/admin/rooms">Cancel</a></c:if>
      <a class="btn btn-outline" href="${ctx}/admin/room-types">Manage room types</a>
    </div>
  </form>
</div>

<div class="card card-flush">
  <form action="${ctx}/admin/rooms" method="get" class="search-row" style="padding:1.2rem 1.4rem 0">
    <select name="type" onchange="this.form.submit()" aria-label="Filter by type"><option value="">All room types</option>
      <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}" ${selectedType == t.typeId ? 'selected' : ''}><c:out value="${t.typeName}"/></option></c:forEach></select>
    <noscript><button class="btn" type="submit">Filter</button></noscript>
  </form>
  <c:choose>
    <c:when test="${empty rooms}"><div class="empty">No rooms found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Room</th><th>Type</th><th>Price / night</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${rooms}">
          <tr><td><c:out value="${r.roomId}"/></td><td><strong><c:out value="${r.roomNumber}"/></strong></td>
            <td><c:out value="${r.roomType.typeName}"/></td>
            <td>&#8377;<fmt:formatNumber value="${r.roomType.price}" pattern="#,##0.00"/></td>
            <td>
              <form action="${ctx}/admin/rooms" method="post" class="flex" style="flex-wrap:nowrap">
                <input type="hidden" name="action" value="status"><input type="hidden" name="roomId" value="${r.roomId}">
                <select name="status" style="width:auto" aria-label="Room status">
                  <c:forEach var="s" items="${roomStatuses}"><option value="${s}" ${fn:toLowerCase(r.status) == fn:toLowerCase(s) ? 'selected' : ''}><c:out value="${s}"/></option></c:forEach>
                </select>
                <button class="btn btn-outline btn-sm" type="submit">Update</button>
              </form></td>
            <td class="actions">
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/rooms?edit=${r.roomId}">Edit</a>
              <c:if test="${sessionScope.loggedUser.admin}">
                <form action="${ctx}/admin/rooms" method="post" class="inline-form" data-confirm="Delete room ${r.roomNumber}?">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="roomId" value="${r.roomId}">
                  <button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
              </c:if></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
