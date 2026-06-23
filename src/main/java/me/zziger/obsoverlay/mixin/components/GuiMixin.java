package me.zziger.obsoverlay.mixin.components;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.AllDefaultOverlayComponents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public class GuiMixin {
    @ModifyVariable(method = "extractScoreboardSidebar", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartScoreboard(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.scoreboards, value);
    }

    @ModifyVariable(method = "extractOverlayMessage", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartActionbar(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.actionbar, value);
    }

    @ModifyVariable(method = "extractTitle", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartTitleSubtitle(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.titleSubtitle, value);
    }

    @ModifyVariable(method = "extractEffects", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartEffects(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.effects, value);
    }

    @ModifyVariable(method = "extractHotbarAndDecorations", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartMainHud(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.mainHud, value);
    }

    @ModifyVariable(method = "extractChat", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartChat(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.chat, value);
    }

    @ModifyVariable(method = "extractTabList", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartPlayerList(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.playerList, value);
    }

    @ModifyVariable(method = "extractSubtitleOverlay", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor drawStartSubtitles(GuiGraphicsExtractor value) {
        return OBSOverlay.getAPI().getGuiGraphicsExtractor(AllDefaultOverlayComponents.subtitles, value);
    }
}