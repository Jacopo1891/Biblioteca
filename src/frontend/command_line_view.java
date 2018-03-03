package frontend;

import java.io.File;
import java.util.Scanner;

import backend.Library;
import backend.User;

public class command_line_view {
	
	String path_xml = System.getProperty("user.dir");
	String name_file_xml = "Library.xml";
	String file_xml = path_xml + File.separator + name_file_xml;
	Scanner scanner;
	Library my_libr;
	
	public command_line_view() {
		scanner = new Scanner(System.in);
		my_libr = new Library();
		my_libr.connectToData(file_xml);
	}
	
	public int command_choice () {
		

		System.out.println("1) Login");
		System.out.println("2) Insert new book");
		System.out.println("3) Edit book");
		System.out.println("4) Delete book");
		System.out.println("5) Create booking");
		System.out.println("6) Delete booking");
		System.out.println("7) Exit");
		System.out.print("Choose what you wanna do: ");
		int command_to_execute = scanner.nextInt();
		
		return command_to_execute;
	}
	
	public void login() {
		System.out.print("Username: ");
		String username = scanner.next().replace("\n", "");
		System.out.print("Password: ");
		String pass = scanner.next().replace("\n", "");
		
		User logged_in = my_libr.login(username, pass);
		if ( logged_in!= null || logged_in instanceof User ) {
			System.out.println("Logged in! Welcome "+ logged_in.getUsername() + "!");
		}else {
			System.out.println("Login failed! User or password incorrect.");
			System.out.println("--------------");
			System.out.println("");
			login();
		}
	}

	public static void main(String[] args) {

		command_line_view cmd_library = new command_line_view();
		int command = 0;
		
		while (command != 7) {
			
			command = cmd_library.command_choice ();			
			
			switch (command) {
			case 1: cmd_library.login();
				break;
			case 7: return;
				
		}
			
		}
		

	}

}
