<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Feedback"/><jsp:param name="active" value="feedback"/></jsp:include>
<section class="section"><div class="container">
  <div class="page-head"><div><h1>Feedback</h1><p>Tell us about your stay. We read every message.</p></div></div>
  <div class="grid grid-2">
    <div class="card">
      <h3>Share your feedback</h3>
      <form action="${ctx}/customer/feedback" method="post">
        <input type="hidden" name="action" value="submit">
        <div class="form-group"><label for="rating">Rating</label>
          <select id="rating" name="rating" required>
            <option value="5">5 - Excellent</option><option value="4">4 - Good</option><option value="3">3 - Average</option>
            <option value="2">2 - Poor</option><option value="1">1 - Very poor</option>
          </select></div>
        <div class="form-group"><label for="comments">Comments</label><textarea id="comments" name="comments" maxlength="1000" placeholder="What went well? What could be better?"></textarea></div>
        <button class="btn btn-gold" type="submit">Submit feedback</button>
      </form>
    </div>
    <div class="card">
      <h3>Your previous feedback</h3>
      <c:choose>
        <c:when test="${empty feedbacks}"><div class="empty">You have not submitted any feedback yet.</div></c:when>
        <c:otherwise>
          <c:forEach var="f" items="${feedbacks}">
            <div class="summary-row" style="display:block">
              <div class="stars"><c:forEach begin="1" end="${f.rating}">&#9733;</c:forEach></div>
              <div><c:out value="${f.comments}"/></div>
              <div class="small muted"><c:out value="${f.feedbackDate}"/></div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div></section>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
