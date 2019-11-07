// Нажммите зелёную ► слева от этой строки и выберите "Run 'CntKt'"
fun main() = robot("cnt17", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun cntN() { решение }, где N - номер задания
// Для решения задач необходимо объявить один или несколько счётчиков: var x = 0
// Увеличение счётчика: x++     Уменьшение: x--
// Пример решённого первого задания:
fun cnt1() {
    var c = 0
    while (freeFromRight) {
        right()
        c++
    }
    paint()
    repeat(c) {
        left()
    }
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "cnt1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun cnt2() {
    var x = 0
    var y = 0
    while (freeFromRight) {
        right()
        x++
    }
    while (freeFromDown) {
        down()
        y++
    }
    paint()
    repeat(y) {
        up()
    }
    repeat(x) {
        left()
    }
}
// Продолжайте решать задачи до номера cnt17 включительно
fun cnt3() {
    var c = 0
    while (cellIsFree) {
        right()
        c++
    }
    repeat(c) {
        up()
    }
}

fun cnt4() {
    var c = 0
    while (wallFromDown) {
        right()
        c++
    }
    down()
    repeat(c) {
        left()
    }
}

fun cnt5() {
    var x = 0
    var y = 0
    while (freeFromRight) {
        right()
        x++
    }
    while (freeFromDown) {
        down()
        y++
    }
    while(freeFromLeft) {
        paint()
        left()
    }
    while(freeFromUp) {
        paint()
        up()
    }
    while(freeFromRight) {
        paint()
        right()
    }
    while(freeFromDown) {
        paint()
        down()
    }
    repeat(x) {
        left()
    }
    repeat(y) {
        up()
    }
}

fun cnt6() {
    var r = 0
    while (freeFromRight) {
        right()
        r++
    }
    repeat(r) {
        left()
    }
    var l = 0
    while (freeFromLeft) {
        left()
        l++
    }
    if (r<l)
    repeat(l+r) {
        right()
    }
}

fun cnt7() {
    var r = 0
    while (freeFromRight && wallFromDown) {
        right()
        r++
    }
    if (freeFromDown) {
        down()
        repeat(r) {
            left()
        }
    }
    else {
        while (wallFromDown) {
            left()
            r--
        }
        down()
        repeat(-r) {
            right()
        }
    }
}

fun cnt8() {
    var c = 0
    while (freeFromRight) {
        if (cellIsPainted) c++
        if (c>=5) break
        right()
    }
}

fun cnt9() {
    var c = 0
    while (freeFromRight) {
        if (cellIsPainted) c++ else c=0
        if (c>=3) break
        right()
    }
}

fun cnt10() {
    var c = 0
    while (wallFromDown && c++<5) {
        paint()
        right()
    }
}

fun cnt11() {
    var c = 0
    while (freeFromRight) {
        right()
        c++
    }
    repeat(c-1) {
        left()
    }
    if (c>4) paint()
}

fun cnt12() {
    var c = 0
    while (freeFromRight) {
        right()
        c++
        if (c%2 == 1) paint()
    }
}

fun cnt13() {
    var c = 0
    while (freeFromRight) {
        if (c%2 == 0) paint()
        right()
        c++
    }
    if (c%2 == 0) paint()
}

fun cnt14() {
    var c = 0
    while (freeFromRight) {
        if (c%3 == 0) paint()
        right()
        c++
    }
    if (c%3 == 0) paint()
}

fun cnt15() {
    var c = 0
    while (freeFromRight) {
        right()
        c++
        if (c%3 == 2) paint()
    }
}

fun cnt16() {
    var c = 0
    while (freeFromRight) {
        if(cellIsPainted) c++ else if(c%2!=0) paint()
        right()
    }
    if(cellIsFree && c%2!=0) paint()
}

fun cnt17() {
    var c = wallFromDown
    do {
        c = wallFromDown
        right()
    } while (c != wallFromDown)
}