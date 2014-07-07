package com.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;




public class AnalyticsMailer {


	public void initMail(String msgPart,String msgBody,String date, String to ,String subject,String cc,String bcc, String filePrefix) throws UnsupportedEncodingException
	{
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		try {
		    Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress("sb_batch@a-cti.com", "DataService Mailer"));
//		    msg.addRecipient(Message.RecipientType.TO,
//		     new InternetAddress("shashanksworld@gmail.com", "Mr. Shashank"));
		    if(to!=null&&!to.equals("") )
		    msg.setRecipients( Message.RecipientType.TO , InternetAddress.parse( to, false ) );
		    if(cc!=null&&!cc.equals("") )
		    msg.setRecipients( Message.RecipientType.CC , InternetAddress.parse( cc, false ) );
		    if(bcc!=null&&!bcc.equals("") )
		    msg.setRecipients( Message.RecipientType.BCC , InternetAddress.parse( bcc, false ) );
		    Multipart multipart = new MimeMultipart();
		    if(msgPart!=null && !msgPart.equals(""))
		    {	
				//Body Part 1
		    	MimeBodyPart attachment = new MimeBodyPart();
				attachment.setFileName( filePrefix +date+ ".csv" );
				attachment.setContent(msgPart , "application/csv" );
				multipart.addBodyPart( attachment );
				
				//Body Part 2
				
			//System.out.println("Sending Mail to:::"+cc+":"+bcc);
			
		    }
		    MimeBodyPart msgTextBodyPart = new MimeBodyPart();
			msgTextBodyPart.setContent( msgBody , "text/plain" );
			multipart.addBodyPart( msgTextBodyPart );
			msg.setContent( multipart );
			msg.setSubject(subject);
		    
		    
		    //System.out.println(GaDatastoreService.convertObjectToJson(msg));
		    System.out.println(msg);
		    Transport.send(msg);

		} catch (AddressException e) {
		    // ...
			System.out.println(e);
		} catch (MessagingException e) {
		    // ...
			System.out.println(e);
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

}
