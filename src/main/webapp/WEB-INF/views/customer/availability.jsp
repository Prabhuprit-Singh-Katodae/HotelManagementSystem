<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Check availability"/><jsp:param name="active" value="availability"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>Check availability</h1><p>Choose your dates to see the rooms that are free.</p></div></div>
  <div class="card mb">
    <form action="${ctx}/availability" method="get" class="form-grid" style="align-items:end">
      <div class="form-group"><label for="avIn">Check-in</label><input type="date" id="avIn" name="checkIn" min="${today}" value="${empty checkIn ? today : checkIn}" required></div>
      <div class="form-group"><label for="avOut">Check-out</label><input type="date" id="avOut" name="checkOut" min="${tomorrow}" value="${empty checkOut ? tomorrow : checkOut}" required></div>
      <div class="form-group"><label for="avType">Room type</label>
        <select id="avType" name="typeId"><option value="">Any type</option>
          <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}" ${typeId == t.typeId ? 'selected' : ''}><c:out value="${t.typeName}"/></option></c:forEach>
        </select></div>
      <div class="form-group"><button class="btn btn-gold" type="submit">Search</button></div>
    </form>
  </div>

  <c:if test="${searched}">
    <h2><c:out value="${fn:length(rooms)}"/> room(s) available for <c:out value="${nights}"/> night(s)</h2>
    <c:choose>
      <c:when test="${empty rooms}"><div class="card empty">No rooms are free on these dates. Try different dates or another room type.</div></c:when>
      <c:otherwise>
        <div class="grid grid-3">
          <c:forEach var="r" items="${rooms}">
            <article class="room-card">
              <div class="photo"><img src="${ctx}/assets/images/${r.roomType.imageFile}" alt="" loading="lazy"></div>
              <div class="body">
                <h3>Room <c:out value="${r.roomNumber}"/> <span class="muted small">- <c:out value="${r.roomType.typeName}"/></span></h3>
                <div class="price">&#8377;<fmt:formatNumber value="${r.roomType.price}" pattern="#,##0"/> <span class="muted small">/ night</span></div>
                <div class="muted small">Total for <c:out value="${nights}"/> night(s): <strong>&#8377;<fmt:formatNumber value="${r.roomType.price * nights}" pattern="#,##0.00"/></strong></div>
                <div class="actions">
                  <a class="btn btn-outline btn-sm" href="${ctx}/room?id=${r.roomId}">Details</a>
                  <a class="btn btn-gold btn-sm" href="${ctx}/customer/book?roomId=${r.roomId}&amp;checkIn=${checkIn}&amp;checkOut=${checkOut}">Book this room</a>
                </div>
              </div>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </c:if>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
