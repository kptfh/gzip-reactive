package reactivegzip;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * Patterned from https://stackoverflow.com/questions/13387215/iterable-gzip-deflate-inflate-in-java?noredirect=1&lq=1
 */
public class NonblockingGzipEncoder {
	/** The standard 10 byte GZIP header */
	private static final byte[] GZIP_HEADER = new byte[] { 0x1f, (byte) 0x8b,
			Deflater.DEFLATED, 0, 0, 0, 0, 0, 0, 0 };

	/** CRC-32 of uncompressed data. */
	private final CRC32 crc = new CRC32();

	/** Deflater to deflate data */
	private final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION,
			true);

	/** Output buffer building area */
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	/** Internal transfer space */
	private final byte[] transfer = new byte[1000];

	/** The flush mode to use at the end of each buffer */
	private final int flushMode;


	/**
	 * New buffer deflater
	 *
	 * @param syncFlush
	 *            if true, all data in buffer can be immediately decompressed
	 *            from output buffer
	 */
	public NonblockingGzipEncoder(boolean syncFlush) {
		flushMode = syncFlush ? Deflater.SYNC_FLUSH : Deflater.NO_FLUSH;
		buffer.write(GZIP_HEADER, 0, GZIP_HEADER.length);
	}


	/**
	 * Deflate the buffer
	 *
	 * @param in
	 *            the buffer to deflate
	 * @return deflated representation of the buffer
	 */
	public ByteBuffer encode(ByteBuffer in) {
		// convert buffer to bytes
		byte[] inBytes;
		int off = in.position();
		int len = in.remaining();
		if( in.hasArray() ) {
			inBytes = in.array();
		} else {
			off = 0;
			inBytes = new byte[len];
			in.get(inBytes);
		}

		// update CRC and deflater
		crc.update(inBytes, off, len);
		deflater.setInput(inBytes, off, len);

		while( !deflater.needsInput() ) {
			int r = deflater.deflate(transfer, 0, transfer.length, flushMode);
			buffer.write(transfer, 0, r);
		}

		byte[] outBytes = buffer.toByteArray();
		buffer.reset();
		return ByteBuffer.wrap(outBytes);
	}


	/**
	 * Write the final buffer. This writes any remaining compressed data and the GZIP trailer.
	 * @return the final buffer
	 */
	public ByteBuffer endOfInput() {
		// finish deflating
		deflater.finish();

		// write all remaining data
		int r;
		do {
			r = deflater.deflate(transfer, 0, transfer.length,
					Deflater.FULL_FLUSH);
			buffer.write(transfer, 0, r);
		} while( r == transfer.length );

		// write GZIP trailer
		writeInt((int) crc.getValue());
		writeInt((int) deflater.getBytesRead());

		// reset deflater
		deflater.reset();

		// final output
		byte[] outBytes = buffer.toByteArray();
		buffer.reset();
		return ByteBuffer.wrap(outBytes);
	}


	/**
	 * Write a 32 bit value in little-endian order
	 *
	 * @param v
	 *            the value to write
	 */
	private void writeInt(int v) {
		System.out.println("v="+v);
		buffer.write(v & 0xff);
		buffer.write((v >> 8) & 0xff);
		buffer.write((v >> 16) & 0xff);
		buffer.write((v >> 24) & 0xff);
	}

}