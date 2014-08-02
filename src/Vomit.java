package src;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Vomit extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	
	// public variables
	public static final int VOMIT_NORMAL = 0;
	public static final int VOMIT_CRASHED = 1;
	public static final int VOMIT_SHADOW = 2;
	public static final int NUM_OF_VOMIT_STATE = 3;

	private static final int VOMITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	private Body.AgeState ageState;
	private int falldownDamage = 0;
	private int amount = 0;
	private int vomitType = 0;
	private static final float[] imageSize = {0.25f, 0.5f, 1.0f};
	private static final int value[] = {50,100,300};
	
	private static Image[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/";
		final YukkuriUtil.YukkuriType[] name = YukkuriUtil.YukkuriType.values();
		
		images = new Image[name.length][NUM_OF_VOMIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx, sy;

		for(int i = 0; i < name.length; i++) {
			if(name[i].imageDirName.length() == 0) continue;
			
			images[i][VOMIT_NORMAL][Body.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan.png"));
			images[i][VOMIT_CRASHED][Body.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan2.png"));
			images[i][VOMIT_SHADOW][Body.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan_shadow.png"));

			for(int j = 0; j < NUM_OF_VOMIT_STATE; j++) {
				imgW[i][Body.adultIndex] = images[i][0][Body.adultIndex].getWidth(io);
				imgH[i][Body.adultIndex] = images[i][0][Body.adultIndex].getHeight(io);
				pivX[i][Body.adultIndex] = imgW[i][Body.adultIndex] >> 1;
				pivY[i][Body.adultIndex] = imgH[i][Body.adultIndex] - 1;

				sx = (int)((float)imgW[i][Body.adultIndex] * imageSize[1]);
				sy = (int)((float)imgH[i][Body.adultIndex] * imageSize[1]);
				images[i][j][Body.childIndex] = images[i][j][Body.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
				sx = (int)((float)imgW[i][Body.adultIndex] * imageSize[0]);
				sy = (int)((float)imgH[i][Body.adultIndex] * imageSize[0]);
				images[i][j][Body.babyIndex] = images[i][j][Body.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
			}
		}
		for(int i = 0; i < name.length; i++) {
			for(int j = 0; j < 3; j++) {
				if(images[i][0][j] == null) continue;
				
				imgW[i][j] = images[i][0][j].getWidth(io);
				imgH[i][j] = images[i][0][j].getHeight(io);
				pivX[i][j] = imgW[i][j] >> 1;
				pivY[i][j] = imgH[i][j] - 1;
			}
		}
	}
	
	public Image getImage() {
		return (images[vomitType][getVomitState()][ageState.ordinal()]);
	}
	
	public Image getShadowImage() {
		return (images[vomitType][VOMIT_SHADOW][ageState.ordinal()]);
	}

	public int getSize() {
		return imgW[vomitType][ageState.ordinal()];
	}

	public Vomit (int initX, int initY, int initZ, Body.AgeState initAgeState, YukkuriUtil.YukkuriType type) {
		objType = Type.VOMIT;
		vomitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		ageState = initAgeState;
		switch (initAgeState) {
		case BABY:
			amount = 100;
			break;
		case CHILD:
			amount = 100*2;
			break;
		case ADULT:
			amount = 100*4;
			break;
		}
		removed = false;
		setBoundary(pivX[vomitType][ageState.ordinal()], pivY[vomitType][ageState.ordinal()],
					imgW[vomitType][ageState.ordinal()], imgH[vomitType][ageState.ordinal()]);
	}

	public Body.AgeState getAgeState() { return ageState; }

	public int getVomitState() {
		if (age >= VOMITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}

	public void eatVomit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			removed = true;
		}
	}

	public void crushVomit() {
		age += VOMITLIMIT[ageState.ordinal()]/2;
	}
	
	public void kick() {
		int blowLevel[] = {-6, -5, -4};
		kick(0, blowLevel[ageState.ordinal()]*2, blowLevel[ageState.ordinal()]);
	}

	public int getValue() {
		return value[ageState.ordinal()];
	}
	
	public Event clockTick()
	{
		if (removed == false) {
			//age += TICK;
			if (age >= VOMITLIMIT[ageState.ordinal()]) {
				removed = true;
			}
			if (!grabbed) {
				if (vx != 0) {
					x += vx;
					if (x < 0) {
						x = 0;
						vx *= -1;
					}
					else if (x > Terrarium.MAX_X) {
						x = Terrarium.MAX_X;
						vx *= -1;
					}
					else if (Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
						x -= vx;
						vx = 0;
					}
				}
				if (vy != 0) {
					y += vy;
					if (y < 0) {
						y = 0;
						vy = 0;
					}
					else if (y > Terrarium.MAX_Y) {
						y = Terrarium.MAX_Y;
						vy = 0;
					}
				}
				if (z != 0 || vz != 0) {
					vz += 1;
					z -= vz;
					falldownDamage += vz;
					if (z <= 0) {
						if (falldownDamage > 10) {
							crushVomit();
						}
						z = 0;
						vx = 0;
						vy = 0;
						vz = 0;
						falldownDamage = 0;
					}
				}
			}
			return Event.DONOTHING;
		}
		return Event.REMOVED;
	}
}
