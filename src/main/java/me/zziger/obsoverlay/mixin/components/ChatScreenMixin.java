package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @ModifyVariable(method = "extractRenderState", at = @At(value = "HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStart(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.chatBar, value);
    }
}
