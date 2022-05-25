package com.st.authlight.service.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public record AesPasswordEncoder(Encoder encoder) implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        try {
            return encoder.encode(charSequence.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            return equalsNoEarlyReturn(encoder.encode(rawPassword.toString()), encodedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean equalsNoEarlyReturn(String a, String b) {
        var caa = a.toCharArray();
        var cab = b.toCharArray();

        if (caa.length != cab.length) return false;

        var ret = 0;
        for (int i = 0; i < caa.length; ++i) {
            ret = (byte) (ret | caa[i] ^ cab[i]);
        }

        return ret == 0;
    }
}