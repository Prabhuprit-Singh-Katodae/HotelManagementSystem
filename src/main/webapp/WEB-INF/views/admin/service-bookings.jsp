<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Service bookings"/><jsp:param name="subtitle" value="Assign services to guests' bookings"/><jsp:param name="active" value="service-bookings"/></jsp:include>

<div class="card mb">
  <h3>Add a service to a booking</h3>
  <form action="${ctx}/admin/service-bookings" method="post">
    <input type="hidden" name="action" value="add">
    <div class="form-grid">
      <div class="form-group"><label for="bookingId">Booking</label>
        <select id="bookingId" name="bookingId" required><option value="">Select booking</option>
          <c:forEach var="b" items="${bookings}"><option value="${b.bookingId}" ${bookingId == b.bookingId ? 'selected' : ''}>#<c:out value="${b.bookingId}"/> - <c:out value="${b.customer.name}"/> - Room <c:out value="${b.room.roomNumber}"/> (<c:out value="${b.status}"/>)</option></c:forEach>
        </select></div>
      <div class="form-group"><label for="serviceId">Service</label>
        <select id="serviceId" name="serviceId" required><option value="">Select service</option>
          <c:forEach var="s" items="${services}"><option value="${s.serviceId}"><c:out value="${s.serviceName}"/> (&#8377;<fmt:formatNumber value="${s.price}" pattern="#,##0.00"/>)</option></c:forEach>
        </select></div>
      <div class="form-group"><label for="quantity">Quantity</label><input type="number" id="quantity" name="quantity" value="1" min="1" max="100" required></div>
      <div class="form-group"><label for="serviceDate">Date</label><input type="date" id="serviceDate" name="serviceDate" value="${today}"></div>
    </div>
    <button class="btn btn-gold" type="submit">Add service</button>
  </form>
</div>

<div class="card card-flush">
  <form action="${ctx}/admin/service-bookings" method="get" class="search-row" style="padding:1.2rem 1.4rem 0">
    <select name="bookingId" aria-label="Filter by booking"><option value="">All bookings</option>
      <c:forEach var="b" items="${bookings}"><option value="${b.bookingId}" ${bookingId == b.bookingId ? 'selected' : ''}>#<c:out value="${b.bookingId}"/> - <c:out value="${b.customer.name}"/></option></c:forEach></select>
    <button class="btn" type="submit">Filter</button>
  </form>
  <c:choose>
    <c:when test="${empty serviceBookings}"><div class="empty">No service bookings found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Booking</th><th>Service</th><th>Quantity</th><th>Date</th><th class="right">Amount</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="sb" items="${serviceBookings}">
          <tr><td><c:out value="${sb.serviceBookingId}"/></td>
            <td><a href="${ctx}/admin/booking-details?id=${sb.booking.bookingId}">#<c:out value="${sb.booking.bookingId}"/></a> <span class="muted small"><c:out value="${sb.booking.customer.name}"/></span></td>
            <td><c:out value="${sb.service.serviceName}"/></td>
            <td><form action="${ctx}/admin/service-bookings" method="post" class="flex" style="flex-wrap:nowrap">
                <input type="hidden" name="action" value="update"><input type="hidden" name="serviceBookingId" value="${sb.serviceBookingId}">
                <input type="hidden" name="back" value="/admin/service-bookings${empty bookingId ? '' : '?bookingId='.concat(bookingId)}">
                <input type="number" name="quantity" value="${sb.quantity}" min="1" max="100" style="width:80px" aria-label="Quantity">
                <button class="btn btn-outline btn-sm" type="submit">Update</button></form></td>
            <td><c:out value="${sb.serviceDate}"/></td>
            <td class="right">&#8377;<fmt:formatNumber value="${sb.lineTotal}" pattern="#,##0.00"/></td>
            <td class="actions"><form action="${ctx}/admin/service-bookings" method="post" class="inline-form" data-confirm="Remove this service from the booking?">
                <input type="hidden" name="action" value="remove"><input type="hidden" name="serviceBookingId" value="${sb.serviceBookingId}">
                <input type="hidden" name="back" value="/admin/service-bookings${empty bookingId ? '' : '?bookingId='.concat(bookingId)}">
                <button class="btn btn-danger btn-sm" type="submit">Remove</button></form></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
