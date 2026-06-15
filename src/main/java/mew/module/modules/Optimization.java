package mew.module.modules;

import mew.module.Module;
import mew.property.properties.BooleanProperty;

public class Optimization extends Module {
    public final BooleanProperty noWeather = new BooleanProperty("No Weather", true);
    public final BooleanProperty noParticles = new BooleanProperty("No Particles", true);
    public final BooleanProperty noOverlays = new BooleanProperty("No Overlays", true);
    public final BooleanProperty noBossBar = new BooleanProperty("No Boss Bar", false);

    public Optimization() {
        super("Optimization", false);
    }

    @Override
    public String getCategory() { return "Misc"; }
}
