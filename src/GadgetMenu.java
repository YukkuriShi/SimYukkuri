package src;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**********************************************
	蜷�ｨｮ繧ｳ繝槭Φ繝峨Γ繝九Η繝ｼ縺ｮ縺ｾ縺ｨ繧�


*/
public class GadgetMenu {
	
	// 繝｡繧､繝ｳ繧ｫ繝�ざ繝ｪ
	enum MainCategory {
		SELECT("SELECTERU", "Select"),
        TOOL("驕灘�", "Tools"),
        TOOL2("驕灘�2", "Abuse Tools"),
        BADGES("moonrunes", "Apply for Badges"),
        MISC("misc", "Miscellaneous"),
        FOODS("縺医＆", "Yukkuri Food"),
        CLEAN("貂�祉", "Clean Yukkurium"),
        ACCESSORY("縺翫°縺悶ｊ", "Accessory"),
        PANTS("縺翫￥繧九∩", "Pants"),
        FLOOR("蠎願ｨｭ鄂ｮ", "Surfaces"),
        BARRIER("繝輔ぅ繝ｼ繝ｫ繝", "Barriers"),
        TOYS("縺翫ｂ縺｡繧�", "Toys"),
        CONVEYOR("繝吶Ν繝医さ繝ｳ繝吶い", "Conveyor Belts"),
        VOICE("螢ｰ謗帙￠", "Speak to yukkuri"),	
        ;
        private String name;
        MainCategory(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }
	
	// 繝��繝ｫ繧ｫ繝�ざ繝ｪ
	enum Tool {
        PUNISH("縺翫＠縺翫″", "Punish Yukkuri"),
        SNAPPING("縺ｧ縺薙�繧�", "Flick Yukkuri"),
        PICKUP("謖√■荳翫￡繧�", "Pickup Yukkuri"),
        VIBRATOR("繝舌う繝�", "Shake Yukkuri"),
        JUICE("繧ｸ繝･繝ｼ繧ｹ", "Use Orange Juice"),
        LEMON_SPLAY("繝ｩ繝�ロ繧ｹ繝励Ξ繝ｼ", "Sedate with Lemon"),
        HAMMER("繝上Φ繝槭�", "Use Hammer"),
        GATHERINJECTINTO("邊ｾ蟄宣､｡謗｡蜿悶�豕ｨ蜈･","Draw/Inject Paste"),
        SELLYUKKURI("Sell","Sell Yukkuri"),
        ;
        private String name;
        Tool(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 繝��繝ｫ繧ｫ繝�ざ繝ｪ2
	enum Tool2 {
		BRAID_PLUCK("縺ｴ縺薙�縺薙■縺弱ｊ", "Snip Braid"),
        ANAL_CLOSE("縺ゅ↓繧�ｋ髢蛾事", "Seal"),
        STALK_CUT("闌主悉蜍｢", "Cut Stalk"),
        CASTRATION("閭守函蜴ｻ蜍｢", "Neuter"),
        STALK_UNPLUG("闌弱�縺｣縺薙〓縺�","Unplug Stalk"),
        LIGHTER("繝ｩ繧､繧ｿ繝ｼ", "Use Lighter"),
        BURNFEET("Burn feet", "Burn Feet"),
        WATER("豌ｴ", "Soak"),
        PIN("豌ｴ", "Pin"),
        ORANGE_AMP("繧ｪ繝ｬ繝ｳ繧ｸ繧｢繝ｳ繝励Ν", "Orange Amp"),
        ACCEL_AMP("謌宣聞菫��繧｢繝ｳ繝励Ν", "Accel Amp"),
        STOP_AMP("謌宣聞謚大宛繧｢繝ｳ繝励Ν", "Stop Amp"),
        ;
        private String name;
        Tool2(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }
	
	enum Badges {
		
        FAKE("Ν", "Fake Badge"),
        BRONZE(" Ν", "Bronze Badge"),
        SILVER(" Ν", "Silver Badge"),
        GOLD(" Ν", "Gold Badge"),
        ;
        private String name;
        Badges(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }
	
	enum Misc {
        BOX(" Ν", "Cardboard Box"),
        ;
        private String name;
        Misc(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 縺医＆繧ｫ繝�ざ繝ｪ
	enum Foods {
        NORMAL("縺ｵ縺､縺�", "Normal"),
        BITTER("闍ｦ縺�", "Bitter"),
        LEMON_POP("繝ｩ繝�ロ", "Lemon pop"),
        HOT("霎帙＞", "Spicy"),
        VIYUGRA("繝舌う繧�げ繝ｩ", "Viyugra"),
        SWEETS1("縺ゅ∪縺ゅ∪(譎ｮ騾�","Vanilla Cake"),
        SWEETS2("縺ゅ∪縺ゅ∪(鬮倡ｴ�","Chocolate Cake"),
        WASTE("逕溘ざ繝","Garbage"),
        AUTO("閾ｪ蜍慕ｵｦ鬢", "Auto-feeder"),
        ;
        private String name;
        Foods(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 貂�祉繧ｫ繝�ざ繝ｪ
	enum Clean {
        INDIVIDUAL("蛟句挨", "Individual"),
        YU_CLEAN("蜈ｨ繧�ｸ�祉", "Clean Yukkuri"),
        BODY("豁ｻ菴", "Bring out your dead"),
        SHIT("縺�ｓ縺�ｓ", "Clean poo-poo"),
        ETC("縺昴�莉", "Etc."),
        ALL("蜈ｨ驛ｨ", "Clean Everything"),
        ;
        private String name;
        Clean(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 蠎願ｨｭ鄂ｮ繧ｫ繝�ざ繝ｪ
	enum Floor {
        TOILET("繝医う繝ｬ(螳臥黄)", "Toilet"),
        TOILET2("繝医う繝ｬ(閾ｪ蜍墓ｸ�祉)", "Self-Cleaning Toilet"),
        BED("繝吶ャ繝", "Bed"),
        STICKY_PLATE("邊倡捩譚ｿ","Sticky Plate"),
        HOT_PLATE("繝帙ャ繝医�繝ｬ繝ｼ繝","Hot Plate"),
        FOOD_MAKER("繝輔�繝峨Γ繝ｼ繧ｫ繝ｼ", "Food Processor"),
        MIXER("繝溘く繧ｵ繝ｼ","Mixer"),
        DIFFUSER("繝�ぅ繝輔Η繝ｼ繧ｶ繝ｼ", "Diffuser"),
        ORANGE_POOL("繧ｪ繝ｬ繝ｳ繧ｸ繝励�繝ｫ", "Orange Pool"),
        BREED_POOL("鬢頑ｮ悶�繝ｼ繝ｫ", "Breeding Pool"),
        GARBAGE_CHUTE("繝�せ繝医す繝･繝ｼ繝", "Garbage Chute"),
        MACHINE_PRESS("繝励Ξ繧ｹ讖", "Machine Press"),
        PRODUCT_CHUTE("陬ｽ蜩∵兜蜈･蜿｣", "Product Chute"),
        ;
        private String name;
        Floor(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 繝輔ぅ繝ｼ繝ｫ繝峨き繝�ざ繝ｪ
	enum Barrier {
        GAP_MINI("谿ｵ蟾ｮ(蟆�", "Set (Small Gap)"),
        GAP_BIG("谿ｵ蟾ｮ(螟ｧ)", "Set (Big Gap)"),
        NET_MINI("驥醍ｶｲ(蟆�", "Set (Small Net)"),
        NET_BIG("驥醍ｶｲ(螟ｧ)", "Set (Big Net)"),
        WALL("螢", "Set (Wall)"),
//        WATER("豌ｴ", "Set (Water)"),
        DELETE("謦､蜴ｻ", "Remove"),
        ALL_DELETE("蜈ｨ謦､蜴ｻ", "Remove All"),
        ;
        private String name;
        Barrier(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 縺翫ｂ縺｡繧�き繝�ざ繝ｪ
	enum Toys {
        BALL("繝懊�繝ｫ", "Ball"),
        YUNBA("繧�Φ繝", "Yunba"),
        YUNBA_SETUP("繧�Φ繝占ｨｭ螳壼､画峩", "Yunba Setup"),
        SUI("縺吶＜繝ｼ","Sui"),
        TRASH("繧ｬ繝ｩ繧ｯ繧ｿ","Trash"),
        ;
        private String name;
        Toys(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 繧ｳ繝ｳ繝吶い繧ｫ繝�ざ繝ｪ
	enum Conveyor {
        FRONT("螂･", "Back"),
        BACK("謇句燕", "Front"),
        RIGHT("蜿ｳ", "Right"),
        LEFT("蟾ｦ", "Left"),
        ;
        private String name;
        Conveyor(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 螢ｰ謗帙￠
	enum Voice {
		YUKKURISITEITTENE("繧�▲縺上ｊ縺励※縺�▲縺ｦ縺ｭ�", "Yukukri, Shittete ne!"),
        YUKKURIDIE("繧�▲縺上ｊ縺励↑縺�〒縺励ｓ縺ｧ縺ｭ�", "Uneasy Yukkuris should die easy!"),
        ;
        private String name;
        Voice(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }

	// 騾溷ｺｦ
	enum GameSpeed {
        PAUSE("荳�凾蛛懈ｭ｢", "Pause"),
        X1("騾溷ｺｦ: x1", "Speed: x1"),
        X2("騾溷ｺｦ: x2", "Speed: x2"),
        X5("騾溷ｺｦ: x5", "Speed: x5"),
        X10("騾溷ｺｦ: x10", "Speed: x10"),
        MAX("騾溷ｺｦ: 譛�､ｧ", "Speed: Max"),
        ;
        private String name;
        GameSpeed(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
    }
	
	// 繧ｵ繝悶い繧ｯ繧ｷ繝ｧ繝ｳ逕ｨ縺ｮ繝倥Ν繝励さ繝ｳ繝�く繧ｹ繝�
	// %mlb : 繝槭え繧ｹ蟾ｦ繝懊ち繝ｳ
	// %mrb : 繝槭え繧ｹ蜿ｳ繝懊ち繝ｳ
	// %sft : SHIFT繧ｭ繝ｼ
	// %ctl : CTRL繧ｭ繝ｼ
	enum HelpContext {
        SHIFT_LMB_ALL("%sft,%mlb,All"),
        SHIFT_LMB_ALL_ONOFF("%sft,%mlb,ON/OFF"),
        CTRL_LMB_ALL_INVERT("%ctl,%mlb,All(Invert)"),
        ;
        private String name;
        HelpContext(String nameJ) { this.name = nameJ; }
        public String toString() { return name; }
	}

	// 鄂ｮ縺肴鋤縺域枚蟄怜� icon[]縺ｨ蟇ｾ蠢�
	enum HelpIcon {
		mlb(16),
		mrb(16),
		sft(32),
		ctl(32),
		;
        private int width;
        HelpIcon(int w) { this.width = w; }
        public int getW() { return width; }
	}

	// 繝倥Ν繝励さ繝ｳ繝�く繧ｹ繝医い繧､繧ｳ繝ｳ
	public static final Image[] icon = new Image[4];

	public static JPopupMenu popup = new JPopupMenu();
	public static PopupAction action = new PopupAction();
	public static boolean popupDisplay = false;

	public static HelpContext[] currentHelp = new HelpContext[4];
	public static int currentHelpNum = 0;
	public static String[][] currentHelpBuf = null;
	public static HelpIcon[][] currentHelpIcon = null;
	public static int helpW, helpH;
	
	public static final void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		icon[0] = ImageIO.read(loader.getResourceAsStream("images/icon/mouse_l.png"));
		icon[1] = ImageIO.read(loader.getResourceAsStream("images/icon/mouse_r.png"));
		icon[2] = ImageIO.read(loader.getResourceAsStream("images/icon/shift.png"));
		icon[3] = ImageIO.read(loader.getResourceAsStream("images/icon/ctrl.png"));
	}

	public static final HelpIcon getHelpIcon(String str) {
		if(str.indexOf("%") != 0) return null;
		return HelpIcon.valueOf(str.substring(1));
	}

	public static final Image getHelpIconImage(HelpIcon help) {
		return icon[help.ordinal()];
	}

	public static final void createPopupMenu()
	{
		JMenuItem subMenu = null;
		
		JMenu toolMenu = new JMenu(MainCategory.TOOL.toString());
		Tool[] toolList = Tool.values();
		for(int i = 0; i < toolList.length; i++)
		{
			subMenu = new JMenuItem(toolList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(toolList[i].name());
			toolMenu.add(subMenu);
		}
		popup.add(toolMenu);

		JMenu tool2Menu = new JMenu(MainCategory.TOOL2.toString());
		Tool2[] tool2List = Tool2.values();
		for(int i = 0; i < tool2List.length; i++)
		{
			subMenu = new JMenuItem(tool2List[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(tool2List[i].name());
			tool2Menu.add(subMenu);
		}
		popup.add(tool2Menu);
		
		JMenu BadgesMenu = new JMenu(MainCategory.BADGES.toString());
		Badges[] BadgesList = Badges.values();
		for(int i = 0; i < BadgesList.length; i++)
		{
			subMenu = new JMenuItem(BadgesList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(BadgesList[i].name());
			BadgesMenu.add(subMenu);
		}
		popup.add(BadgesMenu);
		
		JMenu MiscMenu = new JMenu(MainCategory.MISC.toString());
		Misc[] MiscList = Misc.values();
		for(int i = 0; i < MiscList.length; i++)
		{
			subMenu = new JMenuItem(MiscList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(MiscList[i].name());
			MiscMenu.add(subMenu);
		}
		popup.add(MiscMenu);

		JMenu foodMenu = new JMenu(MainCategory.FOODS.toString());
		Foods[] foodList = Foods.values();
		for(int i = 0; i < foodList.length; i++)
		{
			subMenu = new JMenuItem(foodList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(foodList[i].name());
			foodMenu.add(subMenu);
		}
		popup.add(foodMenu);

		JMenu cleanMenu = new JMenu(MainCategory.CLEAN.toString());
		Clean[] cleanList = Clean.values();
		for(int i = 0; i < cleanList.length; i++)
		{
			subMenu = new JMenuItem(cleanList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(cleanList[i].name());
			cleanMenu.add(subMenu);
		}
		popup.add(cleanMenu);

		subMenu = new JMenuItem(MainCategory.ACCESSORY.toString());
		subMenu.addActionListener(action);
		subMenu.setActionCommand(MainCategory.ACCESSORY.name());
		popup.add(subMenu);
		subMenu = new JMenuItem(MainCategory.PANTS.toString());
		subMenu.addActionListener(action);
		subMenu.setActionCommand(MainCategory.PANTS.name());
		popup.add(subMenu);

		JMenu floorMenu = new JMenu(MainCategory.FLOOR.toString());
		Floor[] floorList = Floor.values();
		for(int i = 0; i < floorList.length; i++)
		{
			subMenu = new JMenuItem(floorList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(floorList[i].name());
			floorMenu.add(subMenu);
		}
		popup.add(floorMenu);

		JMenu barrierMenu = new JMenu(MainCategory.BARRIER.toString());
		Barrier[] barrierList = Barrier.values();
		for(int i = 0; i < barrierList.length; i++)
		{
			subMenu = new JMenuItem(barrierList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(barrierList[i].name());
			barrierMenu.add(subMenu);
		}
		popup.add(barrierMenu);

		JMenu toysMenu = new JMenu(MainCategory.TOYS.toString());
		Toys[] toysList = Toys.values();
		for(int i = 0; i < toysList.length; i++)
		{
			subMenu = new JMenuItem(toysList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(toysList[i].name());
			toysMenu.add(subMenu);
		}
		popup.add(toysMenu);

		JMenu conveyorMenu = new JMenu(MainCategory.CONVEYOR.toString());
		Conveyor[] conveyorList = Conveyor.values();
		for(int i = 0; i < conveyorList.length; i++)
		{
			subMenu = new JMenuItem(conveyorList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(conveyorList[i].name());
			conveyorMenu.add(subMenu);
		}
		popup.add(conveyorMenu);

		JMenu voiceMenu = new JMenu(MainCategory.VOICE.toString());
		Voice[] voiceList = Voice.values();
		for(int i = 0; i < voiceList.length; i++)
		{
			subMenu = new JMenuItem(voiceList[i].toString());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(voiceList[i].name());
			voiceMenu.add(subMenu);
		}
		popup.add(voiceMenu);
	}
	
	// 驕ｸ謚槭さ繝槭Φ繝峨�繝倥Ν繝励ｒ險ｭ螳�
	public static final void setSubActionHelp(int s1, int s2) {
		MainCategory sel = MainCategory.values()[s1];
		switch(sel) {
			case TOOL:
				Tool tool = Tool.values()[s2];
				switch(tool) {
					case PUNISH:
					case SNAPPING:
					case VIBRATOR:
					case JUICE:
					case LEMON_SPLAY:
					case SELLYUKKURI:
					case HAMMER:
						currentHelp[0] = HelpContext.SHIFT_LMB_ALL;
						currentHelpNum = 1;
						break;
					default:
						currentHelpNum = 0;
						break;
				}
				break;
			case TOOL2:
				Tool2 tool2 = Tool2.values()[s2];
				switch(tool2) {
					case BRAID_PLUCK:
						currentHelp[0] = HelpContext.SHIFT_LMB_ALL;
						currentHelpNum = 1;
						break;
					case ANAL_CLOSE:
					case STALK_CUT:
					case CASTRATION:
						currentHelp[0] = HelpContext.SHIFT_LMB_ALL_ONOFF;
						currentHelp[1] = HelpContext.CTRL_LMB_ALL_INVERT;
						currentHelpNum = 2;
						break;
					case LIGHTER:
					case WATER:
					case PIN:
						currentHelp[0] = HelpContext.SHIFT_LMB_ALL;
						currentHelpNum = 1;
						break;
					case ORANGE_AMP:
					case ACCEL_AMP:
					case STOP_AMP:
						currentHelp[0] = HelpContext.SHIFT_LMB_ALL_ONOFF;
						currentHelp[1] = HelpContext.CTRL_LMB_ALL_INVERT;
						currentHelpNum = 2;
						break;
					default:
						currentHelpNum = 0;
						break;
				}
				break;
			case BADGES:
				Badges badges = Badges.values()[s2];
				switch(badges) {
					default:
						currentHelpNum = 0;
						break;
				}
				break;
			case MISC:
			case FOODS:
			case CLEAN:
				currentHelpNum = 0;
				break;
			case ACCESSORY:
			case PANTS:
				currentHelp[0] = HelpContext.SHIFT_LMB_ALL_ONOFF;
				currentHelp[1] = HelpContext.CTRL_LMB_ALL_INVERT;
				currentHelpNum = 2;
				break;
			case FLOOR:
			case BARRIER:
			case TOYS:
			case CONVEYOR:
			case VOICE:
			default:
				currentHelpNum = 0;
				break;
		}
		createHelpBuffer();
	}
	
	private static final void createHelpBuffer() {
		if(currentHelpNum == 0) {
			currentHelpBuf = null;
			currentHelpIcon = null;
			return;
		}
		currentHelpBuf = new String[currentHelpNum][];
		currentHelpIcon = new HelpIcon[currentHelpNum][];
		helpW = 5;
		helpH = 16 * currentHelpNum + 4;
		for(int i = 0; i < currentHelpNum; i++) {
			currentHelpBuf[i] = currentHelp[i].toString().split(",");
			currentHelpIcon[i] = new HelpIcon[currentHelpBuf[i].length];
			int tmpW = 0;

			for(int j = 0; j < currentHelpBuf[i].length; j++) {
				currentHelpIcon[i][j] = getHelpIcon(currentHelpBuf[i][j]);
				if(currentHelpIcon[i][j] != null) {
					tmpW += currentHelpIcon[i][j].getW();
				} else {
					tmpW += currentHelpBuf[i][j].length() * 12; 
				}
			}
			if(tmpW > helpW) helpW = tmpW;
		}
	}

	// 繧�▲縺上ｊ縺ｫ繝｡繧ｽ繝�ラ螳溯｡�
	// 繝代Λ繝｡繝ｼ繧ｿ縺ｪ縺励�SHIFT縺ｧ蜈ｨ菴灘ｮ溯｡檎ｳｻ縺ｮ繧ｳ繝槭Φ繝臥畑
	public static final void executeBodyMethod(MouseEvent e, Obj found, String method) {
		try {
			Method m;

			if(e.isShiftDown()) {
				for(Body b :Terrarium.bodyList) {
					m = b.getClass().getMethod(method, (Class<?>[])null);
					m.invoke(b, (Object[])null);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body)found).getClass().getMethod(method, (Class<?>[])null);
					m.invoke(((Body)found), (Object[])null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	// 繧�▲縺上ｊ縺ｫ繝｡繧ｽ繝�ラ螳溯｡�
	// 繝代Λ繝｡繝ｼ繧ｿboolean縲ヾHIFT縺ｧ蜈ｨ菴薙�CTRL縺ｧ蜿崎ｻ｢螳溯｡檎ｳｻ縺ｮ繧ｳ繝槭Φ繝臥畑
	public static final void executeBodyMethod(MouseEvent e, Obj found, String getMethod, String setMethod, String invMethod) {
		try {
			Method m;

			if(e.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					m = ((Body)found).getClass().getMethod(getMethod, (Class<?>[])null);
					flag = !((Boolean)m.invoke(((Body)found), (Object[])null)).booleanValue();
				}
				for(Body b :Terrarium.bodyList) {
					m = b.getClass().getMethod(setMethod, boolean.class);
					m.invoke(b, flag);
				}
			} else if(e.isControlDown()) {
				for(Body b :Terrarium.bodyList) {
					m = b.getClass().getMethod(invMethod, (Class<?>[])null);
					m.invoke(b, (Object[])null);
				}
			} else {
				if (found instanceof Body){
					m = ((Body)found).getClass().getMethod(invMethod, (Class<?>[])null);
					m.invoke(((Body)found), (Object[])null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
}

class PopupAction implements ActionListener
{
	// 繝昴ャ繝励い繝��縺ｮ驕ｸ謚槭ｒ繧ｳ繝ｳ繝懊�繝�け繧ｹ縺ｸ蜿肴丐
	public final void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();

		try {
			GadgetMenu.MainCategory sel = GadgetMenu.MainCategory.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(sel.ordinal());
			SimYukkuri.setSubMenu();
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Tool sel = GadgetMenu.Tool.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.TOOL.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Tool2 sel = GadgetMenu.Tool2.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.TOOL2.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}
		
		try {
			GadgetMenu.Badges sel = GadgetMenu.Badges.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.BADGES.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}
		
		try {
			GadgetMenu.Misc sel = GadgetMenu.Misc.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.MISC.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Foods sel = GadgetMenu.Foods.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.FOODS.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Clean sel = GadgetMenu.Clean.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.CLEAN.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Floor sel = GadgetMenu.Floor.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.FLOOR.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Barrier sel = GadgetMenu.Barrier.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.BARRIER.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Toys sel = GadgetMenu.Toys.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.TOYS.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Conveyor sel = GadgetMenu.Conveyor.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.CONVEYOR.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}

		try {
			GadgetMenu.Voice sel = GadgetMenu.Voice.valueOf(command);
			SimYukkuri.s1.setSelectedIndex(GadgetMenu.MainCategory.VOICE.ordinal());
			SimYukkuri.setSubMenu();
			SimYukkuri.s2.setSelectedIndex(sel.ordinal());
		} catch(IllegalArgumentException ex) {}
		
		GadgetMenu.popupDisplay = false;
	}
}


