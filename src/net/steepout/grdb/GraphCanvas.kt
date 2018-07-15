package net.steepout.grdb

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.*

inline operator fun String.invoke(block: ()-> Unit) {
    block.invoke()
}

class GraphCanvas : JPanel(), MouseListener{

    private val clicked = AtomicBoolean(false)

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
        if(e.point.x in 10..110 && e.point.y in 10..40) {
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
            g.color = Color.GRAY.brighter()
            g.fill3DRect(10, 10, 100, 30, !clicked.get())
        }
    }

}

fun main(args: Array<String>) {
    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    JFrame().run {
        setSize(300, 400)
        setLocation(200, 300)
        contentPane = GraphCanvas()
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }
}
