import java.io.*;
import java.util.*;

/**
 * Clase principal para demostrar compresión básica con manejo de bits
 * Implementa dos enfoques: StringBuilder y operaciones de bits reales
 */
public class BitCompression {
    
    // Mapas para codificación/decodificación con códigos fijos de 2 bits
    private static final Map<Character, String> ENCODE_MAP = new HashMap<>();
    private static final Map<String, Character> DECODE_MAP = new HashMap<>();
    
    // Inicialización de los mapas de codificación
    static {
        ENCODE_MAP.put('a', "00");
        ENCODE_MAP.put('b', "01");
        ENCODE_MAP.put('c', "10");
        ENCODE_MAP.put('d', "11");
        
        // Crear el mapa inverso para decodificación
        for (Map.Entry<Character, String> entry : ENCODE_MAP.entrySet()) {
            DECODE_MAP.put(entry.getValue(), entry.getKey());
        }
    }
    
    public static void main(String[] args) {
        String textoOriginal = "abcdabcd";
        System.out.println("Texto original: " + textoOriginal);
        System.out.println("=====================================");
        
        // Parte 1: Usando StringBuilder
        System.out.println("PARTE 1 - Bits como texto (StringBuilder)");
        testStringBuilderApproach(textoOriginal);
        
        System.out.println("\n=====================================");
        
        // Parte 2: Usando operaciones de bits reales
        System.out.println("PARTE 2 - Bits reales (operadores de bits)");
        testBitOperationsApproach(textoOriginal);
    }
    
    /**
     * PARTE 1: Implementación usando StringBuilder
     */
    public static void testStringBuilderApproach(String texto) {
        try {
            // 1. Codificar usando StringBuilder
            String bitString = encodeWithStringBuilder(texto);
            System.out.println("Texto codificado en bits: " + bitString);
            
            // 2. Convertir string de bits a bytes
            byte[] bytes = bitStringToBytes(bitString);
            System.out.println("Bytes generados: " + Arrays.toString(bytes));
            System.out.println("Tamaño original: " + texto.length() + " caracteres");
            System.out.println("Tamaño comprimido: " + bytes.length + " bytes");
            
            // 3. Guardar en archivo binario
            String filename = "compressed_sb.bin";
            saveToFile(bytes, filename);
            System.out.println("Archivo guardado: " + filename);
            
            // 4. Leer desde archivo y decodificar
            byte[] readBytes = readFromFile(filename);
            String recoveredBitString = bytesToBitString(readBytes, bitString.length());
            String recoveredText = decodeFromStringBuilder(recoveredBitString);
            
            System.out.println("Bits recuperados: " + recoveredBitString);
            System.out.println("Texto recuperado: " + recoveredText);
            System.out.println("¿Coincide con original? " + texto.equals(recoveredText));
            
        } catch (IOException e) {
            System.err.println("Error en operaciones de archivo: " + e.getMessage());
        }
    }
    
    /**
     * PARTE 2: Implementación usando operaciones de bits reales
     */
    public static void testBitOperationsApproach(String texto) {
        try {
            // 1. Codificar usando operaciones de bits
            byte[] compressedBytes = encodeWithBitOperations(texto);
            System.out.println("Bytes comprimidos: " + Arrays.toString(compressedBytes));
            System.out.println("Tamaño original: " + texto.length() + " caracteres");
            System.out.println("Tamaño comprimido: " + compressedBytes.length + " bytes");
            
            // 2. Guardar en archivo binario
            String filename = "compressed_bits.bin";
            saveToFile(compressedBytes, filename);
            System.out.println("Archivo guardado: " + filename);
            
            // 3. Leer y decodificar
            byte[] readBytes = readFromFile(filename);
            String recoveredText = decodeWithBitOperations(readBytes, texto.length());
            
            System.out.println("Texto recuperado: " + recoveredText);
            System.out.println("¿Coincide con original? " + texto.equals(recoveredText));
            
        } catch (IOException e) {
            System.err.println("Error en operaciones de archivo: " + e.getMessage());
        }
    }
    
    // =================== MÉTODOS PARTE 1 (StringBuilder) ===================
    
    /**
     * Codifica texto usando StringBuilder para concatenar bits como string
     */
    public static String encodeWithStringBuilder(String texto) {
        StringBuilder bitString = new StringBuilder();
        
        for (char c : texto.toCharArray()) {
            String code = ENCODE_MAP.get(c);
            if (code != null) {
                bitString.append(code);
            } else {
                throw new IllegalArgumentException("Carácter no soportado: " + c);
            }
        }
        
        return bitString.toString();
    }
    
    /**
     * Decodifica desde string de bits usando StringBuilder
     */
    public static String decodeFromStringBuilder(String bitString) {
        StringBuilder texto = new StringBuilder();
        
        // Procesar de 2 en 2 bits (código fijo de 2 bits)
        for (int i = 0; i < bitString.length(); i += 2) {
            if (i + 1 < bitString.length()) {
                String code = bitString.substring(i, i + 2);
                Character c = DECODE_MAP.get(code);
                if (c != null) {
                    texto.append(c);
                } else {
                    throw new IllegalArgumentException("Código no válido: " + code);
                }
            }
        }
        
        return texto.toString();
    }
    
    /**
     * Convierte string de bits a array de bytes
     */
    public static byte[] bitStringToBytes(String bitString) {
        // Agregar padding si es necesario para completar bytes
        int padding = 8 - (bitString.length() % 8);
        if (padding != 8) {
            bitString += "0".repeat(padding);
        }
        
        byte[] bytes = new byte[bitString.length() / 8];
        
        for (int i = 0; i < bytes.length; i++) {
            String byteString = bitString.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteString, 2);
        }
        
        return bytes;
    }
    
    /**
     * Convierte array de bytes a string de bits
     */
    public static String bytesToBitString(byte[] bytes, int originalLength) {
        StringBuilder bitString = new StringBuilder();
        
        for (byte b : bytes) {
            // Convertir byte a string de 8 bits, manejando signos
            String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            bitString.append(bits);
        }
        
        // Recortar al tamaño original (quitar padding)
        return bitString.substring(0, originalLength);
    }
    
    // =================== MÉTODOS PARTE 2 (Operaciones de Bits) ===================
    
    /**
     * Codifica texto usando operaciones de bits reales
     */
    public static byte[] encodeWithBitOperations(String texto) {
        // Calcular tamaño necesario: 2 bits por carácter
        int totalBits = texto.length() * 2;
        int bytesNeeded = (totalBits + 7) / 8; // Redondear hacia arriba
        
        byte[] result = new byte[bytesNeeded];
        int bitPosition = 0;
        
        for (char c : texto.toCharArray()) {
            int code = getCharCode(c);
            
            // Insertar 2 bits en el array de bytes
            for (int i = 1; i >= 0; i--) { // 2 bits, del más significativo al menos
                int bit = (code >> i) & 1;
                setBit(result, bitPosition, bit);
                bitPosition++;
            }
        }
        
        return result;
    }
    
    /**
     * Decodifica desde bytes usando operaciones de bits reales
     */
    public static String decodeWithBitOperations(byte[] bytes, int originalLength) {
        StringBuilder texto = new StringBuilder();
        int bitPosition = 0;
        
        for (int i = 0; i < originalLength; i++) {
            // Leer 2 bits para formar el código del carácter
            int code = 0;
            for (int j = 1; j >= 0; j--) { // 2 bits
                int bit = getBit(bytes, bitPosition);
                code |= (bit << j);
                bitPosition++;
            }
            
            char c = getCharFromCode(code);
            texto.append(c);
        }
        
        return texto.toString();
    }
    
    /**
     * Obtiene el código de 2 bits para un carácter
     */
    private static int getCharCode(char c) {
        switch (c) {
            case 'a': return 0; // 00
            case 'b': return 1; // 01
            case 'c': return 2; // 10
            case 'd': return 3; // 11
            default: throw new IllegalArgumentException("Carácter no soportado: " + c);
        }
    }
    
    /**
     * Obtiene el carácter desde un código de 2 bits
     */
    private static char getCharFromCode(int code) {
        switch (code) {
            case 0: return 'a'; // 00
            case 1: return 'b'; // 01
            case 2: return 'c'; // 10
            case 3: return 'd'; // 11
            default: throw new IllegalArgumentException("Código no válido: " + code);
        }
    }
    
    /**
     * Establece un bit específico en un array de bytes
     */
    private static void setBit(byte[] bytes, int bitPosition, int bit) {
        int byteIndex = bitPosition / 8;
        int bitIndex = 7 - (bitPosition % 8); // Bit más significativo primero
        
        if (bit == 1) {
            bytes[byteIndex] |= (1 << bitIndex); // Establecer bit
        } else {
            bytes[byteIndex] &= ~(1 << bitIndex); // Limpiar bit
        }
    }
    
    /**
     * Obtiene un bit específico de un array de bytes
     */
    private static int getBit(byte[] bytes, int bitPosition) {
        int byteIndex = bitPosition / 8;
        int bitIndex = 7 - (bitPosition % 8); // Bit más significativo primero
        
        return (bytes[byteIndex] >> bitIndex) & 1;
    }
    
    // =================== MÉTODOS DE ARCHIVO ===================
    
    /**
     * Guarda bytes en archivo binario
     */
    public static void saveToFile(byte[] data, String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data);
        }
    }
    
    /**
     * Lee bytes desde archivo binario
     */
    public static byte[] readFromFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fis.readAllBytes();
        }
    }
    
    // =================== MÉTODOS AUXILIARES ===================
    
    /**
     * Muestra información detallada sobre la compresión
     */
    public static void showCompressionStats(String original, byte[] compressed) {
        System.out.println("\n--- Estadísticas de Compresión ---");
        System.out.println("Texto original: \"" + original + "\"");
        System.out.println("Longitud original: " + original.length() + " caracteres");
        System.out.println("Tamaño sin comprimir: " + (original.length() * 2) + " bytes (UTF-16)");
        System.out.println("Tamaño comprimido: " + compressed.length + " bytes");
        
        double ratio = (double) compressed.length / (original.length() * 2) * 100;
        System.out.printf("Ratio de compresión: %.2f%%\n", ratio);
        System.out.printf("Ahorro de espacio: %.2f%%\n", 100 - ratio);
    }
}
