package com.craftmend.openaudiomc.spigot;

import com.craftmend.openaudiomc.OpenAudioMc;
import com.craftmend.openaudiomc.generic.core.interfaces.ConfigurationImplementation;
import com.craftmend.openaudiomc.generic.core.interfaces.ITaskProvider;
import com.craftmend.openaudiomc.generic.core.interfaces.OpenAudioInvoker;
import com.craftmend.openaudiomc.generic.core.logging.OpenAudioLogger;
import com.craftmend.openaudiomc.generic.networking.interfaces.INetworkingService;
import com.craftmend.openaudiomc.generic.platform.Platform;
import com.craftmend.openaudiomc.generic.state.states.WorkerState;
import com.craftmend.openaudiomc.spigot.modules.commands.SpigotCommandModule;
import com.craftmend.openaudiomc.generic.state.states.IdleState;
import com.craftmend.openaudiomc.spigot.modules.configuration.SpigotConfigurationImplementation;
import com.craftmend.openaudiomc.spigot.modules.proxy.ProxyModule;
import com.craftmend.openaudiomc.spigot.modules.proxy.enums.ClientMode;
import com.craftmend.openaudiomc.spigot.modules.shortner.AliasModule;
import com.craftmend.openaudiomc.spigot.modules.show.ShowModule;
import com.craftmend.openaudiomc.spigot.modules.traincarts.TrainCartsModule;
import com.craftmend.openaudiomc.spigot.services.scheduling.SpigotTaskProvider;
import com.craftmend.openaudiomc.spigot.services.server.ServerService;

import com.craftmend.openaudiomc.spigot.modules.players.PlayerModule;
import com.craftmend.openaudiomc.spigot.modules.regions.RegionModule;
import com.craftmend.openaudiomc.spigot.modules.speakers.SpeakerModule;

import com.craftmend.openaudiomc.spigot.services.threading.ExecutorService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;

@Getter
public final class OpenAudioMcSpigot extends JavaPlugin implements OpenAudioInvoker {

    /**
     * services OpenAudioMc uses in the background
     *
     *  - State service (keeps track of connections and state of the api)
     *  - Server service (compatibility and stuff)
     *  - authentication (auth)
     *  - time service (time sync with clients)
     *  - networking service (api connection)
     *  - Server Service (used to probe and detect what it is running)
     */
    private ServerService serverService;

    /**
     * modules that make up the plugin
     *
     * - ShortnerModule (handles shortners for urls)
     * - ExecutorService (manages fake syncronized tasks)
     * - ProxyModule (manages bungeecord link)
     * - player module (manages player connections)
     * - region module (OPTIONAL) (only loads regions if WorldGuard is enabled)
     * - command module (registers and loads the OpenAudioMc commands)
     * - media module (loads and manages all media in the service)
     * - show module (manages shows)
     * - Train carts module (hookds into traincarts)
     */
    private AliasModule aliasModule;
    private ExecutorService executorService;
    private ProxyModule proxyModule;
    private PlayerModule playerModule;
    private RegionModule regionModule;
    private SpigotCommandModule commandModule;
    private SpeakerModule speakerModule;
    private ShowModule showModule;
    private TrainCartsModule trainCartsModule;

    /**
     * Constant: main plugin instance and plugin timing
     */
    @Getter private static OpenAudioMcSpigot instance;
    private Instant boot = Instant.now();

    /**
     * load the plugin and start all of it's independent modules and services
     * this is in a specific order
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // setup loader
        this.proxyModule = new ProxyModule();

        // setup core
        try {
            new OpenAudioMc(this);
            // startup modules and services
            this.aliasModule = new AliasModule(this);
            this.executorService = new ExecutorService(this);
            this.serverService = new ServerService();
            this.playerModule = new PlayerModule(this);
            this.speakerModule = new SpeakerModule(this);
            this.commandModule = new SpigotCommandModule(this);
            this.showModule = new ShowModule(this);

            // optional modules
            if (getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
                this.regionModule = new RegionModule(this);
            }

            if (getServer().getPluginManager().isPluginEnabled("Train_Carts")) {
                try {
                    this.trainCartsModule = new TrainCartsModule(this);
                } catch (Exception e) {
                    OpenAudioLogger.toConsole("Hmn, looks like you have an old version of traincarts and thus OpenAudio failed to hook in.");
                }
            }

            // set state to idle, to allow connections and such, but only if not a node
            if (proxyModule.getMode() == ClientMode.NODE) {
                OpenAudioMc.getInstance().getStateService().setState(new WorkerState());
            } else {
                OpenAudioMc.getInstance().getStateService().setState(new IdleState("OpenAudioMc started and awaiting command"));
            }

            // timing end and calc
            Instant finish = Instant.now();
            OpenAudioLogger.toConsole("Starting and loading took " + Duration.between(boot, finish).toMillis() + "MS");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * save configuration and stop the plugin
     */
    @Override
    public void onDisable() {
        OpenAudioLogger.toConsole("Shutting down");
        OpenAudioMc.getInstance().disable();
        HandlerList.unregisterAll(this);
        OpenAudioLogger.toConsole("Stopped OpenAudioMc. Goodbye.");
    }

    @Override
    public boolean hasPlayersOnline() {
        return !Bukkit.getOnlinePlayers().isEmpty();
    }

    @Override
    public boolean isSlave() {
        return getProxyModule().getMode() != ClientMode.STAND_ALONE;
    }

    @Override
    public Platform getPlatform() {
        return Platform.SPIGOT;
    }

    @Override
    public Class<? extends INetworkingService> getServiceClass() {
        return proxyModule.getMode().getServiceClass();
    }

    @Override
    public ITaskProvider getTaskProvider() {
        return new SpigotTaskProvider();
    }

    @Override
    public ConfigurationImplementation getConfigurationProvider() {
        return new SpigotConfigurationImplementation(OpenAudioMcSpigot.getInstance());
    }

}
