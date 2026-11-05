package io.shodo.asgard.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Turns every error into an {@link ApiError} served as application/problem+json.
 */
public class ApiErrorMappers {

    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String STORM_PATH = "/api/thing/storm";

    @ServerExceptionMapper
    public Response mapWebApplicationException(WebApplicationException exception, UriInfo uriInfo, Request request) {
        int status = exception.getResponse().getStatus();
        String detail = exception.getMessage() != null ? exception.getMessage() : Response.Status.fromStatusCode(status).getReasonPhrase();
        return problem(status, detail, uriInfo, request);
    }

    @ServerExceptionMapper
    public Response mapJsonProcessingException(JsonProcessingException exception, UriInfo uriInfo, Request request) {
        return problem(400, "Request body is not valid JSON: " + exception.getOriginalMessage(), uriInfo, request);
    }

    @ServerExceptionMapper
    public Response mapUnexpected(Throwable throwable, UriInfo uriInfo, Request request) {
        Log.errorf(throwable, "Unexpected error on %s %s", request.getMethod(), uriInfo.getPath());
        return problem(500, "An unexpected error occurred while processing the request. Retry later or contact support.", uriInfo, request);
    }

    private Response problem(int status, String detail, UriInfo uriInfo, Request request) {
        Response.StatusType statusType = Response.Status.fromStatusCode(status);
        String title = statusType != null ? statusType.getReasonPhrase() : "Error";
        String path = uriInfo.getPath();

        ApiError error = new ApiError(
                "https://httpstatuses.io/" + status,
                title,
                status,
                detail,
                path,
                links(status, path, uriInfo, request));

        return Response.status(status).type(PROBLEM_JSON).entity(error).build();
    }

    private Map<String, ApiError.Link> links(int status, String path, UriInfo uriInfo, Request request) {
        String base = uriInfo.getBaseUri().toString().replaceAll("/$", "");
        Map<String, ApiError.Link> links = new LinkedHashMap<>();
        links.put("self", new ApiError.Link(base + path, request.getMethod(), "The request that failed"));
        if (status >= 500) {
            links.put("retry", new ApiError.Link(base + path, request.getMethod(), "Retry the same request later"));
        }
        links.put("storm", new ApiError.Link(base + STORM_PATH, "POST",
                "Start the Thing on a question with a JSON body like {\"question\": \"...\"}"));
        return links;
    }
}
