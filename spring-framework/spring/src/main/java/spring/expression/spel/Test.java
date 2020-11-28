package spring.expression.spel;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;

public class Test {

    private final ExpressionEvaluator<String> evaluator = new ExpressionEvaluator<>();

    public void test1() throws NoSuchMethodException {
        SpelService spelService = new SpelServiceImpl();
        Method method = spelService.getClass().getMethod("spel", SpelObj.class);
        SpelObj spelObj = new SpelObj();
        spelObj.name = "shenhuaxin";
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(spelService, spelService.getClass(), method, new SpelObj[]{spelObj});
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, SpelServiceImpl.class);
        String condition = evaluator.condition("#spelObj.name", methodKey, evaluationContext, String.class);
        System.out.println(condition);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Test test = new Test();
        test.test1();
    }

}
