package org.fer.security.back.service;

import org.fer.security.back.model.UserProfile;

/**
 * User: wilfried Date: 12/12/13 Time: 07:48
 */
public interface MailService {
    void sendEmail(final UserProfile targetUser, final String context, final String subject, final String text);
    void sendSignUpMail(final UserProfile targetUser);
    void sendLostPasswordMail(final UserProfile targetUser, String link);
    void sendChangedPasswordMail(final UserProfile targetUser);
}
