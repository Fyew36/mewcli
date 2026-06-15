package mew.module.modules;

import mew.Mew;
import mew.enums.BlinkModules;
import mew.event.EventTarget;
import mew.event.types.EventType;
import mew.event.types.Priority;
import mew.events.LoadWorldEvent;
import mew.events.TickEvent;
import mew.module.Module;
import mew.property.properties.IntProperty;
import mew.property.properties.ModeProperty;

public class Blink extends Module {
    public final ModeProperty mode = new ModeProperty("mode", 0, new String[]{"DEFAULT", "PULSE"});
    public final IntProperty ticks = new IntProperty("ticks", 20, 0, 1200);

    public Blink() {
        super("Blink", false);
    }

    @EventTarget(Priority.LOWEST)
    public void onTick(TickEvent event) {
        if (this.isEnabled() && event.getType() == EventType.POST) {
            if (!Mew.blinkManager.getBlinkingModule().equals(BlinkModules.BLINK)) {
                this.setEnabled(false);
            } else {
                if (this.ticks.getValue() > 0 && Mew.blinkManager.countMovement() > (long) this.ticks.getValue()) {
                    switch (this.mode.getValue()) {
                        case 0:
                            this.setEnabled(false);
                            break;
                        case 1:
                            Mew.blinkManager.setBlinkState(false, BlinkModules.BLINK);
                            Mew.blinkManager.setBlinkState(true, BlinkModules.BLINK);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onWorldLoad(LoadWorldEvent event) {
        this.setEnabled(false);
    }

    @Override
    public void onEnabled() {
        Mew.blinkManager.setBlinkState(false, Mew.blinkManager.getBlinkingModule());
        Mew.blinkManager.setBlinkState(true, BlinkModules.BLINK);
    }

    @Override
    public void onDisabled() {
        Mew.blinkManager.setBlinkState(false, BlinkModules.BLINK);
    }
}
