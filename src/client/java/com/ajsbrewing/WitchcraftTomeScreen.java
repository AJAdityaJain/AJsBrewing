package com.ajsbrewing;

import com.ajsbrewing.items.VialItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.nbt.NbtElement.COMPOUND_TYPE;

public class WitchcraftTomeScreen extends Screen {
    List<NbtCompound> list = new ArrayList<>();
    protected WitchcraftTomeScreen(PacketByteBuf buf) {
        super(Text.literal("Witchcraft Tome"));
        NbtList recipesNbt = buf.readNbt().getList("recipes", COMPOUND_TYPE);
        for (int i = 0; i < recipesNbt.size(); i++) {
            list.add(recipesNbt.getCompound(i));
            AJsBrewingMod.LOGGER.error(recipesNbt.getCompound(i).toString());
        }
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        for (int y = 0; y < list.size(); y++) {
            renderRecipe(ctx,y,mouseX,mouseY);
        }
    }

    private void renderRecipe(DrawContext ctx, int y, int mouseX, int mouseY){
        int x = width/4;
        renderVial(ctx,x,y,mouseX,mouseY);
        renderIngredients(ctx,x,y);
    }

    private void renderVial(DrawContext ctx,int x, int y, int mouseX, int mouseY) {
        NbtList effectsNbt = list.get(y).getList("effects", NbtElement.COMPOUND_TYPE);
        List<StatusEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < effectsNbt.size(); i++) {
            effects.add(StatusEffectInstance.fromNbt(effectsNbt.getCompound(i)));
        }

//        ctx.drawText(textRenderer, "Vial"+list.get(y).getInt("seed"), x, y * 20,0xFAF123,true);
        ItemStack vial = new ItemStack(VialItem.INSTANCE,1);
        PotionUtil.setCustomPotionEffects(vial, effects);
        ctx.drawItem(vial, x, y * 20);
        if(isMouseOver(x, y * 20, 20, 20, mouseX, mouseY)) {
            ctx.drawItemTooltip(textRenderer, vial, mouseX, mouseY);
        }

    }
    private void renderIngredients(DrawContext ctx, int x, int y) {
        NbtList ingredientsNbt = list.get(y).getList("ingredients", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < ingredientsNbt.size(); i++) {
            NbtCompound ingredientNbt = ingredientsNbt.getCompound(i);
            ctx.drawItem(ItemStack.fromNbt(ingredientNbt), x+((1+i) * 20), y * 20);
        }
    }

    public static boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double)x && mouseX < (double)(x + width) && mouseY >= (double)y && mouseY < (double)(y + height);
    }
}