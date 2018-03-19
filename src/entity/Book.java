package entity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Book {
	@XmlAttribute
	private int BookId;
	@XmlAttribute
	private String Title;
	@XmlAttribute
	private String Author;
	@XmlAttribute
	private String PublischingHouse;
	@XmlAttribute
	private int Quantity;
	
	public Book() {	}
	
	public Book( String title, String author, String publisher) {
		setTitle( title );
		setAuthor( author );
		setPublischingHouse( publisher );
		setQuantity( 1 );
	}

	public Book(int id, String title, String author, String publisher, int quantity) {
		setBookId( id );
		setTitle( title );
		setAuthor( author );
		setPublischingHouse( publisher );
		setQuantity( quantity );
	}
	
	public int getBookId() {
		return BookId;
	}
	public void setBookId(int bookId) {
		BookId = bookId;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAuthor() {
		return Author;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public String getPublischingHouse() {
		return PublischingHouse;
	}
	public void setPublischingHouse(String publischingHouse) {
		PublischingHouse = publischingHouse;
	}
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	
}
