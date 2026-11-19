<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Bookings"/><jsp:param name="subtitle" value="Create bookings and manage check-in and check-out"/><jsp:param name="active" value="bookings"/></jsp:include>

<c:if test="${not showNew}">
  <p><a class="btn btn-gold" href="${ctx}/admin/bookings?newBooking=1">+ New booking</a></p>
</c:if>

<c:if test="${showNew}">
<div class="card mb">
  <div class="flex-between"><h3>New booking</h3><a class="btn btn-outline btn-sm" href="${ctx}/admin/bookings">Close</a></div>
  <p class="muted small">Step 1: choose the customer and dates. Step 2: pick one of the free rooms. Staff bookings are confirmed immediately.</p>
  <form action="${ctx}/admin/bookings" method="get">
    <input type="hidden" name="newBooking" value="1">
    <div class="form-grid" style="align-items:end">
      <div class="form-group"><label for="nbCustomer">Customer</label>
        <select id="nbCustomer" name="customerId" required><option value="">Select customer</option>
          <c:forEach var="c" items="${customers}"><option value="${c.customerId}" ${selectedCustomer == c.customerId ? 'selected' : ''}>#<c:out value="${c.customerId}"/> - <c:out value="${c.name}"/> (<c:out value="${c.phone}"/>)</option></c:forEach>
        </select>
        <span class="hint">New guest? <a href="${ctx}/admin/customers">Add the customer first</a>.</span></div>
      <div class="form-group"><label for="nbIn">Check-in</label><input type="date" id="nbIn" name="checkIn" min="${today}" value="${empty checkIn ? today : checkIn}" required></div>
      <div class="form-group"><label for="nbOut">Check-out</label><input type="date" id="nbOut" name="checkOut" min="${tomorrow}" value="${empty checkOut ? tomorrow : checkOut}" required></div>
      <div class="form-group"><label for="nbType">Room type</label>
        <select id="nbType" name="typeId"><option value="">Any type</option>
          <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}" ${typeId == t.typeId ? 'selected' : ''}><c:out value="${t.typeName}"/></option></c:forEach></select></div>
      <div class="form-group"><button class="btn" type="submit">Find available rooms</button></div>
    </div>
  </form>

  <c:if test="${not empty checkIn and not empty checkOut and empty error}">
    <hr style="border:0;border-top:1px solid var(--line);margin:1rem 0">
    <c:choose>
      <c:when test="${empty availableRooms}"><div class="alert alert-info">No rooms are free for these dates.</div></c:when>
      <c:otherwise>
        <form action="${ctx}/admin/bookings" method="post">
          <input type="hidden" name="action" value="create">
          <input type="hidden" name="customerId" value="${selectedCustomer}">
          <input type="hidden" name="checkIn" value="${checkIn}"><input type="hidden" name="checkOut" value="${checkOut}">
          <div class="rooms-picker">
            <c:forEach var="r" items="${availableRooms}" varStatus="st">
              <label><input type="radio" name="roomId" value="${r.roomId}" ${st.first ? 'checked' : ''} required>
                <span><strong>Room <c:out value="${r.roomNumber}"/></strong> - <c:out value="${r.roomType.typeName}"/> - &#8377;<fmt:formatNumber value="${r.roomType.price}" pattern="#,##0"/> / night</span></label>
            </c:forEach>
          </div>
          <button class="btn btn-gold" type="submit" ${empty selectedCustomer ? 'disabled' : ''}>Create booking</button>
          <c:if test="${empty selectedCustomer}"><span class="hint">Select a customer above and search again.</span></c:if>
        </form>
      </c:otherwise>
    </c:choose>
  </c:if>
</div>
</c:if>

<div class="card card-flush">
  <form action="${ctx}/admin/bookings" method="get" class="search-row" style="padding:1.2rem 1.4rem 0">
    <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Booking no., customer name or room number">
    <select name="status" aria-label="Status"><option value="">All statuses</option>
      <c:forEach var="s" items="${statuses}"><option value="${s}" ${status == s ? 'selected' : ''}><c:out value="${s}"/></option></c:forEach></select>
    <button class="btn" type="submit">Filter</button>
  </form>
  <c:choose>
    <c:when test="${empty bookings}"><div class="empty">No bookings found.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Customer</th><th>Room</th><th>Check-in</th><th>Check-out</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${bookings}">
          <tr><td><a href="${ctx}/admin/booking-details?id=${b.bookingId}">#<c:out value="${b.bookingId}"/></a></td>
            <td><c:out value="${b.customer.name}"/></td>
            <td><c:out value="${b.room.roomNumber}"/> <span class="muted small"><c:out value="${b.room.roomType.typeName}"/></span></td>
            <td><c:out value="${b.checkIn}"/></td><td><c:out value="${b.checkOut}"/></td>
            <td><span class="badge badge-${fn:toLowerCase(b.status)}"><c:out value="${b.status}"/></span></td>
            <td class="actions">
              <a class="btn btn-outline btn-sm" href="${ctx}/admin/booking-details?id=${b.bookingId}">Details</a>
              <c:choose>
                <c:when test="${b.status == 'Pending'}">
                  <form action="${ctx}/admin/bookings" method="post" class="inline-form"><input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${b.bookingId}"><input type="hidden" name="newStatus" value="Confirmed"><button class="btn btn-ok btn-sm" type="submit">Confirm</button></form>
                  <form action="${ctx}/admin/bookings" method="post" class="inline-form" data-confirm="Cancel booking #${b.bookingId}?"><input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${b.bookingId}"><input type="hidden" name="newStatus" value="Cancelled"><button class="btn btn-danger btn-sm" type="submit">Cancel</button></form>
                </c:when>
                <c:when test="${b.status == 'Confirmed'}">
                  <form action="${ctx}/admin/bookings" method="post" class="inline-form"><input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${b.bookingId}"><input type="hidden" name="newStatus" value="Checked-In"><button class="btn btn-ok btn-sm" type="submit">Check in</button></form>
                  <form action="${ctx}/admin/bookings" method="post" class="inline-form" data-confirm="Cancel booking #${b.bookingId}?"><input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${b.bookingId}"><input type="hidden" name="newStatus" value="Cancelled"><button class="btn btn-danger btn-sm" type="submit">Cancel</button></form>
                </c:when>
                <c:when test="${b.status == 'Checked-In'}">
                  <form action="${ctx}/admin/bookings" method="post" class="inline-form"><input type="hidden" name="action" value="status"><input type="hidden" name="bookingId" value="${b.bookingId}"><input type="hidden" name="newStatus" value="Checked-Out"><button class="btn btn-sm" type="submit">Check out</button></form>
                </c:when>
              </c:choose>
            </td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
