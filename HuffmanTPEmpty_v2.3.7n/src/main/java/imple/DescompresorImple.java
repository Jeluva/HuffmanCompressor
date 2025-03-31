package imple;

import huffman.def.BitReader;
import huffman.def.HuffmanInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DescompresorImple {

    public long recomponerArbolHuff(FileInputStream fis, HuffmanInfo arbol) {
       long size;
        try {
            byte [] cantArr = null; // Cantidad de caracteres
            cantArr = fis.readNBytes(1);
        int cantidadCaracteres=cantArr[0]&0xFF; // Interpretar el byte como no signado

        if (cantidadCaracteres == 0) { // Si el primer byte era un 0, significaba que había 256 caracteres diferentes
            cantidadCaracteres = 256;
        }

        for (int i = 0; i < cantidadCaracteres; i++) {      // Bucle de caracacteres

            int caracter = fis.read(); // Leer el carácter
            int cantBits = fis.read(); // Número de bits
            HuffmanInfo ramaActual = arbol;  // Declarar un "HuffmanInfo" auxiliar para recorer el árbol

            for (int j = 0; j < cantBits; j++) {     // Recorrer el árbol para llegar a las hojas

                char bit = (char) fis.read();

                if (bit == '0') {     // Si se lee un '0', desplazar a la rama izquierda
                    if (ramaActual.getLeft() == null) {
                        ramaActual.setLeft(new HuffmanInfo());
                    }
                    ramaActual = ramaActual.getLeft();
                } else {    // Si no se lee un '0',es decir, se lee un '1', desplazar a la rama derecha

                    if (ramaActual.getRight() == null) {
                        ramaActual.setRight(new HuffmanInfo());
                    }
                    ramaActual = ramaActual.getRight();

                }
            }

            ramaActual.setC(caracter); // Setear el valor ASCII del caracter en la hoja correspondiente
        }

        byte[] tamanioArchivoOri = fis.readNBytes(4); // Tamaño del archivo original
        int tamanioArchivo= ByteBuffer.wrap(tamanioArchivoOri).getInt();
        size=tamanioArchivo;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return size;  // Retornar la raíz
    }


    public void descomprimirArchivo(HuffmanInfo root, long n, String filename) {
        try (FileInputStream fis = new FileInputStream(filename );
             FileOutputStream fos = new FileOutputStream(filename+"_Descomprimido")) {
            BitReader reader = new BitReaderImple();
            reader.using(fis);

            // Paso 1: Saltar el encabezado
            int totalHojas = fis.read(); // Número de hojas (1 byte)
            if(totalHojas==0)
            {
                totalHojas=256;
            }
            //fis.skip(1); // me salteo el primer byte, la cantidad de hojas (t)
            for (int i = 0; i < totalHojas; i++) {
                fis.read(); // Caracter (1 byte)
                int longitudCodigo = fis.read(); // Longitud del código (1 byte)
                //(m = |00100000|) y mBytes = 1 por ejemplo
                // Número de bytes para el código.
                fis.skip(longitudCodigo); // Saltar los bytes alineados del código Huffman
            }
            fis.skip(4); // Saltar la longitud original del archivo (4 bytes)


            // Paso 2: Decodificar el contenido comprimido
            HuffmanInfo nodoActual = root;
            if (nodoActual == null) {
                throw new IllegalStateException("El nodo actual es null, el árbol de Huffman puede estar mal construido.");
            }

            long bytesEscritos = 0;

            while (bytesEscritos < n) { // n = longitud original del archivo (ejemplo escribir 39 bytes)
                int bit = reader.readBit();

                if (bit == -1) {
                    throw new IOException("Error: Bits insuficientes para decodificar el archivo.");
                }

                // Navegar en el árbol según el bit leído
                if (bit == 0) {
                    nodoActual = nodoActual.getLeft();
                } else {
                    nodoActual = nodoActual.getRight();
                }

                // Si llegamos a una hoja, escribir el carácter en el archivo de salida
                if (nodoActual.getLeft() == null && nodoActual.getRight() == null) {
                    fos.write(nodoActual.getC()); // Escribir el carácter
                    bytesEscritos++;
                    nodoActual = root; // Reiniciar al nodo raíz

                }

            }
            reader.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al descomprimir el archivo.", e);
        }

    }

}
