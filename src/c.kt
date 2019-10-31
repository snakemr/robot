// Нажммите зелёную ► слева от этой строки и выберите "Run 'CKt'"
fun main() = robot("c2", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun cN() { решение }, где N - номер задания
// Используйте команды робота:
//          left(), right(), up(), down() - движение робота
//          paint() - перекраска клетки в зелёный цвет
// Для повторяющихся действий используйте блок повтора:     repeat(10) {...}
// Либо, вместо него - оператор цикла со счётчиком for:     for(i in 1..10) {...}
// Пример решённого первого задания:
fun c1() {
    repeat(10) {
        right()
    }
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "c1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun c2() {
    repeat(10) {
        paint()
        right()
    }
}
// Продолжайте решать задачи до номера c16 включительно
fun c3() {
    repeat(10) {
        right()
        paint()
    }
}

fun c4() {
    repeat(10) {
        paint()
        right()
    }
    paint()
}

fun c5() {
    repeat(10) {
        paint()
        right()
        down()
    }
}

fun c6() {
    repeat(8) {
        left()
        paint()
        left()
    }
    repeat(16) {
        right()
    }
}

fun c7() {
    repeat(5) {
        paint()
        down()
        paint()
        left()
        paint()
        up()
        paint()
        left()
    }
}

fun c8() {
    repeat(10) {
        paint()
        down()
        left()
        up()
    }
    paint()
}

fun c9() {
    repeat(5) {
        paint()
        up()
        right()
        paint()
        down()
        right()
    }
}

fun c10() {
    repeat(10) {
        paint()
        right()
    }
    down()
    repeat(10) {
        left()
        paint()
    }
}

fun c11() {
    repeat(10) {
        paint()
        left()
    }
    paint()
    down()
    paint()
    repeat(10) {
        right()
        paint()
    }
}

fun c12() {
    repeat(5) {
        right()
    }
    repeat(10) {
        paint()
        left()
    }
    paint()
    repeat(5) {
        right()
    }
}

fun c13() {
    repeat(8) {
        paint()
        down()
    }
    repeat(8) {
        paint()
        left()
    }
    repeat(8) {
        paint()
        up()
    }
    repeat(8) {
        paint()
        right()
    }
}

fun c14() {
    repeat(4) {
        paint()
        right()
        up()
    }
    repeat(4) {
        paint()
        down()
        right()
    }
    repeat(4) {
        paint()
        left()
        down()
    }
    repeat(4) {
        paint()
        up()
        left()
    }
}

fun c15() {
    repeat(4) { up() }
    repeat(8) {
        paint()
        down()
    }
    paint()
    repeat(4) {
        left()
        up()
    }
    repeat(8) {
        paint()
        right()
    }
    paint()
    repeat(4) { left() }
}

fun c16() {
    repeat(5){
        paint()
        up()
        right()
        right()
        down()
    }
    down()
    left()
    up()
    repeat(4){
        paint()
        down()
        left()
        left()
        up()
    }
    paint()
}