
### 切入点

```java
    @ModelLog(type = ModelLogTypeEnum.ADD_MODEL, model = "model.vehicleModelId", org = "model.orgId",
            uId = "#model.createdUser", uName = "#model.createdUserName")
    public BaseResponse addVehicleModel(VehicleModel2017 model) {
        return new BaseResponse();
    }
```


### 注解
```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelLog {

    ModelLogTypeEnum type() default ModelLogTypeEnum.UNKONW;

    String model() default "";
    String org() default "";
    String uId() default "";
    String uName() default "";

}
```


### 切面
```java
@Aspect
@Component
public class ModelLogAspect {

    @Autowired
    private VehicleModelOperateLogMapper vehicleModelOperateLogMapper;

    @Pointcut(value = "@annotation(com.extracme.nevmp.anno.ModelLog)")
    public void modelLogPointCut() {}

    private final ExpressionEvaluator<String> evaluator = new ExpressionEvaluator<>();

    @After(value = "modelLogPointCut() && @annotation(modelLog)", argNames = "joinPoint, modelLog")
    public void after(JoinPoint joinPoint, ModelLog modelLog) {
        String userId = getValue(joinPoint, modelLog.uId());
        String userName = getValue(joinPoint, modelLog.uName());
        Date date = new Date();
        VehicleModelOperateLog operateLog = new VehicleModelOperateLog();
        operateLog.setOrgId(getValue(joinPoint, modelLog.org()));
        operateLog.setContent(modelLog.type().getContent());
        operateLog.setCreatedUserId(userId);
        operateLog.setCreatedUserName(userName);
        operateLog.setCreatedTime(date);
        operateLog.setUpdatedUserId(userId);
        operateLog.setUpdatedUserName(userName);
        operateLog.setCreatedTime(date);
        vehicleModelOperateLogMapper.insertSelective(operateLog);
    }

    private String getValue(JoinPoint joinPoint, String condition) {
        return getValue(joinPoint.getTarget(), joinPoint.getArgs(),
                joinPoint.getTarget().getClass(),
                ((MethodSignature) joinPoint.getSignature()).getMethod(), condition);
    }

    private String getValue(Object object, Object[] args, Class clazz, Method method, String condition) {
        if (args == null) {
            return null;
        }
        EvaluationContext evaluationContext = evaluator.createEvaluationContext(object, clazz, method, args);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, clazz);
        return evaluator.condition(condition, methodKey, evaluationContext, String.class);
    }
}
```


### EL解析
```java
public class ExpressionRootObject {
    private final Object object;

    private final Object[] args;

    public ExpressionRootObject(Object object, Object[] args) {
        this.object = object;
        this.args = args;
    }

    public Object getObject() {
        return object;
    }

    public Object[] getArgs() {
        return args;
    }
}


public class ExpressionEvaluator<T> extends CachedExpressionEvaluator {

    // shared param discoverer since it caches data internally
    private final ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<>(64);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    /**
     * Create the suitable {@link EvaluationContext} for the specified event handling
     * on the specified method.
     */
    public EvaluationContext createEvaluationContext(Object object, Class<?> targetClass, Method method, Object[] args) {

        Method targetMethod = getTargetMethod(targetClass, method);
        ExpressionRootObject root = new ExpressionRootObject(object, args);
        return new MethodBasedEvaluationContext(root, targetMethod, args, this.paramNameDiscoverer);
    }

    /**
     * Specify if the condition defined by the specified expression matches.
     */
    public T condition(String conditionExpression, AnnotatedElementKey elementKey, EvaluationContext evalContext, Class<T> clazz) {
        return getExpression(this.conditionCache, elementKey, conditionExpression).getValue(evalContext, clazz);
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            if (targetMethod == null) {
                targetMethod = method;
            }
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }

}
```