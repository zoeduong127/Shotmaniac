package shotmaniacs.group2.di.resources;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;

public class AdministratorsResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static String host = "bronto.ewi.utwente.nl";
    private static String dbName ="dab_dsgnprj_50";
    private static String url = "jdbc:postgresql://" + host + ":5432/" +dbName+"?currentSchema=dab_dsgnprj_50";
    private static String password = "yummybanana";


}
