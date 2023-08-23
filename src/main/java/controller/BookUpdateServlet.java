package controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import book.BookDAO;
import book.BookVO;

/**
 * Servlet implementation class BookUpdateServlet
 */
@WebServlet("/bUpdate")
public class BookUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String upload="d:/upload/img";
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
				RequestDispatcher rd=request.getRequestDispatcher("bView?bno="+bno);
				rd.forward(request, response);
			}else {
				
			}
		}
	}

}
