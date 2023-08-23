package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import book.BookDAO;
import book.BookVO;
import util.PageVO;

/**
 * Servlet implementation class BookListServlet
 */
@WebServlet("/bList")
public class BookListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		BookDAO dao=BookDAO.getInstance();
		//List<BookVO> list=dao.getBookList();
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
		RequestDispatcher rd=request.getRequestDispatcher("/book/bookList.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
