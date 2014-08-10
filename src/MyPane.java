package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.TrashUtil.OkazariType;
import src.attachment.AccelAmpoule;
import src.attachment.BronzeBadge;
import src.attachment.CardboardBox;
import src.attachment.FakeBadge;
import src.attachment.Fire;
import src.attachment.GoldBadge;
import src.attachment.OrangeAmpoule;
import src.attachment.SilverBadge;
import src.attachment.StopAmpoule;
import src.effect.*;
import src.item.*;
import src.yukkuri.*;
import src.yukkuri.Common.Alice;
import src.yukkuri.Common.Chen;
import src.yukkuri.Common.Marisa;
import src.yukkuri.Common.Myon;
import src.yukkuri.Common.Patch;
import src.yukkuri.Common.Reimu;
import src.yukkuri.Predator.Fran;
import src.yukkuri.Predator.Remirya;
import src.yukkuri.Rare.Ayaya;
import src.yukkuri.Rare.Chiruno;
import src.yukkuri.Rare.Eiki;
import src.yukkuri.Rare.Meirin;
import src.yukkuri.Rare.Nitori;
import src.yukkuri.Rare.Ran;
import src.yukkuri.Rare.Sakuya;
import src.yukkuri.Rare.Suwako;
import src.yukkuri.Rare.Tenko;
import src.yukkuri.Rare.Udonge;
import src.yukkuri.Rare.Yuuka;
import src.yukkuri.Rare.Yuyuko;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues;
import src.yukkuriBody.ConstantValues.*;

public class MyPane extends JPanel implements Runnable {
	static final long serialVersionUID = 4L;
	public boolean isRunning = false;
	public Terrarium terrarium = new Terrarium();

	// 陦ｨ遉ｺ逕ｨ縺ｮ繝ｪ繧ｵ繧､繧ｺ縺励◆閭梧勹逕ｻ蜒�
	private Image backGroundImage;
	private Image[] seasonImage = new Image[4];
	// 譎る俣蟶ｯ遨ｺ豌苓牡
	private Color[][] defaultDayColor = {
		{new Color(  0,   0,  71,  12), new Color(  0,   0,  73,  57)},	// 譛�
		{null, null},													// 譏ｼ	
		{new Color(255,  54,  19,  37), new Color(177,  72,  49,  21)},	// 螟�
		{new Color(  0,   0,  20, 113), new Color(  0,   0,  40,  70)},	// 螟�
	};
	private LinearGradientPaint[] dayColor = new LinearGradientPaint[4];
	// 繝舌ャ繧ｯ繝舌ャ繝輔ぃ
	private BufferedImage backBuffer = null;
	private Graphics2D backBufferG2 = null;

	// 謠冗判逕ｨ繝�Φ繝昴Λ繝ｪ
	private Image[] drawLayer = new Image[10];
	private int[] drawMode = new int[10];
	private Image[] drawLayer2 = new Image[10];
	private int[] drawMode2 = new int[10];
	private ArrayList<Body> msgList = new ArrayList<Body>(100);
	
	private Object renderScale;

	static final int MSG_BOX_CHAR_NUM = 13;
	static boolean showLog = false;
	static int showPage = 0;

	static final Random rnd = new Random();
	static final int PAUSE = -1, MAX = 1, DECUPLE = 10, QUINTUPLE = 20, DOUBLE = 50, NORMAL = 100;
	static final int gameSpeed[]={PAUSE, NORMAL, DOUBLE, QUINTUPLE, DECUPLE, MAX};

	static JComboBox cb1;
	static JComboBox cb2;
	static JComboBox cb3;
	static JComboBox cb4;
	static JComboBox cb5;
	static final String[] namesCommonJ = {Marisa.nameE + " $" + Marisa.valuePurchase, Reimu.nameE + " $" + Reimu.valuePurchase, Alice.nameE + " $" + Alice.valuePurchase, Patch.nameE + " $" + Patch.valuePurchase, Chen.nameE + " $" + Chen.valuePurchase, Myon.nameE + " $" + Chen.valuePurchase};
	static final String[] namesRareJ = {Yurusanae.nameE + " $" + Yurusanae.valuePurchase, Yuyuko.nameE + " $" + Yuyuko.valuePurchase, Ayaya.nameE + " $" + Ayaya.valuePurchase, Tenko.nameE + " $" + Tenko.valuePurchase, Udonge.nameE + " $" + Udonge.valuePurchase, Meirin.nameE + " $" + Meirin.valuePurchase, Suwako.nameE + " $" + Suwako.valuePurchase, Chiruno.nameE + " $" + Chiruno.valuePurchase, Eiki.nameE + " $" + Eiki.valuePurchase, Ran.nameE + " $" + Ran.valuePurchase, Nitori.nameE + " $" + Nitori.valuePurchase, Yuuka.nameE + " $" + Yuuka.valuePurchase, Sakuya.nameE + " $" + Sakuya.valuePurchase};
	static final String[] namesPredatorJ = {Remirya.nameE + " $" + Remirya.valuePurchase, Fran.nameE + " $" + Fran.valuePurchase};

	// 謠冗判險ｭ螳壹ヵ繝ｩ繧ｰ
	public static boolean isDisableScript = false;
	public static boolean isEnableTarget = false;
	public static boolean isDisableHelp = false;
	// 繧ｫ繝ｼ繧ｽ繝ｫ
	private Image cursor[] = new Image[4];
	private Image select[] = new Image[4];
	public static Body selectBody = null;
	static ArrayList<Rectangle> markList = new ArrayList<Rectangle>();
	// 繧�▲縺上ｊ縺ｮ霍ｳ縺ｭ遘ｻ蜍�
	private static final int BODY_JUMP[] = {0, 8, 12, 14, 15, 14, 12, 8, 0};
	private static final int BODY_JUMP_LEVEL[] = {2, 2, 1};
	private static final int BODY_FLY[] = {0, 5, 10, 14, 14, 14, 10, 5, 0};
	
	// 讓呎ｺ悶�螢√Λ繧､繝ｳ險ｭ螳�
	private static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
	private static final Stroke WALL_STROKE = new BasicStroke(3.0f);
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	private static final Font NEGI_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 120);
	
	// 險育ｮ励ユ繝ｳ繝昴Λ繝ｪ
	private static Point tmpPoint = new Point();
	private static Rectangle tmpRect = new Rectangle();
	private static Rectangle bodyOriginalRect = new Rectangle();
	private static Rectangle bodyExpandRect = new Rectangle();
	private static Rectangle bodyOriginalImage = new Rectangle();
	private static Rectangle bodyExpandImage = new Rectangle();

	// 蜈ｨ逕ｻ蜒上�隱ｭ縺ｿ霎ｼ縺ｿ
	public void loadImage()
	{
		float[] frac = {0.0f, 1.0f};
		Color[] col = new Color[2];
		for(int i = 0; i < 4; i++) {
			if(defaultDayColor[i][0] == null) {
				dayColor[i] = null;
				continue;
			}
			col[0] = defaultDayColor[i][0];
			col[1] = defaultDayColor[i][1];
			dayColor[i] = new LinearGradientPaint(0.0f, 0.0f, 0.0f, 1.0f, frac, col);
		}

		try {
			ClassLoader loader = this.getClass().getClassLoader();
			// 閭梧勹
			backGroundImage = ModLoader.loadBackImage(loader, "back.jpg");
			seasonImage[0] = ModLoader.loadBackImage(loader, "backSpring.jpg");
			seasonImage[1] = ModLoader.loadBackImage(loader, "backSummer.jpg");
			seasonImage[2] = ModLoader.loadBackImage(loader, "backFall.jpg");
			seasonImage[3] = ModLoader.loadBackImage(loader, "backWinter.jpg");
		

			// 驕灘�
			Food.loadImages(loader, this);
			Toilet.loadImages(loader, this);
			Bed.loadImages(loader, this);
			Toy.loadImages(loader, this);
			Beltconveyor.loadImages(loader, this);
			BreedingPool.loadImages(loader, this);
			GarbageChute.loadImages(loader, this);
			FoodMaker.loadImages(loader, this);
			OrangePool.loadImages(loader, this);
			ProductChute.loadImages(loader, this);
			MachinePress.loadImages(loader, this);
			Diffuser.loadImages(loader, this);
			Yunba.loadImages(loader, this);
			StickyPlate.loadImages(loader, this);
			HotPlate.loadImages(loader, this);
			Mixer.loadImages(loader, this);
			AutoFeeder.loadImages(loader, this);
			Sui.loadImages(loader, this);
			Trash.loadImages(loader, this);

			// 繧ｨ繝輔ぉ繧ｯ繝�
			BakeSmoke.loadImages(loader, this);
			Hit.loadImages(loader, this);
			Mix.loadImages(loader, this);
			Steam.loadImages(loader, this);

			// 繧�▲縺上ｊ驕斐→繧ｵ繝悶ヱ繝ｼ繝�
			Alice.loadImages(loader, this);
			Chen.loadImages(loader, this);
			Deibu.loadImages(loader, this);
			DosMarisa.loadImages(loader, this);
			Fran.loadImages(loader, this);
			Marisa.loadImages(loader, this);
			MarisaKotatsumuri.loadImages(loader, this);
			MarisaReimu.loadImages(loader, this);
			MarisaTsumuri.loadImages(loader, this);
			Myon.loadImages(loader, this);
			Patch.loadImages(loader, this);
			Reimu.loadImages(loader, this);
			ReimuMarisa.loadImages(loader, this);
			Remirya.loadImages(loader, this);
			Tarinai.loadImages(loader, this);
			WasaReimu.loadImages(loader, this);
			Body.loadShadowImages(loader, this);

			Shit.loadImages(loader, this);
			Vomit.loadImages(loader, this);
			Stalk.loadImages(loader, this);
			Fire.loadImages(loader, this);
			OrangeAmpoule.loadImages(loader, this);
			AccelAmpoule.loadImages(loader, this);
			StopAmpoule.loadImages(loader, this);
			TrashUtil.loadImages(loader, this);
			//badges
		    FakeBadge.loadImages(loader, this);
		    BronzeBadge.loadImages(loader, this);
		    SilverBadge.loadImages(loader, this);
		    GoldBadge.loadImages(loader, this);
		    //other
		    CardboardBox.loadImages(loader, this);

			// 繧ｫ繝ｼ繧ｽ繝ｫ
			cursor[0] = ImageIO.read(loader.getResourceAsStream("images/icon/cur_lt.png"));
			cursor[1] = ImageIO.read(loader.getResourceAsStream("images/icon/cur_lb.png"));
			cursor[2] = ImageIO.read(loader.getResourceAsStream("images/icon/cur_rb.png"));
			cursor[3] = ImageIO.read(loader.getResourceAsStream("images/icon/cur_rt.png"));

			select[0] = ImageIO.read(loader.getResourceAsStream("images/icon/sel_0.png"));
			select[1] = ImageIO.read(loader.getResourceAsStream("images/icon/sel_1.png"));
			select[2] = ImageIO.read(loader.getResourceAsStream("images/icon/sel_2.png"));
			select[3] = ImageIO.read(loader.getResourceAsStream("images/icon/sel_3.png"));
			
			// 繧｢繧､繧ｳ繝ｳ
			GadgetMenu.loadImages(loader, this);

			System.gc();

		} catch (IOException e1) {
			System.out.println("File I/O error");
		} catch(OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, "繝｡繝｢繝ｪ荳崎ｶｳ縺ｧ縺�");
		}
	}
	
	// 繧�▲縺上ｊ逕ｨ驕�ｻｶ隱ｭ縺ｿ霎ｼ縺ｿ
	public void loadBodyImage(YukkuriUtil.YukkuriType type) {
		synchronized(SimYukkuri.lock) {
			try {
				ClassLoader loader = this.getClass().getClassLoader();
				
				Class<?> c = Class.forName("src.yukkuri." + type.className);
				Method m = c.getMethod("loadImages", ClassLoader.class, ImageObserver.class);
				m.invoke(null, loader, this);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch(OutOfMemoryError e) {
				JOptionPane.showMessageDialog(null, "繝｡繝｢繝ｪ荳崎ｶｳ縺ｧ縺�");
			}
		}
	}
	
	// 閭梧勹逕ｻ蜒上�繝ｪ繧ｵ繧､繧ｺ
	public void fitScaleBackground(Image bg, Image[] day, int w, int h) {
		backGroundImage = bg.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
	}

	// 繧ｺ繝ｼ繝�｣憺俣險ｭ螳�
	public void setRenderScale(Object hint) {
		renderScale = hint;
	}
	
	// 繝舌ャ繧ｯ繝舌ャ繝輔ぃ菴懈�
	public void createBackBuffer() {
		backBuffer = new BufferedImage(Translate.getBufferW(), Translate.getBufferH(), BufferedImage.TYPE_3BYTE_BGR);
		backBufferG2 = backBuffer.createGraphics();
		backBufferG2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}
	
	public void run() {

		JarGuard.authentication();
		
		// make initial bodies
		initBodies();
		synchronized(SimYukkuri.lock) {
			SimYukkuri.initialized = true;
		}
		// run animation
		while (isRunning) {
			int stress, speed;
			synchronized(SimYukkuri.lock) {
				stress = 100 * Terrarium.bodyList.size() / Body.getHeadageLimit() * 10000 / (Terrarium.terrariumSizeParcent*Terrarium.terrariumSizeParcent);
				speed = gameSpeed[SimYukkuri.selectedGameSpeed]; 
			}
//			SimYukkuri.buttonPaneLabel[SimYukkuri.LabelName.ENVIRONMENT.ordinal()].setText(SimYukkuri.LabelName.ENVIRONMENT.toString() + stress + "%");
			SimYukkuri.buttonPaneLabel[SimYukkuri.LabelName.ENVIRONMENT.ordinal()].setText("Funds: $" + Cash.getCash());

			if(speed != PAUSE && JarGuard.getAuthenticationResult() == 0) {
				synchronized(SimYukkuri.lock) {
					terrarium.run();
				}

				if( Terrarium.operationTime % 10 == 0 ){
					Logger.run();
				}
				SimYukkuri.checkMouseVel();

			}
			repaint();
			try {
				
				if (speed >= 0) {
					Thread.sleep(speed);
				}
				else {
					Thread.sleep(NORMAL);
				}
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
	}

	ClassLoader getImageLoader(){
		return this.getClass().getClassLoader();
	}
	
	public class MyAddYukkuriListener implements ItemListener, ActionListener {
		public void itemStateChanged(ItemEvent e) {
			
		    if (e.getStateChange() == ItemEvent.SELECTED){
		    	if (e.getSource() == cb3) {
    				if ( cb3.getSelectedIndex() == 0 ) {
    					cb1.setModel(new DefaultComboBoxModel(namesCommonJ));
    				} else if ( cb3.getSelectedIndex() == 1 ) {
    					cb1.setModel(new DefaultComboBoxModel(namesRareJ));
    				} else {
    					cb1.setModel(new DefaultComboBoxModel(namesPredatorJ));
    				}
		    	} else if (e.getSource() == cb4) {
		    		if(cb4.getSelectedIndex() == 0) {
		    			cb1.setEnabled(true);
		    			cb2.setEnabled(true);
		    			cb3.setEnabled(true);
		    		} else {
		    			cb1.setEnabled(false);
		    			cb2.setEnabled(false);
		    			cb3.setEnabled(false);
		    		}
		    	}
		    }
		}

		@Override
		public void actionPerformed(ActionEvent e) {
	    	if (e.getSource() == cb5) {
	    		String str = cb5.getSelectedItem().toString();
	    		Integer num;
	    		try {
	    			num = new Integer(str);
	    		} catch(NumberFormatException ne) {
	    			num = new Integer(1);
	    		}
	    		cb5.setSelectedItem(num.toString());
	    	}
		}		
	}
	
	void initBodies ()
	{
		ArrayList<Body> bodies = new ArrayList<Body>();
		String[] options;
		String[] ages;
		String[] rare;
		String[] mode;
		String[] num;
		String mess1, mess2, mess3, mess4;
		final int BABY = 0, CHILD = 1, ADULT = 2;
		MyAddYukkuriListener mayl = new MyAddYukkuriListener();
		cb1 = new JComboBox();
		int lang = 0;
		if (SimYukkuri.locale.getLanguage().startsWith("en")) lang = 1;
	//////////////////// People with a japanese locale or language may get japanese text, making this useless.
		lang = 1;
///////////////////////
		if (lang==0) {
		String[] tempAges = {"襍､縺｡繧�ｓ", "蟄蝉ｾ�", "螟ｧ莠ｺ"};
		ages = tempAges;
		String[] tempRare = {"騾壼ｸｸ遞ｮ","蟶悟ｰ醍ｨｮ","謐暮｣溽ｨｮ"};
		rare = tempRare;
		String[] tempo = {"縺ｯ縺�","縺�＞縺�"};
		options = tempo;
		String[] tempmode = {"OFF","騾壼ｸｸ遞ｮ","蟶悟ｰ醍ｨｮ","騾壼ｸｸ遞ｮ�句ｸ悟ｰ醍ｨｮ"};
		mode = tempmode;
		String[] tempnum = {"1","2","3","4","5","10","50","100"};
		num = tempnum;
		mess1 = "縺ｩ縺ｮ繧�▲縺上ｊ繧定ｿｽ蜉�＠縺ｾ縺吶°��";
		mess2 = "繧ゅ▲縺ｨ霑ｽ蜉�＠縺ｾ縺吶°��";
		mess3 = "繝ｩ繝ｳ繝�Β繝｢繝ｼ繝�";
		mess4 = "霑ｽ蜉�焚";
		}
		else
		{
			String[] tempAges = {"Baby ($x1)", "Child ($x2)", "Adult ($x4)"};
			ages = tempAges;
			String[] tempRare = {"Common","Rare","Predator"};
			rare = tempRare;
			String[] tempo = {"Yes","No"};
			options = tempo;
			String[] tempmode = {"None","From the Common section","From the Rare section","From Either section"};
			mode = tempmode;
			String[] tempnum = {"1","2","3","4","5","10","50","100"};
			num = tempnum;
			mess1 = "What Kind of Yukkuri? ";
			mess2 = "Add More Yukkuri? ";
			mess3 = "Let the Clerk Pick Some Out? ";
			mess4 = "How Many? ";
		}

		for (int choice = 0; choice == 0;) {
			JPanel panel = new JPanel();
			JPanel panel2 = new JPanel();
			JPanel panel3 = new JPanel();
			cb2 = new JComboBox(ages);
			cb2.setSelectedIndex(2);
			panel2.add(cb2);
			cb3 = new JComboBox(rare);
			cb3.setSelectedIndex(0);
			panel2.add(cb3);
			cb3.addItemListener(mayl);
			if ( cb3.getSelectedIndex() == 0 ) {
				cb1.setModel(new DefaultComboBoxModel(namesCommonJ));
			} else {
				cb1.setModel(new DefaultComboBoxModel(namesRareJ));
			}
			cb1.setMaximumRowCount(8);
			cb1.setSelectedIndex(0);
			panel2.add(cb1);
			JLabel modelabel = new JLabel(mess3);
			panel3.add(modelabel);
			cb4 = new JComboBox(mode);
			cb4.addItemListener(mayl);
			panel3.add(cb4);

			JLabel numlabel = new JLabel(mess4);
			panel3.add(numlabel);
			cb5 = new JComboBox(num);
			cb5.setEditable(true);
			cb5.setPreferredSize(new Dimension(40, 32));
			cb5.addActionListener(mayl);
			panel3.add(cb5);
			////////////////////////////////////////////////////// ADJUST SIZE OF THE ADD YUKKURI WINDOW yukkuriwindowsize
			JLabel label = new JLabel(mess1);
			panel.add(label);
			panel.add(panel2);
			panel.add(panel3);
			panel.setLayout(new GridLayout(3,1));
			panel.setPreferredSize(new Dimension(480, 120));
			int ret = JOptionPane.showConfirmDialog(this, panel, SimYukkuri.TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ret == 2) {
				break;
			}
			
			int maxNum;
			try {
				maxNum = Integer.parseInt(cb5.getSelectedItem().toString());
			} catch(NumberFormatException ne) {
				maxNum = 1;
			}

			int rndType = cb4.getSelectedIndex();
			for(int i = 0; i < maxNum; i++) {
				int selectType;
				int selectAge;
				switch(rndType) {
					case 0:
					default:
						selectType = cb1.getSelectedIndex();
						if (cb3.getSelectedIndex() == 1){
							selectType += 1000;
						} else if (cb3.getSelectedIndex() == 2){
							selectType += 3000;
						}
						selectAge = cb2.getSelectedIndex();
						break;
					case 1:
						selectType = rnd.nextInt(namesCommonJ.length);
						selectAge = rnd.nextInt(3);
						break;
					case 2:
						selectType = rnd.nextInt(namesRareJ.length) + 1000;
						selectAge = rnd.nextInt(3);
						break;
					case 3:
						int selectRare = rnd.nextInt(2);
						switch(selectRare) {
							case 0:
							default:
								selectType = rnd.nextInt(namesCommonJ.length);
								break;
							case 1:
								selectType = rnd.nextInt(namesRareJ.length) + 1000;
								break;
						}
						selectAge = rnd.nextInt(3);
						break;
				}
				
				if	(selectType == Reimu.type && rnd.nextInt(20) == 3)
					selectType = WasaReimu.type;
				
				if(selectType == Reimu.type && rnd.nextInt(24) == 0)
					selectType = Deibu.type;
			/*	if(selectType == Marisa.type && rnd.nextInt(70) == 0)
					selectType = MarisaTsumuri.type;
				if(selectType == Marisa.type && rnd.nextInt(125) == 0)
					selectType = MarisaKotatsumuri.type;
					*/
				if(selectType == Ayaya.type && rnd.nextInt(2) == 0)
					selectType = Kimeemaru.type; 

				AgeState age;
				switch (selectAge) {
				case BABY:
					age = AgeState.BABY;
					break;
				case CHILD:
					age = AgeState.CHILD;
					break;
				case ADULT:
				default:
					age = AgeState.ADULT;
					break;
				}
				Body b = terrarium.makeBody(rnd.nextInt(Terrarium.MAX_X), rnd.nextInt(Terrarium.MAX_Y), 0, selectType, null, age, null, null);
				b.addAge(256);
				bodies.add(b);
			}
			choice = 1;
			choice = JOptionPane.showOptionDialog(this, mess2 + System.getProperty("line.separator"), SimYukkuri.TITLE, JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		}
		for (Body b: bodies) {
			synchronized(SimYukkuri.lock) {
				terrarium.addBody(b);
				Cash.buyYukkuri(b);
			}
		}
	}

	// 繧�▲縺上ｊ逕ｨ謠冗判菴咲ｽｮ縺ｮ險育ｮ�
	private void calcDrawBodyPosition(Point origin, int w, int h, int px, int py, Rectangle rect) {
		int sizeW = Translate.transSize(w);
		int sizeH = Translate.transSize(h);
		int pivX = Translate.transSize(px);
		int pivY = Translate.transSize(py);
		rect.x = origin.x - pivX;
		rect.y = origin.y - pivY;
		rect.width = sizeW;
		rect.height = sizeH;
	}

	// 豎守畑縺ｮ謠冗判菴咲ｽｮ縺ｮ險育ｮ�
	private void calcDrawPosition(Obj o, Rectangle rect) {
		int sizeW = Translate.transSize(o.getW());
		int sizeH = Translate.transSize(o.getH());
		int pivX = Translate.transSize(o.getPivotX());
		int pivY = Translate.transSize(o.getPivotY());
		Translate.translate(o.getX(), o.getY(), tmpPoint);
		rect.x = tmpPoint.x - pivX;
		rect.y = tmpPoint.y - pivY;
		rect.width = sizeW;
		rect.height = sizeH;

		// 繧ｪ繝悶ず繧ｧ繧ｯ繝医↓謠冗判諠��繧定ｨｭ螳�
		o.setScreenPivot(tmpPoint);
		o.setScreenRect(rect);
	}

	public void paint(Graphics g) {
		synchronized(SimYukkuri.lock) {
			List <Obj>list4sort = Terrarium.getObjList();
			list4sort.addAll(Terrarium.getFixObjList());
			list4sort.addAll(Terrarium.getObjectList());
			list4sort.addAll(Terrarium.sortLightEffectList);
			Collections.sort(list4sort, ObjDrawComp.INSTANCE);
			int w = Translate.getCanvasW(), h = Translate.getCanvasH();

			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, renderScale);

			Rectangle dispArea = Translate.getDisplayArea();
			backBufferG2.setClip(dispArea.x, dispArea.y, dispArea.width, dispArea.height);

			msgList.clear();
			markList.clear();
			if(selectBody != null)
			{
				if(selectBody.isRemoved()) selectBody = null;
				else SimYukkuri.showStatus(selectBody);
			}
			// draw background and toilet
			//backBufferG2.drawImage(backGroundImage, 0, 0, Translate.getFieldW(), Translate.getFieldH(), this);
			backBufferG2.drawImage(seasonImage[Terrarium.getSeasonState().ordinal()], 0, 0, Translate.getFieldW(), Translate.getFieldH(), this);


			
			List <ObjEX>platformList = Terrarium.getPlatformList();
			for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
				ObjEX oex = i.next();
				calcDrawPosition(oex, tmpRect);
				backBufferG2.drawImage(oex.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
			}

			// draw barriers
			for (Barrier b: Terrarium.getBarriers()) {
				int sx, sy, ex, ey;
				Translate.translate(b.getSX(), b.getSY(), tmpPoint);
				sx = tmpPoint.x;
				sy = tmpPoint.y;
				Translate.translate(b.getEX(), b.getEY(), tmpPoint);
				ex = tmpPoint.x;
				ey = tmpPoint.y;
				backBufferG2.setStroke(WALL_STROKE);
				backBufferG2.setColor(b.getColor());
				backBufferG2.drawLine(sx, sy, ex, ey);
				backBufferG2.setStroke(DEFAULT_STROKE);
			}
			if ((SimYukkuri.barrierSX >= 0) && (SimYukkuri.barrierSY >= 0) && (SimYukkuri.barrierEX >= 0) && (SimYukkuri.barrierEY >= 0)) {
				int sx, sy, ex, ey;
				Translate.translate(SimYukkuri.barrierSX, SimYukkuri.barrierSY, tmpPoint);
				sx = tmpPoint.x;
				sy = tmpPoint.y;
				Translate.translate(SimYukkuri.barrierEX, SimYukkuri.barrierEY, tmpPoint);
				ex = tmpPoint.x;
				ey = tmpPoint.y;
				backBufferG2.setStroke(WALL_STROKE);
				backBufferG2.setColor(Color.WHITE);
				backBufferG2.drawLine(sx, sy, ex, ey);
				backBufferG2.setStroke(DEFAULT_STROKE);
			}
			// draw yukkuri, food and shit
			for (Obj o: list4sort) {
				switch (o.objType) {
				case YUKKURI:
				{
					Body b = (Body)o;
					int ageIndex = b.getAgeState().ordinal();
					b.getBoundaryShape(bodyOriginalImage);	// 蜴溷ｯｸ逕ｻ蜒上し繧､繧ｺ縺ｨ荳ｭ蠢�せ
					b.getExpandShape(bodyExpandImage);	// 閹ｨ蠑ｵ繧りｨ育ｮ励↓蜈･繧後◆菴薙�逕ｻ蜒上し繧､繧ｺ縺ｨ荳ｭ蠢�せ
					int shadowH = b.getShadowH();			// 蠖ｱ縺ｮ逕ｻ蜒城ｫ倥＆  讓ｪ縺ｯ菴薙�蟷�ｒ菴ｿ縺�

					Translate.translate(b.getX(), b.getY(), tmpPoint);
					calcDrawBodyPosition(tmpPoint, bodyOriginalImage.width, bodyOriginalImage.height, bodyOriginalImage.x, bodyOriginalImage.y, bodyOriginalRect);
					calcDrawBodyPosition(tmpPoint, bodyExpandImage.width, bodyExpandImage.height, bodyExpandImage.x, bodyExpandImage.y, bodyExpandRect);
					int braidW = Translate.transSize(b.getBraidW());
					int braidH = Translate.transSize(b.getBraidH());

					// 蜍輔￠縺ｪ縺�ｿｽ蜉�
					int[] jumpTable;

					if(b.isFlyingType()) {
						jumpTable = BODY_FLY;
					} else {
						jumpTable = BODY_JUMP;
					}
					// 蠖ｱ
					if(b.isDropShadow())
						backBufferG2.drawImage(b.getShadowImage(), bodyExpandRect.x, bodyExpandRect.y + bodyExpandRect.height - shadowH, bodyExpandRect.width, shadowH, this);

					// 譛ｬ菴薙′螳吶↓豬ｮ縺�※繧句�
					bodyOriginalRect.y -= Translate.translateZ(b.getZ());
					bodyExpandRect.y -= Translate.translateZ(b.getZ());

					// 謠冗判諠��縺ｮ逋ｻ骭ｲ
					b.setScreenPivot(tmpPoint);
					b.setScreenRect(bodyExpandRect);

					// 繧ｫ繝ｼ繧ｽ繝ｫ逋ｻ骭ｲ
					if(b.isPin()) {
						Rectangle rect = new Rectangle();
						rect.x = bodyExpandRect.x;
						rect.y = bodyExpandRect.y;
						rect.width = bodyExpandRect.width;
						rect.height = bodyExpandRect.height;
						markList.add(rect);
					}

					int direction = b.getDirection().ordinal();
					
					// 譛ｬ菴捺緒逕ｻ
					int num = b.getBodyBaseImage(drawLayer, drawMode);
					if(drawMode[0] == 0) {
						// 豁｣髱｢蜷代″
						// 蜈ｨ繝ｬ繧､繝､繝ｼ繧貞酔荳�ｺｧ讓吶↓謠冗判
						for(int i = 0; i < num; i++) {
							backBufferG2.drawImage(drawLayer[i], bodyExpandRect.x, bodyExpandRect.y, bodyExpandRect.width, bodyExpandRect.height, this);
						}
					} else if(drawMode[0] == 1) {
						// 騾壼ｸｸ
						int faceOfsY = 0;
						int faceOfsH = 0;
						int okazariOfsY = 0;
						
						// 螯雁ｨ�凾縺ｮ繧ｵ繧､繧ｺ險育ｮ�
						okazariOfsY -= (int)((bodyExpandRect.height - bodyOriginalRect.height) * 0.9f);
						faceOfsY -= (int)((bodyExpandRect.height - bodyOriginalRect.height) * 0.6f);
						// 陦ｨ諠�叙蠕�
						int jy = 0;
						int nobinobiYSize = 0;
						int num2 = b.getFaceImage(drawLayer2, drawMode2);
						switch(drawMode2[0]) {
							case 0:
							default:
								break;
							case 1:
								// 逋ｺ諠�螟ｧ繧ｸ繝｣繝ｳ繝�
								jy = Translate.transSize(jumpTable[(int)b.getAge() % 9]/BODY_JUMP_LEVEL[ageIndex]);
								bodyOriginalRect.y -= jy;
								bodyExpandRect.y -= jy;
								break;
							case 2:
								// 縺吶▲縺阪ｊ
								nobinobiYSize = Translate.transSize(jumpTable[(int)b.getAge()/2 % 9]/2/BODY_JUMP_LEVEL[ageIndex]*5/2);
								bodyOriginalRect.y -= nobinobiYSize;
								bodyExpandRect.y -= nobinobiYSize;
								bodyExpandRect.height += nobinobiYSize;
								break;
							case 3:
								// 騾壼ｸｸ繧ｸ繝｣繝ｳ繝�
								jy = Translate.transSize(jumpTable[(int)b.getAge() % 9]/2/BODY_JUMP_LEVEL[ageIndex]);
								bodyOriginalRect.y -= jy;
								bodyExpandRect.y -= jy;
								break;
							case 4:
								// 縺ｮ縺ｳ縺ｮ縺ｳ
								nobinobiYSize = Translate.transSize(jumpTable[(int)b.getAge()/2 % 9]/2/BODY_JUMP_LEVEL[ageIndex]*5/2);
								bodyOriginalRect.y -= nobinobiYSize;
								bodyExpandRect.y -= nobinobiYSize;
								bodyExpandRect.height += nobinobiYSize;
								break;
						}

						// 閭ｴ菴捺緒逕ｻ
						backBufferG2.drawImage(drawLayer[0], bodyExpandRect.x, bodyExpandRect.y, bodyExpandRect.width, bodyExpandRect.height, this);

						// 縺ｲ縺｣縺ｱ繧頑凾縺ｮ繧ｵ繧､繧ｺ險育ｮ�
						// 蛟咲紫縺ｯ隕九◆逶ｮ縺ｧ驕ｩ蠖薙↓隱ｿ謨ｴ
						int force = b.getExternalForce();
						if(force > 0) {
							faceOfsY += (int)((float)force * 1.25f);
							okazariOfsY += (int)((float)force * 0.9f);
						} else if(force < 0) {
							faceOfsY -= (int)((float)force * 0.4f);
							faceOfsH += (int)((float)force * 1.25f);
							okazariOfsY += (int)((float)force * 0.5f);
						}
						
						// 縺翫°縺悶ｊ
						if(num == 2) {
							if(b.getOkazariType() == OkazariType.DEFAULT) {
								backBufferG2.drawImage(drawLayer[1], bodyOriginalRect.x, bodyOriginalRect.y + okazariOfsY, bodyOriginalRect.width, bodyOriginalRect.height, this);
							} else {
								TrashUtil.getOkazariPos(bodyOriginalRect, b, direction, drawMode);
								backBufferG2.drawImage(drawLayer[1], drawMode[0], drawMode[1] + okazariOfsY, drawMode[2], drawMode[3], this);
							}
						}
						// 鬘�
						backBufferG2.drawImage(drawLayer2[0], bodyOriginalRect.x, bodyOriginalRect.y + faceOfsY, bodyOriginalRect.width, bodyOriginalRect.height + faceOfsH, this);
						// 菴楢｡ｨ繧ｨ繝輔ぉ繧ｯ繝郁｡ｨ遉ｺ
						num = b.getEffectImage(drawLayer, drawMode);
						for(int i = 0; i < num; i++) {
							backBufferG2.drawImage(drawLayer[i], bodyExpandRect.x, bodyExpandRect.y, bodyExpandRect.width, bodyExpandRect.height, this);
						}
						// 蛻�妙繝槭せ繧ｯ
						if(b.getCriticalDamege() == CriticalDamegeType.CUT) {
							backBufferG2.drawImage(b.getImage(ConstantValues.BODY_CUT,direction), bodyExpandRect.x, bodyExpandRect.y + (bodyExpandRect.height / 4), bodyExpandRect.width, bodyExpandRect.height, this);
						}
						
						// 縺翫＆縺�
						num = b.getBraidImage(drawLayer, drawMode);
						for(int i = 0; i < num; i++) {
							backBufferG2.drawImage(drawLayer[i], bodyOriginalRect.x + ((bodyOriginalRect.width - braidW) >> 1), bodyOriginalRect.y + faceOfsY, braidW, braidH, this);
						}
						// 闊�
						if (b.isPeroPero() || b.isEating() || b.isEatingShit()) {
							if (b.getMessage() != null) {
								backBufferG2.drawImage(b.getImage(ConstantValues.LICK,direction), bodyOriginalRect.x, bodyOriginalRect.y + faceOfsY, bodyOriginalRect.width, bodyOriginalRect.height, this);
							}
						}

					}
					for(Attachment at : b.getAttach()) {
						at.getBoundaryShape(tmpRect);
						int atX = Translate.transSize(tmpRect.x);
						int atY = Translate.transSize(tmpRect.y);
						int atW = Translate.transSize(tmpRect.width);
						int atH = Translate.transSize(tmpRect.height);
						int ofsX = Translate.transSize(at.getOfsX());
						int ofsY = Translate.transSize(at.getOfsY());
						int parentOrigin = at.getParentOrigin();
						int bx, by;
						if(parentOrigin == 0) {
							bx = bodyOriginalRect.x;
							by = bodyOriginalRect.y;
							if(b.getDirection() == Direction.RIGHT) {
								bx += bodyOriginalRect.width;
								ofsX = -ofsX;
							}
						} else {
							bx = bodyExpandRect.x;
							by = bodyExpandRect.y;
							if(b.getDirection() == Direction.RIGHT) {
								bx += bodyExpandRect.width;
								ofsX = -ofsX;
							}
						}
						backBufferG2.drawImage(at.getImage(b), bx - atX + ofsX, by - atY + ofsY, atW, atH, this);
					}

					// 繝｡繝�そ繝ｼ繧ｸ蝗ｺ菴鍋匳骭ｲ
					if (b.getMessage() != null && !isDisableScript) {
						msgList.add(b);
					}
//{
//	Point pp = new Point();
//	Translate.translate(b.destX, b.destY, pp);
//	g2.drawRect(pp.x, pp.y, 3, 3);				
//}
				}
				break;
				case SHIT: {
					Shit s = (Shit)o;
					calcDrawPosition(s, tmpRect);
					backBufferG2.drawImage(s.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					tmpRect.y -= Translate.translateZ(s.getZ());
					backBufferG2.drawImage(s.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
				}
				break;
				case VOMIT: {
					Vomit v = (Vomit)o;
					calcDrawPosition(v, tmpRect);
					backBufferG2.drawImage(v.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					tmpRect.y -= Translate.translateZ(v.getZ());
					backBufferG2.drawImage(v.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
				}
				break;
				case FIX_OBJECT: {
					ObjEX oex = (ObjEX)o;
					calcDrawPosition(oex, tmpRect);
					backBufferG2.drawImage(oex.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);				
				}
				break;
				case OBJECT: {
					ObjEX oex = (ObjEX)o;
					calcDrawPosition(oex, tmpRect);
					backBufferG2.drawImage(oex.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					tmpRect.y -= Translate.translateZ(oex.getZ());
					int layer = oex.getImageLayerCount();
					for(int i = 0; i < layer; i++) {
						backBufferG2.drawImage(oex.getImage(i), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					}
				}
				break;
				case LIGHT_EFFECT: {
					Effect ef = (Effect)o;
					calcDrawPosition(ef, tmpRect);
					tmpRect.y -= Translate.translateZ(ef.getZ());
					backBufferG2.drawImage(ef.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);				
				}
				break;
				default:
					break;
				}
			}

			// 譛�燕髱｢繧ｨ繝輔ぉ繧ｯ繝郁｡ｨ遉ｺ
			for (Effect ef: Terrarium.frontLightEffectList) {
				calcDrawPosition(ef, tmpRect);
				tmpRect.y -= Translate.translateZ(ef.getZ());
				backBufferG2.drawImage(ef.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);				
			}
			
			// 繧ｫ繝ｼ繧ｽ繝ｫ陦ｨ遉ｺ
			if(isEnableTarget)
			{
				for (Rectangle rect: markList) {
					backBufferG2.drawImage(cursor[0], rect.x, rect.y, this);
					backBufferG2.drawImage(cursor[1], rect.x, rect.y + rect.width - 20, this);
					backBufferG2.drawImage(cursor[2], rect.x + rect.width - 20, rect.y + rect.width - 20, this);
					backBufferG2.drawImage(cursor[3], rect.x + rect.width - 20, rect.y, this);
				}
			}
			if(selectBody != null) {
				Rectangle r = selectBody.getScreenRect();
				int x = r.x + (r.width >> 1) - 12;
				int y = r.y + r.height + 2;
				backBufferG2.drawImage(select[(int)(selectBody.getAge() % 4)], x, y, this);
			}

			// 繝舌ャ繧ｯ繝舌ャ繝輔ぃ縺ｮ霆｢騾�
			g2.drawImage(backBuffer, 0, 0, Translate.getCanvasW(), Translate.getCanvasH(),
						dispArea.x, dispArea.y, dispArea.x + dispArea.width, dispArea.y + dispArea.height, this);

			// 譎る俣蟶ｯ繧ｰ繝ｩ繝�
			if(dayColor[Terrarium.getDayState().ordinal()] != null) {
				g2.setPaint(dayColor[Terrarium.getDayState().ordinal()]);
				g2.fillRect(0, 0, Translate.getFieldW(), Translate.getFieldH());
			}
/*
{
Point mpos = getMousePosition();
if(mpos != null) {
	int[] mpos2 = {0,0};
	Translate.transCanvasToField(mpos.x, mpos.y, mpos2);
	StringBuffer s = new StringBuffer();
	s.append(mpos.x);
	s.append(" , ");
	s.append(mpos.y);
	s.append(" -> ");
	s.append(mpos2[0]);
	s.append(" , ");
	s.append(mpos2[1]);
	g.drawString(s.toString(), 20, 20);
}
}
*/
			//繝｡繝�そ繝ｼ繧ｸ陦ｨ遉ｺ
			String message;
			int fontSize;
			int wx, wy;
			Rectangle bodyRect;
			for(Body b : msgList) {
				message = b.getMessage();
				fontSize = b.getMessageTextSize();
				if(fontSize == 120) {
					g2.setFont(NEGI_FONT);
				} else {
					g2.setFont(DEFAULT_FONT);
				}
				bodyRect = b.getScreenRect();
				int width = Math.min(message.length(), MSG_BOX_CHAR_NUM) * fontSize;
				int height = drawStringMultiLine(g2, message, 0, 0, width, false);
				
				Translate.transFieldToCanvas(bodyRect.x, bodyRect.y, drawMode);
				wx = drawMode[0] + 14;
//				if(wx + width > Translate.canvasW) wx = Translate.canvasW - width; 
				wy = drawMode[1] - height -4;
//				if(wy < 0) wy = 0;
				g2.setColor(b.getMessageBoxColor());
				g2.fillRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				g2.setColor(b.getMessageLineColor()); // no transparent black.
				g2.setStroke(new BasicStroke(b.getMessageLineWidth()));
				g2.drawRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				g2.setStroke(DEFAULT_STROKE);
				g2.setColor(b.getMessageTextColor()); // no transparent black.
				drawStringMultiLine(g2, message, wx + 4, wy + 4, width, true);
				g2.setFont(DEFAULT_FONT);
			}

			// 繝倥Ν繝苓｡ｨ遉ｺ
			if(!isDisableHelp && GadgetMenu.currentHelpNum > 0) {
				Point pos = getMousePosition();
				if(pos != null) {
					g2.setFont(DEFAULT_FONT);
					g2.setColor(Color.WHITE);
					g2.fillRoundRect(pos.x, pos.y + 20, GadgetMenu.helpW, GadgetMenu.helpH, 8, 8);
					g2.setColor(Color.BLACK);
					g2.setStroke(DEFAULT_STROKE);
					g2.drawRoundRect(pos.x, pos.y + 20, GadgetMenu.helpW, GadgetMenu.helpH, 8, 8);
					for(int i = 0; i < GadgetMenu.currentHelpNum; i++) {
						int px = pos.x + 2;
						int py = pos.y + 2 + 20 + (16 * i);
						for(int j = 0; j < GadgetMenu.currentHelpIcon[i].length; j++) {
							if(GadgetMenu.currentHelpIcon[i][j] != null) {
								g2.drawImage(GadgetMenu.getHelpIconImage(GadgetMenu.currentHelpIcon[i][j]), px, py, this);
								px += GadgetMenu.currentHelpIcon[i][j].getW();
							} else {
								drawStringMultiLine(g2, GadgetMenu.currentHelpBuf[i][j], px, py, GadgetMenu.currentHelpBuf[i][j].length() * 12, true);
								px += GadgetMenu.currentHelpBuf[i][j].length() * 12;
							}
						}
					}
				}
			}
			// 繝ｭ繧ｰ
			if ( MyPane.showLog ) {
				Logger.displayLog(g2, showPage);
			}
		}
	}
	
	private int drawStringMultiLine(Graphics2D g2d, String str, int posX, int posY, int width, boolean flag) {
		AttributedString as = new AttributedString(str);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		AttributedCharacterIterator asiterator = as.getIterator();
		FontRenderContext context = g2d.getFontRenderContext();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(as.getIterator(), context);
		float formatWidth = (float)width;
		float drawPosX = 0;
		float drawPosY = posY;
		int beginIndex = asiterator.getBeginIndex();
		int endIndex   = asiterator.getEndIndex();
		lineMeasurer.setPosition(beginIndex);
		while (lineMeasurer.getPosition() < endIndex) {
			TextLayout layout = lineMeasurer.nextLayout(formatWidth);
			drawPosY += layout.getAscent();
			if (layout.isLeftToRight()) {
				drawPosX = posX;
			}
			else {
				drawPosX = posX + formatWidth - layout.getAdvance();
			}
			if (flag) {
				layout.draw(g2d, drawPosX, drawPosY);
			}
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		return (int)Math.ceil(drawPosY);
	}
}

// 謠冗判逕ｨ繧ｽ繝ｼ繝�
final class ObjDrawComp implements Comparator<Obj> {
	final static ObjDrawComp INSTANCE = new ObjDrawComp();

	@Override final public int compare(Obj o1, Obj o2) {
		int c = o1.y - o2.y;
		if(c == 0) {
			//Improve visibility: at the same y-coordinate, draw small
			//objects after large ones.
			c = (o2 instanceof Body ? ((Body)o2).getAgeState().ordinal() : 1) -
					(o1 instanceof Body ? ((Body)o1).getAgeState().ordinal() : 1);
		}
		return c;
	}
}
