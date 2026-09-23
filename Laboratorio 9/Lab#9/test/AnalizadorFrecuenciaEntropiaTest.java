/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rodru
 */
public class AnalizadorFrecuenciaEntropiaTest {
    
    public AnalizadorFrecuenciaEntropiaTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of leerArchivoYCalcularFrecuencias method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testLeerArchivoYCalcularFrecuencias() throws Exception {
        System.out.println("leerArchivoYCalcularFrecuencias");
        String nombreArchivo = "";
        AnalizadorFrecuenciaEntropia instance = new AnalizadorFrecuenciaEntropia();
        HashMap<Character, Integer> expResult = null;
        HashMap<Character, Integer> result = instance.leerArchivoYCalcularFrecuencias(nombreArchivo);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mostrarTablaFrecuencias method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testMostrarTablaFrecuencias() {
        System.out.println("mostrarTablaFrecuencias");
        HashMap<Character, Integer> frecuencias = null;
        AnalizadorFrecuenciaEntropia instance = new AnalizadorFrecuenciaEntropia();
        instance.mostrarTablaFrecuencias(frecuencias);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calcularEntropia method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testCalcularEntropia() {
        System.out.println("calcularEntropia");
        HashMap<Character, Integer> frecuencias = null;
        AnalizadorFrecuenciaEntropia instance = new AnalizadorFrecuenciaEntropia();
        double expResult = 0.0;
        double result = instance.calcularEntropia(frecuencias);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of analizarTexto method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testAnalizarTexto() {
        System.out.println("analizarTexto");
        String texto = "";
        AnalizadorFrecuenciaEntropia instance = new AnalizadorFrecuenciaEntropia();
        HashMap<Character, Integer> expResult = null;
        HashMap<Character, Integer> result = instance.analizarTexto(texto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crearArchivoPrueba method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testCrearArchivoPrueba() {
        System.out.println("crearArchivoPrueba");
        String nombreArchivo = "";
        String contenido = "";
        AnalizadorFrecuenciaEntropia.crearArchivoPrueba(nombreArchivo, contenido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
