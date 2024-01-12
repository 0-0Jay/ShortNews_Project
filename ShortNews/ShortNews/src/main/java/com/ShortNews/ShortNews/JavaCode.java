package com.ShortNews.ShortNews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Component //자바 빈으로 등록
public class JavaCode {

    @Autowired
    private JavaMailSender javaMailSender;

    public String makeCode() {
        Random rnd = new Random();
        return String.format("%06d", rnd.nextInt(1000000));
    }

    public String getTime() {
        SimpleDateFormat formattype = new SimpleDateFormat("yyyyMMdd");
        return formattype.format(new Date());
    }

    public String makeSalt() {
        int leftLimit = 48, rightLimit = 122, targetStringLength = 8;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String makePw(String pw, String member_salt) throws NoSuchAlgorithmException {
        SHA256 sha256 = new SHA256();
        return sha256.encrypt(pw+member_salt);
    }

    public void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setText(code);
        javaMailSender.send(message);
    }
}
