package model;

import Utill.GUI;
import main.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Optional;

public class GiveKeyGUI extends GUI {

    public GiveKeyGUI(Player p, JavaPlugin plugin) {
        super(p, "Key Store", 9 * ((plugin.getConfig().getStringList("TypeList").size() / 9) + 1), plugin);
    }

    @Override
    protected void init(JavaPlugin plugin) {
        int slot = 0;
        for (String type : plugin.getConfig().getStringList("TypeList")) {
            setItem(
                    plugin.getConfig().getString(type + ".keyName", "차키" + (slot + 1)),
                    Collections.singletonList(type),
                    Material.STICK,
                    1,
                    slot++,
                    "key",
                    true);
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Optional.ofNullable(getValue(e.getRawSlot()))
                .filter("key"::equals)
                .ifPresent(slot -> e.getWhoClicked().getInventory().addItem(e.getCurrentItem()));

    }
}
