package chapter6;

public class Item6 {

    public static void main(String[] args){
        //지양해야하는 코딩 방식
        String s1=new String("hello");
        Boolean b1=new Boolean(true);

        //객체의 재생성을 막자 (이미 생성된 객체를 사용)
        String s2="hello";
        Boolean b2=Boolean.valueOf(true); // 정적 팩토리 메소드
    }
}
