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
import src.object.Obj;
import src.object.ObjEX;
import src.system.ModLoader;
import src.yukkuri.*;
import src.yukkuri.Common.Marisa;
import src.yukkuri.Common.Reimu;
import src.yukkuri.Rare.Ayaya;
import src.yukkuriBody.Body;


public class BreedingPool extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum PoolType {
        LOW("Low"),
        HIGH("High"),
        RAPID("Rapid"),
        PRO("Professional-Grade"),
		;
        private String name;
        PoolType(String name) { this.name = name; }
        public String toString() { return name; }
	}

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int images_num = 4; //縺薙�繧ｯ繝ｩ繧ｹ縺ｮ邱丈ｽｿ逕ｨ逕ｻ蜒乗焚
	private static Image[] images = new Image[images_num];
	private static Rectangle boundary = new Rectangle();
	protected Random rnd = new Random();
	
	private boolean highDensity;
	private boolean rapidGrowth;
	private static int[] value = {5000,10000,20000,40000};
	private static int[] cost = {100,100,100,100};
	public int liquidYukkuriType = -1;
	

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num ; i++ ){
			images[i] = ModLoader.loadItemImage(loader, "breedingpool/breedingpool" + String.format("%03d",i+1) + ".png");		
		}
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	public Image getImage() {
		if(enabled) {
			if ( liquidYukkuriType == 2 ){
				return images[2];
			}else if (  liquidYukkuriType != -1  ) {
				return images[1];
			}
			return images[0];
		}
		return images[3];
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
			Body p = (Body)o;
			// 閭守函驕ｿ螯翫＆繧後※縺溘ｉ螯雁ｨ�＠縺ｪ縺�
			if(p.isBodyCastration()) return 0;
			if ( p.isDead() == false && (int)getAge() % ((highDensity==true)?4:10) == 0 ) {
				
				int babyType;

				if ( liquidYukkuriType == -1 ) {
					babyType = p.getType();
				}else if (!p.isHybrid() && liquidYukkuriType < 10000 && (rnd.nextInt(50) == 0)) {
					babyType = p.getHybridType( liquidYukkuriType );
				}
				else if (rnd.nextBoolean()) {
					babyType = liquidYukkuriType;
				}
				else {
					babyType = p.getType();
				}

				// 繝峨せ縺ｾ繧翫＆縺ｯ縺溘□縺ｮ縺ｾ繧翫＆縺ｫ螟画鋤
				if(babyType == DosMarisa.type) {
					babyType = Marisa.type;
				}

				if ((babyType == Reimu.type) && rnd.nextBoolean()) {
					babyType = WasaReimu.type;
				}else if ((babyType == WasaReimu.type) && rnd.nextBoolean()) {
					babyType = Reimu.type;
				}else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type ) && rnd.nextBoolean()){
					babyType = MarisaTsumuri.type;
				}else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type  ) && rnd.nextBoolean()){
					babyType = MarisaKotatsumuri.type;
				}else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type ) && rnd.nextBoolean()){
					babyType = Marisa.type;
				}else if ((babyType == Kimeemaru.type ) && rnd.nextBoolean()){
					babyType = Ayaya.type;
				}else if ((babyType == Ayaya.type ) && rnd.nextBoolean()){
					babyType = Kimeemaru.type;
				}

				if ( p.isSick() || p.isDamaged() || p.isDamaged()) {
					babyType = Tarinai.type;
				}
				p.getBabyTypes().add(new Dna(babyType, null, null, false));
				p.setHasBaby(true);
				if (rapidGrowth == true){
					p.rapidPregnantPeriod();
				}	
				Cash.addCash(-getCost());
			}else if ( p.isDead() == true &&  liquidYukkuriType == -1 ){
				liquidYukkuriType = p.getType();
				p.remove();
			}
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
	public void removeListData() {
		objEXList.remove(this);
	}
	
	public BreedingPool(int initX, int initY,  int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.BREEDINGPOOL;
		
		interval = 1;

		boolean ret = setupPool(this, false);
		if(!ret) {
			objEXList.remove(this);
		}
	}

	// 險ｭ螳壹Γ繝九Η繝ｼ
	public static boolean setupPool(BreedingPool o, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[PoolType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(4, 1));
		mainPanel.setPreferredSize(new Dimension(150, 150));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(PoolType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}
		
		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "鬢頑ｮ悶�繝ｼ繝ｫ險ｭ螳�", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			if(but[0].isSelected()) {
				o.highDensity = false;
				o.rapidGrowth = false;
			} else if(but[1].isSelected()) {
				o.highDensity = true;
				o.rapidGrowth = false;
			} else if(but[2].isSelected()) {
				o.highDensity = false;
				o.rapidGrowth = true;
			} else if(but[3].isSelected()) {
				o.highDensity = true;
				o.rapidGrowth = true;
			}
			ret = true;
		}
		return ret;
	}
}
