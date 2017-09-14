package net.lacnic.epp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

import net.lacnic.web.registro.api.response.Frame;

public class UtilsFiles {
	protected final static int INT_SZ = 4;
	private final static String GREETING = "<greeting>";

	@SuppressWarnings("resource")
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		// Close the input stream and return bytes
		is.close();

		return bytes;
	}

	public static Frame getFrame(InputStream in_stream) throws Exception {

		int len = 0;
		try {
			len = readBufferSize(in_stream);
		} catch (Exception e) {
			throw new Exception("Failed to read from server [" + e.getClass().toString() + "] [" + e.getMessage() + "]");
		}

		if (len <= 0) {
			return null;
		}

		len -= INT_SZ;

		byte[] in_buf = null;
		try {
			in_buf = readInputBuffer(in_stream, len);
		} catch (Exception e) {
			throw new Exception("Failed to read from server [" + e.getClass().toString() + "] [" + e.getMessage() + "]");

		}

		String value = new String(in_buf);

		if (!value.contains(GREETING))
			value = value.replace("   ", "\n");

		return new Frame(value);
	}

	public static void writeFrame(String xml_to_server, OutputStream out_stream) throws Exception {

		try {

			int len = xml_to_server.getBytes().length;
			writeBufferSize(out_stream, len + INT_SZ);
			out_stream.write(xml_to_server.getBytes(), 0, len);
			out_stream.flush();
		} catch (Exception e) {
			throw new Exception("Failed to write to server [" + e.getClass().toString() + "] [" + e.getMessage() + "]");
		}

	}

	public static void disconnect(SSLSocket sslSocket, InputStream in_stream) throws IOException {
		try {
			if (sslSocket != null) {
				sslSocket.close();
			}
			sslSocket = null;

			if (in_stream != null) {
				in_stream.close();
			}
			in_stream = null;

		} catch (IOException e) {
			throw e;
		}

	}

	protected static int readBufferSize(InputStream in) throws Exception {
		byte[] in_buf = new byte[INT_SZ];

		int len = 0;
		int bytesRead = 0;
		while (bytesRead < INT_SZ) {
			try {
				len = in.read(in_buf, bytesRead, INT_SZ - bytesRead);
			} catch (IOException e) {
				if (e instanceof InterruptedIOException)
					throw e;
				return -1;
			}
			if (len < 0) {
				return -1;
			}
			bytesRead += len;
		}

		return (((in_buf[0] & 0xff) << 24) | ((in_buf[1] & 0xff) << 16) | ((in_buf[2] & 0xff) << 8) | (in_buf[3] & 0xff));

	}

	protected static byte[] readInputBuffer(InputStream in, int inbuf_sz) throws Exception {
		byte[] in_buf = new byte[inbuf_sz];

		int len = 0;
		int bytesRead = 0;
		while (bytesRead < inbuf_sz) {
			try {
				len = in.read(in_buf, bytesRead, inbuf_sz - bytesRead);
			} catch (IOException e) {
				if (e instanceof InterruptedIOException)
					throw e;
				return null;
			}
			if (len < 0) {
				return null;
			}
			bytesRead += len;
		}
		return in_buf;
	}

	protected static void writeBufferSize(OutputStream out, int buf_sz) throws IOException {
		byte[] out_buf = new byte[INT_SZ];
		out_buf[0] = (byte) (0xff & (buf_sz >> 24));
		out_buf[1] = (byte) (0xff & (buf_sz >> 16));
		out_buf[2] = (byte) (0xff & (buf_sz >> 8));
		out_buf[3] = (byte) (0xff & buf_sz);

		out.write(out_buf, 0, INT_SZ);
	}
}
