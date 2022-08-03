package screw.microsoft.antipopup;

import com.github.retrooper.packetevents.PacketEvents;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class AntiPopup extends JavaPlugin {

    static YamlDocument config;
    static boolean isPaper;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().debug(false).bStats(false).checkForUpdates(false);
        PacketEvents.getAPI().load();
        getLogger().info("Loaded PacketEvents");
    }

    @Override
    public void onEnable() {
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"),
                    Objects.requireNonNull(getResource("config.yml")),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
                            .build());
            getLogger().info("Config enabled.");
        } catch (IOException ex) {
            getLogger().warning("Config file could not be initialized");
            throw new RuntimeException(ex);
        }
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            if(!config.getBoolean("remove-warning")) {
                getLogger().warning("------------------------------------");
                getLogger().warning("!!! DO NOT REPORT THIS TO PAPER !!!");
                getLogger().warning("You are using Paper or a fork of paper.");
                getLogger().warning("Paper builds contain problems related to");
                getLogger().warning("players getting kicked if player sends");
                getLogger().warning("a message. Use spigot, or paper build 99");
                getLogger().warning("until further notice. AntiPopup still works.");
                getLogger().warning("Make sure to set enforce-secure-profile");
                getLogger().warning("in server.properties to false.");
                getLogger().warning("");
                getLogger().warning("To remove this message, set remove-warning");
                getLogger().warning("to true in config of AntiPopup.");
                getLogger().warning("------------------------------------");
            }
                isPaper = true;
            System.out.println(AntiPopup.isPaper);
        } catch (ClassNotFoundException ignored) {}
        System.out.println(AntiPopup.isPaper);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEventsListener());
        PacketEvents.getAPI().init();
        getLogger().info("Initiated PacketEvents");

        // To be added soon
        /*
        if(config.getBoolean("first-run").equals(true)) {
            try {
                FileInputStream in=new FileInputStream("server.properties");
                Properties props = new Properties();
                props.load(in);
                props.setProperty("enforce-secure-profile", String.valueOf(false));
                in.close();
                FileOutputStream out=new FileOutputStream("server.properties");
                props.store(out, "");
                out.close();
                config.set("first-run", false);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        */
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("Disabled PacketEvents");
    }
}
