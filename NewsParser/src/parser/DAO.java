package parser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAO {

	//table name
	private static final String TBL_NM_G1_CRAWLING = "tb_g1_crawling";

	//Column names
	private static final String COL_VL_CRAWLING_TIME = "crawling_time";
	private static final String COL_VL_FILE_NAME = "file_name";
	private static final String COL_NM_URL_NOTICIA = "url_news";
	private static final String COL_VL_PLACE = "place";
	
	private Connection c = null;
	
	private Statement stmt = null;
	
	public DAO() {

	}
	
	public void insert(News news) throws SQLException{
		this.c = DriverManager
		        .getConnection("jdbc:postgresql://localhost:5432/geosen",
		        "geosen", "geosen");
		c.setAutoCommit(false);
		
		String place = "";
		if(news.getPlace() == null){
			place = "NULL";
		}else{
			place = "'" + news.getPlace() + "'";
		}
		
		stmt = c.createStatement();
		String sql = "INSERT INTO " + TBL_NM_G1_CRAWLING + "(" + COL_VL_CRAWLING_TIME + ", " + COL_VL_FILE_NAME + ", " + 
		COL_NM_URL_NOTICIA + ", " + COL_VL_PLACE + ") "
	               + "VALUES (" + "'" + news.getCrawling_id() + "'" + ", " +"'" + news.getFileName() + "'" + ", " + "'" + news.getLink() + "'" +
		", " + place + ");";
		stmt.executeUpdate(sql);
		stmt.close();
        c.commit();
        c.close();
		
	}
	
	public Integer getStateIdByUf(String uf) throws SQLException{
		this.c = DriverManager
		        .getConnection("jdbc:postgresql://localhost:5432/geosen",
		        "geosen", "geosen");
		c.setAutoCommit(false);
		
		Integer id = null;
		stmt = c.createStatement();
		String sql = "SELECT id FROM estados_br WHERE uf = " + "'" + uf + "';";
		ResultSet rs = stmt.executeQuery( sql );
		while ( rs.next() ) {
			id = rs.getInt("id");
		}
		stmt.close();
		c.close();
		return id;
	}

	public Connection getC() {
		return c;
	}

	public void setC(Connection c) {
		this.c = c;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	
	
	
	
	
	

}
