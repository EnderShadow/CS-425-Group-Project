package cs425.group.project

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

fun main(args: Array<String>)
{
    Application.launch(Launcher::class.java, *args)
}

class Launcher: Application()
{
    override fun start(primaryStage: Stage)
    {
        val loader = FXMLLoader(javaClass.getResource("GUI.fxml"))
        val root: Parent = loader.load()
        primaryStage.title = "CS 425 Database Project"
        primaryStage.scene = Scene(root)
        val controller: Controller = loader.getController()
        controller.window = primaryStage
        
        primaryStage.setOnCloseRequest {
            it.consume()
            controller.exit()
        }
        primaryStage.show()
    }
}