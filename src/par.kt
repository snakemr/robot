// Нажммите зелёную ► слева от этой строки и выберите "Run 'ParKt'"
fun main() = robot("par8", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun parN() { решение }, где N - номер задания
// Для выполнения повторяющихся действий создавайте свои собственные функции с параметрами:
// fun имяФункции(параметр1: ТипПараметра, параметр2: ТипПараметра) { ... }
// и вызывайте их из основной программы: имяФункции(1, 2)
// Пример решённого первого задания:
fun left(n: Int) {  // функция left( n ) не запрещает дальнейшее использование обычной функции left()
    repeat(n) { left() }
}
fun right(n: Int) {  // функция right( n ) тоже отличается от обычной right() числом параметров
    repeat(n) { right() }
}
fun rightPaintLeftDown(n: Int) {
    right(n);    paint();   left(n);   down()
}
fun par1() {
    rightPaintLeftDown(6)
    rightPaintLeftDown(8)
    rightPaintLeftDown(4)
    rightPaintLeftDown(3)
    rightPaintLeftDown(10)
    rightPaintLeftDown(1)
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "par1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun paintDownUp(n: Int) {
    repeat(n) {
        paint()
        down()
    }
    repeat(n) {
        up()
    }
}

fun par2() {
    paintDownUp(6)
    right(2)
    paintDownUp(3)
    right(2)
    paintDownUp(8)
    right(2)
    paintDownUp(5)
    right(2)
    paintDownUp(7)
}
// Продолжайте решать задачи до номера par8 включительно
fun goMaze(d: Int, r: Int, u: Int, l: Int) {
    repeat(d) { down() }
    repeat(r) { right() }
    repeat(u) { up() }
    repeat(l) { left() }
}

fun par3() {
    goMaze(8,8,8,7)
    goMaze(7,6,6,5)
    goMaze(5,4,4,3)
    goMaze(3,2,2,1)
    goMaze(1,0,0,0)
}

fun painT(x: Int, y: Int) {
    repeat(x) { left() }
    repeat(x*2) {
        paint()
        right()
    }
    paint()
    repeat(x) { left() }
    repeat(y) {
        paint()
        down()
    }
    paint()
    repeat(y) { up() }
}

fun par4() {
    painT(3,3)
    right(7)
    painT(2,4)
    right(5)
    painT(1,5)
}

fun box(x: Int, y: Int) {
    repeat(y) {
        repeat(x) {
            paint()
            right()
        }
        repeat(x) { left() }
        down()
    }
    repeat(y) { up() }
}

fun par5() {
    box(5,8)
}

fun down(n: Int) {
    repeat(n) { down() }
}

fun par6() {
    box(5,4)
    right(7);   up()
    box(2, 7)
    right(3)
    box(4, 7)
    down(8);    left(2)
    box(8, 3)
    left(7); up(); up()
    box(5, 5)
}

fun perimeter(x: Int, y: Int) {
    repeat(x) {
        paint()
        right()
    }
    repeat(y) {
        paint()
        down()
    }
    repeat(x) {
        paint()
        left()
    }
    repeat(y) {
        paint()
        up()
    }
}

fun up(n: Int) {
    repeat(n) { up() }
}

fun par7() {
    perimeter(4,3)
    down(6);    left()
    perimeter(3,2)
    right(7);  up(5)
    perimeter(3,4)
}

fun paintH(x: Int, y: Int, z: Int) {
    repeat(y-1) {
        paint()
        down()
    }
    paint()
    repeat(z) { up() }
    repeat(x-1) {
        paint()
        right()
    }
    repeat(z) { down() }
    repeat(y-1) {
        paint()
        up()
    }
    paint()
    repeat(x-1) { left() }
}

fun par8() {
    paintH(4,6,3)
    right(5);   down()
    paintH(5,5,1)
    right(6);   up(2)
    paintH(3,7,5)
}