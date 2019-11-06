// Нажммите зелёную ► слева от этой строки и выберите "Run 'cifKt'"
fun main() = robot("cif4", 5)    // Зелёным цветом выделен номер текущего задания
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