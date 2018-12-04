package cs425.group.project

import cs425.group.project.db.Customer
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.stage.Window
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.Callable

private const val DB_URL = "jdbc:oracle:thin:@smb.lobstergaming.com:1521:orclcdb"
private const val USER = "system"
private const val PASS = "oracle"

val connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

val emailRegex = Regex("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$)")

class Controller
{
    lateinit var window: Window
    
    @FXML private lateinit var stackPane: StackPane
    
    fun initialize()
    {
        stackPane.children.addListener(InvalidationListener {_ ->
            stackPane.children.forEach {it.isVisible = false}
            stackPane.children.last().isVisible = true
        })
    }
    
    fun showCustomerLogin()
    {
        val loader = FXMLLoader(javaClass.getResource("CustLogin.fxml"))
        stackPane.children.add(loader.load())
        loader.getController<CustomerLoginController>().rootController = this
    }
    
    fun showDealerLogin()
    {
        val loader = FXMLLoader(javaClass.getResource("DealerLogin.fxml"))
        stackPane.children.add(loader.load())
        loader.getController<DealerLoginController>().rootController = this
    }
    
    fun exit()
    {
        window.hide()
        connection.close()
    }
}

class CustomerLoginController
{
    lateinit var rootController: Controller
    
    @FXML private lateinit var emailTextField: TextField
    @FXML private lateinit var nameTextField: TextField
    @FXML private lateinit var address1TextField: TextField
    @FXML private lateinit var address2TextField: TextField
    @FXML private lateinit var cityTextField: TextField
    @FXML private lateinit var stateTextField: TextField
    @FXML private lateinit var zipcodeTextField: TextField
    @FXML private lateinit var genderTextField: TextField
    @FXML private lateinit var phoneTextField: TextField
    @FXML private lateinit var incomeTextField: TextField
    
    @FXML private lateinit var loginToggle: RadioButton
    @FXML private lateinit var createAccountToggle: RadioButton
    
    @FXML private lateinit var submitButton: Button
    
    fun initialize()
    {
        nameTextField.disableProperty().bind(loginToggle.selectedProperty())
        address1TextField.disableProperty().bind(loginToggle.selectedProperty())
        address2TextField.disableProperty().bind(loginToggle.selectedProperty())
        cityTextField.disableProperty().bind(loginToggle.selectedProperty())
        stateTextField.disableProperty().bind(loginToggle.selectedProperty())
        zipcodeTextField.disableProperty().bind(loginToggle.selectedProperty())
        genderTextField.disableProperty().bind(loginToggle.selectedProperty())
        phoneTextField.disableProperty().bind(loginToggle.selectedProperty())
        incomeTextField.disableProperty().bind(loginToggle.selectedProperty())
        
        loginToggle.selectedProperty().addListener {_, _, new ->
            if(new)
                createAccountToggle.isSelected = false
        }
        createAccountToggle.selectedProperty().addListener {_, _, new ->
            if(new)
                loginToggle.isSelected = false
        }
        
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(Callable {!emailTextField.text.matches(emailRegex)}, emailTextField.textProperty()))
        submitButton.textProperty().bind(Bindings.`when`(loginToggle.selectedProperty()).then("Login").otherwise("Create Account"))
    }
    
    fun submit()
    {
        if(loginToggle.isSelected)
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailTextField.text.toLowerCase()}'")
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
                println("There is no customer for email: ${emailTextField.text}")
            }
        }
        else
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailTextField.text.toLowerCase()}'")
            val customers = Customer.getAll(result)
    
            if(customers.isEmpty())
            {
                // TODO create Customer object, fill in data, and then call updateDatabase
                //creating customer with data from text fields
                val customer = Customer.create(nameTextField.text, emailTextField.text, address1TextField.text, address2TextField.text, cityTextField.text, stateTextField.text, zipcodeTextField.text.toInt(), phoneTextField.text, genderTextField.text, incomeTextField.text.toInt())
                customer.updateDatabase()
            }
            else
            {
                // TODO customer already exists so can't make a new one
                println("Customer already exists with email: ${emailTextField.text}")
            }
        }
    }
}

class DealerLoginController
{
    lateinit var rootController: Controller
    
    @FXML private lateinit var dealerIDTextField: TextField
    @FXML private lateinit var submitButton: Button
    @FXML private lateinit var dealerNameTextField: TextField


    fun initialize()
    {
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(Callable {!dealerIDTextField.text.matches(Regex("[0-9]+"))}, dealerIDTextField.textProperty()))
    }
    
    fun submit()
    {
        if (submitButton.isPressed){

        }
    
    }
}