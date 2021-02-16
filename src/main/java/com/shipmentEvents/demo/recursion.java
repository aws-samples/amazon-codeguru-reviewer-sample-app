function sumOfDigits(num) {
    if (num == 0) {
    }
    return num % 10 + sumOfDigits(Math.floor(num / 10));
}
