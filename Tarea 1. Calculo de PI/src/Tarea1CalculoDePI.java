
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
    static double pi = 0;
    static Object obj = new Object();
    
    static class Worker_Cliente extends Thread{
        int id_nodo_server;

        Worker_Cliente(int id_nodo_server) {
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
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Tarea1CalculoDePI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                double pi_n = entrada.readDouble();
                //System.out.println("El valor de pi_n: " + pi_n);
                synchronized(obj){
                    pi += pi_n;
                }
                conexion.close();
                
            } catch (IOException ex) {
                Logger.getLogger(Tarea1CalculoDePI.class.getName()).log(Level.SEVERE, null, ex);
            }           
        }          
    }
    
    static class Worker_Servidor extends Thread{
        Socket conexion;
        int id_nodo_server;

        Worker_Servidor(Socket conexion, int id_nodo_server){
            this.conexion = conexion;            
            this.id_nodo_server = id_nodo_server;
        }

        @Override
        public void run(){
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                double sumatoria = 0;
                
                for(double i = 0; i < 1000000; i++) sumatoria += (4/((8*i)+(2*(id_nodo_server - 2))+3));
                
                //System.out.println(sumatoria);
                
                if(id_nodo_server % 2 == 0){ // es un nodo par
                    sumatoria = sumatoria*(-1);
                }
                
                salida.writeDouble(sumatoria);                
                conexion.close();

            } catch (IOException ex) {
                Logger.getLogger(Tarea1CalculoDePI.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }       
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        int n;
        
        System.out.print("Cual es el identificador del nodo: ");
        n = sc.nextInt();
        
        if(n == 0){ // es un nodo cliente
            
            Worker_Cliente c1 = new Worker_Cliente(1);
            Worker_Cliente c2 = new Worker_Cliente(2);
            Worker_Cliente c3 = new Worker_Cliente(3);
            Worker_Cliente c4 = new Worker_Cliente(4);
            c1.start();
            c2.start();
            c3.start();
            c4.start();
            c1.join();
            c2.join();
            c3.join();
            c4.join();
            System.out.println("El valor de pi es: " + pi);
        }
        else if (n == 1 || n == 2 || n == 3 || n == 4){ // es un nodo servidor
                      
            ServerSocket servidor = new ServerSocket(50000 + n);           
            for(;;){
                Socket conexion = servidor.accept();
                Worker_Servidor cl = new Worker_Servidor(conexion, n);
                cl.start();
            }
        }
        else { // es un nodo no definido
            System.out.println("Ingresa un id definido para el nodo");
        }
    }
    
}
