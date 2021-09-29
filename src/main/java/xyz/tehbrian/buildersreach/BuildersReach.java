package xyz.tehbrian.buildersreach;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.tehlib.paper.TehPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.tehbrian.buildersreach.command.BuildersReachCommand;
import xyz.tehbrian.buildersreach.command.CommandService;
import xyz.tehbrian.buildersreach.config.ConfigConfig;
import xyz.tehbrian.buildersreach.config.LangConfig;
import xyz.tehbrian.buildersreach.highlight.BlockHighlightingTask;
import xyz.tehbrian.buildersreach.highlight.MagmaCubeHighlighter;
import xyz.tehbrian.buildersreach.inject.ConfigModule;
import xyz.tehbrian.buildersreach.inject.PluginModule;
import xyz.tehbrian.buildersreach.inject.UserModule;
import xyz.tehbrian.buildersreach.listeners.PlayerListener;

public final class BuildersReach extends TehPlugin {

    private @MonotonicNonNull Injector injector;

    @Override
    public void onEnable() {
        try {
            this.injector = Guice.createInjector(
                    new ConfigModule(),
                    new PluginModule(this),
                    new UserModule()
            );
        } catch (final Exception e) {
            this.getLog4JLogger().error("Something went wrong while creating the Guice injector.");
            this.getLog4JLogger().error("Disabling plugin.");
            this.disableSelf();
            this.getLog4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return;
        }

        this.loadConfigs();
        this.setupListeners();
        this.setupCommands();
        this.setupTasks();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
    }

    /**
     * Loads the various plugin config files.
     */
    public void loadConfigs() {
        this.saveResourceSilently("config.yml");
        this.saveResourceSilently("lang.yml");

        this.injector.getInstance(ConfigConfig.class).load();
        this.injector.getInstance(LangConfig.class).load();
    }

    public void setupListeners() {
        registerListeners(
                this.injector.getInstance(PlayerListener.class)
        );
    }

    public void setupCommands() {
        final @NonNull CommandService commandService = this.injector.getInstance(CommandService.class);
        commandService.init();

        final cloud.commandframework.paper.@Nullable PaperCommandManager<CommandSender> commandManager = commandService.get();
        if (commandManager == null) {
            this.getLog4JLogger().error("The CommandService was null after initialization!");
            this.getLog4JLogger().error("Disabling plugin.");
            this.disableSelf();
            return;
        }

        this.injector.getInstance(BuildersReachCommand.class).register(commandManager);
    }

    public void setupTasks() {
        final var blockHighlightingTask = this.injector.getInstance(BlockHighlightingTask.class);
        blockHighlightingTask.setHighlighter(this.injector.getInstance(MagmaCubeHighlighter.class));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, blockHighlightingTask, 1, 1);
    }

}
