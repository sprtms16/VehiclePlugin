package main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import viewmodel.GUIInteractListener;
import viewmodel.VehicleInteractListener;
import viewmodel.VehicleMovingAdapter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    public static List<String> TYPE_LIST;


    @SneakyThrows
    @Override
    public void onEnable() {
        for (UUID uuid : getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList())) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "kill @e[type=minecraft:armor_stand,name=" + uuid.toString() + "]");
        }

        FileConfiguration configuration = getConfig();
        init(configuration);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new VehicleInteractListener(this),
                        this
                );
        getServer()
                .getPluginManager()
                .registerEvents(
                        new GUIInteractListener(),
                        this
                );
        ProtocolLibrary
                .getProtocolManager()
                .addPacketListener(
                        new VehicleMovingAdapter(this,
                                PacketType.Play.Client.STEER_VEHICLE)
                );
    }

    private void init(FileConfiguration configuration) throws IOException {
        saveDefaultConfig();
        TYPE_LIST = getConfig().getStringList("TypeList");
        for(String s : TYPE_LIST){
            getLogger().log(Level.INFO,s);
        }
    }


    @Override
    public void onDisable() {
        super.onDisable();
        for (UUID uuid : getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList())) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:armor_stand,name=" + uuid.toString() + "]");
        }
    }
}
