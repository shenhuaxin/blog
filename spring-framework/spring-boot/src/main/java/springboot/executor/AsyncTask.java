package springboot.executor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author shenhuaxin
 * @date 2020/10/14
 */
@Component
public class AsyncTask {

    @Async("executor")
    public void asyncRun() throws InterruptedException {
        System.out.println("async-start");
        Thread.sleep(10000L);
        System.out.println("async-end");
    }
}
