<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Dashboard"/><jsp:param name="subtitle" value="Live numbers from the hotel database"/><jsp:param name="active" value="dashboard"/></jsp:include>

<div class="stats mb">
  <div class="stat"><div class="label">Customers</div><div class="value"><c:out value="${stats.totalCustomers}"/></div></div>
  <div class="stat"><div class="label">Rooms</div><div class="value"><c:out value="${stats.totalRooms}"/></div></div>
  <div class="stat"><div class="label">Available rooms</div><div class="value"><c:out value="${stats.availableRooms}"/></div></div>
  <div class="stat"><div class="label">Occupied rooms</div><div class="value"><c:out value="${stats.occupiedRooms}"/></div></div>
  <div class="stat"><div class="label">Bookings</div><div class="value"><c:out value="${stats.totalBookings}"/></div></div>
  <div class="stat"><div class="label">Pending bookings</div><div class="value"><c:out value="${stats.pendingBookings}"/></div></div>
  <div class="stat stat-wide"><div class="label">Revenue (all recorded payments)</div><div class="value">&#8377;<fmt:formatNumber value="${stats.revenue}" pattern="#,##0.00"/></div></div>
</div>

<div class="grid grid-2 mb">
  <div class="card">
    <h3>Bookings by status</h3>
    <c:forEach var="e" items="${stats.bookingsByStatus}">
      <div class="bar-row"><span><c:out value="${e.key}"/></span>
        <div class="bar-track"><div class="bar-fill" style="width:${stats.totalBookings > 0 ? e.value * 100 / stats.totalBookings : 0}%"></div></div>
        <strong><c:out value="${e.value}"/></strong></div>
    </c:forEach>
  </div>
  <div class="card">
    <h3>Rooms by status</h3>
    <div class="bar-row"><span>Available</span><div class="bar-track"><div class="bar-fill ok" style="width:${stats.totalRooms > 0 ? stats.availableRooms * 100 / stats.totalRooms : 0}%"></div></div><strong><c:out value="${stats.availableRooms}"/></strong></div>
    <div class="bar-row"><span>Occupied</span><div class="bar-track"><div class="bar-fill info" style="width:${stats.totalRooms > 0 ? stats.occupiedRooms * 100 / stats.totalRooms : 0}%"></div></div><strong><c:out value="${stats.occupiedRooms}"/></strong></div>
    <div class="bar-row"><span>Maintenance</span><div class="bar-track"><div class="bar-fill bad" style="width:${stats.totalRooms > 0 ? stats.maintenanceRooms * 100 / stats.totalRooms : 0}%"></div></div><strong><c:out value="${stats.maintenanceRooms}"/></strong></div>
  </div>
</div>

<div class="card card-flush">
  <div class="flex-between" style="padding:1.2rem 1.4rem 0"><h3>Recent bookings</h3><a class="btn btn-outline btn-sm" href="${ctx}/admin/bookings">All bookings</a></div>
  <c:choose>
    <c:when test="${empty stats.recentBookings}"><div class="empty">No bookings yet.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Customer</th><th>Room</th><th>Check-in</th><th>Check-out</th><th>Status</th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${stats.recentBookings}">
          <tr><td><a href="${ctx}/admin/booking-details?id=${b.bookingId}">#<c:out value="${b.bookingId}"/></a></td>
            <td><c:out value="${b.customer.name}"/></td><td><c:out value="${b.room.roomNumber}"/></td>
            <td><c:out value="${b.checkIn}"/></td><td><c:out value="${b.checkOut}"/></td>
            <td><span class="badge badge-${fn:toLowerCase(b.status)}"><c:out value="${b.status}"/></span></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
