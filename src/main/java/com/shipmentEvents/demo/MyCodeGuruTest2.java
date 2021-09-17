package com.shipmentEvents.demo;

public class MyCodeGuruTest2 {
    
    public static void main(String[] args) {
        MyCodeGuruTest2.processNumbers(10, 12, 14, 100, 193);
        MyCodeGuruTest2.createDeadlock();
    }

    private static void createDeadlock() {
        MyCodeGuruTest2 test = new MyCodeGuruTest2();

        final A a = test.new A();
        final B b = test.new B();

        // Thread-1
        Runnable block1 = new Runnable() {
            public void run() {
                synchronized (a) {
                    try {
                        // Adding delay so that both threads can start trying to
                        // lock resources
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Thread-1 have A but need B also
                    synchronized (b) {
                        System.out.println("In block 1");
                    }
                }
            }
        };

        // Thread-2
        Runnable block2 = new Runnable() {
            public void run() {
                synchronized (b) {
                    // Thread-2 have B but need A also
                    synchronized (a) {
                        System.out.println("In block 2");
                    }
                }
            }
        };

        new Thread(block1).start();
        new Thread(block2).start();
    }

    // Resource A
    private class A {
        private int i = 10;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    // Resource B
    private class B {
        private int i = 20;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    public static void processNumbers(int a, int b, int c, int d, int e) {
        if (a > 100) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers1");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers2");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers3");
                        } else {
                            new Configuration().addConfigurationName("processNumbers4");
                        }
                        new Configuration().addConfigurationName("processNmubers5");
                    }
                }
            }
        } else if (a > 50) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers6");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers7");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers8");
                        } else {
                            new Configuration().addConfigurationName("processNumbers9");
                        }
                        new Configuration().addConfigurationName("processNmubers10");
                    }
                }
            }

        } else if (a > 25) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers11");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers12");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers13");
                        } else {
                            new Configuration().addConfigurationName("processNumbers14");
                        }
                        new Configuration().addConfigurationName("processNmubers15");
                    }
                }
            }
        } else if (a > 10) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers16");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers17");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers18");
                        } else {
                            new Configuration().addConfigurationName("processNumbers19");
                        }
                        new Configuration().addConfigurationName("processNmubers20");
                    }
                }
            }
        } else if (a > 5) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers21");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers22");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers23");
                        } else {
                            new Configuration().addConfigurationName("processNumbers24");
                        }
                        new Configuration().addConfigurationName("processNmubers25");
                    }
                }
            }
        } else if (a > -100) {
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    for (int k = 0; k < a; k++) {
                        if (b < 1 && c > 55 && d < 9 && e == 10) {
                            new Configuration().addConfigurationName("processNumbers26");
                        } else if(b == 14) {
                            new Configuration().addConfigurationName("processNumbers27");
                        } else if (d == e || c == a || d == 1) {
                            new Configuration().addConfigurationName("processNumbers28");
                        } else {
                            new Configuration().addConfigurationName("processNumbers29");
                        }
                        new Configuration().addConfigurationName("processNmubers30");
                    }
                }
            }
        }
    }

    private void methodNeverCalled() {
        System.out.println("no ones calls me");
    }
}
