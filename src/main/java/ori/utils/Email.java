package ori.utils;



import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;
import ori.entity.User;

import java.util.Properties;
import java.util.Random;





@Component
public class Email {

    public String getRandom() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    // send email to the user email
    public boolean sendEmail(User user) {
        boolean test = false;
        String toEmail = user.getEmail();
        String fromEmail = "Vanvan16102003@gmail.com";
        String password = "gkju mout cpsx fiei";
        try {

            // your host email smtp server details
            Properties pr = configEmail(new Properties());
            // get session to authenticate the host email address and password
            Session session = Session.getInstance(pr, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });
            // set email message details
            Message mess = new MimeMessage(session);
            mess.setHeader("Content-Type", "text/plain; charset=UTF-8");
            // set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            // set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            // set email subject
            mess.setSubject("Tài khoản tham gia Orishop của quý khách đã được đăng ký thành công.");
            // set message text
            mess.setText("Cảm ơn bạn đã tham Orishop! Đây là mã kích hoạt tài khoản của bạn : " + user.getCode());
            // send the message
            Transport.send(mess);
            test = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return test;
    }
    public Properties configEmail(Properties pr) {
        // your host email smtp server details
        pr.setProperty("mail.smtp.host", "smtp.gmail.com");
        pr.setProperty("mail.smtp.port", "587");
        pr.setProperty("mail.smtp.auth", "true");
        pr.setProperty("mail.smtp.starttls.enable", "true");
        pr.put("mail.smtp.socketFactory.port", "587");
        pr.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return pr;
    }

}

