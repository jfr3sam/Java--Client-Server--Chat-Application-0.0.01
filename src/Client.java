
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;


public class Client {
    private String hostName = "127.0.0.1";
    private int portNumber = 51510;
    private Socket clientSocket = null;
    // private RSA rsa ;
    // private CSV csv ; 

    
    public Client(Socket client){
        this.clientSocket = client;
    }
    
    public Client(int portNumber, String hostName  ){
        this.portNumber = portNumber;
        this.hostName = hostName;
      
    }
    
    public void send(byte[] Encmessage ){ //types : 100 for plain text 
        
     
        try{      
            Socket c =new Socket(this.hostName , this.portNumber);  
        
           c.getOutputStream().write(100); // Code 100 : to Reciver to know that this message is Text message.
           c.getOutputStream().write(Encmessage.length);
           c.getOutputStream().write(Encmessage);
           c.getOutputStream().flush();
           c.close();
           
           
           // SAVE TO HISTROY 
          


            }catch(Exception e){System.out.println(e);}  
            }


    public void sendFile(List<byte[]> FileList  ){

        try {
                Socket c = new Socket(this.hostName , this.portNumber);
                byte[] encFile = FileList.get(0); // encrypted  File 
                byte[] AESKey = FileList.get(1); // AES Key Encrypted With RSA 

                DataOutputStream dos = new DataOutputStream(c.getOutputStream());
                dos.write(99);    // 99 code : is for receiver to know it is a file and not a plain text .. 
                dos.write(AESKey.length);
                dos.writeLong(encFile.length);
                dos.write(AESKey);

                for(int i=0;i<encFile.length;i++){
                    dos.writeByte(encFile[i]);
                }
               String FileName = new String(FileList.get(2)) ;
               dos.writeUTF(FileName); // Send the File Name 
                dos.flush();
                dos.close();
                c.close();
                

                // // Save to History .. 
                // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");   //  Time Format  
                // LocalDateTime now = LocalDateTime.now();                                              // Get Current Time
                // csv.editHistory("ME", this.hostName , FileName, encFile,dtf.format(now)  , AESKey); // Save it to myhis.csv file ( Encrypted )


        }catch(Exception e){System.out.println(e);}
        }
     
public void SendKey(PublicKey publicKey){
        try{      
            Socket cClient = new Socket(this.hostName , this.portNumber);  
            cClient.getOutputStream().write(102); // code 102 : for receiver to know this message is RSA Public Key 
            ByteBuffer bb = ByteBuffer.allocate(4);


            
            byte[] encodded = Base64.getEncoder().encode(publicKey.getEncoded());
            bb.putInt(encodded.length);
            cClient.getOutputStream().write(bb.array());
            cClient.getOutputStream().write(encodded);
            cClient.getOutputStream().flush();
            cClient.close();
           // System.out.println(publicKey);
            }catch(Exception e){System.out.println(e);}  
            }  


public void Give_Me_key(){
    try{      
        Socket c=new Socket(this.hostName , this.portNumber);  
        c.getOutputStream().write(101); // Code 101 : is For receiver to give the sender public key .. 
        c.getOutputStream().flush();
        }catch(Exception e){System.out.println(e);}  
        }  


public void senduers (byte[] File ){
    try {

        Socket c=new Socket(this.hostName , this.portNumber);
        
        DataOutputStream dos = new DataOutputStream(c.getOutputStream());

       dos.write(103); 
       dos.write(File.length);

       for(int i=0;i<File.length;i++){
        dos.writeByte(File[i]);
                                }

                                
                                dos.flush();
                                dos.close();
                                c.close();
                                System.out.println("DONE!");
        }catch(Exception e){System.out.println(e);}  
        }  

    public void sendForm(byte[] File , String FileName ){
        try {

            Socket c=new Socket(this.hostName , this.portNumber);
            
            DataOutputStream dos = new DataOutputStream(c.getOutputStream());
    
           dos.write(104); 
           dos.write(File.length);
    
           for(int i=0;i<File.length;i++){
            dos.writeByte(File[i]);
                                    }

            dos.writeUTF(FileName);
        
            dos.flush();
            dos.close();
            c.close();
            }catch(Exception e){System.out.println(e);}  
            }  

   


    


    public void sendForm( String Name ){
        Socket c;
        try {
            c = new Socket(this.hostName , this.portNumber);
            byte[] name = Name.getBytes();
            DataOutputStream dos = new DataOutputStream(c.getOutputStream());
            
           dos.write(104); // Code 100 : to Reciver to know that this message is Text message.
            dos.write(name.length);
            dos.write(name);
           dos.flush();
           dos.close();
            c.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

    }
}