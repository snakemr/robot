import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.lang.Thread.sleep
import javax.swing.*
import kotlin.math.max
import kotlin.math.min


enum class CellState(val mask: Int) {
    WallFromUp      (0b0001),
    WallFromLeft    (0b0010),
    WallFromDown    (0b0100),
    WallFromRight   (0b1000),
    IsGreen     (0b0010000),
    IsYellow    (0b0100000),
    IsRed       (0b1000000),
    maskPaint   (0b1110000),
    maskWalls   (0b0001111),
    maskCanCheck(0b0011111)
}

const val WALLWIDTH = 3
const val CELLMINSIZE = 36

class RobotWindow(taskName: String) : JFrame()  {
    lateinit var field: Array<IntArray>
    lateinit var views:  List<List<JLabel>>
    lateinit var description: String
    lateinit var desc: JLabel
    lateinit var speedSlider: JSlider
    lateinit var pauseButton: JButton
    lateinit var restartButton: JButton
    lateinit var resetButton: JButton
    lateinit var closeButton: JButton
    lateinit var pauseAction: ActionListener
    lateinit var restartAction: ActionListener
    lateinit var resetAction: ActionListener
    lateinit var closeAction: ActionListener
    lateinit var robot: Robot
    val taskName: String
    var fieldWidth: Int = 0
    var fieldHeight: Int = 0
    var maxx: Int = 0
    var maxy: Int = 0
    var maxCycles: Int = 0
    var maxTries: Int = 1
    var tries: Int = 1
    var speed: Int = 3
    private var errorString: String? = null
    private var allSet = false

    init {
        title = "Исполнитель РОБОТ для языка Kotlin. Автор заданий: С.С.Михалкович (2004). Реализация: А.А.Светличный (2019). Задание «$taskName». " +
                when (taskName.filter { it.isLetter() }) {
                    "c"     -> "Циклы со счётчиком: for, repeat"
                    "w"     -> "Цикл с условием while"
                    "cc"    -> "Вложенные циклы"
                    "if"    -> "Оператор ветвления"
                    "cif"   -> "Ветвления и циклы"
                    "cnt"   -> "Использование счётчиков"
                    "fun"   -> "Использование функций"
                    "par"   -> "Функции с параметрами"
                    else    -> ""
                }
        this.taskName = taskName
        setActions()
        getTask(taskName)
        prepareField()
        defaultCloseOperation = EXIT_ON_CLOSE
        //setSize(650, 500)
        setLocationRelativeTo(null)
    }

    fun restartTask() {
        robot.stopped = true
        while (!robot.checked || robot.paused && allSet) sleep(1)
        robot.disposed = true
        errorString = null
        contentPane.removeAll()
        allSet = false
        EventQueue.invokeLater {
            getTask()
            prepareField()
        }
        while(!allSet) sleep(1)
    }

    fun setActions() {
        setActionKey("CANCEL_ACTION_KEY", KeyEvent.VK_ESCAPE, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                System.exit(0)
            }
        } )
        setActionKey("RESTART_ACTION_KEY", KeyEvent.VK_ENTER, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                restartButton.doClick()
            }
        } )
        setActionKey("RESET_ACTION_KEY", KeyEvent.VK_BACK_SPACE, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                resetButton.doClick()
            }
        } )
        setActionKey("PAUSE_ACTION_KEY", KeyEvent.VK_SPACE, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                pauseButton.doClick()
            }
        } )
        pauseAction = ActionListener() {
            robot.paused = !robot.paused
        }
        restartAction = ActionListener() {
            robot.paused = false
            robot.stopped = true
            while (!robot.checked) sleep(1)
            robot.paused = false
        }
        resetAction = ActionListener() {
            robot.stopped = true
            allSet = false
            if (robot.paused) {
                robot.paused = false
                while (!robot.checked) sleep(1)
            }
            robot.paused = true
        }
        closeAction = ActionListener() {
            System.exit(0)
        }
    }

    fun setActionKey(actionKey: String, keyCode: Int, action: Action) {
        val noModifiers = 0
        val inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        val key = KeyStroke.getKeyStroke(keyCode, noModifiers, false)
        inputMap.put(key, actionKey)
        rootPane.actionMap.put(actionKey, action)
    }

    fun checkCoordX(x: Int) = if (x<0) 0 else if (x>=fieldWidth) fieldWidth-1 else x
    fun checkCoordY(y: Int) = if (y<0) 0 else if (y>=fieldHeight) fieldHeight-1 else y

    fun wallH(y: Int, x1: Int, x2: Int) {
        val rangeH = 1..fieldHeight
        for (i in checkCoordX(x1)..checkCoordX(x2)) {
            if (y in rangeH) field[y-1][i] = field[y-1][i] or CellState.WallFromDown.mask
            if (y+1 in rangeH) field[y][i] = field[y][i] or CellState.WallFromUp.mask
        }
    }

    fun wallV(x: Int, y1: Int, y2: Int) {
        val rangeW = 1..fieldWidth
        for (i in checkCoordY(y1)..checkCoordY(y2)) {
            if (x in rangeW) field[i][x-1] = field[i][x-1] or CellState.WallFromRight.mask
            if (x+1 in rangeW) field[i][x] = field[i][x] or CellState.WallFromLeft.mask
        }
    }

    fun unWallH(y: Int, x1: Int, x2: Int) {
        val rangeH = 1..fieldHeight
        for (i in checkCoordX(x1)..checkCoordX(x2)) {
            if (y in rangeH) field[y-1][i] = field[y-1][i] and CellState.WallFromDown.mask.inv()
            if (y+1 in rangeH) field[y][i] = field[y][i] and CellState.WallFromUp.mask.inv()
        }
    }

    fun unWallV(x: Int, y1: Int, y2: Int) {
        val rangeW = 1..fieldWidth
        for (i in checkCoordY(y1)..checkCoordY(y2)) {
            if (x in rangeW) field[i][x-1] = field[i][x-1] and CellState.WallFromRight.mask.inv()
            if (x+1 in rangeW) field[i][x] = field[i][x] and CellState.WallFromLeft.mask.inv()
        }
    }

    fun doPaint(x: Int, y: Int, needToPaint: Boolean = false) {
        val cx = checkCoordX(x-1)
        val cy = checkCoordY(y-1)
        field[cy][cx] = field[cy][cx] or if (needToPaint) CellState.IsYellow.mask else CellState.IsGreen.mask or CellState.IsYellow.mask
    }

    fun doPaints(x: Int, y: Int) {
        val cx = checkCoordX(x-1)
        val cy = checkCoordY(y-1)
        field[cy][cx] = field[cy][cx] or CellState.IsYellow.mask or CellState.IsGreen.mask
    }

    fun doCross(x: Int, y: Int, needToPaint: Boolean = false) {
        doPaint(x,y,needToPaint);
        doPaint(x+1, y, needToPaint);
        doPaint(x-1, y, needToPaint);
        doPaint(x,y+1, needToPaint);
        doPaint(x,y-1, needToPaint);
    }

    fun doBox(x: Int, y: Int, needToPaint: Boolean = false) {
        doPaint(x, y, needToPaint);
        doPaint(x+1, y, needToPaint);
        doPaint(x,y+1, needToPaint);
        doPaint(x+1,y+1, needToPaint);
    }

    fun setField(fieldWidth: Int, fieldHeight: Int, startX: Int, startY: Int, endX: Int, endY: Int) {
        this.fieldWidth = fieldWidth
        this.fieldHeight = fieldHeight
        maxx = fieldWidth - 1
        maxy = fieldHeight - 1
        field = Array(fieldHeight, { IntArray(fieldWidth) })
        val paused = if (::robot.isInitialized) robot.paused else false
        robot = Robot(this, startX-1, startY-1, endX-1, endY-1)
        robot.paused = paused
        wallH(0, 0, fieldWidth - 1)
        wallH(fieldHeight, 0, fieldWidth - 1)
        wallV(0, 0, fieldHeight - 1)
        wallV(fieldWidth, 0, fieldHeight - 1)
        /*for (r in field) {
            for (c in r) print("%3d".format(c))
            println()
        }*/
    }

    private fun createFieldLayout() {
        val gl = GroupLayout(contentPane)
        contentPane.layout = gl
        gl.autoCreateContainerGaps = true

        //val loweredbevel = BorderFactory.createLoweredBevelBorder()
        //val raisedbevel = BorderFactory.createRaisedBevelBorder()
        desc = JLabel("<html>"+description+"<br> </html>", null, SwingConstants.LEADING).apply {
                border = BorderFactory.createLineBorder(Color.WHITE, 5)
                font = Font(null, 0, 17)
                isOpaque = true
                if (errorString != null) {
                    text = "<html>" + description + "<br><font color=red>"+ errorString + "</font></html>"
                }
        }

        val tn = taskName
        pauseButton = JButton("Пауза / продолжить (Пробел)").apply { addActionListener(pauseAction) }
        restartButton = JButton("Перезапуск (Enter)").apply { addActionListener(restartAction) }
        resetButton = JButton("На начало (Backspace ←)").apply { addActionListener(resetAction) }
        closeButton = JButton("Закрыть (Esc)").apply { addActionListener(closeAction) }
        speedSlider = JSlider(1,5,3).apply {
            preferredSize = Dimension(100,16)
            snapToTicks = true
            paintTicks = true
            minorTickSpacing = 1
            majorTickSpacing = 2
            value = speed
            addChangeListener { speed = this.value }
        }

        val pg = gl.createParallelGroup()
        for (r in views) {
            val sg = gl.createSequentialGroup()
            for (c in r) {
                sg.addComponent(c)
            }
            pg.addGroup(sg)
        }
        pg.addComponent(desc)
        pg.addGroup(gl.createSequentialGroup()
            .addComponent(pauseButton).addGap(10)
            .addComponent(restartButton).addGap(10)
            .addComponent(resetButton).addGap(10)
            .addComponent(speedSlider).addGap(10)
            .addComponent(closeButton)
        )
        gl.setHorizontalGroup(pg)

        val sg = gl.createSequentialGroup()
        for (r in views) {
            val pg = gl.createParallelGroup()
            for (c in r) {
                pg.addComponent(c)
            }
            sg.addGroup(pg)
        }
        sg.addComponent(desc)
        sg.addGroup(gl.createParallelGroup()
            .addComponent(pauseButton)
            .addComponent(restartButton)
            .addComponent(resetButton)
            .addComponent(speedSlider)
            .addComponent(closeButton)
        )
        gl.setVerticalGroup(sg)

        pack()
        pauseButton.requestFocus()
    }

    fun prepareField() {
        val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
        val winWidth = screenSize.width * 9 / 10
        val winHeight = screenSize.height * 8 / 10
        val cellSize = max(CELLMINSIZE, min(winWidth/fieldWidth, winHeight/fieldHeight))

        val loweredbevel = BorderFactory.createLoweredBevelBorder()
        views = field.map { it.map {
            JLabel(null, null, SwingConstants.LEADING).apply {
                minimumSize = Dimension(cellSize, cellSize)
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                        if ((it and CellState.WallFromUp.mask) != 0) WALLWIDTH else 0,
                        if ((it and CellState.WallFromLeft.mask) != 0) WALLWIDTH else 0,
                        if ((it and CellState.WallFromDown.mask) != 0) WALLWIDTH else 0,
                        if ((it and CellState.WallFromRight.mask) != 0) WALLWIDTH else 0,
                        Color.DARK_GRAY), loweredbevel)
                if ((it and CellState.IsGreen.mask) != 0) background = Color.GREEN
                else if ((it and CellState.IsYellow.mask) != 0) background = Color.YELLOW
                isOpaque = true
            }
        } }
        robot.prepare(cellSize)
        createFieldLayout()
        robot.set()
        allSet = true
    }

    fun error(ErrorString: String) {
        if (errorString != null) return;
        errorString = ErrorString
        if (::desc.isInitialized){
            //val h1 = desc.preferredSize.height
            //val b = size
            desc.text = "<html>" + description + "<br><font color=red>"+ ErrorString + "</font></html>"
            //b.height += desc.preferredSize.height - h1
            //size = b
            //desc.maximumSize = Dimension(contentPane.width-insets.right-insets.left, 100)
            //pack()
        }
    }

    fun report(progress: String, color: String = "blue") {
        if (errorString != null || !::desc.isInitialized) return;
        desc.text = "<html>" + description + "<br><font color=$color>"+ progress + "</font></html>"
    }

    fun getTask(taskName: String = this.taskName) {
        when (taskName) {
            "c1" -> {
                setField(11,1,1,1,11,1)
                description = "Используйте команду <b>right()</b> в цикле <b>for(i in 1..10)</b> или <b>repeat(10){...}</b>, чтобы робот прошёл в конец коридора и смог благополучно зарядить батарею"
                maxCycles=10
            }
            "c2" -> {
                setField(11, 1, 1, 1, 11, 1)
                for(i in 1 until fieldWidth) doPaint(i, 1, true)
                description = "Используйте команды <b>paint()</b> и <b>right()</b> в блоке <b>for</b> или <b>repeat {...}</b> для перекраски жёлтых клеток в зелёный цвет"
                maxCycles = 20
            }
            "c3" -> {
                setField(11, 1, 1, 1, 11, 1)
                for(i in 2..fieldWidth) doPaint(i, 1, true)
                description = "Используйте команды <b>right()</b> и <b>paint()</b> в блоке <b>for</b> или <b>repeat {...}</b> для перекраски жёлтых клеток в зелёный цвет"
                maxCycles = 20
            }
            "c4" -> {
                setField(11, 1, 1, 1, 11, 1)
                for(i in 1..fieldWidth) doPaint(i, 1, true)
                description = "Используйте команды <b>right()</b> и <b>paint()</b> в блоке <b>for</b> или <b>repeat {...}</b> для перекраски жёлтых клеток в зелёный цвет"
                maxCycles = 21
            }
            "c5" -> {
                setField(11, 11, 1, 1, 11, 11)
                for (i in 1..maxx) doPaint(i, i,true)
                description = "Используйте команды <b>right()</b>, <b>down()</b> и <b>paint()</b> в блоке <b>for</b> или <b>repeat</b>"
                maxCycles = 30
            }
            "c6" -> {
                setField(17, 1, 17, 1, 17, 1)
                for (i in 2..maxx step 2) doPaint(i, 1, true)
                description = "Робот должен закрасить каждую вторую клетку, а затем вернуться на место"
                maxCycles = 40
            }
            "c7" -> {
                setField(11,2,11,1,1,1)
                for(i in 2..fieldWidth){ doPaint(i,1,true); doPaint(i,2,true) }
                for(i in 1 until 6){ wallV(i*2,0,0); wallV(i*2-1,1,1) }
                description = "Используйте цикл <b>for</b> или блок <b>repeat</b> для прохождения повторяющихся участков"
                maxCycles = 40
            }
            "c8" -> {
                setField(11,2,11,1,1,1)
                for(i in 1..fieldWidth) doPaint(i,1,true)
                for(i in 1..maxx) wallV(i,0,0)
                description = "Робот должен закрасить отмеченные клетки"
                maxCycles = 41
            }
            "c9" -> {
                setField(11,2,1,2,11,2)
                for(i in 1 until 6){ doPaint(i*2,1,true); doPaint(i*2-1,2,true) }
                description = "Используйте цикл для прохождения повторяющихся участков"
                maxCycles = 30
            }
            "c10" -> {
                setField(11,2,1,1,1,2)
                for(i in 1..maxx){ doPaint(i,1,true); doPaint(i,2,true) }
                wallH(1,0,maxx-1)
                description = "Без подсказки"
                maxCycles = 41
            }
            "c11" -> {
                setField(11,2,11,1,11,2)
                for(i in 1..fieldWidth){ doPaint(i,1,true); doPaint(i,2,true) }
                wallH(1,1, maxx)
                description = "Без подсказки"
                maxCycles = 43
            }
            "c12" -> {
                setField(11,1,6,1,6,1)
                for(i in 1..fieldWidth) doPaint(i,1,true)
                description = "Робот должен закрасить весь коридор и вернуться на место"
                maxCycles = 31
            }
            "c13" -> {
                setField(9,9,9,1,9,1)
                for(i in 1..fieldWidth){ doPaint(i,1,true); doPaint(i,fieldHeight,true) }
                for(i in 2..maxy){ doPaint(1,i,true); doPaint(fieldWidth,i,true) }
                description = "Теперь робот должен закрасить весь периметр"
                maxCycles = 64
            }
            "c14" -> {
                setField(9,9,1,5,1,5)
                for(i in 1..5){ doPaint(i,6-i,true); doPaint(i,4+i,true); doPaint(10-i,6-i,true); doPaint(10-i,4+i,true) }
                description = "И вновь движение по диагонали"
                maxCycles = 48
            }
            "c15" -> {
                setField(9,9,5,5,5,5)
                for(i in 1..fieldWidth){ doPaint(i,5,true); doPaint(5,i,true) }
                description = "Робот должен закрасить вертикаль и горизонталь и вернуться в центр"
                maxCycles = 50
            }
            "c16" -> {
                setField(12,3,2,2,2+1,2)
                for(i in 2..maxx) doPaint(i,2,true)
                for(i in 1..maxx) wallV(i,1,1)
                for(i in 1 until maxx) wallH(1+i%2,i,i)
                description = "Последнее задание - закрепляем навык использования цикла for"
            }
            "w1" -> {
                val i = (5..14).random()
                setField(i,1,1,1, i,1)
                description = "Используйте цикл с условием <b>while</b>, так как коридор теперь переменной длины"
                maxCycles = maxx
                maxTries = 5
            }
            "w2" -> {
                val i = (5..14).random()
                setField(i,1,1,1, i,1)
                for(i in 1..maxx) doPaint(i,1,true)
                description = "Используйте цикл с условием <b>while</b>, так как коридор переменной длины"
                maxCycles = maxx*2
                maxTries = 3
            }
            "w3" -> {
                val i = (5..9).random()
                setField(12,3,1,1,i+1,1)
                wallH(1,0, i-1)
                description = "Робот должен дойти до конца стены"
                maxCycles = i
                maxTries = 5
            }
            "w4" -> {
                val i = (4..8).random()
                setField(10,3,2,2,i+1,2)
                for(j in 3..i) doPaint(j,2)
                description = "Робот должен дойти до конца закрашенной области"
                maxCycles = i-1
                maxTries = 5
            }
            "w5" -> {
                val x=(4..9).random()%6+4
                val y=(3..8).random()%6+3
                setField(10,9,2,2, x, y)
                for(i in 2..x)
                    for(j in 2..y)
                        doPaint(i,j)
                description = "Робот должен дойти до угла закрашенной области"
                maxTries = 3
            }
            "w6" -> {
                val x=(4..12).random()
                val y=(2..9).random()
                setField(15,10,1,1,15,10)
                wallH(y,0,x-1)
                wallH(y,x+1, maxx)
                description = "Робот оставил зарядное устройство в соседней комнате, чтобы в неё попасть, придётся отыскать дверь"
                maxTries = 5
            }
            "w7" -> {
                val i=(4..7).random()
                val x=(9..13).random()
                val y=(2..5).random()
                setField(15,10,1,8,x,y)
                doPaint(i,8)
                doPaint(i,y)
                doPaint(x,y)
                description = "Робот должен дойти до третьей закрашенной клетки"
                maxTries = 5
            }
            "w8" -> {
                val i=(6..9).random()
                val x=(2..5).random()
                val y=(9..12).random()
                setField(13,4,i,2,x-1,2)
                wallH(2,x-1,y-1)
                for(j in x..y)
                    doPaint(j,2,true)
                description = "Робот должен закрасить все клетки над стеной"
                maxCycles = (y-x)*2+y-i+4
                maxTries = 5
            }
            "w9" -> {
                val y=(2..9).random()
                setField(14,10,7,10,8,10)
                wallV(7, y,9)
                for(i in y..10){ doPaint(7,i,true); doPaint(8,i,true) }
                description = "Робот должен закрасить все клетки вокруг стены"
                maxCycles = (10-y)*4+3
                maxTries = 3
            }
            "w10" -> {
                val y=(2..9).random()
                val yy=(2..9).random()
                val x=(2..5).random()
                val xx=(8..11).random()
                setField(14,10,1,10,14,10)
                wallV(x,y,9)
                wallV(xx,yy,9)
                for(i in y..10){ doPaint(x,i,true); doPaint(x+1,i,true) }
                for(i in yy..10){ doPaint(xx,i,true); doPaint(xx+1,i,true) }
                description = "Робот должен закрасить все клетки вокруг стен"
                maxTries = 3
            }
            "w11" -> {
                val x=(2..6).random()
                val xx=(9..12).random()
                setField(15,5,x,2,x,2)
                wallH(2,x,xx)
                for(i in x..xx+2){ doPaint(i,2,true); doPaint(i,3,true) }
                description = "Робот должен закрасить все клетки вокруг стены"
                maxTries = 3
            }
            "w12" -> {
                val x=(2..6).random()
                val xx=(8..12).random()
                val y=(2..5).random()
                val yy=(7..10).random()
                setField(14,12,x,y,x,y)
                wallH(y,x,xx-1)
                wallH(yy,x,xx-1)
                wallV(x,y,yy-1)
                wallV(xx,y,yy-1)
                for(i in x..xx+1){ doPaint(i,y,true); doPaint(i,yy+1,true) }
                for(i in y..yy){ doPaint(x,i,true); doPaint(xx+1,i,true) }
                description = "Робот должен закрасить все клетки вокруг блока"
                maxTries = 3
            }
            "w13" -> {
                setField(5,10,3,10,3,1)
                for(i in 1..maxy){
                    val x=(0..maxy-i).random()
                    if( x and 4 == 0) wallV(2,i,i)
                    if( x and 2 == 0) wallV(3,i,i)
                    if( x and 6 != 0) robot.endY = i
                }
                description = "Робот должен дойти до первого просвета. Используйте логическую операцию <b>&&</b> (И)"
                maxTries = 7
            }
            "w14" -> {
                setField(5,10,3,10,3,1)
                for(i in 1..maxy){
                    val x=(0..maxy-i).random()
                    if(x and 1 == 0) wallV(2,i,i)
                    if(x and 2 == 0) wallV(3,i,i)
                    if(x and 3 == 3) robot.endY = i
                }
                description = "Робот должен дойти до первого сквозного просвета. Используйте логическую операцию <b>||</b> (ИЛИ)"
                maxTries = 7
            }
            "w15" -> {
                setField(15,5,2,3,15,4)
                doPaint(fieldWidth,3)
                for(i in maxx downTo 2){
                    val x=(0..i-2).random()
                    if(x and 1 != 0) doPaint(i,3)
                    if(x and 2 == 0) wallH(3,i-1,i-1)
                    if(x and 3 == 3) robot.endX = i-1
                }
                doPaint(robot.endX+1,4,true)
                description = "Робот должен закрасить клетку под первым закрашенным просветом. Выберите сами: И = <b>&&</b>, ИЛИ = <b>||</b>"
                maxTries = 7
            }
            "w16" -> {
                val x=(3..13).random()
                setField(15,5,2,3,x,3)
                for(i in 2..x) wallV(i-1,2,2)
                description = "Робот должен остановиться после последней стенки"
                maxTries = 3
            }
            "w17" -> {
                val x = (0..4).random()*2+5
                setField(15,5,2,3,x,3)
                for(i in 2..x step 2) wallV(i,2,2)
                description = "Последнее задание. Робот должен оказаться в итоге после последней стенки"
                maxTries = 3
            }
            "cc1" -> {
                setField(31,7,1,1,31,7)
                var s = 0
                for(i in 1..6)
                    for(j in 1..5)
                        doPaint(++s,i,true)
                description = "Вложенные циклы. Используйте цикл <b>for</b> внутри цикла <b>for</b> для повторения дейтсвий"
                maxCycles = 66
            }
            "cc2" -> {
                setField(22,7,1,1,22,7)
                var s = 0
                for(i in 1..6)
                    for(j in 1..i)
                        doPaint(++s,i,true)
                description = "Используйте цикл <b>for</b> внутри цикла <b>for</b> для повторения дейтсвий (отличие от первой задачи - в один символ!)"
                maxCycles = 48
            }
            "cc3" -> {
                setField(22,7,1,1,22,7)
                var s = 0
                for(i in 6 downTo 1)
                    for(j in 1..i)
                        doPaint(++s,7-i,true)
                description = "Пожалуй, имеет смысл запустить один из циклов в обратную сторону?"
                maxCycles = 48
            }
            "cc4" -> {
                setField(11,10,1,1,1,10)
                for(j in 1..maxy){
                    wallH(j,1, maxx)
                    for(i in 1..fieldWidth)
                        doPaint(i,j,true)
                }
                description = "Теперь в главный цикл вложены два других цикла"
                maxCycles = 288
            }
            "cc5" -> {
                setField(14,9,1,1,1,9)
                for(j in 1..maxy)
                    wallH(j,1-j%2,maxx-j%2)
                description = "Снова в главный цикл вложены два других цикла"
                maxCycles = 112
            }
            "cc6" -> {
                setField(21,10,1,10,21,10)
                for(j in 0..4){
                    wallH(9,j*4+1,j*4+3)
                    wallV(j*4+1,0,8)
                    doPaint(j*4+5,1,true)
                }
                description = "Теперь в главный цикл вложены три других цикла"
                maxCycles = 115
            }
            "cc7" -> {
                setField(10,10,1,1,1,10)
                for(i in 1..9)
                    for(j in 1..i)
                        doPaint(j,i,true)
                description = "Теперь оба внутренних цикла зависят от счётчика внешнего цикла"
                maxCycles = 144
            }
            "cc8" -> {
                setField(10,10,1,1,1,10)
                for(i in 1 .. 9)
                    for(j in 1 .. 10-i)
                        doPaint(j,i,true)
                description = "Пожалуй, имеет смысл запустить один из циклов в обратную сторону?"
                maxCycles = 144
            }
            "cc9" -> {
                setField(10,10,10,1,10,10)
                for(i in 1..9){
                    doPaint(fieldWidth,i,true)
                    if (i<9) wallH(i,0,8-i)
                    wallH(i,10-i,maxx)
                }
                description = "Снова оба внутренних цикла зависят от счётчика внешнего цикла"
                maxCycles = 108
            }
            "cc10" -> {
                setField(15,8,1,1,1,8)
                for(i in 1 .. 7)
                    for(j in 1 .. i*2)
                        doPaint(j,i,true)
                description = "Теперь оба внутренних цикла вдвойне зависят от счётчика внешнего цикла"
                maxCycles = 175
            }
            "cc11" -> {
                setField(14,8,1,1,1,8)
                for(i in 1 .. 7)
                    for(j in 1 until i*2)
                        doPaint(j,i,true)
                description = "Снова оба внутренних цикла вдвойне (почти) зависят от счётчика внешнего цикла"
                maxCycles = 154
            }
            "cc12" -> {
                setField(33,7,1,1,1,7)
                var x = 1
                for (i in 1..6) {
                    for (j in 1 .. x)
                        doPaint(j, i, true)
                    x *= 2
                }
                description = "Геометрическая прогрессия. Добавьте перед циклом дополнительную переменную: <b>var x = 1</b> и увеличивайте её в цикле вдвое: <b>x *= 2</b>"
                maxCycles = 195
            }
            "cc13" -> {
                setField(14,9,1,1,9,9)
                for(i in 1 .. 4){
                    doPaint(2*i+1,2*i,true)
                    wallH(i*2-1,0,maxx-1)
                    wallH(i*2,maxx-7+i,maxx)
                }
                description = "Добавьте перед циклом дополнительную переменную: <b>var x = 13</b> и уменьшайте её внутри цикла на два: <b>x −= 2</b>"
                maxCycles = 84
            }
            "cc14" -> {
                setField(14,9,1,1,5,9)
                for(i in 1 .. 4){
                    wallH(i*2-1,i-1,maxx-i)
                    wallH(i*2,i+1,maxx-i+1)
                    wallV(i,i*2-1,i*2)
                    wallV(fieldWidth-i,i*2+0,i*2+1)
                }
                description = "Добавьте перед циклом дополнительную переменную: <b>var x = 1</b> и дважды за цикл уменьшайте её на единицу: <b>x−−</b>"
                maxCycles = 84
            }
            "cc15" -> {
                setField(15,9,8,1,8,9)
                for(i in 1 .. 8)
                    for(j in 1 .. i){
                        doPaint(j+7,i,true)
                        doPaint(9-j,i,true)
                    }
                description = "Опять пирамиды, только чуть сложнее. Попробуйте красить только на одном проходе внутреннего цикла"
                maxCycles = 184
            }
            "cc16" -> {
                setField(22,7,2,6,22,6)
                for(i in 0 until 20)
                    for(j in 1 .. i%5+1)
                        doPaint(i+2,7-j,true)
                description = "Это уже интересно: тройная вложенность (два цикла <b>⇅</b> внутри цикла <b>for →</b> внутри цикла ◢)"
                maxCycles = 200
            }
            "cc17" -> {
                setField(29,8,2,7,28,7)
                var xx = 2
                for(x in 1..6) {
                    for(i in 1 .. x) {
                        for (j in 1 .. i)
                            doPaint(xx, 8 - j, true)
                        xx++
                    }
                    xx++
                }
                description = "Тройная вложенность (два цикла <b>↑↓</b> внутри цикла <b>for →</b> внутри цикла <b>for</b> ◢)"
                maxCycles = 196
            }
            "cc18" -> {
                setField(12,8,1,1,1,8)
                val xx = intArrayOf(6,8,4,3,10,2,5)
                for(j in 0 until maxy){
                    wallH(j+1,1, maxx)
                    doPaint(xx[j], j+1,true)
                    wallV(xx[j], j, j)
                }
                description = "А теперь внтуренним циклом будет <b>while</b>, так как длины строк переменные"
                wallV(1,maxy,maxy)
            }
            "cc19" -> {
                val y=(3..10).random()
                setField(11,y+1,1,1,1,y+1)
                for(j in 1..maxy){
                    wallH(j,1, maxx)
                    doPaint(fieldWidth, j,true)
                }
                description = "А теперь, наружный цикл - тоже <b>while</b>, так как высота поля - переменная"
                maxTries = 2
            }
            "if1" -> {
                val x=(0..1).random()
                setField(5,5,2,3,4,3)
                doPaint(4,3,true)
                wallV(3,2,2)
                wallH(2+x,2,2)
                wallV(2,x*3,x*3+1)
                description = "Робот должен обогнуть стену. Внимание, поле меняется! Используйте оператор ветвления <b>if()...else...</b>"
                maxCycles = 5
                maxTries = 5
            }
            "if2" -> {
                val x=(0..15).random()
                maxCycles = 9
                setField(5,5,3,3,3,3)
                doPaint(3,3,true)
                if(x and 1 != 0){wallV(1,2,2); doPaint(2,3,true); maxCycles++}
                if(x and 2 != 0){wallV(4,2,2); doPaint(4,3,true); maxCycles++}
                if(x and 4 != 0){wallH(1,2,2); doPaint(3,2,true); maxCycles++}
                if(x and 8 != 0){wallH(4,2,2); doPaint(3,4,true); maxCycles++}
                description = "Закрасьте клетки около стен, используя оператор ветвления <b>if</b>"
                maxTries = 3
            }
            "if3" -> {
                val x=(0..3).random()
                maxCycles = 3
                setField(2,2,(x and 1)+1,(x and 2)/2+1,2-(x and 1),2-(x and 2)/2)
                doPaint(2-(x and 1), 2-(x and 2)/2,true)
                description = "Четыре варианта = три условия <b>if (...)</b>. Не забывайте о ветке <b>else</b> (иначе)"
                maxTries = 5
            }
            "if4" -> {
                val x=(0..3).random()
                maxCycles = 2
                setField(5,5,3,3,if(x and 1 != 0) 3 else (x and 2)+2, if(x and 1 != 0) (x and 2)+2 else 3)
                doPaint(if(x and 1 != 0) 3  else (x and 2)+2, if (x and 1 != 0) (x and 2)+2 else 3, true)
                if (x and 1 != 0) wallH(3-(x and 2)/2,2,2) else wallV(3-(x and 2)/2,2,2)
                description = "Четыре варианта = три условия <b>if (...)</b>. Не забывайте о ветке <b>else</b> (иначе)"
                maxTries = 7
            }
            "if5" -> {
                val x=(0..3).random()
                maxCycles = 6
                setField(5,5,3,3,3,3)
                doPaint(if(x and 1 != 0) (x and 2)+2 else 3, if (x and 1 != 0) 3 else (x and 2)+2, true)
                doPaint(if(x and 1 != 0) 4-(x and 2) else 3, if (x and 1 != 0) 3 else 4-(x and 2), true)
                if (x and 1 != 0) wallH(3-(x and 2)/2,2,2) else wallV(3-(x and 2)/2,2,2)
                description = "Подумайте внимательно, здесь четыре варианта действий или два?"
                maxTries = 7
            }
            "if6" -> {
                val x=(0..3).random()
                setField(5,5,3,3,if (x and 1 != 0) 3 else (x and 2)+2, if (x and 1 != 0) (x and 2)+2 else 3)
                doPaint(if (x and 1 != 0) 3 else (x and 2)+2, if (x and 1 != 0) (x and 2)+2 else 3,true)
                doPaint(if (x and 1 != 0) 3 else 4-(x and 2), if (x and 1 != 0) 4-(x and 2) else 3)
                description = "Задание посложнее, с тремя вложенными операторами <b>if...else...</b>"
                maxTries = 7
            }
            "if7" -> {
                val x=(0..4).random()
                maxCycles = 3
                setField(4,4,(x and 1)+2,(x and 2)/2+2,3-(x and 1),3-(x and 2)/2)
                doPaint(3-(x and 1),3-(x and 2)/2,true)
                wallV((x and 1)*2+1,(x and 2)/2+1,(x and 2)/2+1)
                wallH((x and 2)+1,(x and 1)+1,(x and 1)+1)
                description = "Кажется, что-то подобное уже было? Попробуйте написать <b>fun if7() = if3()</b>"
                maxTries = 5
            }
            "if8" -> {
                val x=(0..3).random()
                setField(6,6,2,2,if (x and 1 != 0) 2 else 5 - (x and 2)/2, if (x and 1 != 0) 5 - (x and 2)/2 else 2)
                if (x and 1 != 0) wallH(1,1,1) else wallV(1,1,1)
                doPaint(if (x and 1 != 0) 2 else 3, if (x and 1 != 0) 3 else 2,true)
                doPaint(if (x and 1 != 0) 2 else 4, if (x and 1 != 0) 4 else 2,true)
                if (x and 2 != 0){
                    if (x and 1 != 0) wallH(4,1,1) else wallV(4,1,1)
                }
                else
                    doPaint(if (x and 1 != 0) 2 else 5, if (x and 1 != 0) 5 else 2,true)
                description = "Вложенные операторы <b>if</b> в обеих ветках главного <b>if...else...</b>"
                maxTries = 7
            }
            "if9" -> {
                val x= min(3, (0..4).random())
                setField(5,5,3,3,3,3)
                if (x and 1 != 0) wallH(2,1,1)
                if (x and 2 != 0) wallH(3,3,3)
                if ((x and 1 != 0)&&(x and 2 != 0)){ doPaint(3,2,true); doPaint(3,4,true) }
                else doPaint(3,3,true)
                description = "Используйте временные переменные для запоминания значений, например, <b>val u = wallFromUp</b>"
                maxTries = 5
            }
            "if10" -> {
                val x= max(0, (-1..3).random())
                setField(5,5,3,3,3,3)
                if (x and 1 != 0) doPaint(2,3)
                if (x and 2 != 0) doPaint(4,3)
                if (x and 3 != 0) doPaint(3,2,true) else doPaint(3,3,true)
                description = "Используйте временные переменные для запоминания значений, например, <b>val r = cellIsPainted</b>"
                maxTries = 5
            }
            "if11" -> {
                val x=(0..2).random()
                setField(5,5,2,2,4,2)
                for(i in 0 until x) doPaint(3,i+2)
                doPaint(3,x+2,true)
                description = "Вложенный <b>if</b> внутри <b>if</b> внутри <b>if</b>"
                maxTries = 5
            }
            "cif1" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12)
                    if((0..1).random() != 0) {wallH(1,i,i); doPaint(i+1,2,true) }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски клеток под стенами"
                maxTries = 3
            }
            "cif2" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x != 0) doPaint(i+1,2,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски клеток около стен. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 3
            }
            "cif3" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x==3) doPaint(i+1,2,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски клеток между стен. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 3
            }
            "cif4" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x==1) doPaint(i+1,2,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 3
            }
            "cif5" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x!=1) doPaint(i+1,2,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 3
            }
            "cif6" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x!=3) doPaint(i+1,2,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 3
            }
            "cif7" -> {
                setField(13,3,1,2,13,2)
                wallV(13,1,1)
                maxCycles = 12
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i) else { doPaint(i+1,1,true); maxCycles+=3 }
                    if (x and 2 != 0) wallH(2,i,i) else { doPaint(i+1,3,true); maxCycles+=3 }
                }
                description = "Используйте вложенные операторы <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif8" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..2).random()
                    if (x and 1 != 0) {wallH(1,i,i); doPaint(i+1,3,true) }
                    if (x and 2 != 0) {wallH(2,i,i); doPaint(i+1,1,true) }
                }
                description = "Используйте вложенные операторы <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif9" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x==1) doPaint(i+1,3,true)
                    if (x==2) doPaint(i+1,1,true)
                    if (x==3) doPaint(i+1,2,true)
                }
                description = "Используйте вложенные операторы <b>if..else</b> внутри цикла <b>while</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif10" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..7).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x and 4 != 0) doPaint(i+1,2)
                    if ((x and 6)==4) doPaint(i+1,3,true)
                }
                description = "Используйте вложенный <b>if</b> внутри цикла <b>while</b> для закраски нужных клеток. Помните, И=<b>&&</b>, ИЛИ=<b>||</b>"
                maxTries = 2
            }
            "cif11" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..7).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x and 4 != 0) doPaint(i+1,2)
                    if (x==5) doPaint(i+1,3,true)
                }
                description = "Используйте два логических И (<b>&&</b>) в условии <b>if</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif12" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..7).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x and 4 != 0) doPaint(i+1,2)
                    if ((x and 5)==4 || x==0) doPaint(i+1,1,true)
                }
                description = "Используйте комбинацию логических И (<b>&&</b>) и ИЛИ (<b>||</b>) в условии <b>if</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif13" -> {
                setField(14,3,1,2,13,2)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..7).random()
                    if (x and 1 != 0) wallH(1,i,i)
                    if (x and 2 != 0) wallH(2,i,i)
                    if (x and 4 != 0) doPaint(i+1,2)
                    if (x==6 || x==0) doPaint(i+1,1,true)
                }
                description = "Используйте комбинацию логических И (<b>&&</b>) и одного ИЛИ (<b>||</b>) в условии <b>if</b> для закраски нужных клеток"
                maxTries = 2
            }
            "cif14" -> {
                setField(14,4,1,2,13,2)
                wallH(2,0,0)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) wallH(2,i,i)
                    if (x and 2 != 0) doPaint(i+1,3)
                    if (x==2) doPaint(i+1,4,true)
                }
                description = "Вложенные <b>if</b> внутри <b>if</b> внутри <b>while</b>. Не забывайте возвращать робота в главную строку"
                maxTries = 2
            }
            "cif15" -> {
                setField(14,3,1,2,13,2)
                wallH(1,0,0)
                wallH(2,0,0)
                wallV(13,1,1)
                for(i in 1..12){
                    val x=(0..3).random()
                    if (x and 1 != 0) doPaint(i+1,1)
                    if (x and 2 != 0) doPaint(i+1,3)
                    if (x==3) doPaint(i+1,2,true)
                }
                description = "Используйте временные переменные для запоминания значений, например, <b>val u = cellIsPainted</b>"
                maxTries = 2
            }
            "cif16" -> {
                val x=(0..3).random()
                maxCycles = 16
                setField(9,9,(x and 1)*8+1,(x and 2)*4+1,9-(x and 1)*8,9-(x and 2)*4)
                description = "Отправьте робота в противоположный угол. Цикл <b>while</b> внтури ветвления <b>if..else</b>"
                maxTries = 3
            }
            "cif17" -> {
                var x = (2..6).random()
                x *= (0..1).random()*2-1
                x += 8
                val y=(1..8).random()
                setField(15,9,8,9,x,y)
                for(i in y until 9) {wallV(7,i,i); wallV(8,i,i) }
                if (x<8){
                    for(i in x-1 until 7) wallH(y,i,i)
                    for(i in x-1 until 8) wallH(y-1,i,i)
                    wallV(8,y-1,y-1); wallV(x-1,y-1,y-1)
                }
                else{
                    for(i in x-1 downTo 8) wallH(y,i,i)
                    for(i in x-1 downTo 7) wallH(y-1,i,i)
                    wallV(7,y-1,y-1); wallV(x,y-1,y-1)
                }
                description = "Дойти до конца коридора переменной конфигурации. Цикл <b>while</b> внтури ветвления <b>if..else</b>"
                maxTries = 5
            }
            "cif18" -> {
                val x=(3..12).random()
                val y=(0..1).random()
                setField(15,4,2,2,x+1,2+y)
                for(i in 3 .. x) { doPaint(i,2); doPaint(i,3) }
                doPaint(x+1,3-y)
                description = "Робот должен стать в конец более короткого ряда. Используйте <b>if(...) break</b> (выход из цикла по условию)"
                maxTries = 5
            }
            "cif19" -> {
                val x=(3..9).random()
                val y=(0..1).random()
                setField(15,4,2,2,x+2,2+y)
                wallH(1,2,x)
                wallH(3,2,x)
                wallH(3-2*y,x+1,x+(1..4).random())
                description = "Робот должен стать в конец более короткой стены. Используйте <b>if(...) break</b> (выход из цикла по условию)"
                maxTries = 5
            }
            "cif20" -> {
                val x=(8..13).random()
                val y=(0..1).random()
                setField(15,4,2,2,x+2,2+y)
                wallH(1+2*y,2,x)
                wallH(3-2*y,2,x-(0..4).random()-1)
                description = "Робот должен стать в конец более длинной стены. Объявите перед циклом 2 переменные, например, <b>var u = freeFromUp</b>, и используйте <b>break</b>"
                maxTries = 5
            }
            "cif21" -> {
                var x=(0..1).random()*2-1
                x *= (1..6).random()
                val y=(3..7).random()
                setField(15,8,8,2,8+x,y)
                doPaint(8+x,2); doPaint(8+x, y)
                description = "Робот должен переместиться во вторую закрашенную клетку"
                maxTries = 5
            }
            "cif22" -> {
                val x = (1..5).random()
                val y = (9..13).random()
                val xx = (x..y).random()+1
                val yy = (x..y).random()
                setField(15,5, xx,3,1,1)
                wallV(x,2,2)
                wallV(y+1,2,2)
                wallH(2,x,y)
                wallH(3,x,y)
                when((0..7).random()){
                    0 ->        unWallV(x,2,2)
                    1 ->        unWallV(y+1,2,2)
                    in 2..4 ->  unWallH(2, yy, yy)
                    else ->     unWallH(3, yy, yy)
                }
                description = "Последнее, усложнённое задание. Робот должен найти выход из комнаты и пройти в верхний левый угол"
                maxTries = 7
            }
            "cnt1" -> {
                val x=(2..9).random()
                setField(10,5,x,3,x,3)
                doPaint(10,3,true)
                description = "Используйте переменную-счётчик (<b>var n = 0</b>) для подсчёта пройденных клеток (<b>n++</b>). Затем верните робота на место с помощью <b>for</b> или <b>repeat</b>"
                maxTries = 3
            }
            "cnt2" -> {
                val x=(2..7).random()
                val y=(2..7).random()
                setField(8,8,x,y,x,y)
                doPaint(8,8,true)
                description = "Используйте две переменные-счётчика для подсчёта пройденных клеток. Затем верните робота на место"
                maxTries = 3
            }
            "cnt3" -> {
                val y=(4..9).random()
                val x=(2..y-1).random()
                setField(10,10,x,y,y,x)
                for(i in 1..10) doPaint(i,i)
                description = "Используйте переменную-счётчик для перемещения робота в клетку, симметричную диагонали"
                maxTries = 3
            }
            "cnt4" -> {
                val x=(1..5).random()
                val y=(4..8).random()
                setField(10,4,x,2,x,3)
                wallH(2,0, y)
                description = "Используйте переменную-счётчик для того, чтобы робот обошёл стену"
                maxTries = 3
            }
            "cnt5" -> {
                val x=(7..11).random()
                val y=(7..11).random()
                val xx=(2..x-1).random()
                val yy=(2..y-1).random()
                setField(x,y,xx,yy,xx,yy)
                for(i in 1 .. x){ doPaint(i,1,true); doPaint(i,y,true) }
                for(i in 2 until y){ doPaint(1,i,true); doPaint(x,i,true) }
                description = "Используйте переменные-счётчики для того, чтобы робот мог вернуться на место после закраски периметра"
                maxTries = 2
            }
            "cnt6" -> {
                val x=(1..6).random()
                var y=(1..6).random()
                while (y==x) y=(1..6).random()
                setField(15,5,8,3, if(x<y) 8-x else 8+y, 3)
                wallV(7-x,1,3)
                wallV(8+y,1,3)
                description = "Робот должен определить, какая стена ближе, и переместиться к ней"
                maxTries = 5
            }
            "cnt7" -> {
                val x=(2..5).random()%5+2
                val y=(11..15).random()%5+11
                val xx = (x+1..y-1).random()
                setField(16,4,xx,2,xx,3)
                wallH(2,x-1,y-1)
                wallV(if((0..1).random()!=0) x-1 else y,0,1)
                description = "Робот должен обойти стену с одной из сторон"
                maxTries = 5
            }
            "cnt8" -> {
                setField(16,3,2,2,1,2)
                var x = 0
                for(i in 2..16)
                    if(i>10 || (0..1).random()!=0) {
                        if(++x==5) robot.endX = i-1
                        doPaint(i,2)
                    }
                description = "Робот должен остановиться на пятой закрашенной клетке"
                maxTries = 5
            }
            "cnt9" -> {
                setField(16,3,2,2,1,2)
                var x = 0
                for(i in 2..16)
                    if(i>10||(0..1).random()%2!=0) {
                        if(robot.endX==0 && ++x==3) robot.endX = i-1
                        doPaint(i,2)
                    }
                    else x=0
                description = "Робот должен остановиться на третьей закрашенной <b>подряд</b> клетке"
                maxTries = 5
            }
            "cnt10" -> {
                val x=(2..9).random()
                val y= min(x, 6)
                setField(10,3,2,2,y+1,2)
                wallH(2,1, x-1)
                for(i in 2 .. y) doPaint(i,2,true)
                description = "Робот должен закрасить клетки над стеной, но всего не более пяти"
                maxTries = 5
            }
            "cnt11" -> {
                val x=(2..9).random()
                setField(10,3,1,2,2,2)
                wallV(x,1,1)
                wallH(1,1, x-1)
                wallH(2,1, x-1)
                if(x>5) doPaint(2,2,true)
                description = "Робот должен закрасить первую клетку коридора, если длина тупика более 4 клеток"
                maxTries = 5
            }
            "cnt12" -> {
                val x=(8..15).random()
                setField(x,1,1,1,x,1)
                for(i in 1 .. x) if(i%2==0) doPaint(i,1,true)
                description = "Робот должен закрасить каждую вторую клетку в коридоре переменной длины. Проверить нечётность можно так: <b>if (n%2 == 1) ...</b>"
                maxTries = 3
            }
            "cnt13" -> {
                val x=(8..15).random()
                setField(x,1,1,1,x,1)
                for(i in 1 .. x) if(i%2!=0) doPaint(i,1,true)
                description = "Робот должен закрасить каждую вторую клетку в коридоре переменной длины. Проверить чётность можно так: <b>if (n%2 == 0) ...</b>"
                maxTries = 3
            }
            "cnt14" -> {
                val x=(8..15).random()
                setField(x,1,1,1,x,1)
                for(i in 1 .. x) if(i%3==1) doPaint(i,1,true)
                description = "Робот должен закрасить каждую третью клетку в коридоре переменной длины"
                maxTries = 3
            }
            "cnt15" -> {
                val x=(8..15).random()
                setField(x,1,1,1,x,1)
                for(i in 1 .. x) if(i%3==0) doPaint(i,1,true)
                description = "Робот должен закрасить каждую третью клетку в коридоре переменной длины"
                maxTries = 3
            }
            "cnt16" -> {
                setField(24,3,2,2,24,2)
                var y = 0
                var i = 2
                while (i<=24) {
                    val x=(0..4).random() + i + if (i>2) 1 else 0
                    while(i<=24 && i<x) {
                        if (y != 0) doPaint(i, 2, true)
                        i++
                    }
                    if (i<=24) doPaint(i++,2)
                    y= 1-y
                }
                description = "Робот должен закрасить все нечётные пролёты между закрашенными клетками"
                maxTries = 3
            }
            "cnt17" -> {
                val x=(4..22).random()
                setField(24,3,2,2,x,2)
                for(i in 2 until 24)
                    if ((i + if(i<x) 1 else 0)%2 != 0)
                        wallH(2, i-1, i-1)
                description = "Последнее задание. Робот должен обнаружить клетку, нарушающую закономерность"
                maxTries = 3
            }
	/*
    "mix1" -> {
		val x=(..).random()%5+2
		setField(8,8,x,x,8,8)
		for(i in x until 8)
			for(j in x until 8)
				doPaint(i,j,true)
		description = "Закрасьте прямоугольник"
	}
    "mix2" -> {
		val x=(..).random()%5+2
		setField(8,8,x,x,8,8)
		for(i in x until 8)
			for(j in x+(i-x)%2 until 8 step 2)
				doPaint(i,j,true)
		description = "Закрасьте прямоугольник в шахматном порядке"
	}
    "mix3" -> {
		val x=(0..3).random()+6
		val y=(0..3).random()+2
		xval x=(0..3).random()+2
		yval y=(0..3).random()+6
		setField(10,10,x,y,x,y)
		for(i in xx until x)
			for(j in y until yy)
				doPaint(i,j,true)
		doPaints(xx,yy)
		description = "Закрасьте прямоугольник, предварительно определив его размер"
	}
    "mix4" -> {
		val x=(..).random()%8+2
		val y=(..).random()%8+2
		xval x=(..).random()%8+2
		yval y=(..).random()%8+2
		setField(10,10,x,y,xx,yy)
		doPaint(xx,yy)
		description = "Найдите закрашенную клетку"
	}
    "mix5" -> {
		val x=(..).random()%8+2
		val y=(..).random()%8+2
		xval x=(..).random()%8+2
		yval y=(..).random()%8+2
		setField(10,10,x,y,xx,yy)
		for (i in 1 i<=10 i++) { doPaint(i,yy,true) doPaint(xx,i,true) }
		doPaints(xx,yy)
		description = "Найдите закрашенную клетку, и закрасьте указанные клетки"
	}
    "mix6" -> {
		setField(10,10,1,1,1,10)
		for (i in 1 i<10 i++)
		  if ((..).random()%2){
			doPaint(1,i)
			doPaint(10,i,true)
		  }
		description = "Закрасьте клетки напротив уже закрашенных"
	}
    "mix7" -> {
		setField(10,10,1,1,1,10)
		for (i in 1 i<10 i++){
		  int f=(0..3).random()
		  if (f and 1) doPaint(1,i)
		  if (f and 2) doPaint(10,i)
		  if (f==3)
			  for (j in 2 j<10 j++) doPaint(j,i,true)
		}
		description = "Закрасьте горизонтальные ряды между закрашенными клетками"
	}
    "mix8" -> {
		setField(11,15,1,1,1,15)
		for (i in 1 i<15 i++){
		  int f=(..).random()%8
		  if (f and 1) doPaint(1,i)
		  if (f and 2) doPaint(11,i)
		  if ((f and 4) && f!=7) doPaint((..).random()%9+2,i)
		  if (f==7){
			  for (j in 2 j<11 j++) doPaint(j,i,true)
			  doPaints((..).random()%9+2,i)
		  }
		}
		description = "Закрасьте горизонтальные ряды между закрашенными клетками, если там есть ещё одна закрашенная"
	}
    "mix9" -> {
		val x=(..).random()%8+2
		val y=(..).random()%8+2
		do xval x=(..).random()%8+2 while(xx==x)
		do yval y=(..).random()%8+2 while(yy==y)
		setField(10,10,x,y,x,y)
		for(i in min(x,xx) i<=max(x,xx) )
			for(j in min(y,yy) j<=max(y,yy) j++)
				doPaint(i,j,true)
		doPaints(xx,yy)
		description = "Закрасьте прямоугольник, предварительно определив его размер"
	}
    "mix10" -> {
		setField(7,10,1,10,7,1)
		wallV(1,2,10)
		wallV(6,1,9)
		for (i in 2 i<10 i++)
		  if ((..).random()%2){
			doPaint(1,i)
			doPaint(7,i,true)
		  }
		description = "Закрасьте клетки напротив уже закрашенных. Используйте массив для запоминания позиций"
	}
    "mix11" -> {
		setField(7,10,1,10,7,10)
		wallV(1,2,10)
		wallV(6,2,10)
		for (i in 2 i<10 i++)
		  if ((..).random()%2){
			doPaint(1,i)
			doPaint(7,i,true)
		  }
		description = "Закрасьте клетки напротив уже закрашенных. Используйте массив для запоминания позиций"
	}
*/
            "fun1" -> {
                setField(10,10,3,7,5,3)
                doCross(3,7,true)
                doCross(8,6,true)
                doCross(5,3,true)
                description = "Составьте функцию <b>fun cross() {...}</b>. Заставьте робота выполнить её трижды"
            }
            "fun2" -> {
                setField(16,4,2,2,14,2)
                for(i in 0..4)
                    doBox(i*3+2,2,true)
                description = "Составьте функцию <b>fun box4()</b>. Заставьте робота выполнить её нужное число раз (в цикле)"
            }
            "fun3" -> {
                setField(12,12,1,1,11,11)
                for(i in 0..6)
                    doBox(i*2+1,i*2+1,true)
                description = "Используйте функцию <b>box4()</b> из второго задания. Заставьте робота выполнить её нужное число раз"
            }
            "fun4" -> {
                setField(6,6,2,2,2,2)
                doBox(2,2,true); doBox(4,2,true); doBox(2,4,true); doBox(4,4,true)
                description = "Составьте функцию <b>box16()</b>, четырежды вызывающую функцию <b>box4()</b> из второго задания"
            }
            "fun5" -> {
                setField(10,10,2,2,2,2)
                for(i in 2 until 10)
                    for(j in 2 until 10)
                        doPaint(i,j,true)
                description = "Используйте функцию <b>box16()</b> из четвёртого задания. Заставьте робота выполнить её четырежды"
            }
            "fun6" -> {
                setField(9,9,1,2,1,8)
                for(i in 2 .. 8 step 2)
                    for(j in 1 .. 9)
                        doPaint(j,i,true)
                description = "Составьте и используйте функцию <b>row()</b>, которая закрашивает строку и возвращает робота назад"
            }
            "fun7" -> {
                setField(9,9,2,1,8,1)
                for(i in 2 .. 8 step 2)
                    for(j in 1 .. 9)
                        doPaint(i,j,true)
                description = "Составьте и используйте функцию <b>col()</b>, которая закрашивает столбец и возвращает робота назад"
            }
            "fun8" -> {
                setField(9,9,1,2,8,1)
                for(i in 2 .. 8 step 2)
                    for(j in 1 .. 9){
                        doPaint(j,i,true); doPaint(i,j,true)
                    }
                description = "Используйте функции <b>row()</b> и <b>col()</b> из предыдущих заданий"
            }
            "fun9" -> {
                setField(15,15,2,2,6,6)
                for(i in 2 .. 6 step 2)
                    for(j in 0..8){
                        doPaint(j+i,i,true); doPaint(i,j+i,true)
                    }
                description = "Используйте функции <b>row()</b> и <b>col()</b> из предыдущих заданий"
            }
            "fun10" -> {
                setField(31,7,2,2,26,2)
                for(i in 0 until 5)
                    for(j in 0 until 5){
                        doPaint(j+i*6+2,2,true); doPaint(j+i*6+2,6,true)
                        doPaint(i*6+2,j+2,true); doPaint(i*6+6,j+2,true)
                    }
                description = "Составьте функцию <b>kontour()</b>. Заставьте робота выполнить её пять раз"
            }
            "fun11" -> {
                setField(15,11,1,1,1,11)
                for(i in 1 until 15 step 2)
                    for(j in 1 until 11 step 2)
                        doPaint(i,j,true)
                description = "Составьте функцию <b>punktir2()</b>. Заставьте робота выполнить её пять раз"
            }
            "fun12" -> {
                setField(16,12,2,2,3,11)
                for(i in 1 until 15 step 2)
                    for(j in 1 until 11)
                        doPaint(i+2-j%2,j+1,true)
                description = "Используйте функцию <b>punktir2()</b> из предыдущего задания"
            }
            "fun13" -> {
                setField(17,14,2,2,2,14)
                for(i in 2 until 15 step 3)
                    for(j in 0 until 12)
                        doPaint(i+j%3,j+2,true)
                description = "Составьте функцию <b>punktir3()</b>. Заставьте робота выполнить её 12 раз"
            }
            "fun14" -> {
                setField(17,15,2,2,2,14)
                for(i in 2 until 17)
                    for(j in 2 until 15)
                        if(i%2==0 || j%2==0) doPaint(i,j,true)
                description = "Составьте функции <b>row1()</b> и <b>row2()</b> для двух разновидностей строк"
            }
            "fun15" -> {
                setField(21,15,2,2,2,14)
                for(i in 2 until 22)
                    for(j in 2 until 15)
                        if((j%2==0 && i%4==0) || (j%2!=0 && (i+2)%4!=0)) doPaint(i-1,j,true)
                description = "Последнее задание. Составьте функции <b>row3()</b> и <b>row4()</b> для двух разновидностей строк"
            }
/*
    "par1" -> {
		setField(12,7,1,1,1,7)
		const int pp[7] = {7,9,5,4,11,2,7}
		for(i in 1 until 7){
			wallH(i,2,12); doPaint(pp[i-1],i,true)
		}
		description = "Составьте процедуры с параметром LeftN(N) и RightN(N), либо одну процедуру LeftRightN(N)"
	}
    "par2" -> {
		setField(10,10,1,1,9,1)
		const int pp[5] = {7,4,9,6,8}
		for(i in 0..5){
			wallV(i*2,2,10)
			for(j in 1 until pp[i]) doPaint(i*2+1,j,true)
		}
		description = "Составьте процедуру с параметром PaintDownN(N)"
	}
    "par3" -> {
		setField(9,9,1,1,5,5)
		wallV(1,1,8)wallH(8,2,8)wallV(8,2,8)wallH(1,3,8)
		wallV(2,2,7)wallH(7,3,7)wallV(7,3,7)wallH(2,4,7)
		wallV(3,3,6)wallH(6,4,6)wallV(6,4,6)wallH(3,5,6)
		wallV(4,4,5)wallH(5,5,5)wallV(5,5,5)
		description = "Составьте процедуру с 4 параметрами GoMaze(D,R,U,L) с использованием 4 циклов for"
	}
    "par4" -> {
		setField(19,8,5,2,17,2)
		for(x=5,i in 0 i<3 x+=7-i*2,i++){
			for(j in 2..i+6); doPaint(x,j,true)
			for(j in 0..3-i){ doPaint(x-j-1,2,true); doPaint(x+j+1,2,true) }
		}
		description = "Составьте процедуру с 2 параметрами PaintT(X,Y) с использованием циклов for"
	}
    "par5" -> {
		setField(7,10,2,2,2,2)
		for(i in 2..7)
			for(j in 2..10); doPaint(i,j,true)
		description = "Составьте процедуру с 2 параметрами Box(X,Y)"
	}
    "par6" -> {
		setField(18,13,2,3,3,8)
		const int pp[5][4] = {{2,6,3,6},{3,7,8,12},{9,10,2,8},{12,15,2,8},{10,17,10,12}}
		for(x=0x<5x++)
			for(i in pp[x][0] until pp[x][1])
				for(j in pp[x][2] until pp[x][3]); doPaint(i,j,true)
		description = "Используйте 5 раз процедуру Box(X,Y) из предыдущего задания"
	}
    "par7" -> {
		setField(13,11,3,2,9,3)
		const int pp[3][4] = {{3,7,2,5},{9,12,3,7},{2,5,8,10}}
		for(x=0x<3x++){
			for(i in pp[x][0] until pp[x][1]){ doPaint(i,pp[x][2],true); doPaint(i,pp[x][3],true) }
			for(j in pp[x][2] until pp[x][3]){ doPaint(pp[x][0],j,true); doPaint(pp[x][1],j,true) }
		}
		description = "Составьте процедуру с 2 параметрами Perimeter(X,Y)"
	}
    "par8" -> {
		setField(16,8,2,2,13,1)
		const int pp[3][4] = {{2,5,2,4},{7,11,3,6},{13,15,1,2}}
		for(x=0x<3x++){
			for(i in pp[x][2]..8){ doPaint(pp[x][0],i,true); doPaint(pp[x][1],i,true) }
			for(j in pp[x][0]+1..pp[x][1]); doPaint(j,pp[x][3],true)
		}
		description = "Последнее задание. Составьте процедуру с 3 параметрами PaintH(W,H,H1) с использованием циклов for"
	}
 */
            else -> {
                setField(1, 1, 1, 1, 1,1)
                description = "Робот остаётся в заводской упаковке"
                error("Нет такого задания: $taskName")
            }
        }
    }
}