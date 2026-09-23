import java.io.*;
import java.util.*;

// Clase Paciente que implementa Comparable
class Paciente implements Comparable<Paciente> {
    private String nombre;
    private int prioridad;
    
    // Constructor
    public Paciente(String nombre, int prioridad) {
        this.nombre = nombre;
        this.prioridad = prioridad;
    }
    
    // Getters
    public String getNombre() {
        return nombre;
    }
    
    public int getPrioridad() {
        return prioridad;
    }
    
    // Método compareTo - orden natural: prioridad ascendente (menor número primero)
    @Override
    public int compareTo(Paciente otro) {
        return Integer.compare(this.prioridad, otro.prioridad);
    }
    
    // Método toString según el formato requerido
    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Prioridad: " + prioridad;
    }
}


public class AtencionPacientes {
    
    public static void main(String[] args) {
        // Declarar la cola de prioridad con orden inverso
        PriorityQueue<Paciente> cola = new PriorityQueue<>(Collections.reverseOrder());
        
        try {
            // Leer archivo de entrada
            leerPacientes("entradaPacientes.txt", cola);
            
            // Simular atención y escribir resultado
            atenderPacientes(cola, "salidaPacientes.txt");
            
            System.out.println("Proceso completado. Revisa el archivo salidaPacientes.txt");
            
        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
        }
    }
    
    /**
     * Lee los pacientes del archivo de entrada y los agrega a la cola
     */
    private static void leerPacientes(String nombreArchivo, PriorityQueue<Paciente> cola) 
            throws IOException {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                // Parsear cada línea: formato "Nombre,Prioridad"
                String[] datos = linea.trim().split(",");
                
                if (datos.length == 2) {
                    String nombre = datos[0].trim();
                    int prioridad = Integer.parseInt(datos[1].trim());
                    
                    // Crear paciente y agregar a la cola
                    Paciente paciente = new Paciente(nombre, prioridad);
                    cola.offer(paciente);
                    
                    System.out.println("Paciente agregado: " + paciente);
                }
            }
        }
    }
    
    /**
     * Simula la atención de pacientes y escribe el resultado al archivo
     */
    private static void atenderPacientes(PriorityQueue<Paciente> cola, String nombreArchivo) 
            throws IOException {
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            
            System.out.println("\n--- Iniciando atencion de pacientes ---");
            
            // Atender pacientes según prioridad (orden inverso: mayor prioridad primero)
            while (!cola.isEmpty()) {
                Paciente pacienteAtendido = cola.poll();
                String lineaSalida = "Atendiendo: " + pacienteAtendido;
                
                // Escribir al archivo
                writer.println(lineaSalida);
                
                // Mostrar en consola para seguimiento
                System.out.println(lineaSalida);
            }
        }
    }
}
