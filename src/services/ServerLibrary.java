package services;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import entity.*;
import result_manager.ComplexBooleanValue;
import result_manager.IValidationResult;
 
//Service Endpoint Interface
@WebService
//@SOAPBinding(style = Style.RPC)
@SOAPBinding(style = Style.DOCUMENT)
public interface ServerLibrary {
 
	@WebMethod User login(String username, String password);
	@WebMethod ComplexBooleanValue insertNewBook ( String title , String author, String publischer , User u ) ;
 
}