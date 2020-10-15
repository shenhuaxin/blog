package springboot.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shenhuaxin
 * @date 2020/10/14
 */
@RestController
public class TestController {



    @Autowired
    private AsyncTask asyncTask;

    @GetMapping("test")
    public void test() throws InterruptedException {
        System.out.println("test-start");
        asyncTask.asyncRun();
        System.out.println("test-end");
    }

}
