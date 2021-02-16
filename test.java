private Random random = new Random();
public static final ArrayList<Double> list = new ArrayList<Double>(1000000);

@Test
public void givenStaticField_whenLotsOfOperations_thenMemoryLeak() throws InterruptedException {
    for (int i = 0; i < 1000000; i++) {
        list.add(random.nextDouble());
    }
    
    System.gc();
    Thread.sleep(10000); // to allow GC do its job
}


package com.example.memoryleak;
public class Adder {
       publiclong addIncremental(long l)
       {
              Long sum=0L;
               sum =sum+l;
               return sum;
       }
       public static void main(String[] args) {
              Adder adder = new Adder();
              for(long ;i<1000;i++)
              {
                     adder.addIncremental(i);
              }
       }
}
