import java.io.*;
import java.nio.file.*;
import java.net.*;




public class Project2Server {
	 public static File directory;
	 public static String input1 = "0123456789ABCDEF";
	 public static String input2 = "FEDCBA9876543210";
	 public static String input3 = "default";
	 public static int hello = -1;
	 public static int close = 0;
	 
	 public static String salutation = "210 I'm ready to ignore you!\n";
	 public static 	String defaultReply = "Message received and ignored.\n";
	 public static 	String message = "Place-Holder";
	 public static 	int portNum = 40000;
	 public static PrintStream output;
	 public static BufferedReader reader;
	 public static ServerSocket serveSock;
	 public static Socket sock;


	public static void main(String[] args) {
	    //File directory;
		//String input1 = "default";
		//String[] parsed;
		//BufferedReader standardInReader;
		//standardInReader = new BufferedReader(new InputStreamReader(System.in));
		
		createConnection();
		openConnection();
		
		while(true){
			

			if(inputOutputSetup()==false){
				if(close == 99){break;}
				continue;}
			
			close = 0;
			hello = 0;
			
			while((close != 1) && (close != 99)){
				try{
					acceptAndParse();
					commandStructure();}
				catch(Exception e){
					System.out.println("failed to interpret input; printing stacktrace and closing connection.");
					e.printStackTrace();
					closeConnection();
					break;
				}//end catch
			}//end while close != 1
			closeConnection();
			openConnection();
			
			if(close == 99){continue;}
			
		}//end while true	
		
		
		

	

}//end main()
	
	public static void start(){
		
		return;
	}
	
	public static boolean inputOutputSetup(){
		try{
			output =  new PrintStream(sock.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			}//end try
			catch(Exception IOe1){
				System.out.print("Failed to instantiate PrintStream or BufferedReader. Printing stack trace.\n");
				IOe1.printStackTrace();
				closeConnection();
				return false;
			}//end catch
		return true;
	}//end intputOutputSetup()
	
	public static void commandStructure(){
		
		if(input1.equals("HELO")==true){
			hello = 1;
			if(input2 != null){
			output.println("HELO " + input2);
			return;}
			else{
				output.println("HELO");
				return;}
		}//end input == 1
		
		if(hello != 1){
			output.println("400 Must enter HELO first");
			return;}//end if didn't HELO
		
		if(input1.equals("LS")==true){listDir();return;}
		if(input1.equals("PWD")==true){presentWorkingDir();return;}
		if(input1.equals("CD")==true){changeDir();return;}
		if(input1.equals("GET")==true){get();return;}
		if(input1.equals("PUT")==true){put();return;}
		if(input1.equals("QUIT")==true){quit();return;}
		//if(input1.equals("EXIT")==true){sever();}
		output.println("404 Invalid command");
		
	}//end commandStructure()
	
	public static void sever(){
		try{
		sock.close();
		serveSock.close();}
		catch(Exception e){;}
		close = 99;
		
		return;
	}
	
	public static void acceptAndParse(){
		String[] parsed;
		//BufferedReader standardInReader;
		//standardInReader = new BufferedReader(new InputStreamReader(System.in));
		
		try{//output.println("Please enter a command.");
			//standardInReader = new BufferedReader(new InputStreamReader(System.in));
			input1 = reader.readLine();	 
		}//end try
	
		catch(IOException io){
			io.printStackTrace();
			close = 1;
			hello = 0;
		}//end catch
	
		parsed = input1.split("\\s+");
		input1 = parsed[0];
		if(parsed.length > 1){input2 = parsed[1];}
		else{input2 = null;}
		if(parsed.length > 2){input3 = parsed[2];}
		else{input3 = null;}
		}//end acceptAndParse
	
	public static void listDir(){
		String[] a;
		int i;

		
		if(directory == null){
			output.println("No directory selected");
			return;}
		
		a = directory.list();
		i = 0;
		while(a.length > i){
		output.println(a[i]);
		i++;	
		}//end loop through a[]
	}//end listDir()
	
	public static void presentWorkingDir(){
		

		
		if(directory == null){
			output.println("No directory selected");
			return;}
		output.println(directory.getAbsolutePath());
	}//end presentWorkingDir();
	
	public static void changeDir(){
		
		
		String filePath = null;
		String filePath2 = null;
		if(input2 == null){
		output.println("no directory specified");	
		return;}
		
		input2 = "\\" + input2 + "\\";
		
		if(directory == null){
			directory = new File(input2);
			if(directory.exists() == false){
				output.println("Specified directory doesn't exist.");
				directory = null;
			}//end if new directory doesn't exist
			return;}//end if directory was null
		
		filePath = directory.getAbsolutePath();//user didn't specify a full path try as relative path

		
		directory = new File(input2);
		if(directory.exists() == false){//if user didn't specify a full path the directory won't exist
			directory = null;}
		else{return;}//if user specified a full path the directory will exist
		
		filePath2 = (filePath + input2);
		directory = new File(filePath2);
		if(directory.exists()== false){directory = new File(filePath);
		output.println("Specified directory doesn't exist.");}
		
		
	}//end changeDir()
	
	/*I lifted this method from user maybeWeCouldStealAVan on StackOverflow;
	 * he posted it in the following thread: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();//Take a string of all hex digits and turn it into a char[]
	    char[] hexChars = new char[bytes.length * 2];//create a new character[] with twice as many elements as the byte[]
	    for ( int j = 0; j < bytes.length; j++ ) {//loop through byte[]
	        int v = bytes[j] & 0xFF;//bitwise and byte with the 8 least significant bits set to one to copy byte into int
	        hexChars[j * 2] = hexArray[v >>> 4];//set the 2nd half of the byte to the corresponding hex digit from the char[] of hex digits
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];//set the 1st half of the byte
	    }
	    return new String(hexChars);//return the char[] of hex digits as a String
	}//end bytesToHex()
	
	/*I lifted this method from user Dave L on StackOverflow
	 * he posted it in the following thread: http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java/140861#140861
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];//byte[] is half the length as each byte holds two chars
	    for (int i = 0; i < len; i += 2) {//loop through s 2 characters at a time
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) //set 1st half of byte
	                             + Character.digit(s.charAt(i+1), 16));//set 2nd half of byte
	    }
	    return data;
	}//end hexStringToByteArray()
	
	public static void put(){
		File f;
		char c;
		int in = 0;
		String hex = "";
		String filePath;
		byte[] bytes;
		//BufferedReader standardInReader;
		//standardInReader = new BufferedReader(new InputStreamReader(System.in));
		
		try{
		while((in = reader.read()) != -1){
			c = (char) in;
			hex += c;}}
		
		catch(IOException io){
			System.out.println("IO exception; printing stackTrace:");
			io.printStackTrace();
			return;}//end catch
		
		bytes = hexStringToByteArray(hex);
		
		filePath = directory.getAbsolutePath();
		filePath = (filePath + "\\" + input2);
		f = new File(filePath);
		
		try{
		f.createNewFile();}
		catch(IOException fileException1){
			System.out.println("Failed to create new file; printing stackTrace");
			fileException1.printStackTrace();}
		
		try{
			FileOutputStream stream = new FileOutputStream(f);
			stream.write(bytes);
			stream.close();}
		catch(IOException fileException2){
			System.out.println("File not found; printing stackTrace");
			fileException2.printStackTrace();}
		
		return;
	}//end get()

	public static void get(){
		Path p;
		File f;
		byte[] bytes;
		String hex = "";
		String filePath;
		
		filePath = directory.getAbsolutePath();
		filePath = (filePath + "\\" + input2);
		f = new File(filePath);
		f = new File(filePath);
		p = Paths.get(filePath);
		
		
		//System.out.println(directory.getAbsolutePath());
		try{
			bytes = Files.readAllBytes(p);
			hex = bytesToHex(bytes);}
		catch(IOException fileException3){
			System.out.println("failed to read file; printing stackTrace:");
			fileException3.printStackTrace();}
		output.println("204 content length: " + (hex.length()/2));
		output.println(hex);
		
		return;
	}//end put()
	
	public static void quit(){
		hello = 0;
		close = 1;
		return;
	}//end quit()
	
	public static void createConnection(){
		try{
			serveSock = new ServerSocket(portNum); 
		}//end try
		catch(Exception eSocketSet){
			System.out.print("Connection failed. Printing stack trace.\n");
			eSocketSet.printStackTrace();
		}//end catch
	}//end createConnection()
	
	public static void openConnection(){
		try{
			sock = serveSock.accept();
		}//end try
		catch(Exception eOpen){
			System.out.print("Connection failed to open. Printing stacktrace.\n");
			eOpen.printStackTrace();
		}//end catch
		
		return;
	}//end openConnection()
	
	public static void closeConnection(){
		try{
			sock.close();
		}//end try
		catch(Exception eClose){
			System.out.print("Connection failed to close. Printing stacktrace.\n");
			eClose.printStackTrace();
		}//end catch
		
		return;
	}//end closeConnection()
	
}//end class 


