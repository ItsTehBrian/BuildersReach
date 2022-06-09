package xyz.tehbrian.buildersreach.command;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudService;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.buildersreach.BuildersReach;

import java.util.function.Function;

public class CommandService extends PaperCloudService<CommandSender> {

    private final BuildersReach buildersReach;

    @Inject
    public CommandService(
            final @NonNull BuildersReach buildersReach
    ) {
        this.buildersReach = buildersReach;
    }

    /**
     * Instantiates {@link #commandManager}.
     *
     * @throws IllegalStateException if {@link #commandManager} is already instantiated
     * @throws Exception             if something goes wrong during instantiation
     */
    public void init() throws Exception {
        if (this.commandManager != null) {
            throw new IllegalStateException("The CommandManager is already instantiated.");
        }

        this.commandManager = new PaperCommandManager<>(
                this.buildersReach,
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );
    }

}
