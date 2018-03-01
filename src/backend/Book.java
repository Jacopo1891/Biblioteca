package backend;

public class Book {
	
	private String BookId;
	private String Title;
	private String Author;
	private String PublischingHouse;
	private int Quantity;
	public String getBookId() {
		return BookId;
	}
	public void setBookId(String bookId) {
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
