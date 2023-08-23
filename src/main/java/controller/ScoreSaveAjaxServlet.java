package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import book.BookscoreDAO;
import book.BookscoreVO;

/**
 * Servlet implementation class ScoreSaveServlet
 */
@WebServlet("/scoreSaveAjax")
public class ScoreSaveAjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScoreSaveAjaxServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
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

}
