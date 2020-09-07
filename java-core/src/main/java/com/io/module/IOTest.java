package com.io.module;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class IOTest {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("C:\\Users\\SHX\\Desktop\\test.txt", "rw");
        MappedByteBuffer mappedByteBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 100);

        byte b = 65;
        mappedByteBuffer.putChar(3, 'M');

        System.out.println(file);
        System.out.println(mappedByteBuffer);
    }
}
