package layer_data;

import java.util.LinkedList;

import org.w3c.dom.NodeList;

import entity.Book;
import entity.Reservation;
import entity.User;

public interface DataMng {

	public User checkLoginData( String user, String pass );
	public NodeList getBooksAvailable();
	public Book searchBook(String[] param, String[] value);
	public boolean insertNewBook(String[] param, String[] value, User u) ;
	public boolean updateBook( Book b, User u ) ;
	public boolean deleteBook( Book b, User u ) ;
	public boolean insertNewBooking( Reservation r ) ;
	public boolean deleteBooking( Reservation r ) ;
	
	public LinkedList<Book> getBooks();
	public LinkedList<User> getUsers();
	public LinkedList<Reservation> getReservations();
}
