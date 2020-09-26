package com.bytecode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Foo {
    public void print(String s) {
        System.out.println("hello, " + s);
    }

//    public static void main(String[] args) throws Throwable {
//
//        int i = 0;
//        i = ++i + i++ + i++ + i++;
//        System.out.println("i=" + i);
//        Foo foo = new Foo();
//
//        MethodType methodType = MethodType.methodType(void.class, String.class);
//        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(Foo.class, "print", methodType);
//        methodHandle.invokeExact(foo, "world");
//    }

    public static void main(String[] args) {
//        for (int i = 1; i < 330; i++) {
//            String password = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
//            String encrypt = crypt(password);
//            System.out.println(password + "        " + encrypt);
//        }
        System.out.println(crypt("4be141d8"));
    }



    public static String crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }


}
