package io.shodo.asgard.api;
// TODO 7: REST API - storm
//
//import io.shodo.asgard.service.Thing;
//import io.smallrye.common.annotation.Blocking;
//import io.smallrye.mutiny.Multi;
//import jakarta.ws.rs.BadRequestException;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.sse.OutboundSseEvent;
//import jakarta.ws.rs.sse.Sse;
//
//@Path("/api/thing")
//public class ThingResource {
//
//    private final Thing thing;
//
//    public ThingResource(Thing thing) {
//        this.thing = thing;
//    }
//
//    private static boolean blank(String text) {
//        return text == null || text.isBlank();
//    }
//
//    /** Streams one round of the brainstorm as server-sent events, see {@link ThingEvent}. */
//    @POST
//    @Blocking
//    @Path("/storm")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.SERVER_SENT_EVENTS)
//    public Multi<OutboundSseEvent> start(StartRequest request, @Context Sse sse) {
//        if (request == null || request.question() == null || request.question().isBlank()) {
//            throw new BadRequestException("question is required");
//        }
//        int round = request.roundOrFirst();
//        if (round > 1 && (blank(request.picks()) || blank(request.push()))) {
//            throw new BadRequestException("picks and push are required from round 2 on");
//        }
//        return thing.start(request.question(), round, request.picks(), request.push())
//                .map(event -> sse.newEventBuilder()
//                        .name(event.type())
//                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
//                        .data(ThingEvent.class, event)
//                        .build());
//    }
//}
