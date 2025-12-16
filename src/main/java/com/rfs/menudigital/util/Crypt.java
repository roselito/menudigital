package com.rfs.menudigital.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Roselito@RFS
 */
@Component
public class Crypt {

    public String SHA(String senha, String algorithm) {
        String retorno = "";
        try {
            MessageDigest message = MessageDigest.getInstance(algorithm);
            byte messageDigest[] = message.digest(senha.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }
            retorno = hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.getLogger(Crypt.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return retorno;
    }

}
