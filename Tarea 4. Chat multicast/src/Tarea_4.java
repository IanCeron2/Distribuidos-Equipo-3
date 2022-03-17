
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tarea_4 {
    
    static Object obj = new Object();
    
    static public void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }
    
    static public byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
    
    static class Worker extends Thread{
        
        @Override
        public void run(){
            try {
                MulticastSocket socket = new MulticastSocket(50000);
                InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"),50000);
                NetworkInterface netInter = NetworkInterface.getByName("em1");
                socket.joinGroup(grupo, netInter);
                
                for(;;){
                    byte[] a = recibe_mensaje_multicast(socket, 1024);
                    synchronized(obj){
                        System.out.println("");
                        System.out.println(new String(a, "UTF-8"));
                    }                    
                }
            } catch (IOException ex) {
                Logger.getLogger(Tarea_4.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        new Worker().start();
        
        String nombre = args[0];
        Scanner sc = new Scanner(System.in);
        
        
        for(;;){
            String mensaje = nombre + " dice ";
            //System.out.println("");
            synchronized(obj){
                System.out.print("Ingrese el mensaje a enviar: ");
            }
            mensaje += sc.nextLine();
            envia_mensaje_multicast(mensaje.getBytes(), "230.0.0.0", 50000);          
        }       
    }    
}
