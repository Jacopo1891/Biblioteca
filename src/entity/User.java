package entity;

//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement
public class User {

	//@XmlAttribute
	private int userId;	
	//@XmlAttribute
	private String username;
	//@XmlAttribute
	private String password;
	//@XmlAttribute
	private String role; 
	
	public User () {
		
	}
	
	public User(int id, String u, String p, String r ) {
		setUserId( id );
		setUsername( u );
		setPassword( p );
		setRole( r );
	}
	
	public User(String u, String p, String r ) {
		setUsername( u );
		setPassword( p );
		setRole( r );
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
