package layer_data;

import java.math.BigDecimal;
import java.security.AccessControlException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import Helpers.EditString;
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
		
		User user_logged = null;
		
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
				
				query_value += ( i == 0)? "'"+ value[i].toLowerCase() +"'" : ", '"+ value[i].toLowerCase() +"'" ;
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

	public LinkedList<Book> getBooksAvailable() {
		/**
		 * Search books inside BOOKS that quantity > number of reservations
		 * @return list of Book obj
		 */
		String query_book_available = "SELECT  BOOKS.BOOK_ID," + 
					" BOOKS.TITLE,"+ 
					" BOOKS.AUTHOR," + 
					" BOOKS.PUBLISHER," + 
					" BOOKS.QUANTITY as TOTAL_QNT," + /* NOT USED BUT MAYBE USEFULL LATER */
					" BOOKS.QUANTITY - COUNT ( RES.RESERVATION_ID ) as QUANTITY," + 
					" COUNT ( RES.RESERVATION_ID ) as RESERVED" + 
					" FROM BOOKS" + 
					" LEFT JOIN (SELECT * FROM RESERVATIONS WHERE RESERVATIONS.END_DATE >= CURRENT_DATE) RES" +
					" ON  (BOOKS.BOOK_ID = RES.R_BOOK_ID)" + 
					" GROUP BY BOOKS.BOOK_ID," + 
					" BOOKS.TITLE," + 
					" BOOKS.AUTHOR," + 
					" BOOKS.PUBLISHER," + 
					" BOOKS.QUANTITY" + 
					" HAVING BOOKS.QUANTITY - COUNT ( RES.RESERVATION_ID ) > 0";
			
			LinkedList<Map<String, Object>> book_availables = db_query( query_book_available );
			LinkedList<Book> result = new LinkedList<Book>();
			for (int i = 0; i < book_availables.size(); i++ ) {
				Book temp = createBookFromData( book_availables.get( i ) );
				result.add( temp );
			}		
	 		return result;
		}

	public LinkedList<Book> searchBook( String[] param, String[] value ) {
	/**
	 * search books inside BOOKS using params and values
	 * @return List of Book obj	
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
				Book b_temp = createBookFromData( res_query.get(i) );
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

	public boolean updateBook( Book b, User u) {
		/**
		 * Update a Book with new info 
		 * @return boolean 
		 */		
		if ( ! u.getRole().equals("Admin")) {
			throw new AccessControlException("Permission denided. You're not Admin!");
		}
		String query_update = "UPDATE BOOKS SET "
				+ "AUTHOR = '" + b.getAuthor() + "', "
				+ "PUBLISHER ='" + b.getPublischingHouse() + "', "
				+ "QUANTITY = "+ b.getQuantity() +" WHERE BOOK_ID = "+b.getBookId();
		//System.out.println( query_update );
		boolean check = db_query_update( query_update );
		
		return check;
	}

	public boolean deleteBook(Book b, User u) {
		/**
		 * Delete a Book, if is not reserved
		 * @return boolean 
		 */		
		if ( ! u.getRole().equals("Admin")) {
			throw new AccessControlException("Permission denided. You're not Admin!");
		}
		LinkedList<Map<String, Object>> result = getActiveBookingBook( b );
		if ( result.isEmpty()  || result.size() < b.getQuantity()) {
			String query_update = "UPDATE BOOKS SET QUANTITY = QUANTITY - 1 WHERE BOOK_ID = "+ b.getBookId();
			boolean res_update = db_query_update( query_update );
			return res_update;
		}
		else if ( b.getQuantity() > 0) {
			String error_string = "Delete denided. Delete blocked because the book '"+ EditString.Capitalize( b.getTitle() )+"' is lend to: ";
			for (int i = 0; i < result.size(); i++ ) {
				String username = EditString.Capitalize((String) result.get( i ).get( "USERNAME" ));
				
				SimpleDateFormat data_format = new SimpleDateFormat("dd/MM/yyyy");
				Timestamp start_data = (Timestamp) result.get( i ).get( "START_DATE" );
				Timestamp end_data = (Timestamp) result.get( i ).get( "END_DATE" );
				error_string += "\n- Copy n°"+ (i + 1) +" to " + EditString.Capitalize( username ) +" from "+  data_format.format( start_data )  + " until " + data_format.format( end_data );
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean insertNewBooking(Reservation r) {
		/**
		 * Insert a new reservation
		 * @return true / false
		 */
		String[] value = new String[] { Integer.toString( r.getBookId() )};
		Book b = searchBook(new String[]{"BOOK_ID"}, value ).getFirst();
		
		LinkedList<Map<String, Object>> result = getActiveBookingBook( b );
        SimpleDateFormat data_format = new SimpleDateFormat( "yyyy-MM-dd" );
		Date firstDayFree = new Date();
        if ( result.isEmpty()  || result.size() < b.getQuantity()) {
			for ( Map<String, Object> el : result ) {
				// Check if user already has a reservation with book
				Reservation temp_r = createReservationFromData( el );
				if ( temp_r.getBookId() == r.getBookId() && temp_r.getUserId() == r.getUserId() ) {
					System.out.print("Booking failed! You're already renting this book!");
					return false;
				}
				firstDayFree = ( firstDayFree.before( r.getEndDate() ) ? firstDayFree : r.getEndDate() );
			}
			String query_insert = "INSERT INTO RESERVATIONS "
								+ "(R_BOOK_ID, R_USER_ID, START_DATE, END_DATE) "
								+ "VALUES "
								+ "(" + r.getBookId() + ", "
								+ r.getUserId()+", "
								+ "TO_DATE('"+ data_format.format( r.getStartDate() ) +"','yyyy-MM-dd'), "
								+ "TO_DATE('"+ data_format.format(r.getEndDate() ) +"','yyyy-MM-dd'))";
			//System.out.println( query_insert );
			return db_query_update( query_insert );
		} else if ( result.size() >= b.getQuantity() ) {
			data_format = new SimpleDateFormat( "dd/MM/yyyy" );
			System.out.println("Booking failed! The book: " + EditString.Capitalize( b.getTitle() ) + " will be bookable after " + data_format.format( firstDayFree ));
		} 
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
	
	private void initializeTables(){
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
	
	private LinkedList<Map<String, Object>> db_query ( String query ) {
		/**
		 * Execute a query on DB, Map results
		 * @return LinkedList of Map obj <Column_name, Value>
		 */
		LinkedList<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		Statement conn = null;
		
		try {
			conn = connect_to_db();
			ResultSet r = conn.executeQuery( query );
			//System.out.println( query );
			result = statementToList( r );
		} catch (SQLException e) {
			//System.out.println("Query failed: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if ( conn != null) {
					//System.out.println("Connection closed."); 
					conn.close();}
			} catch ( SQLException e ){
				System.out.println("Error closing connection to db: "+ e.getMessage() );
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private boolean db_query_update ( String query ) {
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
			e.getStackTrace();
		} finally {
			try {
				if ( conn != null) {
					//System.out.println("Connection closed."); 
					conn.close();
					}
			} catch ( SQLException e ){
				System.out.println("Error closing connection to db:  "+ e.getMessage() );
			}
		}
		return ( r < 0 ) ? false : true;
	}
	
	private LinkedList<Map<String, Object>> statementToList ( ResultSet r){
		
		LinkedList<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

		try {
			while( r.next() ) {
				ResultSetMetaData metaData = r.getMetaData();
				Map<String, Object> row = null;
				row = new HashMap<String, Object>();
				for ( int i = 1; i <= metaData.getColumnCount(); i++ ) {
					row.put(metaData.getColumnName( i ), r.getObject( i ));
				}
				result.add( row );
			}
		} catch (SQLException e) {
			System.out.println("Error parsing ResultSet from DB.");
			e.printStackTrace();
		}
		
		return result;
	}
	
	private Book createBookFromData( Map<String, Object> element ) {
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
	
	private User createUserFromData( Map<String, Object> element ) {
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
	
	private Reservation createReservationFromData( Map<String, Object> element ) {
		/**
		 * Create a Reservation obj from DB's data mapped
		 * @return Reservation
		 */
		int reservation_id = ((BigDecimal) element.get("RESERVATION_ID")).intValue();
		int user_id = ((BigDecimal) element.get("R_USER_ID")).intValue();
		int book_id = ((BigDecimal) element.get("R_BOOK_ID")).intValue();
		Date dataStart = new Date( ((Date) element.get("START_DATE")).getTime() );
		Date dataEnd = new Date( ((Date) element.get("END_DATE")).getTime() );
		Reservation r = new Reservation( reservation_id, user_id, book_id, dataStart, dataEnd);
		return r;
	}
	
	private LinkedList<Map<String, Object>> getActiveBookingBook( Book b){
		/**
		 * Get active booking's book
		 * @return list of booking's information mapped:
		 * [ 	("BOOK_ID" , b.BOOK_ID ),
		 * 		... all book info,
		 * 		("RESERVATION_ID" , _value_ ),
		 * 		... all reservation info,
		 * 		("USER_ID" , _value_ ),
		 * 		... all user info,
		 * ] 
		 */
		String query_lent_books = "SELECT * FROM BOOKS JOIN RESERVATIONS ON "
				+ "( RESERVATIONS.R_BOOK_ID = BOOKS.BOOK_ID ) JOIN USERS ON "
				+ "(RESERVATIONS.R_USER_ID = USERS.USER_ID) "
				+ "WHERE BOOKS.BOOK_ID = "+ b.getBookId() +" "
				+ "AND RESERVATIONS.END_DATE > CURRENT_DATE";
		return  db_query( query_lent_books );
		
	}

}
