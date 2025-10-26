package com.polkax.pixelgreenhouse.gui;

import com.polkax.pixelgreenhouse.container.ContainerGreenhouse;
import com.polkax.pixelgreenhouse.tileentity.TileEntityGreenhouse;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiGreenhouse extends GuiContainer {
    
    private static final ResourceLocation FURNACE_GUI = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
    private final InventoryPlayer player;
    private final TileEntityGreenhouse tileEntity;
    private final ContainerGreenhouse container;
    
    public GuiGreenhouse(InventoryPlayer player, TileEntityGreenhouse tileEntity) {
        super(new ContainerGreenhouse(player, tileEntity));
        this.container = (ContainerGreenhouse) this.inventorySlots;
        this.player = player;
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 166;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String tileName = this.tileEntity.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2), 6, 4210752);
        this.fontRenderer.drawString(this.player.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
        
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawBeveledPanel(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                drawSlotFrame(this.guiLeft + 8 + j * 18, this.guiTop + 17 + i * 18);
            }
        }
        drawSlotFrame(this.guiLeft + 80, this.guiTop + 53);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                drawSlotFrame(this.guiLeft + 116 + j * 18, this.guiTop + 17 + i * 18);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                drawSlotFrame(this.guiLeft + 8 + j * 18, this.guiTop + 84 + i * 18);
            }
        }
        for (int i = 0; i < 9; i++) {
            drawSlotFrame(this.guiLeft + 8 + i * 18, this.guiTop + 142);
        }

        this.mc.getTextureManager().bindTexture(FURNACE_GUI);
        
        // Рисуем огонь в слоте для топлива
        int burnTime = this.container.getBurnTime();
        int currentItemBurnTime = this.container.getCurrentItemBurnTime();
        if (currentItemBurnTime > 0) {
            int k = this.getBurnLeftScaled(14, burnTime, currentItemBurnTime);
            if (k > 0) {
                // Включаем прозрачность и настраиваем цвет
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                
                // Вычисляем центр слота для топлива
                int fuelSlotX = this.guiLeft + 80;  // X координата слота
                int fuelSlotY = this.guiTop + 53;   // Y координата слота
                
                // Центрируем огонь в слоте (слот 18x18, огонь 14x14)
                int fireX = fuelSlotX + 2;
                int fireY = fuelSlotY + 1;
                
                // Рисуем только огонь (начиная с пикселя 176,0 в текстуре печки)
                this.drawTexturedModalRect(fireX, fireY + (14 - k), 176, 14 - k, 14, k);
                
                GlStateManager.disableBlend();
            }
        }
        
        // Рисуем прогресс бар роста (стрелка как в печке)
        // Позиция стрелки между входными и выходными слотами
        // Левые слоты: 8 до 62 (8 + 3*18), правые слоты: 116 до 170
        // Центр между ними: (62 + 116) / 2 = 89, минус половина ширины стрелки (24/2 = 12)
        int arrowX = this.guiLeft + 77;  // По центру между левыми и правыми слотами
        // Верхний ряд слотов: y=17, нижний ряд: y=35
        // Центр между рядами: (17+18 + 35)/2 = 35, минус половина высоты стрелки (16/2 = 8)
        int arrowY = this.guiTop + 27;   // По центру между верхним и нижним рядами
        
        // Рисуем фон для стрелки (темная панель)
        drawRect(arrowX, arrowY, arrowX + 24, arrowY + 16, 0xFF3A3A3A);
        
        // Получаем прогресс из контейнера
        int growthProgress = this.container.getGrowthProgress();
        int growthTime = this.container.getGrowthTime();
        int arrowWidth = this.getGrowthProgressScaled(24, growthProgress, growthTime);
        
        // Рисуем заполненную часть стрелки (увеличивается по мере прогресса)
        // Координаты (176, 14) - оранжевая стрелка в текстуре печки
        if (arrowWidth > 0) {
            GlStateManager.enableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(arrowX, arrowY, 176, 14, arrowWidth + 1, 16);
            GlStateManager.disableBlend();
        }
    }
    
    private int getBurnLeftScaled(int pixels, int burnTime, int currentItemBurnTime) {
        if (currentItemBurnTime == 0) {
            currentItemBurnTime = 200;
        }
        return burnTime * pixels / currentItemBurnTime;
    }
    
    private int getGrowthProgressScaled(int pixels, int growthProgress, int growthTime) {
        if (growthTime == 0) {
            return 0;
        }
        return growthProgress * pixels / growthTime;
    }

    private void drawBeveledPanel(int x, int y, int w, int h) {
        int bg = 0xFF2B2B2B;
        int light = 0xFF5A5A5A;
        int dark = 0xFF0B0B0B;
        drawRect(x, y, x + w, y + h, bg);
        drawRect(x, y, x + w, y + 1, light);
        drawRect(x, y, x + 1, y + h, light);
        drawRect(x + w - 1, y, x + w, y + h, dark);
        drawRect(x, y + h - 1, x + w, y + h, dark);
        drawRect(x + 7, y + 14, x + w - 7, y + 15, light);
    }

    private void drawSlotFrame(int x, int y) {
        int outer = 0xFF1A1A1A;
        int inner = 0xFF3A3A3A;
        drawRect(x - 1, y - 1, x + 17 + 1, y + 17 + 1, outer);
        drawRect(x, y, x + 17, y + 17, inner);
    }
}
