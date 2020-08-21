package com.github.chainmailstudios.astromine.transportations.common.registry.client;

import com.github.chainmailstudios.astromine.registry.AstromineBlocks;
import com.github.chainmailstudios.astromine.registry.client.AstromineRenderLayers;
import com.github.chainmailstudios.astromine.transportations.common.registry.AstromineTransportationsBlocks;
import net.minecraft.client.render.RenderLayer;

public class AstromineTransportationsRenderLayer extends AstromineRenderLayers {
	public static void initialize() {
		register(AstromineTransportationsBlocks.ALTERNATOR, RenderLayer.getCutout());
		register(AstromineTransportationsBlocks.SPLITTER, RenderLayer.getCutout());
		register(AstromineTransportationsBlocks.INCINERATOR, RenderLayer.getCutout());
	}
}
