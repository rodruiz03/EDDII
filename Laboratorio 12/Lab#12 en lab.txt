import java.util.*;

public class LZ78Compressor {
    
    static class CompressedEntry {
        int index;
        char nextChar;
        
        CompressedEntry(int index, char nextChar) {
            this.index = index;
            this.nextChar = nextChar;
        }
        
        @Override
        public String toString() {
            return "(" + index + ", '" + nextChar + "')";
        }
    }
    
    public static List<CompressedEntry> lz78Compress(String inputText) {
        Map<String, Integer> dictionary = new HashMap<>();
        List<CompressedEntry> compressedData = new ArrayList<>();
        
        int i = 0;
        while (i < inputText.length()) {
            int j = i;
            
            // Buscar la longitud más larga de la subcadena que está en el diccionario
            while (j < inputText.length() - 1 && dictionary.containsKey(inputText.substring(i, j + 1))) {
                j++;
            }
            
            // Si hemos encontrado algo en el diccionario
            if (j > i) {
                // Se busca el siguiente carácter, se agrega el código y al diccionario
                char nextChar = (j < inputText.length()) ? inputText.charAt(j) : '\0';
                String substring = inputText.substring(i, j);
                compressedData.add(new CompressedEntry(dictionary.get(substring), nextChar));
                dictionary.put(inputText.substring(i, j + 1), dictionary.size() + 1);
                i = j + 1;
            } else {
                // Si no hay coincidencias, añadir el carácter actual
                char currentChar = inputText.charAt(i);
                compressedData.add(new CompressedEntry(0, currentChar));
                dictionary.put(String.valueOf(currentChar), dictionary.size() + 1);
                i++;
            }
        }
        
        return compressedData;
    }
    
    public static String lz78Decompress(List<CompressedEntry> compressedData) {
        List<String> dictionary = new ArrayList<>();
        StringBuilder decompressedData = new StringBuilder();
        
        for (CompressedEntry entry : compressedData) {
            if (entry.index == 0) {
                // Si el índice es 0, es un nuevo carácter
                decompressedData.append(entry.nextChar);
                dictionary.add(String.valueOf(entry.nextChar));
            } else {
                // Recuperar la entrada del diccionario
                String dictionaryEntry = dictionary.get(entry.index - 1);
                String newEntry = dictionaryEntry + entry.nextChar;
                decompressedData.append(newEntry);
                dictionary.add(newEntry);
            }
        }
        
        return decompressedData.toString();
    }
    
    public static double calcularNivelCompresion(String original, List<CompressedEntry> compressed) {
        // Tamaño original (en bytes, considerando 1 byte por carácter)
        int sizeOriginal = original.length();
        
        // Tamaño comprimido (aproximado: cada entrada ocupa 2 integers + 1 char)
        // Consideramos cada entrada como 2 integers (4 bytes c/u) + 1 char (2 bytes)
        int sizeCompressed = compressed.size() * 10; // 4 + 4 + 2 = 10 bytes por entrada
        
        // Calcular porcentaje de compresión
        double compressionRatio = ((double) sizeCompressed / sizeOriginal) * 100;
        double compressionLevel = 100 - compressionRatio;
        
        return compressionLevel;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Ingresa el texto a comprimir (separado por comas para multiples palabras): ");
        String input = scanner.nextLine();
        
        // Si el usuario ingresó palabras separadas por comas, las concatenamos
        String inputText = input.replace(",", "").replace(" ", "");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Texto original: " + inputText);
        System.out.println("Tamaño original: " + inputText.length() + " bytes");
        
        // Compresión
        List<CompressedEntry> compressed = lz78Compress(inputText);
        System.out.println("\nTexto comprimido: " + compressed);
        System.out.println("Tamaño comprimido: " + (compressed.size() * 10) + " bytes");
        
        // Calcular nivel de compresión
        double compressionLevel = calcularNivelCompresion(inputText, compressed);
        double compressionRatio = (100 - compressionLevel);
        
        System.out.println("\n" + "-".repeat(60));
        System.out.printf("Nivel de compresion: %.2f%%\n", compressionLevel);
        System.out.printf("Ratio de compresion: %.2f%%\n", compressionRatio);
        System.out.println("-".repeat(60));
        
        // Descompresión
        String decompressed = lz78Decompress(compressed);
        System.out.println("\nTexto descomprimido: " + decompressed);
        
        // Verificación
        if (inputText.equals(decompressed)) {
            System.out.println("\n Compresión y descompresion correctas");
        } else {
            System.out.println("\n Error en la compresion/descompresion");
        }
        System.out.println("=".repeat(60));
        
        scanner.close();
    }
}