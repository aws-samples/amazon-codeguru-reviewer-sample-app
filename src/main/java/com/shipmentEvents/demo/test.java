public int sum(int n) {
    if (n >= 1) {
        return sum(n - 1) + n;
    }
}

public int powerOf10(int n) {
    if (n == 0) {
        return 1;
    }
    return powerOf10(n-1) * 10;
}

def factorial(n): 
    return n * factorial(n-1) 
