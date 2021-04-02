package main;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.SneakyThrows;
import model.GiveKeyGUI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import viewmodel.GUIInteractListener;
import viewmodel.VehicleInteractListener;
import viewmodel.VehicleMovingAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getOfflinePlayers;

public class Main extends JavaPlugin {

    public static int COOL_DOWN;


    @SneakyThrows
    @Override
    public void onEnable() {
        killVehicleEntityByCustomNameIsUUID();
        init();

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
        Objects.requireNonNull(getCommand("vehicle")).setExecutor((commandSender, command, s, strings) -> {
            if (command.getName().equalsIgnoreCase("vehicle")) {
                if (commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if (sender.isOp()) {
                        if (sender.getGameMode().equals(GameMode.CREATIVE)) {
                            reloadConfig();
                            new GiveKeyGUI(sender, this);
                            return true;
                        } else {
                            commandSender.sendMessage(ChatColor.DARK_GREEN +
                                    "크리에이티브 모드에서만 가능합니다.");
                            //commandSender.sendMessage(ChatColor.DARK_GREEN +"Command execution is possible only in Creative mode");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED +
                                "권한이 부족합니다.");
                        //commandSender.sendMessage(ChatColor.RED +"Command execution denied because you do not have permission");
                    }
                }
            }
            return false;
        });
    }

    private void init() {
        saveDefaultConfig();
        COOL_DOWN = getConfig().getInt("CoolDown", 5);
        for (String type : getConfig().getStringList("TypeList")) {
            int seatCount = getConfig().getInt(type + ".seatCount");
            if (seatCount > 20) {
                getLogger().log(Level.WARNING, "The seatCount of this type(" + type + ") is " + seatCount + ", " +
                        "which is too big. Change it to the default value of 20.");
                getConfig().set(type + ".seatCount", 20);
                saveConfig();
            }
        }
    }


    @Override
    public void onDisable() {
        super.onDisable();
        killVehicleEntityByCustomNameIsUUID();
    }

    public static void killVehicleEntityByCustomNameIsUUID() {
        Bukkit.getServer().getWorlds().stream().map(World::getEntities).forEach(entities -> entities
                .stream()
                .filter(entity -> Arrays
                        .stream(getOfflinePlayers())
                        .map(OfflinePlayer::getUniqueId)
                        .map(UUID::toString)
                        .anyMatch(uuid -> uuid.equals(
                                entity.getCustomName()
                                )
                        )
                )
                .forEach(Entity::remove));
    }

    public static void killVehicleEntityByCustomNameIsUUID(Player player) {
        Bukkit.getServer().getWorlds().stream().map(World::getEntities).forEach(entities -> entities
                .stream()
                .filter(entity -> player.getUniqueId().toString().equals(entity.getCustomName()))
                .forEach(Entity::remove));
    }
}
