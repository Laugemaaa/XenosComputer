package dk.xenos.computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


public final class ActivityFeed {
    public static final class Entry {
        public final long time;
        public final String text;
        Entry(long time, String text) { this.time=time; this.text=text; }
    }
    private final Map<UUID,List<Entry>> inbox = new HashMap<UUID,List<Entry>>();
    private final List<Entry> announcements = new ArrayList<Entry>();

    public void add(UUID who, String text) {
        List<Entry> list=inbox.get(who);
        if(list==null){list=new ArrayList<Entry>(); inbox.put(who,list);}
        list.add(0,new Entry(System.currentTimeMillis(),text));
        while(list.size()>35)list.remove(list.size()-1);
    }
    public void publish(String text) {
        announcements.add(0,new Entry(System.currentTimeMillis(),text));
        while(announcements.size()>20)announcements.remove(announcements.size()-1);
    }
    public List<Entry> inbox(UUID who) {
        List<Entry> list=inbox.get(who);
        return list==null?new ArrayList<Entry>():new ArrayList<Entry>(list);
    }
    public List<Entry> news() {return new ArrayList<Entry>(announcements);}
    public void load(YamlConfiguration y) {
        ConfigurationSection sec=y.getConfigurationSection("activity.inbox");
        if(sec!=null)for(String id:sec.getKeys(false)){
            try {
                UUID uuid=UUID.fromString(id);
                int count=0;
                for(Map<?,?> item:y.getMapList("activity.inbox."+id)){
                    if(count++>=35)break;
                    Object time=item.get("time"),text=item.get("text");
                    if(time instanceof Number && text!=null)addLoaded(uuid,((Number)time).longValue(),String.valueOf(text));
                }
            }catch(IllegalArgumentException ignored){}
        }
        for(Map<?,?> item:y.getMapList("activity.news")) {
            Object time=item.get("time"),text=item.get("text");
            if(time instanceof Number && text!=null && announcements.size()<20)
                announcements.add(new Entry(((Number)time).longValue(),String.valueOf(text)));
        }
    }
    private void addLoaded(UUID uuid,long time,String text){
        List<Entry> list=inbox.get(uuid);
        if(list==null){list=new ArrayList<Entry>();inbox.put(uuid,list);}
        list.add(new Entry(time,text));
    }
    public void save(YamlConfiguration y) {
        for(Map.Entry<UUID,List<Entry>> item:inbox.entrySet())y.set("activity.inbox."+item.getKey(),serialized(item.getValue()));
        y.set("activity.news",serialized(announcements));
    }
    private List<Map<String,Object>> serialized(List<Entry> entries){
        List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
        for(Entry e:entries){
            Map<String,Object> entry=new HashMap<String,Object>();
            entry.put("time",e.time); entry.put("text",e.text); result.add(entry);
        }
        return result;
    }
}
