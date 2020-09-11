package com.responsibility.chain;

import java.util.Map;

/**
 * @author shenhuaxin
 * @date 2020/9/11
 */
public abstract class Handler {

    protected Handler next;

    public abstract Integer process(User user);
}
