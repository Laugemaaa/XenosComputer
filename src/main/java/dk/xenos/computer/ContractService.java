package dk.xenos.computer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;


public final class ContractService {
    public static final class Task {
        public final String id, title, kind, description;
        public final Material icon;
        public final int dailyGoal, weeklyGoal;
        public final long dailyReward, weeklyReward;
        Task(String id, String title, String kind, String description, Material icon,
             int dailyGoal, int weeklyGoal, long dailyReward, long weeklyReward) {
            this.id = id;
            this.title = title;
            this.kind = kind;
            this.description = description;
            this.icon = icon;
            this.dailyGoal = dailyGoal;
            this.weeklyGoal = weeklyGoal;
            this.dailyReward = dailyReward;
            this.weeklyReward = weeklyReward;
        }
    }
    public static final class Progress {
        public final Task task;
        public final int count, goal;
        public final long reward;
        public final boolean claimed;
        Progress(Task task, int count, int goal, long reward, boolean claimed) {
            this.task=task; this.count=count; this.goal=goal; this.reward=reward; this.claimed=claimed;
        }
    }
    private static final class Cycle {
        String key;
        final Map<String, Integer> count = new HashMap<String, Integer>();
        final List<String> claimed = new ArrayList<String>();
    }
    private static final class Profile {
        Cycle daily = new Cycle();
        Cycle weekly = new Cycle();
    }
    private final Map<UUID, Profile> profiles = new HashMap<UUID, Profile>();
    private final XenosComputer plugin;
    private final Map<String, Task> tasks = new LinkedHashMap<String, Task>();
    private static final ZoneId ZONE = ZoneId.of("Europe/Copenhagen");

    public ContractService(XenosComputer plugin) {
        this.plugin=plugin;
        add(new Task("stone", "Stenhugger", "STONE", "Bryd almindelige stenblokke.", Material.STONE, 96, 768, 400, 3000));
        add(new Task("coal", "Kulminør", "COAL", "Udvind kulmalm i minerne.", Material.COAL_ORE, 20, 120, 600, 4000));
        add(new Task("iron", "Jernjæger", "IRON", "Udvind værdifuld jernmalm.", Material.IRON_ORE, 12, 96, 750, 4500));
        add(new Task("wood", "Skovarbejder", "WOOD", "Fæld naturlige eller plantede stammer.", Material.LOG, 48, 320, 450, 3500));
        add(new Task("gravel", "Grusgraveren", "GRAVEL", "Bryd grusblokke.", Material.GRAVEL, 80, 640, 350, 2700));
        add(new Task("sand", "Sandmester", "SAND", "Saml sand fra verdenen.", Material.SAND, 80, 640, 350, 2700));
        add(new Task("zombie", "Zombie Hunter", "ZOMBIE", "Bekæmp zombier i verdenen.", Material.ROTTEN_FLESH, 12, 75, 800, 5000));
        add(new Task("skeleton", "Skeletjæger", "SKELETON", "Bekæmp skeletter i verdenen.", Material.BONE, 12, 75, 800, 5000));
        add(new Task("fish", "Fiskeren", "FISH", "Fang fisk med fiskestangen.", Material.RAW_FISH, 8, 50, 750, 4700));
    }
    private void add(Task task) { tasks.put(task.id, task); }
    private String today() { return LocalDate.now(ZONE).toString(); }
    private String monday() {
        return LocalDate.now(ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
    }
    private Profile profile(UUID uuid) {
        Profile p = profiles.get(uuid);
        if (p == null) { p = new Profile(); profiles.put(uuid,p); }
        if (!today().equals(p.daily.key)) { p.daily = new Cycle(); p.daily.key=today(); }
        if (!monday().equals(p.weekly.key)) { p.weekly = new Cycle(); p.weekly.key=monday(); }
        return p;
    }

    private List<Task> selection(UUID uuid, String key, boolean weekly) {
        List<Task> chosen = new ArrayList<Task>();
        for (String id : ContractSelector.pick(uuid, key, weekly,
                new ArrayList<String>(tasks.keySet()), 3)) {
            chosen.add(tasks.get(id));
        }
        return chosen;
    }
    public List<Progress> missions(UUID uuid, boolean weekly) {
        Profile p = profile(uuid);
        Cycle c = weekly ? p.weekly : p.daily;
        List<Progress> list = new ArrayList<Progress>();
        for (Task t : selection(uuid, c.key, weekly)) {
            int goal = weekly ? t.weeklyGoal : t.dailyGoal;
            list.add(new Progress(t, Math.min(goal, c.count.containsKey(t.id) ? c.count.get(t.id) : 0),
                goal, weekly ? t.weeklyReward : t.dailyReward, c.claimed.contains(t.id)));
        }
        return list;
    }
    public void progress(UUID uuid, String kind) {
        Profile profile = profile(uuid);
        boolean modified=false;
        for (boolean weekly : new boolean[] {false, true}) {
            Cycle cycle = weekly ? profile.weekly : profile.daily;
            for (Task task : selection(uuid, cycle.key, weekly)) {
                if (!task.kind.equals(kind) || cycle.claimed.contains(task.id)) continue;
                int current = cycle.count.containsKey(task.id) ? cycle.count.get(task.id) : 0;
                int goal = weekly ? task.weeklyGoal : task.dailyGoal;
                if (current < goal) {
                    cycle.count.put(task.id, current + 1);
                    modified = true;
                    if (current + 1 == goal) plugin.alert(uuid, "&aKontrakt klar: &f" + task.title + "&a! Åbn Kontraktcentralen.");
                }
            }
        }

        if (modified) plugin.markDirty();
    }
    public boolean claim(UUID uuid, boolean weekly, int index) {
        List<Progress> list = missions(uuid, weekly);
        if (index < 0 || index >= list.size()) return false;
        Progress task = list.get(index);
        if (task.claimed || task.count < task.goal) return false;
        Cycle cycle = weekly ? profile(uuid).weekly : profile(uuid).daily;
        cycle.claimed.add(task.task.id);

        plugin.queueCredit(uuid, task.reward, "Kontrakt: " + task.task.title);
        plugin.alert(uuid, "&aKontraktbelønning: &e" + task.reward + "$ &7for &f" + task.task.title);
        plugin.saveNow();
        return true;
    }
    public int completed(UUID uuid, boolean weekly) { return (weekly ? profile(uuid).weekly : profile(uuid).daily).claimed.size(); }
    public void load(YamlConfiguration y) {
        ConfigurationSection sec=y.getConfigurationSection("contracts");
        if (sec == null) return;
        for (String id:sec.getKeys(false)) {
            try {
                UUID uuid=UUID.fromString(id);
                Profile p=new Profile();
                for (boolean weekly:new boolean[]{false,true}) {
                    String base="contracts."+id+"."+(weekly?"weekly":"daily")+".";
                    Cycle c=weekly?p.weekly:p.daily;
                    c.key=y.getString(base+"key", "");
                    c.claimed.addAll(y.getStringList(base+"claimed"));
                    ConfigurationSection counts=y.getConfigurationSection(base+"counts");
                    if(counts!=null)for(String task:counts.getKeys(false)){
                        if(tasks.containsKey(task))c.count.put(task,Math.max(0,counts.getInt(task)));
                    }
                }
                profiles.put(uuid,p);
            } catch (IllegalArgumentException e) { plugin.getLogger().warning("Ignoring invalid contract profile: "+id); }
        }
    }
    public void save(YamlConfiguration y) {
        for(Map.Entry<UUID,Profile> entry:profiles.entrySet()) {
            String base="contracts."+entry.getKey()+".";
            for(boolean weekly:new boolean[]{false,true}) {
                Cycle c=weekly?entry.getValue().weekly:entry.getValue().daily;
                String path=base+(weekly?"weekly":"daily")+".";
                y.set(path+"key",c.key);
                y.set(path+"claimed",new ArrayList<String>(c.claimed));
                y.set(path+"counts",new HashMap<String,Integer>(c.count));
            }
        }
    }
}
