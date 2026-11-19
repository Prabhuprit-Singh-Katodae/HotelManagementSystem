<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Booking details"/><jsp:param name="active" value="bookings"/></jsp:include>

<p><a href="${ctx}/admin/bookings">&larr; All bookings</a></p>
<div class="grid grid-2 mb">
  <div class="card">
    <div class="flex-between"><h3>Booking #<c:out value="${booking.bookingId}"/></h3>
      <span class="badge badge-${fn:toLowerCase(booking.status)}"><c:out value="${booking.status}"/></span></div>
    <dl class="detail-list">
      <dt>Customer</dt><dd><c:out value="${booking.customer.name}"/></dd>
      <dt>Phone</dt><dd><c:out value="${booking.customer.phone}"/></dd>
      <dt>Email</dt><dd><c:out value="${booking.customer.email}"/></dd>
      <dt>Room</dt><dd>Room <c:out value="${booking.room.roomNumber}"/> (<c:out value="${booking.room.roomType.typeName}"/>)</dd>
      <dt>Check-in</dt><dd><c:out value="${booking.checkIn}"/></dd>
      <dt>Check-out</dt><dd><c:out value="${booking.checkOut}"/></dd>
      <dt>Nights</dt><dd><c:out value="${bill.nights}"/></dd>
    </dl>
    <div class="form-actions mt">
      <c:forEach var="s" items="${nextStatuses}">
        <form action="${ctx}/admin/bookings" method="post" class="inline-form" data-confirm="Change status to ${s}?">
          <input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${booking.bookingId}">
          <input type="hidden" name="newStatus" value="${s}"><input type="hidden" name="back" value="/admin/booking-details?id=${booking.bookingId}">
          <button class="btn ${s == 'Cancelled' ? 'btn-danger' : 'btn-ok'}" type="submit"><c:out value="${s == 'Confirmed' ? 'Confirm booking' : (s == 'Checked-In' ? 'Check in' : (s == 'Checked-Out' ? 'Check out' : 'Cancel booking'))}"/></button>
        </form>
      </c:forEach>
      <c:if test="${empty nextStatuses}"><span class="muted">No further status changes are possible.</span></c:if>
    </div>
  </div>
  <div class="card">
    <h3>Bill</h3>
    <div class="summary-row"><span>Room (<c:out value="${bill.nights}"/> x &#8377;<fmt:formatNumber value="${bill.roomRate}" pattern="#,##0.00"/>)</span><span>&#8377;<fmt:formatNumber value="${bill.roomCharges}" pattern="#,##0.00"/></span></div>
    <div class="summary-row"><span>Services</span><span>&#8377;<fmt:formatNumber value="${bill.serviceCharges}" pattern="#,##0.00"/></span></div>
    <div class="summary-row"><strong>Total</strong><strong>&#8377;<fmt:formatNumber value="${bill.total}" pattern="#,##0.00"/></strong></div>
    <div class="summary-row"><span>Paid</span><span>&#8377;<fmt:formatNumber value="${bill.paid}" pattern="#,##0.00"/></span></div>
    <div class="summary-row"><span>Balance</span><strong>&#8377;<fmt:formatNumber value="${bill.balance}" pattern="#,##0.00"/></strong></div>
    <div class="flex mt"><span class="badge badge-${fn:toLowerCase(fn:substringBefore(bill.paymentStatus, ' '))}"><c:out value="${bill.paymentStatus}"/></span>
      <a class="btn btn-gold btn-sm" href="${ctx}/admin/payments?bookingId=${booking.bookingId}">Record payment</a>
      <a class="btn btn-outline btn-sm" href="${ctx}/admin/service-bookings?bookingId=${booking.bookingId}">Add services</a>
      <a class="btn btn-outline btn-sm" href="${ctx}/admin/invoice?bookingId=${booking.bookingId}">Invoice</a></div>
  </div>
</div>

<div class="grid grid-2">
  <div class="card card-flush">
    <div style="padding:1.2rem 1.4rem 0"><h3>Services</h3></div>
    <c:choose><c:when test="${empty bill.serviceLines}"><div class="empty">No services added.</div></c:when>
      <c:otherwise><div class="table-wrap"><table><thead><tr><th>Service</th><th>Qty</th><th>Date</th><th class="right">Amount</th></tr></thead><tbody>
        <c:forEach var="sb" items="${bill.serviceLines}"><tr><td><c:out value="${sb.service.serviceName}"/></td><td><c:out value="${sb.quantity}"/></td><td><c:out value="${sb.serviceDate}"/></td><td class="right">&#8377;<fmt:formatNumber value="${sb.lineTotal}" pattern="#,##0.00"/></td></tr></c:forEach>
      </tbody></table></div></c:otherwise></c:choose>
    <h3 style="padding:1.2rem 1.4rem 0">Payments</h3>
    <c:choose><c:when test="${empty bill.payments}"><div class="empty">No payments yet.</div></c:when>
      <c:otherwise><div class="table-wrap"><table><thead><tr><th>ID</th><th>Date</th><th>Method</th><th class="right">Amount</th></tr></thead><tbody>
        <c:forEach var="p" items="${bill.payments}"><tr><td>#<c:out value="${p.paymentId}"/></td><td><c:out value="${p.paymentDate}"/></td><td><c:out value="${p.paymentMethod}"/></td><td class="right">&#8377;<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td></tr></c:forEach>
      </tbody></table></div></c:otherwise></c:choose>
  </div>
  <div class="card">
    <h3>Status history</h3>
    <c:choose><c:when test="${empty history}"><p class="muted">No history recorded.</p></c:when>
      <c:otherwise><ul class="timeline">
        <c:forEach var="h" items="${history}"><li>
          <strong><c:out value="${empty h.oldStatus ? 'Created as' : h.oldStatus.concat(' to')}"/> <c:out value="${h.newStatus}"/></strong><br>
          <span class="muted small"><c:out value="${h.changedAt}"/><c:if test="${not empty h.changedBy}"> by <c:out value="${h.changedBy.username}"/></c:if></span></li></c:forEach>
      </ul></c:otherwise></c:choose>
  </div>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
