package layer_data;

import java.math.BigDecimal;
import java.security.AccessControlException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import entity.*;

public class DBMng implements DataMng {

	private String user;
	private String pass; 
	private String host;
	private int port;
	
/* ############################
 * ###   GETTERS / SETTERS  ###	
 * ############################
 */
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


/*  ############################
 *  ##       CONSTRUCTOR      ##
 *  ############################
 */
	
	public DBMng( String user, String pass, String host, int port ) {
		/**
		 *  Data to connect DB
		 */
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");		
		} catch (ClassNotFoundException e ) {
			e.printStackTrace();
		} 
		
		setUser( user );
		setPass( pass );
		setHost( host );
		setPort( port );
	}
	
	/*  ############################
	 *  ##  METHOD FROM INTERFACE ##
	 *  ############################
	 */

	public User checkLoginData( String user, String pass ) {
		
		User user_logged = new User();
		
		String check_user_query = "SELECT * FROM USERS WHERE USERNAME = '"+ user +"' AND PASSWORD ='" + pass +"'";
		LinkedList<Map<String, Object>> user_data_db = db_query( check_user_query );
		
		if( !user_data_db.isEmpty() ) {
			user_logged = createUserFromData( user_data_db.getFirst() );
		}
		
		return user_logged;
	}
	
	public boolean insertUser( User u ) {
		/**
		 * Insert new User u inside table USERS
		 * @return true / false
		 */
		String check_user = "SELECT * FROM USERS WHERE USERNAME = '"+ u.getUsername() +"'" ;
		LinkedList<Map<String, Object>> check;
		try {
			check = db_query( check_user );
			if ( check.isEmpty() ) {
				String query_insert = "INSERT INTO USERS (USERNAME, PASSWORD, ROLE)"
						+ " VALUES ('"+  u.getUsername()  +"', '"+ u.getPassword() +"', '"+ u.getRole() +"')";
				db_query( query_insert );
				return true;
			} else {
				throw new SQLException("Failed. Username alraedy exists!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;		
	}
	
	public boolean insertBook( String[] param, String[] value, User u )  {
		/**
		 * Insert new book inside table BOOKS
		 * @return true / false
		 */
		if ( ! u.getRole().equals("Admin")) {
			throw new AccessControlException("Permission denided. You're not Admin!");
		}
	
		LinkedList<Book> result = searchBook(param, value);
		if ( result.isEmpty() ) {
			String query_param = "";
			String query_value ="";
			for ( int i = 0; i < param.length ; i++ ) {
				query_param += ( i == 0)? param[i] : " , " + param[i];
				
				query_value += ( i== 0)? "'"+ value[i].toLowerCase() +"'" : ", '"+ value[i].toLowerCase() +"'" ;
			}
			
			String query = "INSERT INTO BOOKS (" + query_param +", QUANTITY) VALUES (" + query_value + ", 1)";
			//System.out.println("INSERT: " + query);
			return db_query_update( query );
		} else {
			Book b_found = result.getFirst();
			String query_update = "UPDATE BOOKS SET QUANTITY = QUANTITY + 1 WHERE BOOK_ID = "+ b_found.getBookId();
			boolean res_update = db_query_update( query_update );
			return res_update;
		}
	}

	@Override
	public LinkedList<Book> getBooksAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedList<Book> searchBook( String[] param, String[] value ) {
	/**
	 * search books inside BOOKS using params and values
	 * @return List of book obj	
	 */
		String query = "SELECT * FROM BOOKS WHERE (";
		LinkedList<Map<String, Object>> res_query = null;
		LinkedList<Book> result = new LinkedList<Book>();
		for ( int i = 0; i < param.length ; i++ ) {
			query += ( i == 0)? "" : " AND ";
			query += param[i] + " LIKE '%" + value[i].toLowerCase() +"%'";
		}
		query += ")";
		//System.out.println( "SEARCH: " + query );
		res_query = db_query( query );
		if ( res_query.isEmpty() ) {
			return result;
		} else {
			for (int i = 0; i < res_query.size(); i++) {
				Book b_temp = createBookFormData( res_query.get(i) );
				result.add( b_temp );
			}
		}
		return result;
	}

	@Override
	public boolean insertNewBook(String[] param, String[] value, User u) {
		if ( ! u.getRole().equals("Admin")) {
			throw new AccessControlException("Permission denided. You're not Admin!");
		}
	
		LinkedList<Book> result = searchBook(param, value);
		if ( result.isEmpty() ) {
			String query_param = "";
			String query_value ="";
			for ( int i = 0; i < param.length ; i++ ) {
				query_param += ( i == 0)? param[i] : " , " + param[i];
				
				query_value += ( i== 0)? "'"+ value[i].toLowerCase() +"'" : ", '"+ value[i].toLowerCase() +"'" ;
			}
			
			String query = "INSERT INTO BOOKS (" + query_param +", QUANTITY) VALUES (" + query_value + ", 1)";
			//System.out.println("INSERT: " + query);
			return db_query_update( query );
		} else {
			Book b_found = result.getFirst();
			String query_update = "UPDATE BOOKS SET QUANTITY = QUANTITY + 1 WHERE BOOK_ID = "+ b_found.getBookId();
			boolean res_update = db_query_update( query_update );
			return res_update;
		}
	}

	@Override
	public boolean updateBook(Book b, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteBook(Book b, User u) throws ParserConfigurationException, TransformerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertNewBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LinkedList<Book> getBooks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedList<Reservation> getReservations() {
		// TODO Auto-generated method stub
		return null;
	}

	/*  ############################
	 *  ###        METHODS       ###
	 *  ############################
	 */	
	public Statement connect_to_db() {
		/**
		 * Connect to DB
		 * @return Statement
		 */
		String param = "jdbc:oracle:thin:@"+host+":"+port+"/orcl";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection( param, user, pass );
			stmt = conn.createStatement();
			if ( stmt == null) {
				throw new SQLException("Connection to DB failed!");
			}
			return stmt ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
		
	}
	
	public void initializeTables(){
		/**
		 * Create tables into DB
		 */
		String create_users = "CREATE TABLE USERS("
				+ "USER_ID INTEGER GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY, "
				+ "USERNAME VARCHAR(50) NOT NULL, "
				+ "PASSWORD VARCHAR(50) NOT NULL, "
				+ "ROLE VARCHAR(10),"
				+ "UNIQUE(USERNAME)"
				+ ")";
		db_query( create_users );
		String create_books = "CREATE TABLE BOOKS("
				+ "BOOK_ID INTEGER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) NOT NULL PRIMARY KEY,"  
				+ "TITLE VARCHAR(100) NOT NULL,"
				+ "AUTHOR  VARCHAR(50),"
				+ "PUBLISHER VARCHAR(50)," 
				+ "QUANTITY INTEGER,"
				+ "UNIQUE(TITLE,AUTHOR)"
				+ ")";
		db_query( create_books );
				
		String create_reservations = "CREATE TABLE RESERVATIONS("
				+ "RESERVATION_ID INTEGER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) NOT NULL PRIMARY KEY,"
				+ "R_BOOK_ID INTEGER NOT NULL,"
				+ "R_USER_ID INTEGER NOT NULL,"
				+ "START_DATE DATE,"
				+ "END_DATE DATE,"
				+ "CONSTRAINT fk_book FOREIGN KEY (R_BOOK_ID) REFERENCES BOOKS(BOOK_ID),"
				+ "CONSTRAINT fk_user FOREIGN KEY (R_USER_ID) REFERENCES Users(USER_ID)"
				+ ")";
		db_query( create_reservations );
		
	}
	
	public LinkedList<Map<String, Object>> db_query ( String query ) {
		/**
		 * Execute a query on DB, Map results
		 * @return LinkedList of Map obj <Column_name, Value>
		 */
		LinkedList<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		Map<String, Object> row = null;
		Statement conn = null;
		
		try {
			conn = connect_to_db();
			ResultSet r = conn.executeQuery( query );
			//System.out.println( query );
			ResultSetMetaData metaData = r.getMetaData();
			while( r.next() ) {
				row = new HashMap<String, Object>();
				for ( int i = 1; i <= metaData.getColumnCount(); i++ ) {
					row.put(metaData.getColumnName( i ), r.getObject( i ));
				}
				result.add( row );
			}
		} catch (SQLException e) {
			//System.out.println("Query failed: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if ( conn != null) {System.out.println("Connection closed."); conn.close();}
			} catch ( SQLException e ){
				System.out.println("Error closing connection to db: "+ e.getMessage() );
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean db_query_update ( String query ) {
		/**
		 * Execute a query to update data on DB
		 * @return true / false
		 */
		Statement conn = null;
		int r = -1;

		try {
			conn = connect_to_db();
			r = conn.executeUpdate( query );	
		} catch (SQLException e) {
			System.out.println("Updated failed: " + e.getMessage());
			System.out.println( e.getStackTrace() );
		} finally {
			try {
				if ( conn != null) {System.out.println("Connection closed."); conn.close();}
			} catch ( SQLException e ){
				System.out.println("Error closing connection to db:  "+ e.getMessage() );
			}
		}
		return ( r < 0 ) ? false : true;
	}
	
	public Book createBookFormData( Map<String, Object> element ) {
		/**
		 * Create a Book obj from DB's data mapped
		 * @return Book
		 */
		int book_id = ((BigDecimal) element.get("BOOK_ID")).intValue();
		String title = (String) element.get("TITLE");
		String author = (String) element.get("AUTHOR");
		String publisher = (String) element.get("PUBLISHER");
		int quantity = ((BigDecimal) element.get("QUANTITY")).intValue();
		Book b = new Book(book_id, title, author,publisher,quantity);
		return b;
	}
	
	public User createUserFromData( Map<String, Object> element ) {
		/**
		 * Create a User obj from DB's data mapped
		 * @return User
		 */
		int user_id = ((BigDecimal) element.get("USER_ID")).intValue();
		String username = (String) element.get("USERNAME");
		String password = (String) element.get("PASSWORD");
		String role = (String) element.get("ROLE");
		User u = new User(user_id,username, password, role);
		return u;
	}

}
