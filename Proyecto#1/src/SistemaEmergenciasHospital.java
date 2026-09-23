import java.io.*;
import java.util.*;
import java.time.LocalDate;
// ==================== CLASE PACIENTE ====================
class Paciente implements Serializable, Comparable<Paciente> {
    private int id;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String correo;
    
    public Paciente(int id, String primerNombre, String segundoNombre, 
                   String primerApellido, String segundoApellido, 
                   String fechaNacimiento, String correo) {
        this.id = id;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = LocalDate.parse(fechaNacimiento);
        this.correo = correo;
    }
    
    // Getters
    public int getId() { return id; }
    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getCorreo() { return correo; }
    
    @Override
    public int compareTo(Paciente otro) {
        return Integer.compare(this.id, otro.id);
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d, %s %s %s %s, Nacimiento: %s, Correo: %s",
            id, primerNombre, segundoNombre, primerApellido, segundoApellido,
            fechaNacimiento, correo);
    }
    
    public String toFileFormat() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
            id, primerNombre, segundoNombre, primerApellido, segundoApellido,
            fechaNacimiento, correo);
    }
}

// ==================== NODO DEL ÁRBOL B+ ADAPTADO ====================
class BPTreeNode {
    List<Integer> claves;           // IDs de pacientes
    List<Paciente> datos;           // Solo en hojas
    List<BPTreeNode> hijos;         // Solo en nodos internos
    boolean esHoja;
    BPTreeNode siguiente;
    BPTreeNode anterior;
    BPTreeNode padre;
    
    public BPTreeNode(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        if (esHoja) {
            this.datos = new ArrayList<>();
        }
        this.siguiente = null;
        this.anterior = null;
        this.padre = null;
    }
}

// ==================== RESULTADO DE SPLIT ====================
class ResultadoSplit {
    int claveMediana;
    BPTreeNode nodoIzquierdo;
    BPTreeNode nodoDerecho;
    
    public ResultadoSplit(int claveMediana, BPTreeNode izq, BPTreeNode der) {
        this.claveMediana = claveMediana;
        this.nodoIzquierdo = izq;
        this.nodoDerecho = der;
    }
}

// ==================== ÁRBOL B+ PARA PACIENTES ====================
class ArbolBPlusPacientes {
    private BPTreeNode raiz;
    private int d; // Orden mínimo
    
    public ArbolBPlusPacientes(int orden) {
        this.d = orden;
        this.raiz = new BPTreeNode(true);
    }
    
    // Insertar paciente
    public boolean insertar(Paciente paciente) {
        // Verificar si ya existe
        if (buscar(paciente.getId()) != null) {
            return false; // Ya existe
        }
        
        ResultadoSplit resultado = insertarRecursivo(raiz, paciente);
        
        if (resultado != null) {
            BPTreeNode nuevaRaiz = new BPTreeNode(false);
            nuevaRaiz.claves.add(resultado.claveMediana);
            nuevaRaiz.hijos.add(resultado.nodoIzquierdo);
            nuevaRaiz.hijos.add(resultado.nodoDerecho);
            resultado.nodoIzquierdo.padre = nuevaRaiz;
            resultado.nodoDerecho.padre = nuevaRaiz;
            raiz = nuevaRaiz;
        }
        return true;
    }
    
    private ResultadoSplit insertarRecursivo(BPTreeNode nodoActual, Paciente paciente) {
        if (nodoActual.esHoja) {
            int pos = Collections.binarySearch(nodoActual.claves, paciente.getId());
            if (pos >= 0) return null; // Ya existe
            pos = -(pos + 1);
            
            nodoActual.claves.add(pos, paciente.getId());
            nodoActual.datos.add(pos, paciente);
            
            if (nodoActual.claves.size() >= 2 * d + 1) {
                return dividirNodo(nodoActual);
            }
        } else {
            int pos = Collections.binarySearch(nodoActual.claves, paciente.getId());
            if (pos < 0) pos = -(pos + 1);
            
            ResultadoSplit resultadoHijo = insertarRecursivo(nodoActual.hijos.get(pos), paciente);
            
            if (resultadoHijo != null) {
                nodoActual.claves.add(pos, resultadoHijo.claveMediana);
                nodoActual.hijos.set(pos, resultadoHijo.nodoIzquierdo);
                nodoActual.hijos.add(pos + 1, resultadoHijo.nodoDerecho);
                resultadoHijo.nodoIzquierdo.padre = nodoActual;
                resultadoHijo.nodoDerecho.padre = nodoActual;
                
                if (nodoActual.claves.size() >= 2 * d + 1) {
                    return dividirNodo(nodoActual);
                }
            }
        }
        return null;
    }
    
    private ResultadoSplit dividirNodo(BPTreeNode nodoLleno) {
        int mitad = nodoLleno.claves.size() / 2;
        int claveMediana = nodoLleno.claves.get(mitad);
        
        if (nodoLleno.esHoja) {
            BPTreeNode nodoIzquierdo = new BPTreeNode(true);
            BPTreeNode nodoDerecho = new BPTreeNode(true);
            
            for (int i = 0; i < mitad; i++) {
                nodoIzquierdo.claves.add(nodoLleno.claves.get(i));
                nodoIzquierdo.datos.add(nodoLleno.datos.get(i));
            }
            for (int i = mitad; i < nodoLleno.claves.size(); i++) {
                nodoDerecho.claves.add(nodoLleno.claves.get(i));
                nodoDerecho.datos.add(nodoLleno.datos.get(i));
            }
            
            nodoIzquierdo.siguiente = nodoDerecho;
            nodoDerecho.anterior = nodoIzquierdo;
            
            if (nodoLleno.anterior != null) {
                nodoLleno.anterior.siguiente = nodoIzquierdo;
                nodoIzquierdo.anterior = nodoLleno.anterior;
            }
            if (nodoLleno.siguiente != null) {
                nodoLleno.siguiente.anterior = nodoDerecho;
                nodoDerecho.siguiente = nodoLleno.siguiente;
            }
            
            return new ResultadoSplit(claveMediana, nodoIzquierdo, nodoDerecho);
        } else {
            BPTreeNode nodoIzquierdo = new BPTreeNode(false);
            BPTreeNode nodoDerecho = new BPTreeNode(false);
            
            for (int i = 0; i < mitad; i++) {
                nodoIzquierdo.claves.add(nodoLleno.claves.get(i));
            }
            for (int i = mitad + 1; i < nodoLleno.claves.size(); i++) {
                nodoDerecho.claves.add(nodoLleno.claves.get(i));
            }
            
            for (int i = 0; i <= mitad; i++) {
                BPTreeNode hijo = nodoLleno.hijos.get(i);
                nodoIzquierdo.hijos.add(hijo);
                hijo.padre = nodoIzquierdo;
            }
            for (int i = mitad + 1; i < nodoLleno.hijos.size(); i++) {
                BPTreeNode hijo = nodoLleno.hijos.get(i);
                nodoDerecho.hijos.add(hijo);
                hijo.padre = nodoDerecho;
            }
            
            return new ResultadoSplit(claveMediana, nodoIzquierdo, nodoDerecho);
        }
    }
    
    // Buscar paciente por ID
    public Paciente buscar(int id) {
        return buscarRecursivo(raiz, id);
    }
    
    private Paciente buscarRecursivo(BPTreeNode nodo, int id) {
        if (nodo.esHoja) {
            int pos = nodo.claves.indexOf(id);
            if (pos >= 0) {
                return nodo.datos.get(pos);
            }
            return null;
        }
        
        int pos = Collections.binarySearch(nodo.claves, id);
        if (pos >= 0) {
            return buscarRecursivo(nodo.hijos.get(pos + 1), id);
        } else {
            pos = -(pos + 1);
            return buscarRecursivo(nodo.hijos.get(pos), id);
        }
    }
    
        // ==================== ELIMINAR PACIENTE (CORREGIDO) ====================
    public boolean eliminar(int id) {
        boolean eliminado = eliminarRecursivo(raiz, id);

        // Si la raíz se queda vacía y no es hoja, la reducimos
        if (!raiz.esHoja && raiz.claves.isEmpty()) {
            if (!raiz.hijos.isEmpty()) {
                raiz = raiz.hijos.get(0);
                raiz.padre = null;
            }
        }

        return eliminado;
    }

    private boolean eliminarRecursivo(BPTreeNode nodo, int id) {
        if (nodo.esHoja) {
            int pos = nodo.claves.indexOf(id);
            if (pos == -1) return false;

            nodo.claves.remove(pos);
            nodo.datos.remove(pos);

            // Verificar subdesbordamiento (salvo si es raíz)
            if (nodo != raiz && nodo.claves.size() < d) {
                rebalancearNodo(nodo);
            }
            return true;
        } else {
            int pos = Collections.binarySearch(nodo.claves, id);
            if (pos >= 0) {
                // Si está en un nodo interno, lo seguimos buscando en el hijo derecho
                return eliminarRecursivo(nodo.hijos.get(pos + 1), id);
            } else {
                pos = -(pos + 1);
                boolean eliminado = eliminarRecursivo(nodo.hijos.get(pos), id);

                // Rebalancear si hay subdesbordamiento en hijo
                if (eliminado && nodo != raiz && nodo.claves.size() < d) {
                    rebalancearNodo(nodo);
                }
                return eliminado;
            }
        }
    }

    // ==================== REBALANCEAR ====================
    private void rebalancearNodo(BPTreeNode nodo) {
        BPTreeNode padre = nodo.padre;
        if (padre == null) return;

        int posEnPadre = padre.hijos.indexOf(nodo);
        BPTreeNode hermanoIzq = (posEnPadre > 0) ? padre.hijos.get(posEnPadre - 1) : null;
        BPTreeNode hermanoDer = (posEnPadre < padre.hijos.size() - 1) ? padre.hijos.get(posEnPadre + 1) : null;

        // Redistribuir de izquierda
        if (hermanoIzq != null && hermanoIzq.claves.size() > d) {
            redistribuirDeIzquierda(nodo, hermanoIzq, padre, posEnPadre - 1);
            return;
        }

        // Redistribuir de derecha
        if (hermanoDer != null && hermanoDer.claves.size() > d) {
            redistribuirDeDerecha(nodo, hermanoDer, padre, posEnPadre);
            return;
        }

        // Fusionar
        if (hermanoIzq != null) {
            fusionarNodos(hermanoIzq, nodo, padre, posEnPadre - 1);
        } else if (hermanoDer != null) {
            fusionarNodos(nodo, hermanoDer, padre, posEnPadre);
        }
    }

    // ==================== REDISTRIBUIR ====================
    private void redistribuirDeIzquierda(BPTreeNode nodo, BPTreeNode hermanoIzq,
                                         BPTreeNode padre, int posClaveEnPadre) {
        if (nodo.esHoja) {
            // Pasar última clave/dato del hermano izquierdo
            int claveMovida = hermanoIzq.claves.remove(hermanoIzq.claves.size() - 1);
            Paciente datoMovido = hermanoIzq.datos.remove(hermanoIzq.datos.size() - 1);

            nodo.claves.add(0, claveMovida);
            nodo.datos.add(0, datoMovido);

            // Actualizar separador en el padre
            padre.claves.set(posClaveEnPadre, nodo.claves.get(0));
        } else {
            // Nodos internos
            int clavePadre = padre.claves.get(posClaveEnPadre);
            int claveMovida = hermanoIzq.claves.remove(hermanoIzq.claves.size() - 1);

            BPTreeNode hijoMovido = hermanoIzq.hijos.remove(hermanoIzq.hijos.size() - 1);
            hijoMovido.padre = nodo;

            nodo.claves.add(0, clavePadre);
            nodo.hijos.add(0, hijoMovido);

            padre.claves.set(posClaveEnPadre, claveMovida);
        }
    }

    private void redistribuirDeDerecha(BPTreeNode nodo, BPTreeNode hermanoDer,
                                       BPTreeNode padre, int posClaveEnPadre) {
        if (nodo.esHoja) {
            // Pasar primera clave/dato del hermano derecho
            int claveMovida = hermanoDer.claves.remove(0);
            Paciente datoMovido = hermanoDer.datos.remove(0);

            nodo.claves.add(claveMovida);
            nodo.datos.add(datoMovido);

            // Actualizar separador en el padre
            padre.claves.set(posClaveEnPadre, hermanoDer.claves.get(0));
        } else {
            // Nodos internos
            int clavePadre = padre.claves.get(posClaveEnPadre);
            int claveMovida = hermanoDer.claves.remove(0);

            BPTreeNode hijoMovido = hermanoDer.hijos.remove(0);
            hijoMovido.padre = nodo;

            nodo.claves.add(clavePadre);
            nodo.hijos.add(hijoMovido);

            padre.claves.set(posClaveEnPadre, claveMovida);
        }
    }

    // ==================== FUSIONAR ====================
    private void fusionarNodos(BPTreeNode nodoIzq, BPTreeNode nodoDer,
                               BPTreeNode padre, int posClaveEnPadre) {
        if (nodoIzq.esHoja) {
            // Fusionar claves/datos
            nodoIzq.claves.addAll(nodoDer.claves);
            nodoIzq.datos.addAll(nodoDer.datos);

            // Actualizar punteros siguiente/anterior
            nodoIzq.siguiente = nodoDer.siguiente;
            if (nodoDer.siguiente != null) {
                nodoDer.siguiente.anterior = nodoIzq;
            }
        } else {
            // Fusionar nodos internos
            nodoIzq.claves.add(padre.claves.get(posClaveEnPadre));
            nodoIzq.claves.addAll(nodoDer.claves);

            for (BPTreeNode hijo : nodoDer.hijos) {
                nodoIzq.hijos.add(hijo);
                hijo.padre = nodoIzq;
            }
        }

        // Eliminar referencia en el padre
        padre.claves.remove(posClaveEnPadre);
        padre.hijos.remove(nodoDer);
    }

    // Obtener todos los pacientes en orden
    public List<Paciente> obtenerTodosLosPacientes() {
        List<Paciente> resultado = new ArrayList<>();
        BPTreeNode nodoHoja = encontrarPrimeraHoja(raiz);
        
        while (nodoHoja != null) {
            resultado.addAll(nodoHoja.datos);
            nodoHoja = nodoHoja.siguiente;
        }
        
        return resultado;
    }
    
    private BPTreeNode encontrarPrimeraHoja(BPTreeNode nodo) {
        while (!nodo.esHoja) {
            nodo = nodo.hijos.get(0);
        }
        return nodo;
    }
    
    // Imprimir árbol por niveles
    public void imprimirPorNiveles() {
        if (raiz == null || (raiz.claves.isEmpty() && raiz.hijos.isEmpty())) {
            System.out.println("Arbol vacio");
            return;
        }
        
        Queue<BPTreeNode> cola = new LinkedList<>();
        Queue<Integer> niveles = new LinkedList<>();
        
        cola.offer(raiz);
        niveles.offer(0);
        
        int nivelActual = -1;
        StringBuilder nivelStr = new StringBuilder();
        
        while (!cola.isEmpty()) {
            BPTreeNode nodo = cola.poll();
            int nivel = niveles.poll();
            
            if (nivel != nivelActual) {
                if (nivelActual != -1) {
                    System.out.println("Nivel " + nivelActual + ": " + nivelStr.toString().trim());
                }
                nivelActual = nivel;
                nivelStr = new StringBuilder();
            }
            
            nivelStr.append("[");
            for (int i = 0; i < nodo.claves.size(); i++) {
                if (i > 0) nivelStr.append(", ");
                nivelStr.append(nodo.claves.get(i));
            }
            nivelStr.append("] ");
            
            if (!nodo.esHoja) {
                for (BPTreeNode hijo : nodo.hijos) {
                    cola.offer(hijo);
                    niveles.offer(nivel + 1);
                }
            }
        }
        
        if (nivelActual != -1) {
            System.out.println("Nivel " + nivelActual + ": " + nivelStr.toString().trim());
        }
    }
}

// ==================== TABLA HASH ====================
class TablaHash {
    private static final int TAMANO_INICIAL = 100;
    private List<List<EntradaHash>> tabla;
    private int tamano;
    
    class EntradaHash {
        String primerNombre;
        String primerApellido;
        int id;
        
        EntradaHash(String primerNombre, String primerApellido, int id) {
            this.primerNombre = primerNombre;
            this.primerApellido = primerApellido;
            this.id = id;
        }
    }
    
    public TablaHash() {
        this.tamano = TAMANO_INICIAL;
        this.tabla = new ArrayList<>(tamano);
        for (int i = 0; i < tamano; i++) {
            tabla.add(new ArrayList<>());
        }
    }
    
    private int hash(String primerNombre, String primerApellido) {
        String clave = primerNombre + primerApellido;
        return Math.abs(clave.hashCode()) % tamano;
    }
    
    public int insertar(Paciente paciente) {
        int indice = hash(paciente.getPrimerNombre(), paciente.getPrimerApellido());
        EntradaHash entrada = new EntradaHash(
            paciente.getPrimerNombre(),
            paciente.getPrimerApellido(),
            paciente.getId()
        );
        tabla.get(indice).add(entrada);
        return indice;
    }
    
    public List<Integer> buscar(String primerNombre, String primerApellido) {
        int indice = hash(primerNombre, primerApellido);
        List<Integer> ids = new ArrayList<>();
        
        for (EntradaHash entrada : tabla.get(indice)) {
            if (entrada.primerNombre.equals(primerNombre) && 
                entrada.primerApellido.equals(primerApellido)) {
                ids.add(entrada.id);
            }
        }
        
        return ids;
    }
    
    public void eliminar(int id) {
        for (List<EntradaHash> lista : tabla) {
            lista.removeIf(entrada -> entrada.id == id);
        }
    }
    
    public void guardarEnArchivo(String nombreArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (List<EntradaHash> lista : tabla) {
                for (EntradaHash entrada : lista) {
                    writer.println(entrada.primerNombre + "," + 
                                 entrada.primerApellido + "," + entrada.id);
                }
            }
        }
    }
    
    public void cargarDesdeArchivo(String nombreArchivo) throws IOException {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) return;
        
        // Limpiar tabla actual
        for (List<EntradaHash> lista : tabla) {
            lista.clear();
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String primerNombre = partes[0].trim();
                    String primerApellido = partes[1].trim();
                    int id = Integer.parseInt(partes[2].trim());
                    
                    int indice = hash(primerNombre, primerApellido);
                    tabla.get(indice).add(new EntradaHash(primerNombre, primerApellido, id));
                }
            }
        }
    }
}

// ==================== MONTÍCULO MÁXIMO ====================
class MontikuloMaximo {
    private List<PacientePrioridad> heap;

    static class PacientePrioridad {
        final Paciente paciente;
        final int prioridad;

        PacientePrioridad(Paciente paciente, int prioridad) {
            this.paciente = paciente;
            this.prioridad = prioridad;
        }
    }

    public MontikuloMaximo() {
        this.heap = new ArrayList<>();
    }

    public void insertar(Paciente paciente, int prioridad) {
    // Evitar duplicados: si ya está, lo eliminamos antes
    eliminarPorId(paciente.getId());

    // Insertar con la nueva prioridad
    heap.add(new PacientePrioridad(paciente, prioridad));
    subirHeap(heap.size() - 1);
    }

    public PacientePrioridad extraerMaximo() {
        if (heap.isEmpty()) return null;

        PacientePrioridad resultado = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            bajarHeap(0);
        }

        return resultado;
    }

    public boolean estaVacio() {
        return heap.isEmpty();
    }

    public int tamano() {
        return heap.size();
    }

    //  NUEVO MÉTODO para eliminar paciente por ID
    public boolean eliminarPorId(int id) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).paciente.getId() == id) {
                heap.set(i, heap.get(heap.size() - 1));
                heap.remove(heap.size() - 1);
                if (i < heap.size()) {
                    subirHeap(i);
                    bajarHeap(i);
                }
                return true;
            }
        }
        return false;
    }

    private void subirHeap(int indice) {
        while (indice > 0) {
            int indicePadre = (indice - 1) / 2;
            if (heap.get(indice).prioridad > heap.get(indicePadre).prioridad) {
                intercambiar(indice, indicePadre);
                indice = indicePadre;
            } else {
                break;
            }
        }
    }

    private void bajarHeap(int indice) {
        while (true) {
            int hijoIzq = 2 * indice + 1;
            int hijoDer = 2 * indice + 2;
            int mayor = indice;

            if (hijoIzq < heap.size() && heap.get(hijoIzq).prioridad > heap.get(mayor).prioridad) {
                mayor = hijoIzq;
            }
            if (hijoDer < heap.size() && heap.get(hijoDer).prioridad > heap.get(mayor).prioridad) {
                mayor = hijoDer;
            }
            if (mayor != indice) {
                intercambiar(indice, mayor);
                indice = mayor;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        PacientePrioridad temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}

// ==================== SISTEMA PRINCIPAL ====================
public class SistemaEmergenciasHospital {
    private ArbolBPlusPacientes arbol;
    private TablaHash tablaHash;
    private MontikuloMaximo monticulo;
    private PrintWriter logOperaciones;
    private PrintWriter logEmergencias;
    private static final String ARCHIVO_PACIENTES = "pacientes_final.txt";
    private static final String ARCHIVO_HASH = "hash_nombres.txt";
    
    public SistemaEmergenciasHospital(int gradoArbol) throws IOException {
        this.arbol = new ArbolBPlusPacientes(gradoArbol / 2);
        this.tablaHash = new TablaHash();
        this.monticulo = new MontikuloMaximo();
        
        // Abrir archivos de log
        this.logOperaciones = new PrintWriter(new FileWriter("registro_operaciones.txt", false));
        this.logEmergencias = new PrintWriter(new FileWriter("registro_emergencias.txt", false));
        
        // Cargar estado previo si existe
        cargarEstado();
    }
    
    private void cargarEstado() throws IOException {
        File archivoPacientes = new File(ARCHIVO_PACIENTES);
        if (archivoPacientes.exists()) {
            System.out.println("Cargando estado previo del sistema...");
            
            // Cargar pacientes
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoPacientes))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] partes = linea.split(",");
                    if (partes.length == 7) {
                        Paciente p = new Paciente(
                            Integer.parseInt(partes[0].trim()),
                            partes[1].trim(),
                            partes[2].trim(),
                            partes[3].trim(),
                            partes[4].trim(),
                            partes[5].trim(),
                            partes[6].trim()
                        );
                        arbol.insertar(p);
                    }
                }
            }
            
            // Cargar tabla hash
            tablaHash.cargarDesdeArchivo(ARCHIVO_HASH);
            System.out.println("Estado cargado exitosamente.");
        }
    }
    
    public void procesarArchivo(String nombreArchivo) throws IOException {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.out.println("Error: El archivo " + nombreArchivo + " no existe.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccionActual = "";
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                
                // Detectar sección
                if (linea.equals("CREAR PACIENTES")) {
                    seccionActual = "CREAR";
                    System.out.println("\n--- Procesando CREAR PACIENTES ---");
                } else if (linea.equals("ATENCION EMERGENCIA")) {
                    seccionActual = "EMERGENCIA";
                    System.out.println("\n--- Procesando ATENCION EMERGENCIA ---");
                } else if (linea.equals("BORRAR PACIENTES")) {
                    seccionActual = "BORRAR";
                    System.out.println("\n--- Procesando BORRAR PACIENTES ---");
                } else {
                    // Procesar datos según sección
                    switch (seccionActual) {
                        case "CREAR":
                            procesarCrearPaciente(linea);
                            break;
                        case "EMERGENCIA":
                            procesarEmergencia(linea);
                            break;
                        case "BORRAR":
                            procesarBorrarPaciente(linea);
                            break;
                    }
                }
            }
            
            // Procesar todos los pacientes en el montículo
            procesarMonticuloCompleto();
        }
        
        // Asegurar que los logs se escriban
        logOperaciones.flush();
        logEmergencias.flush();
    }
    
    private void procesarCrearPaciente(String linea) {
        String[] partes = linea.split(",");
        if (partes.length == 7) {
            try {
                Paciente p = new Paciente(
                    Integer.parseInt(partes[0].trim()),
                    partes[1].trim(),
                    partes[2].trim(),
                    partes[3].trim(),
                    partes[4].trim(),
                    partes[5].trim(),
                    partes[6].trim()
                );
                
                if (arbol.insertar(p)) {
                    int hashIndex = tablaHash.insertar(p);
                    String mensaje = "Paciente creado: id " + p.getId() + ", valor hash: " + hashIndex;
                    System.out.println("S " + mensaje);
                    logOperaciones.println(mensaje);
                } else {
                    String mensaje = "Error: Paciente con ID " + p.getId() + " ya existe";
                    System.out.println("N " + mensaje);
                    logOperaciones.println(mensaje);
                }
            } catch (Exception e) {
                System.out.println("N Error al procesar línea: " + linea);
            }
        }
    }
    
    private void procesarEmergencia(String linea) {
        try {
            if (linea.startsWith("ID:")) {
                // Buscar por ID
                String[] partes = linea.split(",");
                int id = Integer.parseInt(partes[0].substring(3).trim());
                int prioridad = Integer.parseInt(partes[1].substring(10).trim());
                
                Paciente paciente = arbol.buscar(id);
                if (paciente != null) {
                    monticulo.insertar(paciente, prioridad);
                    String mensaje = "Llegada de paciente: ID: " + id + 
                                   ", Prioridad: " + prioridad + " ***Encontrado";
                    System.out.println("Si " + mensaje);
                    logEmergencias.println(mensaje);
                } else {
                    String mensaje = "Llegada de paciente: ID: " + id + 
                                   ", Prioridad: " + prioridad + " ***Rechazado";
                    System.out.println("No " + mensaje);
                    logEmergencias.println(mensaje);
                }
            } else if (linea.startsWith("NOMBRES:")) {
                // ⚠ CAMBIO: ahora se parsea con coma
                String[] partes = linea.split(",");
                String primerNombre = partes[0].substring(8).trim();
                String primerApellido = partes[1].trim();
                int prioridad = Integer.parseInt(partes[2].substring(10).trim());
                
                List<Integer> ids = tablaHash.buscar(primerNombre, primerApellido);
                if (!ids.isEmpty()) {
                    int idSeleccionado = ids.get(0);
                    if (ids.size() > 1) {
                        Paciente masJoven = arbol.buscar(ids.get(0));
                        for (int i = 1; i < ids.size(); i++) {
                            Paciente p = arbol.buscar(ids.get(i));
                            if (p != null && p.getFechaNacimiento().isAfter(masJoven.getFechaNacimiento())) {
                                masJoven = p;
                                idSeleccionado = p.getId();
                            }
                        }
                    }
                    
                    Paciente paciente = arbol.buscar(idSeleccionado);
                    if (paciente != null) {
                        monticulo.insertar(paciente, prioridad);
                        String mensaje = "Llegada de paciente: NOMBRES: " + primerNombre + ", " + 
                                       primerApellido + ", Prioridad: " + prioridad + 
                                       " ***Encontrado ID " + idSeleccionado;
                        if (ids.size() > 1) {
                            mensaje = "Llegada de paciente: NOMBRES: " + primerNombre + ", " + 
                                    primerApellido + ", Prioridad: " + prioridad + " *** " + 
                                    ids.size() + " pacientes encontrados, se atiende a ID " + idSeleccionado;
                        }
                        System.out.println("S " + mensaje);
                        logEmergencias.println(mensaje);
                    }
                } else {
                    String mensaje = "Llegada de paciente: NOMBRES: " + primerNombre + ", " + 
                                   primerApellido + ", Prioridad: " + prioridad + " ***Rechazado";
                    System.out.println("N " + mensaje);
                    logEmergencias.println(mensaje);
                }
            }
        } catch (Exception e) {
            System.out.println(" Error al procesar emergencia: " + linea);
        }
    }
    
    private void procesarBorrarPaciente(String linea) {
        try {
            int id = Integer.parseInt(linea.trim());
            
            if (arbol.eliminar(id)) {
                tablaHash.eliminar(id);
                monticulo.eliminarPorId(id); 
                String mensaje = "Paciente eliminado: id " + id;
                System.out.println("S " + mensaje);
                logOperaciones.println(mensaje);
            } else {
                String mensaje = "Error: Paciente con ID " + id + " no existe";
                System.out.println("N " + mensaje);
                logOperaciones.println(mensaje);
            }
        } catch (Exception e) {
            System.out.println("N Error al procesar borrado: " + linea);
        }
    }
    
    private void procesarMonticuloCompleto() {
    System.out.println("\n--- Atendiendo pacientes por prioridad ---");
    while (!monticulo.estaVacio()) {
        MontikuloMaximo.PacientePrioridad pp = monticulo.extraerMaximo();
        // pp nunca debería ser null si el heap no está vacío, pero por seguridad:
        if (pp == null) break;
        System.out.println("Atendiendo: " + pp.paciente.toString() + ", Prioridad: " + pp.prioridad);
        logEmergencias.println("Paciente atendido: " + pp.paciente.toString() + ", Prioridad: " + pp.prioridad);
    }
}
    
    public void guardarEstado() throws IOException {
        System.out.println("\nGuardando estado del sistema...");
        
        // Guardar pacientes
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_PACIENTES))) {
            List<Paciente> pacientes = arbol.obtenerTodosLosPacientes();
            for (Paciente p : pacientes) {
                writer.println(p.toFileFormat());
            }
        }
        
        // Guardar tabla hash
        tablaHash.guardarEnArchivo(ARCHIVO_HASH);
        
        System.out.println("Estado guardado en " + ARCHIVO_PACIENTES + " y " + ARCHIVO_HASH);
    }
    
    public void mostrarEstadoArbol() {
        System.out.println("\n=== ESTADO ACTUAL DEL ARBOL B+ ===");
        arbol.imprimirPorNiveles();
        
        System.out.println("\n=== PACIENTES EN ORDEN ===");
        List<Paciente> pacientes = arbol.obtenerTodosLosPacientes();
        for (Paciente p : pacientes) {
            System.out.println(p);
        }
    }
    
    public void cerrar() throws IOException {
        logOperaciones.close();
        logEmergencias.close();
    }
    
    // MÉTODO PARA LIMPIAR PANTALLA
    public static void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            limpiarPantalla();
            System.out.println("============================================");
            System.out.println("=     SISTEMA DE EMERGENCIAS HOSPITALARIAS  =");
            System.out.println("=============================================");
            
            System.out.print("\nIngrese el grado maximo del arbol B+: ");
            int gradoMaximo = scanner.nextInt();
            scanner.nextLine(); 
            
            SistemaEmergenciasHospital sistema = new SistemaEmergenciasHospital(gradoMaximo);
            
            boolean continuar = true;
            while (continuar) {
                limpiarPantalla(); //  limpia antes del menú
                System.out.println("\n============================================");
                System.out.println("=                    MENU PRINCIPAL           =");
                System.out.println("===============================================");
                System.out.println("= 1. Procesar archivo de comandos             =");
                System.out.println("= 2. Mostrar estado del arbol B+              =");
                System.out.println("= 3. Guardar estado actual                    =");
                System.out.println("= 4. Salir                                    =");
                System.out.println("================================================");
                
                System.out.print("\nSeleccione una opcion: ");
                int opcion = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcion) {
                    case 1:
                        limpiarPantalla();
                        System.out.print("Ingrese la ruta completa del archivo a procesar: ");
                        String rutaArchivo = scanner.nextLine().trim();
                        sistema.procesarArchivo(rutaArchivo);
                        break;
                        
                    case 2:
                        limpiarPantalla();
                        sistema.mostrarEstadoArbol();
                        break;
                        
                    case 3:
                        limpiarPantalla();
                        sistema.guardarEstado();
                        break;
                        
                    case 4:
                        continuar = false;
                        sistema.guardarEstado();
                        sistema.cerrar();
                        System.out.println("\nGracias por usar este programa!");
                        break;
                        
                    default:
                        System.out.println("Opcion invalida.");
                }

                if (continuar) {
                    System.out.println("\nPresione ENTER para continuar...");
                    scanner.nextLine();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en el sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
