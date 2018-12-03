package cs425.group.project.db

import java.sql.ResultSet

data class Customer(val id: Long, val name: String, val email: String, val address1: String, val address2: String?, val city: String, val state: String, val zipCode: Int, val phone: String, val gender: String, val income: Int)
{
    companion object
    {
        fun create(result: ResultSet) = Customer(result["CustomerID"]!!.toLong(), result["Name"]!!, result["Email"]!!, result["Address1"]!!, result["Address2"],
                result["City"]!!, result["State"]!!, result["ZipCode"]!!.toInt(), result["Phone"]!!, result["Gender"]!!, result["Income"]!!.toInt())
    }
}

operator fun ResultSet.get(key: String): String? = getString(key)