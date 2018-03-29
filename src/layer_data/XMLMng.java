package layer_data;


import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Helpers.EditString;

import java.util.Arrays;

import entity.*;
import result_manager.ComplexBooleanValue;
import result_manager.IValidationResult;

public class XMLMng implements DataMng {
	
	private String xml_file;
	
	public XMLMng( String path_file ) {
		xml_file = path_file;
	}
	
	public User checkLoginData(String user, String pass) {
	/**
	 * Check if login data are correct
	 * @return User / Null
	 */	
		Document doc;
		User user_logged = null;
		try {
			doc = readFile(xml_file);
			doc.getDocumentElement().normalize();
			NodeList users = searchElementGroup( doc , "User");
			for(int i = 0; i < users.getLength(); i++) {
				Element list_user = (Element) users.item(i);
				if( list_user.getAttribute("Username").equals( user )  && list_user.getAttribute("Password").equals(pass) ) {

					user_logged = createUserFromData( list_user );
					
					return user_logged;
				}
			}

		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return user_logged;
	}

	public LinkedList<Book> getBooksAvailable() {

		LinkedList<Book> books_available = new LinkedList<Book>();
		Document doc;
		
		try {
			doc = readFile (xml_file);
			NodeList books = searchElementGroup( doc, "Book");
			for ( int i = 0; i< books.getLength(); i++) {
				Book temp_book = createBookFromData( (Element) books.item(i) );
				LinkedList<Reservation> book_reserv = getActiveReservation( temp_book, null);
				if ( temp_book.getQuantity() > book_reserv.size() ) {
					books_available.add( temp_book );
				}
			}
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return books_available;
	}

	public LinkedList<Book> searchBook(String[] param, String[] value) {
		/**
		 * Looks for a book
		 * @return Book if found / Null
		 */	
			Document doc;
			LinkedList<Book> result = new LinkedList<Book>();
			try {
				doc = readFile(xml_file);
				doc.getDocumentElement().normalize();
				NodeList books = searchElementGroup( doc , "Book");
				for(int i = 0; i < books.getLength(); i++) {
					Element book_list = (Element) books.item(i);
					boolean found = true;
					for (int j = 0; j < param.length; j++) {
						String temp_param = param[j];
						String temp_value = value[j];
						
						if ( !book_list.getAttribute(temp_param).equals( temp_value )) {
							found = false;
							break;
						}
					}
					
					if ( found ) {
						Book book_found = createBookFromData( book_list );
						result.add( book_found );				
					}
				}
				
			} catch (ParserConfigurationException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

	public IValidationResult insertNewBooking(Reservation r) {
		/**
		 * Insert a new Reservation
		 * @return IValidationResult true / false + message 
		 */		
		try {
			Document doc = readFile( xml_file );
			SimpleDateFormat data_format = new SimpleDateFormat("dd/MM/yyyy");
			int new_id = getNewIdReservation();
			String [] param = {	"ReservationId",
								"BookId",
								"UserId",
								"StartDate",
								"EndDate"};

			String [] value = {	String.valueOf( new_id ),
								String.valueOf( r.getBookId() ),
								String.valueOf( r.getUserId() ),
								data_format.format( r.getStartDate() ),
								data_format.format( r.getEndDate() )
								};
			
			doc = insertElement( doc, "Reservation", param, value);
			writeFile( doc, xml_file );
			return new ComplexBooleanValue( true );
			
		} catch ( AccessControlException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new ComplexBooleanValue( "Something goes wrong!" );
	}

	public IValidationResult deleteBooking(Reservation r) {
		/**
		 * Delete reservation setting EndDat = today
		 * @return IValidationResult true / false + message
		 */
		try {
			Document doc = readFile( xml_file );
			NodeList reservations_list = searchElementGroup( doc, "Reservation");
			SimpleDateFormat data_format = new SimpleDateFormat("dd/MM/yyyy");
			for(int i = 0; i < reservations_list.getLength(); i++) {
				Element reservation_found = (Element) reservations_list.item(i);
				if ( r.getReservationId() == Integer.parseInt( reservation_found.getAttribute( "ReservationId" )) ){
					
					reservation_found.setAttribute("EndDate", data_format.format( new Date()) );
					writeFile( doc, xml_file );
					return new ComplexBooleanValue( true );
				}
			}
			
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new ComplexBooleanValue( "Reservation not found!" );
	}

	public IValidationResult insertNewBook(String[] param, String[] value, User u) {
		/**
		 * Insert a new Book, if already exists update stat ( quantity + 1)
		 * @return IValidationResult true / false + message 
		 */		
		try {
			if ( ! u.getRole().equals("Admin")) {
				return new ComplexBooleanValue( "Premission error! You're not an admin!" );
			}
			Document doc = readFile( xml_file );
			LinkedList<Book> temp_b = searchBook(param, value);
			if ( temp_b.isEmpty() ) {
				
				int new_id = getNewIdBook();
				String [] temp = {"BookId", "Quantity"};
				param = Stream.concat(Arrays.stream(temp), Arrays.stream(param)).toArray(String[]::new);
				
				temp[0] = Integer.toString( new_id );
				temp[1] = "1";
				value = Stream.concat(Arrays.stream(temp), Arrays.stream(value)).toArray(String[]::new);
				
				doc = insertElement( doc, "Book", param, value);
				writeFile( doc, xml_file );
				return new ComplexBooleanValue( true );
			} else if( !temp_b.isEmpty() && temp_b.size() <= 1) {
				Book book_temp = temp_b.getFirst();
				book_temp.setQuantity( book_temp.getQuantity() + 1 );
				return updateBook ( book_temp, u );
			}
			
		} catch ( AccessControlException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new ComplexBooleanValue( true );
	}

	public IValidationResult updateBook(Book b, User u) {
		/**
		 * Update data of Book
		 * @return boolean 
		 */		
		try {
			if ( ! u.getRole().equals("Admin")) {
				return new ComplexBooleanValue( "Premission error! You're not an admin!" );
			}
			Document doc = readFile( xml_file );
			NodeList books = searchElementGroup( doc, "Book" );
			for(int i = 0; i < books.getLength(); i++) {
				Element book = (Element) books.item(i);
				
				if ( b.getBookId() == Integer.parseInt(book.getAttribute( "BookId" )) ){
					
					book.setAttribute("Title", b.getTitle());
					book.setAttribute("Author", b.getAuthor());
					book.setAttribute("Publisher", b.getPublischingHouse());
					book.setAttribute("Quantity", Integer.toString(b.getQuantity()) );
					writeFile( doc, xml_file );
					return new ComplexBooleanValue( true );
				}
			}	
		} catch ( AccessControlException e) {
			return new ComplexBooleanValue( "Can not access to file." );
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new ComplexBooleanValue( false );
	}

	public IValidationResult deleteBook(Book b, User u) {
		/**
		 * Delete a Book, if is not reserved
		 * @return IValidationResult -> ComplexBooleanValue true / false + message 
		 */		
		if ( ! u.getRole().equals("Admin")) {
			return new ComplexBooleanValue( "Permission denided. You're not Admin!" );
		}
		Document doc = null;
		try {
			doc = readFile( xml_file );
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		LinkedList<Reservation> reservation_book = getActiveReservation(b, null);
		if ( reservation_book.size() >= b.getQuantity() ) {
			String err = "Action delete denided.";
			for( int i = 0; i< reservation_book.size(); i++ ) {
				int user_id_block =  reservation_book.get( i ).getUserId();
				SimpleDateFormat data_format = new SimpleDateFormat("dd/MM/yyyy");
				User user_block = getUserById ( doc, user_id_block );
				err += "Book '" + EditString.Capitalize( b.getTitle() ) + "is reserved by " + EditString.Capitalize( user_block.getUsername() );
				err += "from " + data_format.format( reservation_book.get( i ).getStartDate() ) + " to " + data_format.format( reservation_book.get( i ).getEndDate() );
			}
			return new ComplexBooleanValue( err );	
		}
		int new_quantity = b.getQuantity() - 1;
		if ( new_quantity <= 0 ) {
			return new ComplexBooleanValue("Book do not exists.");
		}
		b.setQuantity( new_quantity );
		return updateBook( b, u );
	}
	
	private LinkedList<Reservation> getActiveReservation( Book b, User u ) {
		
		LinkedList<Reservation> result = new LinkedList<Reservation>();
		Document doc = null;
		try {
			doc = readFile( xml_file );
			NodeList res = searchElementGroup( doc, "Reservation");
			for ( int i = 0; i< res.getLength(); i++) {
				Reservation reservation = createReservationFromData( (Element) res.item(i) );
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date today = formatter.parse(formatter.format( new Date() ));

				if ( reservation.getBookId() == b.getBookId() && reservation.getEndDate().before( today ) ) {
					result.add( reservation );
				}
			}
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private Document readFile( String pathFile ) throws ParserConfigurationException, TransformerException {
		/**
		 * Loads xml file with Library's data
		 * If xml do not exist it creates it and return
		 * @return Document with all data of Library
		 */
		Document doc = null;
		
		if ( checkFileLibraryExist( pathFile ) ) {
			
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder xmlBuilder;
			try {
				xmlBuilder = xmlFactory.newDocumentBuilder();
				doc = xmlBuilder.parse( pathFile );
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			doc = createFile( pathFile );
		}
		
		return doc;
	}
	
	private boolean checkFileLibraryExist(String filePath) {
		/**
		 * Check if xml file exist
		 * @return Boolean true / false
		 */	
		File f = new File(filePath);
		if( f.exists() ) { 
		    return true;
		}
		return false;
	}
	
	private Document createFile(String PathFile) throws ParserConfigurationException, TransformerException {
		/**
		 * Create file xml and return
		 * @return Document xml read
		 */	
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element library = doc.createElement("Library");
		
		Element users = doc.createElement("Users");
		Element books = doc.createElement("Books");
		Element reservations = doc.createElement("Reservations");
		
		library.appendChild(users);
		library.appendChild(books);
		library.appendChild(reservations);
		
		doc.appendChild(library);
		
		writeFile( doc, PathFile );
		
		return doc;
	}
	
	private void writeFile( Document doc, String pathFile ) throws TransformerException {
		/**
		 * Save document to xml file
		 * @return 
		 */
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		DOMSource source = new DOMSource(doc);	
		StreamResult result = new StreamResult(new File(pathFile));

		transformer.transform(source, result);

		//System.out.println("File saved!");
	}
	
	private NodeList searchElementGroup(Document d, String type) {
		/**
		 * Search element by tag (type = Users/Books/Reservations) xml
		 * @return NodeList with that tag 
		 */
		NodeList list = d.getElementsByTagName( type );
		
		return list;
	}
	
	private Element createElement(Document d, String type, String[] param, String[] value ) {
		/**
		 * Create a new element with attributes
		 * @return Element to be insert in the document
		 */
		Element e = d.createElement( type );
		
		if( param.length > 0) {
			for(int i=0; i < param.length; i++) {
				e.setAttribute( param[i],  value[i] );
			}
		}
		return e;
	}
	
	private Document insertElement(Document d, String type, String[] param, String[] value) {
		/**
		 * Insert new Element inside right group
		 * @return Document to be saved
		 */
		String FatherType = null;
		
		switch (type) {
			case "User": FatherType = "Users";
				break;
			case "Book": FatherType = "Books";
				break;
			case "Reservation": FatherType = "Reservations";
				break;
		}
		
		NodeList elementListFather = searchElementGroup(d, FatherType);
		
		Element elementFather = (Element) elementListFather.item(0);
		
		Element new_el = createElement( d, type, param, value );
		
		elementFather.appendChild(new_el);
		
		return d;
	}
	
	private User createUserFromData(Element e) {
		/**
		 * Create new User from data extracted from xml
		 * @return User
		 */	
		User temp = new User();
		String temp_username = e.getAttribute("Username");
		String temp_pass = e.getAttribute("Password");
		String temp_role = e.getAttribute("Role");
		int temp_id = Integer.parseInt(e.getAttribute("UserId"));
		
		temp.setUserId(temp_id);
		temp.setUsername(temp_username);
		temp.setRole(temp_role);
		temp.setPassword(temp_pass);
		
		return temp;
	}
	
	private Book createBookFromData(Element e) {
		/**
		 * Create new Book from data extracted from xml
		 * @return Book
		 */		
		Book book_found = new Book();
		
		int book_id = Integer.parseInt(e.getAttribute("BookId"));
		String book_title = e.getAttribute("Title");
		String book_author = e.getAttribute("Author");
		String book_publisher = e.getAttribute("Publisher");
		int book_quantity = Integer.parseInt(e.getAttribute("Quantity"));
		
		book_found.setBookId( book_id );
		book_found.setAuthor( book_author );
		book_found.setTitle( book_title );
		book_found.setPublischingHouse( book_publisher );
		book_found.setQuantity( book_quantity );
		
		return book_found;
		
	}
	
	private Reservation createReservationFromData ( Element e ) throws ParseException {
		/**
		 * Create new Reservation from data extracted from xml
		 * @return Reservation
		 */		
		Reservation reservation_found = new Reservation();
		int resevation_id = Integer.parseInt(e.getAttribute("ReservationId"));
		int book_id = Integer.parseInt(e.getAttribute("BookId"));
		int user_id = Integer.parseInt(e.getAttribute("UserId"));
		
		SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");

		Date reservation_data_start = date_format.parse(e.getAttribute("StartDate"));
		Date reservation_data_end = date_format.parse(e.getAttribute("EndDate"));
		
		reservation_found.setReservationId(resevation_id);
		reservation_found.setBookId(book_id);
		reservation_found.setUserId(user_id);
		reservation_found.setStartDate(reservation_data_start);
		reservation_found.setEndDate(reservation_data_end);
		
		return reservation_found;
	}

	public LinkedList<Book> getBooks() {
		
		LinkedList<Book> books = new LinkedList<Book>();
		try {
			Document doc = readFile( xml_file );
			NodeList books_list = searchElementGroup( doc, "Book");
			for (int i = 0; i < books_list.getLength(); i++ ) {
				Book book_temp = createBookFromData ( (Element) books_list.item( i ) );
				books.add(book_temp);
			}			
			
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return books;
	}

	public LinkedList<User> getUsers() {
		LinkedList<User> users = new LinkedList<User>();
		try {
			Document doc = readFile( xml_file );
			NodeList users_list = searchElementGroup( doc, "User");
			for (int i = 0; i < users_list.getLength(); i++ ) {
				User user_temp = createUserFromData ( (Element) users_list.item( i ) );
				users.add(user_temp);
			}			
			
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	public LinkedList<Reservation> getReservations() {
		LinkedList<Reservation> reservations = new LinkedList<Reservation>();
		try {
			Document doc = readFile( xml_file );
			NodeList reservations_list = searchElementGroup( doc, "Reservation");
			for (int i = 0; i < reservations_list.getLength(); i++ ) {
				Reservation user_temp = createReservationFromData ( (Element) reservations_list.item( i ) );
				reservations.add(user_temp);
			}			
			
		} catch (ParserConfigurationException | TransformerException | ParseException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return reservations;
	}
	
	public void saveAllData(LinkedList<User> Users, LinkedList<Book> Books, LinkedList<Reservation> Reservations) {
		
		try {
			Document doc = createFile( xml_file );
			int i;
			for( i = 0; i < Users.size()-1; i++ ) {
				
				User ut = Users.get( i );
				String [] param = {"UserId","Role","Username","Password"};
				String [] value = {Integer.toString(ut.getUserId()),ut.getRole(), ut.getUsername(),ut.getPassword() };
				
				insertElement( doc, "User", param, value);								
			}
			
			for(i = 0; i < Books.size()-1; i++) {
				
				Book bt = Books.get( i );
				String [] param = {"BookId","Title","Author","Publischer","Quantity"};
				String [] value = {Integer.toString(bt.getBookId()),bt.getTitle(), bt.getAuthor(),bt.getPublischingHouse(),Integer.toString(bt.getQuantity()) };
				
				insertElement( doc, "Book", param, value);								
			}
			
			for(i = 0; i < Reservations.size()-1; i++) {
				
				Reservation rt = Reservations.get( i );
				String [] param = {"ReservationId","BookId","UserId","StartDate","EndDate"};
				
				DateFormat date_to_string = new SimpleDateFormat("dd/MM/yyyy");
				String startDate =  date_to_string.format(rt.getStartDate());
				String endDate = date_to_string.format(rt.getEndDate());
				
				
				String [] value = {Integer.toString(rt.getReservationId()),Integer.toString(rt.getBookId()), Integer.toString(rt.getUserId()),startDate,endDate };
				
				insertElement( doc, "Reservation", param, value);								
			}
			
			writeFile( doc, xml_file );
			
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private int getNewIdBook() {
		int id_max = 0;
		try {
			Document doc = readFile( xml_file );
			NodeList books_list = searchElementGroup( doc, "Book");
			for (int i = 0; i < books_list.getLength(); i++ ) {
				String book_id =  ((Element)books_list.item( i )).getAttribute("BookId");
				if ( id_max < Integer.parseInt(book_id)) {
					id_max =  Integer.parseInt(book_id);
				}
			}			
			
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return id_max + 1;
	}
	
	private int getNewIdReservation() {
		int id_max = 0;
		try {
			Document doc = readFile( xml_file );
			NodeList reservations_list = searchElementGroup( doc, "Reservation");
			for (int i = 0; i < reservations_list.getLength(); i++ ) {
				String reservation_id =  ((Element)reservations_list.item( i )).getAttribute("ReservationId");
				if ( id_max < Integer.parseInt(reservation_id)) {
					id_max =  Integer.parseInt(reservation_id);
				}
			}			
			
		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return id_max + 1;
	}
	
	private int getNewIdUser() {
		int id_max = 0;
		try {
			Document doc = readFile(xml_file);
			NodeList users_list = searchElementGroup(doc, "User");
			for (int i = 0; i < users_list.getLength(); i++) {
				String user_id = ((Element) users_list.item(i)).getAttribute("UserId");
				System.out.println(
						"id: " + user_id + " username: " + ((Element) users_list.item(i)).getAttribute("Username"));
				if (id_max < Integer.parseInt(user_id)) {
					id_max = Integer.parseInt(user_id);
				}
			}

		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return id_max + 1;
	}
	
	private User getUserById ( Document doc, int id) {
		/**
		 * Serch user by id
		 * @return User
		 */
		User u = null;
		NodeList users = searchElementGroup ( doc, "User" ); 
		for (int i = 0; i < users.getLength(); i++ ) {
			u = createUserFromData ( (Element) users.item( i ) );
			if ( u.getUserId() == id ) {
				return u;
			}
		}			
		return u;		
	}

	@Override
	public LinkedList<Reservation> searchReservationOfUser(Book b, User u) {
		// TODO @Andrea
		return null;
	}

	private LinkedList<User> searchUser(String[] param, String[] value) {
		/**
		 * Looks for a User
		 * @return LinkedList of user
		 */
		Document doc;
		LinkedList<User> result = new LinkedList<User>();
		try {
			doc = readFile(xml_file);
			doc.getDocumentElement().normalize();
			NodeList users = searchElementGroup(doc, "User");
			for (int i = 0; i < users.getLength(); i++) {
				Element user_list = (Element) users.item(i);
				boolean found = true;
				for (int j = 0; j < param.length; j++) {
					String temp_param = param[j];
					String temp_value = value[j];

					if (!user_list.getAttribute( temp_param ).equals( temp_value )) {
						found = false;
						break;
					}
				}
				if (found) {
					User user_found = createUserFromData( user_list );
					result.add( user_found );
				}
			}
		} catch (ParserConfigurationException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public IValidationResult insertNewUser(User u) {
		/**
		 * Insert new user
		 * @return IValidationResult true / false + message
		 */
		try {
			Document doc = readFile(xml_file);

			String[] param = { "Username" };
			String[] value = { u.getUsername() };

			LinkedList<User> temp_user = searchUser(param, value);

			if (temp_user.isEmpty()) {

				int new_id = getNewIdUser();

				String[] new_param = { "Password", "Role", "UserId", "Username" };
				String[] new_value = { u.getPassword(), u.getRole(), String.valueOf(new_id), u.getUsername() };

				doc = insertElement(doc, "User", new_param, new_value);
				writeFile(doc, xml_file);
				return new ComplexBooleanValue(true);
			} else if (!temp_user.isEmpty()) {
				return new ComplexBooleanValue("Creation new user failed. Username already exists!");
			}

		} catch (ParserConfigurationException | TransformerException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return new ComplexBooleanValue(true);
	}
	
	

}
