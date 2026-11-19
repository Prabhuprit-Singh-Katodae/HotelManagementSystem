<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Booking received"/><jsp:param name="active" value="bookings"/></jsp:include>
<section class="section"><div class="container" style="max-width:720px">
  <div class="card confirm-box">
    <div class="tick">&#10003;</div>
    <h1>Booking received</h1>
    <p class="muted">Your booking number is <strong>#<c:out value="${booking.bookingId}"/></strong>. Its status is <span class="badge badge-${fn:toLowerCase(booking.status)}"><c:out value="${booking.status}"/></span>.</p>
    <div style="text-align:left;max-width:420px;margin:1.2rem auto">
      <div class="summary-row"><span>Room</span><strong>Room <c:out value="${booking.room.roomNumber}"/> (<c:out value="${booking.room.roomType.typeName}"/>)</strong></div>
      <div class="summary-row"><span>Check-in</span><span><c:out value="${booking.checkIn}"/></span></div>
      <div class="summary-row"><span>Check-out</span><span><c:out value="${booking.checkOut}"/></span></div>
      <div class="summary-row"><span>Nights</span><span><c:out value="${bill.nights}"/></span></div>
      <div class="summary-row"><span>Room charges</span><span class="summary-total">&#8377;<fmt:formatNumber value="${bill.total}" pattern="#,##0.00"/></span></div>
    </div>
    <div class="flex" style="justify-content:center">
      <a class="btn" href="${ctx}/customer/bookings">My bookings</a>
      <a class="btn btn-outline" href="${ctx}/customer/invoice?bookingId=${booking.bookingId}">View invoice</a>
    </div>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
