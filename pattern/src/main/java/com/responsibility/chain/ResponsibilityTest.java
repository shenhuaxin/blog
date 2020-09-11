package com.responsibility.chain;

import java.util.Map;

/**
 * https://stackoverflow.com/questions/63840634/how-to-reduce-cyclomatic-complexity-of-this-java-method/63842819#63842819
 *
 * @author shenhuaxin
 * @date 2020/9/11
 */
public class ResponsibilityTest {


    public static void main(String[] args) {

        Handler checkIdEmailName = new Handler() {
            @Override
            public Integer process(User user) {
                boolean check = false;
                if (check) {
                    return 1;
                } else {
                    return next.process(user);
                }
            }
        };
        Handler checkIdEmail = new Handler() {
            @Override
            public Integer process(User user) {
                boolean check = false;
                if (check) {
                    return 1;
                } else {
                    return next.process(user);
                }
            }
        };
        checkIdEmailName.next = checkIdEmail;

        Integer result = checkIdEmailName.process(new User());
    }
}
