
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RSA{

    private   PrivateKey privateKey;
    //private  String publicKeyString;
    public  PublicKey publicKey;
    private byte[] RECPublicKey ;

    private byte[] PublicKeyByte ;

    //private PublicKey ReciverKey ; 

    public RSA() throws Exception {
        File pubFile = new File("Public.key");
        File privFile = new File("Private.key");

        // IF No Public Key and Private Key Generated Before , Genrate New Keys .. 
        if (!pubFile.exists() || !privFile.exists()) {
            System.out.println("Genrate New PUBLIC AND PRIVATE Keys");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
         keyPairGenerator.initialize(1024);
         KeyPair pair = keyPairGenerator.generateKeyPair();
         publicKey = pair.getPublic();
        
         privateKey = pair.getPrivate();

        // Store PublicKey
         FileOutputStream outPub = new FileOutputStream("Public.key");
         outPub.write(Base64.getEncoder().encode(publicKey.getEncoded()));
         outPub.close();

         // Store PrivateKey
         FileOutputStream outPriv = new FileOutputStream("Private.key");
         outPriv.write(Base64.getEncoder().encode(privateKey.getEncoded()));
         outPriv.close();
        } else {

            // Load Keys From File .. 
            KeyPair keyPair = LoadKeyPair();
            privateKey =  keyPair.getPrivate();
            publicKey = keyPair.getPublic();
          

        }

    }


    public KeyPair LoadKeyPair() throws Exception {
        //Read Public Key . 
        File filePub = new File("Public.key");
        FileInputStream inPub = new FileInputStream("Public.key");
        byte[] encodedPublicKey = new byte[(int) filePub.length()];
        inPub.read(encodedPublicKey);
        inPub.close();
        encodedPublicKey = Base64.getDecoder().decode(encodedPublicKey);


        // Read Private Key 
        File filePriv = new File("Private.key");
        FileInputStream inPriv = new FileInputStream("Private.key");
        byte[] encodedPrivateKey = new byte[(int)filePriv.length()];
        inPriv.read(encodedPrivateKey);
        inPriv.close();
        encodedPrivateKey = Base64.getDecoder().decode(encodedPrivateKey);
        


        // Genrate Pairs 

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec PublicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey public_key = keyFactory.generatePublic(PublicKeySpec);

        PKCS8EncodedKeySpec PrivateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey private_key = keyFactory.generatePrivate(PrivateKeySpec);

        return  new KeyPair (public_key , private_key);
}




public PrivateKey getPrivateKey() {
    return privateKey;
    }

    
    public byte[] getPbyte() {
        return PublicKeyByte;
    }


    public Key getKey() {
        return publicKey;
    }

    public void setRecPublic(byte[] Key ) throws Exception {
        
        this.RECPublicKey =  Key; 
    }


    public byte[] getRecPublic( ) {
        return RECPublicKey ; 
    }



    public PublicKey bytestoPublic(byte[] encKey) throws Exception {

        X509EncodedKeySpec pubKeyspec = new X509EncodedKeySpec(encKey);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey public_key =  factory.generatePublic(pubKeyspec);
        return public_key;
    }



    public  byte[] encrypt(String Message , PublicKey publicKey) {
        try {
            byte[] content = Message.getBytes();

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return  cipher.doFinal(content); //return Bytes 
            // return new String(utf8, "UTF8"); // Return String
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(byte[] content) {
        try {
           
           
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //return cipher.doFinal(content); // returning Bytes 
            byte[] utf8 = cipher.doFinal(content);
            return new String(utf8, "UTF8"); // Returning Strings
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    // Return List [Encrypted Image Using AES (We Can't Use RSA for Images ) , AES Key ] 
    public List<byte[]> encryptFile(String Path , PublicKey publicKey ) throws Exception {

        // check file exist :
        File file = new File(Path);
        
        // List of [Encrypted File  , AESKey , FileName]
        List<byte[]> list = new ArrayList<byte[]>();
        
        // Read Image
        byte [] data = null; 
        data = new byte[(int)file.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(data, 0, data.length);
            bis.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found ");
        }
       

        
        // AES Encryption 
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        list.add(aesCipher.doFinal(data)); //add encrypted Data to the List 

        //Encrypt The AES Key 
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.PUBLIC_KEY, publicKey);
        byte[] encryptedKey = cipher.doFinal(secKey.getEncoded());

        list.add(encryptedKey); // Add Encrypted AES Key to the List 
        list.add(file.getName().getBytes()); // Add file Name To the List 

        return list;
    }

    public void DecryptFile(List<byte[]> List , String Pathtosave ) throws Exception{
        // Get Image and encrypted Key 
        byte[] EncData = List.get(0);
        byte[] EncAESKey = List.get(1);
        // Decrypt AES Key 
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.PRIVATE_KEY, privateKey);
        byte[] DecAESKey = cipher.doFinal(EncAESKey);
        // System.out.println("in");
        SecretKey originalKey = new SecretKeySpec(DecAESKey , 0, DecAESKey .length, "AES");
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
        
        byte[] Filedata = aesCipher.doFinal(EncData); // Decrypt the data using AES 

        FileOutputStream fos = new FileOutputStream(Pathtosave);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(Filedata , 0 , Filedata.length);
        bos.close();

        System.out.println("File created at " + Pathtosave);

    } 

    



// // Remove it (For Testing Only )
//     public static void main(String[] args) throws Exception {
//        RSA p = new RSA();
//       String Path = "Test.mp4";

//      List<byte[]> list = p.encryptFile(Path, p.publicKey, 98);

//      p.DecryptFile(list, "output.mp4", 98);


//     }
 
}