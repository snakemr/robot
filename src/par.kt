// Нажммите зелёную ► слева от этой строки и выберите "Run 'ParKt'"
fun main() = robot("par1", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun parN() { решение }, где N - номер задания
// Для выполнения повторяющихся действий создавайте свои собственные функции с параметрами:
// fun имяФункции(параметр1: ТипПараметра, параметр2: ТипПараметра) { ... }
// и вызывайте их из основной программы: имяФункции(1, 2)
// Пример ___частично___ решённого первого задания:
fun left(n: Int) {  // функция left( n ) не запрещает дальнейшее использование обычной функции left()
    repeat(n) { left() }
}
fun right(n: Int) {  // функция right( n ) тоже отличается от обычной right() числом параметров
    // напишите эту функцию сами
}
fun rightPaintLeftDown(n: Int) {
    // напишите сами: вправо на n, закраска, влево на n, вниз на 1
}
fun par1() {
    rightPaintLeftDown(6)
    rightPaintLeftDown(8)
    rightPaintLeftDown(4)
    // дальше аналогично
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "par1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:

fun paintDownUp(n: Int) {
    // вниз с закраской, потом вверх
}

fun par2() {
    paintDownUp(6)
    right(2)
    // дальше аналогично
}

// Продолжайте решать задачи до номера par8 включительно

fun goMaze(d: Int, r: Int, u: Int, l: Int) {
    // вниз, вправо, вверх и влево
}
