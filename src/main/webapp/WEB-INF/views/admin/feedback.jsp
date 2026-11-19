<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/admin-header.jsp"><jsp:param name="title" value="Feedback"/><jsp:param name="subtitle" value="What guests say about their stay"/><jsp:param name="active" value="feedback"/></jsp:include>

<div class="stat mb" style="max-width:260px"><div class="label">Average rating</div><div class="value"><c:out value="${avgRating}"/> <span class="stars" style="font-size:1.1rem">&#9733;</span></div></div>

<div class="card card-flush">
  <c:choose>
    <c:when test="${empty feedbacks}"><div class="empty">No feedback yet.</div></c:when>
    <c:otherwise>
      <div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Customer</th><th>Rating</th><th>Comments</th><th>Date</th><th>Actions</th></tr></thead>
        <tbody>
        <c:forEach var="f" items="${feedbacks}">
          <tr><td><c:out value="${f.feedbackId}"/></td><td><c:out value="${f.customer.name}"/></td>
            <td class="nowrap"><span class="stars"><c:forEach begin="1" end="${f.rating}">&#9733;</c:forEach></span> <c:out value="${f.rating}"/>/5</td>
            <td><c:out value="${f.comments}"/></td><td><c:out value="${f.feedbackDate}"/></td>
            <td class="actions"><c:if test="${sessionScope.loggedUser.admin}">
              <form action="${ctx}/admin/feedback" method="post" class="inline-form" data-confirm="Delete this feedback?">
                <input type="hidden" name="action" value="delete"><input type="hidden" name="feedbackId" value="${f.feedbackId}">
                <button class="btn btn-danger btn-sm" type="submit">Delete</button></form></c:if></td></tr>
        </c:forEach>
        </tbody></table></div>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/views/common/admin-footer.jsp"/>
