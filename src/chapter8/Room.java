package chapter8;

import java.lang.ref.Cleaner;

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
