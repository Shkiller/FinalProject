package main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import main.exception.IncorrectFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;


@Service
public class StorageService {
    private final int LENGTH = 16;
    private final String BASE64 = "data:image/png;base64, ";
    private final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";
    //Так как я под Виндой
    private final String STORAGE_PATH = "https://res.cloudinary.com/hoy3870lz/image";
    private final String UPLOAD = "upload/";
    private final String AVATARS = "avatars/";
    private final Cloudinary cloudinary;

    public StorageService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "hoy3870lz",
                "api_key", "367436962736364",
                "api_secret", "p9MsaZ3XdlXEmYxBDtjWmG2NWCA"));
    }

    public String store(MultipartFile image) throws IOException, IncorrectFormatException {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = (int) (SYMBOLS.length() * Math.random());
            sb.append(SYMBOLS.charAt(index));
        }
        if (!image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.')).equals(".png")
                && !image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.')).equals(".jpg")) {
            throw new IncorrectFormatException();
        }
        if (image.getSize() > 5242880) {
            throw new MaxUploadSizeExceededException(5242880);
        }
        String path = UPLOAD + sb.substring(0, 4) + "/" + sb.substring(4, 8) + "/" + sb.substring(8, 12);
        path = path + "/" + sb.substring(12, 16);

        return cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                "public_id", path)).get("url").toString();
    }


    public String storeAvatar(MultipartFile image) throws IncorrectFormatException, IOException {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = (int) (SYMBOLS.length() * Math.random());
            sb.append(SYMBOLS.charAt(index));
        }
        String path = AVATARS + sb.substring(0, 4) + "/" + sb.substring(4, 8) + "/" + sb.substring(8, 12);
        Files.createDirectories(Path.of(path));
        path = path + "/" + sb.substring(12, 16);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cropImage(image.getInputStream()), "jpg", baos);
        return cloudinary.uploader().upload(baos.toByteArray(), ObjectUtils.asMap(
                "public_id", path)).get("url").toString();
    }

    private BufferedImage cropImage(InputStream inputStream) throws IOException {
        BufferedImage imBuff = ImageIO.read(inputStream);
        ImageFilter filter = new CropImageFilter(0, 0, 36, 36);
        Image cropped = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(imBuff.getSource(), filter));
        BufferedImage image = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawImage(cropped, 0, 0, null);
        g.dispose();
        return image;
    }


}
