package src.item;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.yukkuriBody.ConstantValues.Attitude;
import src.*;
import src.yukkuriBody.Body;


public class Yunba extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static List<ObjEX> objEXList = new ArrayList<ObjEX>();

	public static enum Action {
		CLEAN("貂�祉", "Clean"),
		HEAL("蝗槫ｾｩ", "Heal"),
		KABI("縺九�蜃ｦ蛻�", "Kabi"),
		RUDE("繧ｲ繧ｹ遏ｯ豁｣", "Rude"),
		OKAZARI("縺翫°縺悶ｊ豐｡蜿�", "Okazari"),
		DESTROY("辟｡蟾ｮ蛻･謾ｻ謦�", "Destroy"),
		BODY_REMOVE("豁ｻ菴捺祉髯､", "Remove Corpses"),
		BODY_OKAZARI("豁ｻ菴薙♀縺九＊繧雁屓蜿�", "Body Okazari"),
		SHIT("縺�ｓ縺�ｓ謗�勁", "Poo~Poo"),
		STALK("闌取祉髯､","Stalk"),				//霑ｽ蜉�
		RUDELIMIT("繧ｲ繧ｹ繧帝勁縺�","Rude Limit"),
		SMARTLIMIT("蝟�憶繧帝勁縺�","Intelligence Limit"),
		AVERAGELIMIT("縺ｵ縺､縺�ｒ髯､縺�","Average Limit"),
		SUPERRUDELIMIT("繝峨ご繧ｹ繧帝勁縺�","Super-rude limit"),
		SUPERSMARTLIMIT("雜�埋濶ｯ繧帝勁縺�","Super-smart limit"),
		WALLTHROUGH("螢√せ繝ｫ繝ｼ","Bypass Walls"),
		NORND("逵√お繝阪Δ繝ｼ繝�","Nornd?"),
		KILL("謾ｻ謦�鴨繧｢繝��","Kill"),
		EMPFOOD("遨ｺ縺ｮ鬢檎堊蝗槫庶","Empty Food"),
		ANTIRAPER("繧後＞縺ｱ繝ｼ鬧�勁","Cull Rapists"),
		;
        private String name;
        Action(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
	}
	
	private static final String[] COL_LIST = {
		"Orange",
		"White",
		"Red",
		"Green",
		"Grey",
		"Blue",
		"Black",
	};
	
	public static final int hitCheckObjType = 0;
	private static Image bodyImages[][] = new Image[7][2];
	private static Image images[][] = new Image[4][2];
	private static Rectangle boundary = new Rectangle();

	private static JCheckBox[][] checkBox;
	private static boolean[][] defaultSetFlags = new boolean[Action.values().length][3];
	private static JComboBox colorBox;
	private static int defaultColor = 0;

	private int color;
	private int direction;
	private boolean[][] actionFlags;
	private boolean bodyCheck;
	private boolean shitCheck;
	private boolean stalkCheck;	// 霑ｽ蜉�
	private boolean norndCheck;
	private boolean killCheck;
	private boolean foodCheck;
	private int[] drawLayer;
	private int layerCount;
	private Action action = null;
	private Obj target = null;
	
	private int destX;
	private int destY;
	private int speed = 400;

	private Random rnd = new Random();

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		bodyImages[0][0] = ModLoader.loadItemImage(loader, "yunba/body_yellow_left.png");
		bodyImages[1][0] = ModLoader.loadItemImage(loader, "yunba/body_white_left.png");
		bodyImages[2][0] = ModLoader.loadItemImage(loader, "yunba/body_red_left.png");
		bodyImages[3][0] = ModLoader.loadItemImage(loader, "yunba/body_green_left.png");
		bodyImages[4][0] = ModLoader.loadItemImage(loader, "yunba/body_gray_left.png");
		bodyImages[5][0] = ModLoader.loadItemImage(loader, "yunba/body_blue_left.png");
		bodyImages[6][0] = ModLoader.loadItemImage(loader, "yunba/body_black_left.png");
		for(int i = 0; i < 7; i++) {
			bodyImages[i][1] = ModLoader.flipImage(bodyImages[i][0]);
		}
		
		images[0][0] = null;
		images[1][0] = ModLoader.loadItemImage(loader, "yunba/brush_left.png");
		images[2][0] = ModLoader.loadItemImage(loader, "yunba/spike_left.png");
		images[3][0] = ModLoader.loadItemImage(loader, "yunba/shadow.png");

		images[0][1] = null;
		for(int i = 1; i < 4; i++) {
			images[i][1] = ModLoader.flipImage(images[i][0]);
		}

		boundary.width = bodyImages[0][0].getWidth(io);
		boundary.height = bodyImages[0][0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;

		defaultSetFlags[Action.SHIT.ordinal()][0] = true;
		defaultSetFlags[Action.EMPFOOD.ordinal()][0] = true;
		defaultColor = 0;
	}

	@Override
	public Image getImage() {
		return images[0][direction];
	}

	@Override
	public int getImageLayerCount() {
		return layerCount;
	}

	@Override
	public Image getImage(int idx) {
		if(drawLayer == null) return null;
		if(drawLayer[idx] == 0) {
			return bodyImages[color][direction];
		}
		return images[drawLayer[idx]][direction];
	}

	@Override
	public Image getShadowImage() {
		return images[3][direction];
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public void upDate() {
		if ( age % 2400 == 0 ){
			Cash.addCash(-getCost());
		}
	}

	public int getValue() {
		return value;
	}

	public int getCost() {
		return cost;
	}
	
	@Override
	public void removeListData() {
		objEXList.remove(this);
	}

	@Override
	public Event clockTick()
	{
		age += TICK;
		if (removed) {
			action = null;
			target = null;
			removeListData();
			return Event.REMOVED;
		}
		upDate();
		if (grabbed) {
			action = null;
			target = null;
			return Event.DONOTHING;
		}
		
		if(getZ() > 0) {
			z-=5;
			if(z < 0) z = 0;
			action = null;
			target = null;
			return Event.DONOTHING;
		}
		
		if(action == null && (age > 10 || norndCheck)) { // 霑ｽ蜉�
			age = 0;

			if(shitCheck) {
				for(Shit o: Terrarium.shitList) {
					if(rnd.nextBoolean()) continue;
					// 霑ｽ蜉�
					if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Terrarium.MAP_ADULT)) {
						continue;
					}
					// 縺�ｓ縺�ｓ謗�勁豎ｺ螳�
					action = Action.SHIT;
					target = o;
					break;
				}
				for(Vomit o: Terrarium.vomitList) {
					if(rnd.nextBoolean()) continue;
					// 霑ｽ蜉�
					if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Terrarium.MAP_ADULT)) {
						continue;
					}
					// 縺�ｓ縺�ｓ謗�勁豎ｺ螳�
					action = Action.SHIT;
					target = o;
					break;
				}
			}
			if(stalkCheck && action == null){ // 霑ｽ蜉�
				for(ObjEX o: Stalk.objEXList) {
					Stalk s = (Stalk)o;
					if(norndCheck==false && rnd.nextBoolean()) continue;
					if(s.getPlantYukkuri() != null) continue;
					if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Terrarium.MAP_ADULT)) {
						continue;
					}
					// 闌取祉髯､豎ｺ螳�
					action = Action.STALK;
					target = o;
					break;
				}
			}
			if(bodyCheck && action == null) {
				
				for(Body b: Terrarium.bodyList) {
					if(norndCheck==false && rnd.nextBoolean()) continue;
					// 闌弱↓縺ｶ繧我ｸ九′縺｣縺ｦ繧句崋菴薙�繧ｹ繝ｫ繝ｼ
					if(b.isbindStalk() || b.getZ() > 0) continue;

					// 霑ｽ蜉�
					if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.acrossBarrier(getX(), getY(), b.getX(), b.getY(), Terrarium.MAP_ADULT)) {
						continue;
					}
					if(b.getAttitude() == Attitude.NICE && actionFlags[Action.SMARTLIMIT.ordinal()][0]){
						continue;
					}
					if(b.getAttitude() == Attitude.SHITHEAD && actionFlags[Action.RUDELIMIT.ordinal()][0]){
						continue;
					}
					if(b.getAttitude() == Attitude.AVERAGE && actionFlags[Action.AVERAGELIMIT.ordinal()][0]){
						continue;
					}
					if((b.getAttitude() == Attitude.SUPER_SHITHEAD && actionFlags[Action.SUPERRUDELIMIT.ordinal()][0])) {
						continue;
					}
					if((b.getAttitude() == Attitude.VERY_NICE && actionFlags[Action.SUPERSMARTLIMIT.ordinal()][0])) {
						continue;
					}
					if(b.isDead()) {
						if(actionFlags[Action.BODY_REMOVE.ordinal()][0]) {
							// 豁ｻ菴捺祉髯､
							action = Action.BODY_REMOVE;
							target = b;
							break;
						}
						else if(b.hasOkazari() && actionFlags[Action.BODY_OKAZARI.ordinal()][0]) {
							// 豁ｻ菴薙♀縺九＊繧�
							action = Action.BODY_OKAZARI;
							target = b;
							break;
						}
					} else {
						if(b.isDirty() && actionFlags[Action.CLEAN.ordinal()][b.getAgeState().ordinal()]) {
							// 貂�祉
							action = Action.CLEAN;
							target = b;
							break;
						}
						else if(b.isDamaged() && actionFlags[Action.HEAL.ordinal()][b.getAgeState().ordinal()]) {
							// 蝗槫ｾｩ
							action = Action.HEAL;
							target = b;
							break;
						}
						else if(b.isSick() && actionFlags[Action.KABI.ordinal()][b.getAgeState().ordinal()]) {
							// 縺九�蜃ｦ蛻�
							action = Action.KABI;
							target = b;
							break;
						}
						else if(b.isRude() && (b.getFurifuriDiscipline() != 0)
								&& actionFlags[Action.RUDE.ordinal()][b.getAgeState().ordinal()]) {
							// 繧ｲ繧ｹ遏ｯ豁｣
							action = Action.RUDE;
							target = b;
							break;
						}
						else if(b.hasOkazari() && actionFlags[Action.OKAZARI.ordinal()][b.getAgeState().ordinal()]) {
							// 縺翫°縺悶ｊ豐｡蜿�
							action = Action.OKAZARI;
							target = b;
							break;
						}
						else if(rnd.nextBoolean() && actionFlags[Action.DESTROY.ordinal()][b.getAgeState().ordinal()]) {
							// 謾ｻ謦�
							action = Action.DESTROY;
							target = b;
							break;
						}
						else if(b.isRaper() && b.isExciting() && actionFlags[Action.ANTIRAPER.ordinal()][0]) {
							// 繧後＞縺ｱ繝ｼ鬧�勁
							action = Action.DESTROY;
							target = b;
							break;
						}
					}
				}
			}
			// 遨ｺ縺ｮ鬢檎堊謗�勁
			if(foodCheck && action == null) {
				for(ObjEX o: Food.objEXList) {
					if(rnd.nextBoolean()) continue;
					Food f = (Food)o;
					if(!f.isEmpty()) continue;
					if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.acrossBarrier(getX(), getY(), o.getX(), o.getY(), Terrarium.MAP_ADULT)) {
						continue;
					}
					// 謗�勁豎ｺ螳�
					action = Action.EMPFOOD;
					target = o;
					break;
				}
			}

			if(action == null) {
				if(destX == -1 && destY == -1) {
					moveTo(rnd.nextInt(Terrarium.MAX_X), rnd.nextInt(Terrarium.MAX_Y));
					speed = 400;
				}
			} else {
				moveTo(target.getX(), target.getY());
				speed = 600;
			}
		}
		else
		{
			int vecX = destX - x;
			int vecY = destY - y;
			moveBody();
			// 逶ｮ逧�慍蛻ｰ逹�
			if(destX == -1 && destY == -1) {
				if(action == null || target == null) return Event.DONOTHING;
				if(target.isRemoved() || target.getZ() != 0) {
					action = null;
					target = null;
					return Event.DONOTHING;
				}
				if(40 < distance(x, y, target.getX(), target.getY())) {
					moveTo(target.getX(), target.getY());
					return Event.DONOTHING;
				}
				
				switch(action) {
					case CLEAN:
						((Body)target).setCleaning();
						break;
					case HEAL:
						((Body)target).giveJuice();
						break;
					case KABI:
						((Body)target).remove();
						break;
					case RUDE:
						((Body)target).strikeByPunish();
						break;
					case OKAZARI:
						((Body)target).takeOkazari();
						break;
					case DESTROY:
						if(killCheck){// 霑ｽ蜉�
							((Body)target).strikeByObject(100024*24, 500, 0, 0);
						}
						else{
							if(vecX > 5) vecX = 5;
							else if(vecX < -5) vecX = -5;

							if(vecY > 5) vecY = 5;
							else if(vecY < -5) vecY = -5;
							((Body)target).strikeByObject(1500, 500, vecX, vecY);
						}
						break;
					case BODY_REMOVE:
						target.remove();
						break;
					case BODY_OKAZARI:
						((Body)target).takeOkazari();
						break;
					case SHIT:
						if(target instanceof Shit) {
							((Shit)target).remove();
						} else if(target instanceof Vomit) {
							((Vomit)target).remove();
						}
						break;
					case STALK:
						((Stalk)target).remove();
						break;
					case EMPFOOD:
						((Food)target).remove();
						break;
					default:
						break;
				}
				action = null;
				target = null;
			}
		}
		return Event.DONOTHING;
	}

	private int distance(int x1, int y1, int x2, int y2) {
		return ((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1));
	}

	private void moveTo(int toX, int toY)
	{
		destX = Math.max(0, Math.min(toX, Terrarium.MAX_X));
		destY = Math.max(0, Math.min(toY, Terrarium.MAX_Y));
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
		int vecX = dirX * step * speed / 100;
		int vecY = dirY * step * speed / 100;
		x += vecX;
		y += vecY;
		if (actionFlags[Action.WALLTHROUGH.ordinal()][0] == false && Terrarium.onBarrier(x, y, getW() >> 2, getH() >> 2, Terrarium.MAP_ADULT)) {
			x -= vecX;
			y -= vecY;
			destX = -1;
			destY = -1;
			if(Math.abs(x - destX) > 5 && Math.abs(y - destY) > 5) {
				action = null;
				target = null;
			}
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
		else if (y > Terrarium.MAX_Y) {
			y = Terrarium.MAX_Y;
			dirY = -1;
		}
		// update direction of the face
		if (dirX == -1) {
			direction = 0;
		}
		else if (dirX == 1) {
			direction = 1;
		}
	}

	public Yunba(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		objEXList.add(this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.YUNBA;
		interval = 5;
		value = 35000;
		cost = 200;

		actionFlags = new boolean[Action.values().length][3];
	
		boolean ret = setupYunba(this, false);
		if(ret) {
			moveTo(rnd.nextInt(Terrarium.MAX_X), rnd.nextInt(Terrarium.MAX_Y));
		} else {
			objEXList.remove(this);
		}
	}
	
	// 險ｭ螳壹Γ繝九Η繝ｼ
	public static boolean setupYunba(Yunba y, boolean init) {
		
		JPanel mainPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		Action[] action = Action.values();
		checkBox = new JCheckBox[action.length][3];
		boolean ret = false;
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(350, 400));
		westPanel.setLayout(new GridLayout(7, 1));
		westPanel.setPreferredSize(new Dimension(120, 400));
		centerPanel.setLayout(new GridLayout(7, 3));
		southPanel.setLayout(new GridLayout(8, 2));	// 霑ｽ蜉�

		JLabel l1 = new JLabel("");
		westPanel.add(l1);
		JLabel l2 = new JLabel("Baby");
		centerPanel.add(l2);
		JLabel l3 = new JLabel("Ko");
		centerPanel.add(l3);
		JLabel l4 = new JLabel("Adult");
		centerPanel.add(l4);

		ButtonListener buttonListener = new ButtonListener();

		for(int i = 0; i < 6; i++) {
			JButton but = new JButton(action[i].toString());
			but.setActionCommand(action[i].name());
			but.addActionListener(buttonListener);
			westPanel.add(but);
			for(int j = 0; j < 3; j++) {
				checkBox[i][j] = new JCheckBox("");
				if(init) checkBox[i][j].setSelected(y.actionFlags[i][j]);
				else checkBox[i][j].setSelected(defaultSetFlags[i][j]);
				centerPanel.add(checkBox[i][j]);
			}
		}

		for(int i = 0; i < 14; i++) {// 霑ｽ蜉�
			checkBox[6 + i][0] = new JCheckBox(action[6 + i].toString());
			if(init) checkBox[6 + i][0].setSelected(y.actionFlags[6 + i][0]);
			else checkBox[6 + i][0].setSelected(defaultSetFlags[6 + i][0]);
			southPanel.add(checkBox[6 + i][0]);
		}
		colorBox = new JComboBox(COL_LIST);
		if(init) colorBox.setSelectedIndex(y.color);
		else colorBox.setSelectedIndex(defaultColor);
		southPanel.add(new JLabel(""));
		southPanel.add(colorBox);

		mainPanel.add(BorderLayout.LINE_START, westPanel);
		mainPanel.add(BorderLayout.CENTER, centerPanel);
		mainPanel.add(BorderLayout.SOUTH, southPanel);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "Yunba Setup", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			for(int i = 0; i < 6; i++) {
				for(int j = 0; j < 3; j++) {
					y.actionFlags[i][j] = checkBox[i][j].isSelected();
					defaultSetFlags[i][j] = checkBox[i][j].isSelected();
				}
			}
			
			for(int i = 0; i < 14; i++) {// 霑ｽ蜉�
				y.actionFlags[6 + i][0] = checkBox[6 + i][0].isSelected();
				defaultSetFlags[6 + i][0] = checkBox[6 + i][0].isSelected();
			}
			y.color = colorBox.getSelectedIndex();
			defaultColor = colorBox.getSelectedIndex();

			y.bodyCheck = false;
			y.shitCheck = false;
			y.stalkCheck = false; // 霑ｽ蜉�
			y.norndCheck = false;
			y.killCheck = false;
			y.foodCheck = false;
			for(int i = 0; i < y.actionFlags.length; i++) {
				for(int j = 0; j < 3; j++) {
					if(i == Action.SHIT.ordinal()) {
						if(y.actionFlags[i][0]) y.shitCheck = true;
					} else if(i == Action.STALK.ordinal()){
						if(y.actionFlags[i][0]) y.stalkCheck = true;
					} else if(i == Action.NORND.ordinal()){
						if(y.actionFlags[i][0]) y.norndCheck = true;
					} else if(i == Action.KILL.ordinal()){
						if(y.actionFlags[i][0]) y.killCheck = true;
					} else if(i == Action.EMPFOOD.ordinal()){
						if(y.actionFlags[i][0]) y.foodCheck = true;
					}
					else{
						if(y.actionFlags[i][j]) y.bodyCheck = true;
					}
				}
			}

			boolean brush = false;
			boolean spike = false;
			if (y.actionFlags[Action.CLEAN.ordinal()][0]
					|| y.actionFlags[Action.CLEAN.ordinal()][1]
					|| y.actionFlags[Action.CLEAN.ordinal()][2]) brush = true;

			if (y.actionFlags[Action.OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.OKAZARI.ordinal()][2]) brush = true;

			if (y.actionFlags[Action.BODY_REMOVE.ordinal()][0]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][1]
					|| y.actionFlags[Action.BODY_REMOVE.ordinal()][2]) brush = true;

			if (y.actionFlags[Action.BODY_OKAZARI.ordinal()][0]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][1]
					|| y.actionFlags[Action.BODY_OKAZARI.ordinal()][2]) brush = true;

			if (y.actionFlags[Action.SHIT.ordinal()][0]
					|| y.actionFlags[Action.SHIT.ordinal()][1]
					|| y.actionFlags[Action.SHIT.ordinal()][2]) brush = true;

			if (y.actionFlags[Action.KABI.ordinal()][0]
					|| y.actionFlags[Action.KABI.ordinal()][1]
					|| y.actionFlags[Action.KABI.ordinal()][2]) spike = true;

			if (y.actionFlags[Action.RUDE.ordinal()][0]
					|| y.actionFlags[Action.RUDE.ordinal()][1]
					|| y.actionFlags[Action.RUDE.ordinal()][2]) spike = true;

			if (y.actionFlags[Action.DESTROY.ordinal()][0]
					|| y.actionFlags[Action.DESTROY.ordinal()][1]
					|| y.actionFlags[Action.DESTROY.ordinal()][2]) spike = true;

			if (y.actionFlags[Action.ANTIRAPER.ordinal()][0]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][1]
					|| y.actionFlags[Action.ANTIRAPER.ordinal()][2]) spike = true;

			y.layerCount = 1;
			if(brush) y.layerCount++;
			if(spike) y.layerCount++;
			
			y.drawLayer = new int[y.layerCount];
			int i = 0;
			if(brush) {
				y.drawLayer[i] = 1;
				i++;
			}
			y.drawLayer[i] = 0;
			i++;
			if(spike) {
				y.drawLayer[i] = 2;
			}
			ret = true;
		}
		y.action = null;
		y.target = null;
		return ret;
	}

	public static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action sel = Action.valueOf(command);
			
			if(checkBox[sel.ordinal()][0].isSelected()) {
				checkBox[sel.ordinal()][0].setSelected(false);
				checkBox[sel.ordinal()][1].setSelected(false);
				checkBox[sel.ordinal()][2].setSelected(false);
			} else {
				checkBox[sel.ordinal()][0].setSelected(true);
				checkBox[sel.ordinal()][1].setSelected(true);
				checkBox[sel.ordinal()][2].setSelected(true);
			}
		}
	}
}


