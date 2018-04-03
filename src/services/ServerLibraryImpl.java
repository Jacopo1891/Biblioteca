package services;

import java.util.LinkedList;

import javax.jws.WebService;
import business_logic.*;
import entity.*;
import result_manager.*;


//Service Implementation
@WebService(endpointInterface = "services.ServerLibrary")
public class ServerLibraryImpl implements ServerLibrary {
	
	private Library my_libr;

	public ServerLibraryImpl() {}
	
	public ServerLibraryImpl(String path_xml) {
		my_libr = new Library();
		my_libr.connectToData( path_xml );
	}
	
	public ServerLibraryImpl( String user, String pass, String host, int port ) {
		my_libr = new Library();
		my_libr.connectToData(user, pass, host, port);
	}
	
	@Override
	public User login(String username, String password) {
		return my_libr.login( username, password );
	}

	@Override
	public ComplexBooleanValue insertNewUser(String username, String password, String role) {
		User new_user = new User( username, password, role );
		return (ComplexBooleanValue) my_libr.insertNewUser( new_user );
	}
	
	@Override
	public ComplexBooleanValue insertNewBook ( String title , String author, String publischer , User u ) {
		String[] param = {"Title","Author","Publischer"};
		String[] value = { title, author, publischer };
		return (ComplexBooleanValue) my_libr.insertNewBook(param, value, u);
	}
	
	@Override
	public ComplexBooleanValue deleteBook(Book b, User u) {
		return (ComplexBooleanValue) my_libr.deleteBook(b, u);
	}
	
	@Override
	public ComplexBooleanValue updateBook(Book b, User u) {
		return (ComplexBooleanValue)my_libr.updateBook( b, u);
	}
	
	@Override
	public LinkedList<Book> getBooksAvailable() {
		return my_libr.getBooksAvailable();
	}
	
	@Override
	public ComplexBooleanValue insertNewBooking(Reservation r) {
		return (ComplexBooleanValue) my_libr.insertNewBooking( r );
	}
	
	@Override
	public ComplexBooleanValue deleteBooking(Reservation r) {
		return (ComplexBooleanValue)my_libr.deleteBooking( r );
	}


	@Override
	public LinkedList<Reservation> searchReservationOfUser(User u) {
		return my_libr.searchReservationOfUser(null, u);
	}
}
