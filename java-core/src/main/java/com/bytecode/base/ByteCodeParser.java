package com.bytecode.base;


import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class文件格式详情见深入理解JAVA虚拟机
 */
public class ByteCodeParser {


    public static void main(String[] args) throws Exception {
        printMethodList();
    }


    /**
     * 打印Class文件的函数列表
     * ClassFile {
     * u4             magic;
     * u2             minor_version;
     * u2             major_version;
     * u2             constant_pool_count;
     * cp_info        constant_pool[constant_pool_count-1];
     * u2             access_flags;
     * u2             this_class;
     * u2             super_class;
     * u2             interfaces_count;
     * u2             interfaces[interfaces_count];
     * u2             fields_count;
     * field_info     fields[fields_count];
     * u2             methods_count;
     * method_info    methods[methods_count];
     * u2             attributes_count;
     * attribute_info attributes[attributes_count];
     * }
     *
     * @throws Exception
     */
    public static void printMethodList() throws Exception {
        Map<Integer, String> constantMap = new HashMap<>();
        String path = "./file/class/Hello.class";
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[10];
        // 跳过前8个字节
        inputStream.skip(8);
        // 读取常量池数量
        inputStream.read(buffer, 0, 2);
        int constPoolLen = (buffer[0] << 8) + buffer[1];
        // 读取常量
        for (int i = 1; i <= constPoolLen - 1; i++) {
            inputStream.read(buffer, 0, 1);
            byte type = buffer[0];
            int constantLen = getConstantLen(type);
            if (constantLen < 0) {
                inputStream.read(buffer, 0, 2);
                int len = (buffer[0] << 8) + buffer[1];
                //inputStream.skip(len);
                buffer = new byte[len];
                inputStream.read(buffer, 0, len);
                String s = new String(buffer);

                constantMap.put(i, s);
            } else {
                inputStream.skip(constantLen);
            }
        }
        inputStream.skip(2); // skip access_flags
        inputStream.skip(2); // skip this_class
        inputStream.skip(2); // skip super_class
        // 读取接口
        buffer = new byte[2];
        inputStream.read(buffer, 0, 2);
        int interfaceCount = (buffer[0] << 8) + buffer[1];
        buffer = new byte[2 * interfaceCount];
        inputStream.read(buffer, 0, interfaceCount * 2);
        // 读取field
        buffer = new byte[10];
        inputStream.read(buffer, 0, 2);
        int fieldCount = (buffer[0] << 8) + buffer[1];
        // 读取method
        inputStream.read(buffer, 0, 2);
        int methodCount = (buffer[0] << 8) + buffer[1];
        for (int i = 0; i < methodCount; i++) {
            inputStream.read(buffer, 0, 2);  // access_flags
            inputStream.read(buffer, 0, 2);  // name_index
            int name_index = (buffer[0] << 8) + buffer[1];
            System.out.println("name_index: " + name_index);

            System.out.println("method: " + constantMap.get(name_index));
            inputStream.read(buffer, 0, 2);  // descriptor_index
            inputStream.read(buffer, 0, 2);  // attributes_count
            /**
             * attribute_info
             * u2 name_index
             * u4 length
             * u1 info[length]
             */
            int attrCount = (buffer[0] << 8) + buffer[1];
            for (int j = 0; j < attrCount; j++) {
                inputStream.read(buffer, 0, 2);
                System.out.println("attr_name_index: " + printByte(buffer));
                inputStream.read(buffer, 0, 4);
                int attr_len = (buffer[0] << 24) + (buffer[1] << 16) + (buffer[2] << 8) + buffer[3];
                System.out.println("attr_len: " + attr_len);
                inputStream.skip(attr_len);
            }
        }
        // public init ()V  1个attribute
    }


    /**
     * CONSTANT_Utf8_info	1                 u1 tag  u2 length  u1 byte[length]
     * CONSTANT_Integer_info	3             u1 tag             u4 bytes
     * CONSTANT_Float_info	4                 u1 tag             u4 bytes
     * CONSTANT_Long_info	5                 u1 tag             u8 bytes
     * CONSTANT_Double_info	6                 u1 tag             u8 bytes
     * CONSTANT_Class_info	7                 u1 tag             u2 name_index
     * CONSTANT_String_info	8                 u1 tag             u2 string_index
     * CONSTANT_Fieldref_info	9             u1 tag   u2 class_index  u2 name_and_type_index
     * CONSTANT_Methodref_info	10            u1 tag   u2 class_index  u2 name_and_type_index
     * CONSTANT_InterfaceMethodref_info	11    u1 tag   u2 class_index  u2 name_and_type_index
     * CONSTANT_NameAndType_info	12        u1 tag   u2 name_index  u2 descriptor_index
     * CONSTANT_MethodHandle_info	15        u1 tag   u2 bootstrap_method_attr_index  u2 name_type_index
     * CONSTANT_MethodType_info	16            u1 tag   u2 bootstrap_method_attr_index  u2 name_type_index
     * CONSTANT_InvokeDynamic_info	18        u1 tag   u2 bootstrap_method_attr_index  u2 name_type_index
     * 获取常量长度
     *
     * @return
     */
    private static int getConstantLen(int constantType) {
        int len;
        switch (constantType) {
            case 1:
                len = -2;
                break;
            case 3:
            case 4:
            case 9:
            case 10:
            case 12:
            case 11:
            case 15:
            case 18:
            case 16:
                len = 4;
                break;
            case 5:
            case 6:
                len = 8;
                break;
            case 7:
            case 8:
                len = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + constantType);
        }
        return len;
    }


    public static String printByte(byte[] arr) {
        String s = "";
        for (byte b : arr) {
            s = s + b;
        }
        return s;
    }
}
