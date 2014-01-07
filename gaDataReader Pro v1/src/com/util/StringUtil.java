package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: The class provides methods to check validation of user's input
 * </p>
 * 
 * @author Somnath Ghosh
 * @version 1.0
 */

public class StringUtil
	{
		/**
		 * constructor
		 */
		private StringUtil()
			{
			}

		/**
		 * check if the string is blank. If the length of the character string
		 * is 0, of string is null then it should return true
		 * 
		 * @param val
		 *            String
		 * @return boolean
		 */

		public static boolean isBlank( String val )
			{
				if ( val == null || "".equals( val.trim() ) )
					{
						return true;
					}
				return false;
			}

		/**
		 * check if blank is included in the character string If tab,1 byte
		 * space blank and 2 bytes space blank are included in the character
		 * string , retrun true.
		 * 
		 * @param val
		 *            String
		 * @return boolean
		 */

		public static boolean isIncludeSpace( String val )
			{
				char[] ca = val.toCharArray();
				for ( int i = 0 ; i < ca.length ; i++ )
					{
						char c = ca [i];
						if ( c == '\t' || c == ' ' || c == '\u3000' )
							{
								return true;
							}
					}
				return false;
			}

		/**
		 * check number and alphabet If 0~9,a~z,A~Z are included inthe character
		 * string return true
		 * 
		 * @param val
		 *            String
		 * @return boolean
		 */

		public static boolean isAlphaNumeric( String val )
			{
				// [\w]* is equivalent to [a-zA-z0-09_], so:
				if ( val.matches( "[\\w]*" ) )
					{
						return true;
					}
				else
					{
						return false;
					}

			}

		public static boolean isAlphabetic( String val )
			{
				String lString = "[A-Za-z]";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( val );
				return !lMatcher.matches();
			}

		/**
		 * @param val
		 * @return
		 */
		public static boolean isIncludeSpecChar( String val )
			{
				String lString = "[A-Za-z0-9 ]+";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( val );
				return !lMatcher.matches();
			}

		/**
		 * check number If 0~9 are included inthe character string return true
		 * 
		 * @param val
		 *            String
		 * @return boolean
		 */

		public static boolean isNumeric( String val )
			{
				char[] ca = val.toCharArray();
				for ( int i = 0 ; i < ca.length ; i++ )
					{
						char c = ca [i];
						if ( c < '0' || c > '9' )
							{
								return false;
							}
					}
				return true;
			}

		/**
		 * check date if the date is right by given format return true
		 * 
		 * @param val
		 *            String
		 * @param fmt
		 *            String date format will be in yyyy/MM/dd time format:
		 *            HH:mm:ss
		 * @return boolean
		 */

		public static boolean isDate( String val , String fmt )
			{
				try
					{
						//
						DateFormat df = new SimpleDateFormat( fmt );
						df.setLenient( false );
						df.parse( val );

						Calendar cal = df.getCalendar();
						Calendar now = Calendar.getInstance();
						if ( !cal.isSet( Calendar.YEAR ) )
							{
								cal.set( Calendar.YEAR , now.get( Calendar.YEAR ) );
							}
						if ( !cal.isSet( Calendar.MONTH ) )
							{
								cal.set( Calendar.MONTH , now.get( Calendar.MONTH ) );
							}
						if ( !cal.isSet( Calendar.DAY_OF_MONTH ) )
							{
								cal.set( Calendar.DAY_OF_MONTH , now.get( Calendar.DAY_OF_MONTH ) );
							}
						if ( !cal.isSet( Calendar.HOUR_OF_DAY ) )
							{
								cal.set( Calendar.HOUR_OF_DAY , now.get( Calendar.HOUR_OF_DAY ) );
							}
						if ( !cal.isSet( Calendar.MINUTE ) )
							{
								cal.set( Calendar.MINUTE , now.get( Calendar.MINUTE ) );
							}
						if ( !cal.isSet( Calendar.SECOND ) )
							{
								cal.set( Calendar.SECOND , now.get( Calendar.SECOND ) );
							}
						if ( !cal.isSet( Calendar.MILLISECOND ) )
							{
								cal.set( Calendar.MILLISECOND , now.get( Calendar.MILLISECOND ) );
							}
						//
						Date date = df.getCalendar().getTime();
					}
				catch ( Exception ex )
					{
						return false;
					}
				return true;
			}

		/**
		 * to cancel the blanks on the right of the character string.
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String rtrimSpace( String val )
			{
				if ( val == null || val.length() == 0 )
					{
						return val;
					}
				int len = val.length();
				int end = len;
				while ( end > 0 && val.charAt( end - 1 ) == '\u0020' )
					{
						end-- ;
					}
				if ( end == len )
					{
						return val;
					}
				else
					{
						return val.substring( 0 , end );
					}
			}

		/**
		 * to remove the all the other characters other than alphabets and
		 * numerals and return the String
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String removeSpecialChars( String val )
			{
				char[] ca = val.toCharArray();
				StringBuffer sb = new StringBuffer();
				for ( int i = 0 ; i < ca.length ; i++ )
					{
						char c = ca [i];
						if ( ( c >= '0' && c <= '9' ) || ( c >= 'A' && c <= 'Z' ) || ( c >= 'a' && c <= 'z' ) )
							{
								sb.append( c );
							}
					}
				return sb.toString();
			}

		/**
		 * to cancel the 1 byte blank and 2 bytes space blank on the right of
		 * the character string.
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String rtrimAllSpace( String val )
			{
				if ( val == null || val.length() == 0 )
					{
						return val;
					}
				char c;
				int len = val.length();
				int begin = 0;
				int end = len;
				while ( begin < end )
					{
						c = val.charAt( end - 1 );
						if ( ! ( c <= ' ' || c == '\u3000' ) )
							{
								break;
							}
						end-- ;
					}
				if ( begin == 0 && end == len )
					{
						return val;
					}
				else
					{
						return val.substring( begin , end );
					}
			}

		/**
		 * to cancel the 1 byte space blank and 2 bytes space on the left of the
		 * character string.
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String ltrimAllSpace( String val )
			{
				if ( val == null || val.length() == 0 )
					{
						return val;
					}
				char c;
				int len = val.length();
				int begin = 0;
				int end = len;
				while ( begin < end )
					{
						c = val.charAt( begin );
						if ( ! ( c <= ' ' || c == '\u3000' ) )
							{
								break;
							}
						begin++ ;
					}
				if ( begin == 0 && end == len )
					{
						return val;
					}
				else
					{
						return val.substring( begin , end );
					}
			}

		/**
		 * to delete both of the 1 byte space blank and the 2 bytes space blank
		 * on each side of the character string.
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String trimAllSpace( String val )
			{
				if ( val == null || val.length() == 0 )
					{
						return val;
					}
				char c;
				int len = val.length();
				int begin = 0;
				int end = len;
				while ( begin < end )
					{
						c = val.charAt( begin );
						if ( ! ( c <= ' ' || c == '\u3000' ) )
							{
								break;
							}
						begin++ ;
					}
				while ( begin < end )
					{
						c = val.charAt( end - 1 );
						if ( ! ( c <= ' ' || c == '\u3000' ) )
							{
								break;
							}
						end-- ;
					}
				if ( begin == 0 && end == len )
					{
						return val;
					}
				else
					{
						return val.substring( begin , end );
					}
			}

		/**
		 * to cancel "0" on the left of the character string
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String ltrimZero( String val )
			{
				if ( val == null || val.length() == 0 )
					{
						return val;
					}
				int len = val.length();
				int begin = 0;
				while ( begin < len && val.charAt( begin ) == '\u0030' )
					{
						begin++ ;
					}
				if ( begin == 0 )
					{
						return val;
					}
				else
					{
						return val.substring( begin , len );
					}
			}

		/**
		 * add "0" on the left of character string
		 * 
		 * @param val
		 *            String
		 * @return String
		 */

		public static String lpadZero( String val , int size )
			{
				if ( val == null )
					{
						return val;
					}
				try
					{
						byte[] oldBytes = val.getBytes( "GB2312" );
						int oldSize = oldBytes.length;
						if ( size <= oldSize )
							{
								return val;
							}

						int padSize = size - oldSize;
						byte[] newBytes = new byte [size];
						System.arraycopy( oldBytes , 0 , newBytes , padSize , oldSize );
						for ( int j = 0 ; j < padSize ; j++ )
							{
								newBytes [j] = 0x30;
							}
						return new String( newBytes , 0 , size , "GB2312" );
					}
				catch ( java.io.UnsupportedEncodingException ex )
					{
						ex.printStackTrace();
						return null;
					}
			}

		/**
		 * get current date and return the current date to String
		 * 
		 * @param pDef
		 *            String
		 */
		public static String getDefDate( String pDef )
			{
				return ( new SimpleDateFormat( pDef ) ).format( new Date() );
			}

		/**
		 * verify wether a String is a ip
		 * 
		 * @param pHostIP
		 * @return
		 */
		public static boolean isIP( String pHostIP )
			{
				String lHostIP = pHostIP;
				lHostIP = trimAllSpace( lHostIP );

				boolean lFlag = true;
				String[] lNumber = lHostIP.split( "\\." );
				if ( lNumber != null && lNumber.length == 4 )
					{
						for ( int i = 0 ; i < 4 ; i++ )
							{
								try
									{
										if ( Integer.parseInt( lNumber [i] ) > 255 || ( Integer.parseInt( lNumber [i] ) < 0 ) )
											{
												lFlag = false;
												break;
											}
									}
								catch ( Exception e )
									{
										return false;
									}
							}
					}
				else
					{
						lFlag = false;
					}

				return lFlag;
			}

		/**
		 * Ingnore the lower case of upper case a user input
		 * 
		 * @param pSource
		 * @param pOldString
		 * @param pNewString
		 * @return
		 */
		public static String replaceIgnoreCase( String pSource , String pOldString , String pNewString )
			{
				if ( pSource == null )
					{
						return null;
					}
				String lSource = pSource.toLowerCase();
				String lOldString = pOldString.toLowerCase();
				int i = 0;
				if ( ( i = lSource.indexOf( lOldString , i ) ) >= 0 )
					{
						char[] lSourceArray = pSource.toCharArray();
						char[] lNewStringArray = pNewString.toCharArray();
						int oLength = pOldString.length();
						StringBuffer lStringBuffer = new StringBuffer( lSourceArray.length );
						lStringBuffer.append( lSourceArray , 0 , i ).append( lNewStringArray );
						i += oLength;
						int j = i;
						while ( ( i = lSource.indexOf( lOldString , i ) ) > 0 )
							{
								lStringBuffer.append( lSourceArray , j , i - j ).append( lNewStringArray );
								i += oLength;
								j = i;
							}
						lStringBuffer.append( lSourceArray , j , lSourceArray.length - j );
						return lStringBuffer.toString();
					}
				return pSource;
			}

		/**
		 * @param pSource
		 * @param pOldString
		 * @param pNewString
		 * @return
		 */
		public static String replace( String pSource , String pOldString , String pNewString )
			{
				if ( pSource == null )
					{
						return null;
					}
				String lSource = pSource;
				String lOldString = pOldString;
				int i = 0;
				if ( ( i = lSource.indexOf( lOldString , i ) ) >= 0 )
					{
						char[] lSourceArray = pSource.toCharArray();
						char[] lNewStringArray = pNewString.toCharArray();
						int oLength = pOldString.length();
						StringBuffer lStringBuffer = new StringBuffer( lSourceArray.length );
						lStringBuffer.append( lSourceArray , 0 , i ).append( lNewStringArray );
						i += oLength;
						int j = i;
						while ( ( i = lSource.indexOf( lOldString , i ) ) > 0 )
							{
								lStringBuffer.append( lSourceArray , j , i - j ).append( lNewStringArray );
								i += oLength;
								j = i;
							}
						lStringBuffer.append( lSourceArray , j , lSourceArray.length - j );
						return lStringBuffer.toString();
					}
				return pSource;
			}

		/**
		 * Transfer file to Byte
		 * 
		 * @param pFileName
		 * @return
		 */
		public static byte[] fileToByte( String pFileName )
			{
				byte[] lByteArray = null;
				File lFile = null;
				FileInputStream lFileInputStream = null;

				try
					{
						lFile = new File( pFileName );
						lFileInputStream = new FileInputStream( pFileName );
					}
				catch ( FileNotFoundException e )
					{
						e.printStackTrace();
					}
				long lLength = lFile.length();
				lByteArray = new byte [(int) lLength];
				try
					{
						lFileInputStream.read( lByteArray );
					}
				catch ( Exception e )
					{
						e.getMessage();
					}
				return lByteArray;
			}

		/**
		 * generate a random string not include "I" and "1"
		 * 
		 * @param length
		 * @return
		 */
		public static String getRandomString( int length )
			{
				String sChars;
				String sPswd = "";
				int iIndex = 0;
				sChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
				Random x = new Random(); // default seed is time in milliseconds
				for ( int i = 0 ; i < length ; i++ )
					{
						iIndex = x.nextInt( 31 ) + 1;
						sPswd = sPswd + sChars.substring( iIndex , ( iIndex + 1 ) );
					}
				return sPswd;
			}

		/**
		 * chech email addess.
		 * 
		 * @param pEmail
		 * @return
		 */
		public static boolean isEmail( String pEmail )
			{
				if ( pEmail == null || pEmail.trim().equals( "" ) )
					{
						return false;
					}
				String lString = "^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT|us|US|biz|BIZ)$";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pEmail );
				return lMatcher.matches();
			}

		/**
		 * check phone number.
		 * 
		 * @param pPhoneNumber
		 * @return
		 */
		public static boolean isPhoneNumber( String pPhoneNumber )
			{
				if ( pPhoneNumber == null || pPhoneNumber.trim().equals( "" ) )
					{
						return false;
					}
				String lString = "^\\(?(\\d{3})\\)?[-| ]?(\\d{3})[-| ]?(\\d{4})$";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pPhoneNumber );
				return lMatcher.matches();
			}

		/**
		 * check phone number. and return the required counter
		 * 
		 * @param pPhoneNumber
		 *            , pCounter
		 * @return required counter
		 **/
		public static String[] splitPhoneNumber( String pPhoneNumber )
			{
				pPhoneNumber = pPhoneNumber.replaceAll( "-" , "" );
				pPhoneNumber = pPhoneNumber.replaceAll( " x" , "" );
				pPhoneNumber = pPhoneNumber.replaceAll( "\\(" , "" );
				pPhoneNumber = pPhoneNumber.replaceAll( "\\)" , "" );
				pPhoneNumber = pPhoneNumber.replaceAll( "ext." , "" );
				pPhoneNumber.trim();

				String[] lSplittedPhoneNUmber = new String [4];
				String PHONE_NUMBER_RE = "^\\(?(\\d{3})\\)?[-| ]?(\\d{3})[-| ]?(\\d{4})[-| ]?(\\d{1,9})?$";
				Pattern lPattern = Pattern.compile( PHONE_NUMBER_RE );
				Matcher lMatcher = lPattern.matcher( pPhoneNumber );

				if ( lMatcher.find() )
					{
						lSplittedPhoneNUmber [0] = lMatcher.group( 1 );
						lSplittedPhoneNUmber [1] = lMatcher.group( 2 );
						lSplittedPhoneNUmber [2] = lMatcher.group( 3 );
						lSplittedPhoneNUmber [3] = lMatcher.group( 4 );
					}
				/*
				 * if (pCounter==1) return lFirstCounter; else if (pCounter==2)
				 * return lSecondCounter; else if (pCounter==3) return
				 * lThirdCounter; else return "";
				 */
				return lSplittedPhoneNUmber;
			}

		/**
		 * Arijit Format the phone number for display Only valid for 10 digit
		 * phone number
		 * 
		 * @param pPhoneNumber
		 * @return pPhoneNumber
		 **/
		/*
		 * public static String formatPhoneNumber(String pPhoneNumber){
		 * StringBuffer lTempNumber = new StringBuffer(pPhoneNumber);
		 * lTempNumber =
		 * lTempNumber.insert(6,'.').insert(3,'.').insert(0,'.').insert(0,'1');
		 * String lTempNumber=""; String lFirstCounter =""; String
		 * lSecondCounter =""; String lThirdCounter =""; String PHONE_NUMBER_RE
		 * = "^\\(?(\\d{3})\\)?[-| ]?(\\d{3})[-| ]?(\\d{4})$"; Pattern lPattern
		 * = Pattern.compile(PHONE_NUMBER_RE); Matcher lMatcher =
		 * lPattern.matcher(pPhoneNumber); if(lMatcher.find()){ lFirstCounter =
		 * lMatcher.group(1); lSecondCounter= lMatcher.group(2); lThirdCounter =
		 * lMatcher.group(3); }
		 * lTempNumber="("+lFirstCounter+") "+lSecondCounter+"-"+lThirdCounter;
		 * return lTempNumber; }
		 */

		public static String formatPhoneNumber( String pPhoneNumber )
			{
				if ( pPhoneNumber == null || "null".equalsIgnoreCase( pPhoneNumber ) || pPhoneNumber.trim().length() == 0 )
					return "";
				else
					{
						String pattern = ".*[a-zA-Z].*";
						boolean matches = Pattern.matches( pattern , pPhoneNumber );
						if ( matches )
							return pPhoneNumber;
						pPhoneNumber = getNumbersOnly( pPhoneNumber );
					}
				String extn = "";
				if ( pPhoneNumber != null && pPhoneNumber.length() > 10 )
					{
						extn = " ext." + pPhoneNumber.substring( 10 );
						pPhoneNumber = pPhoneNumber.substring( 0 , 10 );
					}

				String lTempNumber = "";
				String lFirstCounter = "";
				String lSecondCounter = "";
				String lThirdCounter = "";
				String PHONE_NUMBER_RE = "^\\(?(\\d{3})\\)?[-| ]?(\\d{3})[-| ]?(\\d{4})$";
				Pattern lPattern = Pattern.compile( PHONE_NUMBER_RE );
				Matcher lMatcher = lPattern.matcher( pPhoneNumber );

				if ( lMatcher.find() )
					{
						lFirstCounter = lMatcher.group( 1 );
						lSecondCounter = lMatcher.group( 2 );
						lThirdCounter = lMatcher.group( 3 );
					}
				lTempNumber = "(" + lFirstCounter + ") " + lSecondCounter + "-" + lThirdCounter + extn;
				if ( lTempNumber.trim().length() > 4 )
					return lTempNumber;
				else
					return pPhoneNumber;
			}

		public static String getNumbersOnly( String name )
			{
				StringBuilder result = new StringBuilder();
				for ( int i = 0 ; i < name.length() ; i++ )
					{
						char tmpChar = name.charAt( i );
						if ( Character.isDigit( tmpChar ) )
							{
								result.append( tmpChar );
							}
					}
				return result.toString();
			}

		/**
		 * Arijit Format the phone number for display The format is like
		 * 983-061-5940 x033
		 * 
		 * @param pPhoneNumber
		 * @return pPhoneNumber
		 **/
		public static String newFormatPhoneNumber( String pPhoneNumber )
			{
				String lFirstCounter = "";
				String lSecondCounter = "";
				String lThirdCounter = "";
				String lFouthCounter = "";
				String lFinalPhoneNumber = "";

				if ( !pPhoneNumber.contains( "-" ) )
					{
						String PHONE_NUMBER_RE = "^\\(?(\\d{3})\\)?[-| ]?(\\d{3})[-| ]?(\\d{4})[-| ]?(\\d{1,9})?$";
						Pattern lPattern = Pattern.compile( PHONE_NUMBER_RE );
						Matcher lMatcher = lPattern.matcher( pPhoneNumber );

						if ( lMatcher.find() )
							{
								lFirstCounter = lMatcher.group( 1 ) == null ? "" : lMatcher.group( 1 ) + "-";
								lSecondCounter = lMatcher.group( 2 ) == null ? "" : lMatcher.group( 2 ) + "-";
								lThirdCounter = lMatcher.group( 3 ) == null ? "" : lMatcher.group( 3 );
								lFouthCounter = lMatcher.group( 4 ) == null ? "" : " x" + lMatcher.group( 4 );
							}
						lFinalPhoneNumber = lFirstCounter + lSecondCounter + lThirdCounter + lFouthCounter;
					}
				else
					{
						lFinalPhoneNumber = pPhoneNumber;
					}
				return lFinalPhoneNumber;
			}

		/**
		 * Arijit Format the fax number for display
		 * 
		 * @param pPhoneNumber
		 * @return pPhoneNumber
		 **/
		public static String formatFaxNumber( String pFaxNumber )
			{
				String lTempFaxNumber = pFaxNumber;
				lTempFaxNumber = lTempFaxNumber.replace( " " , "" );
				lTempFaxNumber = lTempFaxNumber.replace( "," , "" );
				lTempFaxNumber = lTempFaxNumber.replace( ")" , "" );
				lTempFaxNumber = lTempFaxNumber.replace( "(" , "" );
				lTempFaxNumber = lTempFaxNumber.replace( "-" , "" );
				lTempFaxNumber = lTempFaxNumber.replace( "." , "" );
				if ( lTempFaxNumber.length() > 0 )
					{
						if ( lTempFaxNumber.substring( 0 , 1 ) != "1" )
							lTempFaxNumber = "1" + lTempFaxNumber;
					}
				return lTempFaxNumber;
			}

		/**
		 * check zip
		 * 
		 * @param pZip
		 * @return
		 */
		public static boolean isZip( String pZip )
			{
				if ( pZip == null || pZip.trim().equals( "" ) )
					{
						return false;
					}
				String lString = "^\\d{5}(-\\d{4})?$";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pZip );
				return lMatcher.matches();
			}

		/**
		 * check first name and last name
		 * 
		 * @param pName
		 * @return
		 */
		public static boolean isName( String pName )
			{
				String lString = "[A-Za-z0-9 \\-,.~*]+";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pName );
				return lMatcher.matches();

			}

		/**
		 * check first address (street)
		 * 
		 * @param pName
		 * @return
		 */
		public static boolean isAddress( String pName )
			{
				String lString = "[A-Za-z0-9 .\\-,'#~*]+";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pName );
				return lMatcher.matches();

			}

		/**
		 * check first address (street)
		 * 
		 * @param pName
		 * @return
		 */
		public static boolean isCity( String pName )
			{
				String lString = "[A-Za-z0-9 \\-,#.*~]+";
				Pattern lPattern = Pattern.compile( lString );
				Matcher lMatcher = lPattern.matcher( pName );
				return lMatcher.matches();

			}

		/**
		 * Deterimnes whether a card that expires on a given month/year has
		 * expired. Does this by testing whether or not month-01-year is before
		 * (this_month-01-this_year - 1 day) Example: if this month is May, this
		 * method will test whether month-01-year came before 04-30-2004. This
		 * is so that cards that expire during may are still valid.
		 * 
		 * @param _month
		 *            String
		 * @param _year
		 *            String
		 * @throws ParseException
		 * @return boolean
		 */

		public static boolean isCardExpired( String _month , String _year ) throws ParseException
			{
				// first, get a Calendar set to just before the first day of
				// this month.
				Calendar baseCal = Calendar.getInstance();
				baseCal.set( Calendar.DAY_OF_MONTH , 0 );
				baseCal.set( Calendar.HOUR_OF_DAY , 23 );
				baseCal.set( Calendar.MINUTE , 59 );

				// now initialize a Calendar set to the desired date
				DateFormat df = new SimpleDateFormat( "MM-yyyy" );
				Date testDate = df.parse( _month + "-" + _year );
				Calendar testDateCal = Calendar.getInstance();
				testDateCal.setTime( testDate );

				return testDateCal.before( baseCal );
			}

		public static String removeHTMLTag( String pHtml )
			{
				Pattern lPattern = null;
				Matcher lMatcher = null;
				String s;
				lPattern = Pattern.compile( "</?\\w+?\\s?\\w+?=?\\w+>" );
				lMatcher = lPattern.matcher( pHtml );
				s = lMatcher.replaceAll( "" );
				return s;

			}

		public static String removeHTMLElement( String pHtml )
			{
				Pattern lPattern = null;
				Matcher lMatcher = null;
				String s;
				lPattern = Pattern.compile( "</?\\w+>" );
				lMatcher = lPattern.matcher( pHtml );
				s = lMatcher.replaceAll( "" );
				return s;

			}

		public static String formatStringForValidXml( String pHtml )
			{
				// pHtml = pHtml.replaceAll( "&" , "&amp;" ).replaceAll( "'" ,
				// "''" ).replaceAll( "%" , "&#37;" );
				pHtml = pHtml.replaceAll( "%" , "&#37;" );
				return pHtml;
			}

		/**
		 * Take the String and converts first to lower case and then make an
		 * array and finally converts to Upper Case Example: if String is Hello
		 * World, this method first make an String array by splitting white
		 * space. Before converting to lower case we check whether the elements
		 * of the array is not null. converts all elements of the array to lower
		 * case (hello world) and againg make char array , and converts first
		 * character to upper case.
		 * 
		 * @param _DataString
		 * @return String
		 */
		public static String toTitleCase( String _DataString )
			{
				// null check
				if ( _DataString == null || "".equals( _DataString ) )
					{
						return _DataString;
					}
				String words[] = _DataString.trim().split( " " );
				_DataString = "";
				for ( int i = 0 ; i < words.length ; i++ )
					{
						if ( !words [i].equals( "" ) )
							{
								words [i] = words [i].toLowerCase();
								char[] c = words [i].toCharArray();
								c [0] = Character.toUpperCase( c [0] );
								words [i] = new String( c );
								_DataString += words [i] + " ";
							}
					}
				_DataString = _DataString.trim();
				return _DataString;
			}

		/**
		 * Deterimnes whether a card that expires on a given month/year has
		 * expired. Does this by testing whether or not month-01-year is before
		 * (this_month-01-this_year - 1 day) Example: if this month is May, this
		 * method will test whether month-01-year came before 04-30-2004. This
		 * is so that cards that expire during may are still valid.
		 * 
		 * @param _date
		 *            Date
		 * @param _fr
		 *            String
		 * @throws ParseException
		 * @return boolean
		 */
		public static String DateToString( java.util.Date inputdate , String format )
			{
				String result = "";
				DateFormat Format = null;
				Format = new SimpleDateFormat( format );
				result = Format.format( inputdate );
				return result;
			}

		public static final Date convertStringToDate( String aMask , String strDate ) throws ParseException
			{
				SimpleDateFormat df = null;
				Date date = null;
				df = new SimpleDateFormat( aMask );

				try
					{
						date = df.parse( strDate );
					}
				catch ( ParseException pe )
					{
					}
				return date;
			}

		// to make two places of decimals
		public static final String StringFormatKeepTwo( Object format )
			{
				// double number = Double.parseDouble(format);
				String convertString = new java.text.DecimalFormat( "###.##" ).format( Float.parseFloat( String.valueOf( format ) ) );
				return convertString;
			}

		/**
		 * Shamelessly stolen & modified from
		 * http://www.rgagnon.com/javadetails/java-0448.html.
		 * 
		 * @param _str
		 *            - string to pad
		 * @param _totalLength
		 *            - desired length
		 * @param _padWith
		 *            - char to pad with
		 * @param _fromLeft
		 *            - pad from left or from right?
		 * @return
		 */
		public static String padStr( String _str , int _totalLength , char _padWith , boolean _fromLeft )
			{
				int strLength = _str.length();
				// do we need to pad?
				if ( _totalLength > 0 && _totalLength > strLength )
					{
						StringBuffer str = new StringBuffer( _str );
						// for each char that we need to pad...
						for ( int i = 0 ; i <= _totalLength ; i++ )
							{
								if ( _fromLeft )
									{
										if ( i < _totalLength - strLength )
											str.insert( 0 , _padWith );
									}
								else
									{
										if ( i > strLength )
											str.append( _padWith );
									}
							}
						return str.toString();
					}
				return _str;
			}

		/**
		 * Escapes SQL-sensitive chars (currently just single-quotes) in strings
		 * for insertion into an sql query.
		 * 
		 * @param _in
		 * @return
		 */

		public static String escapeSql( String _in )
			{
				// sql cares about single quotes
				return _in.replaceAll( "'" , "''" );
			}

		/*
		 * For remove all html tag
		 */

		public static String removeHTML( String pString )
			{
				String lString = pString.replaceAll( "&nbsp;" , "" );
				char[] lFinalString = new char [lString.length()];
				int lFinalStrIndex = 0;
				char[] c = lString.toCharArray();
				int startIndex = -1;
				int lastIndex = -1;
				boolean isTake = true;
				for ( int i = 0 ; i < c.length ; i++ )
					{
						if ( c [i] == '<' )
							{
								startIndex = i;
								isTake = false;
							}
						else if ( c [i] == '>' )
							{
								lastIndex = i;
								isTake = true;
								continue;
							}
						if ( isTake )
							{
								lFinalString [lFinalStrIndex++ ] = c [i];
							}
					}
				return new String( lFinalString ).trim();
			}

		/**
		 * @author Maha
		 * @param pSource
		 * @param pPlaceholder
		 * @param pReplacementStringArray
		 * @param pPrefix
		 * @param pPostfix
		 * @return
		 * @throws Exception
		 */
		public static String replaceFromStringArray( String pSource , String pPlaceholder , String[] pReplacementStringArray , String pPrefix ,
														String pPostfix ) throws Exception
			{
				if ( pSource == null || pPlaceholder == null || pReplacementStringArray == null || pSource.equals( "" ) || pPlaceholder.equals( "" ) )
					return pSource;

				boolean lAppendReplacementToEnd = false;
				String lReturnString = "";
				if ( pPrefix == null )
					pPrefix = "";
				if ( pPostfix == null )
					pPostfix = "";
				String[] lSplitString = pSource.split( pPlaceholder );
				String lastChars = pSource.substring( pSource.length() - pPlaceholder.length() , pSource.length() );

				if ( lastChars.equals( pPlaceholder.replace( "\\" , " " ) ) ) // handle
																				// regular
																				// expressions
					{
						lAppendReplacementToEnd = true;
					}
				//System.out.println("lSplitString::"+lSplitString.length+", pReplacementStringArray ::"+pReplacementStringArray.length);
				for ( int i = 0 ; i < lSplitString.length - 1 ; i++ )
					{
						try
							{
								if(i < pReplacementStringArray.length)
									lReturnString += lSplitString [i] + pPrefix + pReplacementStringArray [i] + pPostfix;
							}
						catch ( Exception e )
							{
								e.printStackTrace();
								continue;
							}
					}
				if ( lAppendReplacementToEnd )
					return lReturnString += lSplitString [lSplitString.length - 1] + pPrefix
							+ pReplacementStringArray [pReplacementStringArray.length - 1] + pPostfix;
				else
					return lReturnString += lSplitString [lSplitString.length - 1];
			}

		/**
		 * @author James Mortensen
		 * @param Takes
		 *            a String representing the Source
		 * @param Takes
		 *            a String representing a placeholder where all instances of
		 *            pPlaceholder will be replaced with the contents of
		 *            pReplacementStringArray
		 * @param Takes
		 *            a String[] type containing the values that will replace
		 *            all instances of pPlaceholder in numerical order.
		 * @param Takes
		 *            a String representing text that will prefix the array
		 *            contents. This can be null or an empty String.
		 * @param Takes
		 *            a String representing text that will be placed after the
		 *            String array values. This can be null or an empty String.
		 * @return Returns a String where all instances of the pPlaceholder are
		 *         replaced with the values in the pReplacementStringArray,
		 *         wrapped with the prefix and postfix. This method will return
		 *         an empty String if the following preconditions are not met: -
		 *         The Size of pReplacementStringArray must equal the number of
		 *         instances of pPlaceholder in pSource. If pSource,
		 *         pPlaceholder, or pReplacementStringArray are null or empty
		 *         Strings, then the Source is returned.
		 */
		public static String replaceAllFromStringArray( String pSource , String pPlaceholder , String[] pReplacementStringArray , String pPrefix ,
														String pPostfix ) throws Exception
			{
				if ( pSource == null || pPlaceholder == null || pReplacementStringArray == null || pSource.equals( "" ) || pPlaceholder.equals( "" ) )
					return pSource;

				boolean lAppendReplacementToEnd = false;
				String lReturnString = "";
				if ( pPrefix == null )
					pPrefix = "";
				if ( pPostfix == null )
					pPostfix = "";

				String[] lSplitString = pSource.split( pPlaceholder );

				String lastChars = pSource.substring( pSource.length() - pPlaceholder.length() , pSource.length() );

				// handle regular expressions
				if ( lastChars.equals( pPlaceholder.replace( "\\" , " " ) ) )
					{
						lAppendReplacementToEnd = true;
					}

				// throw exception if one of these conditions is not met.
				if ( ( ( lSplitString.length != pReplacementStringArray.length + 1 ) && !lAppendReplacementToEnd )
						|| ( ( lSplitString.length + 1 != pReplacementStringArray.length + 1 ) && lAppendReplacementToEnd ) )

					{
						ArrayList lReplacementStringList = new ArrayList();
						for ( int i = 0 ; i < pReplacementStringArray.length ; i++ )
							lReplacementStringList.add( pReplacementStringArray [i] );
						throw new Exception( "replaceAllFromStringArray:: Array Size and number of instances of placeholder is not equal::"
								+ "\n   ::pReplacementStringArray length = " + ( pReplacementStringArray.length + 1 )
								+ "\n   ::pSplitString.length = number of pPlaceholders = " + lSplitString.length + "\n   ::pSource = " + pSource
								+ "\n   ::pSource.length() = " + pSource.length() + "\n   ::pPlaceholder = >" + pPlaceholder + "<"
								+ "\n   ::pPlaceholder.length() = " + pPlaceholder.length() + "\n   ::lastChars = >" + lastChars + "<"
								+ "\n   ::lastChars.length() = " + lastChars.length() + "\n   ::lAppendReplacementToEnd = "
								+ Boolean.toString( lAppendReplacementToEnd ) + "\n   ::lReplacementStringList = "
								+ lReplacementStringList.toString() );
					}

				for ( int i = 0 ; i < lSplitString.length - 1 ; i++ )
					{
						lReturnString += lSplitString [i] + pPrefix + pReplacementStringArray [i] + pPostfix;
					}

				if ( lAppendReplacementToEnd )
					return lReturnString += lSplitString [lSplitString.length - 1] + pPrefix
							+ pReplacementStringArray [pReplacementStringArray.length - 1] + pPostfix;
				else
					return lReturnString += lSplitString [lSplitString.length - 1];
			}

		/**
		 * @author James Mortensen
		 * @param Takes
		 *            a String representing the SQL for a prepared statement
		 * @param Takes
		 *            an array of Strings that represent the parameters for the
		 *            SQL statement
		 * @return Returns a String where all ? marks are replaced with the
		 *         parameters wrapped with single quotes (') If
		 *         pPreparesStatementString is null or an empty String, it is
		 *         returned. If pBindVariables is null then the prepared
		 *         statement is returned unchanged. This method is used to
		 *         convert prepared statement text into a String that can be
		 *         printed to the log or used for other purposes where it is
		 *         necessary to embed the parameters in the SQL String.
		 */
		public static String replacePreparedStatementSQLWithParameters( String pPreparedStatementSQL , String[] pBindVariables ) throws Exception
			{
				return replaceAllFromStringArray( pPreparedStatementSQL , "\\?" , pBindVariables , "'" , "'" );
			}

		public static String trimAccountNumber( String lAccountNumber )
			{
				return lAccountNumber.replaceAll( "-" , "" ).replaceAll( "\\(" , "" ).replaceAll( "\\)" , "" ).replaceAll( "\\." , "" )
						.replaceAll( " " , "" );
			}

		public static boolean isStringContains( String val , String lString )
			{
				Pattern lPattern = Pattern.compile( val );
				Matcher lMatcher = lPattern.matcher( lString );
				return lMatcher.find();
			}

		public static TreeMap <String , String> getRequestParameters( HttpServletRequest request )
			{
				Map requestParamMap = request.getParameterMap();
				TreeMap <String , String> paramMap = new TreeMap <String , String>();
				Iterator ite = requestParamMap.keySet().iterator();
				String paramName = "";
				while ( ite.hasNext() )
					{
						paramName = String.valueOf( ite.next() );
						paramMap.put( paramName , request.getParameter( paramName ).trim() );
					}
				return paramMap;
			}
	}
