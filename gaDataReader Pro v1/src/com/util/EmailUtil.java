package com.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.acti.ar.common.util.StackTraceWriter;


/**
 * <p>
 * Title: Email Utility Class
 * </p>
 * <p>
 * Description: This class is designed to manage email sending.
 * </p>
 * 
 * @author Somnath Ghosh
 * @version 1.0
 */
public class EmailUtil
	{

		ResourceBundle resourceBundle = ResourceBundle.getBundle( "GaReportConstant" );
		String mode = resourceBundle.getString( "mode" );
		private String mailer = "EmailUtil";
		private boolean mDevMode = true;
		private boolean isCSV = false;
		private String mDevTo = "";
		private String[] mDevCC = null;
		private String[] mDevBCC = null;
		private Session session = null;

		public EmailUtil()
			{
				try
					{
						Properties properties = new Properties();
						session = Session.getDefaultInstance( properties );
						String lMode = resourceBundle.getString( "mode" );
						if ( lMode.equalsIgnoreCase( "LIVE" ) )
							{
								mDevMode = false;
							}
						else
							{
								mDevMode = true;
							}
						if ( mDevMode )
							{
								String lEmailList = "shashank.ashokkumar@a-cti.com,";
								String[] lEmails = lEmailList.split( "," );
								mDevTo = lEmails [0];
								mDevCC = new String [lEmails.length - 1];
								for ( int i = 1 ; i < lEmails.length ; i++ )
									{
										mDevCC [i - 1] = lEmails [i];
										mDevBCC [i - 1] = lEmails [i];
									}
							}
					}
				catch ( Exception ex )
					{
						StackTraceWriter.printStackTrace( ex );
					}
			}

		// To send text-only mails for single recipient and no attachments
		public void msgSend( String to , String from , String subject , boolean debug , String msgText ) throws BusinessException
			{
				msgSend( to , null , null , from , subject , debug , msgText , null , null );
			}

		// To send mails for single recipient and no attachments
		public void msgSend( String to , String from , String subject , boolean debug , String msgText , String htmlPart ) throws BusinessException
			{
				msgSend( to , null , null , from , subject , debug , msgText , htmlPart , null );
			}

		// To send mails for multiple recipient (has a bcc list) and no
		// attachments
		public void msgSend( String to , String[] bcc , String from , String subject , boolean debug , String msgText , String htmlPart ) throws BusinessException
			{
				msgSend( to , bcc , null , from , subject , debug , msgText , htmlPart , null );
			}

		// To send mails for multiple recipient (has a bcc list) and with
		// attachments
		public void msgSend( String to , String[] bcc , String from , String subject , boolean debug , String msgText , String htmlPart ,
								Vector attachMents , boolean isCSV ) throws BusinessException
			{
				this.isCSV = isCSV;
				msgSend( to , bcc , null , from , subject , debug , msgText , htmlPart , attachMents );
			}

		// To send mails for single recipient and a list of attachments
		public void msgSend( String to , String from , String subject , boolean debug , String msgText , String htmlPart , Vector attachMents ) throws BusinessException
			{
				msgSend( to , null , null , from , subject , debug , msgText , htmlPart , attachMents );
			}

		// To send mails for multiple recipient (has a bcc and cc list) and a
		// list of attachments
		public void msgSend( String to , String[] bcc , String[] cc , String from , String subject , boolean debug , String msgText ,
								String htmlPart , Vector attachMents )
			{
				try
					{
						Message message = new MimeMessage( session );
						message.setFrom( new InternetAddress( from ) );

						if ( mDevMode )
							{
								subject += " for " + to;
								to = mDevTo;
								cc = mDevCC;
							}

						message.setRecipients( Message.RecipientType.TO , InternetAddress.parse( to , false ) );
						if ( bcc != null && bcc.length > 0 )
							{
								String bcclist = bcc [0];
								for ( int bcclen = 1 ; bcclen < bcc.length ; bcclen++ )
									{
										bcclist += "," + bcc [bcclen];
									}
								message.setRecipients( Message.RecipientType.BCC , InternetAddress.parse( bcclist , false ) );
							}
						if ( cc != null && cc.length > 0 )
							{
								String cclist = cc [0];
								for ( int cclen = 1 ; cclen < cc.length ; cclen++ )
									{
										cclist += "," + cc [cclen];
									}
								message.setRecipients( Message.RecipientType.CC , InternetAddress.parse( cclist , false ) );
							}
						Multipart multipart = new MimeMultipart();

						MimeBodyPart msgTextBodyPart = new MimeBodyPart();
						msgTextBodyPart.setContent( msgText , "text/plain" );
						multipart.addBodyPart( msgTextBodyPart );

						if ( htmlPart != null )
							{
								multipart = new MimeMultipart();
								MimeBodyPart htmlPart1 = new MimeBodyPart();
								htmlPart1.setContent( htmlPart , "text/html" );
								multipart.addBodyPart( htmlPart1 );
							}
						else if ( htmlPart == null || "null".equalsIgnoreCase( htmlPart ) )
							{
								if ( msgText == null || msgText.trim() == "" || "null".equalsIgnoreCase( msgText )
										|| "".equalsIgnoreCase( msgText.trim() ) )
									{
										multipart = new MimeMultipart();
										MimeBodyPart htmlBodyPart = new MimeBodyPart();
										htmlBodyPart.setContent( " " , "text/html" );
										multipart.addBodyPart( htmlBodyPart );
									}
							}
						if ( attachMents != null && attachMents.size() > 0 )
							{
								if ( isCSV )
									{
										MimeBodyPart attachment = new MimeBodyPart();
										attachment.setFileName( subject + ".csv" );
										Iterator lIterator = attachMents.iterator();
										while ( lIterator.hasNext() )
											{
												EmailAttachmentView attach = (EmailAttachmentView) lIterator.next();
												attachment.setContent( attach.getAttachmentText() , "application/csv" );
											}
										multipart.addBodyPart( attachment );
									}

								if ( !isCSV )
									{
										MimeBodyPart attachment = new MimeBodyPart();
										attachment.setFileName( "YourMessages.txt" );
										Iterator lIterator = attachMents.iterator();
										while ( lIterator.hasNext() )
											{
												EmailAttachmentView attach = (EmailAttachmentView) lIterator.next();
												attachment.setContent( attach.getAttachmentText() , "application/msword" );
											}
										multipart.addBodyPart( attachment );
									}
							}
						message.setContent( multipart );
						message.setSubject( subject );
						Transport.send( message );
					}
				catch ( Exception e )
					{
						StackTraceWriter.printStackTrace( e );
					}
			}

		/**
		 * Shamelessly Ganked from
		 * http://groups-beta.google.com/group/comp.lang.
		 * java/browse_thread/thread
		 * /701af1f2fb5c45d/593fd4a34a38254e?q=mimebodypart
		 * +byte+array&rnum=1#593fd4a34a38254e comp.lang.java post from Nov 21,
		 * 2001 "Java Mail Attachment" by Forafish Rawd.
		 */
		class ByteArrayDataSource implements DataSource
			{
				private byte[] data;
				private String type; // mime-type

				public ByteArrayDataSource( byte[] _content , String _type )
					{
						this.data = _content;
						this.type = _type;
					}

				public String getContentType()
					{
						return this.type;
					}

				public InputStream getInputStream() throws IOException
					{
						return new ByteArrayInputStream( this.data );
					}

				public String getName()
					{
						return "ByteArrayDataSource";
					}

				public OutputStream getOutputStream() throws IOException
					{
						throw new IOException( "Can't do that." );
					}

			}

		class ByteArrayDataSource1 implements DataSource
			{
				private File data;
				private String type; // mime-type

				public ByteArrayDataSource1( File _content , String _type )
					{
						this.data = _content;
						this.type = _type;
					}

				public String getContentType()
					{
						return this.type;
					}

				public InputStream getInputStream() throws IOException
					{
						return new FileInputStream( this.data );
					}

				public String getName()
					{
						return "ByteArrayDataSource";
					}

				public OutputStream getOutputStream() throws IOException
					{
						throw new IOException( "Can't do that." );
					}

			}

		public void msgSend( String to , String[] bcc , String[] cc , String from , String subject , boolean debug , String msgText )
			{

				try
					{
						MimeMultipart topLevelMultipart = new MimeMultipart();

						MimeMultipart mailLevelMultipart = new MimeMultipart( "alternative" );

						MimeBodyPart txtMimeBodyPart = new MimeBodyPart();
						txtMimeBodyPart.setContent( msgText , "text/plain" );
						mailLevelMultipart.addBodyPart( txtMimeBodyPart );

						MimeBodyPart mailBodyPart = new MimeBodyPart();
						mailBodyPart.setContent( mailLevelMultipart );
						topLevelMultipart.addBodyPart( mailBodyPart );

						MimeMessage msg = new MimeMessage( session );

						msg.setFrom( new InternetAddress( from ) );

						if ( mDevMode )
							{
								subject += " for " + to;
								to = mDevTo;
								cc = mDevCC;
							}
						msg.setRecipients( Message.RecipientType.TO , InternetAddress.parse( to , false ) );
						if ( bcc != null && bcc.length > 0 )
							{
								String bcclist = bcc [0];
								for ( int bcclen = 1 ; bcclen < bcc.length ; bcclen++ )
									{
										bcclist += "," + bcc [bcclen];
									}
								msg.setRecipients( Message.RecipientType.BCC , InternetAddress.parse( bcclist , false ) );
							}

						if ( cc != null && cc.length > 0 )
							{
								String cclist = cc [0];
								for ( int cclen = 1 ; cclen < cc.length ; cclen++ )
									{
										cclist += "," + cc [cclen];
									}
								msg.setRecipients( Message.RecipientType.CC , InternetAddress.parse( cclist , false ) );
							}

						msg.setHeader( "X-Mailer" , mailer );
						msg.setSentDate( new Date() );

						msg.setSubject( subject );
						msg.setText( msgText );
						msg.saveChanges();
						synchronized ( this )
							{
								Transport.send( msg );
							}

					}
				catch ( Exception ex )
					{
						StackTraceWriter.printStackTrace( ex );
					}
			}

		/*public boolean isMessageToMobile( String lToAddress )
			{
				String toAddresses[] = lToAddress.split( "," );
				String toAddress[];
				boolean isDigit = false;
				try
					{
						for ( int i = 0 ; i < toAddresses.length ; i++ )
							{
								toAddress = toAddresses [i].split( "@" );
								if ( StringUtil.isNumeric( toAddress [0] ) && toAddress [1].indexOf( "fax" ) == -1 )
									isDigit = true;
							}
					}
				catch ( Exception ex )
					{
						StackTraceWriter.printStackTrace( ex );
					}
				return isDigit;
			}
		public void pushExceptionMail(MessageHistoryInfoDTO historyInfoDTO, String subject , Exception e) throws BusinessException
		{
			String dateAddedWithTimeZone = ManageMessageUtil.convertTimeZoneRespectToAccount( historyInfoDTO.getHistorySubDto().getDateAddedWithTimeZone() , historyInfoDTO.getHistoryDto().getAccountNumber() );
			StringBuilder message = new StringBuilder();
			message.append( "<b>Account Number : " );
			message.append( historyInfoDTO.getHistoryDto().getAccountNumber() );
			message.append( "</b><br><b>Message Taken : " );
			message.append( dateAddedWithTimeZone );
			message.append( "</b><br><b>Agent Initial : " );
			message.append( historyInfoDTO.getHistoryDto().getOperatorInitial() );
			message.append( "</b><br><b>Exception : " );
			message.append( e+"</b><br><br>" );
			for ( StackTraceElement element : e.getStackTrace() )
				message.append( element.toString() + "<br>" );
			msgSend( "dev.sb@a-cti.com" , null , "customerservice@a-cti.com" , subject
					+ historyInfoDTO.getHistoryDto().getAccountNumber() , false , null , message.toString() );
		}*/
	}
