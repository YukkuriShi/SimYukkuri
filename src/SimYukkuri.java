package src;



import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicBorders;

import java.io.*;

import javax.imageio.*;

import src.attachment.AccelAmpoule;
import src.attachment.CardboardBox;
import src.attachment.OrangeAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.FakeBadge;
import src.attachment.BronzeBadge;
import src.attachment.SilverBadge;
import src.attachment.GoldBadge;
import src.event.EatBodyEvent;
import src.item.*;
import src.object.Obj;
import src.object.ObjEX;
import src.object.Stalk;
import src.system.Logger;
import src.system.ModLoader;
import src.system.Translate;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;
import src.yukkuriLogic.EventLogic;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SimYukkuri extends JFrame {
	static final long serialVersionUID = 1L;
	static final String TITLE = "SimYukkuri";
	static final String VERSION = "1.12.3";
	static final Object lock = new Object();
	static boolean initialized = false;

	public static Locale locale = Locale.getDefault();

	
	private static final String[] fieldScaleTbl = {".50 (Very Large)", "x1", "x2", "x4", "x6 (Very Small)"};
	public static final int[] fieldScaleData = {50, 100, 200, 400, 600};
	public static final int[] bufferSizeData = {50, 100, 200, 400, 600};
	private static final float fieldZoomRate[][] = {
		{ 1.0f },
		{ 1.0f, 0.8f, 0.7f, 0.6f, 0.5f },
		{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f },
		{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.25f },
		{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.125f },
	};

	// 讓呎ｺ悶え繧｣繝ｳ繝峨え縺ｮ繝�Λ繝ｪ繧ｦ繝�緒逕ｻ繧ｨ繝ｪ繧｢縺ｮ繧ｵ繧､繧ｺ
	//RESOLUTIONS
	private static final int[] PAINT_PANE_X = {900, 1260};
	private static final int[] PAINT_PANE_Y = {700, 980};
	// 讓呎ｺ悶え繧｣繝ｳ繝峨え縺ｮ蜀�Κ繝槭ャ繝励し繧､繧ｺ
	static final int[] DEFAULT_MAP_X = {300, 500, 500};
	static final int[] DEFAULT_MAP_Y = {300, 500, 500};
	static final int[] DEFAULT_MAP_Z = {100, 150, 150};
	static int paintPaneMode = 0;
	// 繝｡繝九Η繝ｼ繧ｨ繝ｪ繧｢縺ｮ蟷�
	//CHANGES SIZE OF MAINMENU #SIDEPANE
	static final int MENU_PANE_X = 200;
	
	// 繧ｹ繝��繧ｿ繧ｹ縺ｮ繝ｩ繝吶Ν
	static enum LabelName {
		ENVIRONMENT("迺ｰ蠅�せ繝医Ξ繧ｹ: ", "Funds: "),
		LABEL("繧�▲縺上ｊ縺ｮ迥ｶ諷�", "Label" ),
		NAME("", ""),
		PERSONALITY(" 諤ｧ譬ｼ: ", "Attitude: "),
		INTEL(" 遏･閭ｽ: ", "Intelligence: "),
		DAMAGE(" 繝�Γ繝ｼ繧ｸ: ", "Injury: "),
		STRESS(" 繧ｹ繝医Ξ繧ｹ: ", "Stress: "),
		COMPLACENCY("Complacency","Complacency"),
		HUNGER(" 遨ｺ閻ｹ蠎ｦ: ", "Hunger: "),
		TANG(" 蜻ｳ隕� ", "Tang: "),
		SHIT(" 縺�ｓ縺�ｓ: ", "Expired Paste: "),
	//	WETSTATE ( "", "Sogginess: "),
		;
        private String name;
        LabelName(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
	}

	// 繧｢繧､繝�Β縺ｮ繧｢繧､繧ｳ繝ｳ
	static enum ItemName {
		SpermAnko("邊ｾ蟄宣､｡", "Impregnation Paste"),
		;
        private String name;
        ItemName(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
	}

	// 繧ｹ繝��繧ｿ繧ｹ縺ｮ繧｢繧､繧ｳ繝ｳ
	static enum StatusName {
		AnalClose("縺ゅ↓繧�ｋ髢蛾事", "Sealed"),
		StalkCut("闌主悉蜍｢", "Destalked"),
		BodyCut("閭守函蜴ｻ蜍｢", "Cut"),
		Predator("謐暮｣溽ｨｮ", "Predator"),
		Raper("繧後＞縺ｱ繝ｼ", "Rapist"),
		;
        private String name;
        StatusName(String nameJ, String nameE) { this.name = nameE; }
        public String toString() { return name; }
	}

	static final String[] ATTITUDE_LEVEL_J = { "雜�埋濶ｯ", "蝟�憶", "譎ｮ騾�", "繧ｲ繧ｹ", "繝峨ご繧ｹ"};
	static final String[] ATTITUDE_LEVEL_E = { "Very Nice", "Nice",  "Average", "Shithead", "Super Shithead", };
	static final String[] INTEL_LEVEL_J = { "繝舌ャ繧ｸ邏�", "譎ｮ騾�", "鬢｡蟄占┻"};
	static final String[] INTEL_LEVEL_E = { "Smart", "Average", "Dumb"};
	static final String[] TANG_LEVEL_J = { "繝舌き闊�", "譎ｮ騾�", "閧･縺医※繧�"};
	static final String[] TANG_LEVEL_E = { "Poor", "Normal", "Gourmet"};
	static final String[] COMPLACENCY_LEVEL_E = { "Rebellious", "Irritated", "Unsure", "Complacent","Content"};

	
	static int selectedGameSpeed = 1;
	static int selectedZoomScale = 0;
	JLabel title;
	JPanel rootPane	= new JPanel();
	JPanel buttonPane = new JPanel();
	static JComboBox s1, s2, s3;
	static JLabel[] buttonPaneLabel = new JLabel[LabelName.values().length];
	static JLabel[] itemLabel = new JLabel[6];
	static JLabel[] statLabel = new JLabel[6];

	static JButton saveButton, loadButton, addYukkuriButton, pauseButton, showLogButton, prevPageLogButton, nextPageLogButton, clearLogButton;
	static JToggleButton scriptButton, targetButton, pinButton, helpButton;
	static ImageIcon[] itemIcon;
	static ImageIcon[] statIcon;

	public static int clearLogTime = 0;

	public static SimYukkuri simYukkuri = null;
	public static MyPane mypane  = new MyPane();
	static Thread mythread;
	static int barrierSX = -1, barrierSY = -1;
	static int barrierEX = -1, barrierEY = -1;
	static int barrierType = 0;
	
	static public int mouseNewX = 0, mouseNewY = 0 , mouseOldX = 0, mouseOldY = 0 , mouseVX = 0, mouseVY = 0;
	static public int scrollOldX = 0, scrollOldY = 0;
	static public int[] fieldMousePos = new int[2];

	public static Dna sperm = null;	// 邊ｾ蟄宣､｡

	public SimYukkuri() {
		
		super(TITLE);

		ClassLoader loader = this.getClass().getClassLoader();
		
		MessagePool.loadMessage(loader);
		GadgetMenu.createPopupMenu();

		buttonPane.setLayout(new GridLayout(21, 1, 0, 0));
		buttonPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonPane.setPreferredSize(new Dimension(MENU_PANE_X, PAINT_PANE_Y[paintPaneMode]));
		mypane.setBorder(new EmptyBorder(0, 0, 0, 0));

		title = new JLabel();
		buttonPane.add(title);
		s1 = new JComboBox();
		s2 = new JComboBox();
		s3 = new JComboBox();
		for(int i = 0; i < buttonPaneLabel.length; i++) {
			buttonPaneLabel[i] = new JLabel(" "); 
		}
		addYukkuriButton = new JButton();
		saveButton = new JButton();
		loadButton = new JButton();

		showLogButton = new JButton();
		prevPageLogButton = new JButton();
		nextPageLogButton = new JButton();
		clearLogButton = new JButton();

		Image img1 = null;
		Image img2 = null;
		Image img3 = null;
		Image img4 = null;
		try
		{
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/popup_off.png"));
			img2 = ImageIO.read(loader.getResourceAsStream("images/icon/target.png"));
			img3 = ImageIO.read(loader.getResourceAsStream("images/icon/help_off.png"));
			img4 = ImageIO.read(loader.getResourceAsStream("images/icon/pin.png"));
		}
		catch (IOException e)
		{
			System.out.println("File I/O error [Icon]");
		}
		ImageIcon icon1 = new ImageIcon(img1);
		ImageIcon icon2 = new ImageIcon(img2);
		ImageIcon icon3 = new ImageIcon(img3);
		ImageIcon icon4 = new ImageIcon(img4);

		scriptButton = new JToggleButton(icon1);
		targetButton = new JToggleButton(icon2);
		helpButton = new JToggleButton(icon3);
		pinButton = new JToggleButton(icon4);

		// 	繧｢繧､繧ｳ繝ｳ隱ｭ縺ｿ霎ｼ縺ｿ
		itemIcon = new ImageIcon[ItemName.values().length];
		statIcon = new ImageIcon[StatusName.values().length];
		try
		{
			// 繧｢繧､繝�Β
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/sperm.png"));
			itemIcon[0] = new ImageIcon(img1);
			
			// 繧ｹ繝��繧ｿ繧ｹ
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/unshit.png"));
			statIcon[0] = new ImageIcon(img1);
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/unstalk.png"));
			statIcon[1] = new ImageIcon(img1);
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/unbaby.png"));
			statIcon[2] = new ImageIcon(img1);
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/predator.png"));
			statIcon[3] = new ImageIcon(img1);
			img1 = ImageIO.read(loader.getResourceAsStream("images/icon/raper.png"));
			statIcon[4] = new ImageIcon(img1);
		}
		catch (IOException e)
		{
			System.out.println("File I/O error [Icon]");
		}

		// 蜿ｳ蛛ｴ縺ｮ繝懊ち繝ｳ繝壹う繝ｳ菴懈�
		setLanguage();
		// 繧ｳ繝槭Φ繝峨�繧ｹ繝斐�繝峨さ繝ｳ繝懊�繝�け繧ｹ
		MyItemListener mil = new MyItemListener();
		s1.addItemListener(mil);
		s2.addItemListener(mil);
		s3.addItemListener(mil);
//		MyKeyListener mkl = new MyKeyListener();
//		s1.addKeyListener(mkl);
//		s2.addKeyListener(mkl);
//		s3.addKeyListener(mkl);
		s1.setFocusable(false);
		s2.setFocusable(false);
		s3.setFocusable(false);
		buttonPane.add(s1);
		buttonPane.add(s2);
		buttonPane.add(s3);
		ButtonListener buttonListener = new ButtonListener();

		// 繧�▲縺上ｊ霑ｽ蜉��繧ｿ繝ｳ
		addYukkuriButton.addActionListener(buttonListener);
		addYukkuriButton.setFocusable(false);
		buttonPane.add(addYukkuriButton);

		// 繧ｻ繝ｼ繝悶�繝ｭ繝ｼ繝峨�繧ｿ繝ｳ
		JPanel saveLoadPane = new JPanel();
		saveLoadPane.setLayout(new GridLayout(1, 2, 0, 0));
		saveButton.addActionListener(buttonListener);
		saveButton.setMargin(new Insets(0, 0, 0, 0));
		saveButton.setFocusable(false);
		saveLoadPane.add(saveButton);
		loadButton.addActionListener(buttonListener);
		loadButton.setMargin(new Insets(0, 0, 0, 0));
		loadButton.setFocusable(false);
		saveLoadPane.add(loadButton);
		buttonPane.add(saveLoadPane);

		// 繝ｭ繧ｰ陦ｨ遉ｺ繝懊ち繝ｳ
		JPanel logPane = new JPanel();
		logPane.setLayout(new GridLayout(1, 3, 0, 0));
		prevPageLogButton.addActionListener(buttonListener);
		prevPageLogButton.setMargin(new Insets(0, 0, 0, 0));
		prevPageLogButton.setFocusable(false);
		logPane.add(prevPageLogButton);
		showLogButton.addActionListener(buttonListener);
		showLogButton.setMargin(new Insets(0, 0, 0, 0));
		showLogButton.setFocusable(false);
		logPane.add(showLogButton);
		nextPageLogButton.addActionListener(buttonListener);
		nextPageLogButton.setMargin(new Insets(0, 0, 0, 0));
		nextPageLogButton.setFocusable(false);
		logPane.add(nextPageLogButton);
		buttonPane.add(logPane);

		// 繝ｭ繧ｰ繧ｯ繝ｪ繧｢繝懊ち繝ｳ
		clearLogButton.addActionListener(buttonListener);
		clearLogButton.setFocusable(false);
		buttonPane.add(clearLogButton);

		// 繧ｻ繝ｪ繝輔�繧ｫ繝ｼ繧ｽ繝ｫ陦ｨ遉ｺ蛻�崛繝懊ち繝ｳ
		JPanel iconPane = new JPanel();
		iconPane.setLayout(new GridLayout(1, 2, 0, 0));
		scriptButton.addActionListener(buttonListener);
		scriptButton.setMargin(new Insets(0, 0, 0, 0));
		scriptButton.setFocusable(false);
		iconPane.add(scriptButton);
		targetButton.addActionListener(buttonListener);
		targetButton.setMargin(new Insets(0, 0, 0, 0));
		targetButton.setFocusable(false);
		iconPane.add(targetButton);
		helpButton.addActionListener(buttonListener);
		helpButton.setMargin(new Insets(0, 0, 0, 0));
		helpButton.setFocusable(false);
		iconPane.add(helpButton);
		buttonPane.add(iconPane);

		// 繧｢繧､繝�Β繧｢繧､繧ｳ繝ｳ
		JPanel itemPane = new JPanel();
		itemPane.setLayout(new GridLayout(1, 6, 1, 0)); //1,6,1,0
		for(int i = 0; i < itemLabel.length; i++) {
			itemLabel[i] = new JLabel();
			itemPane.add(itemLabel[i]);
		}
	//	buttonPane.add(itemPane); //i had to make room and this doesnt seem to do anything important

		// 繧ｹ繝��繧ｿ繧ｹ
		buttonPane.add(buttonPaneLabel[LabelName.ENVIRONMENT.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.LABEL.ordinal()]);
		JPanel namePane = new JPanel();
		namePane.setLayout(new BoxLayout(namePane, BoxLayout.X_AXIS));
		pinButton.addActionListener(buttonListener);
		pinButton.setMargin(new Insets(0, 0, 0, 0));
		pinButton.setFocusable(false);
		namePane.add(pinButton);
		namePane.add(buttonPaneLabel[LabelName.NAME.ordinal()]);
		buttonPane.add(namePane);
		buttonPane.add(buttonPaneLabel[LabelName.PERSONALITY.ordinal()]);
		//buttonPane.add(buttonPaneLabel[LabelName.WETSTATE.ordinal()]);

		buttonPane.add(buttonPaneLabel[LabelName.INTEL.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.DAMAGE.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.STRESS.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.HUNGER.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.TANG.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.COMPLACENCY.ordinal()]);
		buttonPane.add(buttonPaneLabel[LabelName.SHIT.ordinal()]);

		JPanel statPane = new JPanel();
		statPane.setLayout(new GridLayout(1, 6, 1, 0));
		for(int i = 0; i < statLabel.length; i++) {
			statLabel[i] = new JLabel();
			statPane.add(statLabel[i]);
		}
		buttonPane.add(statPane);

		// setup my pane
		mypane.setFocusable(true);
		MyMouseListener mml = new MyMouseListener();
		mypane.addMouseListener(mml);
		mypane.addMouseMotionListener(mml);
		mypane.addMouseWheelListener(mml);
		MyKeyListener mkl = new MyKeyListener();
		mypane.addKeyListener(mkl);
		// setup root pane
		rootPane.setLayout(new BoxLayout(rootPane, BoxLayout.X_AXIS));
		rootPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		rootPane.add(mypane);
		rootPane.add(buttonPane);
		
		// setup my frame
		initTerrariumSize();
		mypane.loadImage();
		mypane.createBackBuffer();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(rootPane);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		
		try {
			ModLoader.setJarPath();
			
			if(simYukkuri == null) {
				simYukkuri = new SimYukkuri();
			}
			Translate.createTransTable();
			mypane.isRunning = true;
			mythread = new Thread(mypane);
			mythread.start();
		} catch(OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, "Out of Memory!");
			System.exit(0);
		}
	}

	//險�ｪ�
	private void setLanguage() {
		title.setText(VERSION + " Translated Shitaraba");
		final int selectedIndex = s1.getSelectedIndex();

		s1.setModel(new DefaultComboBoxModel(GadgetMenu.MainCategory.values()));

		if(selectedIndex > -1)
			s1.setSelectedIndex(selectedIndex);
		s3.setModel(new DefaultComboBoxModel(GadgetMenu.GameSpeed.values()));
		s3.setSelectedIndex(selectedGameSpeed);
		addYukkuriButton.setText("Add Yukkuri");
		saveButton.setText("Save");
		loadButton.setText("Load");

		prevPageLogButton.setText("<<");
		showLogButton.setText("Log");
		nextPageLogButton.setText(">>");
		clearLogButton.setText("Clear Log");

		for(LabelName l : LabelName.values()) {
			buttonPaneLabel[l.ordinal()].setText(l.toString() + " - ");
		}
		setSubMenu();
	}
	
	public static void setSubMenu() {
		switch ((GadgetMenu.MainCategory)s1.getSelectedItem()) {
			case TOOL:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Tool.values()));
				s2.setSelectedIndex(0);
				break;
			case TOOL2:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Tool2.values()));
				s2.setSelectedIndex(0);
				break;
			case BADGES:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Badges.values()));
				s2.setSelectedIndex(0);
				break;
			case MISC:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Misc.values()));
				s2.setSelectedIndex(0);
				break;
			case FOODS:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Foods.values()));
				s2.setSelectedIndex(0);
				break;
			case CLEAN:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Clean.values()));
				s2.setSelectedIndex(0);
				break;
			case FLOOR:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Floor.values()));
				s2.setSelectedIndex(0);
				break;
			case BARRIER:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Barrier.values()));
				s2.setSelectedIndex(0);
				break;
			case TOYS:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Toys.values()));
				s2.setSelectedIndex(0);
				break;
			case CONVEYOR:
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Conveyor.values()));
				s2.setSelectedIndex(0);
				break;
			case VOICE:		//螢ｰ謗帙￠
				s2.setModel(new DefaultComboBoxModel(GadgetMenu.Voice.values()));
				s2.setSelectedIndex(0);
				break;
			default:
				s2.setModel(new DefaultComboBoxModel(new String[]{" "}));
				s2.setSelectedIndex(0);
				break;
		}
	}
	
	public static void showStatus(Body b) {
		int damage = 100 * b.getDamage() / b.getDamageLimit();
		int hungry = 100 * b.getHungry() / b.getHungryLimit();
		int shit = 100 * b.getShit() / b.getShitLimit();
		int stress = 100 * b.getStress() / b.getStressLimit();

		buttonPaneLabel[LabelName.LABEL.ordinal()].setText("Sale Value: $ " + Cash.getYukkuriValue(b));

		buttonPaneLabel[LabelName.NAME.ordinal()].setText("Status of: " + b.getNameE());
//		buttonPaneLabel[LabelName.WETSTATE.ordinal()].setText(" Water Absorption: " + b.getDisplayableWetStateString());

		buttonPaneLabel[LabelName.PERSONALITY.ordinal()].setText(LabelName.PERSONALITY.toString() + ATTITUDE_LEVEL_E[b.getAttitude().ordinal()]);
		buttonPaneLabel[LabelName.INTEL.ordinal()].setText(LabelName.INTEL.toString() + INTEL_LEVEL_E[b.getIntelligence().ordinal()]);
		buttonPaneLabel[LabelName.DAMAGE.ordinal()].setText(LabelName.DAMAGE.toString() + damage + "%");
		buttonPaneLabel[LabelName.STRESS.ordinal()].setText(LabelName.STRESS.toString() + stress + "%");
		buttonPaneLabel[LabelName.HUNGER.ordinal()].setText(LabelName.HUNGER.toString() + hungry + "%");
		buttonPaneLabel[LabelName.COMPLACENCY.ordinal()].setText(LabelName.COMPLACENCY.toString() + ": " + b.getComplacencyVal() + " " + COMPLACENCY_LEVEL_E[b.getComplacency().ordinal()] + " " + b.getComplacencyDirection());
		buttonPaneLabel[LabelName.TANG.ordinal()].setText(LabelName.TANG.toString() + TANG_LEVEL_E[b.getTangType().ordinal()]);
		buttonPaneLabel[LabelName.SHIT.ordinal()].setText(LabelName.SHIT.toString() + shit + "%");

		pinButton.setSelected(b.isPin());
		if(b.getAnalClose()) {
			statLabel[0].setIcon(statIcon[0]);
			statLabel[0].setToolTipText(StatusName.AnalClose.toString());
		} else {
			statLabel[0].setIcon(null);
			statLabel[0].setToolTipText(null);
		}
		if(b.getStalkCastration()) {
			statLabel[1].setIcon(statIcon[1]);
			statLabel[1].setToolTipText(StatusName.StalkCut.toString());
		} else {
			statLabel[1].setIcon(null);
			statLabel[1].setToolTipText(null);
		}
		if(b.getBodyCastration()) {
			statLabel[2].setIcon(statIcon[2]);
			statLabel[2].setToolTipText(StatusName.BodyCut.toString());
		} else {
			statLabel[2].setIcon(null);
			statLabel[2].setToolTipText(null);
		}
		if(b.isPredatorType()) {
			statLabel[3].setIcon(statIcon[3]);
			statLabel[3].setToolTipText(StatusName.Predator.toString());
		} else {
			statLabel[3].setIcon(null);
			statLabel[3].setToolTipText(null);
		}
		if(b.isRaper()) {
			statLabel[4].setIcon(statIcon[4]);
			statLabel[4].setToolTipText(StatusName.Raper.toString());
		} else {
			statLabel[4].setIcon(null);
			statLabel[4].setToolTipText(null);
		}

		if(sperm != null) {
			itemLabel[0].setIcon(itemIcon[0]);
			itemLabel[0].setToolTipText(ItemName.SpermAnko.toString());
		} else {
			itemLabel[0].setIcon(null);
			itemLabel[0].setToolTipText(null);
		}
	}

	public class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED){
		    	if (e.getSource() == s1) {
			    	setSubMenu();
			    	GadgetMenu.setSubActionHelp(s1.getSelectedIndex(), s2.getSelectedIndex());
		    	} else if (e.getSource() == s2) {
		    		GadgetMenu.setSubActionHelp(s1.getSelectedIndex(), s2.getSelectedIndex());
		    	} else if (e.getSource() == s3) {
		    		selectedGameSpeed = s3.getSelectedIndex();
		    	}
		    }
		}		
	}
	
	public class MyKeyListener implements KeyListener {
		private int savedGameSpeed;
	
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				synchronized(SimYukkuri.lock) {
					if (selectedGameSpeed != 0) {
						savedGameSpeed = selectedGameSpeed;
						selectedGameSpeed = 0;
					}
					else {
						selectedGameSpeed = savedGameSpeed;
					}
					s3.setSelectedIndex(selectedGameSpeed);
				}
				break;
			case KeyEvent.VK_DELETE:
				synchronized(SimYukkuri.lock) {
					Terrarium.cleanAll();
				}
				break;
			case KeyEvent.VK_Z:
				synchronized(SimYukkuri.lock) {
					if(Translate.addZoomRate(-1)) {
						Point mpos = getMousePosition();
						Translate.setBufferZoom();
						if(mpos != null) {
							Translate.transCanvasToField(mpos.x, mpos.x, fieldMousePos);
							Translate.setBufferCenterPos(fieldMousePos[0], fieldMousePos[1]);
						}
					}
				}
				break;
			case KeyEvent.VK_X:
				synchronized(SimYukkuri.lock) {
					if(Translate.addZoomRate(1)) {
						Point mpos = getMousePosition();
						Translate.setBufferZoom();
						if(mpos != null) {
							Translate.transCanvasToField(mpos.x, mpos.x, fieldMousePos);
							Translate.setBufferCenterPos(fieldMousePos[0], fieldMousePos[1]);
						}
					}
				}
				break;
			case KeyEvent.VK_C:
				synchronized(SimYukkuri.lock) {
					Translate.setZoomRate(0);
					Translate.setBufferPos(0, 0);
					Translate.setBufferZoom();
				}
				break;
			case KeyEvent.VK_W:
				synchronized(SimYukkuri.lock) {
					Translate.addBufferPos(0, -Translate.getDisplayArea().height / 3);
				}
				break;
			case KeyEvent.VK_S:
				synchronized(SimYukkuri.lock) {
					Translate.addBufferPos(0, Translate.getDisplayArea().height / 3);
				}
				break;
			case KeyEvent.VK_A:
				synchronized(SimYukkuri.lock) {
					Translate.addBufferPos(-Translate.getDisplayArea().width / 3, 0);
				}
				break;
			case KeyEvent.VK_D:
				synchronized(SimYukkuri.lock) {
					Translate.addBufferPos(Translate.getDisplayArea().width / 3, 0);
				}
				break;
/*
繝�ヰ繝�げ逕ｨ
繝上う繝悶Μ繝�ラ繧呈ｮ九＠縺ｦ蜑企勁
			case KeyEvent.VK_H:
				synchronized(SimYukkuri.lock) {
					for(Body b :Terrarium.bodyList) {
						if(b.getType() != HybridYukkuri.type) b.remove();
					}
				}
				break;
*/
			default:
				break;
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}

	public class ButtonListener implements ActionListener {
		final private JFileChooser fc = new JFileChooser(ModLoader.getJarPath());

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(lock) {
				if (!initialized) {
					return;
				}
			}
			Object source = e.getSource();
			if(source.equals(saveButton)) {
				doSave();
			}
			else if(source.equals(loadButton)) {
				doLoad();
			}
			else if(source.equals(addYukkuriButton)) {
				mypane.initBodies();
			}
			else if(source.equals(prevPageLogButton)) {
				if ( MyPane.showPage <= 0 ){
					MyPane.showPage = 4;
				}else{
					MyPane.showPage--;
				}
			}
			else if(source.equals(showLogButton)) {
				MyPane.showLog = !MyPane.showLog;
			}
			else if(source.equals(nextPageLogButton)) {
				if ( MyPane.showPage >= 4 ){
					MyPane.showPage = 0;
				}else{
					MyPane.showPage++;
				}
			}
			else if(source.equals(clearLogButton)) {
				Logger.clearLog();
				SimYukkuri.clearLogTime = Terrarium.operationTime;
			}
			else if(source.equals(scriptButton)) {
				MyPane.isDisableScript = scriptButton.isSelected();
			}
			else if(source.equals(targetButton)) {
				MyPane.isEnableTarget = targetButton.isSelected();
			}
			else if(source.equals(helpButton)) {
				MyPane.isDisableHelp = helpButton.isSelected();
			}
			else if(source.equals(pinButton)) {
				if(MyPane.selectBody != null && !MyPane.selectBody.isRemoved())
				{
					if(pinButton.isSelected()) MyPane.selectBody.setPin(true);
					else MyPane.selectBody.setPin(false);
				}
			}
		}

		public void doSave() {
			synchronized(lock) {
				int result = fc.showSaveDialog(SimYukkuri.this);
				if(result != JFileChooser.APPROVE_OPTION) return;
				File file = fc.getSelectedFile();
				try {
					Terrarium.saveState(file);
				} catch(IOException e) {
					System.out.println(e);
					JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		public void doLoad() {
			synchronized(lock) {
				int result = fc.showOpenDialog(SimYukkuri.this);
				if(result != JFileChooser.APPROVE_OPTION) return;
				File file = fc.getSelectedFile();
				try {
					Terrarium.loadState(file);
				} catch(IOException e) {
					System.out.println(e);
					JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE, JOptionPane.ERROR_MESSAGE);
				} catch(ClassNotFoundException e) {
					System.out.println(e);
					JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public class MyMouseListener extends MouseAdapter {
		private Cursor cr = new Cursor(Cursor.HAND_CURSOR);
		private Cursor defCr = new Cursor(Cursor.DEFAULT_CURSOR);
		private Obj grabbedObj = null;
		private int startY = -1, startZ = -1;
		private int oX = 0, oY = 0, altitude = 0;
		private Point translatePos = new Point();
		private Rectangle imageRect = new Rectangle();
		
		private Obj getUpFront(int mx, int my, boolean stalkMode) {
			// Sort the objects according as Y position.
			List <Obj>list4sort = Terrarium.getObjList();
			list4sort.addAll(Terrarium.getFixObjList());
			list4sort.addAll(Terrarium.getObjectList());

			Collections.sort(list4sort, ObjDrawComp.INSTANCE);
			// Check whether hit or not.
			Obj found = null;
			Obj parent = null;
			Body body = null;
			Stalk stalk = null;
			int num = list4sort.size() - 1;
			Obj o = null;
			for(int i = num; i >= 0; i--) {
				o = list4sort.get(i);
				// 逕ｻ髱｢蜀�〒縺ｮ謠冗判遏ｩ蠖｢蜀�↓繧ｫ繝ｼ繧ｽ繝ｫ縺後≠繧九°繝√ぉ繝�け
				if (stalkMode && o instanceof Body) {
					// 闌弱�縺｣縺薙〓縺阪�蝣ｴ蜷医�繧�▲縺上ｊ縺ｫ繝偵ャ繝医＠縺ｦ繧ゅ▽縺�※繧玖月繧貞叙蠕�
					body = (Body)o;
					if(body.getStalks() != null && body.getStalks().size() > 0) {
						parent = body.getStalks().get(0);
					} else {
						parent = body;
					}
				} else if (!stalkMode && o instanceof Stalk) {
					// 闌弱�縺｣縺薙〓縺咲┌蜉ｹ縺ｮ蝣ｴ蜷医�闌弱↓繝偵ャ繝医＠縺ｦ繧ょ�縺ｮ繧�▲縺上ｊ繧貞叙蠕�
					stalk = (Stalk)o;
					if(stalk.getPlantYukkuri() != null) {
						parent = stalk.getPlantYukkuri();
					} else {
						parent = stalk;
					}
				} else {
					parent = o;
				}

				Rectangle screenRect = parent.getScreenRect();
				if(screenRect.contains(mx, my)) {
					// 繝偵ャ繝医＠縺溘ｉ逕ｻ蜒上�蜴溽せ縺九ｉ縺ｮ菴咲ｽｮ繧定ｨ倬鹸
					if(parent instanceof Body) {
						// 繧�▲縺上ｊ縺ｮ蝣ｴ蜷医�閹ｨ繧峨�縺ｮ縺ｧ螳溘し繧､繧ｺ繧貞叙蠕�
						((Body)parent).getExpandShape(imageRect);
					} else {
						parent.getBoundaryShape(imageRect);
					}
					found = parent;
					oX = screenRect.x + Translate.transSize(imageRect.x) - mx;
					oY = screenRect.y + Translate.transSize(imageRect.y) - my;
					break;
				}
			}

			if (found == null) {// platform has lowest priority.
				List <ObjEX>platformList = Terrarium.getPlatformList();
				for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
					ObjEX oex = (ObjEX)i.next();
					Rectangle screenRect = oex.getScreenRect();
					oex.getBoundaryShape(imageRect);
					if(screenRect.contains(mx, my)) {
						found = oex;
						oX = screenRect.x + Translate.transSize(imageRect.x) - mx;
						oY = screenRect.y + Translate.transSize(imageRect.y) - my;
					}
				}
			}			
			return found;
		}
		
		// 豎守畑繧｢繧､繝�Β驟咲ｽｮ蠎ｧ讓吶�險育ｮ�
		private Point calcObjctPutPoint(int mx, int my, Rectangle rect) {
			Point ret = null;
			
			Rectangle r = new Rectangle(mx - rect.x, my - rect.y, rect.width, rect.height);
			if(Translate.fieldPoly.contains(r)) {
				ret = Translate.invert(mx, my);
			}
			return ret;
		}

		public void mouseClicked(MouseEvent e) {
			synchronized(lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) {
					if(SwingUtilities.isRightMouseButton(e)) {
						// get up-front object
						Obj found = getUpFront(fieldMousePos[0], fieldMousePos[1], false);
						if(found == null)
						{
							if(GadgetMenu.popupDisplay) {
								GadgetMenu.popup.setVisible(false);
								GadgetMenu.popupDisplay = false;
							} else {
								GadgetMenu.popup.show(mypane, e.getX(), mypane.getY());
								GadgetMenu.popupDisplay = true;
							}
						} else {
							if(found instanceof ObjEX) {
								((ObjEX)found).invertEnabled();
							}
						}
					}
					return;
				}

				GadgetMenu.popupDisplay = false;
				switch((GadgetMenu.MainCategory)s1.getSelectedItem())
				{
					case BARRIER:
						switch((GadgetMenu.Barrier)s2.getSelectedItem())
						{
							case DELETE:
								Point pos = Translate.invert(fieldMousePos[0], fieldMousePos[1]);
								if(pos != null) {
									Barrier found = Terrarium.getBarrier(pos.x, pos.y, 10);
									if (found != null) {
										Terrarium.clearBarrier(found);
									}
								}
								return;
							case ALL_DELETE:
								Terrarium.barrierList.clear();
								Terrarium.clearMap();
								return;
							default:
								break;
						}
						break;

					case CLEAN:
						switch((GadgetMenu.Clean)s2.getSelectedItem())
						{
							case YU_CLEAN:
								Terrarium.cleanYukkuri();
								return;
							case BODY:
								Terrarium.cleanBody();
								return;
							case SHIT:
								Terrarium.cleanShit();
								return;
							case ETC:
								Terrarium.cleanEtc();
								return;
							case ALL:
								Terrarium.cleanAll();
								return;
							default:
								break;
						}
						break;
					case VOICE:
						switch((GadgetMenu.Voice)s2.getSelectedItem())
						{
							case YUKKURISITEITTENE:
								Terrarium.voice(0);
								return;
							case YUKKURIDIE:
								Terrarium.voice(1);
								return;
							default:
								break;
						}
						break;
					default:
						break;
				}
				// 繝槭え繧ｹ繧ｫ繝ｼ繧ｽ繝ｫ菴咲ｽｮ縺ｮ譛�ｂ謇句燕縺ｫ縺ゅｋ繧ｪ繝悶ず繧ｧ繧ｯ繝医ｒ蜿門ｾ�
				boolean stalkMode = false;
				if (((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.TOOL2)
						&& ((GadgetMenu.Tool2)s2.getSelectedItem() == GadgetMenu.Tool2.STALK_UNPLUG))
				{
					stalkMode = true;
				}
				Obj found = getUpFront(fieldMousePos[0], fieldMousePos[1], stalkMode);

				if (found instanceof Body) {
					showStatus((Body)found);
					MyPane.selectBody = (Body)found;
				}

				// If found, apply action to it.
				if (found != null) {
					switch ((GadgetMenu.MainCategory)s1.getSelectedItem()) {
						case TOOL:
							switch ((GadgetMenu.Tool)s2.getSelectedItem()) {
								case PUNISH:
									GadgetMenu.executeBodyMethod(e, found, "strikeByPunish");
									Terrarium.setAlarm();
									break;
								case SNAPPING:
									if(e.isShiftDown()) {
										for(Obj o :Terrarium.bodyList) {
											o.kick();
										}
										for(Obj o :Terrarium.shitList) {
											o.kick();
										}
										for(Obj o :Terrarium.vomitList) {
											o.kick();
										}
									} else {
										found.kick();
									}
									break;
								case VIBRATOR:
									GadgetMenu.executeBodyMethod(e, found, "forceToExcite");
									break;
								case JUICE:
									GadgetMenu.executeBodyMethod(e, found, "giveJuice");
									break;
								case LEMON_SPLAY:
									GadgetMenu.executeBodyMethod(e, found, "forceToSleep");
									break;
								case SELLYUKKURI:
									if (selectedGameSpeed != 0)
									{
									GadgetMenu.executeBodyMethod(e, found, "sellYukkuri");
									}
									break;
								case HAMMER:
									if(e.isShiftDown()) {
										for(Body b :Terrarium.bodyList) {
											b.strikeByHammer();
											if (!b.hasPants() && !b.isDead()) {
												int ofsX = Translate.invertX(b.getCollisionX()>>1, b.getY());
												if(b.getDirection() == Direction.LEFT) ofsX = -ofsX;
												mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b.getAgeState(), b.getShitType());
												b.stay();
											}
											b.setDirty(true);
										}
										Terrarium.setAlarm();
									} else {
										if (found instanceof Body) { 
											Body b = (Body)found;
											b.strikeByHammer();
											if (!b.hasPants() && !b.isDead()) {
												int ofsX = Translate.invertX(b.getCollisionX()>>1, b.getY());
												if(b.getDirection() == Direction.LEFT) ofsX = -ofsX;
												mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b.getAgeState(), b.getShitType());
												b.stay();
											}
											b.setDirty(true);
											Terrarium.setAlarm();
										}
									}
									break;
								case GATHERINJECTINTO:
									if (found instanceof Body) { 
										if(sperm == null){
											sperm = ((Body)found).getDna();
										}
										else{
											((Body)found).injectInto(sperm);
											sperm = null;
										}
										((Body)found).strikeByPunish();
										Terrarium.setAlarm();
									}
									else{
										sperm = null;
									}
								default:
									break;
							}
							break;
						case TOOL2:
							switch ((GadgetMenu.Tool2)s2.getSelectedItem()) {
								case BRAID_PLUCK:
									GadgetMenu.executeBodyMethod(e, found, "takeBraid");
									break;
								case ANAL_CLOSE:
									GadgetMenu.executeBodyMethod(e, found, "getAnalClose", "setAnalClose", "invAnalClose");
									break;
								case STALK_CUT:
									GadgetMenu.executeBodyMethod(e, found, "getStalkCastration", "setStalkCastration", "invStalkCastration");
									break;
								case STALK_UNPLUG:
									if (found instanceof Stalk){
										Stalk s = ((Stalk)found);
										if(s.getPlantYukkuri() != null) {
											s.getPlantYukkuri().touchStalk();
										}
									}
									break;
								case CASTRATION:
									GadgetMenu.executeBodyMethod(e, found, "getBodyCastration", "setBodyCastration", "invBodyCastration");
									break;
								case LIGHTER:
									GadgetMenu.executeBodyMethod(e, found, "giveFire");
									break;
								case PIN:
									GadgetMenu.executeBodyMethod(e, found, "givePin");
									break;
								case BURNFEET:
									GadgetMenu.executeBodyMethod(e, found, "strikeByBurn");
									break;
								case WATER:
									GadgetMenu.executeBodyMethod(e, found, "giveWater");
									break;
								case ORANGE_AMP:
									if(e.isShiftDown()) {
										int flag = 0;
										if (found instanceof Body) {
											flag = ((Body)found).getAttachmentSize(OrangeAmpoule.class);
										}
										for(Body b :Terrarium.bodyList) {
											if(flag == 0) {
												if(b.getAttachmentSize(OrangeAmpoule.class) == 0)
													b.addAttachment(new OrangeAmpoule(b));
											} else {
												if(b.getAttachmentSize(OrangeAmpoule.class) != 0)
													b.removeAttachment(OrangeAmpoule.class, true);
											}
										}
									} else if(e.isControlDown()) {
										for(Body b :Terrarium.bodyList) {
											if(b.getAttachmentSize(OrangeAmpoule.class) != 0) {
												b.removeAttachment(OrangeAmpoule.class, true);
											} else {
												b.addAttachment(new OrangeAmpoule(b));
											}
										}
									} else {
										if (found instanceof Body) {
											Body b = (Body)found;
											if(b.getAttachmentSize(OrangeAmpoule.class) != 0) {
												b.removeAttachment(OrangeAmpoule.class, true);
											} else {
												b.addAttachment(new OrangeAmpoule((Body)found));
											}
										}
									}
									break;
								case ACCEL_AMP:
									if(e.isShiftDown()) {
										int flag = 0;
										if (found instanceof Body) {
											flag = ((Body)found).getAttachmentSize(AccelAmpoule.class);
										}
										for(Body b :Terrarium.bodyList) {
											if(flag == 0) {
												if(b.getAttachmentSize(AccelAmpoule.class) == 0)
													b.addAttachment(new AccelAmpoule(b));
											} else {
												if(b.getAttachmentSize(AccelAmpoule.class) != 0)
													b.removeAttachment(AccelAmpoule.class, true);
											}
										}
									} else if(e.isControlDown()) {
										for(Body b :Terrarium.bodyList) {
											if(b.getAttachmentSize(AccelAmpoule.class) != 0) {
												b.removeAttachment(AccelAmpoule.class, true);
											} else {
												b.addAttachment(new AccelAmpoule(b));
											}
										}
									} else {
										if (found instanceof Body) {
											Body b = (Body)found;
											if(b.getAttachmentSize(AccelAmpoule.class) != 0) {
												b.removeAttachment(AccelAmpoule.class, true);
											} else {
												b.addAttachment(new AccelAmpoule((Body)found));
											}
										}
									}
									break;
								case STOP_AMP:
									if(e.isShiftDown()) {
										int flag = 0;
										if (found instanceof Body) {
											flag = ((Body)found).getAttachmentSize(StopAmpoule.class);
										}
										for(Body b :Terrarium.bodyList) {
											if(flag == 0) {
												if(b.getAttachmentSize(StopAmpoule.class) == 0)
													b.addAttachment(new StopAmpoule(b));
											} else {
												if(b.getAttachmentSize(StopAmpoule.class) != 0)
													b.removeAttachment(StopAmpoule.class, true);
											}
										}
									} else if(e.isControlDown()) {
										for(Body b :Terrarium.bodyList) {
											if(b.getAttachmentSize(StopAmpoule.class) != 0) {
												b.removeAttachment(StopAmpoule.class, true);
											} else {
												b.addAttachment(new StopAmpoule(b));
											}
										}
									} else {
										if (found instanceof Body) {
											Body b = (Body)found;
											if(b.getAttachmentSize(StopAmpoule.class) != 0) {
												b.removeAttachment(StopAmpoule.class, true);
											} else {
												b.addAttachment(new StopAmpoule((Body)found));
											}
										}
									}
									break;
							}
							break;
						case BADGES: 
							switch ((GadgetMenu.Badges)s2.getSelectedItem()) 
							{
								case FAKE:
									if (found instanceof Body) {   //TODO add method for removing FakeBadge in body, that affects yukkuri attitude/complacency etc based on intelligence
										Body b = (Body)found;
										if(b.getAttachmentSize(FakeBadge.class) != 0) {
											b.removeAttachment(FakeBadge.class, true);
										} else {
											b.setupBadgeTest(0); //TODO work in progress
										}
									}
								
								break;
								
								case BRONZE:
									if (found instanceof Body) {
										Body b = (Body)found;
										if(b.getAttachmentSize(BronzeBadge.class) != 0) {  //TODO 
											b.removeAttachment(BronzeBadge.class, true);
										} else {
											b.addAttachment(new BronzeBadge((Body)found));
										}
									}
								
								break;	
								
								case SILVER: //TODO
									
								
								break;	
								
								case GOLD: //TODO
									
								break;
							}
							break;
							
						case MISC: 
							switch ((GadgetMenu.Misc)s2.getSelectedItem()) 
							{
								case BOX:
									if (found instanceof Body) {   //TODO add method for removing FakeBadge in body, that affects yukkuri attitude/complacency etc based on intelligence
										Body b = (Body)found;
										if(b.getAttachmentSize(CardboardBox.class) != 0) {
											b.removeAttachment(CardboardBox.class, true);
										} else {
											b.addAttachment(new CardboardBox((Body)found)); //TODO work in progress
										}
									}
								
								break;
								
							}
							break;

						
						case CLEAN:
							if (found instanceof Body) {
								if (((Body)found).isDead())
								{
									found.remove();
								}
								else
									((Body)found).setCleaning();
							}
							else {
								found.remove();
							}
							break;
						case ACCESSORY:
							if(e.isShiftDown()) {
								boolean flag = true;
								if (found instanceof Body) {
									flag = !((Body)found).hasOkazari();
								}
								for(Body b :Terrarium.bodyList) {
									if (!flag && b.hasOkazari()) {
										b.takeOkazari();
									} else if(flag && !b.hasOkazari()) {
										b.giveOkazari(TrashUtil.OkazariType.DEFAULT);
										if(b.isDead()) {
											// 豁ｻ菴馴｣溘∋荳ｭ縺ｫ縺翫°縺悶ｊ謌ｻ縺吶う繝吶Φ繝育匱逕�
											EventLogic.addWorldEvent(new EatBodyEvent(b, null, null, 30), null, null);
										}
									}
								}
							} else if(e.isControlDown()) {
								for(Body b :Terrarium.bodyList) {
									if (b.hasOkazari()) {
										b.takeOkazari();
									} else {
										b.giveOkazari(TrashUtil.OkazariType.DEFAULT);
										if(b.isDead()) {
											// 豁ｻ菴馴｣溘∋荳ｭ縺ｫ縺翫°縺悶ｊ謌ｻ縺吶う繝吶Φ繝育匱逕�
											EventLogic.addWorldEvent(new EatBodyEvent(b, null, null, 30), null, null);
										}
									}
								}
							} else {
								if (found instanceof Body) {
									Body b = (Body)found; 
									if (b.hasOkazari()) {
										b.takeOkazari();
									} else {
										b.giveOkazari(TrashUtil.OkazariType.DEFAULT);
										if(b.isDead()) {
											// 豁ｻ菴馴｣溘∋荳ｭ縺ｫ縺翫°縺悶ｊ謌ｻ縺吶う繝吶Φ繝育匱逕�
											EventLogic.addWorldEvent(new EatBodyEvent(b, null, null, 30), null, null);
										}
									}
								}
							}
							break;
						case PANTS:
							if(e.isShiftDown()) {
								boolean flag = true;
								if (found instanceof Body) {
									flag = !((Body)found).hasPants();
								}
								for(Body b :Terrarium.bodyList) {
									if (!flag && b.hasPants())
										b.takePants();
									else if (flag && !b.hasPants())
										b.givePants();
								}
							} else if(e.isControlDown()) {
								for(Body b :Terrarium.bodyList) {
									if (b.hasPants())
										b.takePants();
									else
										b.givePants();
								}
							} else {
								if (found instanceof Body) { 
									if (((Body)found).hasPants())
										((Body)found).takePants();
									else
										((Body)found).givePants();
								}
							}
							break;
						case TOYS:
							switch ((GadgetMenu.Toys)s2.getSelectedItem()) {
								case YUNBA_SETUP:
									if (found instanceof Yunba) {
										Yunba.setupYunba((Yunba)found, true);
									}
									break;
								default:
									break;
							}
							break;
						default://Other tool
							break;
					}
				}
				// 菴輔ｂ縺ｪ縺�園繧偵け繝ｪ繝�け縺励◆繧芽ｨｭ鄂ｮ邉ｻ繝√ぉ繝�け
				if (found == null) {
					switch ((GadgetMenu.MainCategory)s1.getSelectedItem()) {
						case TOOL:{
							switch ((GadgetMenu.Tool)s2.getSelectedItem()) {
								case GATHERINJECTINTO:
									sperm = null;
									break;
								default:
									break;
							}
						}
						break;
						case FOODS:
						{
							Point pos;
							if((GadgetMenu.Foods)s2.getSelectedItem() == GadgetMenu.Foods.AUTO) {
								pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], AutoFeeder.getBounding());
								if (pos != null) {
									mypane.terrarium.addObjEX(ObjEX.ObjEXType.AUTOFEEDER, pos.x, pos.y, 0);
								}
							} else {
								pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Food.getBounding(0));
								if (pos != null) {
									Food.type foodType;
									switch ((GadgetMenu.Foods)s2.getSelectedItem())
									{
										case NORMAL:
										default:
											foodType = Food.type.YUKKURIFOOD;
											break;
										case BITTER:
											foodType = Food.type.BITTER;
											break;
										case LEMON_POP:
											foodType = Food.type.LEMONPOP;
											break;
										case HOT:
											foodType = Food.type.HOT;
											break;
										case VIYUGRA:
											foodType = Food.type.VIYUGRA;
											break;
										case SWEETS1:
											foodType = Food.type.SWEETS1;
											break;
										case SWEETS2:
											foodType = Food.type.SWEETS2;
											break;
										case WASTE:
											foodType = Food.type.WASTE;
											break;
									}
									mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOOD, pos.x, pos.y, foodType.ordinal());
								}
							}
						}
						break;
						
						case FLOOR:
							switch ((GadgetMenu.Floor)s2.getSelectedItem())
							{
								case TOILET: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Toilet.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.TOILET, pos.x, pos.y, 0);
									}
								}
								break;
								case TOILET2: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Toilet.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.TOILET, pos.x, pos.y, 1);
									}
								}
								break;

								case BED: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Bed.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.BED, pos.x, pos.y, 0);
									}
								}
								break;

								case STICKY_PLATE: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], StickyPlate.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.STICKYPLATE, pos.x, pos.y, 0);
									}
								}
								break;

								case HOT_PLATE: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], HotPlate.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.HOTPLATE, pos.x, pos.y, 0);
									}
								}
								break;

								case FOOD_MAKER: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], FoodMaker.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.FOODMAKER, pos.x, pos.y, 0);
									}
								}
								break;

								case MIXER: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Mixer.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.MIXER, pos.x, pos.y, 0);
									}
								}
								break;

								case DIFFUSER: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Diffuser.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.DIFFUSER, pos.x, pos.y, 0);
									}
								}
								break;

								case ORANGE_POOL: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], BreedingPool.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.ORANGEPOOL, pos.x, pos.y, 0);
									}
								}
								break;

								case BREED_POOL: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], BreedingPool.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.BREEDINGPOOL, pos.x, pos.y, 0);
									}
								}
								break;

								case GARBAGE_CHUTE: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], GarbageChute.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.GARBAGECHUTE, pos.x, pos.y, 0);
									}
								}
								break;

								case MACHINE_PRESS: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], MachinePress.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.MACHINEPRESS, pos.x, pos.y, 0);
									}
								}
								break;

								case PRODUCT_CHUTE: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], ProductChute.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.PRODUCTCHUTE, pos.x, pos.y, 0);
									}
								}
								break;
								default:
								break;
							}
							break;

						case TOYS: {
							switch ((GadgetMenu.Toys)s2.getSelectedItem()) {
								case BALL: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Toy.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.TOY, pos.x, pos.y, 0);
									}
								}
								break;
								case YUNBA: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Yunba.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.YUNBA, pos.x, pos.y, 0);
									}
								}
								break;
								case SUI: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Sui.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.SUI, pos.x, pos.y, 0);
									}
								}
								break;
								case TRASH: {
									Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Trash.getBounding());
									if (pos != null) {
										mypane.terrarium.addObjEX(ObjEX.ObjEXType.TRASH, pos.x, pos.y, 0);
									}
								}
								break;
								default:
									break;
							}
						}
						break;

						case CONVEYOR: {
							Point pos = calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], Beltconveyor.getBounding());
							if (pos != null) {
								mypane.terrarium.addObjEX(ObjEX.ObjEXType.BELTCONVEYOR, pos.x, pos.y, s2.getSelectedIndex());
							}
						}
						break;
					
						default://Other tool
							break;
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			synchronized(lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if(e.isShiftDown()) {
					scrollOldX = e.getX();
					scrollOldY = e.getY();
					return;
				}

				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					// 繝槭え繧ｹ繝峨Λ繝�げ縺ｮ螢∬ｨｭ鄂ｮ
					if ((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.BARRIER) {
						
						barrierType = 0;
						
						switch ((GadgetMenu.Barrier)s2.getSelectedItem()) {
							case GAP_MINI:
								barrierType = Terrarium.BARRIER_GAP_MINI;
								break;
							case GAP_BIG:
								barrierType = Terrarium.BARRIER_GAP_BIG;
								break;
							case NET_MINI:
								barrierType = Terrarium.BARRIER_NET_MINI;
								break;
							case NET_BIG:
								barrierType = Terrarium.BARRIER_NET_BIG;
								break;
							case WALL:
								barrierType = Terrarium.BARRIER_WALL;
								break;
							default:
								break;
						}
						if(barrierType > 0) {
							Point pos = Translate.invertLimit(fieldMousePos[0], fieldMousePos[1]);
							barrierSX = Math.max(0, Math.min(pos.x, Terrarium.MAX_X));
							barrierSY = Math.max(0, Math.min(pos.y, Terrarium.MAX_Y));
							barrierEX = barrierSX;
							barrierEY = barrierSY;
							return;
						}
					}
					if (((GadgetMenu.MainCategory)s1.getSelectedItem() != GadgetMenu.MainCategory.TOOL)
							|| ((GadgetMenu.Tool)s2.getSelectedItem() != GadgetMenu.Tool.PICKUP))
					{
						return;
					}
				}
				if (grabbedObj != null) {
					return;
				}
				// 繝槭え繧ｹ繧ｫ繝ｼ繧ｽ繝ｫ菴咲ｽｮ縺ｮ譛�ｂ謇句燕縺ｫ縺ゅｋ繧ｪ繝悶ず繧ｧ繧ｯ繝医ｒ蜿門ｾ�
				boolean stalkMode = false;
				if (((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.TOOL2)
						&& ((GadgetMenu.Tool2)s2.getSelectedItem() == GadgetMenu.Tool2.STALK_UNPLUG))
				{
					stalkMode = true;
				}

				grabbedObj = getUpFront(fieldMousePos[0], fieldMousePos[1], stalkMode);
				if (grabbedObj != null) {
					// yukkuri has been grabbed.
					startY = fieldMousePos[1];
					startZ = fieldMousePos[1] + Translate.transSize(grabbedObj.getZ() * 58 / 10);
					grabbedObj.grab();
					if (grabbedObj instanceof Body) {
						showStatus((Body)grabbedObj);
						MyPane.selectBody = (Body)grabbedObj;
					}
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			synchronized(lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if ((e.getModifiers() & (MouseEvent.BUTTON1_MASK|MouseEvent.BUTTON3_MASK)) == 0) {
					return;
				}
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					// 繝槭え繧ｹ繝峨Λ繝�げ縺ｮ螢∬ｨｭ鄂ｮ
					if ((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.BARRIER) {
						
						switch ((GadgetMenu.Barrier)s2.getSelectedItem()) {
							case GAP_MINI:
							case GAP_BIG:
							case NET_MINI:
							case NET_BIG:
							case WALL:
							{
								Point pos = Translate.invertLimit(fieldMousePos[0], fieldMousePos[1]);
								barrierEX = Math.max(0, Math.min(pos.x, Terrarium.MAX_X));
								barrierEY = Math.max(0, Math.min(pos.y, Terrarium.MAX_Y));
								if ((barrierSX != barrierEX) || (barrierSY != barrierEY)) {
									Terrarium.setBarrier(barrierSX, barrierSY, barrierEX, barrierEY, barrierType);
								}
								barrierSX = -1;
								barrierSY = -1;
								barrierEX = -1;
								barrierEY = -1;
								barrierType = 0;
								return;
							}
							default:
								break;
						}
					}
				}
				if (grabbedObj != null) {
					if(grabbedObj instanceof Body) {
						Body body = (Body)grabbedObj;
						if(body.getPullAndPush()) {
							body.releaseLockNobinobi();
						}
						if(body.canflyCheck()) {
							if(grabbedObj.getZ() > 0) {
								grabbedObj.kick(0, 0, 0);
							}
						} else {
							if(grabbedObj.getZ() > 0) {
								grabbedObj.kick(SimYukkuri.mouseVX/15, 0, SimYukkuri.mouseVY/20);
							}
						}
					} else {
						if(grabbedObj.getZ() > 0) {
							grabbedObj.kick(SimYukkuri.mouseVX/15, 0, SimYukkuri.mouseVY/20);
						}
					}
					grabbedObj.release();
					grabbedObj = null;
					startY = -1;
					startZ = -1;
					altitude = 0;
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			synchronized(lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if(e.isShiftDown()) {
					int dx = (int)((float)(scrollOldX - e.getX()) * Translate.getCurrentZoomRate()); 
					int dy = (int)((float)(scrollOldY - e.getY()) * Translate.getCurrentZoomRate());
					Translate.addBufferPos(dx, dy);
					scrollOldX = e.getX();
					scrollOldY = e.getY();
					return;
				}
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					// 繝槭え繧ｹ繝峨Λ繝�げ縺ｮ螢∬ｨｭ鄂ｮ
					if ((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.BARRIER) {
						switch ((GadgetMenu.Barrier)s2.getSelectedItem()) {
							case GAP_MINI:
							case GAP_BIG:
							case NET_MINI:
							case NET_BIG:
							case WALL:
							{
								Point pos = Translate.invertLimit(fieldMousePos[0], fieldMousePos[1]);
								barrierEX = Math.max(0, Math.min(pos.x, Terrarium.MAX_X));
								barrierEY = Math.max(0, Math.min(pos.y, Terrarium.MAX_Y));
								return;
							}
							default:
								break;
						}
					}
				}
				if (grabbedObj != null) {
					int button = 1;
					if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
						button = 1;
					}
					else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
						button = 2;
					}
					else {
						return;
					}

					if ((button == 1)
						&& ((GadgetMenu.MainCategory)s1.getSelectedItem() == GadgetMenu.MainCategory.TOOL)
						&& ((GadgetMenu.Tool)s2.getSelectedItem() == GadgetMenu.Tool.PICKUP))
					{
						// 謖√■荳翫￡
						// 逕ｻ髱｢荳翫�繝槭え繧ｹ遘ｻ蜍暮㍼繧偵�繝��蠎ｧ讓吶↓螟画鋤
						int newX = fieldMousePos[0] + oX;
						int newY = startY;
						int newZ = startZ - fieldMousePos[1];
						int hitX;
						// 縺壹ｉ縺励◆菴咲ｽｮ繧偵�繝��蠎ｧ讓吶↓螟画鋤縺励※繧ｪ繝悶ず繧ｧ繧ｯ繝医↓蜿肴丐
						switch(grabbedObj.getObjType()) {
							case YUKKURI:
								Body b = (Body)grabbedObj;
								if(b.getPullAndPush()) {
									if(b.getZ() <= 0) b.lockSetZ(newZ * Terrarium.MAX_Z / Translate.canvasH);
								} else {
									hitX = 4;
									altitude = startZ - fieldMousePos[1];
									Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
									grabbedObj.setX(translatePos.x);
									if (newZ > 0 ) {
										grabbedObj.setZ(translatePos.y);
									}
								}
								break;
							case SHIT:
							case VOMIT:
							case OBJECT:
							case FIX_OBJECT:
								hitX = grabbedObj.getPivotX();
								altitude = startZ - fieldMousePos[1];
								Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
								grabbedObj.setX(translatePos.x);
								grabbedObj.setZ(translatePos.y);
								break;
							case PLATFORM:
								hitX = grabbedObj.getPivotX();
								altitude = startZ - fieldMousePos[1];
								Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
								grabbedObj.setX(translatePos.x);
								break;
							default:
								hitX = 1;
								break;
						}
					}
					else if (button == 2) {
						// 譁ｰ縺励＞繝槭え繧ｹ蠎ｧ讓吶°繧牙次轤ｹ險育ｮ�
						int newX = fieldMousePos[0] + oX;
						int newY = fieldMousePos[1] + oY;
						int hitX;
						int hitY;
						switch(grabbedObj.getObjType()) {
							case YUKKURI:
								hitX = 4;
								hitY = 4;
								break;
							case SHIT:
							case VOMIT:
							case OBJECT:
							case FIX_OBJECT:
								hitX = grabbedObj.getPivotX();
								hitY = 4;
								break;
							case PLATFORM:
								hitX = grabbedObj.getPivotX();
								hitY = grabbedObj.getPivotY();
								break;
							default:
								hitX = 1;
								hitY = 1;
								break;
							
						}
						// 縺壹ｉ縺励◆菴咲ｽｮ繧偵�繝��蠎ｧ讓吶↓螟画鋤縺励※繧ｪ繝悶ず繧ｧ繧ｯ繝医↓蜿肴丐
						Translate.invertGround(newX, newY, hitX, hitY, translatePos);
						grabbedObj.setX(translatePos.x);
						grabbedObj.setY(translatePos.y);
					}
				}
				mouseNewX = fieldMousePos[0];
				mouseNewY = fieldMousePos[1];
			}
		}

		public void mouseMoved(MouseEvent e) {   //TODO imported wholesale from previous version, needs testing and cleanup and dialog.
			switch ((GadgetMenu.MainCategory)s1.getSelectedItem()) {
			
			case TOOL:
				switch ((GadgetMenu.Tool)s2.getSelectedItem()) {
				case HAMMER:
					Dimension size = mypane.getSize();
					int w = size.width, h = size.height;
					for (Body b:Terrarium.bodyList) {
						int offsetX = (ConstantValues.MAXSIZE - b.getSize())/2;
						int offsetY = (ConstantValues.MAXSIZE - b.getSize());
						int X = (e.getX() - offsetX)*Terrarium.MAX_X/(w-ConstantValues.MAXSIZE);
						int Y = (e.getY() - offsetY)*Terrarium.MAX_Y/(h-ConstantValues.MAXSIZE);
						int x = Translate.invX(X, Y, Terrarium.MAX_X, Terrarium.MAX_Y);
						int y = Translate.invY(X, Y, Terrarium.MAX_X, Terrarium.MAX_Y);  //TODO yukkuri dont flee in the y axis properly
						if (b.isDead())
						{
							break;  //prevent dead bodies waving their braids
						}
						if (!b.isRude()){    //TODO, poor organization, needs cleaning
							if (b.isAdult()) {
							b.setAngry();
						//	b.lookTo(x, y);
							
							}
							b.setHappiness(Happiness.SAD);
							b.runAway(x, y);
							b.setMessage(null, 20, false, true);
						 if (b.isScare()) {
						//	b.showScare(); //method needs to be written
						}
						} //End of isrude block
						if (b.isRude())   //isRude block uses a graduated response, this can be rewritten to take advantage of the new intelligence attribute, eg: dumb wouldnt understand whats going on and wouldn't flee
						{
							b.setMessage(null, 20, false, true);
							b.runAway(x, y);

							if ( b.isPatchy() == true)
							{
								//b.showPatchyHammerFear();
								b.runAway(x, y);
								b.setHappiness(Happiness.SAD);
								b.setMessage(null, 20, false, true);

							}
							else if (b.getDamagePercent() < 51)
							{
								//b.showTauntMister();
							}
							else if (b.getDamagePercent() > 50)
							{
								//b.showBegMisterStop();
								b.runAway(x, y);
								b.setHappiness(Happiness.VERY_SAD);
							}
							
							
						}
					}
				 default:
					break;
				}
			default:
				break;
			
			}

		}
		
		public void mouseEntered(MouseEvent e) {
			setCursor(cr);
		}

		public void mouseExited(MouseEvent e) {
			setCursor(defCr);
		}
		
		public void mouseWheelMoved(MouseWheelEvent e) {
			int select;

			select = s3.getSelectedIndex();
			select += e.getWheelRotation();
			if(select < 0) select = 0;
			if(select >= s3.getItemCount()) select = s3.getItemCount() - 1;
			s3.setSelectedIndex(select);
		}
	}
	
	public static void checkMouseVel() {
		mouseVX = (mouseNewX - mouseOldX) + mouseVX/3*2;
		mouseVY = (mouseNewY - mouseOldY) + mouseVY/3*2;
		mouseOldX = mouseNewX;
		mouseOldY = mouseNewY;
	}

	public void setWindowMode(int size, int scale) {
		paintPaneMode = size;

		Insets inset = getInsets();
System.out.println(getInsets());
		setPreferredSize(new Dimension(PAINT_PANE_X[size] + MENU_PANE_X, PAINT_PANE_Y[size]));
		setSize(inset.left + inset.right + PAINT_PANE_X[size] + MENU_PANE_X, inset.top + inset.bottom + PAINT_PANE_Y[size]);
		setLocation(new Point(100, 100));
		Translate.setCanvasSize(PAINT_PANE_X[size], PAINT_PANE_Y[size], fieldScaleData[scale], bufferSizeData[scale], fieldZoomRate[scale]);
		mypane.setPreferredSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMinimumSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMaximumSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
	
System.out.println("param " + getSize());
System.out.println("value " + PAINT_PANE_X[size] + "," + PAINT_PANE_Y[size]);
	}
	
	public void setFullScreenMode(int scale) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect = env.getMaximumWindowBounds();
        setSize(rect.width, rect.height);
		setLocation(new Point(0, 0));
		Translate.setCanvasSize(rect.width - MENU_PANE_X, rect.height, fieldScaleData[scale], bufferSizeData[scale], fieldZoomRate[scale]);
		mypane.setPreferredSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMinimumSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMaximumSize(new Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
	}

	void initTerrariumSize ()
	{
		String[] screen;
		String mess1, mess2, mess3, mess4;
		JComboBox cb1;
		JComboBox cb2;
		JComboBox cb3;
		JComboBox cb4;
		JComboBox cb5;
		JRadioButton draw1;
		JRadioButton draw2;
		ButtonGroup drawGrp;

		mess1 = "What kind of terrarium and window size?";
		screen = new String[]{"900*700","1260*980", "Full Screen"};
			
		mess2 = "Background Theme:   ";
		mess3 = "Item Theme:    ";
		mess4 = "Yukkuri Theme:   ";
////////// modloader size
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1, 0, 0));
		mainPanel.setPreferredSize(new Dimension(475, 240));
		JPanel winPanel = new JPanel();
		JPanel grpPanel = new JPanel();
		grpPanel.setLayout(new BoxLayout(grpPanel, BoxLayout.Y_AXIS));
		JPanel modPanel = new JPanel();
		modPanel.setLayout(new GridLayout(3, 2, 0, 0));

		JLabel label = new JLabel(mess1);
		winPanel.add(label);
		cb1 = new JComboBox(screen);
		cb1.setSelectedIndex(0);
		winPanel.add(cb1);
		cb2 = new JComboBox(fieldScaleTbl);
		cb2.setSelectedIndex(1);
		winPanel.add(cb2);
		
		drawGrp = new ButtonGroup();
		draw1 = new JRadioButton("Interpolation: Nearest Neighbor (Sharp, Recommended)");
		draw1.setSelected(true);
		drawGrp.add(draw1);
		grpPanel.add(draw1);
		draw2 = new JRadioButton("Interpolation: Bilinear (Fuzzy)");
		drawGrp.add(draw2);
		grpPanel.add(draw2);
		winPanel.add(grpPanel);

		cb3 = new JComboBox(ModLoader.getBackThemeList());
		cb3.setSelectedIndex(0);
		modPanel.add(new JLabel(mess2, JLabel.RIGHT));
		modPanel.add(cb3);

		cb4 = new JComboBox(ModLoader.getItemThemeList());
		cb4.setSelectedIndex(0);
		modPanel.add(new JLabel(mess3, JLabel.RIGHT));
		modPanel.add(cb4);

		cb5 = new JComboBox(ModLoader.getBodyThemeList());
		cb5.setSelectedIndex(0);
		modPanel.add(new JLabel(mess4, JLabel.RIGHT));
		modPanel.add(cb5);

		mainPanel.add(winPanel);
		mainPanel.add(modPanel);
		int res = JOptionPane.showConfirmDialog(this, mainPanel, SimYukkuri.TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(res != JOptionPane.OK_OPTION) {
			System.exit(0);
		}

		Terrarium.windowType = cb1.getSelectedIndex();
		Terrarium.terrariumSizeIndex = cb2.getSelectedIndex();
		Terrarium.changeTerrariumSize(DEFAULT_MAP_X[Terrarium.windowType],
										DEFAULT_MAP_Y[Terrarium.windowType],
										DEFAULT_MAP_Z[Terrarium.windowType],
										fieldScaleData[cb2.getSelectedIndex()]);
		
		if ( Terrarium.windowType != 2 ){
			setWindowMode(cb1.getSelectedIndex(), cb2.getSelectedIndex());
		}else{
			setFullScreenMode(cb2.getSelectedIndex());
		}
		
		if(draw1.isSelected()) {
			mypane.setRenderScale(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		} else {
			mypane.setRenderScale(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		
		if(cb3.getSelectedIndex() == 0) {
			ModLoader.setBackThemePath(null);
		} else {
			ModLoader.setBackThemePath(cb3.getSelectedItem().toString());
		}

		if(cb4.getSelectedIndex() == 0) {
			ModLoader.setItemThemePath(null);
		} else {
			ModLoader.setItemThemePath(cb4.getSelectedItem().toString());
		}

		if(cb5.getSelectedIndex() == 0) {
			ModLoader.setBodyThemePath(null);
		} else {
			ModLoader.setBodyThemePath(cb5.getSelectedItem().toString());
		}
	}
}


