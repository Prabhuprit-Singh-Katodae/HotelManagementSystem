<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="My bookings"/><jsp:param name="active" value="bookings"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>My bookings</h1><p>Your stays, newest first.</p></div><a class="btn btn-gold" href="${ctx}/availability">Book a room</a></div>
  <c:choose>
    <c:when test="${empty bookings}"><div class="card empty">You have no bookings yet. <a href="${ctx}/availability">Find a room</a>.</div></c:when>
    <c:otherwise>
      <div class="booking-list">
        <c:forEach var="b" items="${bookings}">
          <div class="booking-card">
            <div>
              <div class="flex"><strong>Booking #<c:out value="${b.bookingId}"/></strong>
                <span class="badge badge-${fn:toLowerCase(b.status)}"><c:out value="${b.status}"/></span></div>
              <div>Room <c:out value="${b.room.roomNumber}"/> - <c:out value="${b.room.roomType.typeName}"/></div>
              <div class="muted small"><c:out value="${b.checkIn}"/> to <c:out value="${b.checkOut}"/> (<c:out value="${b.nights}"/> night(s))</div>
            </div>
            <div class="flex">
              <a class="btn btn-outline btn-sm" href="${ctx}/customer/invoice?bookingId=${b.bookingId}">Invoice</a>
              <c:if test="${b.status == 'Pending' or b.status == 'Confirmed'}">
                <form action="${ctx}/customer/bookings" method="post" class="inline-form" data-confirm="Cancel booking #${b.bookingId}?">
                  <input type="hidden" name="action" value="cancel"><input type="hidden" name="bookingId" value="${b.bookingId}">
                  <button class="btn btn-danger btn-sm" type="submit">Cancel booking</button>
                </form>
              </c:if>
            </div>
          </div>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
