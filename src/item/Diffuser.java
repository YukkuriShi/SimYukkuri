package src.item;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.*;
import src.object.Effect;
import src.object.ObjEX;
import src.system.ModLoader;


public class Diffuser extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum SteamType {
        ANTI_FUNGAL("Anti-Fungal", 0),
        STEAM("Steam", 1),
        ORANGE("Orange Juice", 2),
        AGE_BOOST("Aging Serum", 3),
        AGE_STOP("Growth Inhibitor", 4),
        ANTI_DOS("Dosu Inhibitor", 5),
        ANTI_YU("Yukkuricide", 6),
		;
        private String name;
        private int steamColor;
        SteamType(String name, int col) { this.name = name; this.steamColor = col; }
        public String toString() { return name; }
        public int getColor() { return steamColor; }
	}

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = 0;
	private static Image images[] = new Image[3];
	private static Rectangle boundary = new Rectangle();
	private int layerCount;

	private boolean[] steamType = new boolean[SteamType.values().length];
	private int steamNum = 0;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "diffuser/diffuser.png");
		images[1] = ModLoader.loadItemImage(loader, "diffuser/diffuser_off.png");
		images[2] = ModLoader.loadItemImage(loader, "diffuser/shadow.png");		
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public Image getImage() {
		if(enabled) return images[0];
		return images[1];
	}

	@Override
	public int getImageLayerCount() {
		return layerCount;
	}

	@Override
	public Image getImage(int idx) {
		if(idx == 0) {
			return images[2];
		}
		if(enabled) return images[0];
		return images[1];
	}

	@Override
	public Image getShadowImage() {
		return images[2];
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if(!enabled) return;
		
		if ( getAge() % 40 == 0 ) {
			if(steamType[steamNum]) {
				Effect e = SimYukkuri.mypane.terrarium.addEffect(Effect.EffectType.STEAM, x, y, z + getH() >> 3,
						0, 0, -1, false, 30, 0, false, false, false);
					
				e.setAnimeFrame(SteamType.values()[steamNum].getColor());
			}
			do {
				steamNum++;
				if(steamNum == steamType.length) {
					steamNum = 0;
					break;
				}
			} while(!steamType[steamNum]);
		}
		if ( getAge() % 150 == 0 ) {
			Cash.addCash(-getCost()/4);
		}
	}

	@Override
	public void removeListData(){
		objEXList.remove(this);
	}
	
	public boolean[] getSteamType() {
		return steamType;
	}
	
	public Diffuser(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);
		layerCount = 2;
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.DIFFUSER;
		value = 10000;
		cost = 100;

		boolean ret = setupDiffuser(this, false);
		if(!ret) {
			objEXList.remove(this);
		}
	}

	// 險ｭ螳壹Γ繝九Η繝ｼ
	public static boolean setupDiffuser(Diffuser d, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JCheckBox[] checkBox = new JCheckBox[SteamType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(4, 2));
		mainPanel.setPreferredSize(new Dimension(220, 150));
		
		for(int i = 0; i < checkBox.length; i++) {
			JPanel panel = new JPanel();
			checkBox[i] = new JCheckBox(SteamType.values()[i].toString());
			
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(checkBox[i]);
			if(init) checkBox[i].setSelected(d.steamType[i]);
			
			mainPanel.add(panel);
		}

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "繝�ぅ繝輔Η繝ｼ繧ｶ繝ｼ險ｭ螳�", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < checkBox.length; i++) {
				d.steamType[i] = checkBox[i].isSelected();
			}
			ret = true;
		}
		return ret;
	}
}
