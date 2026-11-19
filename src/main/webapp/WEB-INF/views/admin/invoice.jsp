<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Shared by staff (/admin/invoice) and customers (/customer/invoice) --%>
<c:choose>
  <c:when test="${staffView}"><jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Invoice"/><jsp:param name="active" value="payments"/></jsp:include></c:when>
  <c:otherwise><jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Invoice"/><jsp:param name="active" value="bookings"/></jsp:include><section class="section"><div class="container"></c:otherwise>
</c:choose>

<div class="flex no-print mb">
  <button class="btn" type="button" onclick="window.print()">Print invoice</button>
  <c:choose>
    <c:when test="${staffView}"><a class="btn btn-outline" href="${ctx}/admin/booking-details?id=${bill.booking.bookingId}">Back to booking</a></c:when>
    <c:otherwise><a class="btn btn-outline" href="${ctx}/customer/bookings">Back to my bookings</a></c:otherwise>
  </c:choose>
</div>

<div class="invoice">
  <div class="invoice-head">
    <div><h2>Grandstay Hotel</h2>
      <div class="muted small">Front desk, open 24 hours<br>Phone: +91 00000 00000 &middot; frontdesk@grandstay.example<br><em>(Change these hotel details in invoice.jsp)</em></div></div>
    <div class="right"><h2>INVOICE</h2><div>Booking #<c:out value="${bill.booking.bookingId}"/></div>
      <div class="muted small">Status: <c:out value="${bill.booking.status}"/></div></div>
  </div>

  <div class="grid grid-2 mb">
    <div><strong>Billed to</strong><br><c:out value="${bill.booking.customer.name}"/><br>
      <span class="muted small"><c:out value="${bill.booking.customer.phone}"/><br><c:out value="${bill.booking.customer.email}"/><br><c:out value="${bill.booking.customer.address}"/></span></div>
    <div><strong>Stay</strong><br>Room <c:out value="${bill.booking.room.roomNumber}"/> (<c:out value="${bill.booking.room.roomType.typeName}"/>)<br>
      <span class="muted small">Check-in: <c:out value="${bill.booking.checkIn}"/><br>Check-out: <c:out value="${bill.booking.checkOut}"/><br><c:out value="${bill.nights}"/> night(s)</span></div>
  </div>

  <div class="table-wrap"><table>
    <thead><tr><th>Description</th><th class="right">Qty</th><th class="right">Rate (&#8377;)</th><th class="right">Amount (&#8377;)</th></tr></thead>
    <tbody>
      <tr><td>Room <c:out value="${bill.booking.room.roomNumber}"/> - <c:out value="${bill.booking.room.roomType.typeName}"/> (per night)</td>
        <td class="right"><c:out value="${bill.nights}"/></td>
        <td class="right"><fmt:formatNumber value="${bill.roomRate}" pattern="#,##0.00"/></td>
        <td class="right"><fmt:formatNumber value="${bill.roomCharges}" pattern="#,##0.00"/></td></tr>
      <c:forEach var="sb" items="${bill.serviceLines}">
        <tr><td><c:out value="${sb.service.serviceName}"/> <span class="muted small">(<c:out value="${sb.serviceDate}"/>)</span></td>
          <td class="right"><c:out value="${sb.quantity}"/></td>
          <td class="right"><fmt:formatNumber value="${sb.service.price}" pattern="#,##0.00"/></td>
          <td class="right"><fmt:formatNumber value="${sb.lineTotal}" pattern="#,##0.00"/></td></tr>
      </c:forEach>
    </tbody>
  </table></div>

  <table class="totals mt">
    <tr><td>Room charges</td><td class="right">&#8377;<fmt:formatNumber value="${bill.roomCharges}" pattern="#,##0.00"/></td></tr>
    <tr><td>Service charges</td><td class="right">&#8377;<fmt:formatNumber value="${bill.serviceCharges}" pattern="#,##0.00"/></td></tr>
    <tr class="grand"><td>Total</td><td class="right">&#8377;<fmt:formatNumber value="${bill.total}" pattern="#,##0.00"/></td></tr>
    <tr><td>Paid</td><td class="right">&#8377;<fmt:formatNumber value="${bill.paid}" pattern="#,##0.00"/></td></tr>
    <tr><td><strong>Balance due</strong></td><td class="right"><strong>&#8377;<fmt:formatNumber value="${bill.balance}" pattern="#,##0.00"/></strong></td></tr>
  </table>

  <div class="grid grid-2 mt">
    <div><strong>Payment method</strong><br><c:out value="${bill.paymentMethods}"/></div>
    <div><strong>Payment status</strong><br><span class="badge badge-${fn:toLowerCase(fn:substringBefore(bill.paymentStatus, ' '))}"><c:out value="${bill.paymentStatus}"/></span></div>
  </div>
  <c:if test="${not empty bill.payments}">
    <p class="small muted mt">Payments received:
      <c:forEach var="p" items="${bill.payments}" varStatus="st"><c:out value="${p.paymentDate}"/> &#8377;<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/> (<c:out value="${p.paymentMethod}"/>)<c:if test="${not st.last}">; </c:if></c:forEach></p>
  </c:if>
  <p class="note-demo">This is a college-project invoice. Payments are recorded by hotel staff or through a demonstration flow; no real payment gateway is connected.</p>
</div>

<c:choose>
  <c:when test="${staffView}"><jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/></c:when>
  <c:otherwise></div></section><jsp:include page="/WEB-INF/views/common/footer.jsp"/></c:otherwise>
</c:choose>
