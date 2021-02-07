package model;

import Utill.GUI;
import lombok.SneakyThrows;
import main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;

public class GiveKeyGUI extends GUI {
    JavaPlugin plugin;


    @SneakyThrows
    public GiveKeyGUI(Player p, JavaPlugin plugin) {
        super(p, "Key Store", 9 * 3);
        this.plugin = plugin;
    }

    @Override
    protected void init() {
        int slot = 0;
        for (String type : Main.TYPE_LIST) {
            setItem("차키" + (slot + 1),
                    Arrays.asList(type),
                    Material.STICK,
                    1,
                    slot++,
                    "key",
                    true);
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        Optional.ofNullable(getValue(e.getRawSlot())).ifPresent(slot -> {
            e.setCancelled(true);
            switch (slot) {
                case "key":
                    e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
                    break;
                default:
                    break;
            }
        });

    }
}
