package cs425.group.project.db

import cs425.group.project.connection
import java.sql.ResultSet

operator fun ResultSet.get(key: String): String? = getString(key)

abstract class DBCompanion<T>
{
    protected abstract fun get(result: ResultSet): T
    
    fun getAll(result: ResultSet): List<T>
    {
        val list = mutableListOf<T>()
        while(result.next())
            list.add(get(result))
        return list
    }
}

class Customer private constructor(var name: String, var email: String, var address1: String, var address2: String = "", var city: String, var state: String, var zipCode: Int, var phone: String, var gender: String, var income: Int)
{
    // This is only ever 0 when the object is initially created. It is set to a non-zero value after immediately after.
    // See Customer.get and Customer.create
    var id: Long = 0
        private set
    
    companion object: DBCompanion<Customer>()
    {
        override fun get(result: ResultSet): Customer
        {
            val customer = Customer(result["Name"]!!, result["Email"]!!, result["Address1"]!!, result["Address2"] ?: "", result["City"]!!, result["State"]!!,
                    result["ZipCode"]!!.toInt(), result["Phone"]!!, result["Gender"]!!, result["Income"]!!.toInt())
            customer.id = result["CustomerID"]!!.toLong()
            return customer
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
        statement.executeUpdate("update CUSTOMERS set NAME = '$name', EMAIL = '$email', ADDRESS1 = '$address1', ADDRESS2 = '$address2', CITY = '$city', STATE = '$state', ZIPCODE = '$zipCode', PHONE = '$phone', GENDER = '$gender', INCOME = '$income' where CUSTOMERID = $id")
    }
}

class Brand private constructor(var name: String, var company: String)
{
    var id: Short = 0
        private set
    
    companion object: DBCompanion<Brand>()
    {
        override fun get(result: ResultSet): Brand
        {
            val brand = Brand(result["BrandName"]!!, result["Company"]!!)
            brand.id = result["BrandID"]!!.toShort()
            return brand
        }
        
        fun create(name: String, company: String): Brand
        {
            val brand = Brand(name, company)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into BRANDS(BRANDNAME, COMPANY) values ('$name', '$company')")
            val result = statement.executeQuery("select BRANDID from BRANDS where BRANDNAME = '$name' and COMPANY = '$company'")
            result.next()
            brand.id = result["BrandID"]!!.toShort()
            return brand
        }
    }
    
    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update BRANDS set BRANDNAME = '$name', COMPANY = '$company' where BRANDID = $id")
    }
}

class VehicleType private constructor(var trim: String, var brandID: Short, var vModel: String)
{
    var id: Int = 0
        private set
    
    companion object: DBCompanion<VehicleType>()
    {
        override fun get(result: ResultSet): VehicleType
        {
            val type = VehicleType(result["Trim"]!!, result["BrandID"]!!.toShort(), result["VModel"]!!)
            type.id = result["VTypeID"]!!.toInt()
            return type
        }
    
        fun create(trim: String, brandID: Short, vModel: String): VehicleType
        {
            val type = VehicleType(trim, brandID, vModel)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into VEHICLETYPES(TRIM, BRANDID, VMODEL) values ('$trim', $brandID, '$vModel')")
            val result = statement.executeQuery("select VTypeID from VEHICLETYPES where TRIM = '$trim' and BRANDID = '$brandID' and VMODEL = '$vModel'")
            result.next()
            type.id = result["VehcileID"]!!.toInt()
            return type
        }
    }
    
    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLETYPES set TRIM = '$trim', BRANDID = $brandID, VMODEL = '$vModel' where VTYPEID = $id")
    }
}