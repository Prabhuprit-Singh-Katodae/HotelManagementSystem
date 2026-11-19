<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="My payments"/><jsp:param name="active" value="payments"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>My payments</h1><p>Amounts due and your payment history.</p></div></div>

  <c:if test="${not empty dueBills}">
    <div class="card mb">
      <h3>Amount due</h3>
      <div class="alert alert-info">This is a <strong>demonstration payment</strong>. It saves a payment record in the hotel database but no real money is charged. You can also pay at the front desk.</div>
      <div class="table-wrap"><table>
        <thead><tr><th>Booking</th><th>Room</th><th>Total</th><th>Paid</th><th>Balance</th><th>Pay (demo)</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${dueBills}">
          <tr>
            <td><a href="${ctx}/customer/invoice?bookingId=${b.booking.bookingId}">#<c:out value="${b.booking.bookingId}"/></a></td>
            <td><c:out value="${b.booking.room.roomNumber}"/></td>
            <td>&#8377;<fmt:formatNumber value="${b.total}" pattern="#,##0.00"/></td>
            <td>&#8377;<fmt:formatNumber value="${b.paid}" pattern="#,##0.00"/></td>
            <td><strong>&#8377;<fmt:formatNumber value="${b.balance}" pattern="#,##0.00"/></strong></td>
            <td>
              <form action="${ctx}/customer/payments" method="post" class="flex" data-confirm="Record a demo payment of the full balance?">
                <input type="hidden" name="action" value="pay"><input type="hidden" name="bookingId" value="${b.booking.bookingId}">
                <select name="method" style="width:auto" aria-label="Payment method">
                  <c:forEach var="m" items="${methods}"><option value="${m}"><c:out value="${m}"/></option></c:forEach>
                </select>
                <button class="btn btn-gold btn-sm" type="submit">Pay balance</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table></div>
    </div>
  </c:if>

  <div class="card card-flush">
    <div style="padding:1.2rem 1.4rem 0"><h3>Payment history</h3></div>
    <c:choose>
      <c:when test="${empty payments}"><div class="empty">No payments recorded yet.</div></c:when>
      <c:otherwise>
        <div class="table-wrap"><table>
          <thead><tr><th>Payment</th><th>Booking</th><th>Date</th><th>Method</th><th class="right">Amount</th></tr></thead>
          <tbody>
          <c:forEach var="p" items="${payments}">
            <tr><td>#<c:out value="${p.paymentId}"/></td>
              <td><a href="${ctx}/customer/invoice?bookingId=${p.booking.bookingId}">#<c:out value="${p.booking.bookingId}"/></a></td>
              <td><c:out value="${p.paymentDate}"/></td><td><c:out value="${p.paymentMethod}"/></td>
              <td class="right">&#8377;<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td></tr>
          </c:forEach>
          </tbody>
        </table></div>
      </c:otherwise>
    </c:choose>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
