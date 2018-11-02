package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

@JsonRpcService("/incident")
public interface IncidentService {
    boolean notifyCarCrash(@JsonRpcParam(value = "username") String username,
                           @JsonRpcParam(value = "latitude") double latitude,
                           @JsonRpcParam(value = "longitude") double longitude);
}
