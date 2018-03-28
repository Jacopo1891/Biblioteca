package services;

import javax.jws.WebService;
import business_logic.*;
import entity.*;
import result_manager.ComplexBooleanValue;
import result_manager.IValidationResult;


//Service Implementation
@WebService(endpointInterface = "services.ServerLibrary")
public class ServerLibraryImpl implements ServerLibrary {
	
	private Library my_libr;

	public ServerLibraryImpl() {}
	
	public ServerLibraryImpl(String path_xml) {
		my_libr = new Library();
		my_libr.connectToData( path_xml );
	}
	
	@Override
	public User login(String username, String password) {
		return my_libr.login( username, password );
	}
	
	@Override
	public ComplexBooleanValue insertNewBook ( String title , String author, String publischer , User u ) {
		String[] param = {"TITLE","AUTHOR","PUBLISCHER"};
		String[] value = { title, author, publischer };
		return (ComplexBooleanValue) my_libr.insertNewBook(param, value, u);
	}

}
