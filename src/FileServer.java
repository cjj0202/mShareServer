import java.io.*;
import java.net.*;

public class FileServer {

	public static String receiverName;
	public static String filePath;
	public static Socket sock;
	
	public static void main(String[] args) throws IOException {
			
		//Run ReceiveSocket, then let ReceiveSocket to call SendSocket to transfer file
		ServerReceivefileActivity();
		
		System.out.println(receiverName);
		System.out.println(filePath);
			
	}
	
	private static void ServerReceivefileActivity() throws IOException {
		
        int filesize=9999999; 
        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;
        
        ServerSocket serverSock = new ServerSocket(1149);  //Listen on 1149 for Android client
        while (true) {
          System.out.println("Waiting for Android Client");

          Socket sock = serverSock.accept();
          System.out.println("New connection: " + sock);

            //Receive Socket
            byte [] byteArray  = new byte [filesize];
            InputStream inStream = sock.getInputStream();
            DataInputStream clientData = new DataInputStream(inStream);
            String fileName = clientData.readUTF();       //Read Filename
            receiverName = fileName.substring(0, 3);      //Get the ReceiverName from Filename
            System.out.println("Receiver:"+ receiverName);
            System.out.println("Filename" + fileName);
            filePath = "D:\\Server\\" + fileName;
            FileOutputStream fos = new FileOutputStream("D:\\Server\\"+fileName); //File Path
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = inStream.read(byteArray,0,byteArray.length);
            current = bytesRead;

            do {
               bytesRead = inStream.read(byteArray, current, (byteArray.length-current));
               if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(byteArray, 0 , current);
            bos.flush();
            long end = System.currentTimeMillis();
            System.out.println(end-start);
            bos.close();
            sock.close();
            
            //Call SendSocket with ReceiverName(Translating to IP) and File path
            ServerSendfileActivity(NameToIP(receiverName),filePath);
          }
	}

	private static void ServerSendfileActivity(String name,String path) {
		
        try {
            sock = new Socket(name, 1150);    //Use 1150 to transfer
            System.out.println("Connecting to Android Client");

            File myFile = new File (path); 
            byte [] byteArray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(byteArray,0,byteArray.length);
            OutputStream outStream = sock.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outStream);
            dos.writeUTF(myFile.getName());
            System.out.println("Sending...");
            outStream.write(byteArray,0,byteArray.length);
            outStream.flush();
            sock.close();
            
            //Call ReceiveSocket to listen connection
            ServerReceivefileActivity();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
      }

	private static String NameToIP(String receiverName2) {
		// TODO Auto-generated method stub
		// Currently using static map for Receiver Name and client IP address
	String ip1 = "192.168.43.114";
	String ip2 = "192.168.43.181";
	
	switch (receiverName2){
		case "CJJ": return ip1;
		case "CHH": return ip2;
	}
		return null;
	}

}