package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.*;


public class AutoFeeder extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum FeedType {
        NORMAL("Normal"),
        BITTER("Bitter"),
        LEMON_POP("Lemon Pop"),
        HOT("Spicy"),
        VIYUGRA("Viyugra"),
        SWEETS1("Chocolate Cake"),
        SWEETS2("Vanilla Cake"),
        WASTE("Garbage"),
        BODY("Corpse"),
		;
        private String name;
        FeedType(String name) { this.name = name; }
        public String toString() { return name; }
	}

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = 0;
	private static final int images_num = 2;
	private static Image[] images = new Image[images_num];
	private static Rectangle boundary = new Rectangle();

	private Random rnd = new Random();
	private int type = 0;
	private Obj food = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "autofeeder/autofeed.png");
		images[1] = ModLoader.loadItemImage(loader, "autofeeder/autofeed_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public Image getImage() {
		if(enabled) return images[0];
		return images[1];
	}

	@Override
	public Image getImage(int idx) {
		return null;
	}

	@Override
	public int getImageLayerCount() {
		return 0;
	}

	@Override
	public Image getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public int getCost() {
		return cost;
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	@Override
	public void upDate() {
		
		if((age % 20) != 0) return;
		
		if(food != null) {
			if(type == FeedType.BODY.ordinal()) {
				Body b = (Body)food;
				if(b.isDead()) {
					b.remove();
				}
				if(b.isRemoved()) {
					food = null;
				}
			} else {
				Food f = (Food)food;
				if(f.isRemoved()) {
					food = null;
				} else if(f.isEmpty()) {
					f.remove();
				}
			}
		} else {
			if(type == FeedType.BODY.ordinal()) {
				food = SimYukkuri.mypane.terrarium.addBody(getX(), getY(), 0, rnd.nextInt(6), Body.AgeState.BABY, null, null);
				Cash.buyYukkuri((Body)food);
				Cash.addCash(-getCost());
			} else {
				Food.type f = Food.type.YUKKURIFOOD;
				switch(type) {
					case 0:
						f = Food.type.YUKKURIFOOD;
						break;
					case 1:
						f = Food.type.BITTER;
						break;
					case 2:
						f = Food.type.LEMONPOP;
						break;
					case 3:
						f = Food.type.HOT;
						break;
					case 4:
						f = Food.type.VIYUGRA;
						break;
					case 5:
						f = Food.type.SWEETS1;
						break;
					case 6:
						f = Food.type.SWEETS2;
						break;
					case 7:
						f = Food.type.WASTE;
						break;
				}
				food = SimYukkuri.mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, getX(), getY(), f.ordinal());
				Cash.buyItem(food);
				Cash.addCash(-getCost());
			}
		}
	}
	
	public AutoFeeder(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.AUTOFEEDER;

		boolean ret = setupFeeder(this, false);
		if(!ret) {
			objEXList.remove(this);
		}
		value = 5000;
		cost = 325;
	}

	// 險ｭ螳壹Γ繝九Η繝ｼ
	public static boolean setupFeeder(AutoFeeder o, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[FeedType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(5, 2));
		mainPanel.setPreferredSize(new Dimension(250, 150));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(FeedType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		
		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "閾ｪ蜍慕ｵｦ鬢瑚ｨｭ螳�", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < but.length; i++) {
				if(but[i].isSelected()) {
					o.type = i;
					break;
				}
			}
			ret = true;
		}
		return ret;
	}
}
