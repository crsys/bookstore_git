<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/star-rating/css/star-rating.min.css" media="all" type="text/css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/star-rating/themes/krajee-svg/theme.min.css" media="all" type="text/css"/>
<script src="${pageContext.request.contextPath}/star-rating/js/star-rating.min.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/star-rating/themes/krajee-svg/theme.min.js" type="text/javascript"></script>
<style>
	.btn{
		display:flex;
		margin:0 10px;
		justify-content:center;
	}
	th{
		width:100px;
	}
	td{
		text-align:left;
	}
</style>
<main>
	<h2>도서 세부사항</h2>
	<form action="bUpdate" method="post" id="uploadForm" name="uploadForm" enctype="multipart/form-data">
		<table class="table table-sm table-bordered">
			<tr>
				<th>도서제목</th>
				<td class="disp">${vo.title}</td>
				<td class="edit" style="display:none;"><input type="text" size="120" maxlength="50" name="title" id="title"
				required value="${vo.title}"></td>
			</tr>
	        <tr>
				<th>저자</th>
				<td class="disp">${vo.writer}</td>
				<td class="edit" style="display:none;"><input type="text" size="120" maxlength="30" name="writer" id="writer"
				value="${vo.writer}"></td>
			</tr>
	        <tr>
				<th>출판사</th>
				<td class="disp">${vo.publisher}</td>
				<td class="edit" style="display:none;"><input type="text" size="120" maxlength="30" name="publisher" id="publisher"
				value="${vo.publisher}"></td>
			</tr>
	        <tr>
				<th>가격</th>
				<td class="disp"><fmt:formatNumber value="${vo.price}" type="currency" currencySymbol="\\"></fmt:formatNumber></td>
				<td class="edit" style="display:none;"><input type="text" size="120" maxlength="7" name="price" id="price"
				onkeydown="inputNum(this)" required value="${vo.price}"></td>
			</tr>
	        <tr>
				<th>도서내용</th>
				<td class="disp"><% BookVO vo=(BookVO)request.getAttribute("vo");
					   if(vo!=null){
						   String content=vo.getContent();
						   if(content!=null)out.write(content.replaceAll("\r\n", "<br>"));
					   }
					%>
				</td>
				<td class="edit" style="display:none;"><textarea name="content" id="content" cols="119" rows="10" maxlength="1000"
				>${vo.content}</textarea></td>
			</tr>
			<tr>
				<th>도서 이미지</th>
				<td class="disp">
					<c:if test="${vo.saveFilename!=null}">
					<img alt="" src="imgDown?upload=${vo.savePath}&saveFname=${vo.saveFilename}&originFname=${vo.srcFilename}"
					height="300px">
					</c:if>
				</td>
				<td class="edit" style="display:none;">
					<div>
						<c:if test="${vo.saveFilename!=null}">
							기존 파일명 : ${vo.srcFilename}
						</c:if>
					</div>
					<div class="form-group row">
					<label for="file" class="col-sm-2 col-form-label">파일첨부</label>
					<div class="col-sm-10">
						<input type="file" name="file" id="file">
						<small class="text-muted">(파일크기 : 2MB / 이미지 파일만 가능)</small>
						<small id="file" class="text-info"></small>
					</div>
					</div>
				</td>
			</tr> 
			<tr>
				<td>평균 별점</td>
				<td> 
					<input id="avgscore" name="avgscore" value="${avgScore}" class="rating rating-loading" readonly="readonly"
					data-size="sm">
					&nbsp;&nbsp;&nbsp; 평가자 인원수 : <span id="cnt">${cnt}</span>
				</td>
			</tr> 
		</table>
		<c:if test="${sessionScope.mvo!=null}">
		<div>
			<label class="control-label">별점 저장하기</label>
			<table>
				<tr>
					<td width="300px">
						<input id="score" name="score" class="rating rating-loading" data-min="0" data-max="5" data-step="0.5"
						data-size="sm" required>
					</td>
					<td>
						평가글 남기기 : <input type="text" maxlength="100" id="cmt" name="cmt" size="50" required>
						<button type="button" onclick="saveStar()">저장하기</button>
					</td>
				</tr>
			</table>
		</div>
		</c:if>
		<div class="btn">
			<button type="button" id="btnList" class="btn btn-success" 
			onclick="location.href='bList?page=${param.page}&searchword=${param.searchword}&searchtype=${param.searchtype}'">도서목록
			</button>
			<c:if test="${mvo.grade=='a'}">
			<button type="button" id="btnEdit" class="btn btn-warning" onclick="bookEdit()">도서수정</button>
			<button type="button" id="btnDelete" class="btn btn-danger" onclick="bookDelete()">도서삭제</button>
			<button type="submit" id="btnSave" class="btn btn-primary" style="display:none">도서저장</button>
			<button type="reset" id="btnCancel" class="btn btn-danger" onclick="bookCancel()" style="display:none">저장취소</button>
			</c:if>
			<input type="hidden" name="bno" value="${param.bno}">
	    </div>
	    </form>
	    <br>
	    <input type="hidden" name="page" id="page" value="0">
	    <input type="hidden" name="bno" id="bno" value="${param.bno}">
	    <table id="tbl_star">
		
		</table>
		<button type="button" id="btn_next" style="display:none" onclick="getStar()">더보기</button>
</main>
<script type="text/javascript">
	$(document).ready(function(){
		$("#page").val(0);
		getStar();
	});
	function newStar(){
		let ratingStar=$(".rating-loading");
		if(ratingStar.length){
			ratingStar.removeClass("rating-loading").addClass("rating-loading").rating();
		}
		$(".rating-container").removeClass("rating-md rating-sm");
		$(".rating-container").addClass("rating-sm");
	}
	function getStar(){
		let url="scoreListAjax";
		let param={"bno":$("#bno").val()
				,"page":$("#page").val()*1+1};
		$("#page").val($("#page").val()*1+1);
		console.log(param);
		doAjax(url,param,getStarAfter);
	}
	function getStarAfter(data){
		console.log("getStarAfter");
		if(data=="err"){
			
		}else{
			console.log(data);
			let starList=data.arr;
			console.log(starList);
			let html="";
			for(let vo of starList){
				html+='<tr>';
				html+='<td>';
				html+='<input id="score" name="score" value='+vo.score+' class="rating rating-loading" '+ 
				'data-size="sm" readonly="readonly">';
				html+='</td>';
				html+='<td>'+vo.id+' 님 : '+vo.cmt+'</td>';
				html+='</tr>';
			}
			$("#tbl_star").append(html);
			let next=data.next;
			console.log(next);
			if(next)$("#btn_next").css("display","block");
			else $("#btn_next").css("display","none");
			let ratingStar=$(".rating-loading");
			if(ratingStar.length){
				ratingStar.removeClass("rating-loading").addClass("rating-loading").rating();
			}
		}
		
	}
	function saveStar(){
		let cmt=$("#cmt").val().trim();
		if(cmt==""){
			alert("평가글을 입력하세요");
			$("#cmt").focus();
		}
		let url="scoreSaveAjax";
		let param={"id":"${sessionScope.mvo.id}"
				,"bno":"${vo.bno}"
				,"score":$("#score").val()
				,"cmt":cmt};
		console.log(param);
		doAjaxHtml(url,param,saveStarAfter);
	}
	function saveStarAfter(data){
		console.log("평균,참여자수 : "+data);
		$("#tbl_star").html("");
		$("#page").val(0);
		getStar();
		$("#score").rating("destroy");
		$("#score").val(0);
		$("#score").rating("create");
		let retData=JSON.parse(data);
		$("#avgscore").rating("destroy");
		$("#avgscore").val(retData.avgScore);
		$("#avgscore").rating("create");
		$("#cnt").html(retData.cnt);
		newStar();
		$("#cmt").val("");
		$("#cmt").focus();
	}
	function bookEdit(){
		$(".disp").css("display","none");
		$(".edit").css("display","block");
		$("#btnEdit").css("display","none");
		$("#btnDelete").css("display","none");
		$("#btnSave").css("display","block");
		$("#btnCancel").css("display","block");
		//document.getElementById("title").value="${vo.title}";
		//document.querySelector("#writer").value="${vo.writer}";
		//document.querySelector("#publisher").value="${vo.publisher}";
		//document.querySelector("#price").value="${vo.price}";
	}
	function bookDelete(){
		if(confirm("도서삭제를 수행 하시겠습니까?")){
			location.href="bDelete?bno=${param.bno}";
		}
	}
	function bookCancel(){
		$(".disp").css("display","block");
		$(".edit").css("display","none");
		$("#btnEdit").css("display","block");
		$("#btnDelete").css("display","block");
		$("#btnSave").css("display","none");
		$("#btnCancel").css("display","none");
	}
</script>
<%@ include file="../include/footer.jsp" %>