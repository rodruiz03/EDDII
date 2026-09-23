import java.util.*;

/**
 * Clase HashUniversal que implementa una función hash universal
 * para números enteros y cadenas de texto
 */
public class HashUniversal {
    private int a, b;  // Parámetros de la función hash
    private int p;     // Número primo grande
    private int m;     // Tamaño de la tabla hash
    
    /**
     * Constructor que inicializa los parámetros de la función hash
     * @param m Tamaño de la tabla hash
     * @param p Número primo grande
     * @param a Parámetro a (debe cumplir: 1 ≤ a < p)
     * @param b Parámetro b (debe cumplir: 0 ≤ b < p)
     */
    public HashUniversal(int m, int p, int a, int b) {
        // Verificar rangos correctos
        if (a < 1 || a >= p) {
            throw new IllegalArgumentException("a debe estar en el rango [1, p-1]");
        }
        if (b < 0 || b >= p) {
            throw new IllegalArgumentException("b debe estar en el rango [0, p-1]");
        }
        if (m <= 0) {
            throw new IllegalArgumentException("m debe ser mayor que 0");
        }
        
        this.m = m;
        this.p = p;
        this.a = a;
        this.b = b;
    }
    
    /**
     * Calcula el hash de un número entero
     * @param key La clave numérica
     * @return El valor hash
     */
    public int hashInt(int key) {
        long hash = ((long)a * key + b) % p;
        
        // Si el resultado es negativo, sumamos p para corregirlo
        if (hash < 0) {
            hash = hash + p;
        }
        
        return (int)(hash % m);
    }
    
    /**
     * Calcula el hash de una cadena de texto
     * @param key La clave string
     * @return El valor hash
     */
    public int hashString(String key) {
        long value = 0;
        int base = 31;
        
        // Convertir la cadena a un número usando base 31
        for (int i = 0; i < key.length(); i++) {
            value = (value * base + (int)key.charAt(i)) % p;
        }
        
        // Aplicar la fórmula hash
        long hash = (a * value + b) % p;
        if (hash < 0) {
            hash = hash + p;
        }
        
        return (int)(hash % m);
    }
    
    /**
     * Método principal para probar la función hash
     */
    public static void main(String[] args) {
        // Parámetros definidos en el laboratorio
        int p = 104729;
        int m = 20;
        int a = 12345;
        int b = 67890;
        
        // Crear instancia de HashUniversal
        HashUniversal hashFunc = new HashUniversal(m, p, a, b);
        
        // Para almacenar resultados y análisis
        Map<Integer, Integer> claveHash = new HashMap<>();
        Map<Integer, List<Integer>> colisiones = new HashMap<>();
        
        System.out.println("=== CALCULO DE HASH PARA NUMEROS DE -50 A 50 ===");
        System.out.println("Parametros: p=" + p + ", m=" + m + ", a=" + a + ", b=" + b);
        System.out.println("\nClave\tHash");
        System.out.println("-----\t----");
        
        // Calcular hash para cada número de -50 a 50
        for (int key = -50; key <= 50; key++) {
            int hash = hashFunc.hashInt(key);
            claveHash.put(key, hash);
            
            // Agrupar por hash para detectar colisiones
            colisiones.computeIfAbsent(hash, k -> new ArrayList<>()).add(key);
            
            System.out.printf("%d\t%d\n", key, hash);
        }
        
        // Análisis de colisiones
        System.out.println("\n=== ANALISIS DE COLISIONES ===");
        
        int totalClaves = claveHash.size();
        int hashesUnicos = colisiones.size();
        int totalColisiones = totalClaves - hashesUnicos;
        
        System.out.println("Total claves: " + totalClaves);
        System.out.println("Hashes unicos: " + hashesUnicos);
        System.out.println("Colisiones: " + totalColisiones);
        
        // Mostrar qué claves colisionan
        System.out.println("\n=== CLAVES QUE COLISIONAN ===");
        for (Map.Entry<Integer, List<Integer>> entry : colisiones.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.printf("Hash %d: claves %s\n", entry.getKey(), entry.getValue());
            }
        }
        
        // Distribución de hashes
        System.out.println("\n=== DISTRIBUCION DE HASHES ===");
        for (int i = 0; i < m; i++) {
            List<Integer> clavesConEsteHash = colisiones.get(i);
            int count = (clavesConEsteHash != null) ? clavesConEsteHash.size() : 0;
            System.out.printf("Hash %d: %d claves\n", i, count);
        }
        

    }
}