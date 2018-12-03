package cs425.group.project

import cs425.group.project.db.Customer
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.stage.Window
import java.sql.Connection
import java.sql.DriverManager

private const val DB_URL = "jdbc:oracle:thin:@smb.lobstergaming.com:1521:orclcdb"
private const val USER = "system"
private const val PASS = "oracle"

val connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

val emailRegex = Regex("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$)")

class Controller
{
    lateinit var window: Window
    
    @FXML private lateinit var stackPane: StackPane
    @FXML private lateinit var emailAddressTextField: TextField
    
    fun login()
    {
        if(emailAddressTextField.text.matches(emailRegex))
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailAddressTextField.text.toLowerCase()}'")
            val customers = Customer.getAll(result)
            
            if(customers.size == 1)
            {
                val customer = customers[0]
                // TODO do something with this customer since they exist
                println("Found customer: $customer")
            }
            else if(customers.isEmpty())
            {
                // No one has this email
                // TODO do something
                println("There is no customer for email: ${emailAddressTextField.text}")
            }
        }
        // TODO email is not valid
    }
    
    fun createAccount()
    {
        if(emailAddressTextField.text.matches(emailRegex))
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailAddressTextField.text.toLowerCase()}'")
            val customers = Customer.getAll(result)
            
            if(customers.isEmpty())
            {
                // TODO create Customer object, fill in data, and then call updateDatabase
            }
            else
            {
                // TODO customer already exists so can't make a new one
                println("Customer already exists with email: ${emailAddressTextField.text}")
            }
        }
        // TODO email is not valid
    }
    
    fun exit()
    {
        window.hide()
        connection.close()
    }
}