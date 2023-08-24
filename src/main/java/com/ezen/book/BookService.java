package com.ezen.book;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ezen.util.PageVO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class BookService {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private final String path="/WEB-INF/views/book/";
	private final String upload="d:/upload/img";

	public BookService(HttpServletRequest request, HttpServletResponse response) {
		this.request=request;
		this.response=response;
	}

	public String exec() throws IOException {
		String cmd=request.getParameter("cmd");
		if(cmd==null || cmd.equals("list")) {
			return BookListService();
		}else if(cmd.equals("view")) {
			return BookViewService();
		}else if(cmd.equals("new")) {
			return BookNewService();
		}else if(cmd.equals("edit")) {
			return BookUpdateService();
		}else if(cmd.equals("imgDown")) {
			return BookImgDownService();
		}else if(cmd.equals("del")) {
			return BookDeleteService();
		}else if(cmd.equals("ssave")) {
			BookScoreSaveService();
		}else if(cmd.equals("slist")) {
			BookScoreListService();
		}
		
		return null;
	}

	private void BookScoreListService() throws IOException {
		String strbno=request.getParameter("bno").trim();
		int bno=39;
		if(strbno!=null && strbno!="")bno=Integer.parseInt(strbno);
		String strpage=request.getParameter("page").trim();
		int page=1;
		if(strpage!=null && strpage!="")page=Integer.parseInt(strpage);
		BookscoreDAO dao=BookscoreDAO.getInstance();
		int rowCnt=dao.getRowCount(bno);
		int displayRow=5;
		PageVO pvo=new PageVO(page, rowCnt, displayRow, 0);
		boolean next=pvo.nextPageScore();
		List<BookscoreVO> list=dao.getBookScore(page,bno,displayRow);
		if(list!=null) {
			JsonObject jObj=new JsonObject();
			jObj.addProperty("next", next);
			JsonArray arr=new JsonArray();
			JsonObject data=null;
			for(BookscoreVO vo:list) {
				data=new JsonObject();
				data.addProperty("score", vo.getScore());
				data.addProperty("id", vo.getId());
				data.addProperty("cmt", vo.getCmt());
				arr.add(data);
			}
			jObj.add("arr", arr);
	//		Gson gson=new Gson();
	//		System.out.println(gson.toJson(jObj));
			response.setContentType("application/json;charset=utf-8");
			PrintWriter out=response.getWriter();
			out.print(jObj);
			out.flush();
			out.close();
		}	
	}

	private void BookScoreSaveService() throws IOException {
		String id=request.getParameter("id");
		if(id==null || id=="")id="hong1";
		int bno=39;
		String strbno=request.getParameter("bno");
		if(strbno!=null && strbno!="") {
			bno=Integer.parseInt(request.getParameter("bno"));
		}
		double score=0;
		String strscore=request.getParameter("score").trim();
		if(strscore!=null && strscore!="") {
			score=Double.parseDouble(request.getParameter("score"));
		}
		String cmt=request.getParameter("cmt");
		BookscoreVO vo=new BookscoreVO(0, bno, id, score, cmt, null);
		BookscoreDAO dao=BookscoreDAO.getInstance();
		int result=dao.insertBookscore(vo);
		if(result==1) {
			JsonObject jObj=new JsonObject();
			BookscoreDAO sdao=BookscoreDAO.getInstance();
			ArrayList<Number> sc=sdao.getAvgScore(bno);
			jObj.addProperty("avgScore", sc.get(0));
			jObj.addProperty("cnt", sc.get(1));
	//		Gson gson=new Gson();
	//		System.out.println(gson.toJson(jObj));
			response.setContentType("application/json;charset=utf-8");
			PrintWriter out=response.getWriter();
			out.print(jObj);
			out.flush();
			out.close();
		}	
	}

	private String BookDeleteService() {
		int bno=Integer.parseInt(request.getParameter("bno"));
		BookDAO dao=BookDAO.getInstance();
		int result=dao.deleteBook(bno);
		if(result==1) {
			return "book?cmd=list";
		}
		return null;
	}

	private String BookImgDownService() throws IOException {
		String upload=request.getParameter("upload");
		if(!upload.equals("")) {
			String originFname=request.getParameter("originFname");
			String saveFname=request.getParameter("saveFname");
			String filename=upload+"/"+saveFname;
			String agent=request.getHeader("User-Agent");
			System.out.println(agent);
			boolean ieBrowser=(agent.indexOf("Trident")>-1)||(agent.indexOf("Edge")>-1);
			if(ieBrowser) {
				originFname=URLEncoder.encode(originFname,"utf-8").replace("\\", "%20");
			}else {
				originFname=new String(originFname.getBytes("utf-8"),"iso-8859-1");
			}
			response.setContentType("image/jpg");
			response.setHeader("Content-Disposition", "attachment;filename="+originFname);
			FileInputStream in=new FileInputStream(filename);
			BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
			int numRead;
			byte b[]=new byte[4096];
			while((numRead=in.read(b, 0, b.length))!=-1) {
				out.write(b, 0, numRead);
			}
			out.flush();
			in.close();
			out.close();
		}
		return null;
	}

	private String BookUpdateService() throws IOException {
		MultipartRequest mr=new MultipartRequest(request, upload, 2*1024*1024, "utf-8", new DefaultFileRenamePolicy());
		if(mr!=null) {
			String ofilename=mr.getOriginalFileName("file");
			String systemfilename=mr.getFilesystemName("file");
			String title=mr.getParameter("title").trim();
			String writer=mr.getParameter("writer").trim();
			String publisher=mr.getParameter("publisher").trim();
			int price=Integer.parseInt(mr.getParameter("price"));
			String content=mr.getParameter("content").trim();
			int bno=Integer.parseInt(mr.getParameter("bno"));
			BookVO vo=new BookVO(bno,title, writer, price, publisher,null,content,ofilename,systemfilename,upload);
			BookDAO dao=BookDAO.getInstance();
			int result=dao.updateBook(vo);
			if(result==1) {
				return "book?cmd=view&bno="+bno;
			}else {
				
			}
		}
		return null;
	}

	private String BookNewService() throws IOException {
		String method=request.getMethod().toUpperCase();
		if(method.equals("GET")) {
			return path+"bookNew.jsp";
		}else {
			File fpath=new File(upload);
			if(!fpath.exists()) {
				fpath.mkdirs();
			}
			MultipartRequest mr=new MultipartRequest(request, upload, 2*1024*1024, "utf-8", new DefaultFileRenamePolicy());
			if(mr!=null) {
				String ofilename=mr.getOriginalFileName("file");
				String systemfilename=mr.getFilesystemName("file");
				String title=mr.getParameter("title");
				String writer=mr.getParameter("writer");
				String publisher=mr.getParameter("publisher");
				int price=Integer.parseInt(mr.getParameter("price"));
				String content=mr.getParameter("content");
				BookVO vo=new BookVO(title, writer, price, publisher, content,ofilename,systemfilename,upload);
				BookDAO dao=BookDAO.getInstance();
				int result=dao.insertBook(vo);
				if(result==1) {
					return "book?cmd=list";
				}
			}
			
		}
		return null;
	}

	private String BookViewService() {
		int bno=Integer.parseInt(request.getParameter("bno"));
		BookDAO dao=BookDAO.getInstance();
		BookVO vo=dao.getBook(bno);
		if(vo!=null) {
			BookscoreDAO sdao=BookscoreDAO.getInstance();
			ArrayList<Number> score=sdao.getAvgScore(bno);
			request.setAttribute("avgScore", score.get(0));
			request.setAttribute("cnt", score.get(1));
			request.setAttribute("vo", vo);
			return path+"bookView.jsp";
		}
		return null;
	}

	private String BookListService() {
		BookDAO dao=BookDAO.getInstance();
		String paramPage=request.getParameter("page");
		int page=0;
		if(paramPage==null)page=1;
		else page=Integer.parseInt(paramPage);
		int displayRow=5;
		int displayPage=5;
		int rowCnt=0;
		List<BookVO> list=null;
		String searchtype=request.getParameter("searchtype");
		String searchword=request.getParameter("searchword");
		if(searchword==null) {
			list=dao.getBookList(page,displayRow);
			rowCnt=dao.getRowCount();
		}else if(searchword.equals("")){
			list=dao.getBookList(page,displayRow);
			rowCnt=dao.getRowCount();
		}else {
			list=dao.getBookList(page, displayRow,searchtype,searchword);
			rowCnt=dao.getRowCount(searchtype,searchword);
		}
		
		PageVO pvo=new PageVO(page,rowCnt,displayRow,displayPage);
		pvo.setSearchword(searchword);
		pvo.setSearchtype(searchtype);
		request.setAttribute("list", list);
		request.setAttribute("pvo", pvo);
		return path+"bookList.jsp";
	}

}
