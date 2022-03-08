import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tarea3 {
    static int N = 1000;
    static double [][]A = new double[N][N];
    static double [][]B = new double[N][N];
    static double [][]C = new double[N][N];
    static double checksum = 0;
    static Object obj = new Object();
    
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws IOException{
        while(longitud > 0){
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;           
        }
    }
    
    static class nodo_0 extends Thread{
        int id_nodo_connect;
        int inicio_fila_A;
        int fin_fila_A;
        int inicio_fila_B;
        int fin_fila_B;
                
        nodo_0(int id_nodo_connect, int inicio_fila_A, int fin_fila_A, int inicio_fila_B, int fin_fila_B){
            this.id_nodo_connect = id_nodo_connect;
            this.inicio_fila_A = inicio_fila_A;
            this.fin_fila_A = fin_fila_A;
            this.inicio_fila_B = inicio_fila_B;
            this.fin_fila_B = fin_fila_B;
        }
       
        @Override
        public void run(){
            Socket conexion = null;
            
            for(;;){
                try {
                    conexion = new Socket("localhost", 50000 + id_nodo_connect);
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Tarea3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
                        
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                int num_filas = (fin_fila_A - inicio_fila_A) + 1; 
                
                //metemos a un bytebuffer los doubles de la matriz A 
                //y en otro diferente los elementos de la matriz B
                
                ByteBuffer a = ByteBuffer.allocate(N * num_filas * 8);
                ByteBuffer b = ByteBuffer.allocate(N * num_filas * 8);
                
                for(int i = inicio_fila_A; i <= fin_fila_A; i++){
                    for(int j = 0; j < N; j++){
                        a.putDouble(A[i][j]);                        
                    }
                }
                
                for(int i = inicio_fila_B; i <= fin_fila_B; i++){
                    for(int j = 0; j < N; j++){
                        b.putDouble(B[i][j]);                        
                    }
                }
                
                byte[] a_elem = a.array();
                byte[] b_elem = b.array();
                salida.write(a_elem);
                salida.flush();
                salida.write(b_elem);
                salida.flush();
                Thread.sleep(1000);
                
                //recibimos el resultado de la multiplicacion
                int n = entrada.readInt();
                byte[] c = new byte[num_filas * N/2 * 8];
                read(entrada, c, 0, num_filas * N/2 * 8);
                ByteBuffer c_elem = ByteBuffer.wrap(c);
                
                int inicio_fila_C = 0, fin_fila_C = 0, inicio_col_C = 0, fin_col_C = 0;
                switch(n){
                    case 1:
                        inicio_fila_C = 0;
                        fin_fila_C = N/2;
                        inicio_col_C = 0; 
                        fin_col_C = N/2; //primer cuadrante
                        break;
                    case 2:
                        inicio_fila_C = 0;
                        fin_fila_C = N/2;
                        inicio_col_C = N/2; 
                        fin_col_C = N; //segundo cuadrante
                        break;
                    case 3:
                        inicio_fila_C = N/2;
                        fin_fila_C = N;
                        inicio_col_C = 0; 
                        fin_col_C = N/2; //tercer cuadrante
                        break; 
                }
                
                //metemos los elementos en el cuadrante que corresponde
                for(int i = inicio_fila_C; i < fin_fila_C; i++){
                    for(int j = inicio_col_C; j < fin_col_C; j++){
                        C[i][j] = c_elem.getDouble();
                        synchronized(obj){
                            checksum += C[i][j];
                        }
                    }
                }                
                conexion.close();
                                
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Tarea3.class.getName()).log(Level.SEVERE, null, ex);
            }                      
        }   
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        Scanner sc = new Scanner(System.in);
        int nodo;
        System.out.print("Cual es el identificador del nodo: ");
        nodo = sc.nextInt();
        
        if(nodo == 0){ 
            
            // Inicializa las matrices A y B
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    A[i][j] = i + 5 * j;
                    B[i][j] = 5 * i - j;
                    C[i][j] = 0;
                }
            }
                       
            //Traspone la matriz B, la matriz traspuesta queda en B
            for(int i = 0; i < N; i++){
                for(int j = 0; j < i; j++){
                    double x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }
            }
            
            int fin_A1 = (N/2)-1, fin_B1 = (N/2)-1;    
            //iniciamos hilos en el nodo 0, cada uno se encarga de conectarse a cada uno
            //de los otros nodos y envia la parte de las matrices que necesitan para hacer el calculo
            nodo_0 C1 = new nodo_0(1, 0, fin_A1, 0, fin_B1);
            nodo_0 C2 = new nodo_0(2, 0, fin_A1, fin_B1 + 1, N - 1);
            nodo_0 C3 = new nodo_0(3, fin_A1 + 1, N - 1, 0, fin_B1);
            C1.start();
            C2.start();
            C3.start();
            
            //mientras los hilos terminan, el nodo 0 calcula los resultados del cuarto cuadrante                            
            for(int i = N/2; i < N; i++){ 
                for(int j = N/2; j < N; j++){ 
                    for(int k = 0; k < N; k++){ 
                        C[i][j] += A[i][k] * B[j][k];
                    }
                    synchronized(obj){
                        checksum += C[i][j];
                    }
                }                
            }
            
            //esperamos a que terminen los otros hilos
            C1.join();
            C2.join();
            C3.join();
            
            if(N == 8){
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < N; j++){
                        System.out.print(C[i][j] + " ");
                    }
                    System.out.println("");
                }
            }
            System.out.printf("El valor del checksum es: %.9f \n" ,checksum);
        }
        else{
            ServerSocket servidor = new ServerSocket(50000 + nodo);           
            for(;;){
                Socket conexion = servidor.accept();
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                
                int num_filas = N/2;
                byte[] a = new byte[(num_filas*N) * 8];
                byte[] b = new byte[(num_filas*N) * 8];
                
                //recibimos los datos que necesita unicamente de la matrz A y B respectivamente
                read(entrada, a, 0, (num_filas*N) * 8);
                read(entrada, b, 0, (num_filas*N) * 8);
                
                ByteBuffer a_elem = ByteBuffer.wrap(a);
                ByteBuffer b_elem = ByteBuffer.wrap(b);
                                
                //metemos los elementos del bytebuffer en la misma matriz A
                for(int i = 0; i < num_filas; i++){
                    for(int j = 0; j < N; j++){
                        A[i][j] = a_elem.getDouble();
                    }                    
                }
                
                //metemos los elementos del bytebuffer en la misma matriz B                
                for(int i = 0; i < num_filas; i++){
                    for(int j = 0; j < N; j++){
                        B[i][j] = b_elem.getDouble();                        
                    }                    
                }
                
                ByteBuffer c_elem = ByteBuffer.allocate((num_filas * N/2) * 8);
                
                //multiplicamos la matriz A y B                              
                for(int i = 0; i < num_filas; i++){ 
                    for(int j = 0; j < num_filas; j++){
                        C[i][j] = 0;
                        for(int k = 0; k < N; k++){ 
                            C[i][j] += A[i][k] * B[j][k];
                        }
                        c_elem.putDouble(C[i][j]);
                    }
                }
                
                byte[] c = c_elem.array();
                // enviamos primero el identificador del nodo para que el nodo 0
                // sepa en que cuadrante van los datos que recibe
                salida.writeInt(nodo);
                salida.flush();
                salida.write(c);
                salida.flush();
                
                Thread.sleep(1000);
                conexion.close();              
            }            
        }
    }
}