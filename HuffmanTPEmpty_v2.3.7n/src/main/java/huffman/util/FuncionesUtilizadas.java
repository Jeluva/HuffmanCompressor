package huffman.util;

import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FuncionesUtilizadas {
    public byte stringToByte(String s) {
        byte b = 0;
        for (int i = 0; i < 8; i++) {
            char as = s.charAt(i);
            b = (byte) (b << 1);
            if (as == '1') {
                b = (byte) (b | 1);
            }
        }
        return b;
    }

    public String byteToString(byte b) {
        String s = "";
        for (int i = 7; i >= 0; i--) {
            int bit = (b >> i) & 1; // Desplazar el bit actual y enmascarar
            s += (char) ('0' + bit); // Convertir el bit a '0' o '1' antes de añadir
        }
        return s;

    }

    public void LLenarConCeros(HuffmanTable[] arr)
    {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new HuffmanTable();
            arr[i].setN(0); // Llenar de ceros los contadores
        }
    }

    public void orderedInsert(List<HuffmanInfo> lista, HuffmanInfo huf) {
        int i = 0;
        int cant = huf.getN();
        int caracter = huf.getC();

        if (lista.isEmpty()) {     // Si la lista esta vacía, simplemente se añade el elemento
            lista.add(huf);
        } else {

            boolean posicionEncontrada = false;
            while (i < lista.size() && !posicionEncontrada) { // Recorrer la lista mientras no encuentre la posción

                if (lista.get(i).getN() > cant) {  // Comparación de cantidad de ocurrencias
                    posicionEncontrada = true;
                } else if (lista.get(i).getN() == cant && lista.get(i).getC() > caracter) { // Comparacion ASCII
                    posicionEncontrada = true;
                } else {
                    i++;
                }
            }
            lista.add(i, huf); // Insertar en la posición encontrada
        }

    }

    public int escribirCantidadCaracteres(FileOutputStream fos, HuffmanTable[] tabla) throws IOException {
        int size = 0;
        for (int i = 0; i < tabla.length; i++) {
            if (tabla[i].getN() > 0) {
                size++;
            }
        }

        if (size == 256) {   // Si se utilizan 256 caracteres diferentes se escribe un 0 en el archivo,
            fos.write(0); // Asegurarse de escribir solo un byte        // porque de lo contrario no se podría escribir la cantidad en un sólo byte.
        } else {
            fos.write(size); // Asegurarse de escribir solo un byte
        }

        return size;
    }

    // RETORNA LA CANTIDAD DE BYTES QUE OCUPA
    public long escribirHoja(FileOutputStream fos, HuffmanTable hu, int index) throws IOException {
        long ret = 0; // Inicializar la longitud del encabezado en 0

        fos.write(index); // Escribir índice
        ret++;

        String cadena = hu.getCod();  // Guardar la cadena del código huff

        fos.write(cadena.length()); // Escribir longitud de la cadena
        ret++;

        for (int i = 0; i < cadena.length(); i++) {  // Recorrrer la cadena
            char c = cadena.charAt(i);      // Escribir los '1' y '0'
            fos.write(c);
            ret++;
        }


        return ret;
    }
}
