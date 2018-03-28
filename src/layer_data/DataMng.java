package layer_data;

import java.util.LinkedList;
import entity.*;
import result_manager.*;

public interface DataMng {

	public User checkLoginData( String user, String pass );
	public LinkedList<Book> getBooksAvailable();
	public LinkedList<Book> searchBook(String[] param, String[] value);
	public IValidationResult insertNewUser( User u );
	public IValidationResult insertNewBook(String[] param, String[] value, User u) ;
	public IValidationResult updateBook( Book b, User u ) ;
	public IValidationResult deleteBook( Book b, User u );
	public IValidationResult insertNewBooking( Reservation r ) ;
	public IValidationResult deleteBooking( Reservation r ) ;
	
	public LinkedList<Reservation> searchReservationOfUser(Book b, User u);
	
	public LinkedList<Book> getBooks();
	public LinkedList<User> getUsers();
	public LinkedList<Reservation> getReservations();
}
