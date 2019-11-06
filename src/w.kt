// Нажммите зелёную ► слева от этой строки и выберите "Run 'WKt'"
fun main() = robot("w13", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun wN() { решение }, где N - номер задания
// Используйте команды робота:
//          left(), right(), up(), down() - движение робота
//          paint() - перекраска клетки в зелёный цвет
// Для повторяющихся действий используйте оператор цикла с условием:    while(условие) {...}
// В качестве условий повтора используйте команды проверки робота:
//          freeFromLeft, freeFromRight, freeFromUp, freeFromDown - свободен ли путь в соседнюю клетку?
//          wallFromLeft, wallFromRight, wallFromUp, wallFromDown - есть ли стена с этой стороны?
//          cellIsPainted - закрашена ли эта клетка зелёным? cellIsFree - эта клетка не зелёная?
// Пример решённого первого задания:
fun w1() {
    while(freeFromRight) {
        right()
    }
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "w1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun w2() {
    while(freeFromRight) {
        paint()
        right()
    }
}
// Продолжайте решать задачи до номера w17 включительно
fun w3() {
    while(wallFromDown) {
        right()
    }
}

fun w4() {
    right()
    while(cellIsPainted) {
        right()
    }
}

fun w5() {
    while (cellIsPainted) right()
    left()
    while (cellIsPainted) down()
    up()
}

fun w6() {
    while (freeFromDown) down()
    while (wallFromDown) right()
    while (freeFromDown) down()
    while (freeFromRight) right()
}

fun w7() {
    while (cellIsFree) right()
    up()
    while (cellIsFree) up()
    right()
    while (cellIsFree) right()
}

fun w8() {
    while (wallFromDown) right()
    left()
    while (wallFromDown) {
        paint()
        left()
    }
}

fun w9() {
    while (wallFromRight) {
        paint()
        up()
    }
    paint()
    right()
    while (freeFromDown) {
        paint()
        down()
    }
    paint()
}

fun w10() {
    while (freeFromRight) right()
    w9()
    while (freeFromRight) right()
    w9()
    while (freeFromRight) right()
}

fun w11() {
    paint()
    right()
    while (wallFromDown) {
        paint()
        right()
    }
    paint()
    down()
    paint()
    left()
    while (wallFromUp) {
        paint()
        left()
    }
    paint()
    up()
}

fun w12() {
    paint()
    right()
    while (wallFromDown) {
        paint()
        right()
    }
    paint()
    down()
    while (wallFromLeft) {
        paint()
        down()
    }
    paint()
    left()
    while (wallFromUp) {
        paint()
        left()
    }
    paint()
    up()
    while (wallFromRight) {
        paint()
        up()
    }
}

fun w13() {
    while (wallFromLeft && wallFromRight) up()
}

fun w14() {
    while (wallFromLeft || wallFromRight) up()
}

fun w15() {
    while (wallFromDown || cellIsFree) right()
    down()
    paint()
}

fun w16() {
    while (wallFromRight) {
        up()
        right()
        down()
    }
}

fun w17() {
    while (wallFromRight) {
        up()
        right()
        right()
        down()
    }
    left()
}