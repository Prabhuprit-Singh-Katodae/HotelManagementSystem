<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Admin/staff layout: sidebar + top bar. Parameters: title, subtitle, active --%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${param.title}"/> | Grandstay Admin</title>
  <c:if test="${not empty sessionScope.csrfToken}"><meta name="csrf" content="${sessionScope.csrfToken}"></c:if>
  <link rel="icon" href="${ctx}/assets/images/logo.svg" type="image/svg+xml">
  <link rel="stylesheet" href="${ctx}/assets/css/common.css">
  <link rel="stylesheet" href="${ctx}/assets/css/admin.css">
  <link rel="stylesheet" href="${ctx}/assets/css/booking.css">
</head>
<body>
<div class="admin-shell">
  <aside class="sidebar" id="sidebar">
    <a class="brand" href="${ctx}/admin/dashboard" style="color:#fff"><img src="${ctx}/assets/images/logo.svg" alt="" style="width:32px;height:32px">Grandstay</a>
    <a href="${ctx}/admin/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">Dashboard</a>

    <div class="group">Front desk</div>
    <a href="${ctx}/admin/bookings" class="${param.active == 'bookings' ? 'active' : ''}">Bookings</a>
    <a href="${ctx}/admin/customers" class="${param.active == 'customers' ? 'active' : ''}">Customers</a>
    <a href="${ctx}/admin/payments" class="${param.active == 'payments' ? 'active' : ''}">Payments</a>
    <a href="${ctx}/admin/service-bookings" class="${param.active == 'service-bookings' ? 'active' : ''}">Service bookings</a>
    <a href="${ctx}/admin/feedback" class="${param.active == 'feedback' ? 'active' : ''}">Feedback</a>

    <div class="group">Hotel setup</div>
    <a href="${ctx}/admin/rooms" class="${param.active == 'rooms' ? 'active' : ''}">Rooms</a>
    <a href="${ctx}/admin/room-types" class="${param.active == 'room-types' ? 'active' : ''}">Room types</a>
    <a href="${ctx}/admin/services" class="${param.active == 'services' ? 'active' : ''}">Services</a>

    <c:if test="${sessionScope.loggedUser.admin}">
      <div class="group">Administration</div>
      <a href="${ctx}/admin/employees" class="${param.active == 'employees' ? 'active' : ''}">Employees</a>
      <a href="${ctx}/admin/users" class="${param.active == 'users' ? 'active' : ''}">User accounts</a>
    </c:if>

    <div class="group">Website</div>
    <a href="${ctx}/home">View guest website</a>
  </aside>

  <div class="admin-main">
    <div class="topbar">
      <div class="flex">
        <button class="menu-btn" id="menuBtn" aria-label="Open menu">&#9776;</button>
        <strong><c:out value="${param.title}"/></strong>
      </div>
      <div class="who">
        <span><c:out value="${sessionScope.loggedUser.displayName}"/> <span class="badge"><c:out value="${sessionScope.loggedUser.role}"/></span></span>
        <a href="${ctx}/logout" class="btn btn-outline btn-sm">Log out</a>
      </div>
    </div>
    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>
    <div class="page-head">
      <div>
        <h1><c:out value="${param.title}"/></h1>
        <c:if test="${not empty param.subtitle}"><p><c:out value="${param.subtitle}"/></p></c:if>
      </div>
    </div>
