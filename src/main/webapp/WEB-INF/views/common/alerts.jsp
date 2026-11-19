<%@ page contentType="text/html;charset=UTF-8" %>
<%-- Shows flash messages (set by servlets) and the request "error" message --%>
<c:set var="okMsg" value="${sessionScope.flashSuccess}"/>
<c:set var="errMsg" value="${not empty sessionScope.flashError ? sessionScope.flashError : requestScope.error}"/>
<c:if test="${not empty okMsg or not empty errMsg}">
  <div class="alert-area">
    <c:if test="${not empty okMsg}"><div class="alert alert-success" role="status"><c:out value="${okMsg}"/></div></c:if>
    <c:if test="${not empty errMsg}"><div class="alert alert-error" role="alert"><c:out value="${errMsg}"/></div></c:if>
  </div>
</c:if>
<c:remove var="flashSuccess" scope="session"/>
<c:remove var="flashError" scope="session"/>
