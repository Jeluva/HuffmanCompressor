package huffman.def;

import java.io.IOException;
import java.io.OutputStream;

public interface BitWriter
{
	public void using(OutputStream os);
	public void writeBit(int bit) throws IOException;
	public void flush() throws IOException;
}
