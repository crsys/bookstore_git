package book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.JDBCUtil;

public class BookDAO {
	private static BookDAO dao=new BookDAO();
	private BookDAO() {}
	public static BookDAO getInstance() {
		return dao;
	}
	public List<BookVO> getBookList(){
		List<BookVO> list=null;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select bno,title,writer,price,publisher,regdate from booktbl order by regdate desc";
		try {
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				list=new ArrayList<>();
				do {
					BookVO vo=new BookVO(rs.getInt("bno"),rs.getString("title"),rs.getString("writer"),rs.getInt("price")
							,rs.getString("publisher"),rs.getDate("regdate"));
					list.add(vo);
				}while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return list;
	}
	public int getRowCount() {
		int result=0;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select count(*) from booktbl";
		try {
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return result;
	}
	public List<BookVO> getBookList(int page, int displayRow) {
		List<BookVO> list=null;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from(\r\n"
				+ "    select rownum rn,A.* from\r\n"
				+ "        (select * from booktbl order by bno desc) A\r\n"
				+ "    where rownum<=?\r\n"
				+ ") where rn>=?";
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, displayRow*page);
			pstmt.setInt(2, displayRow*page-displayRow+1);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				list=new ArrayList<>();
				do {
					BookVO vo=new BookVO(rs.getInt("bno"),rs.getString("title"),rs.getString("writer"),rs.getInt("price")
							,rs.getString("publisher"),rs.getDate("regdate"));
					list.add(vo);
				}while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return list;
	}
	public List<BookVO> getBookList(int page, int displayRow, String searchtype, String searchword) {
		List<BookVO> list=null;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from(\r\n"
				+ "    select rownum rn,A.* from\r\n"
				+ "        (select * from booktbl where "+searchtype+" like ? order by title) A\r\n"
				+ "    where rownum<=?\r\n"
				+ ") where rn>=?";
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, "%"+searchword+"%");
			pstmt.setInt(2, displayRow*page);
			pstmt.setInt(3, displayRow*page-displayRow+1);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				list=new ArrayList<>();
				do {
					BookVO vo=new BookVO(rs.getInt("bno"),rs.getString("title"),rs.getString("writer"),rs.getInt("price")
							,rs.getString("publisher"),rs.getDate("regdate"));
					list.add(vo);
				}while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return list;
	}
	public int getRowCount(String searchtype, String searchword) {
		int result=0;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select count(*) from booktbl where "+searchtype+" like ?";
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, "%"+searchword+"%");
			rs=pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return result;
	}
	public int insertBook(BookVO vo) {
		int result=0;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		String sql="insert into booktbl(bno,title,writer,price,publisher,content,srcFilename,saveFilename,savePath) "
				+ "values(book_seq.nextval,?,?,?,?,?,?,?,?)";
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getWriter());
			pstmt.setInt(3, vo.getPrice());
			pstmt.setString(4, vo.getPublisher());
			pstmt.setString(5, vo.getContent());
			pstmt.setString(6, vo.getSrcFilename());
			pstmt.setString(7, vo.getSaveFilename());
			pstmt.setString(8, vo.getSavePath());
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt);
		}
		return result;
	}
	public BookVO getBook(int bno) {
		BookVO vo=null;
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from booktbl where bno="+bno;
		try {
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {
					vo=new BookVO(rs.getInt("bno"),rs.getString("title"), rs.getString("writer"), rs.getInt("price")
							, rs.getString("publisher"),rs.getDate("regdate"),rs.getString("content"), rs.getString("srcFilename")
							, rs.getString("saveFilename"), rs.getString("savePath"));				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return vo;
	}
	public int updateBook(BookVO vo) {
		int result=0;
		String oFileName=vo.getSrcFilename();
		String sql=null;
		if(oFileName==null) {
			sql="update booktbl set title=?,writer=?,price=?,publisher=?,content=? where bno=?";
		}else {
			sql="update booktbl set title=?,writer=?,price=?,publisher=?,content=?,srcFilename=?,saveFilename=?,savePath=? "
					+ "where bno=?";
		}
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getWriter());
			pstmt.setInt(3, vo.getPrice());
			pstmt.setString(4, vo.getPublisher());
			pstmt.setString(5, vo.getContent());
			if(oFileName==null) {
				pstmt.setInt(6, vo.getBno());
			}else {
				pstmt.setString(6, oFileName);
				pstmt.setString(7, vo.getSaveFilename());
				pstmt.setString(8, vo.getSavePath());
				pstmt.setInt(9, vo.getBno());
			}
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt);
		}
		return result;
	}
	public int deleteBook(int bno) {
		int result=0;
		String sql="delete from booktbl where bno=?";
		Connection conn=JDBCUtil.getConnection();
		PreparedStatement pstmt=null;
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			result=pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtil.close(conn, pstmt);
		}
		return result;
	}
}
