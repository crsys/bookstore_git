<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp" %>
<style>
	#pagediv,#searchdiv{
		display:flex;
		justify-content:center;
	}
	#top-button{
		display:flex;
		justify-content:right;
		padding:10px;
	}
	a:link,a:visited,a:hover{
		text-decoration: none;
		font-weight: bold;
		color: #333;
	}
</style>
<main>
<h2>도서 목록</h2>
<div id="top-button">
	<c:if test="${mvo.grade=='a'}">
	<a href="bNew"><button type="button" class="btn btn-primary">도서등록</button></a>
	</c:if>
</div>
	<table class="table table-sm table-bordered">
		<tr>
			<th>순서</th>
			<th>도서명</th>
			<th>저자</th>
			<th>출판사</th>
			<th>가격</th>
			<th>등록일자</th>
		</tr>
		<c:if test="${list==null}">
			<tr>
				<td colspan="6">검색된 도서가 없습니다.</td>
			</tr>
		</c:if>
		<c:forEach items="${list}" var="book" varStatus="sts">
			<tr>
				<td>${sts.count}</td>
				<td><a href="bView?bno=${book.bno}&page=${pvo.page}&searchword=${pvo.searchword}&searchtype=${pvo.searchtype}">
				${book.title}</a></td>
				<td>${book.writer}</td>
				<td>${book.publisher}</td>
				<td><fmt:formatNumber value="${book.price}" type="currency" currencySymbol="\\"></fmt:formatNumber></td>
				<td>${book.regdate}</td>
			</tr>
		</c:forEach>
	</table>
	<div id="pagediv">
        <nav aria-label="Standard pagination example">
          <ul class="pagination">
          <c:if test="${pvo.prev}">
            <li class="page-item">
              <a class="page-link" href="bList?page=${pvo.beginPage-1}&searchword=${pvo.searchword}&searchtype=${pvo.searchtype}" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
              </a>
            </li>
          </c:if>
            <c:forEach begin="${pvo.beginPage}" end="${pvo.endPage}" var="i">
			<c:choose>
				<c:when test="${i!=pvo.page}"><li class="page-item"><a class="page-link" href="bList?page=${i}&searchword=${pvo.searchword}&searchtype=${pvo.searchtype}">${i}</a></li></c:when>
				<c:otherwise><li class="page-item" style="padding:5px 15px 10px 15px">${i}</li></c:otherwise>
			</c:choose>
			</c:forEach>
			<c:if test="${pvo.next}">
            <li class="page-item">
              <a class="page-link" href="bList?page=${pvo.endPage+1}&searchword=${pvo.searchword}&searchtype=${pvo.searchtype}" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
              </a>
            </li>
            </c:if>
          </ul>
        </nav>
      </div>
      <div id="searchdiv">
		<form action="bList" method="post">
	        <select name="searchtype" id="searchtype">
	            <option value="title" checked>도서명</option>
	            <option value="writer">저자명</option>
	            <option value="publisher">출판사</option>
	        </select>
	        <input type="text" size="20" name="searchword" id="searchword">
	        <button onclick="return searchFun()">검 색</button>&nbsp;
	    </form>
	    <!-- <a href="bList"><button>전체도서검색</button></a> -->
	  </div> 
</main>
<script type="text/javascript">
	function searchFun(){
		let searchword=document.querySelector("#searchword");
		if(searchword.value.trim()==""){
			searchword.value="";
			return true;
		}
		return true;
	}
</script>
<%@ include file="../include/footer.jsp" %>