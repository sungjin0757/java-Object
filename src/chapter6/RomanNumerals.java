package chapter6;

import java.util.regex.Pattern;

public class RomanNumerals {
    public static boolean isRomanNumeralFirst(String s){
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
                + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }

    static final Pattern Roman=Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    public static boolean isRomanNumeralSecond(String s){
        return Roman.matcher(s).matches();
    }

}
