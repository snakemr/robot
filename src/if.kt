// Нажммите зелёную ► слева от этой строки и выберите "Run 'IfKt'"
fun main() = robot("if1", 4)    // Зелёным цветом выделен номер текущего задания
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
// Пример ___частично___ решённого первого задания:
fun if1() {
    right()
    if(freeFromUp) {
        up()
        right()
        down()
    } else {
        // ветку "иначе" допишите сами
    }
    paint()
}
// Нажммите зелёную ► (или Shift+F10) ещё раз, и убедитесь, что задача решена верно.

// Поменяйте номер задания в зелёной строке "if1" (вторая строка этого текста)
// Нажммите зелёную ►, прочитайте и решите аналогичным способом следующее задание:
fun if2() {

}

// Продолжайте решать задачи до номера if11 включительно
