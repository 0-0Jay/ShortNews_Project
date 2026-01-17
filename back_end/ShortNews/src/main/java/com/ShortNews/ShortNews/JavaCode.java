package com.ShortNews.ShortNews;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import net.nurigo.sdk.message.model.Message;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Component //자바 빈으로 등록
public class JavaCode {

//    @Autowired
//    private JavaMailSender javaMailSender;

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

    @Value("${sms.api.key}")
    private String API_KEY;
    @Value("${sms.api.secret}")
    private String API_SECRET;
    @Value("${sms.api.uri}")
    private String API_URI;

    public void sendSMS(String phone, String code) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom("01086383977");
        message.setTo(phone);
        message.setText("[Shortnews 인증 번호 발송] " + code);

        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET, API_URI);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setText(code);
//        javaMailSender.send(message);
    }
}
