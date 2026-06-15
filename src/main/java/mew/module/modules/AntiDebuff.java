package mew.module.modules;

import mew.module.Module;
import mew.property.properties.BooleanProperty;

public class AntiDebuff extends Module {
    public final BooleanProperty blindness = new BooleanProperty("blindness", true);
    public final BooleanProperty nausea = new BooleanProperty("nausea", true);

    public AntiDebuff() {
        super("AntiDebuff", false);
    }

    @Override
    public String getCategory() { return "Combat"; }
}
