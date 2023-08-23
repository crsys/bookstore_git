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
 * Servlet implementation class BookInsertServlet
 */
@WebServlet("/bNew")
public class BookInsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookInsertServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd=request.getRequestDispatcher("/book/bookNew.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String upload="d:/upload/img";
		File path=new File(upload);
		if(!path.exists()) {
			path.mkdirs();
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
				RequestDispatcher rd=request.getRequestDispatcher("bList");
				rd.forward(request, response);
			}else {
				
			}
		}
	}

}
