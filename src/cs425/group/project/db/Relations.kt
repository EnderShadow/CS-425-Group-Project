package cs425.group.project.db

import cs425.group.project.connection
import java.sql.ResultSet

operator fun ResultSet.get(key: String): String? = getString(key)

class Customer private constructor(val name: String, val email: String, val address1: String, val address2: String = "", val city: String, val state: String, val zipCode: Int, val phone: String, val gender: String, val income: Int)
{
    // This is only ever 0 when the object is initially created. It is set to a non-zero value after immediately after.
    // See Customer.get and Customer.create
    var id: Long = 0
        private set
    
    companion object
    {
        private fun get(result: ResultSet): Customer
        {
            val customer = Customer(result["Name"]!!, result["Email"]!!, result["Address1"]!!, result["Address2"] ?: "", result["City"]!!, result["State"]!!,
                    result["ZipCode"]!!.toInt(), result["Phone"]!!, result["Gender"]!!, result["Income"]!!.toInt())
            customer.id = result["CustomerID"]!!.toLong()
            return customer
        }
        
        fun getAll(result: ResultSet): List<Customer>
        {
            val customers = mutableListOf<Customer>()
            while(result.next())
                customers.add(get(result))
            return customers
        }
        
        fun create(name: String, email: String, address1: String, address2: String, city: String, state: String, zipCode: Int, phone: String, gender: String, income: Int): Customer
        {
            val customer = Customer(name, email, address1, address2, city, state, zipCode, phone, gender, income)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into CUSTOMERS(NAME, EMAIL, ADDRESS1, ADDRESS2, CITY, STATE, ZIPCODE, PHONE, GENDER, INCOME) values ('$name', '$email', '$address1', '$address2', '$city', '$state', $zipCode, '$phone', '$gender', $income)")
            val result = statement.executeQuery("select CUSTOMERID from CUSTOMERS where EMAIL = '$email'")
            result.next()
            customer.id = result["CustomerId"]!!.toLong()
            return customer
        }
    }
    
    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update CUSTOMERS set NAME = '$name', EMAIL = '$email', ADDRESS1 = '$address1', ADDRESS2 = '$address2', CITY = '$city', STATE = '$state', ZIPCODE = '$zipCode', PHONE = '$phone', GENDER = '$gender', INCOME = '$income' where CUSTOMERID = '$id'")
    }
}