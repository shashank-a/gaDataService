package com.util;

import java.io.ByteArrayOutputStream; 
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipData
	{
		public static byte[] compressBytes( String data ) throws UnsupportedEncodingException , IOException
			{
			System.out.println("Size of string Data before compression::"+data.length());
				byte[] input = data.getBytes(); // the format... data is the total string
				Deflater df = new Deflater(9); // this function mainly generate the byte code
				df.setInput( input );
				ByteArrayOutputStream baos = new ByteArrayOutputStream( input.length ); // we write the generated byte code in this array
				df.finish();
				byte[] buff = new byte [1024]; // segment segment pop....segment set 1024
				while ( !df.finished() )
					{
						int count = df.deflate( buff ); // returns the generated code... index
						baos.write( buff , 0 , count ); // write 4m 0 to count
					}
				baos.close();
				byte[] output = baos.toByteArray();
				System.out.println("Size of byte array after compression::"+output.length);
				return output;
			}

		public static String extractBytes( byte[] input ) throws UnsupportedEncodingException , IOException , DataFormatException
			{
				Inflater ifl = new Inflater(); // mainly generate the extraction
				ifl.setInput( input );
				ByteArrayOutputStream baos = new ByteArrayOutputStream( input.length );
				byte[] buff = new byte [1024];
				while ( !ifl.finished() )
					{
						int count = ifl.inflate( buff );
						baos.write( buff , 0 , count );
					}
				baos.close();	
				byte[] output = baos.toByteArray();
				return new String( output );
				//return output;
			}
	}