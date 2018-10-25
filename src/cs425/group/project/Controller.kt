package cs425.group.project

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.stage.Window

class Controller
{
    lateinit var window: Window
    
    @FXML private lateinit var stackPane: StackPane
    @FXML private lateinit var emailAddressTextField: TextField
    
    fun login()
    {
        // TODO check if email address is valid and account exists
    }
    
    fun createAccount()
    {
        // TODO check if email account is valid and account doesn't exist
    }
    
    fun exit()
    {
        window.hide()
    }
}