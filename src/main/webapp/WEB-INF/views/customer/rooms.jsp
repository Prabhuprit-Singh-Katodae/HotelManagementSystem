<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Rooms"/><jsp:param name="active" value="rooms"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head">
    <div><h1>Rooms</h1><p>All rooms with their nightly price. Pick dates to see if a room is free.</p></div>
    <form action="${ctx}/rooms" method="get" class="flex">
      <select name="type" onchange="this.form.submit()" aria-label="Filter by room type">
        <option value="">All types</option>
        <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}" ${selectedType == t.typeId ? 'selected' : ''}><c:out value="${t.typeName}"/></option></c:forEach>
      </select>
      <noscript><button class="btn btn-sm" type="submit">Filter</button></noscript>
    </form>
  </div>
  <c:choose>
    <c:when test="${empty rooms}"><div class="card empty">No rooms found.</div></c:when>
    <c:otherwise>
      <div class="grid grid-3">
        <c:forEach var="r" items="${rooms}">
          <article class="room-card">
            <div class="photo"><img src="${ctx}/assets/images/${r.roomType.imageFile}" alt="Room <c:out value='${r.roomNumber}'/>" loading="lazy"></div>
            <div class="body">
              <div class="row"><h3>Room <c:out value="${r.roomNumber}"/></h3>
                <c:if test="${fn:toLowerCase(r.status) == 'maintenance'}"><span class="badge badge-maintenance">Maintenance</span></c:if></div>
              <div class="muted"><c:out value="${r.roomType.typeName}"/></div>
              <div class="price">&#8377;<fmt:formatNumber value="${r.roomType.price}" pattern="#,##0"/> <span class="muted small">/ night</span></div>
              <div class="actions">
                <a class="btn btn-outline btn-sm" href="${ctx}/room?id=${r.roomId}">Details</a>
                <a class="btn btn-gold btn-sm" href="${ctx}/room?id=${r.roomId}#book">Book</a>
              </div>
            </div>
          </article>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
