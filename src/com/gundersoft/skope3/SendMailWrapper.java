package com.gundersoft.skope3;

import java.util.Properties;
 
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
 
/**
 * A Wrapper Class for the JavaMail APIS. For best luck, use this class with
 * the Google SMTP servers, which it is expressly configured for.
 * @author Christian Gunderman
 */
public class SendMailWrapper {
    /** SMTP Server username */
    private String username;
    /** SMTP Server password */
    private String password;
    /** SMTP Host address */
    private String smtpHost;
    /** Enable TLS secure login? */
    private boolean enableTLS;
    /** SMTP Port */
    private String smtpPort;
    /** Email Subject Line */
    private String subject;
	
    /**
     * Wraps the default constructor and preconfigures the SendMailWrapper
     * for use with Google's SMTP servers.
     * @param username Your gmail address.
     * @param password Your gmail password.
     * @return A new SendMailWrapper, ready to send mail.
     */
    public static SendMailWrapper getGoogleMailInstance(String username, 
							String password) {
	return new SendMailWrapper(username, password, "smtp.gmail.com",
				   true, "587");
    }
	
    /**
     * Sets the username that will be used to log into the
     * remote server for sending emails.
     * @param username Username to log in with.
     */
    public void setUsername(String username) {
	this.username = username;
    }
	
    /**
     * Gets the username currently used to log into the server with.
     * @return The current email username.
     */
    public String getUsername() {
	return this.getUsername();
    }
	
    /**
     * Sets the password used to log into the email account.
     * @param password The password used to log into the
     * email account.
     */
    public void setPassword(String password) {
	this.password = password;
    }
	
    public String getPassword() {
	return this.password;
    }
	
    /**
     * Creates a send mail wrapper object that holds all of your connection
     * information for your convenience, so that you do not have to retype
     * them for every email sent.
     * @param username SMTP login username or email address
     * @param password SMTP password
     * @param smtpHost SMTP host address. Ex: smtp.gmail.com
     * @param enableTLS Enables TLS secure login.
     * @param smtpPort Which port to login through.
     */
    public SendMailWrapper(String username, String password, String smtpHost, 
			   boolean enableTLS, String smtpPort) {
	this.username = username;
	this.password = password;
	this.smtpHost = smtpHost;
	this.enableTLS = enableTLS;
	this.smtpPort = smtpPort;
	this.subject = "[No Subject]";
    }
	
    /**
     * Sets the subject line for all future emails.
     * @param subject The Subject of your email.
     */
    public void setSubject(String subject) {
	this.subject = subject;
    }
	
    /**
     * Gets the subject for all future emails.
     * @return The subject line.
     */
    public String getSubject() {
	return this.subject;
    }
	
    /**
     * Sends a plain text email to the given recipient with the specified body and
     * attached files.
     * @param recipient The email address to send to.
     * @param body The body text to place in the email.
     * @param file An array of paths to the files to attach to the email.
     * @throws MessagingException Thrown if there is a problem sending the email.
     */
    public void sendMail(String recipient, String body, String... file) throws MessagingException {
 
	Properties props = new Properties();
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.starttls.enable", this.enableTLS ? "true":"false");
	props.put("mail.smtp.host", this.smtpHost);
	props.put("mail.smtp.port", this.smtpPort);
 
	Session session = Session.getInstance(props,
					      new javax.mail.Authenticator() {
						  protected PasswordAuthentication getPasswordAuthentication() {
						      return new PasswordAuthentication(username, password);
						  }
					      });
 
	Message message = new MimeMessage(session);
	message.setFrom(new InternetAddress(this.username));
	message.setRecipients(Message.RecipientType.TO,
			      InternetAddress.parse(recipient));
	message.setSubject(this.subject);
		
	// Create the message part 
	BodyPart messageBodyPart = new MimeBodyPart();

	// Fill the message
	messageBodyPart.setText(body);
         
	// Create a multipart message
	Multipart multipart = new MimeMultipart();

	// Set text message part
	multipart.addBodyPart(messageBodyPart);

	if(file != null) {
	    for(String s: file) {
		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(s);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(s);
		multipart.addBodyPart(messageBodyPart);
	    }
	}

	// Send the complete message parts
	message.setContent(multipart );
 
	Transport.send(message);
 
    }
}