import java.awt.EventQueue
import java.lang.Thread.sleep
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

lateinit var frame: RobotWindow

fun task(taskName: String) {
    if (::frame.isInitialized) {
        frame.restartTask()
        return
    }
    EventQueue.invokeLater {
        frame = RobotWindow(taskName)
        frame.isVisible = true
    }
    while (!::frame.isInitialized) sleep(1)
}

fun right() = frame.robot.doMove(1)
fun left() = frame.robot.doMove(-1)
fun down() = frame.robot.doMove(dy = 1)
fun up() = frame.robot.doMove(dy = -1)
fun paint() = frame.robot.doPaint()

fun wallFromLeft() = frame.robot.checkState(CellState.WallFromLeft)
fun wallFromRight() = frame.robot.checkState(CellState.WallFromRight)
fun wallFromUp() = frame.robot.checkState(CellState.WallFromUp)
fun wallFromDown() = frame.robot.checkState(CellState.WallFromDown)
fun freeFromLeft() = !frame.robot.checkState(CellState.WallFromLeft, true)
fun freeFromRight() = !frame.robot.checkState(CellState.WallFromRight, true)
fun freeFromUp() = !frame.robot.checkState(CellState.WallFromUp, true)
fun freeFromDown() = !frame.robot.checkState(CellState.WallFromDown, true)
fun cellIsPainted() = frame.robot.checkState(CellState.IsGreen)
fun cellIsFree() = !frame.robot.checkState(CellState.IsGreen, true)

val wallFromLeft: Boolean
    get() = wallFromLeft()
val wallFromRight: Boolean
    get() = wallFromRight()
val wallFromUp: Boolean
    get() = wallFromUp()
val wallFromDown: Boolean
    get() = wallFromDown()
val freeFromLeft: Boolean
    get() = freeFromLeft()
val freeFromRight: Boolean
    get() = freeFromRight()
val freeFromUp: Boolean
    get() = freeFromUp()
val freeFromDown: Boolean
    get() = freeFromDown()
val cellIsPainted: Boolean
    get() = cellIsPainted()
val cellIsFree: Boolean
    get() = cellIsFree()

var speed: Int
        get() = if (::frame.isInitialized) frame.speed else 3
        set(value) {
            if (::frame.isInitialized && value in 1..5) {
                frame.speedSlider.value = value
                frame.speed = value
            }
        }

fun main() {
    repeat(100) {
        task("c1")
        repeat(10) {
            right()
        }
        frame.robot.check()
    }
}

fun getFunctionFromFile(fileName: String, funcName: String): KFunction<*>? {
    val selfRef = ::getFunctionFromFile
    val currentClass = selfRef.javaMethod!!.declaringClass
    val classDefiningFunctions = try {
        currentClass.classLoader.loadClass("${fileName}Kt")
    }
    catch (e: ClassNotFoundException) {
        frame.error("ОШИБКА: В вашем проекте нет файла «${fileName.toLowerCase()}.kt» с решением задачи ${frame.taskName}")
        currentClass
    }
    val javaMethod  = classDefiningFunctions.methods.find { it.name == funcName && Modifier.isStatic(it.modifiers)}
    return javaMethod?.kotlinFunction
}

fun robot(taskname: String, speed: Int = 3) {
    task(taskname.toLowerCase())
    frame.speed = speed
    frame.speedSlider.value = speed
    val module = taskname.toUpperCase().filter { it.isLetter() }
    val kFunction = getFunctionFromFile(module, taskname.toLowerCase())
    kFunction?.call() ?: frame.error("ОШИБКА: В файле «${module.toLowerCase()}.kt» не определена функция-решение: fun $taskname() {...}")
    while (true) {
        val result = frame.robot.check()
        println(result)
        if (result && frame.tries++ < frame.maxTries)
            frame.robot.paused = false
        task(taskname)
        kFunction?.call()
    }
}
