// Нажммите зелёную ► слева от этой строки и выберите "Run 'CcKt'"
fun main() = robot("cc1", 5)    // Зелёным цветом выделен номер текущего задания
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

}

// Продолжайте решать задачи до номера cc19 включительно
