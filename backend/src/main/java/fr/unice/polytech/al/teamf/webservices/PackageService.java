package fr.unice.polytech.al.teamf.webservices;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import fr.unice.polytech.al.teamf.entities.Mission;
import fr.unice.polytech.al.teamf.entities.User;

@JsonRpcService("/package")
public interface PackageService {
    boolean missionFinished(@JsonRpcParam(value = "mission") long missionId);

    boolean answerToPendingMission(@JsonRpcParam(value = "missionId") long missionId,
                                   @JsonRpcParam(value = "username") String username,
                                   @JsonRpcParam(value="answer") boolean answer);
    boolean answerToPendingPackageHosting(@JsonRpcParam(value = "parcelId") long parcelId,
                                   @JsonRpcParam(value = "username") String username,
                                   @JsonRpcParam(value="answer") boolean answer);

    boolean takePackage(@JsonRpcParam(value = "missionId") long missionId,
                        @JsonRpcParam(value = "username") String username);

    boolean dropPackageToHost(@JsonRpcParam(value = "parcelId") long parcelId,
                     @JsonRpcParam(value = "username") String username);

    boolean takePackageFromHost(@JsonRpcParam(value = "parcelId") long parcelId,
                           @JsonRpcParam(value = "username") String username);
    long getPackageMissionId(@JsonRpcParam(value = "parcelId") long parcelId);
}
