package com.github.chainmailstudios.astromine.registry;

import com.github.chainmailstudios.astromine.common.entity.placer.SpaceEntityPlacer;
import com.github.chainmailstudios.astromine.common.registry.DimensionLayerRegistry;
import net.minecraft.world.dimension.DimensionType;

public class AstromineDimensionLayers {
	public static void initialize() {
		DimensionLayerRegistry.INSTANCE.register(DimensionLayerRegistry.Type.BOTTOM, AstromineDimensions.EARTH_SPACE_REGISTRY_KEY, AstromineConfig.get().overworldTravelYLevel, DimensionType.OVERWORLD_REGISTRY_KEY, SpaceEntityPlacer.TO_PLANET);
		DimensionLayerRegistry.INSTANCE.register(DimensionLayerRegistry.Type.TOP, DimensionType.OVERWORLD_REGISTRY_KEY, AstromineConfig.get().spaceTravelYLevel, AstromineDimensions.EARTH_SPACE_REGISTRY_KEY, SpaceEntityPlacer.TO_SPACE);
	}
}
