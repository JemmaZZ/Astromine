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

package com.github.mixinors.astromine.common.util;

import com.github.mixinors.astromine.common.component.block.entity.TransferComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import com.github.mixinors.astromine.common.widget.blade.TransferTypeSelectorButtonWidget;
import com.github.vini2003.blade.common.collection.TabWidgetCollection;
import com.github.vini2003.blade.common.miscellaneous.Position;
import com.github.vini2003.blade.common.miscellaneous.Size;

import java.util.Map;

public class WidgetUtils {
	/** Populates a {@link TabWidgetCollection} widgets corresponding to
	 * the specified {@link TransferComponent} and {@link Identifier}.
	 */
	public static void createTransferTab(TabWidgetCollection tab, Position anchor, Direction rotation, TransferComponent component, BlockPos blockPos, Identifier id) {
		var north = Position.of(anchor, 7.0F + 22.0F, 31.0F + 22.0F);
		var south = Position.of(anchor, 7.0F + 0.0F, 31.0F + 44.0F);
		var up = Position.of(anchor, 7.0F + 22.0F, 31.0F + 0.0F);
		var down = Position.of(anchor, 7.0F + 22.0F, 31.0F + 44.0F);
		var west = Position.of(anchor, 7.0F + 44.0F, 31.0F + 22.0F);
		var east = Position.of(anchor, 7.0F + 0.0F, 31.0F + 22.0F);

		var positions = Map.of(
				Direction.NORTH, north,
				Direction.SOUTH, south,
				Direction.WEST, west,
				Direction.EAST, east,
				Direction.UP, up,
				Direction.DOWN, down
		);

		for (var direction : Direction.values()) {
			var button = new TransferTypeSelectorButtonWidget();
			
			button.setPosition(positions.get(MirrorUtils.rotate(direction, rotation)));
			button.setSize(Size.of(18.0F, 18.0F));
			button.setComponent(component);
			button.setId(id);
			button.setRotation(rotation);
			button.setDirection(direction);
			button.setBlockPos(blockPos);

			tab.addWidget(button);
		}
	}
}
