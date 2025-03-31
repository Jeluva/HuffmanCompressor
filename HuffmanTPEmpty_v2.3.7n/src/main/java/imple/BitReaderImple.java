package imple;

import java.io.IOException;
import java.io.InputStream;

import huffman.def.BitReader;
import huffman.util.FuncionesUtilizadas;

public class BitReaderImple implements BitReader
{
	private InputStream is;
	private int bitActual;
	private String byteActual;


	@Override
	public void using(InputStream is) {
		this.is = is;
		this.byteActual = "";
		this.bitActual = -1;
	}

	@Override
	public int readBit() throws IOException {
		FuncionesUtilizadas func = new FuncionesUtilizadas();
		if (this.bitActual == -1 || this.bitActual == 7) {
			int br = this.is.read();
			if (br == -1) { // Verifica el final del archivo
				return -1;
			}
			byte brr = (byte) br;
			this.byteActual = func.byteToString(brr);
			this.bitActual = 0; // Reinicia a 0
		} else {
			this.bitActual++;
		}
		return this.byteActual.charAt(this.bitActual) - '0'; // Convertir char a int
	}

	@Override
	public void flush() {

	}
}
