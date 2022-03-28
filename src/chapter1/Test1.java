package chapter1;

import java.util.EnumSet;

public class Test1 {
    private int a;
    private int b;
    private static final Test1 test1=new Test1();

// 기본 생성자
    public Test1() {
    }

    public Test1(int a, int b) {
        this.a=a;
        this.b=b;
    }

//    static factory method
    public static Test1 getInstanceNoArgs(){
        return new Test1();
    }

    public static Test1 getInstanceWithArgs(int a, int b){
        return new Test1(a,b);
    }

//    singleton
    public static Test1 getInstanceWithSingleton(){
        return test1;
    }

}
