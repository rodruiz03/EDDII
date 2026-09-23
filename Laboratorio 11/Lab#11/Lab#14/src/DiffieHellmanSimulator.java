import java.math.BigInteger; // Recomendada por el Inge
import java.util.Random;
import java.util.Scanner;

public class DiffieHellmanSimulator {
    
    // Constantes globales
    private static final BigInteger G = BigInteger.valueOf(5);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger ONE = BigInteger.ONE;
    
    // Variables para almacenar los parámetros generados
    private static BigInteger p = null;
    private static BigInteger g = G;
    private static BigInteger a = null;
    private static BigInteger A = null;
    private static BigInteger b = null;
    private static BigInteger B = null;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        
        do {
            mostrarMenu();
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextInt();
            
            switch (opcion) {
                case 1:
                    simularAlice(scanner);
                    break;
                case 2:
                    simularBob(scanner);
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
            }
            
            System.out.println();
            
        } while (opcion != 0);
        
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("---------------------------------------------");
        System.out.println("|   SIMULADOR DIFFIE-HELLMAN                         |");
        System.out.println("---------------------------------------------");
        System.out.println("1. Simular a Alice (Generar Parametros y Clave A)");
        System.out.println("2. Simular a Bob (Generar Clave B)");
        System.out.println("3. Calcular Clave Secreta Compartida (S)");
        System.out.println("4. Criptoanalisis (Romper el Codigo)");
        System.out.println("0. Salir");
        System.out.println("-----------------------------------------------");
    }
    
    /**
     * Opción 1: Simula a Alice generando los parámetros públicos (p, g) 
     * y su par de claves (privada a, pública A)
     */
    private static void simularAlice(Scanner scanner) {
        System.out.println("\n- SIMULACIÓN DE ALICE -");
        
        // Solicitar cantidad de bits para el primo p
        System.out.print("Ingrese la cantidad de bits para el número primo p (ej. 16, 24, 32): ");
        int bits = scanner.nextInt();
        
        // Validar entrada
        if (bits < 8 || bits > 64) {
            System.out.println("Advertencia: Se recomienda usar entre 8 y 64 bits para esta simulación.");
        }
        
        // Generar número primo p
        Random rnd = new Random();
        p = BigInteger.probablePrime(bits, rnd);
        
        // g ya está establecido como 5
        g = G;
        
        // Generar clave privada a en el rango [2, p-1]
        a = generarClavePrivada(p, rnd);
        
        // Calcular clave pública A = g^a mod p
        A = g.modPow(a, p);
        
        // Mostrar resultados
        System.out.println("\n--- PARAMETROS Y CLAVES DE ALICE ---");
        System.out.println("p (primo publico):      " + p);
        System.out.println("g (generador publico):  " + g);
        System.out.println("a (SECRETO de Alice):   " + a);
        System.out.println("A (clave publica):      " + A);
        System.out.println("\n Alice ha generado sus parametros exitosamente.");
        System.out.println("  Los valores p, g y A pueden compartirse publicamente.");
    }
    
    /**
     * Opción 2: Simula a Bob generando su par de claves (privada b, pública B)
     * usando los parámetros públicos de Alice
     */
    private static void simularBob(Scanner scanner) {
        System.out.println("\n--- SIMULACIÓN DE BOB ---");
        
        // Verificar si Alice ya generó los parámetros
        if (p == null) {
            System.out.print("¿Desea ingresar manualmente p y g? (S/N): ");
            String respuesta = scanner.next();
            
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.print("Ingrese el valor de p: ");
                p = scanner.nextBigInteger();
                System.out.print("Ingrese el valor de g: ");
                g = scanner.nextBigInteger();
            } else {
                System.out.println(" Error: Primero debe ejecutar la opción 1 (Alice) o ingresar p y g manualmente.");
                return;
            }
        } else {
            System.out.println("Usando los parámetros públicos de Alice:");
            System.out.println("p = " + p);
            System.out.println("g = " + g);
        }
        
        // Generar clave privada b en el rango [2, p-1]
        Random rnd = new Random();
        b = generarClavePrivada(p, rnd);
        
        // Calcular clave pública B = g^b mod p
        B = g.modPow(b, p);
        
        // Mostrar resultados
        System.out.println("\n--- CLAVES DE BOB ---");
        System.out.println("b (SECRETO de Bob):   " + b);
        System.out.println("B (clave publica):    " + B);
        System.out.println("\n Bob ha generado sus claves exitosamente.");
        System.out.println("  El valor B puede compartirse publicamente con Alice.");
    }
    
    /**
     * Genera una clave privada aleatoria en el rango [2, p-1]
     * @param p El número primo
     * @param rnd Generador de números aleatorios
     * @return Clave privada en el rango válido
     */
    private static BigInteger generarClavePrivada(BigInteger p, Random rnd) {
        BigInteger pMinusOne = p.subtract(ONE);
        BigInteger clavePrivada;
        
        do {
            // Genera un número aleatorio con la misma cantidad de bits que p
            clavePrivada = new BigInteger(p.bitLength(), rnd);
            
            // Verificar que esté en el rango [2, p-1]
        } while (clavePrivada.compareTo(TWO) < 0 || clavePrivada.compareTo(pMinusOne) > 0);
        
        return clavePrivada;
    }
}