package com.application.se2.misc;


/**
 * Simple ID-Generator using random numbers and supporting a variety of formats
 * such as ALPHANUM, AIRLINE, NUM, HEX, BIN.
 * 
 * Examples are:
 *  - "C.ED84DX" using prefix "C." followed by a random number of type AIRLINE-code
 *  - "3450629369" as simple 10-digit decimal number
 *  - "A8C86ED4D8" as 10-digit hex number.
 * 
 * @author sgra64
 *
 */
public class IDGenerator {
	private final String prefix;
	private final int len;
	private final String alphabet;

	public enum IDTYPE { ALPHANUM, AIRLINE, NUM, HEX, BIN };

	private static final String[] alphabets = new String[] {
		"0123456789" + "ABCDEGFHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuwvxyz",
		"0123456789" + "ABCDEGFHIJKLMNOPQRSTUVWXYZ",
		"0123456789",
		"0123456789ABCDEF",
		"01"
	};


	/**
	 * Public constructor.
	 * @param _prefix prefix followed by random number.
	 * @param type one of the IDTYPE's.
	 * @param len total number of digits.
	 */
	public IDGenerator( final IDTYPE type, final int len ) {
		this( "", type, len );
	}

	public IDGenerator( final String prefix, final IDTYPE type, final int len ) {
		this.prefix = prefix;
		this.len = len;
		this.alphabet = alphabets[ type.ordinal() ];
	}

	/**
	 * Generate next id.
	 * @return next id according to the format specified in the constructor.
	 */
	public String nextId() {
		final int alphaLen = alphabet.length();
		final int chunkSize = 10;	// generate + append chunks of up to 10 digits (long limit)
		StringBuffer sb = new StringBuffer();
		if( prefix != null ) {
			sb.append( prefix );
		}
		for( int l1 = len; l1 > 0; l1 -= chunkSize ) {
			int l2 = Math.min( chunkSize,  l1 );
			long rnd = (long)( Math.random() * Math.pow( alphaLen, l2 ) );
			for( int i=0; i++ < l2; rnd /= alphaLen ) {
				int k1 = (int)( rnd % alphaLen );
				sb.append( alphabet.charAt( (int)k1 ) );
			}
		}
		for( int i=sb.length(); i < len; i++ ) {
			sb.insert( 0, "0" );
		}
		return sb.toString();
	}

}
