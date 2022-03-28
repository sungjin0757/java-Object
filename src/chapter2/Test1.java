package chapter2;

public class Test1 {
    private int a;
    private int b;
    private int c;

    public Test1() {
    }

    //점층적 생성자 증가
    public Test1(int a) {
        this(a,0,0);
    }

    public Test1(int a, int b) {
        this(a,b,0);
    }

    public Test1(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    //setter
    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setC(int c) {
        this.c = c;
    }

    //Builder
    public static class Builder{
        //필수 매개변수
        private final int a;

        //선택 매개변수 - 기본값으로 초기화
        private int b=0;
        private int c=0;

        public Builder(int a){
            this.a=a;
        }

        public Builder b(int b){
            this.b=b;
            return this;
        }

        public Builder c(int c){
            this.c=c;
            return this;
        }

        public Test1 build(){
            return new Test1(this);
        }
    }

    private Test1(Builder builder){
        this.a= builder.a;
        this.b= builder.b;
        this.c= builder.c;
    }
}
