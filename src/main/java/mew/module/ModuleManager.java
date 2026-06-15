package mew.module;

import mew.Mew;
import mew.event.EventTarget;
import mew.event.types.EventType;
import mew.events.KeyEvent;
import mew.events.TickEvent;
import mew.module.modules.GuiModule;
import mew.module.modules.HUD;
import mew.util.ChatUtil;
import mew.util.SoundUtil;

import java.util.*;
import java.util.stream.*;

public class ModuleManager {

    private static final List<String> CATEGORIES = List.of("Combat", "Movement", "Render", "Player", "Misc", "Latency", "Minigames", "Target");
    private boolean sound = false;
    public final LinkedHashMap<Class<?>, Module> modules = new LinkedHashMap<>();

    public Module getModule(String string) {
        return this.modules.values().stream().filter(mD -> mD.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public Module getModule(Class<?> clazz){
        return this.modules.get(clazz);
    }

    public LinkedHashMap<String, List<Module>> getModulesByCategory() {
        LinkedHashMap<String, List<Module>> categories = new LinkedHashMap<>();
        for (String name : CATEGORIES) {
            categories.put(name, new ArrayList<>());
        }
        for (Module module : modules.values()) {
            String cat = module.getCategory();
            List<Module> list = categories.get(cat);
            if (list == null) list = categories.get("Misc");
            if (list != null) list.add(module);
        }
        return categories;
    }

    public void playSound() {
        this.sound = true;
    }

    @EventTarget
    public void onKey(KeyEvent event) {
        for (Module module : this.modules.values()) {
            if (module.getKey() != event.getKey()) {
                continue;
            }
            boolean shouldNotify = module.toggle();
            HUD hud = (HUD) this.modules.get(HUD.class);
            if (hud != null && shouldNotify) {
                shouldNotify = hud.toggleAlerts.getValue();
            }
            if(module instanceof GuiModule){
                shouldNotify = false;
            }
            if (shouldNotify) {
                String status = module.isEnabled() ? "&a&lON" : "&c&lOFF";
                String message = String.format("%s%s: %s&r", Mew.clientName, module.getName(), status);
                ChatUtil.sendFormatted(message);
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (event.getType() == EventType.PRE) {
            if (this.sound) {
                this.sound = false;
                SoundUtil.playSound("random.click");
            }
        }
    }
}
