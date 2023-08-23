package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import book.BookscoreDAO;
import book.BookscoreVO;
import util.PageVO;

/**
 * Servlet implementation class ScoreListServlet
 */
@WebServlet("/scoreListAjax")
public class ScoreListAjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScoreListAjaxServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd=request.getRequestDispatcher("score/score_list_ajax.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
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

}
