package backend;
import java.util.Date;

import org.w3c.dom.Node;

import gest_dati.DataMng;
import gest_dati.XMLMng;

public class Library {
	
	private Node Books;
	private Node Users;
	private Node Reservations;
	private DataMng data;
	
	public Library () {
		
	}
	
	public Node getBooks() {
		return Books;
	}
	public void setBooks(Node books) {
		Books = books;
	}
	public Node getUsers() {
		return Users;
	}
	public void setUsers(Node users) {
		Users = users;
	}
	public Node getReservations() {
		return Reservations;
	}
	public void setReservations(Node reservations) {
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
	
	
	
}
