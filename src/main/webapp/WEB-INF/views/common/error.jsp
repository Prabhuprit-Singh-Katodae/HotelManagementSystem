<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Something went wrong"/></jsp:include>
<section class="section"><div class="container center" style="max-width:560px">
  <h1>
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 403}">Access denied</c:when>
      <c:when test="${pageContext.errorData.statusCode == 404}">Page not found</c:when>
      <c:otherwise>Something went wrong</c:otherwise>
    </c:choose>
  </h1>
  <p class="muted">
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 403}">You do not have permission to open this page, or your session expired.</c:when>
      <c:when test="${pageContext.errorData.statusCode == 404}">The page you are looking for does not exist.</c:when>
      <c:otherwise>An unexpected error occurred. Please try again. If it keeps happening, check the Tomcat log.</c:otherwise>
    </c:choose>
  </p>
  <a class="btn" href="${ctx}/home">Back to home</a>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
