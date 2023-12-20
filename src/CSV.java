
import java.io.*;
import java.util.*;

public class CSV { 

  private File History ;
  private File PublicKeys ;
  //private File ComingHistory ;
  private File Users ; 

  public CSV() throws Exception{

    File chHistory = new File("myhis.csv");
    File KeysHis = new File("publicKeys.csv");

    if (!chHistory.exists()) { // Genrate new Chat history File . ( myhis.csv)
         try (PrintWriter writer = new PrintWriter("myhis.csv")) {
     
           StringBuilder sb = new StringBuilder();
           sb.append("From");
           sb.append(',');
           sb.append("To");
           sb.append(',');
           sb.append("File-Name");
           sb.append(',');
           sb.append("Messaage");
           sb.append(',');
           sb.append("Time");
           sb.append(',');
           sb.append("AESKey");
           sb.append('\n');
           writer.write(sb.toString());

           
         
         
         }
  } 

  if (!KeysHis.exists()) { // Genrate new IP's Public Key File (publicKeys.csv)
    try (PrintWriter writer = new PrintWriter("publicKeys.csv")) { 

      StringBuilder sb = new StringBuilder();
      sb.append("IP");
      sb.append(',');
      sb.append("Key");
      sb.append('\n');
      writer.write(sb.toString());



    }}


    this.History = chHistory;
    this.PublicKeys = KeysHis ;
  }


  public void createUser() throws FileNotFoundException{
    File usersfile = new File("users.csv");

    if (!usersfile.exists()) {
      System.out.println("Create New User File");
      try (PrintWriter writer = new PrintWriter("users.csv")) { 

        StringBuilder sb = new StringBuilder();
        sb.append("Admin");
        sb.append(',');
        sb.append("192.168.8.101");
        sb.append('\n');
        writer.write(sb.toString());
      }
    }else{
      System.out.println("User Already Exist ");
    }

    this.Users = usersfile ;
  }


  public void EditUser(String name , String ip  , boolean add) throws FileNotFoundException{
    Scanner infile = new Scanner(Users); 

    List<String[]> rows = new ArrayList<String[]>();


  while (infile.hasNext()) {
    String [] l = infile.nextLine().split(",");
    if (!add){
    if (l[0].equals(name) || l[1].equals(ip)){
       continue;  
    }}
    
    rows.add(l);
  }

  try (PrintWriter writer = new PrintWriter(this.Users)) {

    StringBuilder sb = new StringBuilder();
  
  
    for (String[] s : rows){
      for (int i = 0 ; i < s.length; i++){

        sb.append(s[i]);
  
        if (i < s.length - 1){
          sb.append(',');
  
        }
      }
  
      sb.append("\n");
     
    
    }

    if (add){
      sb.append(name);
      sb.append(",");
      sb.append(ip);
      sb.append("\n");
    }
    writer.write(sb.toString());
    writer.close();
  }

  }


  public List<String> getUsers(){
    List<String> users = new ArrayList<String>();
    Scanner infile = null;
       try {
         infile = new Scanner(Users);
       } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
       }
     
         List <String[]> rows = new ArrayList<String[]>();
       
       
       while (infile.hasNext()) {
        String Data = infile.nextLine();
        if (Data != null ){
        String [] l = Data.split(",");
        rows.add(l);}
      }

      for (String[] s : rows){
        if (!s[0].equals("Admin")){
        users.add(s[1]);
      }}

      return users ;
  }

  public List<Object> getListofuser(){
    List<Object> users = new ArrayList<Object>();

    File usersfile=  new File("users.csv"); 

    try {
      usersfile.createNewFile();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
     System.out.println("at CSV LINE 172");
    }

    Scanner infile = null;
       try {
         infile = new Scanner(new File("users.csv") );
       } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
          return null ;
       }
     
         List <String[]> rows = new ArrayList<String[]>();
       
       
       while (infile.hasNext()) {
        String Data = infile.nextLine();
        if (Data != null ){
        String [] l = Data.split(",");
        rows.add(l);}
      }

      for (String[] s : rows){
        ArrayList<String> u = new ArrayList<String>();
        u.add(s[0] );
        u.add(s[1]);

        users.add(u);

      }

      return users ;
  }
  

public void editHistory(String From  , String To , String type , byte[] Messaage , String Time , byte[] AESKeynull  ) throws FileNotFoundException{
  Scanner infile = new Scanner(History);

  List <String[]> rows = new ArrayList<String[]>();
  
  
  while (infile.hasNext()) {
   String [] l = infile.nextLine().split(",");
   rows.add(l);
 }
 
 try (PrintWriter writer = new PrintWriter(this.History)) {

  StringBuilder sb = new StringBuilder();


  for (String[] s : rows){
    for (int i = 0 ; i < s.length; i++){
      sb.append(s[i]);

      if (i < s.length - 1){
        sb.append(',');

      }
    }

    sb.append("\n");
    //System.out.println("Done");
  
  }
  // Add new Element 
    sb.append(From);
    sb.append(',');
    sb.append(To);
    sb.append(',');
    sb.append(type);
    sb.append(',');

    String bytemessage = Base64.getEncoder().encodeToString(Messaage) ; 
    sb.append(bytemessage);
    sb.append(',');
    sb.append(Time);
    sb.append(',');
    
    if (AESKeynull != null ){
      String byteAES = Base64.getEncoder().encodeToString(AESKeynull) ;
      sb.append(byteAES);

    }else{
      sb.append("null");
    }
    
    sb.append('\n');



  writer.write(sb.toString());

  

}
}

// public List<byte[]> getHistory( String From ) throws Exception{
//   List <byte[]> chatHis = new ArrayList();
//   Scanner infile = new Scanner(History);

//   List <String[]> rows = new ArrayList<String[]>();
  
  
//   while (infile.hasNext()) {
//    String Data = infile.nextLine();
//    if (Data != null ){
//    String [] l = Data.split(",");
//    rows.add(l);}
//  }

//  for (String[] s : rows){
//     if ((s[0].equals(From) || s[1].equals(From)) && (s[2].equals("Plain"))){
      
     
//       byte[] messaage = s[3].getBytes();
//       byte[] bytee = Base64.getDecoder().decode(messaage);
//       chatHis.add(bytee);
//     }
//   }
//   return chatHis ;
// }



public List<Object> getHistory( String From ) {
  List <Object> chatHis = new ArrayList();
  Scanner infile = null;
  try {
    infile = new Scanner(History);
  } catch (FileNotFoundException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }

  List <String[]> rows = new ArrayList<String[]>();
  
  
  while (infile.hasNext()) {
   String Data = infile.nextLine();
   if (Data != null ){
   String [] l = Data.split(",");
   rows.add(l);}
 }

 for (String[] s : rows){
    if ((s[0].equals(From) || s[1].equals(From))){
      ArrayList<Object> elements = new ArrayList<Object>();
      
      byte[] messaage = s[3].getBytes();
      byte[] bytee = Base64.getDecoder().decode(messaage);
      if (s[2].equals("Plain")){
        elements.add(s[0]);
        elements.add("Plain");
        elements.add(bytee);
        elements.add(s[4]);
      }else{
        elements.add(s[0]);
        elements.add("File");
        elements.add(bytee);
        elements.add(s[4]);
        elements.add(s[2]);
        byte[] aeskey = s[5].getBytes();
        byte[] keybyte = Base64.getDecoder().decode(aeskey);
        elements.add(keybyte);
      }
      chatHis.add(elements);
      
    }
  }
  return chatHis ;
}



public void editKeys(String IP , byte[] Key) throws FileNotFoundException{
  Scanner infile = new Scanner(PublicKeys);

  List <String[]> rows = new ArrayList<String[]>();
  
  
  while (infile.hasNext()) {
   String [] l = infile.nextLine().split(",");
   rows.add(l);
 }
 
 try (PrintWriter writer = new PrintWriter(this.PublicKeys)) {

  StringBuilder sb = new StringBuilder();


  for (String[] s : rows){
    for (int i = 0 ; i < s.length; i++){
      sb.append(s[i]);

      if (i < s.length - 1){
        sb.append(',');

      }
    }

    sb.append("\n");
  
  }
  // Add new Element 
    sb.append(IP);
    sb.append(',');
    String byteKey = Base64.getEncoder().encodeToString(Key);
  
    sb.append(byteKey);
    sb.append('\n');
  writer.write(sb.toString());
}
}



public byte[] getHisKey(String IP) throws Exception {


  Scanner infile = new Scanner(PublicKeys);

  List <String[]> rows = new ArrayList<String[]>();
  
  
  while (infile.hasNext()) {
   String [] l = infile.nextLine().split(",");
   rows.add(l);
 }

 for (String[] s : rows){
    if (s[0].equals(IP)){
      byte[] Key = s[1].getBytes();
      byte[] bytee = Base64.getDecoder().decode(Key);
      return bytee;
      
      
    }
  }
  return null ;

}


public static void main(String[] args)  {
  CSV csv = null;
  try {
    csv = new CSV();
  } catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
  System.out.println(csv.getUsers());
  



}

}
