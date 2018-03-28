package services;

import java.io.File;

import javax.xml.ws.Endpoint;


//Endpoint publisher
public class ServerLibraryPublisher {

	public static void main(String[] args) {
		
		String path_xml = System.getProperty("user.dir");
		String name_file_xml = "Library.xml";
		String file_xml = path_xml + File.separator + name_file_xml;
		Endpoint.publish("http://localhost:7779/ws/library", new ServerLibraryImpl( file_xml ));
  }

}
