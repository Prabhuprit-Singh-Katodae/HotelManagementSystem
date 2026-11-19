<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Payments"/><jsp:param name="subtitle" value="Record cash, UPI and card payments"/><jsp:param name="active" value="payments"/></jsp:include>

<div class="card mb">
  <h3>Record a payment</h3>
  <c:choose>
    <c:when test="${empty dueBills}"><p class="muted">Every active booking is fully paid. Nothing to record.</p></c:when>
    <c:otherwise>
      <form action="${ctx}/admin/payments" method="post">
        <input type="hidden" name="action" value="record">
        <div class="form-grid">
          <div class="form-group"><label for="bookingId">Booking</label>
            <select id="bookingId" name="bookingId" required>
              <c:forEach var="b" items="${dueBills}">
                <option value="${b.booking.bookingId}" ${selectedBooking == b.booking.bookingId ? 'selected' : ''}>#<c:out value="${b.booking.bookingId}"/> - <c:out value="${b.booking.customer.name}"/> - due &#8377;<fmt:formatNumber value="${b.balance}" pattern="#,##0.00"/></option>
              </c:forEach>
            </select></div>
          <div class="form-group"><label for="amount">Amount (&#8377;)</label><input type="number" id="amount" name="amount" min="1" step="0.01" required></div>
          <div class="form-group"><label for="method">Method</label>
            <select id="method" name="method"><c:forEach var="m" items="${methods}"><option value="${m}"><c:out value="${m}"/></option></c:forEach></select></div>
          <div class="form-group"><label for="paymentDate">Date</label><input type="date" id="paymentDate" name="paymentDate" value="${today}"></div>
        </div>
        <p class="hint">The amount cannot be more than the balance due. This records a payment; it does not contact any payment gateway.</p>
        <button class="btn btn-gold" type="submit">Save payment</button>
      </form>
    </c:otherwise>
  </c:choose>
</div>

<div class="card card-flush">
  <div style="padding:1.2rem 1.4rem 0"><h3>Payment history</h3></div>
  <c:choose>
    <c:when test="${empty payments}"><div class="empty">No payments recorded yet.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Booking</th><th>Customer</th><th>Date</th><th>Method</th><th class="right">Amount</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="p" items="${payments}">
          <tr><td>#<c:out value="${p.paymentId}"/></td>
            <td><a href="${ctx}/admin/booking-details?id=${p.booking.bookingId}">#<c:out value="${p.booking.bookingId}"/></a></td>
            <td><c:out value="${p.booking.customer.name}"/></td><td><c:out value="${p.paymentDate}"/></td><td><c:out value="${p.paymentMethod}"/></td>
            <td class="right">&#8377;<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td>
            <td class="actions">
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/invoice?bookingId=${p.booking.bookingId}">Invoice</a>
              <c:if test="${sessionScope.loggedUser.admin}">
                <form action="${ctx}/admin/payments" method="post" class="inline-form" data-confirm="Delete this payment record?">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="paymentId" value="${p.paymentId}">
                  <button class="btn btn-danger btn-sm" type="submit">Delete</button></form>
              </c:if></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
