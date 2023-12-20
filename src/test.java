
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class test {

  public static void main(String args[]) throws Exception {
    Scanner scanner = new Scanner(System.in);

    String pin = "";

    int ch = -1;

    while (ch != 0) {

      System.out.println("Choose :- \n1: Login\n2: Submit Form\n-1: Exit");
      ch = scanner.nextInt();

      if (ch == 1) {
        System.out.println("Enter PIN ? or enter ex to exit : ");
        pin = scanner.next();
        File file = new File("password ");
        Scanner infile = new Scanner(file);

        if (infile.hasNextLine()) {
          String password = infile.next();
          if (password.equals(pin)) {
            ch = 0;
          }
        }
      }
        else if (ch == 2) {
          Scanner scanner1 = new Scanner(System.in);
          System.out.println("Creating form");
          System.out.print("Name : ");
          String name = scanner1.nextLine();
          File file = new File(name+"Form.csv");
          ///////////////////////////////////////
          //researcher information for the form

          //////////////////////////////////////
          PrintWriter writer=new PrintWriter(file);
          writer.write(name);
          System.out.print("password : ");
          File file1 = new File("password ");
          PrintWriter writer1=new PrintWriter(file1);
          writer1.write(scanner1.nextLine());
          writer1.close();
          writer.close();

          ////////////Send the form file to the admin
          Client admin=new Client(51510,"192.168.8.100");
          byte[] form=new byte[(int)file.length()];
          BufferedInputStream buffer=new BufferedInputStream(new FileInputStream(file));
          buffer.read(form,0,form.length);
          buffer.close();
          admin.sendForm(form,name+"Form.csv");
          ////////////////////////////////////////////////////////////////
        }

        else if(ch==-1){
          System.exit(1);
        }


    }
    // UI for the user (admin or researcher)
    Admin res = new Admin();
    adminer(pin, res);

  }

  public static void reserahcer(String pin, Researcher res) throws Exception {
    String IP = "";
    String Name = "";
    Scanner scanner = new Scanner(System.in);

    if (res.login(pin)) {
      int rch = 0;
      while (rch != -1) {
        List<Object> ReseacrherList = res.csv.getListofuser();
        System.out.println("Researchers : ");
        ArrayList<String> u = null;
        for (int i = 0; i < ReseacrherList.size(); i++) {
          u = (ArrayList<String>) ReseacrherList.get(i);
          System.out.println(i + " - " + u.get(0));
        }
        System.out.println("-1 : Exit");
        System.out.println("Select one :");
        rch = scanner.nextInt();
        if (rch == -1) {
          res.disconnect();
          System.exit(1);
        }

        u = (ArrayList<String>) ReseacrherList.get(rch);
        Name = u.get(0);
        IP = u.get(1);

        System.out.println("-------------------------------------------------------------------------------");

        int tfch = 0;
        while (tfch != 3) {

          System.out.println("------\n1- Text \n 2-File \n 3-exit chat ");
          tfch = scanner.nextInt();

          if (tfch == 1) {
            res.retriveChat(IP, Name);
            System.out.print("Message : ");

            String message = scanner.nextLine();
            message += scanner.nextLine();

            res.send(IP, message, true);
          } else if (tfch == 2) {
            res.retriveChat(IP, Name);
            System.out.println("File Path  : ");
            String message = scanner.next();
            res.send(IP, message, false);
          } else if (tfch == 3) {
            System.out.println("Exit");
          }

        }

      }
    }

    else {
      System.out.println("Wrong PIN ");
      // continue;

    }
  }

  public static void adminer(String pin, Admin res) throws Exception {
    String IP = "";
    String Name = "";
    Scanner scanner = new Scanner(System.in);

    if (res.login(pin)) {

      int adch = 0;
      while (adch != -1) {
        System.out.println("-----\n1: Add researcher\n2: Remove researcher\n3: Send Message\n4- Report\n-1 : Exit");
        adch = scanner.nextInt();
        if (adch == 1) {
          System.out.print("Name : ");
          String reserachername = scanner.next();
          System.out.println("IP : ");
          String host = scanner.next();
          res.addResearcher(reserachername, host);

        } else if (adch == 2) {
          System.out.println("You can Enter IP or name : ");
          System.out.print("Name : ");
          String reserachername = scanner.next();
          System.out.println("IP : ");
          String host = scanner.next();
          res.removeResearcher(reserachername, host);

        } else if (adch == 3) {

          int rch = 0;
          while (rch != -1) {
            List<Object> ReseacrherList = res.csv.getListofuser();
            System.out.println("Researchers : ");
            ArrayList<String> u = null;
            for (int i = 0; i < ReseacrherList.size(); i++) {
              u = (ArrayList<String>) ReseacrherList.get(i);
              System.out.println(i + " - " + u.get(0));
            }
            System.out.println("-1 : Exit");
            System.out.println("Select one :");
            rch = scanner.nextInt();
            if (rch == -1) {
            break;
            }

            u = (ArrayList<String>) ReseacrherList.get(rch);
            Name = u.get(0);
            IP = u.get(1);

            System.out.println("-------------------------------------------------------------------------------");

            int tfch = 0;
            while (tfch != 3) {

              System.out.println("------\n1- Text \n 2-File \n 3-exit chat ");
              tfch = scanner.nextInt();

              if (tfch == 1) {
                res.retriveChat(IP, Name);
                System.out.print("Message : ");

                String message = scanner.nextLine();
                message+= scanner.nextLine();

                res.send(IP, message, true);
              } else if (tfch == 2) {
                res.retriveChat(IP, Name);
                System.out.println("File Path  : ");
                String message = scanner.next();
                res.send(IP, message, false);
              } else if (tfch == 3) {
                System.out.println("Exit");
              }

            }

          }
        } else if (adch == 4){
          List<String> report = res.getreport() ;
          for (int i = 0 ; i < report.size() ; i++) {
            System.out.println(report.get(i));
          }



        }
        else if (adch == -1 ){
           System.exit(1); ; 
        }
      }
    }

    else {
      System.out.println("Wrong PIN ");
      // continue;
    }
  }
}
