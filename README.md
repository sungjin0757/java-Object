## 객체 생성과 파괴

***
### 🎯 수행 목적
1. 객체를 만들어야 할 때를 구분하는 법
2. 올바른 객체 생성 방법과 불필요한 생성을 피하는 법
3. 파괴됨을 보장하고 파괴전에 수행해야 할 정리 작업

***

### 📌 References.

**Effective Java** 

***

### 🚀 Item1. 생성자 대신 정적 팩터리 메서드를 고려하라

클래스는 생성자와 별도로 정적 팩터리 메서드를 제공할 수 있습니다.

여기서 정적 팩터리 메소드란 클래스의 인스턴스를 반환하는 단순한 정적 메소드를 말합니다.

**EX)**

```java
public static Boolean valueOf(boolean b){
    return b ? Boolean.TRUE : Boolean.FALSE;
}
```

이 방식에는 장점과 단점이 둘 다 존재합니다.

먼저, 장점부터 살펴 봅시다.

**첫번째, 이름을 가질 수 있다**

생성자에 넘기는 매개변수와 생성자 자체만으로 반환될 객체의 특성을 제대로 설명하지 못할 수 있습니다.

예를들어, 소수 값을 반환하는 `BigInteger`의 생성자가 있을 때,
`BigInteger(int, int, Random)`보다는 `BigInteger.probablePrime`이 더욱 의미를 정확히 파악할 수 있습니다.

또한, 입력 매개변수만을 `Overloading`하여 만드는 생성자는 생성자가 추가 될 때마다, 개발자 마저도 어떤 생성자가 무엇을 의미하는 지 파악하기
어려울 수 있습니다.

따라서, 이름을 가진 정적 팩터리 메서드는 클래스의 인스턴스를 생성할 때 의미있는 이름의 메소드로 인스턴스를 생성할 수 있을 것입니다.

**두번쨰, 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.**

정적 팩터리 메서드는 인스턴스를 미리 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용할 수 있습니다.

`String` 과 같은 불변 클래스를 예로들 수 있죠!

```java
String s=new String("Hello");
String s=String.valueOf("Hello");
```

`new String`은 인스턴스를 하나 더 생성하는 것을 뜻합니다. 따라서, 같은 "Hello"라도 추가적으로 객체를 생성하는 비용이 들게 되죠.
`valueOf`는 정적 팩터리 메소드로서 이미 있는 객체를 캐싱하여 제공하게 됩니다. 원래 있던 "Hello"를 제공하게 되는 것이죠.

이런 반복되는 요청에 같은 객체를 반환하는 방식은 `싱글턴 패턴`의 기초가 되기도 합니다.

**싱글톤 예**
```java
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
    

//    singleton
    private Test1(){
    
    }
    public static Test1 getInstanceWithSingleton(){
        return test1;
    }

}
```

또한, 등치인 인스턴스가 단 하나 뿐임을 보장할 수 있습니다. 등치란 서로 똑같은 메모리 상에있는 객체를 뜻합니다.

**세번째, 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.**

이 능력은 반환할 객체의 클래스를 자유롭게 선택할 수 있게 하는 '유연성'을 제공합니다.

이런 유연성을 이용하면 구현 클래스를 공개하지 않고도 그 객체를 반환할 수 있습니다.

**그 예로**

```java
public interface Test2 {
    static Test2 getA(){
       return new A();
    }

    static Test2 getB(){
        return new B();
    }
    
}

class A implements Test2{

}

class B implements Test2{

}
```
이와 같은 코드를 들 수 있습니다.

자바8 이전에는 `인터페이스`에 정적 메서드를 선언할 수 없었다고 합니다. 따라서 인터페이스를 반환하려는 정적 메서드가 필요하면
그 안에 동반클래스를 두고 사용했다고 합니다.

```java
public interface Test2 {
    class C implements Test2{
        private static final Test2 c=new C();

        private C(){
        }

        public static Test2 getInstance(){
            return c;
        }
    }    

}
```

**네 번째, 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다**

원소의 수에 따라 두 가지 하위 클래스 중 하나의 인스턴스를 반환할 수 있습니다.
`EnumSet`을 예로 들어 봅시다.

```java
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64)
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }
```

위의 정적 팩터리 메소드에서는 매개변수의 길이에 따라 ` RegularEnumSet`와 `JumboEnumSet`을 반환 하고 있습니다.

**이제 단점을 알아봅시다!**

**첫 번째, 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다**

이 뜻을 이해하려면 상속받은 클래스가 어떤 방식으로 인스턴스를 생성하는지 알아볼 필요가 있습니다.

상속받은 클래스는 기본적으로 한 클래스를 상속하고 있는 형태입니다. 상속을 제공하는 클래스의 멤버 변수, 멤버 메소드등을 사용하기 위하여 먼저 부모 클래스의
인스턴스화가 필요하게 되는 것이죠.

근데, 만약 생성자가 존재하지 않고 정적 팩터리 메서드만을 제공하게 된다면 자식 클래스를 생성할 때, 부모 클래스를 생성시켜주지 못한다는 것을 뜻합니다.

따라서, 만약 부모 클래스에 생성자가 없이 자식 클래스가 상속을 한 후 생성을 하게 된다면 오류가 발생하게 됩니다.

**두 번째, 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.**

이와 같은 이유는 생성자 처럼 API설명에 명확히 드러나지 않을 수 있기 때문에 그렇습니다. 사용자는 정적 팩터리 메서드 방식 클래스를
인스턴스화할 방법을 직접 알아내야하는 것을 뜻합니다.

***

### 🚀 Item2. 생성자에 매개변수가 많다면 빌더를 고려하라.

프로그래머들은 클래스를 만들 때, `점층적 생성자 패턴`을 활용하게 됩니다.

이는, 필수 매개변수만 받는 생성자, 필수 매개변수와 선택 매개변수 1개를 받는 생성자 ... 쭈욱 생성자를 늘려가는 방식을 뜻합니다.

이런 점층적 생성자 패턴은 매개변수의 개수가 많아질수록 제약이 많아질수록 코드를 작성하거나 읽기 어렵게 됩니다.

이런 방식을 보완하기 위해서 `Builder` 패턴을 사용하게 됩니다.

어떤 패턴인지 알아봅시다.
1. 필수 매개변수만으로 생성자를 호출해 빌더 객체를 업습니다.
2. 일종의 세터 메소드들로 원하는 선택 매개변수들을 설정합니다.
3. 마지막으로 매개변수가 없는 build 메소드를 호출해 객체를 생성해 반환합니다.

코드로 살펴보도록 합시다.

```java
public class Test1 {
    private int a;
    private int b;
    private int c;

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
    
    public static void Main(String[] args){
        Test1 build = new Builder(1).b(2).c(3).build();
    }
}
```

빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출 할 수 있습니다.

빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에도 좋습니다.

추상 클래스는 추상 빌더를, 구체 클래스는 구체 빌더를 갖게 합니다.

**Pizza,java**

```java
public abstract class Pizza {
    public enum Topping{HAM, CHEESE}
    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>>{
        EnumSet<Topping> toppings=EnumSet.noneOf(Topping.class);
        public T addTopping(Topping topping){
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        protected abstract T self();
    }

    Pizza(Builder<?> builder){
        toppings=builder.toppings.clone();
    }
}
```

**MyPizza.java**

```java
public class MyPizza extends Pizza{
    public enum Size{SMALL,MEDIUM,LARGE}
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder>{
        private final Size size;

        public Builder(Size size){
            this.size=size;
        }

        @Override
        public MyPizza build() {
            return new MyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private MyPizza(Builder builder){
        super(builder);
        this.size=builder.size;
    }
}
```

하위 클래스의 빌더가 정의한 build 메서드는 해당하는 구체 하위 클래스를 반환하고 있습니다.
즉, Pizza가아닌 MyPizza를 반환한다는 것을 뜻합니다.

이러한 기능을 이용하면 클라이언트가 형변환에 신경쓰지 않고도 빌더를 만들 수 있습니다.

**단점도 알아봅시다!**

지금까지 공부한
빌더 패턴은 상당히 유연합니다. 매개변수를 선택적으로 집어넣을 수도 있고, 매개변수에따라 다른 객체도 만들 수 있습니다.

단점으로는, 빌더 생성 비용이 발생할 수 있다는 것입니다.
성능이 민감한 상황에서는 빌더를 생성하는 비용또한 문제가 될수도 있습니다.

그러므로, 점층적 생성자 패턴을 완전히 대체할만한 (예를 들면, 매개변수의 개수가 4개이상..) 값어치가 있을 때,
사용하는 것을 추천합니다.

***

### 🚀 Item3. private 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말합니다.

```java
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

//    singleton
    private Test1(){
        
    }
    public static Test1 getInstanceWithSingleton(){
        return test1;
    }

}
```

하지만, 싱글톤의 단점도 엄청 많습니다.

1. Private 생성자를 갖고 있기 때문에 상속할 수 없습니다.
2. 테스트하기 어렵습니다.
   - 싱글톤은 만들어지는 방식이 제한적이기 때문에 테스트에서 사용될 때 목 오브젝트 등오로 대체하기 힘듭니다. 초기화 과정에서 생성자 등을 통해 사용할
   오브젝트를 다이내믹하게 주입하기 힘들기 때문입니다.
3. 싱글톤의 사용은 전역상태를 만들 수 있기 때문에 바람직하지 못합니다.

이렇게 만들어진 클래스를 직렬화하려면 단순히 `serializable`을 구현한다고 선언하는 것만으로는 부족합니다.
왜냐하면, 싱글턴은 단 하나의 인스턴스가 있음을 보장해야합니다. 즉, 역직렬화시에 인스턴스가 새로 생성되는 것을 막아야합니다.
따라서, 모든 필드를 `transient`선언하고 readResolve 메서드를 제공하여야 합니다.

**👍 번외로, 스프링빈은 이와 같은 문제를 완벽히 해결하여 싱글톤의 상태를 유지시켜줍니다!**

**추가로, Enum 타입을 활용하여 싱글턴을 만들수도 있다고 합니다**
```java
public enum Test1 {
    INSTANCE;
}
```

이 방식은 더 간결하고 추가 노력없이 직렬화 할수 있습니다. 심지어 아주 복잡한 직렬화 상황에서도 인스턴스가 생기는 일을 완벽히 막아줍니다.