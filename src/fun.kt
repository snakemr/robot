// Нажммите зелёную ► слева от этой строки и выберите "Run 'FunKt'"
fun main() = robot("fun14", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun funN() { решение }, где N - номер задания
// Для выполнения повторяющихся действий создавайте свои собственные функции: fun имяФункции() { ... }
// и вызывайте их из основной программы: имяФункции()
// Пример решённого первого задания:
fun cross() {
    paint()
    up();       paint();    down()
    right();    paint();    left()
    down();     paint();    up()
    left();     paint();    right()
}

fun fun1() {
    cross()
    repeat(5) { right() }
    up()
    cross()
    repeat(3) { up() }
    repeat(3) { left() }
    cross()
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "fun1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun box4() {
    paint()
    right()
    paint()
    down()
    paint()
    left()
    paint()
    up()
}

fun fun2() {
    repeat(4) {
        box4()
        repeat(3) { right() }
    }
    box4()
}
// Продолжайте решать задачи до номера fun15 включительно
fun fun3() {
    repeat(5) {
        box4()
        right(); right()
        down();  down()
    }
    box4()
}

fun box16() {
    box4()
    right(); right()
    box4()
    down();  down()
    box4()
    left();  left()
    box4()
    up();    up()
}
fun fun4() = box16()

fun fun5() {
    box16()
    repeat(4) { right() }
    box16()
    repeat(4) { down() }
    box16()
    repeat(4) { left() }
    box16()
    repeat(4) { up() }
}

fun row() {
    repeat(8) {
        paint()
        right()
    }
    paint()
    repeat(8) { left() }
}

fun fun6() {
    repeat(3) {
        row()
        down(); down()
    }
    row()
}

fun col() {
    repeat(8) {
        paint()
        down()
    }
    paint()
    repeat(8) { up() }
}

fun fun7() {
    repeat(3) {
        col()
        right(); right()
    }
    col()
}

fun fun8() {
    repeat(3) {
        row()
        down(); down()
    }
    row()
    repeat(7) { up() }
    right()
    repeat(3) {
        col()
        right(); right()
    }
    col()
}

fun fun9() {
    repeat(2) {
        row()
        col()
        right()
        down()
        right()
        down()
    }
    row()
    col()
}

fun kontour() {
    repeat(4) {
        paint();    right()
    }
    repeat(4) {
        paint();    down()
    }
    repeat(4) {
        paint();    left()
    }
    repeat(4) {
        paint();    up()
    }
}

fun fun10() {
    repeat(4) {
        kontour()
        repeat(6) { right() }
    }
    kontour()
}

fun punktir2() {
    var c = 0
    repeat(12) {
        if (c%2 == 0) paint()
        right()
        c++
    }
    if (c%2 == 0) paint()
    repeat(12) { left() }
}

fun fun11() {
    repeat(5) {
        punktir2()
        down(); down()
    }
}

fun fun12() {
    repeat(5) {
        punktir2()
        down(); right()
        punktir2()
        left(); down()
    }
    right()
    up()
}

fun punktir3() {
    var c = 0
    repeat(12) {
        if (c%3 == 0) paint()
        right()
        c++
    }
    if (c%3 == 0) paint()
    repeat(12) { left() }
}

fun fun13() {
    repeat(4) {
        repeat(3) {
            punktir3()
            down(); right()
        }
        left(); left(); left()
    }
}