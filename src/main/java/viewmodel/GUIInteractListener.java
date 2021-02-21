package viewmodel;

import Utill.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIInteractListener implements Listener {
    @EventHandler
    public void guiClick(InventoryClickEvent e) {
        GUI gui = GUI.getGUI((Player) e.getWhoClicked());
        if (gui != null) gui.onClick(e);
    }

    @EventHandler
    public void guiClose(InventoryCloseEvent e) {
        GUI gui = GUI.getGUI((Player) e.getPlayer());
        if (gui != null) gui.closeGUI(e);
    }
}
