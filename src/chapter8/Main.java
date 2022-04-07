package chapter8;

public class Main {
    public static void main(String[] args) throws Exception {
        try(Room room=new Room(7)){
            System.out.println("Hello");
        }
    }
}
