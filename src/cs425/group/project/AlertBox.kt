package cs425.group.project

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage

class AlertBox(title: String, message: String)
{
    private val window: Stage = Stage()
    
    init
    {
        window.initModality(Modality.APPLICATION_MODAL)
        window.title = title
        window.minWidth = 250.0
        val label = Text(message)
        label.font = Font.font(label.font.family, 14.0)
        label.wrappingWidth = 500.0
        val hbox = HBox(10.0)
        hbox.alignment = Pos.CENTER
        val okButton = Button("Ok")
        okButton.setOnAction {window.hide()}
        hbox.children.addAll(okButton)
        val layout = VBox(10.0)
        layout.padding = Insets(5.0)
        layout.children.addAll(label, hbox)
        layout.alignment = Pos.CENTER
        window.scene = Scene(layout)
    }
    
    fun showAndWait() = window.showAndWait()
}