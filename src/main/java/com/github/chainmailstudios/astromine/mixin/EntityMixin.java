package com.github.chainmailstudios.astromine.mixin;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import com.github.chainmailstudios.astromine.access.EntityAccess;
import com.github.chainmailstudios.astromine.common.registry.DimensionLayerRegistry;
import com.github.chainmailstudios.astromine.common.registry.GravityRegistry;

import com.google.common.collect.Lists;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {
	int lastY = 0;

	@Shadow
	public World world;

	@Shadow public double lastRenderX;

	@Unique Entity lastVehicle = null;

	@Override
	public Entity astromine_getLastVehicle() {
		return lastVehicle;
	}

	@Override
	public void astromine_setLastVehicle(Entity lastVehicle) {
		this.lastVehicle = lastVehicle;
	}

	@ModifyVariable(at = @At("HEAD"), method = "handleFallDamage(FF)Z", index = 1)
	float getDamageMultiplier(float damageMultiplier) {
		World world = ((Entity) (Object) this).world;

		return (float) (damageMultiplier * GravityRegistry.INSTANCE.get(world.getDimensionRegistryKey()) * GravityRegistry.INSTANCE.get(world.getDimensionRegistryKey()));
	}

	@Inject(at = @At("HEAD"), method = "tickNetherPortal()V")
	void onTick(CallbackInfo callbackInformation) {
		Entity entity = (Entity) (Object) this;

		if ((int) entity.getPos().getY() != lastY && !entity.world.isClient && entity.getVehicle() == null) {
			lastY = (int) entity.getPos().getY();

			int bottomPortal = DimensionLayerRegistry.INSTANCE.getLevel(DimensionLayerRegistry.Type.BOTTOM, entity.world.getDimensionRegistryKey());
			int topPortal = DimensionLayerRegistry.INSTANCE.getLevel(DimensionLayerRegistry.Type.TOP, entity.world.getDimensionRegistryKey());

			if (lastY <= bottomPortal && bottomPortal != Integer.MIN_VALUE) {
				RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, DimensionLayerRegistry.INSTANCE.getDimension(DimensionLayerRegistry.Type.BOTTOM, entity.world.getDimensionRegistryKey()).getValue());

				ServerWorld serverWorld = entity.world.getServer().getWorld(worldKey);

				List<Entity> existingPassengers = Lists.newArrayList(entity.getPassengerList());

				List<DataTracker.Entry<?>> entries = Lists.newArrayList();
				for (DataTracker.Entry<?> entry : entity.getDataTracker().getAllEntries()) {
					entries.add(entry.copy());
				}

				Entity newEntity = FabricDimensions.teleport(entity, serverWorld, DimensionLayerRegistry.INSTANCE.getPlacer(DimensionLayerRegistry.Type.BOTTOM, entity.world.getDimensionRegistryKey()));

				for (DataTracker.Entry entry : entries) {
					newEntity.getDataTracker().set(entry.getData(), entry.get());
				}

				for (Entity existingEntity : existingPassengers) {
					((EntityAccess) existingEntity).astromine_setLastVehicle(newEntity);
				}
			} else if (lastY >= topPortal && topPortal != Integer.MIN_VALUE) {
				RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, DimensionLayerRegistry.INSTANCE.getDimension(DimensionLayerRegistry.Type.TOP, entity.world.getDimensionRegistryKey()).getValue());

				ServerWorld serverWorld = entity.world.getServer().getWorld(worldKey);

				List<Entity> existingPassengers = Lists.newArrayList(entity.getPassengerList());

				List<DataTracker.Entry<?>> entries = Lists.newArrayList();
				for (DataTracker.Entry<?> entry : entity.getDataTracker().getAllEntries()) {
					entries.add(entry.copy());
				}

				Entity newEntity = FabricDimensions.teleport(entity, serverWorld, DimensionLayerRegistry.INSTANCE.getPlacer(DimensionLayerRegistry.Type.TOP, entity.world.getDimensionRegistryKey()));

				for (DataTracker.Entry entry : entries) {
					newEntity.getDataTracker().set(entry.getData(), entry.get());
				}

				for (Entity existingEntity : existingPassengers) {
					((EntityAccess) existingEntity).astromine_setLastVehicle(newEntity);
				}
			}
		}

		if (entity.getVehicle() != null) lastVehicle = null;
		if (lastVehicle != null) {
			if (lastVehicle.getEntityWorld().getRegistryKey().equals(entity.getEntityWorld().getRegistryKey())) {
				entity.startRiding(lastVehicle);
				lastVehicle = null;
			}
		}
	}
}
