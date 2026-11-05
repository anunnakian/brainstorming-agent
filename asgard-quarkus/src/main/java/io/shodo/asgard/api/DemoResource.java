package io.shodo.asgard.api;
// TODO 1: agent - a demo endpoint that asks Freyr through TestSession
//
//import io.shodo.asgard.agent.TestSession;
//import io.shodo.asgard.agent.model.AgentAnswer;
//import io.smallrye.common.annotation.Blocking;
//import jakarta.ws.rs.BadRequestException;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.QueryParam;
//import jakarta.ws.rs.core.MediaType;
//
//@Path("/api/demo")
//public class DemoResource {
//
//    private final TestSession session;
//
//    public DemoResource(TestSession session) {
//        this.session = session;
//    }
//
//    @GET
//    @Blocking
//    @Path("/freyr")
//    @Produces(MediaType.APPLICATION_JSON)
//    public AgentAnswer freyr(@QueryParam("prompt") String prompt) {
//        if (prompt == null || prompt.isBlank()) {
//            throw new BadRequestException("prompt is required");
//        }
//        return session.brainstorm(prompt);
//    }
//}
