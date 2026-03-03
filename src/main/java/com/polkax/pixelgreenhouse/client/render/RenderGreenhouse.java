package com.polkax.pixelgreenhouse.client.render;

import com.polkax.pixelgreenhouse.blocks.BlockGreenhouse;
import com.polkax.pixelgreenhouse.tileentity.TileEntityGreenhouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import com.pixelmonmod.pixelmon.items.ItemApricorn;
import com.pixelmonmod.pixelmon.enums.items.EnumApricorns;
import com.pixelmonmod.pixelmon.client.render.tileEntities.RenderTileEntityApricornTrees;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class RenderGreenhouse extends TileEntitySpecialRenderer<TileEntityGreenhouse> {
    private static Block APRICORN_BLOCK;
    private static IProperty<Integer> AGE_PROP;
    private static IProperty<?> COLOR_PROP;
    private static int AGE_MAX = -1;

    @Override
    public void render(TileEntityGreenhouse te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = EnumFacing.NORTH;
        if (state.getPropertyKeys().contains(BlockGreenhouse.FACING)) {
            facing = state.getValue(BlockGreenhouse.FACING);
        }
        float rotY = 0f;
        switch (facing) {
            case SOUTH: rotY = 180f; break;
            case WEST: rotY = 90f; break;
            case EAST: rotY = -90f; break;
            default: rotY = 0f; break;
        }

        GlStateManager.translate(0.5, 0.0, 0.5);
        GlStateManager.rotate(rotY, 0f, 1f, 0f);
        GlStateManager.translate(-0.5, 0.0, -0.5);

        int growthProgress = te.getGrowthProgress();
        int growthTime = te.getGrowthTime();
        float progressFrac = growthTime > 0 ? Math.min(1.0f, (float) growthProgress / (float) growthTime) : 0f;

        float baseY = 3f / 16f;
        float cellScale = 0.20f;
        float eps = 0.040f;

        long time = Minecraft.getSystemTime();

        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        for (int i = 0; i < 6; i++) {
            ItemStack stack = te.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            int row = i / 3;
            int col = i % 3;
            double centerX = 0.18 + col * 0.32;
            double centerZ = 0.25 + row * 0.50;

            EnumApricorns type = resolveApricornType(stack);
            if (type == null) continue;
            int stageInt = MathHelper.clamp((int)Math.floor(progressFrac * 6f), 0, 5);

            if (stageInt == 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(centerX, baseY + eps, centerZ);
                float pm = 0.40f;
                GlStateManager.scale(-0.25f * pm, 0.28f * pm, -0.25f * pm);
                renderApricornSeedOBJ(type);
                GlStateManager.popMatrix();
            }

            if (stageInt > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(centerX, baseY + eps, centerZ);
                float m = 0.40f;
                GlStateManager.scale(-0.25f * m, 0.28f * m, -0.25f * m);
                renderApricornOBJ(stack, progressFrac);
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }

    private void renderBlockState(IBlockState state) {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        dispatcher.getBlockModelRenderer().renderModel(this.getWorld(), dispatcher.getModelForState(state), state, net.minecraft.util.math.BlockPos.ORIGIN, buffer, false);
        tessellator.draw();
    }

    private static final ResourceLocation TEX_BLACK  = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornblack.png");
    private static final ResourceLocation TEX_WHITE  = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornwhite.png");
    private static final ResourceLocation TEX_PINK   = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornpink.png");
    private static final ResourceLocation TEX_GREEN  = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricorngreen.png");
    private static final ResourceLocation TEX_BLUE   = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornblue.png");
    private static final ResourceLocation TEX_YELLOW = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornyellow.png");
    private static final ResourceLocation TEX_RED    = new ResourceLocation("pixelmon:textures/blocks/apricorn_trees/apricornred.png");

    private void renderApricornOBJ(ItemStack stack, float progressFrac) {
        EnumApricorns type = resolveApricornType(stack);
        if (type == null) return;
        int stage = MathHelper.clamp((int)Math.floor(progressFrac * 6f), 0, 5);
        if (stage <= 0) return;
        ResourceLocation tex = textureFor(type);
        if (tex == null) return;
        this.bindTexture(tex);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        try {
            RenderTileEntityApricornTrees.EnumApricornModel.values()[stage].getModel().render();
        } catch (Throwable ignored) { }
    }

    private void renderApricornSeedOBJ(EnumApricorns type) {
        ResourceLocation tex = textureFor(type);
        if (tex == null) return;
        this.bindTexture(tex);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        try {
            RenderTileEntityApricornTrees.EnumApricornModel.SEED.getModel().render();
        } catch (Throwable ignored) { }
    }

    private EnumApricorns resolveApricornType(ItemStack s) {
        if (s == null || s.isEmpty()) return null;
        Item it = s.getItem();
        if (it instanceof ItemApricorn) {
            return ((ItemApricorn) it).apricorn;
        }
        ResourceLocation rl = it.getRegistryName();
        if (rl != null) {
            String p = rl.getResourcePath();
            for (EnumApricorns e : EnumApricorns.values()) {
                if (p.contains(e.name().toLowerCase())) return e;
            }
        }
        return null;
    }

    private ResourceLocation textureFor(EnumApricorns t) {
        switch (t) {
            case Black: return TEX_BLACK;
            case White: return TEX_WHITE;
            case Pink:  return TEX_PINK;
            case Green: return TEX_GREEN;
            case Blue:  return TEX_BLUE;
            case Yellow:return TEX_YELLOW;
            case Red:   return TEX_RED;
            default:    return null;
        }
    }

    private IBlockState resolveApricornPlantState(ItemStack apricornItem, float progress) {
        ensureApricornBlock();
        if (APRICORN_BLOCK == null) return null;
        IBlockState state = APRICORN_BLOCK.getDefaultState();
        if (COLOR_PROP != null) {
            Object val = pickColorValue(COLOR_PROP, apricornItem);
            if (val != null) state = state.withProperty((IProperty) COLOR_PROP, (Comparable) val);
        } else {
            ResourceLocation rl = apricornItem.getItem().getRegistryName();
            if (rl != null) {
                String p = rl.getResourcePath();
                for (Object prop : state.getPropertyKeys()) {
                    IProperty<?> ip = (IProperty<?>) prop;
                    if (ip.getValueClass().isEnum()) {
                        Object guess = guessEnumByPath(ip, p);
                        if (guess != null) { state = state.withProperty((IProperty) ip, (Comparable) guess); break; }
                    }
                }
            }
        }
        if (AGE_PROP != null && AGE_MAX >= 0) {
            int stage = Math.max(0, Math.min(AGE_MAX, Math.round(progress * AGE_MAX)));
            state = state.withProperty(AGE_PROP, stage);
        }
        return state;
    }

    private void ensureApricornBlock() {
        if (APRICORN_BLOCK != null) return;
        for (Block b : ForgeRegistries.BLOCKS.getValuesCollection()) {
            ResourceLocation rl = b.getRegistryName();
            if (rl == null) continue;
            String dom = rl.getResourceDomain();
            String path = rl.getResourcePath();
            if (!"pixelmon".equals(dom)) continue;
            if (!(path.contains("apricon") || path.contains("apricorn"))) continue;
            IBlockState st = b.getDefaultState();
            IProperty<Integer> age = null;
            IProperty<?> color = null;
            int ageMax = -1;
            for (Object prop : st.getPropertyKeys()) {
                IProperty<?> ip = (IProperty<?>) prop;
                String name = ip.getName();
                if (age == null && ("age".equals(name) || "stage".equals(name))) {
                    try {
                        @SuppressWarnings("unchecked") IProperty<Integer> c = (IProperty<Integer>) ip;
                        age = c;
                        int m = -1; for (Integer v : c.getAllowedValues()) { if (v > m) m = v; }
                        ageMax = m;
                    } catch (Throwable ignored) {}
                }
                if (color == null && (name.contains("color") || name.contains("type") || name.contains("variant"))) {
                    color = ip;
                }
            }
            APRICORN_BLOCK = b;
            AGE_PROP = age;
            COLOR_PROP = color;
            AGE_MAX = ageMax;
            if (AGE_PROP != null) break;
        }
    }

    private Object pickColorValue(IProperty<?> prop, ItemStack apricornItem) {
        String hint = "";
        ResourceLocation rl = apricornItem.getItem().getRegistryName();
        if (rl != null) hint = rl.getResourcePath();
        Object m = guessEnumByPath(prop, hint);
        if (m != null) return m;
        for (Object v : prop.getAllowedValues()) return v;
        return null;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private String getPropValueName(IProperty<?> prop, Object value) {
        return ((IProperty) prop).getName((Comparable) value);
    }

    private Object guessEnumByPath(IProperty<?> prop, String path) {
        String[] colors = new String[]{"black","blue","green","pink","red","white","yellow"};
        String found = null;
        for (String c : colors) { if (path.contains(c)) { found = c; break; } }
        if (found == null) return null;
        for (Object v : prop.getAllowedValues()) {
            String n = getPropValueName(prop, v);
            if (n.equalsIgnoreCase(found)) return v;
        }
        return null;
    }
}
