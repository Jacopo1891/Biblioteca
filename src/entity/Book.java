package entity;

public class Book {
	
	private int BookId;
	private String Title;
	private String Author;
	private String PublischingHouse;
	private int Quantity;
	
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
