import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZWCompress {
    
    public static List<Integer> lzwCompress(String data, int maxDiccionario) {
        // Crear el diccionario inicial con todos los caracteres únicos
        Map<String, Integer> diccionario = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            diccionario.put(String.valueOf((char) i), i);
        }
        int codigoActual = 256; // Siguiente código disponible
        
        // Variables de estado
        String W = "";
        List<Integer> resultado = new ArrayList<>();
        
        // Iterar sobre cada símbolo en la entrada
        for (char C : data.toCharArray()) {
            String WC = W + C;
            if (diccionario.containsKey(WC)) {
                W = WC; // Si W + C está en el diccionario, expandir W
            } else {
                // Emitir el código de W
                resultado.add(diccionario.get(W));
                
                if (codigoActual < maxDiccionario) {
                    // Agregar W + C al diccionario
                    diccionario.put(WC, codigoActual);
                    codigoActual++;
                }
                
                // Establecer W como el nuevo símbolo actual
                W = String.valueOf(C);
            }
        }
        
        // Emitir el último código de W
        if (!W.isEmpty()) {
            resultado.add(diccionario.get(W));
        }
        
        return resultado;
    }
    
    public static void main(String[] args) {
        // Ejemplo de uso
        String data = "abracadabrapatasdecabrarrarrad";
        
        // Puedes usar el texto largo descomentando la siguiente línea
        // String data = "abracadabrapatasdecabrarrarrad the_cat_sat_on_the_mat...";
        
        List<Integer> compressedData = lzwCompress(data, 4092);
        System.out.println("Datos comprimidos: " + compressedData);
        
        // Escribir resultado en archivo
        try (FileWriter writer = new FileWriter("output.txt")) {
            for (Integer item : compressedData) {
                writer.write(item + "\n");
            }
            System.out.println("Datos escritos en output.txt");
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
        
        // Mostrar directorio actual
        System.out.println("Directorio actual: " + System.getProperty("user.dir"));
    }
}