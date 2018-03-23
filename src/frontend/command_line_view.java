package frontend;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import Helpers.EditString;
import business_logic.Library;
import entity.Book;
import entity.Reservation;
import entity.User;

public class command_line_view {
	
	String path_xml = System.getProperty("user.dir");
	String name_file_xml = "Library.xml";
	String file_xml = path_xml + File.separator + name_file_xml;
	Scanner scanner;
	Library my_libr;
	private User logged_user;
	
	public command_line_view() {
		scanner = new Scanner( System.in );
		my_libr = new Library();
		my_libr.connectToData("tester", "firenze", "localhost", 1521);
		my_libr.loadData();
	}
	
	public void setCurrentUser( User u ) {
		logged_user = u;
	}
	
	public User getCurrentUser() {
		return logged_user;
	}
	
	public int command_choice_admin () {
		
		System.out.println("1) Insert new book");
		System.out.println("2) Edit book");
		System.out.println("3) Delete book");
		System.out.println("4) Update book");
		System.out.println("5) Show Books availables");
		System.out.println("6) Rent a book");
		System.out.println("7) Delete booking");
		System.out.println("8) Exit");
		System.out.print("Choose what you wanna do: ");
		int command_to_execute = scanner.nextInt();
		scanner.nextLine(); // To ignore newline after int choice
		return command_to_execute;
	}
	
	public int command_choice_user () {
		
		System.out.println("1) Rent a book");
		System.out.println("2) Book restitution");
		System.out.println("3) Show Books availables");
		System.out.println("4) Exit");
		System.out.print("Choose what you wanna do: ");
		int command_to_execute = scanner.nextInt();
		scanner.nextLine(); // To ignore newline after int choice
		return command_to_execute;
	}
	public void login() {
		System.out.println("Insert data to log in");
		System.out.print("Username: ");
		String username = scanner.next().replace("\n", "");
		System.out.print("Password: ");
		String pass = scanner.next().replace("\n", "");
		
		User logged_in = my_libr.login(username, pass);
		if ( logged_in!= null || logged_in instanceof User ) {
			logged_user = logged_in;
			System.out.println("Logged in! Welcome "+ logged_user.getUsername() + "!");
			System.out.print("");
		}else {
			System.out.println("Login failed! User or password incorrect.");
			System.out.println("");
			System.out.println("  !--------------------------------------------!");
			System.out.println("");
			login();
		}
	}
	
	public String[] getBookInputData( String s){
		/**
		 * Create and array with data of book
		 * @return String [] = ["_title_","_author_","_publisher_"]
		 */
		String [] value = new String[3];
		int command = -1;
		
		switch ( s ) {
		case "create": command = 1;
			break;
		case "search": command = 2;
			break;
		case "update": command = 3; 
			break;
		}
		
		if ( command != 3 ) {
			/**
			 * Title is not editable
			 */
			System.out.print("	Insert book's title: ");
			value[0] = scanner.nextLine().replace("\n", "");
			while ( command == 1 && ( value[0]== null || value[0].isEmpty() ) ) {
				/**
				 * Title required only on creation
				 */
				System.out.println("	Book's title can not be empty!");
				System.out.print("	Insert book's title: ");
				value[0] = scanner.nextLine().replace("\n", "");			
			}
		}

		System.out.print("	Insert book's author: ");
		value[1] = scanner.nextLine().replace("\n", "");
		while ( command == 1 && (value[1]== null || value[1].isEmpty() ) ) {
			/**
			 * Author required only on creation
			 */
			System.out.println("	Book's author can not be empty!");
			System.out.print("	Insert author's title: ");
			value[1] = scanner.nextLine().replace("\n", "");			
		}

		System.out.print("	Insert book's publisher: ");
		value[2] = scanner.nextLine().replace("\n", "");
		while ( command == 1 && (value[2]== null || value[2].isEmpty() ) ) {
			/**
			 * Published required only on creation
			 */
			System.out.println("	Book's publisher can not be empty!");
			System.out.print("	Insert book's publisher: ");
			value[2] = scanner.nextLine().replace("\n", "");			
		}
		
		if ( command == 3 ) {
			/**
			 * Ask for quantity on update
			 * -> Array[0] save quantity on update instead of title
			 */
			System.out.print("	Insert book's quantity: ");
			value[0] = scanner.nextLine().replace("\n", "");
		}
		return value;
	}
	
	public void insertNewBook() {
		
		System.out.println("Create a new book!");
		String[] value = getBookInputData( "create" );
		String [] param = {"Title", "Author", "Publisher"};
		boolean result = my_libr.insertNewBook(param, value, logged_user);
		
		if ( result ) {
			System.out.println("New book '" + value[0] + "' by "+ value[1] +" saved!");
		} else {
			System.out.println("Ooops! Something goes wrong!");
		}
	}
	
	public void deleteBook() {
		
		System.out.println("Delete a book!");		
		Book book_to_delete = secureSearchBook("search");
		
		if ( book_to_delete == null ) {
			System.out.println("Ooops! Book do not exist!");
			return;
		}
		
		boolean result;
		result = my_libr.deleteBook( book_to_delete, logged_user);
		
		if ( result ) {
			System.out.println("Deleted book '" + EditString.Capitalize(book_to_delete.getTitle()) + "' by "+ EditString.Capitalize(book_to_delete.getAuthor()) +"!");
		} else {
			System.out.println("Ooops! Something goes wrong!");
		}
	}

	public void updateBook() {
		System.out.println("Update a book!");
		System.out.println("Search a book:");
		boolean result;
		Book book_to_update = secureSearchBook("search");
		
		if ( book_to_update == null ) {
			System.out.println("Ooops! Book do not exist!");
			return;
		}
		System.out.println("Insert data to update:");
		String [] value = getBookInputData( "update" );
		
		if ( !value[1].equals("") ) {
			book_to_update.setAuthor(value[1]);
		}
		if ( !value[2].equals("")) {
			book_to_update.setPublischingHouse(value[2]);
		}
		
		if ( !value[0].equals("")) {
			try {
			    int quantity = Integer.parseInt(value[0]);
			    book_to_update.setQuantity(book_to_update.getQuantity() + quantity);
			}catch(Exception e) {
			    // If is not int value do not update and do nothing
			}
		}
		
		result = my_libr.updateBook( book_to_update, logged_user);
		
		if ( result ) {
			System.out.println("Updated book '" + EditString.Capitalize(book_to_update.getTitle()) + "' by "+ EditString.Capitalize(book_to_update.getAuthor()) +"!");
		} else {
			System.out.println("Ooops! Something goes wrong!");
		}		
	}
	
	public void getBooksAvailable() {
		/**
		 * Return all books that are available: not rent or quantity > bookings
		 */
		System.out.println("");
		System.out.println("Book that you can borrow:");
		LinkedList<Book> books = my_libr.getBooksAvailable();
		
		for ( Book b : books ) {
			System.out.println("Title:	" + EditString.Capitalize( b.getTitle() ) );
			System.out.println("	by " + EditString.Capitalize( b.getAuthor() ) );
			System.out.println("	Publisher: " + EditString.Capitalize( b.getPublischingHouse() ) );
			System.out.println("	Number of copy: " + b.getQuantity() );
		}
		System.out.println("!--         END        --!");
		System.out.println(" ");
	}
	
	public void insertNewBooking() {
		System.out.println("Rent a book!");
		System.out.println("Search a book:");

		Book book_to_rent = secureSearchBook("search") ;
		
		if ( book_to_rent == null ) {
			System.out.println("Book found: " + EditString.Capitalize( book_to_rent.getTitle()) +" written by " + EditString.Capitalize( book_to_rent.getAuthor()) );
			return;
		}
		
		Date date_start = new Date();		// TODAY
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date date_end = cal.getTime();		// TODAY + 1 MONTH
        Reservation r = new Reservation( logged_user.getUserId(), book_to_rent.getBookId(), date_start, date_end);
        boolean new_booking = my_libr.insertNewBooking( r );
        
        if ( new_booking ) {
        	SimpleDateFormat data_format = new SimpleDateFormat( "dd/MM/yyyy" );
     		System.out.println("	Enjoy your reading " + EditString.Capitalize( logged_user.getUsername() ) + "! "
     				+ " Your rental of " + EditString.Capitalize(book_to_rent.getTitle() ) +" expires on " + data_format.format( date_end ) );
        }
        System.out.println("");
	}
	
	public void deleteBooking() {
		System.out.println("Return a book!");
		System.out.println("What booking do you want end:");		
		
		Reservation reservation_to_delete = secureSearchReservation( logged_user );
		
		if ( reservation_to_delete == null) {
			System.out.println("	You have not reservations!");
			System.out.println("");
			return;
		}
		
		boolean del_reservation = my_libr.deleteBooking( reservation_to_delete );
		
		if ( del_reservation ) {
			System.out.println("Book correctly returned! Thanks " + EditString.Capitalize( logged_user.getUsername() ) + "!");
		} else {
			System.out.println("	Ooops! Something goes wrong!");
		}
		System.out.println("");
	}
	
	private Book secureSearchBook(String intent){
		/**
		 * Check if searched book exists - if exists return the book, else return null
		 * @return Book
		 */
		String [] param = {"Title", "Author", "Publisher"};
		String [] value = getBookInputData( intent );
		
		LinkedList<Book> result = my_libr.searchBook( param, value );
		if ( result.isEmpty() ) {
			return null;
		} else if ( result.size() == 1 ) {
			return result.getFirst();
		} else {
			return secureSearchBookChoice( result );
		}
	}
	
	private Book secureSearchBookChoice(LinkedList<Book> result) {
		/**
		 * 
		 */
		int book_selected = 0 ;
		System.out.println("Choose the right one book: ");
		for(int i = 0; i < result.size(); i++) {
			System.out.println("	" + (i + 1) + ") " + EditString.Capitalize(result.get(i).getTitle()) + " by " + EditString.Capitalize(result.get(i).getAuthor()));
		}
		System.out.print("Insert choice: ");
		try {
			book_selected = scanner.nextInt();
			scanner.nextLine();
			return result.get( book_selected - 1 );
		} catch( Exception e) {
			System.out.println("");
			secureSearchBookChoice( result );
		}
		return null;
	}

	private Reservation secureSearchReservation( User u) {
		System.out.println("Choose which booking do you want to end: ");
		LinkedList<Reservation> reservations = my_libr.searchReservationOfUser( null, u);
		int reservation_selected= 0;
		for(int i = 0; i < reservations.size(); i++) {
			Reservation r = reservations.get( i );
			String [] param = {"Book_Id"};
			String [] value = { Integer.toString( r.getBookId() )};
			
			LinkedList<Book> result = my_libr.searchBook( param, value );
			Book b = result.getFirst();
			SimpleDateFormat data_format = new SimpleDateFormat( "dd/MM/yyyy" );
			System.out.println("	" + (i + 1) + ") " + EditString.Capitalize(b.getTitle()) + " expires on " + data_format.format( r.getEndDate() ));
		}
		System.out.print("Insert choice: ");
		try {
			reservation_selected = scanner.nextInt();
			scanner.nextLine();
			return reservations.get( reservation_selected - 1 );
		} catch( Exception e) {
			System.out.println("");
			secureSearchReservation( u );
		}
		return null;
	}
	
	public static void main(String[] args) {

		command_line_view cmd_library = new command_line_view();
		int command = 0;
		boolean work = true;
		
		System.out.println("  !-- WELCOME TO LIBRARY CONSOLE APPLICATION --!");
		
		cmd_library.login();	// Login before all
		
		while ( work ) {
			if ( cmd_library.getCurrentUser().getRole().equals("Admin") ) {
					
					command = cmd_library.command_choice_admin ();			
					
					switch (command) {
					case 1: cmd_library.insertNewBook();
						break;
					case 3: cmd_library.deleteBook();
						break;
					case 4: cmd_library.updateBook();
						break;
					case 5: cmd_library.getBooksAvailable();
						break;
					case 6: cmd_library.insertNewBooking();
						break;
					case 7: cmd_library.deleteBooking();
						break;
					case 8: work = false; 
						break;
					}
					
				}
			
			if ( cmd_library.getCurrentUser().getRole().equals("User") ) {
				
					command = cmd_library.command_choice_user ();			
					
					switch (command) {
					case 1: cmd_library.insertNewBooking();
						break;
					case 2: cmd_library.deleteBooking();
					break;						
					case 3: cmd_library.getBooksAvailable();
						break;
					case 4: work = false;
						break;
						
					}
			}
		}
		System.out.println("  !- LIBRARY CONSOLE APPLICATION LOGGED OUT -!");
	}
}
