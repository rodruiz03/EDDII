import java.util.*;

// Interfaz HashableKey
interface HashableKey {
    String getHashKey();
}

// Clase UniversalHashTable genérica
class UniversalHashTable<T extends HashableKey> {
    // Atributos
    private List<T>[] tabla;
    private int tamaño;
    private long a, b, p;
    private Random random = new Random();
    
    // Constructor 1: Inicializa con tamaño
    @SuppressWarnings("unchecked")
    public UniversalHashTable(int tamaño) {
        this.tamaño = tamaño;
        this.tabla = new List[tamaño];
        
        // Inicializar cada posición con una lista vacía
        for (int i = 0; i < tamaño; i++) {
            tabla[i] = new ArrayList<>();
        }
        
        // Número primo grande (2^31 - 1)
        this.p = 2147483647L;
        // Generar a y b aleatoriamente según las restricciones del hashing universal
        this.a = 1 + random.nextInt((int)(p - 1));
        this.b = random.nextInt((int)p);
    }
    
    // Constructor 2: Inicializa con parámetros específicos
    @SuppressWarnings("unchecked")
    public UniversalHashTable(int tamaño, long a, long b, long p) {
        this.tamaño = tamaño;
        this.tabla = new List[tamaño];
        
        // Inicializar cada posición con una lista vacía
        for (int i = 0; i < tamaño; i++) {
            tabla[i] = new ArrayList<>();
        }
        
        this.p = p;
        this.a = a;
        this.b = b;
    }
    
    // Método universalHash
    private int universalHash(long k) {
        long clave = ((this.a * k + this.b) % this.p);
        return (int)(Math.abs(clave) % this.tamaño);
    }
    
    // Método stringToInt
    private long stringToInt(String s) {
        long hash = 0;
        int base = 31;
        
        for (int i = 0; i < s.length(); i++) {
            hash = (hash * base + s.charAt(i)) % p;
        }
        
        return Math.abs(hash);
    }
    
    // Método insert
    public void insert(T obj) {
        String claveString = obj.getHashKey();
        long claveInt = stringToInt(claveString);
        int indice = universalHash(claveInt);
        
        // Solo insertar si no existe ya
        if (!tabla[indice].contains(obj)) {
            tabla[indice].add(obj);
        }
    }
    
    // Método search
    public T search(String clave) {
        long claveInt = stringToInt(clave);
        int indice = universalHash(claveInt);
        
        for (T elemento : tabla[indice]) {
            if (elemento.equals(clave)) {
                return elemento;
            }
        }
        
        return null; // No se encontró
    }
    
    // Método remove
    public boolean remove(T obj) {
        String claveString = obj.getHashKey();
        long claveInt = stringToInt(claveString);
        int indice = universalHash(claveInt);
        
        return tabla[indice].remove(obj);
    }
    
    // Método printTable
    public void printTable() {
        System.out.println("Parámetros: a: " + a + ", b: " + b + ", p: " + p);
        for (int i = 0; i < tamaño; i++) {
            System.out.println(i + " -> " + tabla[i]);
        }
    }
    
    // Métodos adicionales útiles
    public int size() {
        int count = 0;
        for (List<T> lista : tabla) {
            count += lista.size();
        }
        return count;
    }
    
    public double factorDeCarga() {
        return (double)size() / tamaño;
    }
    
    public void estadisticas() {
        int elementosTotal = size();
        int celdas_no_vacias = 0;
        int colisionesMax = 0;
        
        for (List<T> lista : tabla) {
            if (!lista.isEmpty()) {
                celdas_no_vacias++;
                colisionesMax = Math.max(colisionesMax, lista.size());
            }
        }
        
        System.out.println("\n=== Estadísticas de la Tabla Hash ===");
        System.out.println("Tamaño de la tabla: " + tamaño);
        System.out.println("Elementos totales: " + elementosTotal);
        System.out.println("Celdas no vacías: " + celdas_no_vacias);
        System.out.println("Factor de carga: " + String.format("%.2f", factorDeCarga()));
        System.out.println("Máxima longitud de cadena: " + colisionesMax);
    }
}

// Clase Persona que implementa HashableKey
class Persona implements HashableKey {
    private String id;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String fechaNacimiento;
    
    // Constructor
    public Persona(String id, String primerNombre, String segundoNombre, 
                   String primerApellido, String segundoApellido, String fechaNacimiento) {
        this.id = id;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
    }
    
    // Implementación del método de la interfaz
    @Override
    public String getHashKey() {
        return primerNombre + primerApellido;
    }
    
    // Método toString
    @Override
    public String toString() {
        return id + ": " + primerNombre + " " + (segundoNombre != null ? segundoNombre + " " : "") +
               primerApellido + " " + (segundoApellido != null ? segundoApellido : "") +
               " (" + fechaNacimiento + ")";
    }
    
    // Método equals
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        
        // Si el objeto es un String, comparar con getHashKey()
        if (o instanceof String) {
            return this.getHashKey().equals(o);
        }
        
        // Si es la misma instancia
        if (this == o) return true;
        
        // Si no es una instancia de Persona
        if (!(o instanceof Persona)) return false;
        
        Persona persona = (Persona) o;
        return this.getHashKey().equals(persona.getHashKey());
    }
    
    @Override
    public int hashCode() {
        return getHashKey().hashCode();
    }
    
    // Getters
    public String getId() { return id; }
    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public String getFechaNacimiento() { return fechaNacimiento; }
}

// Clase principal para demostrar el uso
public class Main {
    public static void main(String[] args) {
        // Crear una tabla hash con tamaño 10
        UniversalHashTable<Persona> tablaHash = new UniversalHashTable<>(10);
        
        // Crear algunas personas de ejemplo
        Persona p1 = new Persona("001", "Juan", "Carlos", "Pérez", "López", "1990-01-15");
        Persona p2 = new Persona("002", "María", "Elena", "García", "Rodríguez", "1985-03-22");
        Persona p3 = new Persona("003", "Carlos", null, "Martínez", "Hernández", "1992-07-10");
        Persona p4 = new Persona("004", "Ana", "Lucía", "Fernández", "Gómez", "1988-11-05");
        Persona p5 = new Persona("005", "Luis", "Alberto", "Sánchez", "Torres", "1995-09-18");
        
        // Insertar personas en la tabla hash
        System.out.println("=== Insertando personas ===");
        tablaHash.insert(p1);
        tablaHash.insert(p2);
        tablaHash.insert(p3);
        tablaHash.insert(p4);
        tablaHash.insert(p5);
        
        // Mostrar la tabla
        System.out.println("\n=== Estado de la tabla hash ===");
        tablaHash.printTable();
        
        // Buscar personas
        System.out.println("\n=== Búsquedas ===");
        Persona encontrada = tablaHash.search("JuanPérez");
        System.out.println("Búsqueda 'JuanPérez': " + (encontrada != null ? encontrada : "No encontrado"));
        
        encontrada = tablaHash.search("MaríaGarcía");
        System.out.println("Búsqueda 'MaríaGarcía': " + (encontrada != null ? encontrada : "No encontrado"));
        
        encontrada = tablaHash.search("PedroLópez");
        System.out.println("Búsqueda 'PedroLópez': " + (encontrada != null ? encontrada : "No encontrado"));
        
        // Eliminar una persona
        System.out.println("\n=== Eliminación ===");
        boolean eliminado = tablaHash.remove(p2);
        System.out.println("Eliminar María García: " + (eliminado ? "Exitoso" : "No encontrado"));
        
        // Mostrar tabla después de eliminación
        System.out.println("\n=== Tabla después de eliminación ===");
        tablaHash.printTable();
        
        // Mostrar estadísticas
        tablaHash.estadisticas();
    }
}
