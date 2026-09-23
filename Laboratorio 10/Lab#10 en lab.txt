import java.util.*;

public class CodificacionHuffman {
    
    // Clase Nodo del árbol de Huffman
    static class Nodo implements Comparable<Nodo> {
        char caracter;      // caracter (si es hoja)
        int frecuencia;     // frecuencia del caracter o suma de frecuencias
        Nodo izquierda;     // hijo izquierdo
        Nodo derecha;       // hijo derecho
        
        // Constructor para hoja
        Nodo(char caracter, int frecuencia) {
            this.caracter = caracter;
            this.frecuencia = frecuencia;
        }
        
        // Constructor para nodo interno
        Nodo(int frecuencia, Nodo izquierda, Nodo derecha) {
            this.caracter = '\0';  // nodo interno no tiene caracter
            this.frecuencia = frecuencia;
            this.izquierda = izquierda;
            this.derecha = derecha;
        }
        
        // Para que PriorityQueue ordene por frecuencia (menor primero)
        @Override
        public int compareTo(Nodo o) {
            return Integer.compare(this.frecuencia, o.frecuencia);
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 1. Leer texto desde consola
        System.out.print("Ingrese el texto: ");
        String texto = scanner.nextLine();
        
        if (texto.isEmpty()) {
            System.out.println("El texto no puede estar vacío.");
            return;
        }
        
        // 2. Crear un diccionario (mapa) de frecuencias
        Map<Character, Integer> frecuencias = new HashMap<>();
        
        // 3. Para cada caracter c en el texto
        for (char c : texto.toCharArray()) {
            frecuencias.put(c, frecuencias.getOrDefault(c, 0) + 1);
        }
        
        // Caso especial: si solo hay un caracter único
        if (frecuencias.size() == 1) {
            char unicoCaracter = frecuencias.keySet().iterator().next();
            System.out.println("\nFrecuencias: " + frecuencias);
            System.out.println("Codigos de Huffman: {" + unicoCaracter + "=0}");
            System.out.println("Texto codificado: " + "0".repeat(texto.length()));
            System.out.println("\narbol de Huffman:");
            System.out.println("└── " + unicoCaracter + " (" + frecuencias.get(unicoCaracter) + ")");
            return;
        }
        
        // 4. Construir el árbol de Huffman
        Nodo raiz = construirArbolHuffman(frecuencias);
        
        // 5. Generar códigos de Huffman
        Map<Character, String> codigosHuffman = new HashMap<>();
        generarCodigos(raiz, "", codigosHuffman);
        
        // 6. Codificar el texto original
        StringBuilder textoCodificado = new StringBuilder();
        for (char c : texto.toCharArray()) {
            textoCodificado.append(codigosHuffman.get(c));
        }
        
        // 7. Imprimir resultados
        System.out.println("\nFrecuencias: " + frecuencias);
        System.out.println("Codigos de Huffman: " + codigosHuffman);
        System.out.println("Texto codificado: " + textoCodificado.toString());
        System.out.println("\narbol de Huffman:");
        imprimirArbol(raiz, "");
        
        scanner.close();
    }
    
    // Construir el árbol de Huffman
    private static Nodo construirArbolHuffman(Map<Character, Integer> frecuencias) {
        // Crear una PriorityQueue de Nodos (ordenada por frecuencia)
        PriorityQueue<Nodo> cola = new PriorityQueue<>();
        
        // Insertar cada caracter con su frecuencia como nodo hoja
        for (Map.Entry<Character, Integer> entrada : frecuencias.entrySet()) {
            cola.offer(new Nodo(entrada.getKey(), entrada.getValue()));
        }
        
        // Mientras haya más de un nodo en la cola
        while (cola.size() > 1) {
            // Sacar los dos nodos con menor frecuencia
            Nodo izquierda = cola.poll();
            Nodo derecha = cola.poll();
            
            // Crear nodo padre con frecuencia = suma de ambos
            int frecuenciaPadre = izquierda.frecuencia + derecha.frecuencia;
            Nodo padre = new Nodo(frecuenciaPadre, izquierda, derecha);
            
            // Insertar nodo padre en la cola
            cola.offer(padre);
        }
        
        // El nodo restante en la cola es la raíz del árbol
        return cola.poll();
    }
    
    // Generar códigos de Huffman mediante recorrido recursivo
    private static void generarCodigos(Nodo nodo, String codigo, Map<Character, String> codigos) {
        if (nodo == null) return;
        
        // Si el nodo es hoja: asignar código al caracter
        if (nodo.caracter != '\0') {
            codigos.put(nodo.caracter, codigo.isEmpty() ? "0" : codigo);
        } else {
            // Bajar a hijo izquierdo con código + "0"
            generarCodigos(nodo.izquierda, codigo + "0", codigos);
            // Bajar a hijo derecho con código + "1"
            generarCodigos(nodo.derecha, codigo + "1", codigos);
        }
    }
    
    // Método para imprimir árbol con indentación
    public static void imprimirArbol(Nodo raiz, String prefijo) {
        if (raiz == null) return;
        
        if (raiz.caracter != '\0') {  // nodo hoja
            System.out.println(prefijo + "-- " + raiz.caracter + " (" + raiz.frecuencia + ")");
        } else {  // nodo interno
            System.out.println(prefijo + "-- * (" + raiz.frecuencia + ")");
        }
        
        if (raiz.izquierda != null) {
            imprimirArbol(raiz.izquierda, prefijo + "    ");
        }
        if (raiz.derecha != null) {
            imprimirArbol(raiz.derecha, prefijo + "    ");
        }
    }
}
