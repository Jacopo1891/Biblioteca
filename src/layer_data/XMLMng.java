package layer_data;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import javax.security.auth.login.FailedLoginException;
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

import java.util.ArrayList;
import java.util.Arrays;

import entity.*;

public class XMLMng implements DataMng {
	
	private String xml_file;
	
	public XMLMng( String path_file ) {
		xml_file = path_file;
	}
	
	public User checkLoginData(String user, String pass) {
	/**
	 * Check if login data are correct
	 * @return True / False
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user_logged;
	}

	public LinkedList<Book> getBooksAvailable() {
		// TODO Auto-generated method stub
		return null;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

	public boolean insertNewBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean insertNewBook(String[] param, String[] value, User u) {
		/**
		 * Insert a new Book, if already exists update stat ( quantity + 1)
		 * @return boolean 
		 */		
		try {
			if ( ! u.getRole().equals("Admin")) {
				throw new AccessControlException("Permission denided. You're not Admin!");
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
				return true;
			} else if( !temp_b.isEmpty() && temp_b.size() <= 1) {
				Book book_temp = temp_b.getFirst();
				book_temp.setQuantity( book_temp.getQuantity() + 1 );
				return updateBook ( book_temp, u );
			}
			
		} catch ( AccessControlException e) {
			return false;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateBook(Book b, User u) {
		/**
		 * Update data of Book
		 * @return boolean 
		 */		
		try {
			if ( ! u.getRole().equals("Admin")) {
				throw new AccessControlException("Permission denided. You're not Admin!");
			}
			Document doc = readFile( xml_file );
			NodeList books = searchElementGroup( doc, "Book" );
			for(int i = 0; i < books.getLength(); i++) {
				Element book = (Element) books.item(i);
				
				if ( b.getBookId() == Integer.parseInt(book.getAttribute( "BookId" )) );{
					
					book.setAttribute("Title", b.getTitle());
					book.setAttribute("Author", b.getAuthor());
					book.setAttribute("Publisher", b.getPublischingHouse());
					book.setAttribute("Quantity", Integer.toString(b.getQuantity()) );
					writeFile( doc, xml_file );
					return true;
				}
				
			}
			
		} catch ( AccessControlException e) {
			return false;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteBook(Book b, User u) {
		/**
		 * Delete a Book, if is not reserved
		 * @return boolean 
		 */		
		if ( ! u.getRole().equals("Admin")) {
			throw new AccessControlException("Permission denided. You're not Admin!");
		}
		Document doc = null;
		try {
			doc = readFile( xml_file );
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList res = searchElementGroup( doc, "Reservation");
		for ( int i = 0; i< res.getLength(); i++) {
			Element reservation = (Element) res.item(i);
			int id_book_to_delete = Integer.parseInt( reservation.getAttribute("BookId") );
			if ( id_book_to_delete == b.getBookId()) {
				String err = "Action delete denided.";
				int user_id_block = Integer.parseInt( reservation.getAttribute("UserId") );
				User user_block = getUserById ( doc, user_id_block );
				err += "Book '" + b.getTitle() + "is reserved by " + user_block.getUsername();
				err += "from " + reservation.getAttribute("StartDate") + " to " + reservation.getAttribute("EndDate");
				throw new AccessControlException( err );
			}
		}
		return false;
	}
	
	public Document readFile( String pathFile ) throws ParserConfigurationException, TransformerException {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			doc = createFile( pathFile );
		}
		
		return doc;
	}
	
	public boolean checkFileLibraryExist(String filePath) {
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
	
	public Document createFile(String PathFile) throws ParserConfigurationException, TransformerException {
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
	
	public void writeFile( Document doc, String pathFile ) throws TransformerException {
		/**
		 * Save document to xml file
		 * @return 
		 */
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(pathFile);
		DOMSource source = new DOMSource(doc);	
		StreamResult result = new StreamResult(new File(pathFile));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		//System.out.println("File saved!");
	}
	
	public NodeList searchElementGroup(Document d, String type) {
		/**
		 * Search element by tag (type = Users/Books/Reservations) xml
		 * @return NodeList with that tag 
		 */
		NodeList list = d.getElementsByTagName( type );

		//d.getDocumentElement().normalize();
		
		return list;
	}
	
	public Element createElement(Document d, String type, String[] param, String[] value ) {
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
	
	public Document insertElement(Document d, String type, String[] param, String[] value) {
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
		reservation_found.setUserIdr(user_id);
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
				
				
				String [] value = {Integer.toString(rt.getReservationId()),Integer.toString(rt.getBookId()), Integer.toString(rt.getUserIdr()),startDate,endDate };
				
				insertElement( doc, "Reservation", param, value);								
			}
			
			writeFile( doc, xml_file );
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getNewIdBook() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id_max + 1;
	}
	
	private User getUserById ( Document doc, int id) {
		
		User u = null;
		NodeList users = searchElementGroup ( doc, "User" ); 
		for (int i = 0; i < users.getLength(); i++ ) {
			User user_temp = createUserFromData ( (Element) users.item( i ) );
			if (user_temp.getUserId() == id) {
				return user_temp;
			}
		}			
		
		return null;		
	}
	
	

}
