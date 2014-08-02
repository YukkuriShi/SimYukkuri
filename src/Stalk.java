package src;



import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;



public class Stalk extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	private static final int images_num = 1; //このクラスの総使用画像数
	private static Image[] images = new Image[images_num*2+1];
	private static Rectangle boundary = new Rectangle();

	private Body plantYukkuri = null;		// この茎が生えてる親
	private ArrayList<Body> bindBaby = new ArrayList<Body>();	// この茎にぶら下がってる子
	
	private int amount = 0;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/general/";
		for( int i = 0; images_num > i ; i++ ){
			images[i*2] = ImageIO.read(loader.getResourceAsStream(path+"stalk"+String.format("%03d",i+1)+".png"));
			images[i*2+1] = flipImage(ImageIO.read(loader.getResourceAsStream(path+"stalk"+String.format("%03d",i+1)+".png")));
		}
		images[images_num*2] = ImageIO.read(loader.getResourceAsStream(path + "stalk_shadow.png"));
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}
	
	@Override
	public Image getImage() {
		if ( option == 0 ){
			return images[1];
		}else{
			return images[0];
		}
	}

	@Override
	public Image getImage(int idx){
		if ( option == 0 ){
			return images[1];
		}else{
			return images[0];
		}
	}

	@Override
	public int getImageLayerCount() {
		return 1;
	}

	@Override
	public Image getShadowImage() {
		if(plantYukkuri == null) return images[2];
		return null;
	}

	protected static Image flipImage(Image img) {
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
	
	public void setDirection( int dir ) {
		if ( dir == 0 ){
			option = 0;
		}else{
			option = 1;
		}
	}
	
	@Override
	public void upDate () {
		int i = 0;
		int babyX = 0;
		int babyZ = 0;
		for (Body b : getBindBaby()){
			if ( b == null ) {
				i++;
				continue;
			}
			if (option == 0) {
				babyX = (( i % 5 ) * -5 + 14);
				b.setDirection( Body.Direction.RIGHT );
			}else{
				babyX = (( i % 5 ) * -5 + 14) * -1;
				b.setDirection( Body.Direction.LEFT );
			}
			babyZ = (( i % 5 ) * -2 + 14);
			b.setX( getX() + babyX );
			b.setY( getY() + 1 );
			b.setZ( getZ() + babyZ );
			b.kick(0,0,0);
			i++;
		}
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	public void setPlantYukkuri( Body b ) {
		plantYukkuri = b;
	}
	
	public Body getPlantYukkuri() {
		return plantYukkuri;
	}

	@Override
	public Obj getBindObj() {
		return getPlantYukkuri();
	}

	public void setBindBaby( Body b ) {
		if ( bindBaby.size() < 5 ) {
			bindBaby.add(b);
		}
	}
	
	public ArrayList<Body> getBindBaby() {
		return bindBaby;
	}
	
	public void disBindBabys() {
		if ( plantYukkuri != null ){
			plantYukkuri.getStalks().set(plantYukkuri.getStalks().indexOf( this ), null );
		}
		for ( Body b : bindBaby ){
			if ( b != null ){
				b.setBindStalk( null );
			}
		}
	}

	public void setX (int X)
	{
		if (X < 0 && plantYukkuri == null) {
			x = 0;
		}
		else if (X > Terrarium.MAX_X && plantYukkuri == null) {
			x = Terrarium.MAX_X;
		}
		else {
			x = X;
		}
	}

	public void setY (int Y) {
		if (Y < 0 && plantYukkuri == null) {
			y = 0;
		}
		else if(Y > Terrarium.MAX_Y && plantYukkuri == null) {
			y = Terrarium.MAX_Y;
		}
		else {
			y = Y;
		}
	}

	public void setZ(int Z)
	{
		if (Z < 0 && plantYukkuri == null) {
			z = 0;
		}
		else if (Z > Terrarium.MAX_Z && plantYukkuri == null) {
			z = Terrarium.MAX_Z;
		}
		else {
			z = Z;
		}
	}

	public boolean isPlantYukkuri(){
		for ( Body b : bindBaby ){
			if ( b != null ){
				return true;
			}
		}
		return (plantYukkuri!=null);
	}

	public int getAmount() {
		return amount;
	}

	public void eatStalk(int eatAmount)
	{
		amount -= eatAmount;
		if (amount <= 0) {
			amount = 0;
			for ( Body b : bindBaby ){
				if ( b != null ){
					b.setBindStalk( null );
				}
			}
			removed = true;
		}
	}

	@Override
	public void grab() {
		grabbed = true;
		if ( getPlantYukkuri() != null ){
			getPlantYukkuri().removeStalk(this);
		}
		setPlantYukkuri(null);
	}
	
	public Event clockTick()
	{
		age += TICK;
		if (removed) {
			removeListData();
			disBindBabys();
			return Event.REMOVED;
		}
		if (!grabbed && plantYukkuri == null) {
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
					vy *= -1;
				}
				else if (y > Terrarium.MAX_Y) {
					y = Terrarium.MAX_Y;
					vy *= -1;
				}
				else if (Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if (z <= 0) {
					z = 0;
					vx = 0;
					vy = 0;
					vz = 0;	
				}
			}
		}
		upDate();
		return Event.DONOTHING;
	}

	public Stalk(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.STALK;
		amount = 100*24*5;
	}
}

