package onebeastchris.placebook.forms;

import net.minecraft.server.network.ServerPlayerEntity;
import onebeastchris.placebook.util.FloodgateUtil;
import onebeastchris.placebook.util.PlayerDataCache;
import onebeastchris.placebook.util.PlayerStorage;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;

public class DeleteForm implements FormInterface {

        public static ModalForm.Builder sendForm(ServerPlayerEntity player, Form previousForm, String placeName, int index) {
            return ModalForm.builder()
                    .title("Delete Place")
                    .content("Are you sure you want to delete " + placeName + "?")
                    .button1("Yes")
                    .button2("No")
                    .validResultHandler((form, response) -> {
                        switch (response.clickedButtonId()) {
                            case 0 -> {
                                PlayerStorage.removeHome(player, index);
                                FloodgateUtil.sendForm(player, previousForm);
                            }
                            case 1 ->
                                //go back
                                FloodgateUtil.sendForm(player, previousForm);
                        }
                    });
        }
}