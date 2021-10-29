package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

import java.util.Set;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class TournamentServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(TournamentServiceCommandHandlers.class);

    @Autowired
    private TournamentProvidedService tournamentProvidedService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
                .onMessage(UndoUpdateTournamentCommand.class, this::undoUpdate)
                .onMessage(ConfirmUpdateTournamentQuizCommand.class, this::confirmUpdate)
                .onMessage(ConfirmCreateTournamentCommand.class, this::confirmCreate)
                .onMessage(RejectCreateTournamentCommand.class, this::rejectCreate)
                .onMessage(StoreTournamentTopicsCommand.class, this::storeTopics)
                .onMessage(StoreTournamentCourseExecutionCommand.class, this::storeCourseExecution)
                .onMessage(UpdateTopicsTournamentCommand.class, this::updateTopics)
                .onMessage(UndoUpdateTopicsTournamentCommand.class, this::undoUpdateTopics)
                .onMessage(BeginUpdateTournamentCommand.class, this::beginUpdateTournament)
                .onMessage(BeginRemoveTournamentCommand.class, this::beginRemoveTournament)
                .onMessage(ConfirmRemoveTournamentCommand.class, this::confirmRemoveTournament)
                .onMessage(UndoRemoveTournamentCommand.class, this::undoRemoveTournament)
                .build();
    }

    private Message undoRemoveTournament(CommandMessage<UndoRemoveTournamentCommand> cm) {
        logger.info("Received UndoRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.undoRemoveTournament(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmRemoveTournament(CommandMessage<ConfirmRemoveTournamentCommand> cm) {
        logger.info("Received ConfirmRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.confirmRemoveTournament(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message beginRemoveTournament(CommandMessage<BeginRemoveTournamentCommand> cm) {
        logger.info("Received BeginRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.beginRemoveTournament(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message beginUpdateTournament(CommandMessage<BeginUpdateTournamentCommand> cm) {
        logger.info("Received BeginUpdateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.beginUpdateTournament(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message undoUpdateTopics(CommandMessage<UndoUpdateTopicsTournamentCommand> cm) {
        logger.info("Received UndoUpdateTopicsTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> topics = cm.getCommand().getOldTopics();

        try {
            tournamentProvidedService.undoUpdateTopics(tournamentId, topics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message updateTopics(CommandMessage<UpdateTopicsTournamentCommand> cm) {
        logger.info("Received UpdateTopicsTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> topics = cm.getCommand().getTournamentTopics();

        try {
            tournamentProvidedService.updateTopics(tournamentId, topics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message storeCourseExecution(CommandMessage<StoreTournamentCourseExecutionCommand> cm) {
        logger.info("Received StoreTournamentCourseExecutionCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        TournamentCourseExecution tournamentCourseExecution = cm.getCommand().getTournamentCourseExecution();

        try {
            tournamentProvidedService.storeCourseExecution(tournamentId, tournamentCourseExecution);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message storeTopics(CommandMessage<StoreTournamentTopicsCommand> cm) {
        logger.info("Received StoreTournamentTopicsCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> tournamentTopics = cm.getCommand().getTournamentTopics();

        try {
            tournamentProvidedService.storeTopics(tournamentId, tournamentTopics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message rejectCreate(CommandMessage<RejectCreateTournamentCommand> cm) {
        logger.info("Received RejectCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.rejectCreate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmCreate(CommandMessage<ConfirmCreateTournamentCommand> cm) {
        logger.info("Received ConfirmCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Integer quizId = cm.getCommand().getQuizId();

        try {
            tournamentProvidedService.confirmCreate(tournamentId, quizId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmUpdate(CommandMessage<ConfirmUpdateTournamentQuizCommand> cm) {
        logger.info("Received ConfirmUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        TournamentDto tournamentDto = cm.getCommand().getTournamentDto();

        try {
            tournamentProvidedService.confirmUpdate(tournamentId, tournamentDto);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message undoUpdate(CommandMessage<UndoUpdateTournamentCommand> cm) {
        logger.info("Received UndoUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentProvidedService.undoUpdate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

}
