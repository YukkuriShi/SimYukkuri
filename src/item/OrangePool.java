package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.*;


public class OrangePool extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum OrangeType {
        NORMAL("Normal"),
        RESCUE("Highly Concentrated"),
		;
        private String name;
        OrangeType(String name) { this.name = name; }
        public String toString() { return name; }
	}

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int images_num = 2; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static Image[] images = new Image[images_num];
	private static Rectangle boundary = new Rectangle();
	private boolean rescue;
	private static int[] value = {2000,10000};
	private static int[] cost = {50,100};

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "orangepool/orangepool.png");
		images[1] = ModLoader.loadItemImage(loader, "orangepool/orangepool_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

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
	public int objHitProcess( Obj o ) {
		if ( o.getObjType() == Obj.Type.YUKKURI ){
			Body b = (Body)o;
			b.giveJuice();
			if ( b.isDirty() == true ) {
				b.setDirtyFlag(false);
			}
			if ( rescue ) {
				if ( b.isDead() && !b.isCrashed() && !b.isBurned() ){
					b.revival();
				}
				if(b.getFootBakeLevel() != Body.FootBake.CRITICAL) {
					b.setFootBakePeriod(0);
				}
//				b.setCantDie();
			}
			b.setCantDie();
			Cash.addCash(-getCost());
		}
		return 0;
	}
	
	@Override
	public int getValue() {
		return value[option];
	}

	@Override
	public int getCost() {
		return cost[option];
	}
	
	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	public OrangePool(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.ORANGEPOOL;
		
		interval = 3;

		boolean ret = setupOrange(this, false);
		if(!ret) {
			objEXList.remove(this);
		}
	}

	// 險ｭ螳壹Γ繝九Η繝ｼ
	public static boolean setupOrange(OrangePool o, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[OrangeType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(OrangeType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		
		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "繧ｪ繝ｬ繝ｳ繧ｸ繝励�繝ｫ險ｭ螳�", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			if(but[0].isSelected()) o.rescue = false;
			else o.rescue = true;
			ret = true;
		}
		return ret;
	}
}
