package cs425.group.project

import cs425.group.project.db.Customer
import cs425.group.project.db.Dealer
import cs425.group.project.db.get
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.stage.Window
import javafx.util.Callback
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.Callable
import kotlin.reflect.full.memberProperties

private const val DB_URL = "jdbc:oracle:thin:@smb.lobstergaming.com:1521:orclcdb"
private const val USER = "system"
private const val PASS = "oracle"

val connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

val emailRegex = Regex("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$)")

class Controller
{
    lateinit var window: Window
    
    @FXML lateinit var stackPane: StackPane
    
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
                val loader = FXMLLoader(javaClass.getResource("CustData.fxml"))
                rootController.stackPane.children.add(loader.load())
                loader.getController<CustomerDataController>().let {
                    it.rootController = rootController
                    it.customer = customers[0]
                    it.reset()
                }
                
                emailTextField.text = ""
                nameTextField.text = ""
                address1TextField.text = ""
                address2TextField.text = ""
                cityTextField.text = ""
                stateTextField.text = ""
                zipcodeTextField.text = ""
                genderTextField.text = ""
                phoneTextField.text = ""
                incomeTextField.text = ""
            }
            else if(customers.isEmpty())
            {
                AlertBox("Error", "There is no customer with that email address").showAndWait()
            }
        }
        else
        {
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from CUSTOMERS where EMAIL = '${emailTextField.text.toLowerCase()}'")
            val customers = Customer.getAll(result)
    
            if(customers.isEmpty())
            {
                val customer = Customer.create(nameTextField.text, emailTextField.text, address1TextField.text, address2TextField.text, cityTextField.text, stateTextField.text, zipcodeTextField.text.toInt(), phoneTextField.text, genderTextField.text, incomeTextField.text.toInt())
                
                val loader = FXMLLoader(javaClass.getResource("CustData.fxml"))
                rootController.stackPane.children.add(loader.load())
                loader.getController<CustomerDataController>().let {
                    it.rootController = rootController
                    it.customer = customer
                    it.reset()
                }
                
                emailTextField.text = ""
                nameTextField.text = ""
                address1TextField.text = ""
                address2TextField.text = ""
                cityTextField.text = ""
                stateTextField.text = ""
                zipcodeTextField.text = ""
                genderTextField.text = ""
                phoneTextField.text = ""
                incomeTextField.text = ""
                
                loginToggle.isSelected = true
                
            }
            else
            {
                AlertBox("Error", "A customer already exists with that email address").showAndWait()
            }
        }
    }
    
    fun back()
    {
        rootController.stackPane.children.run {removeAt(lastIndex)}
    }
}

class DealerLoginController
{
    lateinit var rootController: Controller
    
    @FXML private lateinit var dealerIDTextField: TextField
    @FXML private lateinit var submitButton: Button


    fun initialize()
    {
        submitButton.disableProperty().bind(Bindings.createBooleanBinding(Callable {!dealerIDTextField.text.matches(Regex("[0-9]+"))}, dealerIDTextField.textProperty()))
    }
    
    fun submit()
    {
        if(submitButton.isPressed)
        {
            val dealerId = dealerIDTextField.text.toIntOrNull() ?: 0
            val statement = connection.createStatement()
            val result = statement.executeQuery("select * from DEALERS where DEALERID = '$dealerId'")
            val dealers = Dealer.getAll(result)
            if(dealers.isEmpty())
            {
                AlertBox("Error", "There is no dealer with that id").showAndWait()
            }
            else
            {
                val dealer = dealers[0]
                // TODO
            }
        }
    }
    
    fun back()
    {
        rootController.stackPane.children.run {removeAt(lastIndex)}
    }
}

class CustomerDataController
{
    lateinit var rootController: Controller
    lateinit var customer: Customer
    
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
    
    @FXML private lateinit var saveButton: Button
    
    fun initialize()
    {
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(Callable {!emailTextField.text.matches(emailRegex)}, emailTextField.textProperty()))
    }
    
    fun reset()
    {
        emailTextField.text = customer.email
        nameTextField.text = customer.name
        address1TextField.text = customer.address1
        address2TextField.text = customer.address2
        cityTextField.text = customer.city
        stateTextField.text = customer.state
        zipcodeTextField.text = customer.zipCode.toString()
        genderTextField.text = customer.gender
        phoneTextField.text = customer.phone
        incomeTextField.text = customer.income.toString()
    }
    
    fun save()
    {
        customer.email = emailTextField.text
        customer.name = nameTextField.text ?: ""
        customer.address1 = address1TextField.text ?: ""
        customer.address2 = address2TextField.text ?: ""
        customer.city = cityTextField.text ?: ""
        customer.state = stateTextField.text ?: ""
        customer.zipCode = zipcodeTextField.text.toIntOrNull() ?: 0
        customer.gender = genderTextField.text ?: ""
        customer.phone = phoneTextField.text ?: ""
        customer.income = incomeTextField.text.toIntOrNull() ?: 0
        
        customer.updateDatabase()
    }
    
    fun viewPurchaseHistory()
    {
        val loader = FXMLLoader(javaClass.getResource("CustPurchase.fxml"))
        rootController.stackPane.children.add(loader.load())
        loader.getController<CustomerPurchaseController>().let {
            it.rootController = rootController
            it.customer = customer
            it.fill()
        }
    }
    
    fun logout()
    {
        rootController.stackPane.children.run {removeAt(lastIndex)}
    }
}

class CustomerPurchaseController
{
    lateinit var rootController: Controller
    lateinit var customer: Customer
    
    @FXML private lateinit var purchaseTableView: TableView<Purchase>
    
    fun initialize()
    {
        purchaseTableView.columns.forEach {col ->
            col.cellValueFactory = Callback {param: TableColumn.CellDataFeatures<Purchase, out Any> ->
                SimpleObjectProperty(param.value::class.java.methods.first {it.name.endsWith(param.tableColumn.text.replace(" ", ""), true)}.invoke(param.value))
            }
        }
    }
    
    fun fill()
    {
        purchaseTableView.items.addAll(Purchase.getPurchases(customer))
    }
    
    fun back()
    {
        rootController.stackPane.children.run {removeAt(lastIndex)}
    }
    
    private class Purchase private constructor(val dealer: String, val saleDate: String, val vin: String, val trim: String, val model: String, val brand: String, val company: String, val transmission: String, val color: String, val year: Short)
    {
        companion object
        {
            fun getPurchases(customer: Customer): List<Purchase>
            {
                val purchases = mutableListOf<Purchase>()
                
                val statement = connection.createStatement()
                val result = statement.executeQuery("select * from (VEHICLESALES natural join CUSTOMERS natural join DEALERS natural join VEHICLES natural join VEHICLETYPES natural join BRANDS) where CUSTOMERID = '${customer.id}'")
                
                while(result.next())
                {
                    purchases.add(Purchase(result["dName"]!!, result["SaleDate"]!!, result["VIN"]!!, result["Trim"]!!, result["VModel"]!!, result["BrandName"]!!, result["Company"]!!, result["Transmission"]!!, result["VColor"]!!, result["VYear"]!!.toShort()))
                }
                
                return purchases
            }
        }
    }
}