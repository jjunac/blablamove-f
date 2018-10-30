package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

@JsonRpcService("/drop")
public interface PackageDropped {
    boolean computePoints(@JsonRpcParam(value = "username") Mission mission);
}
