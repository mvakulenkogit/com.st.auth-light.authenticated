package com.st.authlight.service.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.binary.Hex.decodeHex;

@Service
public class AesEncoderImpl implements Encoder {

    private static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    @Value("${password.encoder.secret.key}")
    private String key;

    private final Cipher encryptor;
    private SecretKey secretKey;
    private IvParameterSpec iv_specs;

    public AesEncoderImpl() {
        try {
            encryptor = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            var raw = parseHexBinary(key);
            secretKey = new SecretKeySpec(raw, ALGORITHM);
            iv_specs = new IvParameterSpec(decodeHex(key.toCharArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encode(String text) throws Exception {
        synchronized (encryptor) {
            encryptor.init(ENCRYPT_MODE, secretKey, iv_specs);
            return encodeBase64String(encryptor.doFinal(text.getBytes()));
        }
    }
}