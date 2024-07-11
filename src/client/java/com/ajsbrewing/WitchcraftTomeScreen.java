package com.ajsbrewing;

import com.ajsbrewing.blocks.Preparers;
import com.ajsbrewing.items.VialItem;
import net.minecraft.block.MagmaBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.nbt.NbtElement.COMPOUND_TYPE;

public class WitchcraftTomeScreen extends Screen {
    List<NbtCompound> list = new ArrayList<>();
    static final Identifier SLOT_TEXTURE = new Identifier("container/slot");

    protected WitchcraftTomeScreen(PacketByteBuf buf) {
        super(Text.literal("Witchcraft Tome"));
        NbtCompound tag = buf.readNbt();
        if (tag != null) {
            NbtList recipesNbt = tag.getList("recipes", COMPOUND_TYPE);
            for (int i = 0; i < recipesNbt.size(); i++) {
                list.add(recipesNbt.getCompound(i));
                AJsBrewingMod.LOGGER.error(recipesNbt.getCompound(i).toString());
            }
        }
    }

    @Override
    protected void init() {
        super.init();


//        addDrawable(new TexturedButtonWidget(
//                ButtonTextures
//        ));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
    }
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        renderItems(ctx, mouseX, mouseY);
    }

    private void renderItems(DrawContext ctx, int mouseX, int mouseY) {
//        ctx.drawSprite(0,0,0,0,0,Items.CAMPFIRE.);



        for (int i = 0; i < list.size(); i++) {
            int x = (i%8)*20;
            int y = (i/8)*20;

            renderVial(ctx, x, y, mouseX, mouseY,getRecipeVial(i));
        }
//        renderIngredients(ctx,x,y);
    }

    private ItemStack getRecipeVial(int i) {
        NbtList effectsNbt = list.get(i).getList("effects", NbtElement.COMPOUND_TYPE);
        List<StatusEffectInstance> effects = new ArrayList<>();
        for (int j = 0; j < effectsNbt.size(); j++) {
            effects.add(StatusEffectInstance.fromNbt(effectsNbt.getCompound(i)));
        }
        ItemStack vial = new ItemStack(VialItem.INSTANCE, 1);
        PotionUtil.setCustomPotionEffects(vial, effects);
        return vial;
    }

    private void renderVial(DrawContext ctx, int x, int y, int mouseX, int mouseY, ItemStack item) {

        ctx.drawGuiTexture(SLOT_TEXTURE, x+1, y+1, 0, 18, 18);
        ctx.drawItemWithoutEntity(item, x + 2, y + 2);

//        ctx.drawItem(getPreparer(Preparers.values()[list.get(y).getInt("preparer")]), x + 20, y * 20);
        if (isMouseOver(x, y * 20, 20, 20, mouseX, mouseY)) {
            ctx.drawItemTooltip(textRenderer, item, mouseX, mouseY);
        }

    }

    private void renderIngredients(DrawContext ctx, int x, int y) {
        NbtList ingredientsNbt = list.get(y).getList("ingredients", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < ingredientsNbt.size(); i++) {
            NbtCompound ingredientNbt = ingredientsNbt.getCompound(i);
            ctx.drawItem(ItemStack.fromNbt(ingredientNbt), x + ((1 + i) * 20), y * 20);
        }
    }

    public static boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) x && mouseX < (double) (x + width) && mouseY >= (double) y && mouseY < (double) (y + height);
    }

    public ItemStack getPreparer(Preparers preparer) {
        AJsBrewingMod.LOGGER.info(preparer.toString());
        return switch (preparer) {
            case MAGMA -> new ItemStack(Items.MAGMA_BLOCK, 1);
            case FIRE -> new ItemStack(Items.CAMPFIRE, 1);
            case LAVA -> new ItemStack(Items.LAVA_BUCKET, 1);
            case SOUL_FIRE -> new ItemStack(Items.SOUL_CAMPFIRE, 1);
            default -> new ItemStack(Items.ICE, 1);
        };
    }
}