import java.io.*;
import java.util.*;

public class AnalizadorTexto {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== ANALIZADOR DE FRECUENCIA Y ENTROPIA ===");
        System.out.print("Ingrese la ruta del archivo a analizar: ");
        String nombreArchivo = scanner.nextLine().trim();
        
        // Verificar que se ingresó una ruta
        if (nombreArchivo.isEmpty()) {
            System.err.println("Error: Debe ingresar una ruta de archivo valida.");
            scanner.close();
            return;
        }
        
        try {
            // Verificar si el archivo existe
            File archivo = new File(nombreArchivo);
            if (!archivo.exists()) {
                System.err.println("Error: El archivo '" + nombreArchivo + "' no existe.");
                scanner.close();
                return;
            }
            
            if (!archivo.isFile()) {
                System.err.println("Error: '" + nombreArchivo + "' no es un archivo valido.");
                scanner.close();
                return;
            }
            
            System.out.println("\nAnalizando archivo: " + nombreArchivo);
            System.out.println("Tamano del archivo: " + archivo.length() + " bytes");
            System.out.println("Procesando...\n");
            
            // HashMap para almacenar frecuencias
            HashMap<Character, Integer> frecuencias = new HashMap<>();
            
            // Leer archivo y calcular frecuencias
            BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo));
            int caracter;
            
            while ((caracter = reader.read()) != -1) {
                char c = (char) caracter;
                
                // Solo considerar letras
                if (Character.isLetter(c)) {
                    // Convertir a minúscula para normalizar
                    c = Character.toLowerCase(c);
                    
                    // Incrementar contador en el HashMap
                    frecuencias.put(c, frecuencias.getOrDefault(c, 0) + 1);
                }
            }
            reader.close();
            
            if (frecuencias.isEmpty()) {
                System.out.println("El archivo no contiene letras para analizar.");
            } else {
                // Mostrar tabla de frecuencias
                System.out.println("=== TABLA DE FRECUENCIAS ===");
                System.out.println("Caracter | Frecuencia | Frecuencia Relativa");
                System.out.println("---------|------------|-------------------");
                
                // Calcular total de caracteres
                int totalCaracteres = 0;
                for (int freq : frecuencias.values()) {
                    totalCaracteres += freq;
                }
                
                // Mostrar frecuencias ordenadas por carácter
                TreeMap<Character, Integer> frecuenciasOrdenadas = new TreeMap<>(frecuencias);
                for (Map.Entry<Character, Integer> entry : frecuenciasOrdenadas.entrySet()) {
                    char letra = entry.getKey();
                    int frecuencia = entry.getValue();
                    double frecuenciaRelativa = (double) frecuencia / totalCaracteres;
                    
                    System.out.printf("    %c    |     %d      |      %.4f\n", 
                        letra, frecuencia, frecuenciaRelativa);
                }
                
                System.out.println("---------|------------|-------------------");
                System.out.println("Total caracteres: " + totalCaracteres);
                
                // Calcular entropía de Shannon
                double entropia = 0.0;
                for (int frecuencia : frecuencias.values()) {
                    if (frecuencia > 0) {
                        // Calcular probabilidad
                        double probabilidad = (double) frecuencia / totalCaracteres;
                        
                        // Entropía de Shannon: H = -Σ(p * log2(p))
                        entropia -= probabilidad * (Math.log(probabilidad) / Math.log(2));
                    }
                }
                
                System.out.println("\nEntropia: " + String.format("%.4f", entropia) + " bits");
            }
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}