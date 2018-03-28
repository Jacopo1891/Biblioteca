package entity;

import java.util.Date;

//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement
public class Reservation {
	//@XmlAttribute
	private int ReservationId;
	//@XmlAttribute
	private int UserId;
	//@XmlAttribute
	private int BookId;
	//@XmlAttribute
	private Date StartDate;
	//@XmlAttribute
	private Date EndDate;
	
	public Reservation() {}
	public Reservation( int user_id, int book_id, Date dataStart, Date dataEnd) {
		setUserId( user_id );
		setBookId( book_id );
		setStartDate( dataStart );
		setEndDate( dataEnd );
	}
	public Reservation(int reservation_id, int user_id, int book_id, Date dataStart, Date dataEnd) {
		setReservationId( reservation_id );
		setUserId( user_id );
		setBookId( book_id );
		setStartDate( dataStart );
		setEndDate( dataEnd );
	}
	public int getReservationId() {
		return ReservationId;
	}
	public void setReservationId(int id) {
		this.ReservationId = id;
	}
	public int getUserId() {
		return UserId;
	}
	public void setUserId(int userId) {
		UserId = userId;
	}
	public int getBookId() {
		return BookId;
	}
	public void setBookId(int bookId) {
		BookId = bookId;
	}
	public Date getStartDate() {
		return StartDate;
	}
	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}
	public Date getEndDate() {
		return EndDate;
	}
	public void setEndDate(Date endDate) {
		EndDate = endDate;
	}
	
	
}
