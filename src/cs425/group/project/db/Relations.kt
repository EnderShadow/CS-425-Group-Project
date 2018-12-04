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
            type.id = result["VehicleID"]!!.toInt()
            return type
        }
    }
    
    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLETYPES set TRIM = '$trim', BRANDID = $brandID, VMODEL = '$vModel' where VTYPEID = $id")
    }
}

class VehicleSale private constructor(var DealerID: Short, var VehicleID: Short, var CustomerID: Short, var SaleDate: String)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<VehicleSale>()
    {
        override fun get(result: ResultSet): VehicleSale
        {
            val type = VehicleSale(result["DealerID"]!!.toShort(), result["VehicleID"]!!.toShort(), result["CustomerID"]!!.toShort(), result["SaleDate"]!!)
            type.id = result["SaleID"]!!.toInt()
            return type
        }

        fun create(DealerID: Short, VehicleID: Short, CustomerID: Short, SaleDate: String): VehicleSale
        {
            val type = VehicleSale(DealerID, VehicleID, CustomerID, SaleDate)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into VEHICLESALES(VEHICLEID, DEALERID, CUSTOMERID, SALEDATE) values ($VehicleID, $DealerID, $CustomerID, '$SaleDate')")
            val result = statement.executeQuery("select SaleID from VEHICLESALES where VEHICLEID = '$VehicleID' and DEALERID = '$DealerID' and SALEDATE = '$SaleDate' and CUSTOMERID = '$CustomerID'")
            result.next()
            type.id = result["SaleID"]!!.toInt()
            return type
        }
    }

    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLESALES set VEHICLEID = $VehicleID, DEALERID = $DealerID, CUSTOMERID = $CustomerID, SALEDATE = '$SaleDate' where SALEID = $id")
    }
}
class Vehicle private constructor(var VIN: String, var VTypeID: Short, var Engine: String, var Transmission: String, var VColor: String, var VYear: Short)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Vehicle>()
    {
        override fun get(result: ResultSet): Vehicle
        {
            val type = Vehicle(result["VIN"]!!, result["VTypeID"]!!.toShort(), result["Engine"]!!, result["Transmission"]!!, result["VColor"]!!, result["VYear"]!!.toShort())
            type.id = result["VehicleID"]!!.toInt()
            return type
        }

        fun create(VIN: String, VTypeID: Short, Engine: String, Transmission: String, VColor: String, VYear: Short): Vehicle
        {
            val type = Vehicle(VIN, VTypeID, Engine, Transmission, VColor, VYear)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into VEHICLES(VIN, VTYPEID, ENGINE, TRANSMISSION, VCOLOR, VYEAR) values ('$VIN', $VTypeID, '$Engine', '$Transmission', '$VColor', $VYear)")
            val result = statement.executeQuery("select VEHICLEID from VEHICLES where VIN = '$VIN' and VTYPEID = '$VTypeID' and ENGINE = '$Engine' and TRANSMISSION = '$Transmission' and VCOLOR = '$VColor' and VYEAR = '$VYear'")
            result.next()
            type.id = result["VehicleID"]!!.toInt()
            return type
        }
    }

    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLES set VIN = '$VIN', VTYPEID = $VTypeID, ENGINE = '$Engine', TRANSMISSION = '$Transmission', VCOLOR = '$VColor', VYEAR = $VYear where VEHICLEID = $id")
    }
}
class Dealer private constructor(var Name: String)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Dealer>()
    {
        override fun get(result: ResultSet): Dealer
        {
            val type = Dealer(result["Name"]!!)
            type.id = result["DealerID"]!!.toInt()
            return type
        }
    }
}
class Factory private constructor(var Name: String, var PartID: Short)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Factory>()
    {
        override fun get(result: ResultSet): Factory
        {
            val type = Factory(result["Name"]!!, result["PartID"]!!.toShort())
            type.id = result["FactoryID"]!!.toInt()
            return type
        }
    }
}
class Part private constructor(var Name: String, var VTypeID: Short)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Part>()
    {
        override fun get(result: ResultSet): Part
        {
            val type = Part(result["Name"]!!, result["VTypeID"]!!.toShort())
            type.id = result["PartID"]!!.toInt()
            return type
        }
    }
}
