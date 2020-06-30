/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.curios.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;

public class CuriosButton extends ImageButton {

  private final ContainerScreen<?> parentGui;
  private boolean isRecipeBookVisible = false;

  CuriosButton(ContainerScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn,
      int textureOffsetX, int textureOffsetY, int yDiffText, ResourceLocation resource) {

    super(xIn, yIn, widthIn, heightIn, textureOffsetX, textureOffsetY, yDiffText, resource,
        (button) -> {
          Minecraft mc = Minecraft.getInstance();

          if (parentGui instanceof CuriosScreen && mc.player != null) {
            InventoryScreen inventory = new InventoryScreen(mc.player);
            ItemStack stack = mc.player.inventory.getItemStack();
            mc.player.inventory.setItemStack(ItemStack.EMPTY);
            mc.displayGuiScreen(inventory);
            mc.player.inventory.setItemStack(stack);
            NetworkHandler.INSTANCE
                .send(PacketDistributor.SERVER.noArg(), new CPacketOpenVanilla());
          } else {

            if (parentGui instanceof InventoryScreen) {
              InventoryScreen inventory = (InventoryScreen) parentGui;
              RecipeBookGui recipeBookGui = inventory.getRecipeGui();

              if (recipeBookGui.isVisible()) {
                recipeBookGui.toggleVisibility();
              }
            }
            NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CPacketOpenCurios());
          }
        });
    this.parentGui = parentGui;
  }

  @Override
  public void func_230431_b_(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY,
      float partialTicks) {

    if (parentGui instanceof InventoryScreen) {
      boolean lastVisible = isRecipeBookVisible;
      isRecipeBookVisible = ((InventoryScreen) parentGui).getRecipeGui().isVisible();

      if (lastVisible != isRecipeBookVisible) {
        Tuple<Integer, Integer> offsets = CuriosScreen.getButtonOffset(false);
        this.setPosition(parentGui.getGuiLeft() + offsets.getA(),
            parentGui.field_230709_l_ / 2 + offsets.getB());
      }
    } else if (parentGui instanceof CreativeScreen) {
      CreativeScreen gui = (CreativeScreen) parentGui;
      boolean isInventoryTab = gui.getSelectedTabIndex() == ItemGroup.INVENTORY.getIndex();
      this.field_230693_o_ = isInventoryTab;
      this.field_230694_p_ = isInventoryTab;
    }
    super.func_230431_b_(matrixStack, mouseX, mouseY, partialTicks);
  }
}
