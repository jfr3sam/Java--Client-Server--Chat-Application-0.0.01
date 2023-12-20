
import javax.management.monitor.CounterMonitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Researcher  {
    private int timeOnline;
    private String status;
    private Server s1;
    private Client c1;
    private String key;
    private RSA rsa;
    public CSV csv;
    private Thread t1 ;
    

    public Researcher() throws Exception {
        this.csv=new CSV();
        this.rsa=new RSA();
    }

    public int getTimeOnline() {
        return timeOnline;
    }

    public String getStatus() {
        return status;
    }

    public Server getC1() {
        return s1;
    }

    public String getKey() {
        return key;
    }

    public RSA getS1() {
        return rsa;
    }

    public void setTimeOnline(int timeOnline) {
        this.timeOnline = timeOnline;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setC1(Server s1) {
        this.s1 = s1;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setS1(RSA s1) {
        this.rsa = rsa;
    }

    public boolean login(String pin){
        boolean check =false;
        try{
            File file=new File("Researcher");
            Scanner scanner=new Scanner(file);
            while(scanner.hasNextLine()){
                String password=scanner.nextLine();
                if(password.equals(pin)){
                    check=true;
                }
            }
        }catch(FileNotFoundException e){}
        if (check){
            int port = 51510;
            setStatus("Online");
            this.s1=new Server(port,rsa,csv);
            this.t1=new Thread(s1);
            t1.start();
         return true;
        }
        else{
         return false;
        }
    }

    public void send( String hostName,String message, boolean type){
        int port = 51510;
        c1=new Client(port,hostName);
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] RecPubyte=null;;
                try {
                    RecPubyte = csv.getHisKey(hostName);
                    rsa.setRecPublic(RecPubyte);
                    RecPubyte = rsa.getRecPublic();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (RecPubyte  == null  ){
                    // System.out.print("IN IF");
                    c1.Give_Me_key();
                    boolean con = true ;
                    while ( con ){
                        RecPubyte = rsa.getRecPublic();
                        System.out.print("");
                        if (RecPubyte != null ){
                            con = false;
                            try {
                                csv.editKeys(hostName, rsa.getRecPublic());
                            } catch (FileNotFoundException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }}}
                byte[] encmessage;
                if(type) {
                    try {
                        encmessage = rsa.encrypt(message, rsa.bytestoPublic(rsa.getRecPublic()));
                        c1.send(encmessage);
                       // System.out.println(encmessage);
                        // Save to history with The Sender Public Key ( NOT THE RECIVER BECAUES HE CAN'T DECRYPT IT WITH RECIVER KEY )
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();

                        // SET THE TYPE = "Plain" For TEXT MESSAGES .
                        csv.editHistory("ME", hostName, "Plain", rsa.encrypt(message, rsa.publicKey), dtf.format(now), null);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                try { // Files
                    List<byte[] > ReciverFile = rsa.encryptFile(message, rsa.bytestoPublic(rsa.getRecPublic()));
                    c1.sendFile(ReciverFile);


                    //  Save to history with The Sender Public Key ( NOT THE RECIVER BECAUES HE CAN'T DECRYPT IT WITH RECIVER KEY )
                    List<byte[] > SenderFile = rsa.encryptFile("kok.png", rsa.publicKey);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");   //  Time Format
                    LocalDateTime now = LocalDateTime.now();  // Get Current Time

                    // Here The
                    csv.editHistory("ME",hostName, new String(SenderFile.get(2)), SenderFile.get(0),dtf.format(now)  , SenderFile.get(1)); // Save it to myhis.csv file ( Encrypted )

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }}
        });
        t2.start();

    }

    public void disconnect(){
        //t1.sleep(1000);
        s1.Close();
        setStatus("Offline");
    }

    public void retriveChat(String ip , String Name) throws Exception {
        List<Object> d= csv.getHistory(ip);
        for(int i=0;i<d.size();i++){

            ArrayList<Object> elements = (ArrayList<Object>) d.get(i);

            if (elements.get(1).equals("Plain")){
                byte[] encmessage = (byte[]) elements.get(2);
                System.out.println("From: " +elements.get(0) +" at "+ elements.get(3)+" - "+rsa.decrypt(encmessage));

            }else if (elements.get(1).equals("File")){
                byte[] encmessage = (byte[]) elements.get(2);
                byte[] AES = (byte[]) elements.get(5);
                List <byte[]> list = new ArrayList<byte[]>();

                list.add(encmessage);
                list.add(AES);

                rsa.DecryptFile(list, (String)elements.get(4));

                System.out.println("From : " +elements.get(0) +" at "+ elements.get(3) + " - " + "File Saved as " + (String) elements.get(4));

            }

            // System.out.println(rsa.decrypt(d.get(i)));
        }
    }



    public List<Object> getUsers(){

        return csv.getListofuser();

    }

}
