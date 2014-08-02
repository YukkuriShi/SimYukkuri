package src.effect;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.Effect;
import src.ModLoader;


/****************************************
 *  ディフューザーの蒸気
 */
public class Steam extends Effect {

	private static final long serialVersionUID = 1L;

	private static Image[] images;
	private static int imgW;
	private static int imgH;
	private static int pivX;
	private static int pivY;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		images = new Image[7];

		// ディフューザーの蒸気
		images[0] = ModLoader.loadItemImage(loader, "effect/steam_green.png");
		images[1] = ModLoader.loadItemImage(loader, "effect/steam_white.png");
		images[2] = ModLoader.loadItemImage(loader, "effect/steam_orange.png");
		images[3] = ModLoader.loadItemImage(loader, "effect/steam_liteblue.png");
		images[4] = ModLoader.loadItemImage(loader, "effect/steam_yellow.png");
		images[5] = ModLoader.loadItemImage(loader, "effect/steam_black.png");
		images[6] = ModLoader.loadItemImage(loader, "effect/steam_red.png");

		imgW = images[0].getWidth(io);
		imgH = images[0].getHeight(io);
		pivX = imgW >> 1;
		pivY = imgH >> 1;
	}
	
	public Image getImage() {
		return images[animeFrame];
	}

	public Steam(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivX, pivY, imgW, imgH);
		interval = 0;
		frames = 1;
	}
}
