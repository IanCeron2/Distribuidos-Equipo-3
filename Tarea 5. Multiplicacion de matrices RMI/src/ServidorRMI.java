import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;


public class ServidorRMI {
    
    public static void main(String[] args) throws RemoteException, MalformedURLException {        
        String url = "rmi://localhost/multiplica";  //siempre es en localhost ya que el ServidorRMI se ejecuta en la misma maquina
        ClaseRMI obj = new ClaseRMI();
        
        //registra la instancia en el rmiregistry
        Naming.rebind(url, obj);
    }    
}
