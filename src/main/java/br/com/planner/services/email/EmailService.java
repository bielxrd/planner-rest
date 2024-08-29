package br.com.planner.services.email;

import br.com.planner.domain.Owner;
import br.com.planner.dto.email.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.encrypt.BouncyCastleAesCbcBytesEncryptor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.encrypt.secretKey}")
    private String password;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailToOwner(Email email) {

    }

    public void sendEmailToParticipant(Email email) {
        try {
            MimeMessage simpleMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(simpleMailMessage, true);
            String template = loadTemplate("/templates/email-template.html");
            template = template.replace("#{destination}", email.getSubject());
            template = template.replace("#{date}", email.getStartsAt().toString());
            for (int i = 0; i < email.getTo().size(); i++) {
                mimeMessageHelper.setFrom(email.getFrom());
                mimeMessageHelper.setTo(email.getTo().get(i));
                mimeMessageHelper.setSubject(email.getSubject());

                ClassPathResource resource = new ClassPathResource("/static/img/Logo-black.svg");
                mimeMessageHelper.addInline("logo", resource);

                String personalizedTemplate = template.replace("#{link}", email.getBody() + "&data=" + encryptEmail(email.getTo().get(i)));

                mimeMessageHelper.setText(personalizedTemplate, true);
                mailSender.send(simpleMailMessage);
            }
        } catch (MailException | IOException | MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendEmailFromConsumer(Email email, String type, String tripId) {
        switch (type) {
            case "activity_queue":
                sendActivityCreatedNotificationEmail(email, tripId);
                break;
            case "link_queue":
                sendLinkActivityCreatedNotificationEmail(email, tripId);
                break;
            default:
                throw new MailSendException("Email notification not worked");
        }

    }

    private void sendActivityCreatedNotificationEmail(Email email, String tripId) {
        email.setSubject("Nova atividade cadastrada com sucesso.");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            String template = loadTemplate("/templates/activity-email-template.html");
            createMimeMessage(email, tripId, message, mimeMessageHelper, template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendLinkActivityCreatedNotificationEmail(Email email, String tripId) {
        email.setSubject("Novo link cadastrado com sucesso.");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            String template = loadTemplate("/templates/link-email-template.html");
            createMimeMessage(email, tripId, message, mimeMessageHelper, template);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createMimeMessage(Email email, String tripId, MimeMessage message, MimeMessageHelper mimeMessageHelper, String template) throws MessagingException {
        for (int i = 0; i < email.getTo().size(); i++) {
            mimeMessageHelper.setFrom(email.getFrom());
            mimeMessageHelper.setTo(email.getTo().get(i));
            mimeMessageHelper.setSubject(email.getSubject());

            String personalizedTemplate = template.replace("#{tripId}", tripId);

            mimeMessageHelper.setText(personalizedTemplate, true);
            mailSender.send(message);
        }
    }

    private String loadTemplate(String classPath) throws IOException {
        ClassPathResource resource = new ClassPathResource(classPath);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String encryptEmail(String email) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[8];
            sr.nextBytes(salt);
            final byte[][] keyAndIV = generateKeyAndIV(32, 16, 1, salt, password.getBytes(StandardCharsets.UTF_8), MessageDigest.getInstance("MD5"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyAndIV[0], "AES"), new IvParameterSpec(keyAndIV[1]));
            byte[] encryptedData = cipher.doFinal(email.getBytes(StandardCharsets.UTF_8));
            byte[] prefixAndSaltAndEncryptedData = new byte[16 + encryptedData.length];

            System.arraycopy("Salted__".getBytes(StandardCharsets.UTF_8), 0, prefixAndSaltAndEncryptedData, 0, 8);
            System.arraycopy(salt, 0, prefixAndSaltAndEncryptedData, 8, 8);
            System.arraycopy(encryptedData, 0, prefixAndSaltAndEncryptedData, 16, encryptedData.length);
            return Base64.getEncoder().encodeToString(prefixAndSaltAndEncryptedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private static byte[][] generateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {
            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte)0);
        }
    }

}
