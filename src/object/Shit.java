package src.object;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.Terrarium;
import src.YukkuriUtil;
import src.YukkuriUtil.YukkuriType;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;

public class Shit extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	
	// public variables
	public static final int SHIT_NORMAL = 0;
	public static final int SHIT_CRASHED = 1;
	public static final int SHIT_SHADOW = 2;
	public static final int NUM_OF_SHIT_STATE = 3;

	private static final int SHITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	private Body owner;
	private ConstantValues.AgeState ageState;
	private int falldownDamage = 0;
	private int amount = 0;
	private int shitType = 0;
	
	private static final float[] shitSize = {0.4f, 0.7f, 1.0f};

	private static final int value[] = {50,100,300};
	
	private static Image[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/";
		final YukkuriType[] name = YukkuriType.values();

		images = new Image[name.length][NUM_OF_SHIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx, sy;
		
		for(int i = 0; i < name.length; i++) {
			if(name[i].imageDirName.length() == 0) continue;
			
			images[i][SHIT_NORMAL][ConstantValues.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun.png"));
			images[i][SHIT_CRASHED][ConstantValues.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun2.png"));
			images[i][SHIT_SHADOW][ConstantValues.adultIndex] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun-shadow.png"));

			for(int j = 0; j < NUM_OF_SHIT_STATE; j++) {
				imgW[i][ConstantValues.adultIndex] = images[i][0][ConstantValues.adultIndex].getWidth(io);
				imgH[i][ConstantValues.adultIndex] = images[i][0][ConstantValues.adultIndex].getHeight(io);
				pivX[i][ConstantValues.adultIndex] = imgW[i][ConstantValues.adultIndex] >> 1;
				pivY[i][ConstantValues.adultIndex] = imgH[i][ConstantValues.adultIndex] - 1;

				sx = (int)((float)imgW[i][ConstantValues.adultIndex] * shitSize[1]);
				sy = (int)((float)imgH[i][ConstantValues.adultIndex] * shitSize[1]);
				images[i][j][ConstantValues.childIndex] = images[i][j][ConstantValues.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
				sx = (int)((float)imgW[i][ConstantValues.adultIndex] * shitSize[0]);
				sy = (int)((float)imgH[i][ConstantValues.adultIndex] * shitSize[0]);
				images[i][j][ConstantValues.babyIndex] = images[i][j][ConstantValues.adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
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
		return (images[shitType][getShitState()][ageState.ordinal()]);
	}
	
	public Image getShadowImage() {
		return (images[shitType][SHIT_SHADOW][ageState.ordinal()]);
	}

	public int getSize() {
		return imgW[shitType][ageState.ordinal()];
	}

	public Shit (int initX, int initY, int initZ, Body b, YukkuriType type) {
		objType = Type.SHIT;
		shitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		owner = b;
		ageState = b.getAgeState();
		amount = imgW[shitType][ageState.ordinal()] * 12;
		removed = false;
		setBoundary(pivX[shitType][ageState.ordinal()], pivY[shitType][ageState.ordinal()],
					imgW[shitType][ageState.ordinal()], imgH[shitType][ageState.ordinal()]);
	}

	public ConstantValues.AgeState getAgeState() {
		return ageState;
	}

	public Body getOwner() {
		return owner;
	}

	public int getShitState() {
		if (getAge() >= SHITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}

	public void eatShit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			removed = true;
		}
	}

	public void crushShit() {
		setAge(getAge() + SHITLIMIT[ageState.ordinal()]/2);
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
			if (getAge() >= SHITLIMIT[ageState.ordinal()]) {
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
							crushShit();
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
