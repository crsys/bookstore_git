package com.ezen.book;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ezen.member.MemberDAO;
import com.ezen.member.MemberVO;

public class MemberService {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private final String path="/WEB-INF/views/member/";
	
	public MemberService(HttpServletRequest req, HttpServletResponse resp) {
		this.request=req;
		this.response=resp;
	}

	public String exec() {
		String cmd=request.getParameter("cmd");
		String view=null;
		if(cmd.equals("login")) {
			return loginService();
		}else if(cmd.equals("logout")) {
			HttpSession s=request.getSession();
			s.invalidate();
			return "book?cmd=list";
		}
		return view;
	}

	private String loginService() {
		String method=request.getMethod().toUpperCase();
		if(method.equals("GET")) {
			return path+"login.jsp";
		}else {
			String id=request.getParameter("userid");
			String pwd=request.getParameter("pwd");
			MemberDAO dao=MemberDAO.getInstance();
			MemberVO mvo=dao.login(id, pwd);
			if(mvo!=null) {
				HttpSession session=request.getSession();
				session.setAttribute("mvo", mvo);
				return "book?cmd=list";
			}else {
				request.setAttribute("message", "로그인 실패");
				return path+"login.jsp";
			}
		}
	}

}
