package backend;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Date;

public class Library {

	private LinkedList<Book> Books;
	private LinkedList<User> Users;
	private LinkedList<Reservation> Reservations;
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
	
	
}
