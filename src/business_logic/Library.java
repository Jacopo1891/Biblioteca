package business_logic;
import java.util.LinkedList;
import entity.*;
import layer_data.*;

public class Library {
	
	private LinkedList<Book> Books;
	private LinkedList<User> Users;
	private LinkedList<Reservation> Reservations;
	private DataMng data;
	
	public Library () {
		
	}
	
	public DataMng getData() {
		return data;
	}

	public void setData(DataMng data) {
		this.data = data;
	}

	public LinkedList<Book> getBooks() {
		return Books;
	}
	public void setBooks(LinkedList<Book> books) {
		Books = books;
	}
	public LinkedList<User> getUsers() {
		return Users;
	}
	public void setUsers(LinkedList<User> users) {
		Users = users;
	}
	public LinkedList<Reservation> getReservations() {
		return Reservations;
	}
	public void setReservations(LinkedList<Reservation> reservations) {
		Reservations = reservations;
	}
	
	public void connectToData(String path_file) {
		data = new XMLMng(path_file);
	}
	
	public User login( String user, String pass ) {
		User check_login_data = data.checkLoginData(user, pass);
		if ( check_login_data!= null || check_login_data instanceof User ) {
			return check_login_data;
		}
		return null;
	}
	
	public void loadData() {
		setBooks( data.getBooks() );
		setUsers( data.getUsers() );
		setReservations( data.getReservations() );
	}
	
	public boolean insertNewBook ( String[] param, String[] value, User u ) {
		
		
		return data.insertNewBook(param, value, u);
	}
	
	
}
