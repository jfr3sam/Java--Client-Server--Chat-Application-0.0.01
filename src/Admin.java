
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Admin extends Researcher{
   
    public Admin() throws Exception {
        super();
        csv.createUser();
        //TODO Auto-generated constructor stub
    }

    
    
    public void addResearcher (String name , String ip ) throws FileNotFoundException {

       
        csv.EditUser(name, ip , true);
        notfiy();
        
    

    }

    public void removeResearcher (String name , String ip ) throws FileNotFoundException  {
        csv.EditUser(name, ip , false);
        notfiy();
      

    }


    private void notfiy () {

        File file = new File("users.csv");

        byte [] data = null; 
        data = new byte[(int)file.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(data, 0, data.length);
            bis.close();
        } catch (Exception e) {
            System.out.println("File Not Found ");
        }

        List<String> users = new ArrayList<String>();

        users = csv.getUsers(); 

        for (String user : users) {
        //    System.out.println(user);

        if (is_online(user)){
            //Client client = new Client(6602,user);
            //client.senduers(data);
            send(user , " " , true);
            send(user, "users.csv", false);
            System.out.println("Sending --> " + user);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        } 
    }
    
    public List<String> getreport(){
        List<String> online = new ArrayList<String>();
        List<String> users = new ArrayList<String>();

        users = csv.getUsers();
        boolean useronline = false ; 

        for (String user : users) {
        //    System.out.println(user);
        useronline = is_online(user);
        if (useronline){
           online.add(user + " is Online");
            
        }else{
            online.add(user + " is Offline");
        }
        } 

        return online ;
    }

    public boolean is_online(String host)  { 
       
         Socket sock = new Socket();
         int timeOut = (int)TimeUnit.SECONDS.toMillis(2); 
     
         try {
         sock.connect(new InetSocketAddress(host, 51510), timeOut);
         }catch (SocketTimeoutException ex){
             return false;
         } catch (IOException e){
             // Ignore 

         }
         return true ;
       
         }


}
