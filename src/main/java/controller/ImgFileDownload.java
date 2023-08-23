package controller;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImgFileDownload
 */
@WebServlet("/imgDown")
public class ImgFileDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImgFileDownload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String upload=request.getParameter("upload");
		if(!upload.equals("")) {
			request.setCharacterEncoding("utf-8");
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
