package gest_dati;

import java.io.File;
import java.io.IOException;

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

import entity.Book;
import entity.Reservation;
import entity.User;

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
		try {
			doc = readFile(xml_file);
			doc.getDocumentElement().normalize();
			NodeList users = searchElement( doc , "User");
			for(int i = 0; i < users.getLength(); i++) {
				Element list_user = (Element) users.item(i);
				if( list_user.getAttribute("Username").equals( user )  && list_user.getAttribute("Password").equals(pass) ) {

					User user_logged = createUserFromData( list_user );
					
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
		return null;
	}

	public NodeList getBooksAvailable() {
		// TODO Auto-generated method stub
		return null;
	}

	public Book searchBook(String[] param, String[] value) {
		/**
		 * Looks for a book
		 * @return Book if found / Null
		 */	
			Document doc;
			try {
				doc = readFile(xml_file);
				doc.getDocumentElement().normalize();
				NodeList books = searchElement( doc , "Book");
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
						
						return book_found;
					}
				}
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	public boolean insertNewBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteBooking(Reservation r) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean insertNewBook(Book b, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateBook(Book b, User u) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteBook(Book b, User u) {
		// TODO Auto-generated method stub
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
	
	public NodeList searchElement(Document d, String type) {
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
		
		NodeList elementListFather = searchElement(d, FatherType);
		
		Element elementFather = (Element) elementListFather.item(0);
		
		Element new_el = createElement( d, type, param, value );
		
		elementFather.appendChild(new_el);
		
		return d;
	}
	
	private User createUserFromData(Element e) {
		
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
	

}
