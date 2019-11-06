// Нажммите зелёную ► слева от этой строки и выберите "Run 'cifKt'"
fun main() = robot("cif17", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun cifN() { решение }, где N - номер задания
// Используйте команды робота:
//          left(), right(), up(), down() - движение робота
//          paint() - перекраска клетки в зелёный цвет
// Используйте любые операторы ветвлений и циклов, которые знаете.
// В качестве условий повтора используйте команды проверки робота:
//          freeFromLeft, freeFromRight, freeFromUp, freeFromDown - свободен ли путь в соседнюю клетку?
//          wallFromLeft, wallFromRight, wallFromUp, wallFromDown - есть ли стена с этой стороны?
//          cellIsPainted - закрашена ли эта клетка зелёным? cellIsFree - эта клетка не зелёная?
// Пример решённого первого задания:
fun cif1() {
    while (freeFromRight) {
        right()
        if(wallFromUp) paint()
    }
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "cif1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun cif2() {
    while (freeFromRight) {
        right()
        if(wallFromUp || wallFromDown) paint()
    }
}
// Продолжайте решать задачи до номера cif22 включительно
fun cif3() {
    while (freeFromRight) {
        right()
        if(wallFromUp && wallFromDown) paint()
    }
}

fun cif4() {
    while (freeFromRight) {
        right()
        if(wallFromUp && freeFromDown) paint()
    }
}

fun cif5() {
    while (freeFromRight) {
        right()
        if(freeFromUp || wallFromDown) paint()
    }
}

fun cif6() {
    while (freeFromRight) {
        right()
        if(freeFromUp || freeFromDown) paint()
    }
}

fun cif7() {
    while (freeFromRight) {
        right()
        if(freeFromUp) {
            up()
            paint()
            down()
        }
        if(freeFromDown) {
            down()
            paint()
            up()
        }
    }
}

fun cif8() {
    while (freeFromRight) {
        right()
        if(freeFromUp && wallFromDown) {
            up()
            paint()
            down()
        }
        if(freeFromDown && wallFromUp) {
            down()
            paint()
            up()
        }
    }
}

fun cif9() {
    while (freeFromRight) {
        right()
        if(wallFromUp && wallFromDown)
            paint()
        else if(freeFromUp && wallFromDown) {
            up()
            paint()
            down()
        }
        else if(freeFromDown && wallFromUp) {
            down()
            paint()
            up()
        }
    }
}

fun cif10() {
    while (freeFromRight) {
        right()
        if(freeFromDown && cellIsPainted) {
            down()
            paint()
            up()
        }
    }
}

fun cif11() {
    while (freeFromRight) {
        right()
        if(freeFromDown && cellIsPainted && wallFromUp) {
            down()
            paint()
            up()
        }
    }
}

fun cif12() {
    while (freeFromRight) {
        right()
        if(freeFromUp && (cellIsPainted || freeFromDown)) {
            up()
            paint()
            down()
        }
    }
}

fun cif13() {
    while (freeFromRight) {
        right()
        if(freeFromUp && (cellIsPainted && wallFromDown || cellIsFree && freeFromDown)) {
            up()
            paint()
            down()
        }
    }
}

fun cif14() {
    while (freeFromRight) {
        right()
        if(freeFromDown) {
            down()
            if(cellIsPainted) {
                down()
                paint()
                up()
            }
            up()
        }
    }
}

fun cif15() {
    while (freeFromRight) {
        right()
        up()
        val u = cellIsPainted
        down()
        down()
        val d = cellIsPainted
        up()
        if (u && d) paint()
    }
}

fun cif16() {
    if(freeFromUp && freeFromLeft) {
        while (freeFromUp) up()
        while (freeFromLeft) left()
    } else if(freeFromUp && freeFromRight) {
        while (freeFromUp) up()
        while (freeFromRight) right()
    } else if(freeFromDown && freeFromLeft) {
        while (freeFromDown) down()
        while (freeFromLeft) left()
    } else {
        while (freeFromDown) down()
        while (freeFromRight) right()
    }
}

fun cif17() {
    while (freeFromUp) up()
    if (freeFromLeft)
        while (freeFromLeft) left()
    else
        while (freeFromRight) right()
}