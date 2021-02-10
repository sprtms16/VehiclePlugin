package Utill;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GUI {

    public static short WHITE = 0;
    public static short ORANGE = 1;
    public static short MAGENTA = 2;
    public static short LIGHT_BLUE = 3;
    public static short YELLOW = 4;
    public static short LIME = 5;
    public static short PINK = 6;
    public static short GRAY = 7;
    public static short LIGHT_GRAY = 8;
    public static short CYAN = 9;
    public static short PURPLE = 10;
    public static short BLUE = 11;
    public static short BROWN = 12;
    public static short GREEN = 13;
    public static short RED = 14;
    public static short BLACK = 15;

    private static Map<Player, GUI> guiMap = new HashMap<Player, GUI>();

    public static GUI getGUI(Player p) {
        return guiMap.getOrDefault(p, null);
    }

    private Inventory inv;
    private Map<Integer, String> slotMap;

    protected GUI(Player p, String name, int size) {
        inv = Bukkit.createInventory(null, size, name);
        slotMap = new HashMap<Integer, String>();
        init();
        p.openInventory(inv);
        guiMap.put(p, this);
    }

    protected abstract void init();

    public abstract void onClick(InventoryClickEvent e);

//    protected void setItem(String name, List<String> lore, Material m, short data, int amount, int slot, String value, boolean glow) {
//        ItemStack item = new ItemStack(m, amount, data);
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName(name);
//        if (lore != null) meta.setLore(lore);
//        if (glow) {
//            meta.addEnchant(Enchantment.LURE, 1, false);
//            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        }
//        item.setItemMeta(meta);
//        slotMap.put(slot, value);
//        inv.setItem(slot, item);
//    }

    protected void setItem(String name, List<String> lore, Material m, int amount, int slot, String value, boolean glow) {
        ItemStack item = new ItemStack(m, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        if (glow) {
            meta.addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        slotMap.put(slot, value);
        inv.setItem(slot, item);
    }

    protected String getValue(int slot) {
        return slotMap.getOrDefault(slot, null);
    }

    public void closeGUI(InventoryCloseEvent e) {
        slotMap = null;
        guiMap.remove((Player) e.getPlayer());
    }

}
