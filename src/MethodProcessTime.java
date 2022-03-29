import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodProcessTime  {

    public static void logTime(MethodProceed method,Object target,Object[] args) throws Throwable{
        long start=System.nanoTime();
        Method proceed = method.proceed();
        if(args.length!=0) {
            proceed.invoke(target, args);
        }else{
            proceed.invoke(target);
        }
        System.out.println(proceed.getName()+" : "+(System.nanoTime()-start)+"ns");
    }

}
