package org.external.customer.mgt.service;

import com.google.gson.JsonObject;
import org.external.customer.mgt.models.Customer;

import javax.ws.rs.*;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


@Path("/services")
public class CustomerService {
    private static Map<String, Customer> customerMap = new HashMap<String, Customer>();

    @POST
    @Path("/customer")
    @Consumes("application/json")
    @Produces("application/json")
    public JsonObject createLoanRequest(Customer customer) {
        String uniqueID = UUID.randomUUID().toString();
        String customerType = "Premium";
        //Random code to switch between a normal and premium customer for the demo purpose.
        if (Character.isDigit(uniqueID.charAt(uniqueID.length()-1)) && (Character.getNumericValue(uniqueID.charAt(uniqueID.length()-1))< 5)) {
            customerType = "Normal";
        }
        customerMap.put(customer.customerName, customer);
        JsonObject response = new JsonObject();
        System.out.println("Customer "+ customer.customerName+ " added with id " + uniqueID);
        response.addProperty("status","Success");
        response.addProperty("customerName", customer.customerName);
        response.addProperty("externalCustomerId", uniqueID);
        response.addProperty("customerType", customerType);
        return response;
    }

    @GET
    @Path("customer/{customerName}")
    @Consumes("application/json")
    @Produces("application/json")
    public JsonObject getCustomerDetails(@PathParam("customerName") String customerName) {
        JsonObject response = new JsonObject();
        if (customerMap.containsKey(customerName)) {
            Customer customer = customerMap.get(customerName);

            response.addProperty("customerName", customer.getCustomerName());
            response.addProperty("phone", customer.getPhoneNumber());
            response.addProperty("address", customer.getAddress());
            response.addProperty("country", customer.getCountry());
        } else {
            response.addProperty("message","Requested customer " + customerName + " does not exist");
        }
        return response;
    }
}
