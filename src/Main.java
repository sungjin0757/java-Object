import chapter1.Test2;
import chapter2.Test1;
import chapter6.RomanNumerals;
import chapter6.Sum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) throws Throwable {
        //메소드 Time Check
        RomanNumerals romanNumerals = new RomanNumerals();
        Sum sum = new Sum();
        MethodProcessTime.logTime(new MethodProceed(){
            @Override
            public Method proceed() throws NoSuchMethodException {
                return RomanNumerals.class.getMethod("isRomanNumeralSecond", String.class);
            }
        },romanNumerals,new Object[]{"1"});

        MethodProcessTime.logTime(new MethodProceed(){
            @Override
            public Method proceed() throws NoSuchMethodException {
                return RomanNumerals.class.getMethod("isRomanNumeralFirst", String.class);
            }
        },romanNumerals,new Object[]{"1"});

        MethodProcessTime.logTime(new MethodProceed() {
            @Override
            public Method proceed() throws NoSuchMethodException {
                return Sum.class.getMethod("AutoboxedSum");
            }
        },sum,new Object[]{});
        MethodProcessTime.logTime(new MethodProceed() {
            @Override
            public Method proceed() throws NoSuchMethodException {
                return Sum.class.getMethod("UnAutoboxedSum");
            }
        },sum,new Object[]{});

    }
}
