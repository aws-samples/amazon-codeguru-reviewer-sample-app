package com.mainpackage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Even though this file contains weak crypto issues, CodeGuru Reviewer will not
 * report any issues in it, because it has been excluded in aws-codeguru-reviewer.yml.
 * 
 * For more information, see the Amazon CodeGuru Reviewer User Guide:
 * https://docs.aws.amazon.com/codeguru/latest/reviewer-ug/welcome.html.
 */
public class Main {

    public static void main(String[] argv) {
        String message = argv[0];
        String key = argv[1];
        Cipher cipher = Cipher.getInstance("RSA");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        System.out.println(String(cipher.doFinal(message.getBytes()), StandardCharsets.UTF_8));

    }
    
}
