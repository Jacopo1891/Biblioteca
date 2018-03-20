package frontend;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import Helpers.EditString;
import business_logic.Library;
import entity.Book;
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
		System.out.println("4) Create booking");
		System.out.println("5) Delete booking");
		System.out.println("6) Exit");
		System.out.print("Choose what you wanna do: ");
		int command_to_execute = scanner.nextInt();
		scanner.nextLine(); // To ignore newline after int choice
		return command_to_execute;
	}
	
	public int command_choice_user () {
		
		System.out.println("1) Create booking");
		System.out.println("2) Book restitution");
		System.out.println("3) Exit");
		System.out.print("Choose what you wanna do: ");
		int command_to_execute = scanner.nextInt();
		
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
	
	public String[][] getBookInputData( boolean b){
		
		String [] param = {"Title", "Author", "Publisher"};
		String [] value = new String[3];
		
		System.out.print("Insert book's title: ");
		value[0] = scanner.nextLine().replace("\n", "");
		while ( value[0]== null || value[0].isEmpty()) {	
			System.out.println("Book's title can not be empty!");
			System.out.print("Insert book's title: ");
			value[0] = scanner.nextLine().replace("\n", "");			
		}

		System.out.print("Insert book's author: ");
		value[1] = scanner.nextLine().replace("\n", "");
		while ( value[1]== null || value[1].isEmpty() ) {	
			System.out.println("Book's author can not be empty!");
			System.out.print("Insert author's title: ");
			value[1] = scanner.nextLine().replace("\n", "");			
		}

		System.out.print("Insert book's publisher: ");
		value[2] = scanner.nextLine().replace("\n", "");
		while ( b && (value[2]== null || value[2].isEmpty() ) ) {	
			System.out.println("Book's publisher can not be empty!");
			System.out.print("Insert book's publisher: ");
			value[2] = scanner.nextLine().replace("\n", "");			
		}
		
		String[][] book_data = new String[][] { value, param};
		return book_data;
	}
	
	public void insertNewBook() {
		
		System.out.println("Create a new book!");
		String[][] book_values = getBookInputData( true );
		String [] param = (String[]) book_values[0];
		String [] value = (String[]) book_values[1];
		boolean result = my_libr.insertNewBook(param, value, logged_user);
		
		if ( result ) {
			System.out.println("New book '" + value[0] + "' by "+ value[1] +" saved!");
		} else {
			System.out.println("Ooops! Something goes wrong!");
		}
	}
	
	public void deleteBook() {
		
		System.out.println("Delete a book!");
		String [] param = {"Title", "Author", "Publisher"};
		String [] value = new String[3];
		
		System.out.print("Insert book's title: ");
		value[0] = scanner.nextLine().replace("\n", "");
		while ( value[0]== null || value[0].isEmpty()) {	
			System.out.println("Book's title can not be empty!");
			System.out.print("Insert book's title: ");
			value[0] = scanner.nextLine().replace("\n", "");			
		}

		System.out.print("Insert book's author: ");
		value[1] = scanner.nextLine().replace("\n", "");
		while ( value[1]== null || value[1].isEmpty() ) {	
			System.out.println("Book's author can not be empty!");
			System.out.print("Insert author's title: ");
			value[1] = scanner.nextLine().replace("\n", "");			
		}

		System.out.print("Insert book's publisher: ");
		value[2] = scanner.nextLine().replace("\n", "");			
		
		Book book_to_delete = my_libr.searchBook( param, value ).getFirst();
		boolean result;
		result = my_libr.deleteBook( book_to_delete, logged_user);
		
		if ( result ) {
			System.out.println("Deleted book '" + EditString.Capitalize(book_to_delete.getTitle()) + "' by "+ EditString.Capitalize(book_to_delete.getAuthor()) +"!");
		} else {
			System.out.println("Ooops! Something goes wrong!");
		}
	}

	public void updateBook() {
		
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
					case 6: work = false; 
						break;
					}
					
				}
			
			if ( cmd_library.getCurrentUser().getRole().equals("User") ) {
				
					command = cmd_library.command_choice_user ();			
					
					switch (command) {
					case 1: return;
						//break;
					case 3: work = false;
						break;
						
					}
			}
		}
		System.out.println("  !- LIBRARY CONSOLE APPLICATION LOGGED OUT -!");
	}

}
