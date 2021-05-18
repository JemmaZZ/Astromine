package com.github.mixinors.astromine.common.component.block.entity;

import com.github.mixinors.astromine.common.block.redstone.RedstoneType;

class SimpleRedstoneComponent implements RedstoneComponent{
	private RedstoneType type = RedstoneType.WORK_WHEN_OFF;
	
	public RedstoneType getType() {
		return type;
	}
	
	@Override
	public void setType(RedstoneType type) {
		this.type = type;
	}
}
