import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.lang.Thread;

public class Project2Client {
	public static File directory;
	public static String input1 = "0123456789ABCDEF";
	public static String input2 = "FEDCBA9876543210";
	public static String input3 = "default";
	public static String inputA = "default";
	public static String inputB = "default";
	public static String inputC = "default";
	public static int hello = -1;
	public static int close = 0;
	 
	public static String salutation = "210 I'm ready to ignore you!\n";
	public static String defaultReply = "Message received and ignored.\n";
	public static String message = "Place-Holder";
	public static int portNum = 40000;
	public static PrintStream outputServer;
	public static BufferedReader reader;
	public static InputStreamReader inStream;
	//public static ServerSocket serveSock;
	public static Socket sock;
	public static BufferedReader stdIn =  new BufferedReader(new InputStreamReader(System.in));
	public static boolean quit;
	
	
	public static void main(String[] args){
		welcome();
		connect();
		readersAndStreams();
		
		while(quit == false){
			commands();
		}
		
	}//end main
	
	public static void connect(){
		try{
		InetAddress serverName = InetAddress.getByName(input1);
		sock = new Socket(serverName, portNum);}
		catch(Exception e1){System.out.println("Failed to connect to server; printing stacktrace and closing program.");
		e1.printStackTrace();
		System.exit(1);}
		}//end connect
	
	public static void readersAndStreams(){
		try{
		reader = new BufferedReader(inStream = new InputStreamReader(sock.getInputStream()));
		outputServer = new PrintStream(sock.getOutputStream());}
		catch(Exception e2){System.out.println("Failed to create input and output streams; printing stacktrace:");
		e2.printStackTrace();}
	}//end readersAndStreams
	
	public static void welcome(){
		quit = false;
		System.out.println("Welcome to the simple command Server!\nPlease input the servers name.");
		try{
		input1 = stdIn.readLine();}
		catch(Exception e3){System.out.println("Failed to read server name from stdin; printing stacktrace:");
		e3.printStackTrace();}
	}//end welcome
	
	public static void commands(){
		String[] parsed;
		System.out.println("Please enter a command; enter HELP for a list of commands");
		try{
		input1 = stdIn.readLine();}
		catch(Exception e4){System.out.println("Failed to read command from stdin; printing stacktrace:");
		e4.printStackTrace();}
		
		parsed = input1.split("\\s+");
		input1 = parsed[0];
		if(parsed.length > 1){input2 = parsed[1];}
		else{input2 = null;}
		if(parsed.length > 2){input3 = parsed[2];}
		else{input3 = null;}
		
		if(input1.equals("HELP")==true){help();}
		if(input1.equals("HELO")==true){helo();}
		if(input1.equals("QUIT")==true){quit();}
		if(input1.equals("GET")==true){get();}
		if(input1.equals("PUT")==true){put();}
		if(input1.equals("CD")==true){cd();}
		if(input1.equals("PWD")==true){pwd();}
		if(input1.equals("LS")==true){ls();}
	}//end commands
	
	public static void help(){
		System.out.println("This program communicates with a server to exchange files and directory information.");
		System.out.println("There are 8 commands including HELP.");
		System.out.println("HELO starts the process and may be followed by the name of the client");
		System.out.println("CD changes the present working directory of the server.");
		System.out.println("CD is followed by a directory name to change to.");
		System.out.println("PWD displays the name of the present working directory of the server.");
		System.out.println("PWD is not followed by a parameter.");
		System.out.println("LS lists the contents of the present working directory of the server.");
		System.out.println("LS is not followed by a parameter.");
		System.out.println("PUT copies a file from the client into the present working directory of the server");
		System.out.println("PUT is followed by the name of the file that is to by copied followed by the path to it on the client.");
		System.out.println("GET copies a file from the server into the present working directory of the client");
		System.out.println("GET is followed by the name of the file that is to be copied followed by the path to create it in on the client, including the filename.");
		System.out.println("QUIT terminates the connection with the server; this command isn't followed by a parameter.");
	}//end help()
	
	public static void cd(){
		outputServer.println("CD " + input2);
		outputServer.flush( );
		
		//flush();
		return;
	}//end cd()
	
	public static void pwd(){
		String temp = "\0"; 
		outputServer.println("PWD");
		outputServer.flush( );
		try{temp = reader.readLine();}
		catch(Exception e5){
			System.out.println("Failed to read from server; printing stacktrace:");
			e5.printStackTrace();}
		System.out.println(temp);
		
		//flush();
		return;
	}//end pwd()
	
	public static void ls(){
		String temp = "\0";
		outputServer.println("LS");
		outputServer.flush( );
		try{while((temp = reader.readLine()) != null){
			System.out.println(temp);
		}}
		catch(Exception e6){
			System.out.println("Failed to read from server; printing stacktrace:");
			e6.printStackTrace();
		}
		//flush();
		return;
	}//end ls()
	
	public static void get(){
		
		//System.out.println("start get");
		
		String temp = " \0";
		File f;
		char c;
		int in = 0;
		String hex = "";
		String filePath;
		byte[] bytes;
		
		outputServer.println("GET " + input2);
		outputServer.flush( );
		
		//System.out.println("Get " + input2 +", sent to server");

		try{temp = reader.readLine();}
		catch(Exception e8){
			System.out.println("Failed to read from server; printing stacktrace:");
			e8.printStackTrace();}
		System.out.println(temp);
		
		try{
			while((in = reader.read()) != -1){
				c = (char) in;
				System.out.println(c);
				hex += c;}}	
		
		catch(IOException io){
			System.out.println("IO exception; printing stackTrace:");
			io.printStackTrace();
			return;}//end catch

		bytes = hexStringToByteArray(hex);
		
		f = new File(input3);
		
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
		
		//flush();
		return;
		
	}//end get()
	
	public static void put(){
		String temp = " \0";
		Path p;
		File f;
		byte[] bytes;
		String hex = "";
		
		f = new File(input3);
		p = Paths.get(input3);
		
		try{
			bytes = Files.readAllBytes(p);
			hex = bytesToHex(bytes);}
		catch(IOException fileException3){
			System.out.println("failed to read file; printing stackTrace:");
			fileException3.printStackTrace();}
		
		outputServer.println("PUT " + input2 + " " + (hex.length()/2));
		outputServer.flush( );
		try{temp = reader.readLine();}
		catch(Exception e8){
			System.out.println("Failed to read from server; printing stacktrace:");
			e8.printStackTrace();}
		System.out.println(temp);
		outputServer.println(hex);
		//flush();
	}//end put()
	
	public static void helo(){
		String temp = "\0";
		if(input2 != null){
			outputServer.println("HELO " + input2);}
		else{outputServer.println("HELO");}
		outputServer.flush( );
		try{temp = reader.readLine();}
		catch(Exception e7){
			System.out.println("Failed to read from server; printing stacktrace:");
			e7.printStackTrace();}
		System.out.println(temp);
		//flush();
	}//end helo()
	
	public static void quit(){

		quit = true;
		outputServer.println("QUIT");
		outputServer.flush( );
		System.exit(0);
	}//end quit()
	
	public static void readAndParse(){
		String[] parsed;
		try{inputA = reader.readLine();}
		catch(Exception e4){
			System.out.println("Failed to read from server; printing stacktrace:");
			e4.printStackTrace();}
		
		parsed = input1.split("\\s+");
		inputA = parsed[0];
		if(parsed.length > 1){inputB = parsed[1];}
		else{input2 = null;}
		if(parsed.length > 2){inputC = parsed[2];}
		else{input3 = null;}
	}//end parse()
	
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
	
	public static void flush(){
		try{Thread.sleep(1);
			while((reader.read()) != -1){
				}}
		catch(Exception e){System.out.println("failed to wait while flushing server's output");
			e.printStackTrace();}
	}//end flush();
	
}//end class
