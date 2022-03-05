package sec03.brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {

	BoardDAO boardDAO;
	
	public BoardService() {
		boardDAO=new BoardDAO();
	}
	
	public Map listArticles(Map<String, Integer> pagingMap) {
		List<ArticleVO> articlesList=boardDAO.selectAllArticles(pagingMap);
		int totArticle=boardDAO.selectTotArticles();
		
		Map articlesMap= new HashMap();
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totArticle", 150);
		
		return articlesMap;
	}
	
	
	public List<ArticleVO> listArticles() {
		List<ArticleVO> articleList=boardDAO.selectAllArticles();
		return articleList;
	}


	public int addArticle(ArticleVO articleVO) {
		return boardDAO.isertNewArticle(articleVO);
	}


	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article=boardDAO.selectArticle(articleNO);
		return article;
	}


	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}


	public List<Integer> removeArticle(int articleNO) {
		return boardDAO.deleteArticle(articleNO);
	}




}
