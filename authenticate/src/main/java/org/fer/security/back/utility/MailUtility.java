package org.fer.security.back.utility;


import javax.mail.internet.MimeMessage;

import org.fer.security.back.model.UserProfile;
import org.apache.log4j.Logger;
//import org.apache.velocity.app.VelocityEngine;
import org.infinispan.jcache.annotation.DefaultCacheKeyGenerator;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;


public class MailUtility {

    public static Logger LOGGER = Logger.getLogger(MailUtility.class);
    private JavaMailSender mailSender;
    private String targetBaseUrl;
    private String fromMail;
    private String contactMail;
    private String tmpMail;
	


    public void sendEmail(final UserProfile targetUser, final String context, final String subject, final String text) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
             public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(targetUser.getEmail());
                message.setFrom(fromMail);
                message.setSubject(subject);
                message.setText(text, true);
           }
          };
        LOGGER.info("Sending " + context + " message to: " + targetUser.getEmail());
        mailSender.send(preparator);
    }
	
	public void sendSignUpMail(final UserProfile targetUser) {

        // TODO: Use a templating engine ;-) with I18N
        String subject = "Welcome to ReqApps.com";
        String text = "Your account '" + targetUser + "' has been created on ReqApps.com !";
        sendEmail(targetUser, "SIGN-UP", subject, text);
    }

    public void sendLostPasswordMail(final UserProfile targetUser, String link) {

        // TODO: Use a templating engine ;-) with I18N
        String subject = "Lost your ReqApps.com credentials";
        String text = "The following link '" + link + "' enables you to change your password !";
        sendEmail(targetUser, "LOST-PWD", subject, text);
    }

    public void sendChangedPasswordMail(final UserProfile targetUser, String link) {

        // TODO: Use a templating engine ;-) with I18N
        String subject = "Your password changes on ReqApps.com";
        String text = "This message informs you that your password changed !";
        sendEmail(targetUser, "CHANGE-PWD", subject, text);
    }

    public JavaMailSender getMailSender() {
			return mailSender;
	}
	public void setMailSender(JavaMailSender mailSender) {
			this.mailSender = mailSender;
	}

	public String getFromMail() {
		return fromMail;
	}

	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}

	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	public String getTmpMail() {
		return tmpMail;
	}

	public void setTmpMail(String tmpMail) {
		this.tmpMail = tmpMail;
	}
		
		
}
