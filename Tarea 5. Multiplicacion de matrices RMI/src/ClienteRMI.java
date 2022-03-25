import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClienteRMI {
    
    static int N = 8;
    static float[][]A = new float[N][N];
    static float[][]B = new float[N][N];
    static float checksum = 0;
    
    static float[][] traspone_B(float[][]B, int N){
        //Traspone la matriz B, la matriz traspuesta queda en B
        for(int i = 0; i < N; i++){
            for(int j = 0; j < i; j++){
                float x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }
        return B;        
    }
    
    static float[][] separa_matriz(float[][] A, int inicio, int N){
        float[][] M = new float[N/4][N];
        for(int i = 0; i < N/4; i++)
            for(int j = 0; j < N; j++)
                M[i][j] = A[i + inicio][j];
        return M;
    }
    
    static void acomoda_matriz(float[][] C, float[][] A, int renglon, int columna, int N){
        for(int i = 0; i < N/4; i++)
            for(int j = 0; j < N/4; j++)
                C[i + renglon][j + columna] = A[i][j];        
    }   
    
    static class multiplica_matriz extends Thread{
        InterfaceRMI r;
        float[][] C;
        float[][] A;
        float[][] B;
        int N;

        public multiplica_matriz(InterfaceRMI r, float[][] A, float[][] B, int N) {
            this.r = r;
            this.A = A;
            this.B = B;
            this.N = N;
        }
        
        @Override
        public void run(){
            try {
                C = r.multiplica_matrices(A, B, N);
            } catch (RemoteException ex) {
                Logger.getLogger(ClienteRMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
            
    
    public static void main(String[] args) throws NotBoundException, RemoteException, MalformedURLException, InterruptedException {
          
        String url_1 = "rmi://10.0.0.4/multiplica";
        String url_2 = "rmi://10.0.0.5/multiplica";
        String url_3 = "rmi://10.0.0.6/multiplica";
        String url_4 = "rmi://10.0.0.7/multiplica";        
	//para ejecutar en otra computadora tendriamos que poner algo asi
	//String url = "rmi://10.0.0.5/prueba";  
        
        //obtiene una referencia que "apunta" al objeto remoto asociado a la URL
        InterfaceRMI r_1 = (InterfaceRMI)Naming.lookup(url_1);
        InterfaceRMI r_2 = (InterfaceRMI)Naming.lookup(url_2);
        InterfaceRMI r_3 = (InterfaceRMI)Naming.lookup(url_3);
        InterfaceRMI r_4 = (InterfaceRMI)Naming.lookup(url_4);
        
        // Inicializa las matrices A y B
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                A[i][j] = i + 2 * j;
                B[i][j] = 3 * i - j;
            }
        }
        
        B = traspone_B(B, N);        
        
        // Separamos las matrices
        float[][] A1 = separa_matriz(A, 0, N);
        float[][] A2 = separa_matriz(A, (N/4)*1, N);
        float[][] A3 = separa_matriz(A, (N/4)*2, N);
        float[][] A4 = separa_matriz(A, (N/4)*3, N);
        
        float[][] B1 = separa_matriz(B, 0, N);
        float[][] B2 = separa_matriz(B, (N/4)*1, N);
        float[][] B3 = separa_matriz(B, (N/4)*2, N);
        float[][] B4 = separa_matriz(B, (N/4)*3, N);
        
        float[][] C1 = new float[N/4][N/4], C2 = new float[N/4][N/4], 
                C3 = new float[N/4][N/4], C4 = new float[N/4][N/4], 
                C5 = new float[N/4][N/4], C6 = new float[N/4][N/4], 
                C7 = new float[N/4][N/4], C8 = new float[N/4][N/4], 
                C9 = new float[N/4][N/4], C10 = new float[N/4][N/4], 
                C11 = new float[N/4][N/4], C12 = new float[N/4][N/4], 
                C13 = new float[N/4][N/4], C14 = new float[N/4][N/4], 
                C15 = new float[N/4][N/4], C16 = new float[N/4][N/4];
        
        multiplica_matriz m1 = new multiplica_matriz(r_1, A1, B1, N);
        multiplica_matriz m2 = new multiplica_matriz(r_1, A1, B2, N);
        multiplica_matriz m3 = new multiplica_matriz(r_1, A1, B3, N);
        multiplica_matriz m4 = new multiplica_matriz(r_1, A1, B4, N);
        
        multiplica_matriz m5 = new multiplica_matriz(r_2, A2, B1, N);
        multiplica_matriz m6 = new multiplica_matriz(r_2, A2, B2, N);
        multiplica_matriz m7 = new multiplica_matriz(r_2, A2, B3, N);
        multiplica_matriz m8 = new multiplica_matriz(r_2, A2, B4, N);
        
        multiplica_matriz m9 = new multiplica_matriz(r_3, A3, B1, N);
        multiplica_matriz m10 = new multiplica_matriz(r_3, A3, B2, N);
        multiplica_matriz m11 = new multiplica_matriz(r_3, A3, B3, N);
        multiplica_matriz m12 = new multiplica_matriz(r_3, A3, B4, N);
        
        multiplica_matriz m13 = new multiplica_matriz(r_4, A4, B1, N);
        multiplica_matriz m14 = new multiplica_matriz(r_4, A4, B2, N);
        multiplica_matriz m15 = new multiplica_matriz(r_4, A4, B3, N);
        multiplica_matriz m16 = new multiplica_matriz(r_4, A4, B4, N);
                
        // iniciamos todos los hilos
        m1.start();
        m2.start();
        m3.start();
        m4.start();
        m5.start();
        m6.start();
        m7.start();
        m8.start();
        m9.start();
        m10.start();
        m11.start();
        m12.start();
        m13.start();
        m14.start();
        m15.start();
        m16.start();
        
        // esperamos a que terminen los hilos
        m1.join();
        m2.join();
        m3.join();
        m4.join();
        m5.join();
        m6.join();
        m7.join();
        m8.join();
        m9.join();
        m10.join();
        m11.join();
        m12.join();
        m13.join();
        m14.join();
        m15.join();
        m16.join(); 
        
        C1 = m1.C;
        C2 = m2.C;
        C3 = m3.C;
        C4 = m4.C;
        C5 = m5.C;
        C6 = m6.C;
        C7 = m7.C;
        C8 = m8.C;
        C9 = m9.C;
        C10 = m10.C;
        C11 = m11.C;
        C12 = m12.C;
        C13 = m13.C;
        C14 = m14.C;
        C15 = m15.C;
        C16 = m16.C;       
        
        // juntamos las matrices
        
        float[][] C = new float[N][N];
        acomoda_matriz(C, C1, 0, 0, N);
        acomoda_matriz(C, C2, 0, (N/4)*1, N);
        acomoda_matriz(C, C3, 0, (N/4)*2, N);
        acomoda_matriz(C, C4, 0, (N/4)*3, N);
        
        acomoda_matriz(C, C5, (N/4)*1, 0, N);
        acomoda_matriz(C, C6, (N/4)*1, (N/4)*1, N);
        acomoda_matriz(C, C7, (N/4)*1, (N/4)*2, N);
        acomoda_matriz(C, C8, (N/4)*1, (N/4)*3, N);  
        
        acomoda_matriz(C, C9, (N/4)*2, 0, N);
        acomoda_matriz(C, C10, (N/4)*2, (N/4)*1, N);
        acomoda_matriz(C, C11, (N/4)*2, (N/4)*2, N);
        acomoda_matriz(C, C12, (N/4)*2, (N/4)*3, N); 
        
        acomoda_matriz(C, C13, (N/4)*3, 0, N);
        acomoda_matriz(C, C14, (N/4)*3, (N/4)*1, N);
        acomoda_matriz(C, C15, (N/4)*3, (N/4)*2, N);
        acomoda_matriz(C, C16, (N/4)*3, (N/4)*3, N);
        
        if(N == 8){
            
            System.out.println("Matriz A: ");
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    System.out.print(A[i][j] + " ");
                }
                System.out.println("");
            }  
            
            B = traspone_B(B, N);
            System.out.println("\nMatriz B: ");
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    System.out.print(B[i][j] + " ");
                }
                System.out.println("");
            }  
            
            System.out.println("\nMatriz C: ");
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    System.out.print(C[i][j] + " ");
                    checksum += C[i][j];
                }
                System.out.println("");
            }  
        }
        else{
            for(int i = 0; i < N; i++)
                for(int j = 0; j < N; j++)
                    checksum += C[i][j];                
        }        
        System.out.println("El checksum vale: " + checksum);
    }
}
