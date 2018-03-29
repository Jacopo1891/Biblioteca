package services;

import java.util.LinkedList;

import javax.jws.WebService;
import business_logic.*;
import entity.*;
import result_manager.ComplexBooleanValue;
import result_manager.IValidationResult;


//Service Implementation
@WebService(endpointInterface = "services.ServerLibrary")
public class ServerLibraryImpl implements ServerLibrary {
	
	private Library my_libr;

	public ServerLibraryImpl() {}
	
	public ServerLibraryImpl(String path_xml) {
		my_libr = new Library();
		my_libr.connectToData( path_xml );
	}
	
	@Override
	public User login(String username, String password) {
		return my_libr.login( username, password );
	}
	
	@Override
	public ComplexBooleanValue insertNewBook ( String title , String author, String publischer , User u ) {
		String[] param = {"Title","Author","Publischer"};
		String[] value = { title, author, publischer };
		return (ComplexBooleanValue) my_libr.insertNewBook(param, value, u);
	}

	@Override
	public ComplexBooleanValue insertNewUser(String username, String password, String role) {
		User new_user = new User( username, password, role );
		return (ComplexBooleanValue) my_libr.insertNewUser( new_user );
	}

	@Override
	public ComplexBooleanValue deleteBooking(Reservation r) {
		return (ComplexBooleanValue)my_libr.deleteBooking( r );
	}

	@Override
	public LinkedList<Book> getBooksAvailable() {
		return my_libr.getBooksAvailable();
	}

	@Override
	public ComplexBooleanValue updateBook(Book b, User u) {
		return (ComplexBooleanValue)my_libr.updateBook( b, u);
	}

}
