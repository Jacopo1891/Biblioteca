package entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Reservation {
	@XmlAttribute
	private int ReservationId;
	@XmlAttribute
	private int UserIdr;
	@XmlAttribute
	private int BookId;
	@XmlAttribute
	private Date StartDate;
	@XmlAttribute
	private Date EndDate;
	
	public int getReservationId() {
		return ReservationId;
	}
	public void setReservationId(int id) {
		this.ReservationId = id;
	}
	public int getUserIdr() {
		return UserIdr;
	}
	public void setUserIdr(int userIdr) {
		UserIdr = userIdr;
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
