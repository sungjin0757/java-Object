package chapter6;

public class Sum {

    public static long AutoboxedSum(){
        Long sum=0l;
        for(int i=0;i<Integer.MAX_VALUE;i++){
            sum+=i;
        }
        return sum;
    }

    public static long UnAutoboxedSum(){
        long sum=0l;
        for(int i=0;i<Integer.MAX_VALUE;i++){
            sum+=i;
        }
        return sum;
    }

}
