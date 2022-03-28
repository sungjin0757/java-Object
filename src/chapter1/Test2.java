package chapter1;

public interface Test2 {
    static Test2 getA(){
       return new A();
    }

    static Test2 getB(){
        return new B();
    }

    class C implements Test2{
        private static final Test2 c=new C();

        private C(){
        }

        public static Test2 getInstance(){
            return c;
        }
    }
}

class A implements Test2{

}

class B implements Test2{

}