/**
 *
 * @author Iancr
 */
public class MultiplicaMatriz_2 {
    
    static int N = 1000;
    static double[][]A = new double[N][N];
    static double[][]B = new double[N][N];
    static double[][]C = new double[N][N];
    static double checksum = 0;
    
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        
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
        
        
        // Multiplica la matriz A y B
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                for(int k = 0; k < N; k++){
                    C[i][j] += A[i][k] * B[j][k];
                }
                checksum += C[i][j];
            }
        }
        
//        for(int i = 0; i < N; i++){
//            for(int j = 0; j < N; j++){
//                System.out.print(C[i][j] + " ");
//            }
//            System.out.println("");
//        }
        System.out.println("El checksum vale: " + checksum);
        
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo: " + (t2 - t1) + "ms");
    }
}
