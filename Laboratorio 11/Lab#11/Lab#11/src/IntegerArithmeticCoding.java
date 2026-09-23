import java.util.*;

public class IntegerArithmeticCoding {
    
    // Clase para representar un símbolo con sus frecuencias
    static class Symbol {
        char character;
        int frequency;
        int cumulativeFreq;
        
        public Symbol(char character, int frequency, int cumulativeFreq) {
            this.character = character;
            this.frequency = frequency;
            this.cumulativeFreq = cumulativeFreq;
        }
    }
    
    // Tabla de símbolos y sus frecuencias
    private Map<Character, Symbol> symbolTable;
    private int totalFrequency;
    private List<Integer> outputBits;
    
    public IntegerArithmeticCoding() {
        symbolTable = new HashMap<>();
        outputBits = new ArrayList<>();
    }
    
    /**
     * Inicializa la tabla de símbolos con sus frecuencias
     * @param frequencies Map con el símbolo y su frecuencia
     */
    public void initializeSymbolTable(Map<Character, Integer> frequencies) {
        int cumulative = 0;
        totalFrequency = 0;
        
        // Calcular frecuencia total
        for (int freq : frequencies.values()) {
            totalFrequency += freq;
        }
        
        // Crear tabla de símbolos con frecuencias acumuladas
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            symbolTable.put(entry.getKey(), 
                new Symbol(entry.getKey(), entry.getValue(), cumulative));
            cumulative += entry.getValue();
        }
    }
    
    /**
     * Escribe un bit a la salida
     * @param bit el bit a escribir (0 o 1)
     */
    private void writeBit(int bit) {
        outputBits.add(bit);
        System.out.print(bit);
    }
    
    /**
     * Obtiene la frecuencia acumulada del símbolo v
     * @param v el símbolo
     * @return frecuencia acumulada hasta v (no inclusive)
     */
    private int getFi(char v) {
        return symbolTable.get(v).cumulativeFreq;
    }
    
    /**
     * Obtiene la frecuencia acumulada del siguiente símbolo
     * @param v el símbolo
     * @return frecuencia acumulada hasta v+1 (inclusive de v)
     */
    private int getFiPlusOne(char v) {
        Symbol symbol = symbolTable.get(v);
        return symbol.cumulativeFreq + symbol.frequency;
    }
    
    /**
     * Codifica el mensaje usando codificación aritmética entera
     * @param message el mensaje a codificar
     * @param k número de bits de precisión
     */
    public void encode(String message, int k) {
        long R = (long) Math.pow(2, k);  // Rango total [0, R-1]
        long l = 0;                       // Límite inferior
        long u = R - 1;                   // Límite superior
        int m = 0;                        // Contador de underflow
        int n = message.length();         // Tamaño del mensaje
        
        System.out.println("Codificando mensaje: " + message);
        System.out.println("k = " + k + ", R = " + R + ", T = " + totalFrequency);
        System.out.println("\nVerificacion: R > 4*T? " + R + " > " + (4 * totalFrequency) + " = " + (R > 4 * totalFrequency));
        System.out.println("\nBits de salida: ");
        
        // Procesar cada símbolo del mensaje
        for (int i = 0; i < n; i++) {
            char vi = message.charAt(i);
            long s = u - l + 1;  // Tamaño actual del intervalo
            
            // Actualizar límites del intervalo
            long newU = l + (s * getFiPlusOne(vi)) / totalFrequency - 1;
            long newL = l + (s * getFi(vi)) / totalFrequency;
            
            u = newU;
            l = newL;
            
            System.out.println("\n--- Simbolo " + (i+1) + ": '" + vi + "' ---");
            System.out.println("Intervalo: [" + l + ", " + u + "], s = " + s);
            
            // Renormalización
            while (true) {
                if (l >= R / 2) {
                    // Intervalo en mitad superior
                    writeBit(1);
                    u = 2 * u - R + 1;
                    l = 2 * l - R;
                    
                    // Emitir bits de underflow diferidos como 0
                    for (int j = 0; j < m; j++) {
                        writeBit(0);
                    }
                    m = 0;
                    System.out.println(" [Mitad superior: emitio 1]");
                    
                } else if (u < R / 2) {
                    // Intervalo en mitad inferior
                    writeBit(0);
                    u = 2 * u + 1;
                    l = 2 * l;
                    
                    // Emitir bits de underflow diferidos como 1
                    for (int j = 0; j < m; j++) {
                        writeBit(1);
                    }
                    m = 0;
                    System.out.println(" [Mitad inferior: emitio 0]");
                    
                } else if (l >= R / 4 && u < 3 * R / 4) {
                    // Intervalo en mitad intermedia (underflow)
                    u = 2 * u - R / 2 + 1;
                    l = 2 * l - R / 2;
                    m++;
                    System.out.println(" [Underflow: m = " + m + "]");
                    
                } else {
                    // Intervalo bien posicionado
                    break;
                }
            }
        }
        
        // Terminación: generar bits finales
        System.out.println("\n--- Terminacion ---");
        if (l >= R / 4) {
            writeBit(1);
            for (int j = 0; j < m; j++) {
                writeBit(0);
            }
            writeBit(0);
        } else {
            writeBit(0);
            for (int j = 0; j < m; j++) {
                writeBit(1);
            }
            writeBit(1);
        }
        
        System.out.println("\n\nCodificacion completada.");
        System.out.println("Total de bits: " + outputBits.size());
    }
    
    /**
     * Obtiene los bits de salida como String
     */
    public String getOutputBitsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int bit : outputBits) {
            sb.append(bit);
        }
        return sb.toString();
    }
    
    /**
     * Método principal de ejemplo
     */
    public static void main(String[] args) {
        IntegerArithmeticCoding codec = new IntegerArithmeticCoding();
        
        // Definir frecuencias según tu ejemplo
        Map<Character, Integer> frequencies = new LinkedHashMap<>();
        frequencies.put('1', 1);   // f(1) = 1,  fi(1) = 0
        frequencies.put('2', 10);  // f(2) = 10, fi(2) = 1
        frequencies.put('3', 20);  // f(3) = 20, fi(3) = 11
        // Total T = 31
        
        codec.initializeSymbolTable(frequencies);
        
        // Codificar el mensaje "3212"
        String message = "3212";
        int k = 8;  // 8 bits de precisión
        
        codec.encode(message, k);
        
        System.out.println("\n\nCodigo binario final: " + codec.getOutputBitsAsString());
    }
}