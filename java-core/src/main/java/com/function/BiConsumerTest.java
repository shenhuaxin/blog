package com.function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author shenhuaxin
 * @date 2020/11/20
 */
public class BiConsumerTest {


    public static void main(String[] args) {
        List a = new ArrayList<>();
        BiConsumer<Long, String> consumer = (p1, p2) -> {
            System.out.println(p1);
            System.out.println(p2 + ":" + a);
        };
        a.add("11");
        consumer.accept(1L, "j");
        a.add("11");
        consumer.accept(1L, "j");
        compute();
    }

    public static void compute() {
        Strategy strategy = new Strategy() {
            @Override
            public void compute() {
                System.out.println("compute");
            }
        };
    }

}
