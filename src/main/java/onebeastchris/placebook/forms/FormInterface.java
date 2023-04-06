package onebeastchris.placebook.forms;

import net.minecraft.server.network.ServerPlayerEntity;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;

import java.util.List;
public interface FormInterface {
    public static FormBuilder sendForm(ServerPlayerEntity player, Form previousForm, String... args) {
        return null;
    };

    private static List<String> parseArgs(String... args) {
        return null;
    };
}