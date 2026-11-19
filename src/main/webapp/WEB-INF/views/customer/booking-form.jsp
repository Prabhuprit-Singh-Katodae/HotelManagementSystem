<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Confirm booking"/><jsp:param name="active" value="rooms"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>Confirm your booking</h1><p>Check the details below. Availability is checked again when you confirm.</p></div></div>
  <div class="checkout-grid">
    <div class="card">
      <h3>Your stay</h3>
      <c:if test="${not available}">
        <div class="alert alert-error">Room <c:out value="${room.roomNumber}"/> is not available for these dates. Please
          <a href="${ctx}/availability?checkIn=${checkIn}&amp;checkOut=${checkOut}">search for another room</a>.</div>
      </c:if>
      <form action="${ctx}/customer/book" method="post">
        <input type="hidden" name="roomId" value="${room.roomId}">
        <div class="form-grid">
          <div class="form-group"><label for="bfCheckIn">Check-in</label><input type="date" id="bfCheckIn" name="checkIn" min="${today}" value="${checkIn}" required></div>
          <div class="form-group"><label for="bfCheckOut">Check-out</label><input type="date" id="bfCheckOut" name="checkOut" min="${checkIn}" value="${checkOut}" required></div>
        </div>
        <p class="hint">If you change the dates here, the price below updates and availability is re-checked when you confirm.</p>
        <div class="form-actions">
          <button class="btn btn-gold" type="submit" ${available ? '' : 'disabled'}>Confirm booking</button>
          <a class="btn btn-outline" href="${ctx}/availability?checkIn=${checkIn}&amp;checkOut=${checkOut}">Choose another room</a>
        </div>
      </form>
    </div>
    <aside class="card">
      <h3>Booking summary</h3>
      <div class="summary-row"><span>Room</span><strong>Room <c:out value="${room.roomNumber}"/> (<c:out value="${room.roomType.typeName}"/>)</strong></div>
      <div class="summary-row"><span>Rate per night</span><span>&#8377;<fmt:formatNumber value="${room.roomType.price}" pattern="#,##0.00"/></span></div>
      <div class="summary-row" id="estimate" data-rate="${room.roomType.price}"><span>Nights</span><span id="estNights"><c:out value="${nights}"/></span></div>
      <div class="summary-row"><span>Estimated room total</span><span class="summary-total" id="estTotal">&#8377;<fmt:formatNumber value="${total}" pattern="#,##0.00"/></span></div>
      <p class="hint mt">Your booking starts as <strong>Pending</strong> and is confirmed by the hotel. Services such as breakfast are added to your invoice later.</p>
    </aside>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
