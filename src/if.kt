// Нажммите зелёную ► слева от этой строки и выберите "Run 'IfKt'"
fun main() = robot("if11", 4)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun ifN() { решение }, где N - номер задания
// Используйте команды робота:
//          left(), right(), up(), down() - движение робота
//          paint() - перекраска клетки в зелёный цвет
// Для организации ветвлений используйте оператор if: if(условие) действие1 else действие2
// Действие может быть {блоком из нескольких команд}, ветка else - не обязательна.
// В качестве условий повтора используйте команды проверки робота:
//          freeFromLeft, freeFromRight, freeFromUp, freeFromDown - свободен ли путь в соседнюю клетку?
//          wallFromLeft, wallFromRight, wallFromUp, wallFromDown - есть ли стена с этой стороны?
//          cellIsPainted - закрашена ли эта клетка зелёным? cellIsFree - эта клетка не зелёная?
// Пример решённого первого задания:
fun if1() {
    right()
    if(freeFromUp) {
        up()
        right()
        down()
    } else {
        down()
        right()
        up()
    }
    paint()
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "if1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun if2() {
    paint()
    up()
    if(wallFromUp) paint()
    down()
    down()
    if(wallFromDown) paint()
    up()
    right()
    if(wallFromRight) paint()
    left()
    left()
    if(wallFromLeft) paint()
    right()
}
// Продолжайте решать задачи до номера if11 включительно
fun if3() {
    if(freeFromUp && freeFromLeft) {
        up()
        left()
    } else if(freeFromUp && freeFromRight) {
        up()
        right()
    } else if(freeFromDown && freeFromLeft) {
        down()
        left()
    } else {
        down()
        right()
    }
    paint()
}

fun if4() {
    if(wallFromUp) down()
    else if(wallFromDown) up()
    else if(wallFromLeft) right()
    else left()
    paint()
}

fun if5() {
    if(wallFromUp || wallFromDown) {
        left()
        paint()
        right()
        right()
        paint()
        left()
    } else {
        up()
        paint()
        down()
        down()
        paint()
        up()
    }
}

fun if6() {
    up()
    if(cellIsPainted) { down(); down() }
    else {
        down(); down()
        if (cellIsPainted) { up(); up() }
        else {
            up(); left()
            if (cellIsPainted) { right(); right() }
        }
    }
    paint()
}

fun if7() = if3()

fun if8() {
    if (wallFromLeft) {
        right()
        paint()
        right()
        paint()
        if (freeFromRight) {
            right()
            paint()
        }
    } else {
        down()
        paint()
        down()
        paint()
        if (freeFromDown) {
            down()
            paint()
        }
    }
}

fun if9() {
    left()
    val u = wallFromUp
    right()
    right()
    val d = wallFromDown
    left()
    if (u && d) {
        up()
        paint()
        down()
        down()
        paint()
        up()
    }
    else
        paint()
}

fun if10() {
    left()
    val l = cellIsPainted
    right()
    right()
    val r = cellIsPainted
    left()
    if (l || r) {
        up()
        paint()
        down()
    }
    else
        paint()
}

fun if11() {
    right()
    if (cellIsPainted) {
        down()
        if(cellIsPainted) {
            down()
            paint()
            up()
        }
        else
            paint()
        up()
    }
    else
        paint()
    right()
}