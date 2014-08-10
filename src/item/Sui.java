package src.item;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.MessagePool;
import src.Terrarium;
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.FavItemType;
import src.yukkuriBody.ConstantValues.Intelligence;

public class Sui extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	
	private static int lurd = 0;
	private static int fb = 1;
	private static int ldru = 2;
	private static int rl = 3;
	private static int lr =4;
	private static int ruld = 5;
	private static int bf = 6;
	private static int rdlu = 7;
	private static int direction_num = 8;

	private static int shadow = 0;
	private static int rest = 1;
	private static int yukkuri = 2;
	private static int out_of_control = 3;
	private static int condition_num = 4;
	private static Image[][] images = new Image[direction_num][condition_num];
	
	private static Rectangle boundary = new Rectangle();

	private static int bindbody_num = 3;
	private int current_bindbody_num = 0;
	private Body[] bindBody = new Body[bindbody_num];
	private Obj bindobj = null;

	private int current_direction = bf;
	private int current_condition = rest;
	
	private final static int[][] direction = {{lurd, fb, ldru},
											  {rl, -1, lr},
											  {ruld, bf, rdlu}};
	private final static int ofs = 15;
	private final static int ofsX = 5;
	private final static int ofsY = 1;
	private final static int[][] OfsX = {{-ofs-ofsX*2	,	0,	ofs+ofsX*2,	-ofs-ofsX*2,	ofs+ofsX*2,	-ofs-ofsX*2,	0,	ofs+ofsX*2},
										 {-ofsX*2	,	0,	ofsX*2,		-ofsX*2,		ofsX*2,		-ofsX*2,		0,	ofsX*2},
										 {0,			0,	0,		ofsX,		-ofsX,		0,		0,	0}};
	private final static int[][] OfsY = {{-ofsY,-ofsY*3,	-ofsY,		0,			0,			ofsY,		ofsY*2,	ofsY},
										 {0,	-ofsY*2,			0,		0,			0,				0,		ofsY,	0},
										 {ofsY,		-ofsY,		ofsY,		0,			0,			-ofsY,	    0,	-ofsY},};
	
	private int destX=-1;
	private int destY=-1;
	private int vecX,vecY;
	private int speed = 400;

	private int[] drawLayer = {1};
	
	private Random rnd = new Random();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		String[] tmp = {"_shadow","1","2","3"};
		for(int i=0;i<condition_num;i++){
			images[bf][i] = ModLoader.loadItemImage(loader, "sui/sui_gray/bf" + tmp[i] +".png");
			images[fb][i] = ModLoader.loadItemImage(loader, "sui/sui_gray/fb" + tmp[i] +".png");
			images[lr][i] = ModLoader.loadItemImage(loader, "sui/sui_gray/lr" + tmp[i] +".png");
			images[rl][i] = flipImage(images[lr][i]);
			images[ldru][i] = ModLoader.loadItemImage(loader, "sui/sui_gray/ldru" + tmp[i] +".png");
			images[lurd][i] = flipImage(images[ldru][i]);
			images[ruld][i] = ModLoader.loadItemImage(loader, "sui/sui_gray/ruld" + tmp[i] +".png");
			images[rdlu][i] = flipImage(images[ruld][i]);
		}
			boundary.width = images[0][0].getWidth(io);
			boundary.height = images[0][0].getHeight(io);
			boundary.x = boundary.width >> 1;
			boundary.y = boundary.height >> 1;
	}

	@Override
	public Image getImage() {
		return images[current_direction][current_condition];
	}

	@Override
	public int getImageLayerCount() {
		return 1;
	}

	@Override
	public Image getImage(int idx) {
		if(drawLayer == null) return null;
		return getImage();
	}
	
	@Override
	public Image getShadowImage() {
		return images[current_direction][shadow];
	}
	
	public boolean rideOn(Body b){
		if(current_bindbody_num>=bindbody_num){
			return false;
		}
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==b){
				return false;
			}
		}
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==null){
				bindBody[i]=b;
				b.setLinkParent(this);
				bindBody[i].setX(x+OfsX[i][current_direction]);
				bindBody[i].setY(y+OfsY[i][current_direction]+10);
				bindBody[i].setZ(1);
				if(bindobj==null){
					bindobj=b;
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetSui), true);
					b.setFavItem(FavItemType.SUI, this);
				}
				else{
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideSui), true);
				}
				break;
			}
		}
		current_bindbody_num++;
		b.setDropShadow(false);
		return true;
	}
	
	public boolean iscanriding(){
		if(bindobj == null) return false;
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==bindobj){
				return true;
			}
		}
		return false;
	}
	public int getcurrent_bindbody_num(){
		return current_bindbody_num;
	}
	
	public int getcurrent_condition(){
		return current_condition;
	}
	
	public void rideOff(Body b){
		if(current_bindbody_num<=0){
			return;
		}
		b.setLinkParent(null);
//		b.clearActions();
		for(int i=0;i<bindbody_num;i++){
			if(bindBody[i]==b){
				if(bindobj==b){
					bindobj=null;
				}
				bindBody[i]=null;
				break;
			}
		}
		current_bindbody_num--;
	}
	public boolean NoCanBind(){
		return (bindobj != null);
	}
	
	public Obj getbindobj(){
		return bindobj;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override	
	public boolean enableHitCheck() {
		return true;
	}

	@Override	
	public int objHitProcess( Obj o ) {
		Body b = (Body)o;
		if(current_condition==out_of_control){
			b.strikeByHammer();
			b.strikeByObject(0, 1000, vecX, vecY);
		}
		return 0;
	}

	@Override
	public void upDate() {
		if(bindobj != null && bindobj.isRemoved()){
			bindobj=null;
		}
		for(int i=0;i<bindbody_num;i++){
//			System.out.println(i + ":" + current_bindbody_num + ":" + bindBody[i]);
			if(bindBody[i] == null) continue;
			if(bindBody[i].isGrabbed() || bindBody[i].getFavItem(FavItemType.SUI) == null){
				bindBody[i].clearActions();
				rideOff(bindBody[i]);
				continue;
			}
			bindBody[i].setX(x+OfsX[i][current_direction]);
//			bindBody[i].setY(y+OfsY[i][current_direction]+10);
//			bindBody[i].setZ(1);
			if(y < Terrarium.MAX_Y) bindBody[i].setY(y + 1);
			else bindBody[i].setY(y);
			bindBody[i].setZ(OfsY[i][current_direction]);
			bindBody[i].lookTo(destX, destY);
		}
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	@Override
	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if (removed) {
			for(Body r:bindBody){
				r.clearActions();
				rideOff(r);
			}
			removeListData();
			return Event.REMOVED;
		}
		
		upDate();
		
		if (grabbed) {
			return Event.DONOTHING;
		}
		
		if(getZ() > 0) {
			z-=5;
			if(z < 0) z = 0;
			return Event.DONOTHING;
		}
		
		if(getAge()>10){
			setAge(0);
			if(destX == -1 && destY == -1) {
				speed=400;	
				if(iscanriding()){
					
					Body b = (Body)bindobj;
/*					Obj o = b.getTarget();
					if(o != null){
						int d = distance(b.getX(), b.getY(),o.getX(), o.getY());
						if(d<(boundary.height/2)*(boundary.height/2)){
							for(Body r:bindBody){
								rideOff(r);
							}
						}
					}*/
					int bx = b.getDestX();
					int by = b.getDestY();
					if(bx == -1){
						bx=x;
					}
					if(by==-1){
						by=y;
					}
					if(b.isIdiot()){
						bx = rnd.nextInt(Terrarium.MAX_X);
						by = rnd.nextInt(Terrarium.MAX_Y-boundary.height/2);
					}
					if(rnd.nextInt(100)==0 && b.getIntelligence() == Intelligence.FOOL){
						speed=1000;
					}
					moveTo(bx, by);
				}
							
			}
		}
		else{
			moveBody();
		}
		
		return Event.DONOTHING;
	}
	
	private void moveTo(int toX, int toY)
	{
		destX = Math.max(0, Math.min(toX, Terrarium.MAX_X));
		destY = Math.max(0, Math.min(toY, (Terrarium.MAX_Y - 1)));
	}

	private int decideDirection(int curPos, int destPos, int range) {
		if (destPos - curPos > range) {
			return 1;
		}
		else if (curPos - destPos > range) {
			return -1;
		}
		return 0;
	}
	
	private void moveBody() {

		int step = 1;
		int dirX = 0;
		int dirY = 0;
		// calculate x direction
		if (destX >= 0) {
			dirX = decideDirection(x, destX, step);
			if (dirX == 0) {
				destX = -1;
			}
		}
		// calculate y direction
		if (destY >= 0) {
			dirY = decideDirection(y, destY, step);
			if (dirY == 0) {
				destY = -1;
			}
		}
		// move to the direction
		vecX = dirX * step * speed / 100;
		vecY = dirY * step * speed / 100;
		x += vecX;
		y += vecY;
		if (Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
/*			if(Math.abs(x - destX) > 5 && Math.abs(y - destY) > 5) {
				action = null;
				target = null;
			}*/
			return;
		}
		
		if(vecX < 0 && x < destX) x = destX;
		if(vecX > 0 && x > destX) x = destX;
		if(vecY < 0 && y < destY) y = destY;
		if(vecY > 0 && y > destY) y = destY;
		
		if (x < 0) {
			x = 0;
			dirX = 1;
		}
		else if (x > Terrarium.MAX_X) {
			x = Terrarium.MAX_X;
			dirX = -1;
		}
		if (y < 0) {
			y = 0;
			dirY = 1;
		}
		else if (y > Terrarium.MAX_Y-boundary.height/2) {
			y = Terrarium.MAX_Y-boundary.height/2;
			dirY = -1;
		}
		// update direction of the face
		int directiontmp=direction[dirY+1][dirX+1];
		if(directiontmp !=-1){
			current_direction=directiontmp;
			if(speed>900){
				current_condition=out_of_control;
			}
			else{
				current_condition=yukkuri;
			}
		}
		else{
			current_condition=rest;
			
		}
	}

	public Sui(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.SUI;
		
		moveTo(x, y);
		
		interval = 10;
		value = 20000;
		cost = 0;
	}
	
	public void ChangeY(boolean A){
		if(A){
			setForceY(y+boundary.height/2);
			for(Body b: bindBody){
				if(b==null) continue;
				b.setForceY(b.getY()+boundary.height);
			}
		}
		else{
			setForceY(y-boundary.height/2);
			for(Body b: bindBody){
				if(b==null) continue;
				b.setForceY(b.getY()-boundary.height);
			}
		}
	}
	
	private static Image flipImage(Image img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		int pix[] = new int[w*h];
		int new_pix[] = new int[w*h];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pix, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) { }
		for (int i=0; i<h; i++){
			for (int j=0; j<w; j++){
				new_pix[(i+1)*w-(j+1)]=pix[i*w+j];
			}
		}
		MemoryImageSource mimg = new MemoryImageSource(w, h, new_pix, 0, w) ;
		return Toolkit.getDefaultToolkit().createImage(mimg);
	}
}
