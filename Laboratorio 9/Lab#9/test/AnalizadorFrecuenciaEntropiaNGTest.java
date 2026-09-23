/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */

import java.util.HashMap;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author rodru
 */
public class AnalizadorFrecuenciaEntropiaNGTest {
    
    public AnalizadorFrecuenciaEntropiaNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of main method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        AnalizadorFrecuenciaEntropia.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of leerArchivoYCalcularFrecuencias method, of class AnalizadorFrecuenciaEntropia.
     */
    @Test
    public void testLeerArchivoYCalcularFrecuencias() throws Exception {
        System.out.println("leerArchivoYCalcularFrecuencias");
        String nombreArchivo = "";
        AnalizadorFrecuenciaEntropia instance = new AnalizadorFrecuenciaEntropia();
        HashMap expResult = null;
        HashMap result = instance.leerArchivoYCalcularFrecuencias(nombreArchivo);
        assertEquals(result, expResult);
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
        assertEquals(result, expResult, 0.0);
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
        HashMap expResult = null;
        HashMap result = instance.analizarTexto(texto);
        assertEquals(result, expResult);
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
