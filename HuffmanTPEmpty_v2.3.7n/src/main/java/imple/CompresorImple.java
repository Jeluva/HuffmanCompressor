package imple;

import huffman.def.BitWriter;
import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import huffman.util.FuncionesUtilizadas;
import huffman.util.HuffmanTree;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CompresorImple implements Compresor {

    public HuffmanTable[] contarOcurrencias(String rutaArchivo) {
        HuffmanTable[] tabla = new HuffmanTable[256]; // Inicializar el array de los contadores
        FuncionesUtilizadas func=new FuncionesUtilizadas();
        func.LLenarConCeros(tabla);   // Inicializar los contadores en 0

        try (FileInputStream fis = new FileInputStream(rutaArchivo)) {
            int caracterValor;
            while ((caracterValor = fis.read()) != -1) {  // Recorrer mientras que no termine el archivo

                tabla[caracterValor].increment(); // Acumular los bytes en los contadores

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tabla;  // Retornar la tabla
    }
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] tabla) {
        List<HuffmanInfo> lista = new ArrayList<>(); // Inicializar la lista
        FuncionesUtilizadas func=new FuncionesUtilizadas();

        for (int i = 0; i < 256; i++) { // Recorrer la tabla

            if (tabla[i].getN() != 0) {  // Tomar en cuenta los caracteres que tienen por lo menos 1 ocurrencia

                HuffmanInfo rama = new HuffmanInfo(i,tabla[i].getN()); // Inicializar la rama
                func.orderedInsert(lista, rama); // Insertar según el criterio de precedencia
            }
        }

        return lista;   // Retornar la lista.
    }

    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista) {
        LinkedList<HuffmanInfo> listaEnlazada = new LinkedList<>(lista);
        FuncionesUtilizadas func=new FuncionesUtilizadas();
        for (int i = 0; listaEnlazada.size() > 1; i++) {
            // Recorrer la lista enlazada hasta que quede sólo un nodo

            HuffmanInfo hi1 = listaEnlazada.removeFirst();     // Remover los 2 primeros nodos
            HuffmanInfo hi2 = listaEnlazada.removeFirst();

            HuffmanInfo nodo = new HuffmanInfo(256 + i, hi1.getN() + hi2.getN());  // Crear nueva nodo

            nodo.setLeft(hi2);   // Setear a las ramas del nuevo nodo, los nodos removidos
            nodo.setRight(hi1);

            func.orderedInsert(listaEnlazada, nodo);   // Insertar el nodo según el criterio de precedencia

        }

        // Llegado a este punto, debería haber un sólo nodo (la raíz), el valor de retorno.

        return listaEnlazada.getFirst();
    }

    public void generarCodigosHuffman(HuffmanInfo raiz, HuffmanTable[] tabla) {
        HuffmanTree arbol = new HuffmanTree(raiz);
        HuffmanInfo hoja;
        StringBuffer cod = new StringBuffer();
        while ((hoja = arbol.next(cod)) != null) {        // Recorrer las hojas del árbol
            tabla[hoja.getC()].setCod(cod.toString());    // Asignarle sus códigos correspondientes
        }
    }
    public long escribirEncabezado(String filename, HuffmanTable[] tabla) {
        long sizeArchivoOri;
        try (FileOutputStream fos = new FileOutputStream(filename + ".huff")) {
            DataOutputStream dos = new DataOutputStream(fos);
            FuncionesUtilizadas func = new FuncionesUtilizadas();

            int caracteresCant = func.escribirCantidadCaracteres(fos, tabla); // Escribir cantidad de caracteres
            long ret = 1;

            for (int i = 0; i < tabla.length; i++) { // Recorrer la tabla

                if (tabla[i].getN() > 0) { // Tomar en cuenta sólo los caracteres que aparecen en el archivo

                    ret += func.escribirHoja(fos, tabla[i], i); // Escribir el registro del carácter (hoja)

                }
            }
            System.out.println("Tamaño archivo original: ");
            File archivoOri = new File(filename);
            sizeArchivoOri = archivoOri.length();
            System.out.println(sizeArchivoOri);

            dos.writeInt((int) sizeArchivoOri); // Escribir tamaño del archivo original
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sizeArchivoOri;

    }
    public void escribirContenido(String filenameOri, HuffmanTable[] tabla) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filenameOri));
             FileOutputStream fos = new FileOutputStream(filenameOri+".huff", true)) {

            BitWriter bw = Factory.getBitWriter(); // Instanciar el bitWriter
            bw.using(fos);    // Preparar el archivo "fos" para escribir en él

            int caracterValor;
            while ((caracterValor = bis.read()) != -1) { // Leer el archivo hasta que EOF

                HuffmanTable ht = tabla[caracterValor]; // Caracter en el HuffmanTable
                String cod = ht.getCod();           // Código huffman

                for (int i = 0; i < cod.length(); i++) {  // Escribir bit a bit su código huffman
                    int bit = (cod.charAt(i)) - 48;
                    bw.writeBit(bit);
                }
            }
            bw.flush(); // Completar con ceros si fuera necesario
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
