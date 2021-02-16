public class MemLeak {
    public static void main(String[] args) throws InterruptedException {
        int ITERATIONS=100000;
        for (int i=0 ; i<ITERATIONS ; i++) {
            MemLeak.class.getClassLoader().getResourceAsStream("resource.txt");
        }
        System.out.println("finished creation of streams, now waiting to be killed");

        Thread.sleep(Long.MAX_VALUE);
    }

}
