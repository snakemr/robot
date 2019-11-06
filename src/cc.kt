// Нажммите зелёную ► слева от этой строки и выберите "Run 'CcKt'"
fun main() = robot("cc19", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun ccN() { решение }, где N - номер задания
// Используйте команды робота:
//          left(), right(), up(), down() - движение робота
//          paint() - перекраска клетки в зелёный цвет
// Для повторяющихся действий используйте вложенные циклы со счётчиком for: for(i in 1..10) {...}
// Пример решённого первого задания:
fun cc1() {
    for(i in 1..6) {
        for(j in 1..5) {
            paint()
            right()
        }
        down()
    }
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "cc1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun cc2() {
    for(i in 1..6) {
        for(j in 1..i) {
            paint()
            right()
        }
        down()
    }
}
// Продолжайте решать задачи до номера cc19 включительно
fun cc3() {
    for(i in 6 downTo 1) {
        for(j in 1..i) {
            paint()
            right()
        }
        down()
    }
}

fun cc4() {
    repeat(9) {
        repeat(10) {
            paint()
            right()
        }
        paint()
        repeat(10) {
            left()
        }
        down()
    }
}

fun cc5() {
    repeat(4) {
        repeat(13) { right() }
        down()
        repeat(13) { left() }
        down()
    }
}

fun cc6() {
    repeat(5) {
        repeat(4) { right() }
        repeat(9) { up() }
        paint()
        repeat(9) { down() }
    }
}

fun cc7() {
    for(i in 1..9) {
        repeat(i) {
            paint()
            right()
        }
        repeat(i) {
            left()
        }
        down()
    }
}

fun cc8() {
    for(i in 9 downTo 1) {
        repeat(i) {
            paint()
            right()
        }
        repeat(i) {
            left()
        }
        down()
    }
}

fun cc9() {
    for(i in 1..9) {
        paint()
        repeat(i) {
            left()
        }
        down()
        repeat(i) {
            right()
        }
    }
}

fun cc10() {
    for(i in 1..7) {
        repeat(i*2) {
            paint()
            right()
        }
        repeat(i*2) {
            left()
        }
        down()
    }
}

fun cc11() {
    for(i in 1..7) {
        repeat(i*2-1) {
            paint()
            right()
        }
        repeat(i*2-1) {
            left()
        }
        down()
    }
}

fun cc12() {
    var x = 1
    for(i in 1..6) {
        repeat(x) {
            paint()
            right()
        }
        repeat(x) {
            left()
        }
        down()
        x *= 2
    }
}

fun cc13() {
    var x = 13
    for(i in 1..4) {
        repeat(x) {
            right()
        }
        down()
        x -= 2
        repeat(x) {
            left()
        }
        paint()
        down()
    }
}

fun cc14() {
    var x = 13
    for(i in 1..4) {
        repeat(x) {
            right()
        }
        down()
        x --
        repeat(x) {
            left()
        }
        down()
        x --
    }
}

fun cc15() {
    for(i in 0..7) {
        repeat(i) { right() }
        repeat(i*2) {
            paint()
            left()
        }
        paint()
        repeat(i) { right() }
        down()
    }
}

fun cc16() {
    repeat(4) {
        for(i in 1..5) {
            repeat(i) {
                paint()
                up()
            }
            repeat(i) {
                down()
            }
            right()
        }
    }
}

fun cc17() {
    for(j in 1..6) {
        for(i in 1..j) {
            repeat(i) {
                paint()
                up()
            }
            repeat(i) {
                down()
            }
            right()
        }
        right()
    }
    left()
}

fun cc18() {
    repeat(7) {
        while (freeFromRight) right()
        paint()
        while (freeFromLeft) left()
        down()
    }
}

fun cc19() {
    while(freeFromDown) {
        while (freeFromRight) right()
        paint()
        while (freeFromLeft) left()
        down()
    }
}