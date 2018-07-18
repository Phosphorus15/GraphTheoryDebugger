package net.steepout.grdb.ui

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.geom.Arc2D
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

inline operator fun String.invoke(block: () -> Unit) {
    block.invoke()
}

class GraphCanvas : JPanel(), MouseListener {

    private val clicked = AtomicBoolean(false)

    init {
        background = Color.WHITE
    }

    override fun mouseReleased(e: MouseEvent?) {
        println("released")
        clicked.set(false)
        repaint()
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent) {
        println("pressed ${e.point}")
        if (e.point.x in 10..110 && e.point.y in 10..40) {
            clicked.set(true)
            repaint()
        }
    }

    init {
        addMouseListener(this)
    }

    override fun paint(g1: Graphics?) {
        super.paint(g1)
        val g = g1 as Graphics2D
        "draw it , guys" {
            g.color = Color.CYAN
            g.stroke = BasicStroke(10.0.toFloat())
            g.draw(Arc2D.Double(10.0, 10.0, 200.0, 200.0, .0, 360.0, Arc2D.OPEN))
        }
    }

}

class PrimaryFrame : JFrame() {

    private val container: MainFrame = MainFrame()

    private val canvas: GraphCanvas = GraphCanvas()

    init {
        setSize(600, 400)
        contentPane = container.mainPanel
        canvas.setSize(600, 400)
        canvas.layout = null
        container.canvasContainer.run {
            setViewportView(canvas)
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

}

fun main(args: Array<String>) {
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    /*JFrame().run {
        setSize(300, 400)
        setLocation(200, 300)
        contentPane = GraphCanvas()
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }*/
    PrimaryFrame().isVisible = true
}
