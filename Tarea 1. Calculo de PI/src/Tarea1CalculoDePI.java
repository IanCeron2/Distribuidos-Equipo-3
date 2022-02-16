
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Iancr
 */
public class Tarea1CalculoDePI {
    
    static class Connection_toServer extends Thread{
        int id_nodo_server;

        public Connection_toServer(int id_nodo_server) {
            this.id_nodo_server = id_nodo_server;
        }
                
        @Override
        public void run(){  
            Socket conexion = null;
            //re-intentos de conexion con el servidor
            for(;;){
                try {
                    conexion = new Socket("localhost", 50000 + id_nodo_server);
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Tarea1CalculoDePI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            //ya que se conecto con un servidor, debe de recibir el calculo que hizo el servidor
            
        }          
    }
    
    static class Worker extends Thread{
        Socket conexion;
        int id_nodo_server;

        Worker(Socket conexion, int id_nodo_server){
            this.conexion = conexion;            
            this.id_nodo_server = id_nodo_server;
        }

        @Override
        public void run(){
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                float sumatoria = 0;
                
                for(int i = 0; i < 1000000; i++) sumatoria += (4/(8*i+2*(id_nodo_server - 2)+3));
                
                if(id_nodo_server % 2 == 0){ // es un nodo par
                    sumatoria = sumatoria*(-1);
                }
                
                salida.writeFloat(sumatoria);                
                conexion.close();

            } catch (IOException ex) {
                Logger.getLogger(Tarea1CalculoDePI.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }       
    }
    
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int n;
        
        System.out.print("Cual es el identificador del nodo: ");
        n = sc.nextInt();
        
        if(n == 0){ // es un nodo cliente
            for(int i = 1; i < 5; i++) {
                Connection_toServer c = new Connection_toServer(i);
                c.start();
            }
        }
        else if (n == 1 || n == 2 || n == 3 || n == 4){ // es un nodo servidor
                      
            ServerSocket servidor = new ServerSocket(50000 + n);           
            for(;;){
                Socket conexion = servidor.accept();
                Worker cl = new Worker(conexion, n);
                cl.start();
            }
        }
        else { // es un nodo no definido
            System.out.println("Ingresa un id definido para el nodo");
        }
    }
    
}
