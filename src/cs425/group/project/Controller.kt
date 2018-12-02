package cs425.group.project

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.stage.Window
import java.sql.DriverManager
import java.sql.ResultSet

private const val DB_URL = "jdbc:oracle:thin:@localhost:1521:orclcdb"
private const val USER = "system"
private const val PASS = "oracle"

val emailRegex = Regex("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$)")

class Controller
{
    lateinit var window: Window
    
    @FXML private lateinit var stackPane: StackPane
    @FXML private lateinit var emailAddressTextField: TextField
    
    private val connection = DriverManager.getConnection(DB_URL, USER, PASS)
    
    fun login()
    {
        if(emailAddressTextField.text.matches(emailRegex))
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailAddressTextField.text.toLowerCase()}'")
            val customers = mutableListOf<Customer>()
            while(result.next())
                customers.add(Customer.create(result))
            
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

data class Customer(val id: Long, val name: String, val email: String, val address1: String, val address2: String?, val city: String, val state: String, val zipCode: Int, val phone: String, val gender: String, val income: Int)
{
    companion object
    {
        fun create(result: ResultSet) = Customer(result.getLong("CustomerID"), result.getString("Name"),
                result.getString("Email"), result.getString("Address1"), result.getString("Address2"),
                result.getString("City"), result.getString("State"), result.getInt("ZipCode"),
                result.getString("Phone"), result.getString("Gender"), result.getInt("Income"))
    }
}