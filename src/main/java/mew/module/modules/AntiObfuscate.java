package mew.module.modules;

import mew.module.Module;

public class AntiObfuscate extends Module {
    public AntiObfuscate() {
        super("AntiObfuscate", false, true);
    }

    @Override
    public String getCategory() { return "Render"; }

    public String stripObfuscated(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("§k", "");
    }
}
