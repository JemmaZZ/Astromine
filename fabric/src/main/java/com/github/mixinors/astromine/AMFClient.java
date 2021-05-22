package com.github.mixinors.astromine;

import com.github.mixinors.astromine.client.registry.AMFPatchouliPages;
import net.fabricmc.api.ClientModInitializer;

public class AMFClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AMClient.init();
		
		AMFPatchouliPages.init();
	}
}
