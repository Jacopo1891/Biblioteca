package services;

import java.util.LinkedList;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import entity.*;
import result_manager.*;
 
//Service Endpoint Interface
@WebService
//@SOAPBinding(style = Style.RPC)
@SOAPBinding(style = Style.DOCUMENT)
public interface ServerLibrary {
 
	@WebMethod User login(String username, String password);
	@WebMethod ComplexBooleanValue insertNewUser ( String username, String password, String role );	
	
	@WebMethod ComplexBooleanValue insertNewBook ( String title , String author, String publischer , User u ) ;
	@WebMethod ComplexBooleanValue deleteBook( Book b, User u);
	@WebMethod ComplexBooleanValue updateBook( Book b, User u );
	@WebMethod LinkedList<Book> getBooksAvailable();		
	
	@WebMethod ComplexBooleanValue insertNewBooking ( Reservation r );
	@WebMethod ComplexBooleanValue deleteBooking ( Reservation r );
	@WebMethod LinkedList<Reservation> searchReservationOfUser( User u );
}