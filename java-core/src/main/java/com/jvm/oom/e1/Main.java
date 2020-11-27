package com.jvm.oom.e1;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author shenhuaxin
 * @date 2020/11/26
 */
public class Main {

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
        long i = 0;
        while (true) {
            System.out.println(i++);
            Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
        }
    }
}
