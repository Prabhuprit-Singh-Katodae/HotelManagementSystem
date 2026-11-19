<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Room details"/><jsp:param name="active" value="rooms"/></jsp:include>
<section class="section"><div class="container">
  <p class="small"><a href="${ctx}/rooms">&larr; All rooms</a></p>
  <div class="room-detail">
    <div>
      <div class="photo"><img src="${ctx}/assets/images/${room.roomType.imageFile}" alt="Room <c:out value='${room.roomNumber}'/>"></div>
      <h1 class="mt">Room <c:out value="${room.roomNumber}"/> - <c:out value="${room.roomType.typeName}"/></h1>
      <p class="muted">A comfortable <c:out value="${fn:toLowerCase(room.roomType.typeName)}"/> room with a private bathroom, air conditioning and Wi-Fi.</p>
      <ul class="amenities"><li>Air conditioning</li><li>Wi-Fi</li><li>Private bathroom</li><li>Daily housekeeping</li><li>24-hour front desk</li></ul>
    </div>
    <aside class="card" id="book">
      <div class="price" style="font-size:1.6rem">&#8377;<fmt:formatNumber value="${room.roomType.price}" pattern="#,##0"/> <span class="muted small">/ night</span></div>
      <c:choose>
        <c:when test="${fn:toLowerCase(room.status) == 'maintenance'}">
          <div class="alert alert-info mt">This room is currently under maintenance and cannot be booked.</div>
        </c:when>
        <c:otherwise>
          <form action="${ctx}/customer/book" method="get" class="mt">
            <input type="hidden" name="roomId" value="${room.roomId}">
            <div class="form-group"><label for="rdIn">Check-in</label><input type="date" id="rdIn" name="checkIn" min="${today}" value="${today}" required></div>
            <div class="form-group"><label for="rdOut">Check-out</label><input type="date" id="rdOut" name="checkOut" min="${tomorrow}" value="${tomorrow}" required></div>
            <button class="btn btn-gold btn-block" type="submit">Continue to booking</button>
            <p class="hint mt">We check that the room is free for these dates before you confirm.</p>
          </form>
        </c:otherwise>
      </c:choose>
    </aside>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
