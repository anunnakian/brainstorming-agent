package io.shodo.asgard.service;
// TODO 6: planner - the Thing runs the brainstorming session
//
//import io.shodo.asgard.agent.BrainStormingAgent;
//import io.shodo.asgard.api.ThingEvent;
//import io.smallrye.mutiny.Multi;
//import io.smallrye.mutiny.infrastructure.Infrastructure;
//import jakarta.enterprise.context.ApplicationScoped;
//
//import java.util.UUID;
//
//import static io.shodo.asgard.agent.God.*;
//
///**
// * Debate where Odin opens with a canned line before the session, and every other turn is
// * streamed live by the agents' {@link GodListeners} while the six hats session runs.
// */
//@ApplicationScoped
//public class Thing {
//
//    BrainStormingAgent brainStormingAgent;
//    ThingStreams streams;
//
//    public Thing(
//            BrainStormingAgent brainStormingAgent,
//            ThingStreams streams) {
//        this.brainStormingAgent = brainStormingAgent;
//        this.streams = streams;
//    }
//
//    public Multi<ThingEvent> start(String question) {
//        return start(question, 1, null, null);
//    }
//
//    /**
//     * One round of the brainstorm. Round 1 is the session on the question itself; from round 2 on, the gods hear the
//     * question, Odin's harvest of the last round and what the mortal wants to push, and Odin reopens instead of opening.
//     * Each round is a debate of its own, with its own id, on the same question.
//     */
//    public Multi<ThingEvent> start(String question, int round, String harvest, String push) {
//        boolean nextRound = round > 1;
//        String prompt = nextRound ? roundPrompt(question, round, harvest, push) : question;
//        Debate debate = new Debate(UUID.randomUUID().toString(), question);
//
//        Multi<ThingEvent> live = Multi.createFrom().emitter(emitter -> {
//            streams.open(debate.getId(), emitter);
//            Infrastructure.getDefaultWorkerPool().execute(() -> {
//                try {
//                    debate.conclude(brainStormingAgent.answer(prompt, debate.getId()));
//                    emitter.complete();
//                } catch (Exception e) {
//                    emitter.fail(e);
//                } finally {
//                    streams.close(debate.getId());
//                }
//            });
//        });
//        return Multi.createBy().concatenating().streams(
//                        Multi.createFrom().item(() -> ThingEvent.debateStart(debate.getId(), question)),
//                        nextRound ? debate.reopen(round, push) : debate.turn(ODIN, 1),
//                        live,
//                        Multi.createFrom().item(debate::done))
//                .onFailure().recoverWithItem(t -> ThingEvent.error(null, t.getMessage()));
//    }
//
//    /** What the gods hear from round 2 on: the question, Odin's last harvest and what the mortal wants to push. */
//    static String roundPrompt(String question, int round, String harvest, String push) {
//        return """
//                %s
//
//                This is round %d of the brainstorm. Odin's picks from the last round:
//                %s
//
//                The mortal wants to push this further: %s
//                Build on Odin's picks: bring ideas that take them further. Do not repeat ideas already picked.
//                Hel alone brings no ideas and warns of the dangers in what the mortal wants to push."""
//                .formatted(question.strip(), round, harvest.strip(), push.strip());
//    }
//}
