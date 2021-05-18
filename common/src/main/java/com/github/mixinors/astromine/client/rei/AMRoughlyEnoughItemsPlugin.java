/*
 * MIT License
 *
 * Copyright (c) 2020, 2021 Mixinors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mixinors.astromine.client.rei;

import com.github.mixinors.astromine.AMCommon;
import com.github.mixinors.astromine.client.rei.alloysmelting.AlloySmeltingCategory;
import com.github.mixinors.astromine.client.rei.alloysmelting.AlloySmeltingDisplay;
import com.github.mixinors.astromine.client.rei.electricsmelting.ElectricSmeltingCategory;
import com.github.mixinors.astromine.client.rei.electricsmelting.ElectricSmeltingDisplay;
import com.github.mixinors.astromine.client.rei.electrolyzing.ElectrolyzingCategory;
import com.github.mixinors.astromine.client.rei.electrolyzing.ElectrolyzingDisplay;
import com.github.mixinors.astromine.client.rei.fluidmixing.FluidMixingCategory;
import com.github.mixinors.astromine.client.rei.fluidmixing.FluidMixingDisplay;
import com.github.mixinors.astromine.client.rei.infusing.InfusingCategory;
import com.github.mixinors.astromine.client.rei.infusing.InfusingDisplay;
import com.github.mixinors.astromine.client.rei.fluidgenerating.FluidGeneratingCategory;
import com.github.mixinors.astromine.client.rei.fluidgenerating.FluidGeneratingDisplay;
import com.github.mixinors.astromine.client.rei.melting.MeltingCategory;
import com.github.mixinors.astromine.client.rei.melting.MeltingDisplay;
import com.github.mixinors.astromine.client.rei.pressing.PressingCategory;
import com.github.mixinors.astromine.client.rei.pressing.PressingDisplay;
import com.github.mixinors.astromine.client.rei.refining.RefiningCategory;
import com.github.mixinors.astromine.client.rei.refining.RefiningDisplay;
import com.github.mixinors.astromine.client.rei.solidgenerating.SolidGeneratingCategory;
import com.github.mixinors.astromine.client.rei.solidgenerating.SolidGeneratingDisplay;
import com.github.mixinors.astromine.client.rei.solidifying.SolidifyingCategory;
import com.github.mixinors.astromine.client.rei.solidifying.SolidifyingDisplay;
import com.github.mixinors.astromine.client.rei.triturating.TrituratingCategory;
import com.github.mixinors.astromine.client.rei.triturating.TrituratingDisplay;
import com.github.mixinors.astromine.client.rei.wiremilling.WireMillingCategory;
import com.github.mixinors.astromine.client.rei.wiremilling.WireMillingDisplay;
import com.github.mixinors.astromine.common.recipe.*;
import com.github.mixinors.astromine.common.util.ClientUtils;
import com.github.mixinors.astromine.registry.common.AMBlocks;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.fractions.Fraction;
import me.shedaniel.rei.plugin.crafting.DefaultCustomDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import com.github.mixinors.astromine.client.render.sprite.SpriteRenderer;
import com.github.mixinors.astromine.common.util.FluidUtils;
import com.github.mixinors.astromine.common.util.NumberUtils;
import com.github.mixinors.astromine.common.volume.fluid.FluidVolume;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.api.widgets.Tooltip;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.RenderingEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AMRoughlyEnoughItemsPlugin implements REIPluginV0 {
	private static final Identifier ENERGY_BACKGROUND = AMCommon.id("textures/widget/energy_volume_fractional_vertical_bar_background_thin.png");
	private static final Identifier ENERGY_FOREGROUND = AMCommon.id("textures/widget/energy_volume_fractional_vertical_bar_foreground_thin.png");
	
	public static final Identifier INFUSING = AMCommon.id("infusing");
	public static final Identifier TRITURATING = AMCommon.id("triturating");
	public static final Identifier ELECTRIC_SMELTING = AMCommon.id("electric_smelting");
	public static final Identifier LIQUID_GENERATING = AMCommon.id("fluid_generating");
	public static final Identifier SOLID_GENERATING = AMCommon.id("solid_generating");
	public static final Identifier FLUID_MIXING = AMCommon.id("fluid_mixing");
	public static final Identifier ELECTROLYZING = AMCommon.id("electrolyzing");
	public static final Identifier REFINING = AMCommon.id("refining");
	public static final Identifier PRESSING = AMCommon.id("pressing");
	public static final Identifier MELTING = AMCommon.id("melting");
	public static final Identifier WIREMILLING = AMCommon.id("wire_milling");
	public static final Identifier ALLOY_SMELTING = AMCommon.id("alloy_smelting");
	public static final Identifier SOLIDIFYING = AMCommon.id("solidifying");
	
	
	@Override
	public Identifier getPluginIdentifier() {
		return AMCommon.id(AMCommon.MOD_ID);
	}
	
	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {
		recipeHelper.registerCategories(new InfusingCategory());
		recipeHelper.registerCategories(new SolidifyingCategory(), new TrituratingCategory(), new ElectricSmeltingCategory(), new FluidGeneratingCategory(), new SolidGeneratingCategory(), new PressingCategory(), new MeltingCategory(), new WireMillingCategory(), new AlloySmeltingCategory(), new FluidMixingCategory(FLUID_MIXING, "category.astromine.fluid_mixing", EntryStack.create(AMBlocks.ADVANCED_FLUID_MIXER.get())), new ElectrolyzingCategory(ELECTROLYZING, "category.astromine.electrolyzing", EntryStack.create(AMBlocks.ADVANCED_ELECTROLYZER.get())), new RefiningCategory(REFINING, "category.astromine.refining", EntryStack.create(AMBlocks.ADVANCED_REFINERY.get())));
	}
	
	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {
		recipeHelper.registerRecipes(INFUSING, AltarRecipe.class, InfusingDisplay::new);
		recipeHelper.registerRecipes(TRITURATING, TrituratingRecipe.class, TrituratingDisplay::new);
		recipeHelper.registerRecipes(ELECTRIC_SMELTING, SmeltingRecipe.class, ElectricSmeltingDisplay::new);
		recipeHelper.registerRecipes(LIQUID_GENERATING, FluidGeneratingRecipe.class, FluidGeneratingDisplay::new);
		recipeHelper.registerRecipes(FLUID_MIXING, FluidMixingRecipe.class, FluidMixingDisplay::new);
		recipeHelper.registerRecipes(ELECTROLYZING, ElectrolyzingRecipe.class, ElectrolyzingDisplay::new);
		recipeHelper.registerRecipes(REFINING, RefiningRecipe.class, RefiningDisplay::new);
		recipeHelper.registerRecipes(PRESSING, PressingRecipe.class, PressingDisplay::new);
		recipeHelper.registerRecipes(MELTING, MeltingRecipe.class, MeltingDisplay::new);
		recipeHelper.registerRecipes(WIREMILLING, WireMillingRecipe.class, WireMillingDisplay::new);
		recipeHelper.registerRecipes(ALLOY_SMELTING, AlloySmeltingRecipe.class, AlloySmeltingDisplay::new);
		recipeHelper.registerRecipes(SOLIDIFYING, SolidifyingRecipe.class, SolidifyingDisplay::new);
		
		recipeHelper.registerRecipes(BuiltinPlugin.CRAFTING, WireCuttingRecipe.class, recipe -> {
			return new DefaultCustomDisplay(recipe, EntryStack.ofIngredients(ImmutableList.of(recipe.getInput(), recipe.getTool())), Collections.singletonList(EntryStack.create(recipe.getOutput())));
		});
		
		for (Map.Entry<Item, Integer> entry : AbstractFurnaceBlockEntity.createFuelTimeMap().entrySet()) {
			if (!(entry.getKey() instanceof BucketItem) && entry != null && entry.getValue() > 0) {
				recipeHelper.registerDisplay(new SolidGeneratingDisplay((entry.getValue()/ 2.0F * 5) / (entry.getValue()/ 2.0F) * 6, Collections.singletonList(EntryStack.create(entry.getKey())), null, (entry.getValue() / 2) / 6.0));
			}
		}
	}
	
	@Override
	public void registerOthers(RecipeHelper recipeHelper) {
		recipeHelper.registerWorkingStations(INFUSING, EntryStack.create(AMBlocks.ALTAR.get()));
		recipeHelper.registerWorkingStations(TRITURATING, EntryStack.create(AMBlocks.PRIMITIVE_TRITURATOR.get()), EntryStack.create(AMBlocks.BASIC_TRITURATOR.get()), EntryStack.create(AMBlocks.ADVANCED_TRITURATOR.get()), EntryStack.create(AMBlocks.ELITE_TRITURATOR.get()));
		recipeHelper.registerWorkingStations(ELECTRIC_SMELTING, EntryStack.create(AMBlocks.PRIMITIVE_ELECTRIC_FURNACE.get()), EntryStack.create(AMBlocks.BASIC_ELECTRIC_FURNACE.get()), EntryStack.create(AMBlocks.ADVANCED_ELECTRIC_FURNACE.get()), EntryStack.create(AMBlocks.ELITE_ELECTRIC_FURNACE.get()));
		recipeHelper.registerWorkingStations(LIQUID_GENERATING, EntryStack.create(AMBlocks.PRIMITIVE_LIQUID_GENERATOR.get()), EntryStack.create(AMBlocks.BASIC_LIQUID_GENERATOR.get()), EntryStack.create(AMBlocks.ADVANCED_LIQUID_GENERATOR.get()), EntryStack.create(AMBlocks.ELITE_LIQUID_GENERATOR.get()));
		recipeHelper.registerWorkingStations(SOLID_GENERATING, EntryStack.create(AMBlocks.PRIMITIVE_SOLID_GENERATOR.get()), EntryStack.create(AMBlocks.BASIC_SOLID_GENERATOR.get()), EntryStack.create(AMBlocks.ADVANCED_SOLID_GENERATOR.get()), EntryStack.create(AMBlocks.ELITE_SOLID_GENERATOR.get()));
		recipeHelper.registerWorkingStations(FLUID_MIXING, EntryStack.create(AMBlocks.PRIMITIVE_FLUID_MIXER.get()), EntryStack.create(AMBlocks.BASIC_FLUID_MIXER.get()), EntryStack.create(AMBlocks.ADVANCED_FLUID_MIXER.get()), EntryStack.create(AMBlocks.ELITE_FLUID_MIXER.get()));
		recipeHelper.registerWorkingStations(ELECTROLYZING, EntryStack.create(AMBlocks.PRIMITIVE_ELECTROLYZER.get()), EntryStack.create(AMBlocks.BASIC_ELECTROLYZER.get()), EntryStack.create(AMBlocks.ADVANCED_ELECTROLYZER.get()), EntryStack.create(AMBlocks.ELITE_ELECTROLYZER.get()));
		recipeHelper.registerWorkingStations(REFINING, EntryStack.create(AMBlocks.PRIMITIVE_REFINERY.get()), EntryStack.create(AMBlocks.BASIC_REFINERY.get()), EntryStack.create(AMBlocks.ADVANCED_REFINERY.get()), EntryStack.create(AMBlocks.ELITE_REFINERY.get()));
		recipeHelper.registerWorkingStations(PRESSING, EntryStack.create(AMBlocks.PRIMITIVE_PRESSER.get()), EntryStack.create(AMBlocks.BASIC_PRESSER.get()), EntryStack.create(AMBlocks.ADVANCED_PRESSER.get()), EntryStack.create(AMBlocks.ELITE_PRESSER.get()));
		recipeHelper.registerWorkingStations(MELTING, EntryStack.create(AMBlocks.PRIMITIVE_MELTER.get()), EntryStack.create(AMBlocks.BASIC_MELTER.get()), EntryStack.create(AMBlocks.ADVANCED_MELTER.get()), EntryStack.create(AMBlocks.ELITE_MELTER.get()));
		recipeHelper.registerWorkingStations(WIREMILLING, EntryStack.create(AMBlocks.PRIMITIVE_WIREMILL.get()), EntryStack.create(AMBlocks.BASIC_WIREMILL.get()), EntryStack.create(AMBlocks.ADVANCED_WIREMILL.get()), EntryStack.create(AMBlocks.ELITE_WIREMILL.get()));
		recipeHelper.registerWorkingStations(ALLOY_SMELTING, EntryStack.create(AMBlocks.PRIMITIVE_ALLOY_SMELTER.get()), EntryStack.create(AMBlocks.BASIC_ALLOY_SMELTER.get()), EntryStack.create(AMBlocks.ADVANCED_ALLOY_SMELTER.get()), EntryStack.create(AMBlocks.ELITE_ALLOY_SMELTER.get()));
		recipeHelper.registerWorkingStations(SOLIDIFYING, EntryStack.create(AMBlocks.PRIMITIVE_SOLIDIFIER.get()), EntryStack.create(AMBlocks.BASIC_SOLIDIFIER.get()), EntryStack.create(AMBlocks.ADVANCED_SOLIDIFIER.get()), EntryStack.create(AMBlocks.ELITE_SOLIDIFIER.get()));
		
		recipeHelper.registerAutoCraftButtonArea(LIQUID_GENERATING, bounds -> new Rectangle(0, 0, 0, 0));
		recipeHelper.registerAutoCraftButtonArea(SOLID_GENERATING, bounds -> new Rectangle(bounds.getCenterX() - 55 + 110 - 16, bounds.getMaxY() - 16, 10, 10));
		recipeHelper.registerAutoCraftButtonArea(FLUID_MIXING, bounds -> new Rectangle(0, 0, 0, 0));
		recipeHelper.registerAutoCraftButtonArea(ELECTROLYZING, bounds -> new Rectangle(0, 0, 0, 0));
		recipeHelper.registerAutoCraftButtonArea(REFINING, bounds -> new Rectangle(0, 0, 0, 0));
	}
	
	public static Fraction convertToFraction(long fraction) {
		return Fraction.ofWhole(fraction);
	}

	public static EntryStack convertToEntryStack(FluidVolume volume) {
		return EntryStack.create(volume.getFluid(), convertToFraction(volume.getAmount()));
	}

	public static List<Widget> createEnergyDisplay(Rectangle bounds, double energy, boolean generating, long speed) {
		return Collections.singletonList(new EnergyEntryWidget(bounds, speed, generating).entry(new RenderingEntry() {
			@Override
			public void render(MatrixStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {}

			@Override
			public @Nullable Tooltip getTooltip(Point mouse) {
				return Tooltip.create(mouse, new TranslatableText("text.astromine.energy"), new LiteralText(NumberUtils.shorten(energy, "") + "E").formatted(Formatting.GRAY), new LiteralText("Astromine").formatted(Formatting.BLUE, Formatting.ITALIC));
			}
		}).notFavoritesInteractable());
	}

	public static List<Widget> createFluidDisplay(Rectangle bounds, List<EntryStack> fluidStacks, boolean generating, long speed) {
		EntryWidget entry = new FluidEntryWidget(bounds, speed, generating).entries(fluidStacks);
		if (generating)
			entry.markOutput();
		else entry.markInput();
		return Collections.singletonList(entry);
	}

	private static class EnergyEntryWidget extends EntryWidget {
		private final long speed;
		private final boolean generating;

		protected EnergyEntryWidget(Rectangle rectangle, long speed, boolean generating) {
			super(rectangle.x, rectangle.y);
			this.getBounds().setBounds(rectangle);
			this.speed = speed;
			this.generating = generating;
		}

		@Override
		protected void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if (background) {
				Rectangle bounds = getBounds();
				ClientUtils.getInstance().getTextureManager().bindTexture(ENERGY_BACKGROUND);
				DrawableHelper.drawTexture(matrices, bounds.x, bounds.y, 0, 0, bounds.width, bounds.height, bounds.width, bounds.height);
				ClientUtils.getInstance().getTextureManager().bindTexture(ENERGY_FOREGROUND);
				int height;
				if (generating)
					height = bounds.height - MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				else height = MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				DrawableHelper.drawTexture(matrices, bounds.x, bounds.y + height, 0, height, bounds.width - 1, bounds.height - height - 1, bounds.width, bounds.height);
			}
		}

		@Override
		protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {}
	}

	private static class FluidEntryWidget extends EntryWidget {
		private final long speed;
		private final boolean generating;

		protected FluidEntryWidget(Rectangle rectangle, long speed, boolean generating) {
			super(rectangle.x, rectangle.y);
			this.getBounds().setBounds(rectangle);
			this.speed = speed;
			this.generating = generating;
		}

		@Override
		protected void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			if (background) {
				Rectangle bounds = getBounds();
				ClientUtils.getInstance().getTextureManager().bindTexture(ENERGY_BACKGROUND);
				DrawableHelper.drawTexture(matrices, bounds.x, bounds.y, 0, 0, bounds.width, bounds.height, bounds.width, bounds.height);
			}
		}

		@Override
		protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			EntryStack entry = getCurrentEntry();
			if (entry.getType() == EntryStack.Type.FLUID && entry.getFluid() != Fluids.EMPTY) {
				Rectangle bounds = getBounds();
				int height;
				if (!generating)
					height = bounds.height - MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				else height = MathHelper.ceil((System.currentTimeMillis() / (speed / bounds.height) % bounds.height) / 1f);
				VertexConsumerProvider.Immediate consumers = ClientUtils.getInstance().getBufferBuilders().getEntityVertexConsumers();
				SpriteRenderer.beginPass().setup(consumers, RenderLayer.getSolid()).sprite(FluidUtils.getSprite(entry.getFluid())).color(FluidUtils.getColor(ClientUtils.getPlayer(), entry.getFluid())).light(0x00f000f0).overlay(OverlayTexture.DEFAULT_UV).alpha(
					0xff).normal(matrices.peek().getNormal(), 0, 0, 0).position(matrices.peek().getModel(), bounds.x + 1, bounds.y + bounds.height - height + 1, bounds.x + bounds.width - 1, bounds.y + bounds.height - 1, getZOffset() + 1).next(
					PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
				consumers.draw();
			}
		}
	}
}
