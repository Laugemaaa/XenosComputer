package dk.xenos.computer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;


public final class XenosComputer extends JavaPlugin implements Listener {
    private static final int[] CONTENT = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private static final String PREFIX = "&8[&5&lXENOS PC&8] &7";


    private static final String ICON_REWARDS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThlNTY2OTQ5YmJkMTZkZTIyMDU2ZmE2NTRjMjdjOTJkM2NhM2MyMGI2MzM2MmIwY2E5ZjE5YTc0YmYxZmU5MyJ9fX0=";
    private static final String ICON_MISSIONS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM5MmU1MTExZWE3ZDdlYjNmMDU1ODMzZTFmMzVkNjUxYzBkYTU1NjQzYzkzODNlMGJjZTZjMjM2OTZkNThiOSJ9fX0=";
    private static final String ICON_PREMIUM_PURCHASE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMwNjVkNTA0OTVjMGEyZjI1YTMzZjkyYWE5MGQwODQ5Y2FmOTE0NzBiYjEwMjdkN2Y3OWNkYjVjZTM3MzhlYyJ9fX0=";
    private static final String ICON_PREMIUM_ACTIVE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2IwYTY4OWU1NTc0YWY4MDNlMDFlZmRhMDExMTRjOWRjZDM1N2U5YzQyNzg5NTY1YjViNGRiYjVjMzM4NzM0NyJ9fX0=";
    private static final String ICON_INFO = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzU3NDcwMTBkODRhYTU2NDgzYjc1ZjYyNDNkOTRmMzRjNTM0NjAzNTg0YjJjYzY4YTQ1YmYzNjU4NDAxMDVmZCJ9fX0=";
    private static final String ICON_FREE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRjZGZiODhjZjhjMzY1NjMwNDI1NGU0ZDY3YTBhYjAyZjZkMjdjNzlmMWIxZTQwNGRjM2E3Y2U0ZWIyZGUwMCJ9fX0=";
    private static final String ICON_PREMIUM_INFO = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmVkM2UxMzcyZDYyMDc4Zjk0MDRkODdmMDUzODEzMTFkZDg3MDcyODczMTIwZDQ3MTVlNjMzNWQ1YmY3ZWM0ZiJ9fX0=";


    private final Set<String> computers = new java.util.HashSet<String>();
    private final Set<String> postboxes = new java.util.HashSet<String>();
    private final Map<String, Auction> auctions = new LinkedHashMap<String, Auction>();
    private final Map<UUID, List<Parcel>> mailbox = new HashMap<UUID, List<Parcel>>();
    private final Map<UUID, List<Order>> orders = new HashMap<UUID, List<Order>>();
    private final Map<UUID, Stats> stats = new HashMap<UUID, Stats>();
    private final List<Credit> credits = new ArrayList<Credit>();
    private final Map<UUID, Input> awaiting = new HashMap<UUID, Input>();
    private File stateFile;
    private final VaultMoney money = new VaultMoney();
    private final ContractService contracts = new ContractService(this);
    private final ActivityFeed feed = new ActivityFeed();
    private final List<Transfer> transfers = new ArrayList<Transfer>();
    private boolean dirty = false;

    private static final class Transfer {
        UUID sender, receiver;
        long amount, time;
    }

    private enum Screen { DESKTOP, SHOP, AUCTIONS, AUCTION, SELL, ORDERS, MAIL, PROFILE, HELP, BANK, RECIPIENTS, CONTRACTS, FEED, NEWS, RANKINGS }

    private static final class PcHolder implements InventoryHolder {
        final Screen screen;
        final int page;
        final String selected;
        final Map<Integer, String> links = new HashMap<Integer, String>();
        Inventory inventory;
        PcHolder(Screen screen, int page, String selected) {
            this.screen = screen;
            this.page = page;
            this.selected = selected;
        }
        @Override public Inventory getInventory() { return inventory; }
    }

    private static final class Auction {
        String id, sellerName;
        UUID seller, bidder;
        ItemStack item;
        long created, expires, start, bid;
    }
    private static final class Parcel {
        String id, reason;
        ItemStack item;
        long time;
    }
    private static final class Order {
        String name;
        long price, time;
    }
    private static final class Credit {
        UUID to;
        long amount;
        String reason;
    }
    private static final class Stats {
        long spent, earned;
        int listings, sales, wins, bids;
    }
    private static final class Input {
        enum Type { SELL_PRICE, CUSTOM_BID, BANK_AMOUNT }
        Type type;
        String auctionId;
        UUID recipient;
        long duration;
        ItemStack snapshot;
    }


    private static final class VaultMoney {
        private Object economy;
        private Method withdraw, deposit, getBalance;
        @SuppressWarnings({"unchecked", "rawtypes"})
        boolean connect() {
            try {
                Class<?> api = Class.forName("net.milkbowl.vault.economy.Economy");
                economy = Bukkit.getServicesManager().load((Class) api);
                if (economy == null) return false;
                withdraw = api.getMethod("withdrawPlayer", OfflinePlayer.class, double.class);
                deposit = api.getMethod("depositPlayer", OfflinePlayer.class, double.class);
                getBalance = api.getMethod("getBalance", OfflinePlayer.class);
                return true;
            } catch (Exception ex) {
                economy = null;
                return false;
            }
        }
        boolean ready() { return economy != null || connect(); }
        private boolean attempt(Method method, UUID who, long amount) {
            if (amount <= 0) return amount == 0;
            if (!ready()) return false;
            try {
                Object result = method.invoke(economy, Bukkit.getOfflinePlayer(who), (double) amount);
                return Boolean.TRUE.equals(result.getClass().getMethod("transactionSuccess").invoke(result));
            } catch (Exception ex) {
                return false;
            }
        }
        long balance(UUID who) {
            if (!ready()) return -1;
            try {
                Object balance = getBalance.invoke(economy, Bukkit.getOfflinePlayer(who));
                if (!(balance instanceof Number)) return -1;
                return Math.max(0L, (long)((Number)balance).doubleValue());
            } catch (Exception e) {return -1L;}
        }
        boolean take(UUID who, long amount) { return attempt(withdraw, who, amount); }
        boolean give(UUID who, long amount) { return attempt(deposit, who, amount); }
    }

    @Override public void onEnable() {
        saveDefaultConfig();
        stateFile = new File(getDataFolder(), "state.yml");
        readState();
        money.connect();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() { if (dirty) writeState(); }
        }, 1200L, 1200L);
        long interval = Math.max(5L, getConfig().getLong("settings.auction-expire-check-seconds", 15)) * 20L;
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() {
                settleExpired();
                processCredits();
            }
        }, 40L, interval);
        getLogger().info("XenosComputer online: " + computers.size() + " computers, "
            + auctions.size() + " auctions. Vault economy: " + (money.ready() ? "ready" : "missing"));
    }

    @Override public void onDisable() {
        awaiting.clear();
        writeState();
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    private void tell(Player player, String message) { player.sendMessage(color(PREFIX + message)); }
    private static String moneyText(long value) { return String.format("%,d", value).replace(',', '.'); }
    private static String nameOf(UUID uuid) {
        String n = Bukkit.getOfflinePlayer(uuid).getName();
        return n == null ? "Ukendt" : n;
    }
    private static String blockKey(Block block) {
        Location l = block.getLocation();
        return l.getWorld().getUID() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }
    private Stats stats(UUID uuid) {
        Stats value = stats.get(uuid);
        if (value == null) {
            value = new Stats();
            stats.put(uuid, value);
        }
        return value;
    }
    private List<Parcel> mail(UUID uuid) {
        List<Parcel> value = mailbox.get(uuid);
        if (value == null) {
            value = new ArrayList<Parcel>();
            mailbox.put(uuid, value);
        }
        return value;
    }
    private List<Order> purchases(UUID uuid) {
        List<Order> value = orders.get(uuid);
        if (value == null) {
            value = new ArrayList<Order>();
            orders.put(uuid, value);
        }
        return value;
    }
    private void deliver(UUID uuid, ItemStack item, String reason) {
        Parcel parcel = new Parcel();
        parcel.id = UUID.randomUUID().toString();
        parcel.time = System.currentTimeMillis();
        parcel.item = item.clone();
        parcel.reason = reason;
        mail(uuid).add(parcel);
        alert(uuid, "&eNy levering: &f" + reason + "&7. Brug en fysisk postkasse.");
        Player online = Bukkit.getPlayer(uuid);
        if (online != null && online.isOnline()) {
            tell(online, "&e&lNY LEVERING &8» &7Du har fået post! "
                + "&7Højreklik på en &eXenos-postkasse &7for at afhente pakken.");
        }
    }

    void markDirty() { dirty = true; }
    void saveNow() { writeState(); }
    void alert(UUID recipient, String message) {
        feed.add(recipient, message);
        dirty = true;
        Player online = Bukkit.getPlayer(recipient);
        if (online != null && online.isOnline()) tell(online, "&d&lNY BESKED &8» &7Se Xenos OS for detaljer.");
    }
    void queueCredit(UUID to, long amount, String reason) {
        if (amount <= 0 || amount > 10000000L) return;
        Credit credit = new Credit();
        credit.to = to; credit.amount = amount; credit.reason = reason;
        credits.add(credit);
        dirty = true;
    }

    private void transfer(Player sender, UUID to, long amount) {
        if (amount < 1 || amount > 10000000L) {
            tell(sender, "&cBeløbet skal være mellem 1 og 10.000.000 $."); return;
        }
        if (to.equals(sender.getUniqueId())) {
            tell(sender, "&cDu kan ikke overføre penge til dig selv."); return;
        }
        if (!money.ready()) {
            tell(sender, "&cBanken er midlertidigt offline (Vault)."); return;
        }
        if (!money.take(sender.getUniqueId(), amount)) {
            tell(sender, "&cBetalingen blev afvist. Kontrollér din saldo."); return;
        }

        queueCredit(to, amount, "Bankoverførsel fra " + sender.getName());
        Transfer log = new Transfer();
        log.sender = sender.getUniqueId(); log.receiver = to;
        log.amount = amount; log.time = System.currentTimeMillis();
        transfers.add(0, log);
        while (transfers.size() > 150) transfers.remove(transfers.size()-1);
        alert(to, "&aBankoverførsel: &e+" + moneyText(amount) + " $ &7fra &f" + sender.getName());
        alert(sender.getUniqueId(), "&fSendt &e" + moneyText(amount) + " $ &7til &f" + nameOf(to));
        writeState();
        processCredits();
        tell(sender, "&aDu har overført &e" + moneyText(amount) + " $ &atil &f" + nameOf(to) + "&a.");
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase();
        if (name.equals("xpcnews")) {
            if (!sender.hasPermission("xenoscomputer.admin")) {
                sender.sendMessage("No permission."); return true;
            }
            if (args.length == 0) {
                sender.sendMessage("/xpcnews <nyhed>"); return true;
            }
            String text = String.join(" ", args);
            if (text.length() > 100) {
                sender.sendMessage("Nyheder skal vaere under 100 tegn."); return true;
            }
            feed.publish(text);
            writeState();
            Bukkit.broadcastMessage(color("&8[&5&lXENOS OS&8] &d&lNYHED &8» &f" + text));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command requires an in-game player.");
            return true;
        }
        Player player = (Player) sender;
        if (name.equals("xpc")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("status") && player.hasPermission("xenoscomputer.admin")) {
                tell(player, "&f" + computers.size() + " &7computere, &f" + postboxes.size() + " &7postkasser, &f" + auctions.size()
                    + " &7auktioner, &f" + credits.size() + " &7ventende udbetalinger. Vault: &f"
                    + (money.ready() ? "OK" : "MANGLER"));
                return true;
            }
            if (!player.hasPermission("xenoscomputer.remote")) {
                tell(player, "&cBrug en computerblok for at åbne PC'en.");
                return true;
            }
            openDesktop(player);
            return true;
        }
        if (!player.hasPermission("xenoscomputer.admin")) {
            tell(player, "&cDu har ikke adgang.");
            return true;
        }
        Block block = player.getTargetBlock((Set<Material>) null, 6);
        if (block == null || block.getType() == Material.AIR) {
            tell(player, "&cKig på en blok højst 6 blokke væk.");
            return true;
        }
        String key = blockKey(block);
        if (name.equals("setcomputer")) {
            if (postboxes.contains(key)) {
                tell(player, "&cDenne blok er allerede en postkasse. Fjern den først.");
                return true;
            }
            computers.add(key);
            writeState();
            tell(player, "&aComputeren er installeret! &7Højreklik på blokken for at bruge Xenos OS.");
        } else if (name.equals("removecomputer")) {
            if (computers.remove(key)) {
                writeState();
                tell(player, "&aComputeren er fjernet fra blokken.");
            } else {
                tell(player, "&cDenne blok er ikke registreret som computer.");
            }
        } else if (name.equals("setpostkasse")) {
            if (computers.contains(key)) {
                tell(player, "&cDenne blok er allerede en computer. Fjern den først.");
                return true;
            }
            postboxes.add(key);
            writeState();
            tell(player, "&aPostkassen er installeret! &7Højreklik på blokken for at hente leveringer.");
        } else if (name.equals("removepostkasse")) {
            if (postboxes.remove(key)) {
                writeState();
                tell(player, "&aPostkassen er fjernet. &7Spillernes varer er stadig gemt.");
            } else {
                tell(player, "&cDenne blok er ikke registreret som postkasse.");
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) return;
        String key = blockKey(event.getClickedBlock());
        if (!computers.contains(key) && !postboxes.contains(key)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (computers.contains(key)) {
            if (!player.hasPermission("xenoscomputer.use")) {
                tell(player, "&cDu har ikke adgang til computeren.");
                return;
            }
            openDesktop(player);
        } else {
            if (!player.hasPermission("xenoscomputer.mailbox")) {
                tell(player, "&cDu har ikke adgang til postkassen.");
                return;
            }
            openMail(player, 0);
        }
    }
    @EventHandler(ignoreCancelled = true) public void onBreak(BlockBreakEvent event) {
        String key = blockKey(event.getBlock());
        if (computers.contains(key) || postboxes.contains(key)) {
            event.setCancelled(true);
            tell(event.getPlayer(), computers.contains(key)
                ? "&cDenne computer er beskyttet. Brug /removecomputer først."
                : "&cDenne postkasse er beskyttet. Brug /removepostkasse først.");
        }
    }
    @EventHandler public void onExplosion(EntityExplodeEvent event) {
        java.util.Iterator<Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            String key = blockKey(iterator.next());
            if (computers.contains(key) || postboxes.contains(key)) iterator.remove();
        }
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onContractBreak(BlockBreakEvent event) {
        Material material=event.getBlock().getType();
        String kind=null;
        if(material==Material.STONE)kind="STONE";
        else if(material==Material.COAL_ORE)kind="COAL";
        else if(material==Material.IRON_ORE)kind="IRON";
        else if(material==Material.LOG || material==Material.LOG_2)kind="WOOD";
        else if(material==Material.GRAVEL)kind="GRAVEL";
        else if(material==Material.SAND)kind="SAND";
        if(kind!=null)contracts.progress(event.getPlayer().getUniqueId(),kind);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onContractKill(EntityDeathEvent event) {
        Player killer=event.getEntity().getKiller();
        if(killer==null)return;
        if(event.getEntityType()==org.bukkit.entity.EntityType.ZOMBIE)
            contracts.progress(killer.getUniqueId(),"ZOMBIE");
        else if(event.getEntityType()==org.bukkit.entity.EntityType.SKELETON)
            contracts.progress(killer.getUniqueId(),"SKELETON");
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onContractFish(PlayerFishEvent event) {
        if(event.getState()==PlayerFishEvent.State.CAUGHT_FISH)
            contracts.progress(event.getPlayer().getUniqueId(),"FISH");
    }

    @EventHandler public void onQuit(PlayerQuitEvent event) { awaiting.remove(event.getPlayer().getUniqueId()); }

    @EventHandler public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override public void run() {
                if (!player.isOnline()) return;
                int count = mail(player.getUniqueId()).size();
                if (count > 0) {
                    tell(player, "&d&lNY POST &8» &fDu har &e" + count
                        + " &flevering(er) klar! &7Find en Xenos-postkasse og højreklik.");
                }
            }
        }, 60L);
    }

    private ItemStack item(Material material, int count, short data, String title, String... details) {
        ItemStack stack = new ItemStack(material, count, data);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(title));
            List<String> lore = new ArrayList<String>();
            for (String detail : details) lore.add(color(detail));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }
    private ItemStack item(Material material, String title, String... details) {
        return item(material, 1, (short) 0, title, details);
    }
    private ItemStack glass(int data, String title, String... details) {
        return item(Material.STAINED_GLASS_PANE, 1, (short) data, title, details);
    }
    private ItemStack head(String owner, String title, String... details) {
        ItemStack stack = item(Material.SKULL_ITEM, 1, (short) 3, title, details);
        if (stack.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(owner);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private ItemStack texturedHead(String texture, String fallbackOwner, String title, String... details) {
        ItemStack stack = head(fallbackOwner, title, details);
        try {
            Class<?> profileClass = Class.forName("com.mojang.authlib.GameProfile");
            Object profile = profileClass.getConstructor(UUID.class, String.class)
                .newInstance(UUID.randomUUID(), "XenosOS");
            Object properties = profileClass.getMethod("getProperties").invoke(profile);
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Object property = propertyClass.getConstructor(String.class, String.class)
                .newInstance("textures", texture);
            Method put = properties.getClass().getMethod("put", Object.class, Object.class);
            put.invoke(properties, "textures", property);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
            stack.setItemMeta(meta);
        } catch (Exception ex) {

        }
        return stack;
    }

    private Inventory menu(PcHolder holder, String title) {
        Inventory inv = Bukkit.createInventory(holder, 54, color(title));
        holder.inventory = inv;
        for (int i = 0; i < 54; i++) inv.setItem(i, glass(15, "&8"));
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass(10, "&8"));
            inv.setItem(45 + i, glass(10, "&8"));
        }
        for (int r = 1; r < 5; r++) {
            inv.setItem(9 * r, glass(7, "&8"));
            inv.setItem(9 * r + 8, glass(7, "&8"));
        }
        return inv;
    }
    private void footer(Inventory inv, int page, int count) {
        inv.setItem(45, item(Material.ARROW, "&d&lSKRIVEBORD", "&7Tilbage til computeren"));
        if (page > 0) inv.setItem(48, item(Material.PAPER, "&f&lFORRIGE SIDE"));
        inv.setItem(49, item(Material.COMPASS, "&7Side &f" + (page + 1) + "&7/&f" + Math.max(1, (count + CONTENT.length - 1) / CONTENT.length)));
        if ((page + 1) * CONTENT.length < count) inv.setItem(50, item(Material.PAPER, "&f&lNÆSTE SIDE"));
        inv.setItem(53, item(Material.BARRIER, "&c&lLUK"));
    }

    private void openDesktop(Player player) {
        PcHolder holder = new PcHolder(Screen.DESKTOP, 0, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &f&lCOMPUTER");
        UUID id=player.getUniqueId();
        inv.setItem(4, texturedHead(ICON_PREMIUM_INFO, "MHF_Chest", "&d&lXENOS OS &8• &f&lDESKTOP",
            "&8&m----------------------------", "&7Velkommen tilbage, &f" + player.getName(),
            "&7Køb, sælg, løs kontrakter og", "&7styr din økonomi fra ét sted.", " ",
            "&7Økonomi: " + (money.ready() ? "&aONLINE" : "&cOFFLINE"),
            "&7Auktioner: &f" + auctions.size(),
            "&7Postkasse: &e" + mail(id).size() + " pakke(r)", "&8&m----------------------------"));

        inv.setItem(10, texturedHead(ICON_FREE,"MHF_Villager","&a&lXENOS MARKET &8» &fButik",
            "&8&m----------------------------", "&7Bestil udstyr og ressourcer", "&7fra serverens officielle butik."," ",
            "&a✔ &7Faste priser og tydelige beskrivelser", "&a✔ &7Vault-betaling", "&a✔ &7Levering via fysisk postkasse", " ", "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(12, texturedHead(ICON_PREMIUM_PURCHASE,"MHF_Chest","&6&lAUKTIONSHUSET &8» &fHandel",
            "&8&m----------------------------", "&7Køb sjældne items fra andre spillere", "&7eller sælg dine egne værdier.", " ",
            "&6◆ &7Sikker budreservation", "&6◆ &7Automatisk refusion ved overbud", "&6◆ &7Se profiler og levende auktioner", " ",
            "&7Aktive auktioner: &e" + auctions.size(), "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(14, item(Material.WRITTEN_BOOK,"&b&lKONTRAKTCENTRAL &8» &fJobs",
            "&8&m----------------------------", "&7Tjen ekstra penge ved at udføre", "&7daglige og ugentlige udfordringer.", " ",
            "&b◆ &73 personlige dagskontrakter", "&b◆ &73 større ugentlige kontrakter", "&b◆ &7Følg fremskridt i realtid", "&b◆ &7Hent belønninger direkte", " ", "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));

        inv.setItem(20, item(Material.GOLD_INGOT,"&e&lXENOS BANK &8» &fOverførsler",
            "&8&m----------------------------", "&7Send penge til andre spillere", "&7gennem bankens sikre overførselsflow.", " ",
            "&e◆ &7Vælg en modtager", "&e◆ &7Indtast beløb i chatten", "&e◆ &7Se din transaktionshistorik", " ",
            "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(22, head(player.getName(),"&d&lMIN PROFIL &8» &f" + player.getName(),
            "&8&m----------------------------", "&7Dit personlige handelsdashboard.", " ",
            "&d◆ &7Solgte auktioner og vundne bud", "&d◆ &7Indtjening og samlet forbrug", "&d◆ &7Antal oprettede annoncer", " ",
            "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(24, item(Material.BOOK,"&f&lORDREARKIV &8» &fKvitteringer",
            "&8&m----------------------------", "&7Alle dine ordrer samlet på ét sted.", " ",
            "&f◆ &7Se vare, pris og købstidspunkt", "&f◆ &7Find tidligere bestillinger", "&f◆ &7Levering til fysisk postkasse", " ", "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));

        inv.setItem(30, item(Material.CHEST,"&e&lPOSTCENTER &8» &fLeveringsstatus",
            "&8&m----------------------------", "&7Se, hvad der ligger klar til dig.", "&7Varer kan kun afhentes fysisk.", " ",
            "&7Pakker klar: &e"+mail(id).size(), "&7Afhentning: &fPostkasseblok", " ",
            "&eKLIK FOR AT SE STATUS", "&8&m----------------------------"));
        inv.setItem(32, item(Material.DIAMOND,"&b&lHANDELSRANGLISTE &8» &fTopspillere",
            "&8&m----------------------------", "&7Se hvem der har solgt mest", "&7på serverens auktionsplatform.", " ",
            "&b◆ &7Rangering efter salgsomsætning", "&b◆ &7Klik på en spiller for profil", " ",
            "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(34, item(Material.PAPER,"&d&lBESKEDCENTER &8» &fNetværket",
            "&8&m----------------------------", "&7Få overblik over dine aktiviteter", "&7og officielle beskeder fra serveren."," ",
            "&d◆ &7Nye leveringer og overførsler", "&d◆ &7Kontrakter klar til aflevering", "&d◆ &7Seneste servernyheder", " ",
            "&eKLIK FOR AT ÅBNE", "&8&m----------------------------"));
        inv.setItem(49,item(Material.COMPASS,"&5&lXENOS NETWORK &8• &fSTATUS",
            "&7Vault: "+(money.ready()?"&aONLINE":"&cOFFLINE"),
            "&7Aktive auktioner: &f"+auctions.size(),
            "&7Dine pakker: &e"+mail(id).size(), "&7Version 2.0"));
        inv.setItem(53,item(Material.BARRIER,"&c&lSLUK COMPUTEREN","&7Afslut sikkert."));
        player.openInventory(inv);
    }


    private void mailboxInstructions(Player player) {
        int parcels = mail(player.getUniqueId()).size();
        tell(player, "&e&lPOSTCENTER &8» &7Du har &f" + parcels + " &7pakke(r) klar.");
        tell(player, "&7Find en fysisk postkasse og &ehøjreklik på blokken &7for at hente dem.");
    }

    private List<String> products() {
        ConfigurationSection shop = getConfig().getConfigurationSection("shop");
        if (shop == null) return Collections.emptyList();
        return new ArrayList<String>(shop.getKeys(false));
    }
    private void openShop(Player player, int page) {
        List<String> ids = products();
        page = clampPage(page, ids.size());
        PcHolder holder = new PcHolder(Screen.SHOP, page, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &a&lBUTIK");
        inv.setItem(4, item(Material.EMERALD, "&a&lXENOS MARKET &8| &fOFFICIEL BUTIK",
            "&8&m-----------------------------",
            "&7Udstyr din fange med ressourcer",
            "&7og forsyninger til din rejse.",
            " ", "&7Betaling: &eServerens penge ($)",
            "&7Levering: &eFysisk postkasse",
            "&7Køb en vare ved at klikke på den.",
            "&8&m-----------------------------"));
        for (int i = 0; i < CONTENT.length && page * CONTENT.length + i < ids.size(); i++) {
            String id = ids.get(page * CONTENT.length + i);
            String path = "shop." + id + ".";
            Material material = Material.matchMaterial(getConfig().getString(path + "material", "STONE"));
            if (material == null || material == Material.AIR) continue;
            int amount = Math.max(1, Math.min(64, getConfig().getInt(path + "amount", 1)));
            long price = Math.max(0, getConfig().getLong(path + "price", 0));
            List<String> lines = new ArrayList<String>();
            lines.add("&8&m---------------------------");
            List<String> description = getConfig().getStringList(path + "lore");
            if (description.isEmpty()) {
                lines.add(getConfig().getString(path + "description", "&7Butiksvare"));
            } else {
                lines.addAll(description);
            }
            lines.add(" ");
            lines.add("&7Antal: &f" + amount + " stk.");
            lines.add("&7Pris: &e" + moneyText(price) + " $");
            lines.add("&7Levering: &fFysisk postkasse");
            lines.add(" ");
            lines.add("&a➜ KLIK FOR AT BESTILLE");
            lines.add("&8&m---------------------------");
            inv.setItem(CONTENT[i], item(material, amount, (short) 0,
                getConfig().getString(path + "name", id),
                lines.toArray(new String[lines.size()])));
            holder.links.put(CONTENT[i], id);
        }
        footer(inv, page, ids.size());
        player.openInventory(inv);
    }
    private int clampPage(int page, int size) {
        return Math.max(0, Math.min(page, Math.max(0, (size - 1) / CONTENT.length)));
    }
    private void purchase(Player player, String id, int page) {
        String path = "shop." + id + ".";
        if (!getConfig().isConfigurationSection("shop." + id)) return;
        Material material = Material.matchMaterial(getConfig().getString(path + "material", "AIR"));
        if (material == null || material == Material.AIR) return;
        int amount = getConfig().getInt(path + "amount", 1);
        long price = getConfig().getLong(path + "price", -1);
        if (amount < 1 || amount > 64 || price < 1 || price > 10000000) {
            tell(player, "&cDenne vare er forkert konfigureret."); return;
        }
        if (!money.ready()) { tell(player, "&cButikken kræver Vault og et økonomi-plugin."); return; }
        if (!money.take(player.getUniqueId(), price)) {
            tell(player, "&cDu har ikke råd til den vare."); return;
        }
        String title = getConfig().getString(path + "name", id);
        deliver(player.getUniqueId(), new ItemStack(material, amount), "Butik: " + ChatColor.stripColor(color(title)));
        Order order = new Order(); order.name = ChatColor.stripColor(color(title));
        order.price = price; order.time = System.currentTimeMillis();
        purchases(player.getUniqueId()).add(order);
        alert(player.getUniqueId(), "&aOrdre bekræftet: &f" + order.name + " &7(&e" + moneyText(price) + "$&7)");
        stats(player.getUniqueId()).spent += price;
        writeState();
        tell(player, "&aBestilling gennemført! &f" + moneyText(price)
            + " $ &7trukket. &eHent varen ved en fysisk postkasse.");
        refresh(new Runnable() { @Override public void run() { openShop(player, page); } });
    }
    private void openAuctions(Player player, int page) {
        List<Auction> all = new ArrayList<Auction>(auctions.values());
        Collections.sort(all, new Comparator<Auction>() {
            @Override public int compare(Auction a, Auction b) { return Long.compare(b.created, a.created); }
        });
        page = clampPage(page, all.size());
        PcHolder holder = new PcHolder(Screen.AUCTIONS, page, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &6&lAUKTIONER");
        inv.setItem(4, item(Material.GOLD_INGOT, "&6&lXENOS AUCTIONS &8| &fMARKEDSPLADS",
            "&8&m----------------------------",
            "&7Køb og sælg med andre fanger.",
            "&7Bud trækkes fra din saldo og",
            "&7refunderes, hvis du bliver overbudt.",
            " ",
            "&7Aktive auktioner: &e" + all.size(),
            "&7Levering: &fFysisk postkasse",
            "&8&m----------------------------"));
        inv.setItem(8, item(Material.ANVIL, "&a&lOPRET NY AUKTION",
            "&8&m----------------------------",
            "&7Hold dit item i hånden og",
            "&7vælg en varighed og startpris.",
            " ",
            "&7Varigheden kan være 1, 6 eller 24 timer.",
            "&7Varen reserveres under auktionen.",
            " ", "&a➜ KLIK FOR AT OPRETTE",
            "&8&m----------------------------"));
        for (int i = 0; i < CONTENT.length && page * CONTENT.length + i < all.size(); i++) {
            Auction a = all.get(page * CONTENT.length + i);
            inv.setItem(CONTENT[i], decorateAuction(a));
            holder.links.put(CONTENT[i], a.id);
        }
        footer(inv, page, all.size());
        player.openInventory(inv);
    }
    private ItemStack decorateAuction(Auction a) {
        ItemStack stack = a.item.clone();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
            lore.add(color("&8&m--------------------------"));
            lore.add(color("&6&lXENOS AUCTIONS"));
            lore.add(color("&7Sælger: &f" + a.sellerName));
            lore.add(color("&7Højeste bud: &e" + moneyText(a.bid == 0 ? a.start : a.bid) + " $"));
            lore.add(color("&7Tid tilbage: &f" + remaining(a.expires)));
            lore.add(color("&7Levering: &fFysisk postkasse"));
            lore.add(color("&eKLIK FOR AT BYDE"));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }
    private String remaining(long end) {
        long seconds = Math.max(0L, (end - System.currentTimeMillis()) / 1000L);
        return (seconds / 3600L) + "t " + (seconds % 3600L / 60L) + "m";
    }
    private long minBid(Auction a) {
        if (a.bid == 0) return a.start;
        return a.bid + Math.max(10L, getConfig().getLong("settings.min-bid-increase", 10L));
    }
    private void openAuction(Player player, String id) {
        Auction a = auctions.get(id);
        if (a == null) { tell(player, "&cAuktionen eksisterer ikke længere."); openAuctions(player, 0); return; }
        PcHolder holder = new PcHolder(Screen.AUCTION, 0, id);
        Inventory inv = menu(holder, "&5&lXENOS &8- &6&lBUD");
        inv.setItem(4, item(Material.WATCH, "&6&lAUKTIONSDETALJER &8| &fBUDCENTRAL",
            "&8&m----------------------------",
            "&7Sælger: &f" + a.sellerName,
            "&7Tid tilbage: &f" + remaining(a.expires),
            "&7Nuværende bud: &e" + moneyText(a.bid == 0 ? a.start : a.bid) + " $",
            "&7Mindste næste bud: &e" + moneyText(minBid(a)) + " $",
            " ",
            "&7Dit bud bliver reserveret.",
            "&7Du får beløbet retur ved overbud.",
            "&7Vinderen afhenter varen i postkassen.",
            "&8&m----------------------------"));
        inv.setItem(13, decorateAuction(a));
        long minimum = minBid(a);
        inv.setItem(20, item(Material.GOLD_INGOT, "&a&lBYD MINIMUM", "&7Bud: &e" + moneyText(minimum), "&eKLIK FOR AT BYDE"));
        inv.setItem(22, item(Material.GOLD_BLOCK, "&6&lBYD +25%", "&7Bud: &e" + moneyText(minimum + Math.max(10, minimum / 4)), "&eKLIK FOR AT BYDE"));
        inv.setItem(24, item(Material.PAPER, "&b&lVALGFRIT BUD", "&7Indtast selv et beløb i chatten.", "&eKLIK"));
        inv.setItem(31, head(a.sellerName, "&d&l" + a.sellerName + "s PROFIL", "&7Se sælgerens profil.", "&eKLIK"));
        if (a.seller.equals(player.getUniqueId()) && a.bidder == null) {
            inv.setItem(40, item(Material.BARRIER, "&c&lANNULLER AUKTION", "&7Du får varen tilbage i postkassen."));
        }
        inv.setItem(45, item(Material.ARROW, "&d&lTILBAGE TIL AUKTIONER"));
        inv.setItem(53, item(Material.BARRIER, "&c&lLUK"));
        player.openInventory(inv);
    }
    private void placeBid(Player player, String id, long amount) {
        Auction a = auctions.get(id);
        if (a == null || a.expires <= System.currentTimeMillis()) {
            tell(player, "&cAuktionen er afsluttet.");
            refresh(new Runnable() { @Override public void run() { openAuctions(player, 0); } }); return;
        }
        UUID who = player.getUniqueId();
        if (who.equals(a.seller)) { tell(player, "&cDu kan ikke byde på din egen auktion."); return; }
        if (amount < minBid(a) || amount > 10000000L) {
            tell(player, "&cDit bud skal være mindst &e" + moneyText(minBid(a)) + " &cog højst 10.000.000."); return;
        }
        if (!money.ready()) { tell(player, "&cVault/økonomi er ikke tilgængelig."); return; }

        long charge = who.equals(a.bidder) ? amount - a.bid : amount;
        if (!money.take(who, charge)) { tell(player, "&cDu har ikke råd til buddet."); return; }
        if (a.bidder != null && !who.equals(a.bidder)) {
            Credit refund = new Credit(); refund.to = a.bidder; refund.amount = a.bid;
            refund.reason = "Overbud på auktion " + a.id;
            credits.add(refund);
            alert(a.bidder, "&eDu er blevet overbudt. &7Din refundering er under behandling.");
        }
        a.bidder = who;
        a.bid = amount;
        alert(a.seller, "&eNyt bud på auktion: &f" + moneyText(amount) + " $");
        stats(who).bids++;
        writeState();
        processCredits();
        tell(player, "&aBud registreret: &e" + moneyText(amount) + " $&7.");
        refresh(new Runnable() { @Override public void run() { openAuction(player, id); } });
    }
    private void openSell(Player player) {
        PcHolder holder = new PcHolder(Screen.SELL, 0, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &f&lSÆLG ITEM");
        ItemStack held = player.getItemInHand();
        inv.setItem(4, item(Material.ANVIL, "&6&lOPRET AUKTION &8| &fSALGSKONTOR",
            "&8&m----------------------------",
            "&7Gør dit item tilgængeligt for bud.",
            "&7Hold varen i hånden, vælg varighed",
            "&7og skriv din startpris i chatten.",
            " ",
            "&7Varen opbevares indtil auktionen slutter.",
            "&7Ved salg modtager du pengene.",
            "&7Usolgte varer sendes til postkassen.",
            "&8&m----------------------------"));
        inv.setItem(13, held == null || held.getType() == Material.AIR
            ? item(Material.BARRIER, "&c&lINTET ITEM I HÅNDEN") : held.clone());
        inv.setItem(20, item(Material.WATCH, "&a&l1 TIME", "&7Auktionen varer i 60 minutter.", "&eKLIK"));
        inv.setItem(22, item(Material.WATCH, "&e&l6 TIMER", "&7Auktionen varer i 6 timer.", "&eKLIK"));
        inv.setItem(24, item(Material.WATCH, "&6&l24 TIMER", "&7Auktionen varer i 24 timer.", "&eKLIK"));
        inv.setItem(45, item(Material.ARROW, "&d&lTILBAGE"));
        inv.setItem(53, item(Material.BARRIER, "&c&lLUK"));
        player.openInventory(inv);
    }
    private void promptSell(Player player, long duration) {
        ItemStack held = player.getItemInHand();
        if (held == null || held.getType() == Material.AIR) {
            tell(player, "&cHold den vare du vil sælge i hånden."); return;
        }
        int amount = getConfig().getInt("settings.max-auction-item-amount", 64);
        if (held.getAmount() < 1 || held.getAmount() > amount) {
            tell(player, "&cDu kan sælge højst " + amount + " items ad gangen."); return;
        }
        int mine = 0;
        for (Auction a : auctions.values()) if (a.seller.equals(player.getUniqueId())) mine++;
        if (mine >= getConfig().getInt("settings.max-auctions-per-player", 5)
            || auctions.size() >= getConfig().getInt("settings.max-active-auctions", 200)) {
            tell(player, "&cGrænsen for aktive auktioner er nået."); return;
        }
        Input input = new Input(); input.type = Input.Type.SELL_PRICE;
        input.duration = duration; input.snapshot = held.clone();
        awaiting.put(player.getUniqueId(), input);
        player.closeInventory();
        tell(player, "&dSkriv startprisen i chatten &7(eller &cannuller&7). Itemet bliver først fjernet ved oprettelse.");
    }
    private void createAuction(Player player, Input input, long start) {
        if (start < getConfig().getLong("settings.min-start-price", 10)
            || start > getConfig().getLong("settings.max-start-price", 10000000)) {
            tell(player, "&cStartprisen er uden for de tilladte grænser."); return;
        }
        ItemStack held = player.getItemInHand();
        if (held == null || held.getType() == Material.AIR || !held.isSimilar(input.snapshot)
            || held.getAmount() != input.snapshot.getAmount()) {
            tell(player, "&cDit item er ændret. Auktionen blev ikke oprettet."); return;
        }
        int mine = 0;
        for (Auction a : auctions.values()) if (a.seller.equals(player.getUniqueId())) mine++;
        if (mine >= getConfig().getInt("settings.max-auctions-per-player", 5)
            || auctions.size() >= getConfig().getInt("settings.max-active-auctions", 200)) {
            tell(player, "&cAuktionsgrænsen er nået."); return;
        }
        if (!money.ready()) { tell(player, "&cVault og et økonomi-plugin kræves for at sælge."); return; }
        Auction a = new Auction();
        a.id = UUID.randomUUID().toString();
        a.seller = player.getUniqueId(); a.sellerName = player.getName();
        a.item = input.snapshot.clone(); a.start = start;
        a.created = System.currentTimeMillis(); a.expires = a.created + input.duration;
        auctions.put(a.id, a);
        player.setItemInHand(null);
        stats(a.seller).listings++;
        writeState();
        tell(player, "&aAuktion oprettet! Startpris: &e" + moneyText(start) + " $&7.");
        openAuctions(player, 0);
    }
    private void cancelAuction(Player player, String id) {
        Auction a = auctions.get(id);
        if (a == null || !a.seller.equals(player.getUniqueId()) || a.bidder != null) return;
        auctions.remove(id);
        deliver(a.seller, a.item, "Annulleret auktion");
        writeState();
        tell(player, "&aAuktion annulleret. Din vare ligger i postkassen.");
        refresh(new Runnable() { @Override public void run() { openAuctions(player, 0); } });
    }
    private void settleExpired() {
        if (!money.ready()) return;
        long now = System.currentTimeMillis();
        List<Auction> expired = new ArrayList<Auction>();
        for (Auction a : auctions.values()) if (a.expires <= now) expired.add(a);
        if (expired.isEmpty()) return;
        for (Auction a : expired) {
            if (a.bidder == null) {
                deliver(a.seller, a.item, "Auktion udløb uden bud");
                alert(a.seller, "&eDin auktion udløb uden bud.");
            } else {
                deliver(a.bidder, a.item, "Vundet auktion fra " + a.sellerName);
                long feePercent = Math.max(0, Math.min(100,
                    getConfig().getLong("settings.sales-fee-percent", 0)));
                Credit payment = new Credit(); payment.to = a.seller;
                payment.amount = a.bid - (a.bid * feePercent / 100);
                payment.reason = "Solgt auktion " + a.id;
                if (payment.amount > 0) credits.add(payment);
                stats(a.seller).earned += payment.amount;
                stats(a.seller).sales++;
                stats(a.bidder).wins++;
                alert(a.seller,"&aDin auktion blev solgt for &e"+moneyText(a.bid)+" $");
                alert(a.bidder,"&aDu vandt en auktion! Hent varen i din postkasse.");
            }
            auctions.remove(a.id);
        }
        writeState();
    }
    private void processCredits() {
        if (!money.ready() || credits.isEmpty()) return;
        boolean changed = false;
        java.util.Iterator<Credit> iterator = credits.iterator();
        while (iterator.hasNext()) {
            Credit credit = iterator.next();
            if (money.give(credit.to, credit.amount)) {
                iterator.remove(); changed = true;
            }
        }
        if (changed) writeState();
    }
    private void openMail(Player player, int page) {
        List<Parcel> list = mail(player.getUniqueId());
        page = clampPage(page, list.size());
        PcHolder holder = new PcHolder(Screen.MAIL, page, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &e&lPOSTKASSE");
        inv.setItem(4, item(Material.CHEST, "&e&lXENOS POST &8| &fAFHENTNING",
            "&8&m----------------------------",
            "&7Velkommen til din personlige postkasse.",
            "&7Alle køb og vundne auktioner",
            "&7bliver leveret sikkert hertil.",
            " ",
            "&7Afventende leveringer: &e" + list.size(),
            "&7Klik på en pakke for at hente den.",
            "&7Pakker gemmes, til du har plads.",
            "&8&m----------------------------"));
        for (int i = 0; i < CONTENT.length && page * CONTENT.length + i < list.size(); i++) {
            Parcel parcel = list.get(page * CONTENT.length + i);
            ItemStack stack = parcel.item.clone();
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList<String>();
                lore.add(color("&8&m-----------------------"));
                lore.add(color("&7Fra: &f" + parcel.reason));
                lore.add(color("&aKLIK FOR AT HENTE"));
                meta.setLore(lore); stack.setItemMeta(meta);
            }
            inv.setItem(CONTENT[i], stack);
            holder.links.put(CONTENT[i], parcel.id);
        }
        footer(inv, page, list.size());
        inv.setItem(45, item(Material.ARROW, "&c&lFORLAD POSTKASSEN", "&7Afslut afhentningen."));
        inv.setItem(52, item(Material.HOPPER, "&a&lHENT ALLE PAKKER",
            "&8&m----------------------------",
            "&7Forsøger at hente alle dine varer.",
            "&7Der hentes kun hele item-stacks,",
            "&7som der er plads til i inventory.",
            " ", "&7Pakker uden plads bliver liggende.",
            "&a➜ KLIK FOR AT HENTE",
            "&8&m----------------------------"));
        player.openInventory(inv);
    }
    private void collectMail(Player player, String id, int page) {
        List<Parcel> list = mail(player.getUniqueId());
        Parcel target = null;
        for (Parcel parcel : list) if (parcel.id.equals(id)) { target = parcel; break; }
        if (target == null) { refresh(new Runnable() { @Override public void run() { openMail(player, page); } }); return; }
        if (!canFit(player, target.item)) {
            tell(player, "&cDu har ikke plads nok til hele pakken."); return;
        }
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(target.item.clone());
        if (leftover.isEmpty()) {
            list.remove(target);
            writeState();
            tell(player, "&aVaren blev hentet fra postkassen.");
        } else {

            target.item = leftover.values().iterator().next().clone();
            writeState();
            tell(player, "&eDer blev kun plads til en del af varen.");
        }
        refresh(new Runnable() { @Override public void run() { openMail(player, page); } });
    }
    private void collectAllMail(Player player, int page) {
        List<Parcel> list = mail(player.getUniqueId());
        int received = 0;
        boolean changed = false;

        java.util.Iterator<Parcel> iterator = list.iterator();
        while (iterator.hasNext()) {
            Parcel parcel = iterator.next();
            if (!canFit(player, parcel.item)) continue;
            Map<Integer, ItemStack> remainder = player.getInventory().addItem(parcel.item.clone());
            if (remainder.isEmpty()) {
                iterator.remove();
                received++;
                changed = true;
            } else {

                parcel.item = remainder.values().iterator().next().clone();
                changed = true;
                break;
            }
        }
        if (changed) writeState();
        tell(player, received > 0 ? "&aDu har hentet &f" + received + " &apakke(r)!"
            : "&eDer er ingen pakker, som kan være i dit inventory.");
        refresh(new Runnable() { @Override public void run() { openMail(player, page); } });
    }


    private boolean canFit(Player player, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (player.getInventory().firstEmpty() >= 0) return true;

        int free = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.isSimilar(stack)) {
                free += Math.max(0, content.getMaxStackSize() - content.getAmount());
            }
        }
        return free >= stack.getAmount();
    }

    private void openOrders(Player player, int page) {
        List<Order> list = purchases(player.getUniqueId());
        page = clampPage(page, list.size());
        PcHolder holder = new PcHolder(Screen.ORDERS, page, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &b&lORDRER");
        inv.setItem(4, item(Material.BOOK, "&b&lORDREARKIV &8| &fKØBSHISTORIK",
            "&8&m----------------------------",
            "&7Her gemmes dine tidligere køb.",
            "&7Hver ordre viser dato, varenavn",
            "&7og det beløb, du har betalt.",
            " ",
            "&7Samlet antal ordrer: &f" + list.size(),
            "&7Varerne afhentes fysisk i postkassen.",
            "&8&m----------------------------"));
        for (int i = 0; i < CONTENT.length && page * CONTENT.length + i < list.size(); i++) {
            Order order = list.get(list.size() - 1 - (page * CONTENT.length + i));
            inv.setItem(CONTENT[i], item(Material.PAPER, "&f&l" + order.name,
                "&7Pris: &e" + moneyText(order.price) + " $",
                "&7Dato: &f" + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date(order.time)),
                "&8&m----------------------------",
                "&7Betalt: &e" + moneyText(order.price) + " $",
                "&7Leveringssted: &fFysisk postkasse",
                "&8Denne oversigt er kun en kvittering."));
        }
        footer(inv, page, list.size());
        player.openInventory(inv);
    }
    private void openProfile(Player viewer, UUID subject) {
        Stats s = stats(subject);
        PcHolder holder = new PcHolder(Screen.PROFILE, 0, subject.toString());
        Inventory inv = menu(holder, "&5&lXENOS &8- &d&lPROFIL");
        inv.setItem(4, head(nameOf(subject), "&d&l" + nameOf(subject) + " &8| &fHANDELSPROFIL",
            "&8&m----------------------------",
            "&7Xenos Network handelsprofil",
            "&7Se en spillers auktionshistorik",
            "&7og økonomiske statistik.",
            " ",
            "&7Konto-ID: &8" + subject.toString().substring(0, 8) + "...",
            "&8&m----------------------------"));
        inv.setItem(20, item(Material.GOLD_INGOT, "&6&lMINE SALG &8| &fOVERBLIK",
            "&8&m----------------------------",
            "&7Her ser du, hvordan det går",
            "&7med dine annoncer på markedet.",
            " ",
            "&7Auktioner oprettet: &f" + s.listings,
            "&7Auktioner solgt: &a" + s.sales,
            "&8&m----------------------------"));
        inv.setItem(22, item(Material.EMERALD, "&a&lHANDELSØKONOMI",
            "&8&m----------------------------",
            "&7Din omsætning i Xenos Network.",
            " ",
            "&7Køb for: &c" + moneyText(s.spent) + " $",
            "&7Salg for: &a" + moneyText(s.earned) + " $",
            "&8&m----------------------------"));
        inv.setItem(24, item(Material.DIAMOND, "&b&lAUKTIONSSTATISTIK",
            "&8&m----------------------------",
            "&7Din aktivitet som køber.",
            " ",
            "&7Bud afgivet: &f" + s.bids,
            "&7Vundne auktioner: &b" + s.wins,
            "&8&m----------------------------"));
        inv.setItem(45, item(Material.ARROW, "&d&lTILBAGE"));
        inv.setItem(53, item(Material.BARRIER, "&c&lLUK"));
        viewer.openInventory(inv);
    }
    private void openHelp(Player player) {
        PcHolder holder = new PcHolder(Screen.HELP, 0, null);
        Inventory inv = menu(holder, "&5&lXENOS &8- &f&lHJÆLP");
        inv.setItem(4, item(Material.BOOK, "&f&lXENOS SUPPORT &8| &fHJÆLPECENTER",
            "&8&m----------------------------",
            "&7Læs, hvordan du handler sikkert",
            "&7via computeren og får dine varer.",
            "&7Vælg et emne nedenfor.",
            "&8&m----------------------------"));
        inv.setItem(20, item(Material.EMERALD, "&a&lBUTIK", "&7Klik på varer og betal med $.",
            "&7Leveringer lander i postkassen."));
        inv.setItem(22, item(Material.GOLD_INGOT, "&6&lAUKTIONER", "&7Hold varen i hånden og opret en auktion.",
            "&7Andre spillere kan byde.", "&7Når et bud bliver overbudt, refunderes det."));
        inv.setItem(24, item(Material.CHEST, "&e&lFYSISK POSTKASSE",
            "&8&m----------------------------",
            "&7Alle køb og vundne auktioner",
            "&7bliver gemt på din konto.",
            " ",
            "&71. Find en opsat postkasse-blok",
            "&72. Højreklik på postkassen",
            "&73. Klik på de pakker, du vil hente",
            " ",
            "&7Hvis dit inventory er fyldt,",
            "&7bliver resten liggende sikkert.",
            "&8&m----------------------------"));
        inv.setItem(45, item(Material.ARROW, "&d&lTILBAGE"));
        inv.setItem(53, item(Material.BARRIER, "&c&lLUK"));
        player.openInventory(inv);
    }
    private void openBank(Player player) {
        PcHolder holder=new PcHolder(Screen.BANK,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &e&lBANK");
        long balance=money.balance(player.getUniqueId());
        inv.setItem(4,item(Material.GOLD_BLOCK,"&e&lXENOS BANK &8• &fKONTROLRUM",
            "&8&m--------------------------",
            "&7Overfør penge mellem spillere.",
            "&7Overførsler logges med afsender", "&7og modtager for din egen sikkerhed."," ",
            "&7Din saldo: &e"+(balance<0?"Utilgængelig":moneyText(balance)+" $"),
            "&7Bankstatus: "+(money.ready()?"&aONLINE":"&cOFFLINE"),
            "&8&m--------------------------"));
        inv.setItem(20,item(Material.PAPER,"&a&lNY OVERFØRSEL",
            "&7Vælg en online modtager.","&7Indtast beløbet i chatten.",
            "&7Beløbet kan ikke være negativt."," ","&eKLIK FOR AT FORTSÆTTE"));
        inv.setItem(24,item(Material.BOOK,"&f&lTRANSAKTIONSHISTORIK",
            "&7De seneste overførsler gemmes.","&7Se oversigten nedenfor."));
        int pos=28;
        for(Transfer transfer:transfers){
            if(pos>34)break;
            if(!transfer.sender.equals(player.getUniqueId())&&!transfer.receiver.equals(player.getUniqueId()))continue;
            boolean inbound=transfer.receiver.equals(player.getUniqueId());
            inv.setItem(pos++,item(Material.PAPER,inbound?"&a&lMODTAGET":"&c&lSENDT",
                "&7Modpart: &f"+nameOf(inbound?transfer.sender:transfer.receiver),
                "&7Beløb: &e"+moneyText(transfer.amount)+" $",
                "&7Dato: &f"+new SimpleDateFormat("dd/MM HH:mm").format(new Date(transfer.time))));
        }
        inv.setItem(45,item(Material.ARROW,"&d&lSKRIVEBORD"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }

    private void openRecipients(Player player) {
        PcHolder holder=new PcHolder(Screen.RECIPIENTS,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &f&lMODTAGERE");
        inv.setItem(4,item(Material.GOLD_INGOT,"&e&lVÆLG MODTAGER",
            "&7Klik på spilleren, du vil overføre til.",
            "&7Overførsler er endelige. Kontrollér navnet."));
        int cursor=0;
        for(Player target:Bukkit.getOnlinePlayers()){
            if(target.equals(player)||cursor>=CONTENT.length)continue;
            int slot=CONTENT[cursor++];
            inv.setItem(slot,head(target.getName(),"&a&l"+target.getName(),
                "&7Modtager: &f"+target.getName(),"&eKLIK FOR AT INDTASTE BELØB"));
            holder.links.put(slot,target.getUniqueId().toString());
        }
        inv.setItem(45,item(Material.ARROW,"&d&lTILBAGE"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }

    private void openContracts(Player player) {
        PcHolder holder=new PcHolder(Screen.CONTRACTS,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &b&lKONTRAKTER");
        UUID id=player.getUniqueId();
        inv.setItem(4,item(Material.WRITTEN_BOOK,"&b&lKONTRAKTCENTRAL &8| &fUDFORDRINGER",
            "&8&m--------------------------", "&7Vælg en fuldført kontrakt for", "&7at hente din kontante belønning.", " ",
            "&7Daglige hentet: &b"+contracts.completed(id,false)+"&7/3",
            "&7Ugentlige hentet: &5"+contracts.completed(id,true)+"&7/3",
            "&7Nulstilling: &f00:00 dansk tid / mandag", "&8&m--------------------------"));
        drawContracts(inv,contracts.missions(id,false),new int[]{11,13,15},new int[]{20,22,24},false);
        drawContracts(inv,contracts.missions(id,true),new int[]{29,31,33},new int[]{38,40,42},true);
        inv.setItem(9,item(Material.WATCH,"&b&lDAGLIGE", "&7Nye dagskontrakter hver dag."));
        inv.setItem(27,item(Material.COMPASS,"&5&lUGENTLIGE", "&7Nye ugekontrakter hver mandag."));
        inv.setItem(45,item(Material.ARROW,"&d&lSKRIVEBORD"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }
    private void drawContracts(Inventory inv,List<ContractService.Progress> tasks,int[] slots,int[] bars,boolean weekly){
        for(int i=0;i<tasks.size() && i<slots.length;i++){
            ContractService.Progress t=tasks.get(i);
            int percent=Math.min(100,(int)((long)t.count*100L/t.goal));
            inv.setItem(slots[i],item(t.task.icon,(t.claimed?"&a&l✔ ":weekly?"&5&l":"&b&l")+t.task.title,
                "&8&m-----------------------", "&7"+t.task.description," ",
                "&7Fremskridt: &f"+t.count+"&7/&f"+t.goal,
                "&7Belønning: &e"+moneyText(t.reward)+" $", " ",
                t.claimed?"&aALLEREDE HENTET":t.count>=t.goal?"&aKLIK FOR AT HENTE":"&eI GANG",
                "&8&m-----------------------"));
            inv.setItem(bars[i],glass(t.claimed?5:percent>=65?4:7,
                (t.claimed?"&a":"&e")+percent+"% GENNEMFØRT",
                "&7"+t.count+" / "+t.goal));
        }
    }

    private void openMessages(Player player) {
        PcHolder holder=new PcHolder(Screen.FEED,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &d&lBESKEDER");
        List<ActivityFeed.Entry> entries=feed.inbox(player.getUniqueId());
        inv.setItem(4,item(Material.PAPER,"&d&lDIN INDBAKKE",
            "&7Log over handler, kontrakter og", "&7leveringer fra Xenos Network.",
            "&7Seneste hændelser: &f"+entries.size()));
        int n=0;
        for(ActivityFeed.Entry e:entries){
            if(n>=CONTENT.length)break;
            inv.setItem(CONTENT[n++],item(Material.PAPER,"&f&lNOTIFIKATION",
                e.text,"&7Dato: &f"+new SimpleDateFormat("dd/MM HH:mm").format(new Date(e.time))));
        }
        inv.setItem(49,item(Material.BOOK,"&d&lSERVERNYHEDER","&7Læs de officielle nyheder.","&eKLIK"));
        inv.setItem(45,item(Material.ARROW,"&d&lSKRIVEBORD"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }
    private void openNews(Player player){
        PcHolder holder=new PcHolder(Screen.NEWS,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &f&lNYHEDER");
        inv.setItem(4,item(Material.BOOK,"&d&lXENOS NYHEDSCENTER",
            "&7Officielle beskeder fra serveren."));
        int i=0;
        for(ActivityFeed.Entry entry:feed.news()){
            if(i>=CONTENT.length)break;
            inv.setItem(CONTENT[i++],item(Material.PAPER,"&d&lNYHED",
                "&7"+entry.text,
                "&8"+new SimpleDateFormat("dd/MM HH:mm").format(new Date(entry.time))));
        }
        inv.setItem(45,item(Material.ARROW,"&d&lBESKEDCENTER"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }
    private void openRankings(Player player){
        PcHolder holder=new PcHolder(Screen.RANKINGS,0,null);
        Inventory inv=menu(holder,"&5&lXENOS &8- &b&lRANGLISTE");
        inv.setItem(4,item(Material.DIAMOND,"&b&lTOP SÆLGERE",
            "&7Spillere med højest omsætning", "&7fra afsluttede auktioner.","&7Klik for at se en handelsprofil."));
        List<Map.Entry<UUID,Stats>> sorted=new ArrayList<Map.Entry<UUID,Stats>>(stats.entrySet());
        Collections.sort(sorted,new Comparator<Map.Entry<UUID,Stats>>(){
            @Override public int compare(Map.Entry<UUID,Stats> a,Map.Entry<UUID,Stats> b){
                return Long.compare(b.getValue().earned,a.getValue().earned);
            }
        });
        int n=0;
        for(Map.Entry<UUID,Stats> entry:sorted){
            if(n>=CONTENT.length)break;
            int slot=CONTENT[n++];
            inv.setItem(slot,head(nameOf(entry.getKey()),"&b&l#"+n+" &f"+nameOf(entry.getKey()),
                "&7Omsætning: &a"+moneyText(entry.getValue().earned)+" $",
                "&7Salg: &f"+entry.getValue().sales,"&eKLIK FOR AT SE PROFIL"));
            holder.links.put(slot,entry.getKey().toString());
        }
        inv.setItem(45,item(Material.ARROW,"&d&lSKRIVEBORD"));
        inv.setItem(53,item(Material.BARRIER,"&c&lLUK"));
        player.openInventory(inv);
    }

    private void refresh(Runnable action) { Bukkit.getScheduler().runTask(this, action); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Inventory top = event.getView().getTopInventory();
        if (top == null || !(top.getHolder() instanceof PcHolder)) return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= top.getSize()) return;
        final Player player = (Player) event.getWhoClicked();
        final PcHolder holder = (PcHolder) top.getHolder();
        if (slot == 53) { player.closeInventory(); return; }
        switch (holder.screen) {
            case DESKTOP:
                if (slot == 10) refresh(() -> openShop(player, 0));
                else if (slot == 12) refresh(() -> openAuctions(player, 0));
                else if (slot == 14) refresh(() -> openContracts(player));
                else if (slot == 20) refresh(() -> openBank(player));
                else if (slot == 22) refresh(() -> openProfile(player, player.getUniqueId()));
                else if (slot == 24) refresh(() -> openOrders(player, 0));
                else if (slot == 30) mailboxInstructions(player);
                else if (slot == 32) refresh(() -> openRankings(player));
                else if (slot == 34) refresh(() -> openMessages(player));
                break;
            case SHOP:
                if (holder.links.containsKey(slot)) purchase(player, holder.links.get(slot), holder.page);
                else if (slot == 45) refresh(new Runnable() { @Override public void run() { openDesktop(player); } });
                else if (slot == 48 && holder.page > 0) refresh(new Runnable() { @Override public void run() { openShop(player, holder.page - 1); } });
                else if (slot == 50) refresh(new Runnable() { @Override public void run() { openShop(player, holder.page + 1); } });
                break;
            case AUCTIONS:
                if (holder.links.containsKey(slot)) {
                    final String id = holder.links.get(slot);
                    refresh(new Runnable() { @Override public void run() { openAuction(player, id); } });
                } else if (slot == 8) refresh(new Runnable() { @Override public void run() { openSell(player); } });
                else if (slot == 45) refresh(new Runnable() { @Override public void run() { openDesktop(player); } });
                else if (slot == 48 && holder.page > 0) refresh(new Runnable() { @Override public void run() { openAuctions(player, holder.page - 1); } });
                else if (slot == 50) refresh(new Runnable() { @Override public void run() { openAuctions(player, holder.page + 1); } });
                break;
            case AUCTION:
                Auction auction = auctions.get(holder.selected);
                if (auction == null) { tell(player, "&cAuktionen findes ikke længere."); return; }
                if (slot == 20) placeBid(player, auction.id, minBid(auction));
                else if (slot == 22) placeBid(player, auction.id, minBid(auction) + Math.max(10, minBid(auction) / 4));
                else if (slot == 24) {
                    Input bid = new Input(); bid.type = Input.Type.CUSTOM_BID; bid.auctionId = auction.id;
                    awaiting.put(player.getUniqueId(), bid);
                    player.closeInventory(); tell(player, "&dSkriv dit bud i chatten &7(eller &cannuller&7).");
                } else if (slot == 31) refresh(new Runnable() { @Override public void run() { openProfile(player, auction.seller); } });
                else if (slot == 40) cancelAuction(player, auction.id);
                else if (slot == 45) refresh(new Runnable() { @Override public void run() { openAuctions(player, 0); } });
                break;
            case SELL:
                if (slot == 20) promptSell(player, 60L * 60L * 1000L);
                else if (slot == 22) promptSell(player, 6L * 60L * 60L * 1000L);
                else if (slot == 24) promptSell(player, 24L * 60L * 60L * 1000L);
                else if (slot == 45) refresh(new Runnable() { @Override public void run() { openAuctions(player, 0); } });
                break;
            case MAIL:
                if (holder.links.containsKey(slot)) collectMail(player, holder.links.get(slot), holder.page);
                else if (slot == 52) collectAllMail(player, holder.page);
                else if (slot == 45) player.closeInventory();
                else if (slot == 48 && holder.page > 0) refresh(new Runnable() { @Override public void run() { openMail(player, holder.page - 1); } });
                else if (slot == 50) refresh(new Runnable() { @Override public void run() { openMail(player, holder.page + 1); } });
                break;
            case ORDERS:
                if (slot == 45) refresh(new Runnable() { @Override public void run() { openDesktop(player); } });
                else if (slot == 48 && holder.page > 0) refresh(new Runnable() { @Override public void run() { openOrders(player, holder.page - 1); } });
                else if (slot == 50) refresh(new Runnable() { @Override public void run() { openOrders(player, holder.page + 1); } });
                break;
            case BANK:
                if (slot == 20) refresh(() -> openRecipients(player));
                else if (slot == 45) refresh(() -> openDesktop(player));
                break;
            case RECIPIENTS:
                if (holder.links.containsKey(slot)) {
                    UUID recipient=UUID.fromString(holder.links.get(slot));
                    if (Bukkit.getPlayer(recipient) == null) {
                        tell(player,"&cSpilleren er ikke online længere."); return;
                    }
                    Input request=new Input(); request.type=Input.Type.BANK_AMOUNT;
                    request.recipient=recipient;
                    awaiting.put(player.getUniqueId(),request);
                    player.closeInventory();
                    tell(player,"&dSkriv beløbet til &f"+nameOf(recipient)+" &di chatten (eller &cannuller&d).");
                } else if(slot==45)refresh(() -> openBank(player));
                break;
            case CONTRACTS:
                if(slot==45)refresh(() -> openDesktop(player));
                else if ((slot>=11&&slot<=15&&slot%2==1) || (slot>=29&&slot<=33&&slot%2==1)) {
                    boolean weekly=slot>=29;
                    int index=weekly?(slot-29)/2:(slot-11)/2;
                    boolean success=contracts.claim(player.getUniqueId(),weekly,index);
                    tell(player,success?"&aKontrakt afsluttet! Pengene er overført/til udbetaling.":"&eDenne kontrakt er ikke klar eller allerede hentet.");
                    refresh(() -> openContracts(player));
                }
                break;
            case FEED:
                if(slot==45)refresh(() -> openDesktop(player));
                else if(slot==49)refresh(() -> openNews(player));
                break;
            case NEWS:
                if(slot==45)refresh(() -> openMessages(player));
                break;
            case RANKINGS:
                if(holder.links.containsKey(slot)) {
                    UUID subject=UUID.fromString(holder.links.get(slot));
                    refresh(() -> openProfile(player,subject));
                } else if(slot==45)refresh(() -> openDesktop(player));
                break;
            case PROFILE: case HELP:
                if (slot == 45) refresh(new Runnable() { @Override public void run() { openDesktop(player); } });
                break;
            default: break;
        }
    }
    @EventHandler public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top != null && top.getHolder() instanceof PcHolder) {
            for (Integer slot : event.getRawSlots()) {
                if (slot < top.getSize()) { event.setCancelled(true); return; }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        final UUID who = event.getPlayer().getUniqueId();
        if (!awaiting.containsKey(who)) return;
        event.setCancelled(true);
        final String message = event.getMessage().trim();

        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override public void run() {
                Player player = Bukkit.getPlayer(who);
                if (player == null) { awaiting.remove(who); return; }
                Input input = awaiting.remove(who);
                if (input == null) return;
                if (message.equalsIgnoreCase("annuller") || message.equalsIgnoreCase("cancel")) {
                    tell(player, "&eHandling annulleret."); return;
                }
                try {
                    long value = Long.parseLong(message);
                    if (value <= 0L) throw new NumberFormatException();
                    if (input.type == Input.Type.SELL_PRICE) createAuction(player, input, value);
                    else if (input.type == Input.Type.CUSTOM_BID) placeBid(player, input.auctionId, value);
                    else if (input.type == Input.Type.BANK_AMOUNT) transfer(player, input.recipient, value);
                } catch (NumberFormatException ex) {
                    tell(player, "&cUgyldigt beløb. Skriv kun et helt tal, f.eks. 1500. Start forfra.");
                }
            }
        });
    }


    private void readState() {
        if (!stateFile.exists()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(stateFile);
        computers.addAll(y.getStringList("computers"));
        postboxes.addAll(y.getStringList("postboxes"));
        ConfigurationSection section = y.getConfigurationSection("auctions");
        if (section != null) for (String id : section.getKeys(false)) {
            String p = "auctions." + id + ".";
            try {
                Auction a = new Auction(); a.id = id;
                a.seller = UUID.fromString(y.getString(p + "seller"));
                a.sellerName = y.getString(p + "sellerName", "Ukendt");
                String bidder = y.getString(p + "bidder", "");
                if (bidder != null && !bidder.isEmpty()) a.bidder = UUID.fromString(bidder);
                a.item = y.getItemStack(p + "item");
                a.created = y.getLong(p + "created"); a.expires = y.getLong(p + "expires");
                a.start = y.getLong(p + "start"); a.bid = y.getLong(p + "bid");
                if (a.item != null) auctions.put(id, a);
            } catch (RuntimeException ex) { getLogger().warning("Skipping invalid auction " + id); }
        }
        readParcels(y);
        readOrders(y);
        contracts.load(y);
        feed.load(y);
        for (Map<?,?> row : y.getMapList("bank.transfers")) {
            try {
                Transfer t=new Transfer();
                t.sender=UUID.fromString(String.valueOf(row.get("from")));
                t.receiver=UUID.fromString(String.valueOf(row.get("to")));
                t.amount=Long.parseLong(String.valueOf(row.get("amount")));
                t.time=Long.parseLong(String.valueOf(row.get("time")));
                if(t.amount>0 && transfers.size()<150)transfers.add(t);
            } catch (RuntimeException ignored) {getLogger().warning("Invalid bank log entry ignored");}
        }
        section = y.getConfigurationSection("stats");
        if (section != null) for (String id : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(id); String p = "stats." + id + ".";
                Stats s = new Stats(); s.spent = y.getLong(p + "spent"); s.earned = y.getLong(p + "earned");
                s.listings = y.getInt(p + "listings"); s.sales = y.getInt(p + "sales");
                s.wins = y.getInt(p + "wins"); s.bids = y.getInt(p + "bids"); stats.put(uuid, s);
            } catch (RuntimeException ex) { getLogger().warning("Skipping invalid stats record"); }
        }
        for (Map<?, ?> saved : y.getMapList("credits")) {
            try {
                Credit credit = new Credit(); credit.to = UUID.fromString(String.valueOf(saved.get("to")));
                credit.amount = Long.parseLong(String.valueOf(saved.get("amount")));
                credit.reason = String.valueOf(saved.get("reason"));
                if (credit.amount > 0) credits.add(credit);
            } catch (RuntimeException ex) { getLogger().warning("Skipping invalid credit"); }
        }
    }
    private void readParcels(YamlConfiguration y) {
        ConfigurationSection section = y.getConfigurationSection("mail");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(id);
                ConfigurationSection parcels = y.getConfigurationSection("mail." + id);
                if (parcels == null) continue;
                for (String key : parcels.getKeys(false)) {
                    String path = "mail." + id + "." + key + ".";
                    ItemStack stack = y.getItemStack(path + "item");
                    if (stack == null) continue;
                    Parcel parcel = new Parcel(); parcel.id = key; parcel.item = stack;
                    parcel.reason = y.getString(path + "reason", "Levering"); parcel.time = y.getLong(path + "time");
                    mail(uuid).add(parcel);
                }
            } catch (RuntimeException ex) { getLogger().warning("Skipping invalid mail record"); }
        }
    }
    private void readOrders(YamlConfiguration y) {
        ConfigurationSection section = y.getConfigurationSection("orders");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(id);
                ConfigurationSection entries = y.getConfigurationSection("orders." + id);
                if (entries == null) continue;
                for (String key : entries.getKeys(false)) {
                    String path = "orders." + id + "." + key + ".";
                    Order o = new Order(); o.name = y.getString(path + "name", "Vare");
                    o.price = y.getLong(path + "price"); o.time = y.getLong(path + "time");
                    purchases(uuid).add(o);
                }
            } catch (RuntimeException ex) { getLogger().warning("Skipping invalid order record"); }
        }
    }
    private void writeState() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("computers", new ArrayList<String>(computers));
        y.set("postboxes", new ArrayList<String>(postboxes));
        for (Auction a : auctions.values()) {
            String p = "auctions." + a.id + ".";
            y.set(p + "seller", a.seller.toString()); y.set(p + "sellerName", a.sellerName);
            y.set(p + "bidder", a.bidder == null ? "" : a.bidder.toString());
            y.set(p + "item", a.item); y.set(p + "created", a.created); y.set(p + "expires", a.expires);
            y.set(p + "start", a.start); y.set(p + "bid", a.bid);
        }
        for (Map.Entry<UUID, List<Parcel>> e : mailbox.entrySet()) for (Parcel parcel : e.getValue()) {
            String p = "mail." + e.getKey() + "." + parcel.id + ".";
            y.set(p + "item", parcel.item); y.set(p + "reason", parcel.reason); y.set(p + "time", parcel.time);
        }
        for (Map.Entry<UUID, List<Order>> e : orders.entrySet()) {
            int idx = 0;
            for (Order order : e.getValue()) {
                String p = "orders." + e.getKey() + "." + (idx++) + ".";
                y.set(p + "name", order.name); y.set(p + "price", order.price); y.set(p + "time", order.time);
            }
        }
        for (Map.Entry<UUID, Stats> e : stats.entrySet()) {
            String p = "stats." + e.getKey() + "."; Stats s = e.getValue();
            y.set(p + "spent", s.spent); y.set(p + "earned", s.earned);
            y.set(p + "listings", s.listings); y.set(p + "sales", s.sales);
            y.set(p + "wins", s.wins); y.set(p + "bids", s.bids);
        }
        List<Map<String, Object>> pending = new ArrayList<Map<String, Object>>();
        for (Credit credit : credits) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("to", credit.to.toString()); m.put("amount", credit.amount); m.put("reason", credit.reason);
            pending.add(m);
        }
        y.set("credits", pending);
        contracts.save(y);
        feed.save(y);
        List<Map<String,Object>> banklog=new ArrayList<Map<String,Object>>();
        for(Transfer t:transfers){
            Map<String,Object> row=new HashMap<String,Object>();
            row.put("from",t.sender.toString());row.put("to",t.receiver.toString());
            row.put("amount",t.amount);row.put("time",t.time);banklog.add(row);
        }
        y.set("bank.transfers",banklog);
        try {


            File temp = new File(getDataFolder(), "state.yml.tmp");
            y.save(temp);
            try {
                Files.move(temp.toPath(), stateFile.toPath(),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp.toPath(), stateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            dirty = false;
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Failed to save state.yml. Stop transactions and check disk permissions.", ex);
        }
    }
}
