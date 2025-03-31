package imple;

import java.io.IOException;
import java.io.OutputStream;

import huffman.def.BitWriter;
import huffman.util.FuncionesUtilizadas;

public class BitWriterImple implements BitWriter 
{
    private String cadenaByte;
    private int acum;
    private OutputStream is;

    public void setAcum(int acum) {
        this.acum = acum;
    }

    public void setCadenaByte(String cadenaByte) {
        this.cadenaByte = cadenaByte;
    }


    @Override
    public void using(OutputStream os) {
        this.is = os;
        this.acum = 0;
        this.cadenaByte = "";
    }

    @Override
    public void writeBit(int bit) throws IOException {
        FuncionesUtilizadas func = new FuncionesUtilizadas();
        char bin = (char) (48 + bit);
        this.cadenaByte += bin;
        this.acum++;
        if (this.acum % 8 == 0 && this.acum != 0) {
            byte b = func.stringToByte(cadenaByte);
            this.is.write(b);
            this.acum = 0;
            this.cadenaByte = "";
        }
    }

    @Override
    public void flush() throws IOException {
        FuncionesUtilizadas func = new FuncionesUtilizadas();
        while (this.acum % 8 != 0 && !(this.cadenaByte.isEmpty())) {
            this.cadenaByte += '0';
            this.acum++;
        }
        if (!this.cadenaByte.isEmpty()) {
            byte bc = func.stringToByte(this.cadenaByte);
            this.is.write(bc);
        }
        this.cadenaByte = "";

    }
}
