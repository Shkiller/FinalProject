package main.service;
import com.github.cage.GCage;
import main.api.response.CaptchaResponse;
import main.model.CaptchaCode;
import main.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

@Service
public class CaptchaService {
    private final String BASE64 = "data:image/png;base64, ";
    @Value("${captcha.deleteTime}")
    private long hour;
    private final CaptchaRepository captchaRepository;

    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public CaptchaResponse getCaptcha() throws IOException, NoSuchAlgorithmException {
        GCage cage = new GCage();
        String token = cage.getTokenGenerator().next();
        while (captchaRepository.findByCode(token).isPresent())
        {
            token = cage.getTokenGenerator().next();
        }
        //Получение MD5
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(token.getBytes(StandardCharsets.UTF_8));
        String s2 = new BigInteger(1, m.digest()).toString(16);
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0, count = 32 - s2.length(); i < count; i++) {
            sb.append("0");
        }
        sb.append(s2);
        //
        captchaRepository.findAll().forEach(captchaCode -> {
            if(new Date().getTime()-captchaCode.getTime().getTime()>=hour)
            {
                captchaRepository.delete(captchaCode);
            }
        });
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(token);
        captchaCode.setTime(new Date());
        captchaCode.setSecretCode(sb.toString());
        captchaRepository.save(captchaCode);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(cage.drawImage(token), "png", byteStream);
        byte[] fileContent = byteStream.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setImage(BASE64 + encodedString);
        captchaResponse.setSecret(sb.toString());
        return captchaResponse;
    }
}
