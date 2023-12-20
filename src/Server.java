
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;



public class Server implements Runnable{
    private String hostName = "127.0.0.1";
    private int portNumber;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    public RSA rsa ;
    public CSV csv ; 
    
    
    public Server(int port , RSA rsa  , CSV csv ){
        this.portNumber = port;
        this.rsa = rsa ;
        this.csv = csv ; 
       
    }
    
    public void run(){
        int message = -1;
       
       
    
        try{
            this.serverSocket = new ServerSocket(this.portNumber); 
        }catch (IOException e) {
            System.out.println("Could not listen on port");
        }
    
      
        while(true){
          
            try{
                    clientSocket = serverSocket.accept();
                    message = clientSocket.getInputStream().read();
                   
            }catch (IOException e) {
                System.out.println("Read failed");
            }
        
     

           

          
           
            if (message != 0) { // 101 : Sending Key to Sender , 102 : Recive Key from Server , 100 : Plain Text Enc Message , 99 : Files .  
               
                String IP = clientSocket.getRemoteSocketAddress().toString();
                String[] arrOfStr = IP.split(":", 2);
                IP = arrOfStr[0].substring(1);

                if (message == 101){ // Code 101 : for Sending Public Key to Sender 
                    try{
                    Client KeyClient = new Client(51510 , IP );
                    KeyClient.SendKey(rsa.publicKey);
                    } catch(Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("ERROR !!!!" + e);
                    }
                        
                    
                    

                }

                  else if (message == 102){             // Code 102 : for reciving RSA Key         
                    


                        byte[] lenb = new byte[4];
                        try {
                            clientSocket.getInputStream().read(lenb,0,4);
                            ByteBuffer bb = ByteBuffer.wrap(lenb);
                            int len = bb.getInt();
                           
                            byte[] servPubKeyBytes = new byte[len];
                            clientSocket.getInputStream().read(servPubKeyBytes);
                            byte[] Decbase64 = Base64.getDecoder().decode(servPubKeyBytes);
                            rsa.setRecPublic(Decbase64);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                           System.out.println("ERROR 1" + e);
                        }


                        
                     }
                    
               
               else if(message == 100 ){ // Plain Text . 
                    try {
                        
                        int len = clientSocket.getInputStream().read();
                        byte[] encmessage = new byte[len];
                        clientSocket.getInputStream().read(encmessage);
                        // Save To history : 
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                        LocalDateTime now = LocalDateTime.now();  
                        csv.editHistory(IP, "ME", "Plain", encmessage , dtf.format(now) , null );

                        System.out.println("From "+ IP +" "+dtf.format(now)+": "+rsa.decrypt(encmessage));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



               } else if(message == 99 ){ // 99 : File ( Image or Video or Document )  
                try {

                
                // Read AES Key 
                    DataInputStream din = new DataInputStream(clientSocket.getInputStream());

                // Read The Length of AES and Enc File 
                int AESLen =din.read();         
                long Filelen = din.readLong();
                // Read AES Key bytes 
                byte[] AESKey = new byte[ AESLen ];
                din.read(AESKey); 
                
                // Read File bytes 
                byte[] File1 = new byte[(int)Filelen];
                for (int i=0 ; i<File1.length ; i++ ){
                    File1[i] = din.readByte();
                }
                String path = din.readUTF() ;
                
               

               

                // Save to history
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();
                csv.editHistory(IP , "ME" , path , File1, dtf.format(now) , AESKey);

                // Decrypt Message : 
                List<byte[]> Details = new ArrayList<byte[]>();
                Details.add(File1);
                Details.add(AESKey);


               
                rsa.DecryptFile(Details , path );

                System.out.println("Done :) " + path);        
                } catch (Exception e) {
                    System.out.println(e);
                }
            
            }else if(message == 103){
System.out.println("103 users");
                try {
                    DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                    int Filelen = din.read();
                    byte[] File = new byte[Filelen];

                    for (int i = 0 ; i < Filelen ; i ++ ){
                        File[i] = din.readByte();

                    }

                    FileOutputStream fos = new FileOutputStream("users.csv"); // 
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(File , 0 , File.length);
                    bos.close();






                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else if (message == 104) {  // Form 

                try {

                    
                    
                    DataInputStream din = new DataInputStream(clientSocket.getInputStream());
                    // int namelen = din.readInt();
                    // byte[] name = new byte[namelen];


                    int Filelen = din.read();
                    byte[] File = new byte[Filelen];

                    for (int i = 0 ; i < Filelen ; i ++ ){
                        File[i] = din.readByte();

                    }
                    // String Name = din.readUTF();
                    // System.out.println(Name);

                    FileOutputStream fos = new FileOutputStream(IP+".csv"); // 
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(File , 0 , File.length);
                    bos.close();








                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
           

            }
           
        }
    
    
    }
    
    protected void Close(){
        try{
            serverSocket.close();
        }catch (IOException e) {
            System.out.println("Could not close socket");
        }
     }}