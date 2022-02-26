import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class Tarea2TokenRing {
    
    static void enviar_token(int nodo, short token) throws IOException, InterruptedException{
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = null;
        
        //re-intentos de conexion con el servidor
        for(;;){
            try {
                conexion = cliente.createSocket("localhost", 50000 + nodo);
                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tarea2TokenRing.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeShort(token);
        Thread.sleep(1000);
        salida.close();
        conexion.close();
    }
    
    

    public static void main(String[] args) throws InterruptedException, IOException {
        
        System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
        System.setProperty("javax.net.ssl.trustStore", "keystore_servidor.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "1234567");
        
        
        Scanner sc = new Scanner(System.in);
        int nodo;
        System.out.print("Cual es el identificador del nodo: ");
        nodo = sc.nextInt();
        
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory.createServerSocket(nodo + 50000);
        
        if (nodo == 0)
            enviar_token(1, (short) 0);
        
        for(;;){
            try {
                Socket conexion = socket_servidor.accept();
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                short token = entrada.readShort();
                token = (short) (token + 1);
                System.out.println("El valor del token es: " + token);
                
                if(nodo == 0 && token >= 500)
                    System.exit(0);
                
                enviar_token(((nodo + 1) % 6), token);
            } catch (IOException ex) {
                Logger.getLogger(Tarea2TokenRing.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }    
}
