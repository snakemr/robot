// Нажммите зелёную ► слева от этой строки и выберите "Run 'MixKt'"
fun main() = robot("mix11", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun mixN() { решение }, где N - номер задания
fun mix1() {
    while (true) {
        var n = 0
        while (freeFromRight) {
            paint()
            right()
            n++
        }
        paint()
        if (wallFromDown) break
        repeat(n) { left() }
        down()
    }
}
// Продолжайте решать задачи до номера mix11 включительно
fun mix10() {
    var a = BooleanArray(10)
    var i = 0
    while (freeFromUp) {
        a[i++] = cellIsPainted
        up()
    }
    right()
    while (freeFromDown) down()
    while (freeFromRight) right()
    i = 0
    while (freeFromUp) {
        if (a[i++]) paint()
        up()
    }
}

fun mix11() {
    var a = BooleanArray(10)
    var i = 0
    while (freeFromUp) {
        a[i++] = cellIsPainted
        up()
    }
    while (freeFromRight) right()
    while (freeFromDown) {
        if (a[i--]) paint()
        down()
    }
}
