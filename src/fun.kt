// Нажммите зелёную ► слева от этой строки и выберите "Run 'FunKt'"
fun main() = robot("fun1", 5)    // Зелёным цветом выделен номер текущего задания
// Прочитайте задание. Напишите решение в виде функции: fun funN() { решение }, где N - номер задания
// Для выполнения повторяющихся действий создавайте свои собственные функции:
// fun имяФункции() { ... }
// и вызывайте их из основной программы: имяФункции()
// Пример ___частично___ решённого первого задания:
fun cross() {
    paint()
    up();       paint();    down()
    right();    paint();    left()
    // дальше допишите сами
}

fun fun1() {
    cross()
    repeat(5) { right() }
    up()
    cross()
    // дальше допишите сами
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "fun1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun box4() {
    // рисование "коробки" 2х2 здесь
}

fun fun2() {
    // основная программа здесь
}

// Продолжайте решать задачи до номера fun15 включительно
