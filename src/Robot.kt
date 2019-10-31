import java.awt.Color
import java.awt.Image
import java.awt.Rectangle
import java.lang.Integer.max
import java.lang.Thread.sleep
import javax.swing.ImageIcon
import javax.swing.JLabel
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class Robot(val window: RobotWindow, var x: Int, var y: Int, val endX: Int, val endY: Int) {
    private lateinit var image: JLabel
    private lateinit var blown: JLabel
    private lateinit var chrgr: JLabel
    private var broken:  Boolean = false
    var stopped: Boolean = false
    var paused: Boolean = false
    var checked: Boolean = false
    var tried: Boolean = false
    var disposed: Boolean = false
    private var count = 0

    fun prepare(cellSize: Int) {
        val ico = ImageIcon("res/robot.png")
        ico.image = ico.image.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)
        image = JLabel(ico)
        image.setBounds(0, 0, cellSize, cellSize)

        val ico2 = ImageIcon("res/blow.gif")
        blown = JLabel(ico2)
        val b = blown.preferredSize
        blown.setBounds(0, 0, b.width, b.height)
        blown.isVisible = false

        val ico3 = ImageIcon("res/charger.png")
        ico3.image = ico3.image.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)
        chrgr = JLabel(ico3)
        chrgr.setBounds(0, 0, cellSize, cellSize)

        window.contentPane.layout = null
        window.contentPane.add(blown)
        window.contentPane.add(image)
        window.contentPane.add(chrgr)
    }

    fun set(X: Int = x, Y: Int = y, animate: Boolean = false) {
        x = X
        y = Y
        val cell = window.views[y][x]
        if (animate)
            fixedRateTimer(period = 5){
                val b = image.bounds
                if (b.x < cell.x) b.x++ else if (b.x > cell.x) b.x--
                if (b.y < cell.y) b.y++ else if (b.y > cell.y) b.y--
                image.setBounds(b.x, b.y, b.width, b.height)
                if (b.x == cell.x && b.y == cell.y) this.cancel()
            }
        else
            image.setBounds(cell.x, cell.y, image.bounds.width, image.bounds.height)
        val cell2 = window.views[endY][endX]
        chrgr.setBounds(cell2.x, cell2.y, chrgr.bounds.width, chrgr.bounds.height)
    }

    fun doMove(dx: Int = 0, dy: Int = 0) {
        println("$x $y $dx $dy")
        tried = true
        if (stopped) return
        while (paused) sleep(1)
        val mask = window.field[y][x]
        broken = dx>0 && (mask and CellState.WallFromRight.mask != 0) ||
                 dx<0 && (mask and CellState.WallFromLeft.mask != 0) ||
                 dy>0 && (mask and CellState.WallFromDown.mask != 0) ||
                 dy<0 && (mask and CellState.WallFromUp.mask != 0)
        val b = image.bounds
        val cell = if (broken) {
            val cur = window.views[y][x].bounds
            cur.x = cur.x + dx * cur.width / 3
            cur.y = cur.y + dy * cur.height / 3
            Rectangle(cur.x, cur.y, cur.width, cur.height)
        } else {
            x += dx
            y += dy
            window.views[y][x].bounds
        }
        count++
        window.report("Шаг $count" + (if(window.maxCycles>0) " из ${window.maxCycles}" else "") +
            ". Робот идёт " + when{
            dx>0 -> "вправо"
            dx<0 -> "влево"
            dy>0 -> "вниз"
            dy<0 -> "вверх"
            else -> ""
        })
        while (b.x != cell.x || b.y != cell.y) {
            if (stopped) return
            while (paused) sleep(1)
            sleep(6L-window.speed)
            if (b.x < cell.x) b.x++ else if (b.x > cell.x) b.x--
            if (b.y < cell.y) b.y++ else if (b.y > cell.y) b.y--
            image.setBounds(b.x, b.y, b.width, b.height)
        }
        if (broken) {
            val bl = blown.preferredSize
            blown.setBounds(b.x - (bl.width-b.width)/2, b.y - (bl.height-b.height), bl.width, bl.height)
            blown.isVisible = true
            stopped = true
            window.error("Робот врезался в " + when{
                dx>0 -> "правую"
                dx<0 -> "левую"
                dy>0 -> "нижнюю"
                dy<0 -> "верхнюю"
                else -> ""
            } + " стену!")
        }
    }

    fun doPaint() {
        tried = true
        if (stopped) return
        while (paused) sleep(1)
        var mask = window.field[y][x]
        val view = window.views[y][x]
        count++
        //window.report("Шаг $count. Робот красит клетку")
        if (mask and CellState.IsYellow.mask != 0) {
            if (mask or CellState.IsGreen.mask != 0)
                view.background = Color(view.background.red, max(view.background.green-32, 64), 0)
            mask = mask or CellState.IsGreen.mask
            var r = 255
            fixedRateTimer(period = 6L-window.speed){
                view.background = Color(--r, view.background.green, 0)
                window.repaint()
                if (r==0 || disposed) cancel()
            }
        } else {
            mask = mask or CellState.IsGreen.mask or CellState.IsRed.mask
            var gb = 255
            fixedRateTimer(period = 6L-window.speed){
                gb--
                view.background = Color(255, gb, gb)
                window.repaint()
                if (gb==0 || disposed) cancel()
            }
        }
        window.field[y][x] = mask
    }

    fun checkState(cellState: CellState, ifStopped: Boolean = false): Boolean {
        if (stopped) return ifStopped
        tried = true
        return if (cellState.mask and CellState.maskCanCheck.mask == 0) false
        else window.field[y][x] and cellState.mask != 0
    }

    fun check(): Boolean {
        println("$x $y")
        checked = true
        paused = true
        if (stopped && !broken || !tried) return false
        var result = !broken
        if (x!=endX || y!=endY) {
            result = false
            window.error("Робот не добрался до своего зарядного устройства!")
            fixedRateTimer(period = 300) {
                chrgr.isVisible = !chrgr.isVisible
                if (!broken) image.isVisible = !chrgr.isVisible
                if (disposed) cancel()
            }
        }

        var unpainted = 0
        var unwanted = 0
        for ((y, row) in window.field.withIndex())
            for ((x, mask) in row.withIndex())
                if (mask and CellState.IsYellow.mask != 0 && mask and CellState.IsGreen.mask == 0) {
                    window.views[y][x].background = Color(255, 254, 0)
                    unpainted++
                } else if (mask and CellState.IsYellow.mask == 0 && mask and CellState.IsGreen.mask != 0) {
                    unwanted++
                }
        if (unpainted>0 || unwanted>0) {
            result = false
            window.error((if (unpainted>0) "Робот не смог закрасить $unpainted ${rusCells(unpainted)}. " else "")+
                        if (unwanted>0) "Робот ошибочно закрасил $unwanted ${rusCells(unwanted)}." else "" )
            if (unpainted>0) fixedRateTimer(period = 10) {
                window.views.map { it.map {
                    val c = it.background
                    if(c.red==255 && c.green in 1..254) it.background = Color(255, c.green-1, 0)
                } }
                window.repaint()
                if (disposed) cancel()
            }
        }
        if (result) window.report((if (window.tries>1) "Мы проверили ${window.tries} ${rusTries(window.tries)}. " else "") +
            "Задание выполнено " +
            if (window.maxCycles>0 && count>window.maxCycles)
                "почти хорошо, <font color=orange>но сделано ${count-window.maxCycles} ${rusSteps(count-window.maxCycles)}. Может, получится сократить программу?</font>"
            else when (Random.nextInt(3)) {
                0 -> "блестяще"
                1 -> "идеально"
                2 -> "мастерски"
                3 -> "абсолютно верно"
                else -> ""
            } + " в $count ${rusCounts(count)}."
            , "green")
        return result
    }

    fun rusCells(n: Int) = when {
        n%10 == 1 && n/10%10 != 1 -> "клетку"
        n%10 in 2..4 && n/10%10 != 1 -> "клетки"
        else -> "клеток"
    }

    fun rusSteps(n: Int) = when {
        n%10 == 1 && n/10%10 != 1 -> "лишнее действие"
        n%10 in 2..4 && n/10%10 != 1 -> "лишних действия"
        else -> "лишних действий"
    }

    fun rusCounts(n: Int) = when {
        n%10 == 1 && n/10%10 != 1 -> "ход"
        n%10 in 2..4 && n/10%10 != 1 -> "хода"
        else -> "ходов"
    }

    fun rusTries(n: Int) = when {
        n%10 == 1 && n/10%10 != 1 -> "раз"
        n%10 in 2..4 && n/10%10 != 1 -> "раза"
        else -> "раз"
    }
}