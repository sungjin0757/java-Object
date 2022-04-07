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

***

### 🚀 Item4. 인스턴스화를 막으려거든 private 생성자를 사용하라.

정적 메소드, 정적 필드만을 담은 클래스를 만들고 싶을 때 주로 인스턴스활를 막습니다.

인스턴스화를 막을 때는 private 생성자를 추가하여주면 됩니다.
```java
public class Test{
    private Test(){
        throw new AssertionError();
    }
}
```

이 코드는 어떤 환경에서도 클래스가 인스턴스화 되는 것을 막을 수 있습니다.

또한, 이 방식은 상속을 불가능하게 하는 효과도 있습니다. 모든 생성자는 명시적이든 묵시적이든 상위 클래스의 생성자를 호출하게 되는데, 이를 `private`로 막아놨기 때문
입니다.

***

### 🚀 Item5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라.

의존 객체 주입 방식은 `Spring`과 같은 `framework`에서 많이 사용하는 방식입니다.

예를 들어, `service`와 `repository`의 관계를 생각해봅시다.

`service`는 `Jpa`, `MyBatis`, `JDBC`등 다양한 형태의 `repository`를 사용할 수 있어야 유연하게 코드가 작성되었다고 말할 수 있습니다.

하지만, 다음과 같이

```java
@Service
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository=new JdbcUserRepository();
} 
```

이런식으로, `final`자원에 `repository`의 구체화를 명시해두면 한 `repository`에만 종속됨과 동시에 코드의 유연한 확장이라곤 기대할 수 없는 코드가 됩니다.

이럴때, 필요한 것이 생성자를 통한 의존 객체 주입입니다.

```java
@Service
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }
}
```

위와 같은 방식을 말하죠.. 스프링에서는 의존객체 주입을 더욱 더 잘 사용하기 위해서 클라이언트 전략을 취합니다.

이와 같은 내용은 이곳에 정리해 두었습니다 : <a href="https://velog.io/@sungjin0757/SPRING-%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8%EC%99%80-%EC%9D%98%EC%A1%B4%EA%B4%80%EA%B3%84-1" target="_blank">바로가기</a>
> 시리즈별로 정리해 두었습니다.
> 

***
### 🚀 Item6. 불필요한 객체 생성을 피하라.

똑같은 기능의 객체를 매번 생성하기 보다는 객체 하나를 재사용하는 것이 더욱 바람직합니다. 예를 들어 `String`타입과 같은 불변 객체에는 더욱더 그럽니다

```java
String s=new String("Hello"); // 실행될 때 마다 인스턴스를 생성
String s=String.valueOf("Hello"); //객체의 재사용
```

따라서 `Java`에서는 `new String()`은 deprecated시켜놓고 valueOf메소드를 적극 권장합니다.

생성 비용이 아주 비싼 객체도 있습니다. 이럴 경우에 캐싱하여 재사용하는 것은 훌륭한 작업입니다.

```java
public class RomanNumerals {
   public static boolean isRomanNumeralFirst(String s) {
      return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
              + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
   }
}
```
위의 코드는 문자열이 로마 숫자인지 확인하는 코드입니다.

이 방식의 문제점은 `matches`에 있습니다. 이유는 `matches`는 정규 표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만
정규 표현식 패턴을 한 번만 쓰고 버린다는 아쉬운 점이 있습니다. Pattern은 입력받은 정규표현식에 해당하는 유한 상태 머신을 만들기 때문에 인스턴스 생성 비용이 높습니다.

그러므로 클래스를 인스턴스화할 적에 Pattern을 미리 캐싱하여두고 사용하는 것이 바람직합니다.

```java
public class RomanNumerals {
    public static boolean isRomanNumeralFirst(String s){
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
                + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }

    //캐싱하여 사용하는 방식
    static final Pattern Roman=Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    public static boolean isRomanNumeralSecond(String s){
        return Roman.matcher(s).matches();
    }

}
```

메소드의 시간을 측정해 봅시다. 시간 측정을 하는 메소드는 중복된 코드로서 재사용될 확률이 높습니다. 그러므로 저는 템플릿/콜백 패턴을 이용하였습니다.

**템플릿**
```java
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
```

**콜백**
```java
public interface MethodProceed {
    Method proceed() throws NoSuchMethodException;
}
```

```java
public class Main {
   public static void main(String[] args) throws Throwable {
      //메소드 Time Check
      RomanNumerals romanNumerals = new RomanNumerals();
      
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
      

   }
}
```

**실행 결과**

<img width="70%" alt="스크린샷 2022-04-05 오후 8 22 09" src="https://user-images.githubusercontent.com/56334761/161743427-e2090267-5deb-42b2-95ba-f721905693b7.png">

실행시간이 확연히 차이나는 것을 볼 수 있습니다.

불필요한 객체를 만들어 내는 또다른 예로는 오토박싱이 있습니다.

오토박싱은 프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 변환해주는 기능입니다. 오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을
흐려주지만, 완전히 없애주는 것은 아닙니다. 즉 성능에 차이가 있을 수 있다는 것이죠.

```java
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

public class Main {
   public static void main(String[] args) throws Throwable {
      //메소드 Time Check
      Sum sum = new Sum();

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
```
**실행 결과**

<img width="70%" alt="스크린샷 2022-04-05 오후 8 43 38" src="https://user-images.githubusercontent.com/56334761/161746619-93c25f17-9335-41dd-aec9-f6723aded4eb.png">

엄청난 차이를 보이고 있습니다. 이는, 불필요한 Long인스턴스를 엄청나게 만들었기 때문입니다.

이는 객체 생성은 비싸니 피하자는 것보다는 조심하자라는 의미를 내재하고 있습니다.

요즘 JVM은 Gc가 잘 최적화 되어서 작은 객체를 만들고 회수하는 일은 크게 성능을 좌지우지 하지 않는다고 합니다.

***

### 🚀Item7. 다 쓴 객체 참조를 해제하라.

자바는 JVM을 가지고 있기 때문에 메모리 회수에는 좀 더 관대한 편입니다. 그렇다고 하더라도 메모리 관리에 아예 신경쓰지 않으면 안됩니다.

```java
public class Stack {
    private Object[] elements;
    private int size=0;
    private static final int DEFAULT_INITIAL_CAPACITY=16;

    public Stack(){
        elements=new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e){
        ensureCapacity();
        elements[size++]=e;
    }

    public Object pop(){
       if(size==0){
          throw new EmptyStackException();
       }
        Object pop=elements[--size];
        return pop;
    }

    private void ensureCapacity(){
        if(elements.length==size){
            elements=Arrays.copyOf(elements,size*2+1);
        }
    }
}
```

위의 코드는 `Stack`을 간략화한 코드입니다. 겉보기에는 멀쩡해 보이지만, 숨어있는 문제가 있습니다.

그 문제는 메모리 누수이고, 점차적으로 성능이 저하될 것입니다.

어떤 문제가 있을 까요?? 바로 `Pop`에서의 과정이 문제입니다. 스택에서 꺼내진 객체는 GC가 회수해 가지않습니다. 왜냐하면 현재 활성화 되어있는 배열의 크기만큼만
사이즈를 조정해주고 있을 뿐 따로 회수하지 않기 때문입니다.

그러므로 스택에서 꺼내진 부분의 요소로 `null`을 집어 넣어야합니다.

```java
public Object pop(){
    if(size==0){
        throw new EmptyStackException();
    }
    Object pop=elements[--size];
    elements[size]=null;
    return pop;
}
```

이제 null 처리를 하였기 때문에 이 부분을 참조하려고 하면 `NullPointerException`이 던져집니다.

객체 참조를 Null처리하는 일은 예외 적인 경우여야 합니다.

근데 왜 `stack`에서는 메모리 누수에 취약했을 까요??

자기 자신이 직접 메모리를 관리했기 때문입니다. 이 스택은 객체 자체가 아니라 객체 참조를 담는 `elements`배열로 저장소 풀을 만들어 관리했습니다.
즉, 활성 영역과 비활성 영역은 이 `stack`만이 알 뿐이지 GC는 똑같이 유효한 객체로 판단합니다. 
따라서, 취약했었던 것입니다.

**캐시 역시 메모리 누수를 일으키는 주범입니다.**  객체 참조를 캐시에 넣고 나서, 객체를 다 쓴 뒤로 까먹고 한참을 놔둘 수 있기 때문입니다.

이럴때는, `WeakHashMap`을 사용하는 것이 권장 됩니다. 약한 참조로서 `null`선언만 하면 바로 GC의 대상이 되기 때문입니다.

***

### 🚀 Item8. finalizer와 cleaner사용을 피하라

자바는 두 가지 객체 소멸자를 제공합니다. 그중 `finalizer`는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요합니다.

또한, `cleaner`는 `finalizer`보다는 덜 위험하지만, 예측 불가능합니다.

C++의 파괴자는 특정 객체와 관련된 자원을 회수하지만, 일반적으로 자바에서는 자원을 회수하고 쓸모 없어진 객체는 JVM의 GC가 담당합니다.

물론 예외의 상황에서 자원을 회수하기 위해서는 `try-finally`나 `try-with-resources`를 사용해 해결합니다.

`finalizer`와 `cleaner`는 제때 수행된다는 보장이 업습니다. 즉, 자원 회수를 얘들한테 맡기면 치명적 오류를 낳을 수도 있습니다.

**`finalizer`와 `cleaner`는 심각한 성능 문제 또한 야기합니다.**

왜냐하면 이들이 GC의 효율을 떨어뜨리기 때문입니다.

**`finalizer`를 사용한 클래스는 `finalizer` 공격에 노출되어 심각한 보안 문제를 일으킬 수도 있습니다**

생성자나 직렬화 과정에서 예외가 발생하면, 생성되다 만 객체에서 악의적인 하위클래스의 `finalizer`가 수행될수 있게 되기 때문입니다.
또한 , 이 `finalizer`는 정적 필드에 자신의 참조를 할당하여 GC의 대상에서 빠져나갈 수도 있습니다.
이렇게 일그러진 객체에서 메소드를 호출하여 허용되지 않았을 작업을 수행하는 것은 일도 아니게 됩니다.

`final`이 아닌 클래스를 `finalizer`공격으로부터 방어하려면 아무 일도 하지 않는 `finalize`메소드를 만들고 `final`로 선언합니다.

그렇다면, 파일이나 스레드 등을 종료해야할 때 대안은 무엇이 있을 까요??
`AutoCloseable`을 구현하고, 클라이언트에서 인스턴스를 다 쓰고 나면 `close`메소드를 호출하면 됩니다.

`close` 메소드에서 이 객체는 더 이상 유효하지 않음을 필드에 기록하고, 다른 메소드는 이 필드를 검사해서 객체가 닫힌 후에 불렸다면 `IllegalStateException`을 던지는 것입니다.

그렇다면, `cleaner`와 `finalizer`를 사용하는 경우는 어떤 경우일 까요??

자원을 `close`하지 않았을 때의 안전망으로서 주로 사용한다고 합니다.

또한, 네이티브 피어와 연결된 객체에서 많이 사용한다고 합니다. 네이티브 피어란 일반 자바 객체가 네이티브 메서드를 통해 기능을 위힘한 네이티브 객체를 말합니다.
이 네이티브 피어는 일반 객체가 아니기 때문에 GC의 대상이 아닙니다. 따라서 `cleaner`와 `finalizer`가 적절히 활용됩니다. 만일 심각한 자원을 회수해야한다면 이 또한
`close`를 사용하는 것이 적절합니다.

`Cleaner`의 사용방법을 봅시다
```java
public class Room implements AutoCloseable{
    private static final Cleaner cleaner=Cleaner.create();

    private static class State implements Runnable{
        int numJunkPiles;

        State(int numJunkPiles){
            this.numJunkPiles=numJunkPiles;
        }

        @Override
        public void run() {
            System.out.println("방 청소");
            numJunkPiles=0;
        }
    }

    //방의 상태. cleanable에 등록할 것
    private final State state;

    //수거 대상을 등록해야함 여기에.
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        this.state = new State(numJunkPiles);
        this.cleanable = cleaner.register(this,state);
    }

    @Override
    public void close() throws Exception {
        cleanable.clean();
    }
}
```

중첩 `class`인 `state`는 `cleaner`가 방을 청소할 때 수거할 자원들을 담고 있습니다.

`state`는 `runnable`을 구현하고, 그 안의 run메소드는 `cleanable`에 의해 딱 한번 호출될 것입니다.

보통은 `Room`의 `close` 메소드를 호출할 때마다 `cleanable`에 등록된 `state`의 `run`을 호출하게 됩니다. 혹은 GC가 Room을 회수할 때 까지
`close`를 호출하지 않으면 `cleaner`가 `state`의 `run` 메소드를 호출할 수도 있습니다.

`state`는 절대 `room`을 참조해서는 안됩니다. 이 경우 순환 참조가 생겨 GC가 `room`을 회수해가지 못하기 때문입니다.

이제 한번 `close` 메소드를 호출해 봅시다.

```java
public class Main {
    public static void main(String[] args) throws Exception {
        try(Room room=new Room(7)){
            System.out.println("Hello");
        }
    }
}
```

**실행 결과**

<img width="70%" alt="스크린샷 2022-04-05 오후 9 30 21" src="https://user-images.githubusercontent.com/56334761/161753955-df7c76bb-3772-4c0e-a855-cb4bb9410dac.png">

***

### 🚀 Item9. try-finally보다는 try-with-resources를 사용하라.

자바 라이브러리에는 `close`메소드를 호출해 직접 닫아줘야 하는 자원이 많습니다.
`Connection`, `BufferedReader`등등이 있죠.

`close`를 호출하기 위해서는 전통적으로 `try-finally`를 사용합니다.

```java
public class Resource {
   static void copyV1(String s, String dst) throws IOException {
      InputStream in = new FileInputStream(s);
      try {
         OutputStream out = new FileOutputStream(dst);
         try {
            byte[] buf = new byte[100];
            int n;
            while ((n = in.read(buf)) >= 0) {
               out.write(buf, 0, n);
            }
         } finally {
            out.close();
         }
      } finally {
         in.close();
      }
   }
}
```

`try-finally`를 활용한 자원회수를 보여주고 있습니다. 여기서 예기치 못한 예외가 발생해 `InputStream`이 실행되지 못한다면

`outputStream` -> `out.close()` -> `in.close()`가 차례대로 예외가 발생하여 실행하지 목할 것입니다.

그러면 스택 추적 내용에 `in.close()`의 예외정보만 남게 되고 나머지 예외에 대한 정보는 남지 않게 될 것입니다. 이것은 실제 시스템에서의 디버깅을 몹시 어렵게 만들 수
있습니다.

이러한 문제를 해결하기 위해서 `try-with-resources`를 사용합니다.

```java
static void copyV2(String s, String dst) throws IOException{
        try(InputStream in=new FileInputStream(s);OutputStream out=new FileOutputStream(dst)) {
            byte[] buf = new byte[100];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
}
```

위의 코드는 그 예입니다. 훨씬 코드의 양이 줄어들었을 뿐만 아니라 문제를 진단하기도 훨씬 수월해집니다.
여기서는 `InputStream`에서 생긴 예외는 코드에서 나타나지 않는 `close`호출 예외는 숨겨지고 `InputStream`에서 발생한 예외가 기록됩니다.

또한, `try-with-resouces`절 또한 `catch`절 덕분에 `try`문을 더 중첩하지 않고도 다수의 예외를 처리할 수 있습니다.

***
### <span style="color:lightpink; font-weight:bold;">이상으로 마치겠습니다. 🙋🏻‍♂️</span>