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
            val customer = Customer(result["cName"]!!, result["Email"]!!, result["Address1"]!!, result["Address2"] ?: "", result["City"]!!, result["State"]!!,
                    result["ZipCode"]!!.toInt(), result["Phone"]!!, result["Gender"]!!, result["Income"]!!.toInt())
            customer.id = result["CustomerID"]!!.toLong()
            return customer
        }
        
        fun create(name: String, email: String, address1: String, address2: String, city: String, state: String, zipCode: Int, phone: String, gender: String, income: Int): Customer
        {
            val customer = Customer(name, email, address1, address2, city, state, zipCode, phone, gender, income)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into CUSTOMERS(CNAME, EMAIL, ADDRESS1, ADDRESS2, CITY, STATE, ZIPCODE, PHONE, GENDER, INCOME) values ('$name', '$email', '$address1', '$address2', '$city', '$state', $zipCode, '$phone', '$gender', $income)")
            val result = statement.executeQuery("select CUSTOMERID from CUSTOMERS where EMAIL = '$email'")
            result.next()
            customer.id = result["CustomerId"]!!.toLong()
            return customer
        }
    }
    
    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update CUSTOMERS set CNAME = '$name', EMAIL = '$email', ADDRESS1 = '$address1', ADDRESS2 = '$address2', CITY = '$city', STATE = '$state', ZIPCODE = '$zipCode', PHONE = '$phone', GENDER = '$gender', INCOME = '$income' where CUSTOMERID = $id")
    }
}

class Brand private constructor(val name: String, val company: String)
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

class VehicleSale private constructor(var dealerID: Short, var vehicleID: Short, var customerID: Short, var saleDate: String)
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

        fun create(dealerID: Short, vehicleID: Short, customerID: Short, saleDate: String): VehicleSale
        {
            val type = VehicleSale(dealerID, vehicleID, customerID, saleDate)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into VEHICLESALES(VEHICLEID, DEALERID, CUSTOMERID, SALEDATE) values ($vehicleID, $dealerID, $customerID, '$saleDate')")
            val result = statement.executeQuery("select SaleID from VEHICLESALES where VEHICLEID = '$vehicleID' and DEALERID = '$dealerID' and SALEDATE = '$saleDate' and CUSTOMERID = '$customerID'")
            result.next()
            type.id = result["SaleID"]!!.toInt()
            return type
        }
    }

    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLESALES set VEHICLEID = $vehicleID, DEALERID = $dealerID, CUSTOMERID = $customerID, SALEDATE = '$saleDate' where SALEID = $id")
    }
}
class Vehicle private constructor(var vin: String, var vTypeID: Short, var engine: String, var transmission: String, var vColor: String, var vYear: Short)
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

        fun create(vin: String, vTypeID: Short, engine: String, transmission: String, vColor: String, vYear: Short): Vehicle
        {
            val type = Vehicle(vin, vTypeID, engine, transmission, vColor, vYear)
            val statement = connection.createStatement()
            statement.executeUpdate("insert into VEHICLES(VIN, VTYPEID, ENGINE, TRANSMISSION, VCOLOR, VYEAR) values ('$vin', $vTypeID, '$engine', '$transmission', '$vColor', $vYear)")
            val result = statement.executeQuery("select VEHICLEID from VEHICLES where VIN = '$vin' and VTYPEID = '$vTypeID' and ENGINE = '$engine' and TRANSMISSION = '$transmission' and VCOLOR = '$vColor' and VYEAR = '$vYear'")
            result.next()
            type.id = result["VehicleID"]!!.toInt()
            return type
        }
    }

    fun updateDatabase()
    {
        val statement = connection.createStatement()
        statement.executeUpdate("update VEHICLES set VIN = '$vin', VTYPEID = $vTypeID, ENGINE = '$engine', TRANSMISSION = '$transmission', VCOLOR = '$vColor', VYEAR = $vYear where VEHICLEID = $id")
    }
}
class Dealer private constructor(val name: String)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Dealer>()
    {
        override fun get(result: ResultSet): Dealer
        {
            val type = Dealer(result["dName"]!!)
            type.id = result["DealerID"]!!.toInt()
            return type
        }
    }
}
class Factory private constructor(val name: String, val partID: Short)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Factory>()
    {
        override fun get(result: ResultSet): Factory
        {
            val type = Factory(result["fName"]!!, result["PartID"]!!.toShort())
            type.id = result["FactoryID"]!!.toInt()
            return type
        }
    }
}
class Part private constructor(val name: String, val vTypeID: Short)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Part>()
    {
        override fun get(result: ResultSet): Part
        {
            val type = Part(result["pName"]!!, result["VTypeID"]!!.toShort())
            type.id = result["PartID"]!!.toInt()
            return type
        }
    }
}
class Suppliers private constructor(val name: String, val partID: Long)
{
    var id: Int = 0
        private set

    companion object: DBCompanion<Suppliers>(){
        override fun get(result: ResultSet): Suppliers
        {
            val type = Suppliers(result["sName"]!!, result["PartID"]!!.toLong())
            type.id = result["SupplierID"]!!.toInt()
            return type
        }
    }
}
