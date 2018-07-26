/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.pizza.service;

import com.google.gson.JsonObject;
import org.wso2.pizza.service.dto.PizzaDTO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 0.1-SNAPSHOT
 */

@Path("/pizzaservice")
public class PizzaService {

    @POST
    @Produces("application/json")
    @Path("/getpizza")
    public Response getMenu(JsonObject pizzaDetails) {
        // Just put a log so that we can ensure this got invoked.
        System.out.println(pizzaDetails);
        System.out.println("GET invoked for Get Menu " + pizzaDetails);
        JsonObject pizzaObject = pizzaDetails.getAsJsonObject("pizza");
        String pizzaId = pizzaObject.get("id").getAsString();

        return Response.ok().entity(PizzaDTO.getPizza(pizzaId)).build();
    }

    @POST
    @Path("/orderpizza")
    public Response post(JsonObject orderDetailsJson) {
        System.out.println("GET invoked with data : " + orderDetailsJson);
        // Do whatever the process you need to do with this post invocation
        // For demo purpose I will just put a log to ensure this has been invoked.
        System.out.println("GET invoked with data : " + orderDetailsJson);
        return Response.ok().entity("PizzaDTO ordered !!!").build();
    }
}