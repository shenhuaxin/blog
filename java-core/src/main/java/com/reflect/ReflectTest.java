package com.reflect;



import java.util.ArrayList;
import java.util.List;

public class ReflectTest {


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.getClass().getTypeParameters();
    }




}
