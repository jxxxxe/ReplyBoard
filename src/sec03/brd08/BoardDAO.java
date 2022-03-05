package sec03.brd08;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {

	private Connection con;
	private DataSource dataFactory;
	private PreparedStatement pstmt;
	
	public BoardDAO() {
		try {
			Context ctx=new InitialContext();
			Context envContext=(Context) ctx.lookup("java:/comp/env");
			dataFactory=(DataSource) envContext.lookup("jdbc/oracle");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public List<ArticleVO> selectAllArticles() {

		List<ArticleVO> articlesList=new ArrayList();
		try {
			con=dataFactory.getConnection();
			String query="SELECT LEVEL, articleNO, parentNO, title, content, writeDate, id " + 
					"FROM t_board " + 
					"START WITH parentNO=0 " + 
					"CONNECT BY PRIOR articleNO=parentNO " + 
					"ORDER SIBLINGS BY articleNO DESC";
			pstmt=con.prepareStatement(query);
			ResultSet rs=pstmt.executeQuery();
			
			while(rs.next()) {
				int level=rs.getInt("level");
				int articleNO=rs.getInt("articleNO");
				int parentNO=rs.getInt("parentNO");
				String title=rs.getString("title");
				String content=rs.getString("content");
				Date writeDate=rs.getDate("writeDate");
				String id=rs.getString("id");
				
				ArticleVO articleVO=new ArticleVO(level,parentNO,articleNO,title,content,null,id);
				articleVO.setWriteDate(writeDate);
				
				articlesList.add(articleVO);
			}
			con.close();
			pstmt.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return articlesList;
	}

	private int getNewArticleNO() {
		try {
			con=dataFactory.getConnection();
			String query="select max(articleNO) from t_board";
			pstmt=con.prepareStatement(query);
			
			ResultSet rs=pstmt.executeQuery();
			if(rs.next())
				return (rs.getInt(1)+1);
			
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int isertNewArticle(ArticleVO articleVO) {
		int articleNO=getNewArticleNO();
		try {
			con=dataFactory.getConnection();
			String query="insert into t_board(articleNO, parentNO, title, content, imageFileName, id)"
					+"values(?,?,?,?,?,?)";
			pstmt=con.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.setInt(2, articleVO.getParentNO());
			pstmt.setString(3, articleVO.getTitle());
			pstmt.setString(4, articleVO.getContent());
			pstmt.setString(5, articleVO.getImageFileName());
			pstmt.setString(6, articleVO.getId());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return articleNO;
	}


	public ArticleVO selectArticle(int articleNO) {
		ArticleVO article=new ArticleVO();
		try {
			con=dataFactory.getConnection();
			String query="select * from t_board where articleNO=?";
			pstmt=con.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			
			
			article.setArticleNO(articleNO);
			article.setParentNO(rs.getInt("parentNO"));
			article.setTitle(rs.getString("title"));
			article.setContent(rs.getString("content"));
			article.setImageFileName(rs.getString("imageFileName"));
			article.setWriteDate(rs.getDate("writeDate"));
			article.setId(rs.getString("id"));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return article;
	}


	public void updateArticle(ArticleVO article) {
		try {
			con=dataFactory.getConnection();
			String query="update t_board set title=?,content=?";
			if(article.getImageFileName()!=null && article.getImageFileName().length()!=0) {
				query+=", imageFileName=? ";
			}
			query+="where articleNO=?";
			pstmt=con.prepareStatement(query);
			pstmt.setString(1, article.getTitle());
			pstmt.setString(2, article.getContent());
			
			if(article.getImageFileName()!=null && article.getImageFileName().length()!=0) {
				pstmt.setString(3, article.getImageFileName());
				pstmt.setInt(4, article.getArticleNO());
			}
			else
				pstmt.setInt(3, article.getArticleNO());
			
			pstmt.executeUpdate();
			pstmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public List<Integer> deleteArticle(int articleNO) {
		List<Integer> childNOList=selectRemovedArticles(articleNO);

		try {
			con=dataFactory.getConnection();
			String query="delete from t_board where articleNO in ("
					+ "select articleNO from t_board start with articleNO=? connect by prior articleNO=parentNO)";
			pstmt=con.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.executeUpdate();
			

			pstmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return childNOList;
	}


	private List<Integer> selectRemovedArticles(int articleNO) {
		List<Integer> articleNOList = new ArrayList<Integer>();
		
		try {
			con=dataFactory.getConnection();
			String query="select articleNO from t_board start with articleNO=? connect by prior articleNO=parentNO";
			
			pstmt=con.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			
			ResultSet rs=pstmt.executeQuery();
			while (rs.next()) {
				articleNOList.add(rs.getInt("articleNO"));
			}
			
			rs.close();
			pstmt.close();
			con.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return articleNOList;
	}


	public List<ArticleVO> selectAllArticles(Map<String, Integer> pagingMap) {
		List<ArticleVO> articles=new ArrayList();
		
		try {
			con=dataFactory.getConnection();
			String query="SELECT * FROM( SELECT ROWNUM as recNum,LVL,articleNO,parentNO,title,content,id,writedate\r\n" + 
					"    FROM ( SELECT LEVEL as LVL, articleNO,parentNO,title,content,id,writedate\r\n" + 
					"            From t_board" + 
					"            START WITH parentNO=0" + 
					"            CONNECT BY PRIOR articleNO=parentNO" + 
					"            ORDER SIBLINGS BY articleNO DESC)" + 
					"    	 	)" + 
					"     where recNum between (?-1)*100+(?-1)*10+1 and (?-1)*100+?*10";
			pstmt=con.prepareStatement(query);
			
			int section=pagingMap.get("section");
			int pageNum=pagingMap.get("pageNum");
			pstmt.setInt(1, section);
			pstmt.setInt(2, pageNum);
			pstmt.setInt(3, section);
			pstmt.setInt(4, pageNum);
			
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				ArticleVO articleVO=new ArticleVO();
				articleVO.setLevel(rs.getInt("LVL"));
				articleVO.setArticleNO(rs.getInt("articleNO"));
				articleVO.setParentNO(rs.getInt("parentNO"));
				articleVO.setTitle(rs.getString("content"));
				articleVO.setId(rs.getString("id"));
				articleVO.setWriteDate(rs.getDate("writeDate"));
				
				articles.add(articleVO);
				
			}
			
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return articles;
	}


	public int selectTotArticles() {
		
		try {
			con=dataFactory.getConnection();
			String query="select count(articleNO) from t_board";
			pstmt=con.prepareStatement(query);
			
			ResultSet rs=pstmt.executeQuery();
			if(rs.next())
				return rs.getInt(1);
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
}
