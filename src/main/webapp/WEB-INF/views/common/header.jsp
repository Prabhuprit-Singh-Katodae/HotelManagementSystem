<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Public / customer page header + navbar. Parameters: title, active --%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${param.title}"/> | Grandstay Hotel</title>
  <c:if test="${not empty sessionScope.csrfToken}"><meta name="csrf" content="${sessionScope.csrfToken}"></c:if>
  <link rel="icon" href="${ctx}/assets/images/logo.svg" type="image/svg+xml">
  <link rel="stylesheet" href="${ctx}/assets/css/common.css">
  <link rel="stylesheet" href="${ctx}/assets/css/customer.css">
  <link rel="stylesheet" href="${ctx}/assets/css/booking.css">
  <link rel="stylesheet" href="${ctx}/assets/css/auth.css">
</head>
<body>
<header class="site-header">
  <div class="container nav">
    <a class="brand" href="${ctx}/home"><img src="${ctx}/assets/images/logo.svg" alt="">Grandstay</a>
    <button class="nav-toggle" id="navToggle" aria-label="Open menu">&#9776;</button>
    <ul class="nav-links" id="navLinks">
      <li><a href="${ctx}/home" class="${param.active == 'home' ? 'active' : ''}">Home</a></li>
      <li><a href="${ctx}/rooms" class="${param.active == 'rooms' ? 'active' : ''}">Rooms</a></li>
      <li><a href="${ctx}/availability" class="${param.active == 'availability' ? 'active' : ''}">Check availability</a></li>
      <c:choose>
        <c:when test="${sessionScope.loggedUser.customer}">
          <li><a href="${ctx}/customer/bookings" class="${param.active == 'bookings' ? 'active' : ''}">My bookings</a></li>
          <li><a href="${ctx}/customer/payments" class="${param.active == 'payments' ? 'active' : ''}">Payments</a></li>
          <li><a href="${ctx}/customer/feedback" class="${param.active == 'feedback' ? 'active' : ''}">Feedback</a></li>
          <li><a href="${ctx}/customer/profile" class="${param.active == 'profile' ? 'active' : ''}">Profile</a></li>
          <li><a href="${ctx}/logout" class="nav-out">Log out</a></li>
        </c:when>
        <c:when test="${sessionScope.loggedUser.hotelStaff}">
          <li><a href="${ctx}/admin/dashboard">Staff dashboard</a></li>
          <li><a href="${ctx}/logout" class="nav-out">Log out</a></li>
        </c:when>
        <c:otherwise>
          <li><a href="${ctx}/login" class="${param.active == 'login' ? 'active' : ''}">Log in</a></li>
          <li><a href="${ctx}/register" class="nav-cta">Register</a></li>
        </c:otherwise>
      </c:choose>
    </ul>
  </div>
</header>
<main>
<jsp:include page="/WEB-INF/views/common/alerts.jsp"/>
