package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

@JsonRpcService("/package")
public interface PackageService {
    // TODO why not drop package ?
    boolean computePoints(@JsonRpcParam(value = "mission") long missionId);
    boolean answerToPendingMission(@JsonRpcParam(value = "missionId") long missionId,
                                   @JsonRpcParam(value = "username") String username,
                                   @JsonRpcParam(value="answer") boolean answer);

    void takePackage(@JsonRpcParam(value = "missionId") long missionId,
                        @JsonRpcParam(value = "username") String username);


}
