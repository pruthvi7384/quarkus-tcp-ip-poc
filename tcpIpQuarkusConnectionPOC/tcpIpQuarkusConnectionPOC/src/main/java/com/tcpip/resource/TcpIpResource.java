package com.tcpip.resource;

import com.tcpip.dto.ErrorRes;
import com.tcpip.service.TcpIpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tcp-ip")
public class TcpIpResource {
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Inject
    TcpIpService tcpIpService;

    @POST
    @Path("/send-request")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response tcpIpSend(String request){
        try {
            String response = tcpIpService.tcpIpProcess(request.trim().replaceAll("[\\n\\r\\t]+", "")).await().indefinitely();
            return Response.status(Response.Status.OK).entity(response).build();
        }catch (Exception e){
            logger.error("Error - ",e);
            ErrorRes errorRes = new ErrorRes();
            errorRes.setErrorCode("500");
            errorRes.setErrorDescription("Something went wrong. More details check logs.");
            return Response.status(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR).entity(errorRes).build();
        }
    }

}
