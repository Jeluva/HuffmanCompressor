import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import imple.CompresorImple;
import imple.DescompresorImple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String rutaArchivo = "C:\\Users\\Jejol\\Desktop\\UTN\\Algoritmos y Estructura de Datos\\TP\\Prueba\\JEDE.wav.huff";
        File archivo = new File(rutaArchivo);
        DescompresorImple d=new DescompresorImple();
        CompresorImple c= new CompresorImple();



        if (archivo.getName().endsWith(".huff")) {
            HuffmanInfo hu=new HuffmanInfo();
            FileInputStream fis=new FileInputStream(archivo);


            long n=d.recomponerArbolHuff(fis,hu);

            d.descomprimirArchivo(hu,n,rutaArchivo);

        }

        else {
            HuffmanTable[] tabla = c.contarOcurrencias(rutaArchivo); // Crear la tabla de los contadores

            List<HuffmanInfo> lista = c.crearListaEnlazada(tabla);                // Crear la lista

            HuffmanInfo raiz = c.convertirListaEnArbol(lista);                      // Convertir la lista en el árbol

            c.generarCodigosHuffman(raiz, tabla);                   // Generar los códigos huff para cada carácter


            // Escribir el encabezado del archivoHuff
             c.escribirEncabezado(rutaArchivo, tabla);

            // Escribir el contenido en el archivo usando los códigos huff
            c.escribirContenido(rutaArchivo, tabla);
        }
    }

}
