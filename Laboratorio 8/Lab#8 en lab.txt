import java.io.*;
import java.util.*;

/**
 * Clase para compresión con código variable y soporte para hasta 200 caracteres
 * Implementa codificación de longitud variable basada en frecuencias
 */
public class VariableLengthCompression {
    
    // Estructura para almacenar información de codificación
    private static class CodeInfo {
        String code;
        int frequency;
        
        CodeInfo(String code, int frequency) {
            this.code = code;
            this.frequency = frequency;
        }
    }
    
    // Mapas dinámicos para codificación/decodificación
    private Map<Character, CodeInfo> encodeMap = new HashMap<>();
    private Map<String, Character> decodeMap = new HashMap<>();
    
    // Conjunto de caracteres soportados (hasta 200)
    private static final String SUPPORTED_CHARS = 
        "abcdefghijklmnopqrstuvwxyz" +
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "0123456789" +
        " .,;:!?()[]{}\"'-+*/=<>@#$%&_|\\^`~" + //Hay mas acaracteres extras pero empiezan a dar error en el programa recordate de eso
        "áéíóúñüÁÉÍÓÚÑÜ"; // Caracteres especiales adicionales
    
    public static void main(String[] args) {
        VariableLengthCompression compressor = new VariableLengthCompression();
        
        // Texto de prueba con diferentes frecuencias
        String textoOriginal = "aaaaabbbbcccddeeffgghhiijjkkllmmnnooppqqrrssttuuvvwwxxyyzz " +
                              "AABBCCDD 123456 .,;:!? Texto con frecuencias variables para " +
                              "demostrar la eficiencia del código variable en compresión.";
        
        System.out.println("Texto original: " + textoOriginal);
        System.out.println("Longitud: " + textoOriginal.length() + " caracteres");
        System.out.println("Caracteres únicos: " + getUniqueCharCount(textoOriginal));
        System.out.println("=====================================");
        
        // Parte 1: StringBuilder con código variable
        System.out.println("PARTE 1 - Código variable con StringBuilder");
        compressor.testVariableStringBuilderApproach(textoOriginal);
        
        System.out.println("\n=====================================");
        
        // Parte 2: Operaciones de bits reales con código variable
        System.out.println("PARTE 2 - Código variable con operaciones de bits");
        compressor.testVariableBitOperationsApproach(textoOriginal);
        
        System.out.println("\n=====================================");
        
        // Demostración con texto más largo
        System.out.println("DEMOSTRACIÓN CON TEXTO EXTENSO");
        String textoLargo = generateLongText();
        System.out.println("Texto largo generado: " + textoLargo.length() + " caracteres");
        compressor.testVariableBitOperationsApproach(textoLargo);
    }
    
    /**
     * PARTE 1: Implementación con StringBuilder y código variable
     */
    public void testVariableStringBuilderApproach(String texto) {
        try {
            // 1. Analizar frecuencias y generar códigos variables
            generateVariableCodes(texto);
            showCodeTable();
            
            // 2. Codificar usando StringBuilder
            String bitString = encodeWithVariableStringBuilder(texto);
            System.out.println("Bits totales generados: " + bitString.length());
            System.out.println("Primeros 100 bits: " + 
                (bitString.length() > 100 ? bitString.substring(0, 100) + "..." : bitString));
            
            // 3. Convertir a bytes y guardar
            byte[] bytes = bitStringToBytes(bitString);
            String filename = "compressed_variable_sb.bin";
            saveToFile(bytes, filename, bitString.length());
            
            System.out.println("Archivo guardado: " + filename);
            showCompressionStats(texto, bytes);
            
            // 4. Leer y decodificar
            CompressedData data = readFromFile(filename);
            String recoveredBitString = bytesToBitString(data.bytes, data.originalBitLength);
            String recoveredText = decodeFromVariableStringBuilder(recoveredBitString);
            
            System.out.println("¿Recuperación exitosa? " + texto.equals(recoveredText));
            if (!texto.equals(recoveredText)) {
                System.out.println("ORIGINAL: " + texto.substring(0, Math.min(50, texto.length())));
                System.out.println("RECUPERD: " + recoveredText.substring(0, Math.min(50, recoveredText.length())));
            }
            
        } catch (IOException e) {
            System.err.println("Error en operaciones de archivo: " + e.getMessage());
        }
    }
    
    /**
     * PARTE 2: Implementación con operaciones de bits y código variable
     */
    public void testVariableBitOperationsApproach(String texto) {
        try {
            // 1. Generar códigos variables
            generateVariableCodes(texto);
            
            // 2. Codificar usando operaciones de bits
            byte[] compressedBytes = encodeWithVariableBitOperations(texto);
            String filename = "compressed_variable_bits.bin";
            
            // Guardamos también la información de longitud original
            saveCompressedData(compressedBytes, texto.length(), filename);
            
            System.out.println("Archivo guardado: " + filename);
            showCompressionStats(texto, compressedBytes);
            
            // 3. Leer y decodificar
            CompressedDataWithLength data = readCompressedData(filename);
            String recoveredText = decodeWithVariableBitOperations(data.bytes, data.originalLength);
            
            System.out.println("¿Recuperación exitosa? " + texto.equals(recoveredText));
            
        } catch (IOException e) {
            System.err.println("Error en operaciones de archivo: " + e.getMessage());
        }
    }
    
    // =================== GENERACIÓN DE CÓDIGOS VARIABLES ===================
    
    /**
     * Genera códigos variables basados en frecuencia de caracteres
     * Usa un enfoque simple: códigos más cortos para caracteres más frecuentes
     */
    public void generateVariableCodes(String texto) {
        // 1. Contar frecuencias
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char c : texto.toCharArray()) {
            if (SUPPORTED_CHARS.indexOf(c) == -1) {
                throw new IllegalArgumentException("Carácter no soportado: '" + c + "' (código: " + (int)c + ")");
            }
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }
        
        // 2. Crear lista ordenada por frecuencia (descendente)
        List<Map.Entry<Character, Integer>> sortedChars = new ArrayList<>(frequencies.entrySet());
        sortedChars.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // 3. Asignar códigos variables
        encodeMap.clear();
        decodeMap.clear();
        
        int codeValue = 0;
        int currentLength = 1;
        int charsAtCurrentLength = 0;
        int maxCharsAtCurrentLength = (int) Math.pow(2, currentLength);
        
        for (Map.Entry<Character, Integer> entry : sortedChars) {
            char c = entry.getKey();
            int freq = entry.getValue();
            
            // Si hemos asignado todos los códigos posibles de la longitud actual,
            // incrementar la longitud
            if (charsAtCurrentLength >= maxCharsAtCurrentLength) {
                currentLength++;
                maxCharsAtCurrentLength = (int) Math.pow(2, currentLength);
                charsAtCurrentLength = 0;
            }
            
            // Generar código binario de longitud actual
            String code = String.format("%" + currentLength + "s", 
                Integer.toBinaryString(codeValue)).replace(' ', '0');
            
            encodeMap.put(c, new CodeInfo(code, freq));
            decodeMap.put(code, c);
            
            codeValue++;
            charsAtCurrentLength++;
            
            // Resetear codeValue si cambiamos de longitud
            if (charsAtCurrentLength == 0) {
                codeValue = 0;
            }
        }
    }
    
    // =================== MÉTODOS PARTE 1 (StringBuilder Variable) ===================
    
    public String encodeWithVariableStringBuilder(String texto) {
        StringBuilder bitString = new StringBuilder();
        
        for (char c : texto.toCharArray()) {
            CodeInfo info = encodeMap.get(c);
            if (info != null) {
                bitString.append(info.code);
            } else {
                throw new IllegalArgumentException("Carácter no codificado: " + c);
            }
        }
        
        return bitString.toString();
    }
    
    public String decodeFromVariableStringBuilder(String bitString) {
        StringBuilder texto = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        
        for (char bit : bitString.toCharArray()) {
            currentCode.append(bit);
            String code = currentCode.toString();
            
            if (decodeMap.containsKey(code)) {
                texto.append(decodeMap.get(code));
                currentCode.setLength(0); // Limpiar el código actual
            }
        }
        
        return texto.toString();
    }
    
    // =================== MÉTODOS PARTE 2 (Operaciones de Bits Variable) ===================
    
    public byte[] encodeWithVariableBitOperations(String texto) {
        // Primero calculamos el tamaño total en bits
        int totalBits = 0;
        for (char c : texto.toCharArray()) {
            CodeInfo info = encodeMap.get(c);
            if (info != null) {
                totalBits += info.code.length();
            }
        }
        
        int bytesNeeded = (totalBits + 7) / 8;
        byte[] result = new byte[bytesNeeded];
        int bitPosition = 0;
        
        for (char c : texto.toCharArray()) {
            CodeInfo info = encodeMap.get(c);
            if (info != null) {
                String code = info.code;
                
                // Escribir cada bit del código
                for (char bitChar : code.toCharArray()) {
                    int bit = bitChar - '0';
                    setBit(result, bitPosition, bit);
                    bitPosition++;
                }
            }
        }
        
        return result;
    }
    
    public String decodeWithVariableBitOperations(byte[] bytes, int originalLength) {
        StringBuilder texto = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        int bitPosition = 0;
        
        // Necesitamos conocer cuántos bits leer en total
        int totalBits = calculateTotalBitsForDecoding(bytes);
        
        while (bitPosition < totalBits && texto.length() < originalLength) {
            int bit = getBit(bytes, bitPosition);
            currentCode.append(bit);
            bitPosition++;
            
            String code = currentCode.toString();
            if (decodeMap.containsKey(code)) {
                texto.append(decodeMap.get(code));
                currentCode.setLength(0);
            }
        }
        
        return texto.toString();
    }
    
    private int calculateTotalBitsForDecoding(byte[] bytes) {
        return bytes.length * 8;
    }
    
    // =================== OPERACIONES DE BITS ===================
    
    private void setBit(byte[] bytes, int bitPosition, int bit) {
        int byteIndex = bitPosition / 8;
        int bitIndex = 7 - (bitPosition % 8);
        
        if (byteIndex >= bytes.length) return;
        
        if (bit == 1) {
            bytes[byteIndex] |= (1 << bitIndex);
        } else {
            bytes[byteIndex] &= ~(1 << bitIndex);
        }
    }
    
    private int getBit(byte[] bytes, int bitPosition) {
        int byteIndex = bitPosition / 8;
        int bitIndex = 7 - (bitPosition % 8);
        
        if (byteIndex >= bytes.length) return 0;
        
        return (bytes[byteIndex] >> bitIndex) & 1;
    }
    
    // =================== MANEJO DE ARCHIVOS MEJORADO ===================
    
    private static class CompressedData {
        byte[] bytes;
        int originalBitLength;
        
        CompressedData(byte[] bytes, int originalBitLength) {
            this.bytes = bytes;
            this.originalBitLength = originalBitLength;
        }
    }
    
    private static class CompressedDataWithLength {
        byte[] bytes;
        int originalLength;
        
        CompressedDataWithLength(byte[] bytes, int originalLength) {
            this.bytes = bytes;
            this.originalLength = originalLength;
        }
    }
    
    public void saveToFile(byte[] data, String filename, int originalBitLength) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            dos.writeInt(originalBitLength); // Guardar longitud original en bits
            dos.write(data);
        }
    }
    
    public CompressedData readFromFile(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int originalBitLength = dis.readInt();
            byte[] data = dis.readAllBytes();
            return new CompressedData(data, originalBitLength);
        }
    }
    
    public void saveCompressedData(byte[] data, int originalLength, String filename) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            dos.writeInt(originalLength); // Longitud original en caracteres
            dos.write(data);
        }
    }
    
    public CompressedDataWithLength readCompressedData(String filename) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int originalLength = dis.readInt();
            byte[] data = dis.readAllBytes();
            return new CompressedDataWithLength(data, originalLength);
        }
    }
    
    // =================== MÉTODOS AUXILIARES ===================
    
    public byte[] bitStringToBytes(String bitString) {
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
    
    public String bytesToBitString(byte[] bytes, int originalLength) {
        StringBuilder bitString = new StringBuilder();
        
        for (byte b : bytes) {
            String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            bitString.append(bits);
        }
        
        return bitString.substring(0, Math.min(originalLength, bitString.length()));
    }
    
    public void showCodeTable() {
        System.out.println("\n--- Tabla de Códigos Variables ---");
        List<Map.Entry<Character, CodeInfo>> entries = new ArrayList<>(encodeMap.entrySet());
        entries.sort((a, b) -> b.getValue().frequency - a.getValue().frequency);
        
        System.out.println("Carácter | Código | Frecuencia | Bits ahorrados");
        System.out.println("---------|--------|------------|---------------");
        
        for (Map.Entry<Character, CodeInfo> entry : entries.subList(0, Math.min(10, entries.size()))) {
            char c = entry.getKey();
            CodeInfo info = entry.getValue();
            String displayChar = (c == ' ') ? "SPACE" : String.valueOf(c);
            int bitsFixed = 8; // UTF-8 básico
            int bitsSaved = (bitsFixed - info.code.length()) * info.frequency;
            
            System.out.printf("   %-6s | %-6s | %10d | %13d%n", 
                displayChar, info.code, info.frequency, bitsSaved);
        }
        if (entries.size() > 10) {
            System.out.println("... y " + (entries.size() - 10) + " caracteres más");
        }
    }
    
    public void showCompressionStats(String original, byte[] compressed) {
        System.out.println("\n--- Estadísticas de Compresión ---");
        System.out.println("Longitud original: " + original.length() + " caracteres");
        System.out.println("Tamaño sin comprimir: " + (original.length() * 1) + " bytes (ASCII)");
        System.out.println("Tamaño comprimido: " + compressed.length + " bytes");
        
        double ratio = ((double) compressed.length / (double) original.length()) * 100.0;
        System.out.printf("Ratio de compresión: %.2f%%\n", ratio);
        System.out.printf("Ahorro de espacio: %.2f%%\n", 100.0 - ratio);
        
        // Calcular bits promedio por carácter
        double avgBitsPerChar = (compressed.length * 8.0) / (double) original.length();
        System.out.printf("Bits promedio por carácter: %.2f\n", avgBitsPerChar);
    }
    
    private static int getUniqueCharCount(String text) {
        return (int) text.chars().distinct().count();
    }
    
    private static String generateLongText() {
        StringBuilder sb = new StringBuilder();
        String[] words = {"el", "la", "de", "que", "y", "en", "un", "es", "se", "no", "te", "lo", 
                         "le", "da", "su", "por", "son", "con", "para", "como", "está", "todo", 
                         "una", "sobre", "ser", "muy", "más", "fue", "han", "do", "mi", "qué"};
        
        Random random = new Random(42); // Seed fijo para reproducibilidad
        
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 5; j++) {
                sb.append(words[random.nextInt(words.length)]);
                if (j < 4) sb.append(" ");
            }
            sb.append(". ");
        }
        
        return sb.toString();
    }
}
