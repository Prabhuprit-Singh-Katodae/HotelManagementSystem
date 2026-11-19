<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Home"/><jsp:param name="active" value="home"/></jsp:include>

<section class="hero">
  <div class="container hero-grid">
    <div class="hero-text">
      <h1>Rooms at Grandstay Hotel</h1>
      <p>Pick your dates, see which rooms are free, and book online. The front desk confirms every booking.</p>
      <a class="btn" href="${ctx}/rooms">See rooms and rates</a>
    </div>
    <div class="hero-art" role="img" aria-label="Illustration of the hotel at dusk"></div>
  </div>
</section>

<div class="container search-card">
  <div class="card">
    <form action="${ctx}/availability" method="get">
      <div class="form-group"><label for="hCheckIn">Check-in</label><input type="date" id="hCheckIn" name="checkIn" min="${today}" value="${today}" required></div>
      <div class="form-group"><label for="hCheckOut">Check-out</label><input type="date" id="hCheckOut" name="checkOut" min="${tomorrow}" value="${tomorrow}" required></div>
      <div class="form-group"><label for="hType">Room type</label>
        <select id="hType" name="typeId"><option value="">Any type</option>
          <c:forEach var="t" items="${roomTypes}"><option value="${t.typeId}"><c:out value="${t.typeName}"/></option></c:forEach>
        </select></div>
      <button class="btn" type="submit">Check availability</button>
    </form>
  </div>
</div>

<section class="section">
  <div class="container">
    <div class="section-title"><h2>Rooms and rates</h2><p>Prices are per night.</p></div>
    <c:choose>
      <c:when test="${empty roomTypes}"><div class="card empty">No room types have been added yet.</div></c:when>
      <c:otherwise>
        <div class="grid grid-4">
          <c:forEach var="t" items="${roomTypes}">
            <article class="room-card">
              <div class="photo"><img src="${ctx}/assets/images/${t.imageFile}" alt="<c:out value='${t.typeName}'/> room" loading="lazy"></div>
              <div class="body">
                <h3><c:out value="${t.typeName}"/></h3>
                <div class="price">&#8377;<fmt:formatNumber value="${t.price}" pattern="#,##0"/> <span class="muted small">per night</span></div>
                <div class="actions"><a class="btn btn-outline btn-sm" href="${ctx}/rooms?type=${t.typeId}">View rooms</a></div>
              </div>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</section>

<section class="section" style="padding-top:0">
  <div class="container">
    <div class="section-title"><h2>How booking works</h2></div>
    <ol class="steps">
      <li><strong>Choose your dates</strong><span>Only rooms with no booking on those nights are listed.</span></li>
      <li><strong>Pick a room</strong><span>You see the total for your stay before you confirm.</span></li>
      <li><strong>The hotel confirms</strong><span>Your booking starts as pending. You can cancel it from My bookings.</span></li>
    </ol>
  </div>
</section>

<c:if test="${not empty reviews}">
  <section class="section" style="padding-top:0">
    <div class="container">
      <div class="section-title"><h2>From our guests</h2></div>
      <div class="grid grid-3">
        <c:forEach var="f" items="${reviews}">
          <div class="review">
            <p>&ldquo;<c:out value="${f.comments}"/>&rdquo;</p>
            <div class="small muted"><c:out value="${f.customer.name}"/> &middot; <span class="stars"><c:forEach begin="1" end="${f.rating}">&#9733;</c:forEach></span></div>
          </div>
        </c:forEach>
      </div>
    </div>
  </section>
</c:if>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
