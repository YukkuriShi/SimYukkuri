package src;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.imageio.ImageIO;

/*****************************************************
	繧ｰ繝ｩ繝輔ぅ繝�け繝��繧ｿ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ諡｡蠑ｵ
	繧ｲ繝ｼ繝�幕蟋区凾縺ｫ謖�ｮ壹＠縺溘ヵ繧ｩ繝ｫ繝�°繧峨�隱ｭ縺ｿ霎ｼ縺ｿ縺ｨ繧ｨ繝ｩ繝ｼ蜃ｦ逅�ｒ陦後≧
	莉･荳九�繝輔か繝ｫ繝�ｧ区�繧貞燕謠舌→縺励※閭梧勹縺ｨ蟆冗黄縺ｮ繧ｰ繝ｩ繝輔ぅ繝�け繧帝�鄂ｮ縺吶ｋ
	
	SimYukkuri.jar
	mod <--------------------MOD繝輔か繝ｫ繝�蜷榊燕縺ｯ蝗ｺ螳�
	|
	+-back <-----------------閭梧勹繝輔か繝ｫ繝�蜷榊燕縺ｯ蝗ｺ螳�
	|  |
	|  +-閭梧勹繝��繝槫錐 <---------蜷榊燕縺ｯ閾ｪ逕ｱ縲ゅ％縺ｮ蜷榊燕縺瑚レ譎ｯ繝��繝槭Μ繧ｹ繝医↓陦ｨ遉ｺ縺輔ｌ繧�
	|     |
	|     +-back.jpg <-------蟾ｮ縺玲崛縺郁レ譎ｯ(辟｡縺上※繧ょ庄縲ら┌縺代ｌ縺ｰjar蜀��繝輔ぃ繧､繝ｫ縺御ｽｿ繧上ｌ繧�
	|     +-day      <-------譎る俣邨碁℃繝輔ぅ繝ｫ繧ｿ縲ゆｸｭ縺ｫ縺ｯevening.png遲峨ｒ蜈･繧後ｋ
	|                        (辟｡縺上※繧ょ庄縲ら┌縺代ｌ縺ｰjar蜀��繝輔ぃ繧､繝ｫ縺御ｽｿ繧上ｌ繧�
    |
    +-item <-----------------繧｢繧､繝�Β繝輔か繝ｫ繝�蜷榊燕縺ｯ蝗ｺ螳�
       |
       +-繧｢繧､繝�Β繝��繝槫錐<-------蜷榊燕縺ｯ閾ｪ逕ｱ縲ゅ％縺ｮ蜷榊燕縺後い繧､繝�Β繝��繝槭Μ繧ｹ繝医↓陦ｨ遉ｺ縺輔ｌ繧�
          |
          +-beltconveyor <--縺薙ｌ繧峨�繝�ヵ繧ｩ繝ｫ繝医�image繝輔か繝ｫ繝�→蜷後§讒区�縺ｧ驟咲ｽｮ縺吶ｋ
          +-food            (縺吶∋縺ｦ逕ｨ諢上☆繧句ｿ�ｦ√�縺ｪ縺��辟｡縺代ｌ縺ｰjar蜀��繝輔ぃ繧､繝ｫ縺御ｽｿ繧上ｌ繧�
          :
          :
          +toilet.png
*/

public class ModLoader
{
	private static final String DEFAULT_IMG_ROOT_DIR = "images/";
	private static final String DEFAULT_DATA_ROOT_DIR = "data/";
	private static final String MOD_ROOT_DIR = "mod";
	private static final String MOD_BACK_DIR = "back";
	private static final String MOD_ITEM_DIR = "item";
	private static final String MOD_BODY_DIR = "yukkuri";

	// jar繝輔ぃ繧､繝ｫ縺ｮ繝代せ
	private static String jarPath = "";
	
	// 驕ｸ謚槭＠縺溘ユ繝ｼ繝� null縺ｪ繧泳ar蜀��繝�ヵ繧ｩ繝ｫ繝育判蜒上ｒ菴ｿ逕ｨ
	private static String backTheme = null;
	private static String itemTheme = null;
	private static String bodyTheme = null;

	// jar繝輔ぃ繧､繝ｫ縺ｮ繝代せ繧貞叙蠕�
	public static void setJarPath()
	{
		String tmp = System.getProperty("java.class.path");
		jarPath = tmp.substring(0, tmp.lastIndexOf(File.separator)+1);
System.out.println("jarPath : " + jarPath);
	}
	
	public static String getJarPath()
	{
		return jarPath;
	}

	// mod/back/蜀��繝輔か繝ｫ繝�ｸ�ｦｧ繧剃ｽ懈�縺励※霑斐☆
	public static Vector<String> getBackThemeList()
	{
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_BACK_DIR);
	}

	// mod/item/蜀��繝輔か繝ｫ繝�ｸ�ｦｧ繧剃ｽ懈�縺励※霑斐☆
	public static Vector<String> getItemThemeList()
	{
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_ITEM_DIR);
	}

	// mod/yukkuri/蜀��繝輔か繝ｫ繝�ｸ�ｦｧ繧剃ｽ懈�縺励※霑斐☆
	public static Vector<String> getBodyThemeList()
	{
		return createThemeList(jarPath + MOD_ROOT_DIR + File.separator + MOD_BODY_DIR);
	}

	private static Vector<String> createThemeList(String root)
	{
		Vector<String> list = new Vector<String>();
		list.add("Default");
		
		// 繝輔か繝ｫ繝�ｸ�ｦｧ蜿門ｾ�
		File dir = new File(root);
		System.out.println("MOD search path : " + dir.getAbsolutePath());
	    File[] files = dir.listFiles();
	    if(files != null)
	    {
	    	for (int i = 0; i < files.length; i++)
	    	{
	    		if(files[i].isDirectory())
	    		{
	    			list.add(files[i].getName());
	    		}
	    	}
	    }
	    return list;
	}
	
	public static String getBackThemePath()
	{
		return backTheme;
	}
	public static void setBackThemePath(String path)
	{
		if(path == null)
		{
			backTheme = null;
		}
		else
		{
			backTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_BACK_DIR + File.separator + path + File.separator;
		}
	}

	public static String getItemThemePath()
	{
		return itemTheme;
	}
	public static void setItemThemePath(String path)
	{
		if(path == null)
		{
			itemTheme = null;
		}
		else
		{
			itemTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_ITEM_DIR + File.separator + path + File.separator;
		}
	}

	public static String getBodyThemePath()
	{
		return bodyTheme;
	}
	public static void setBodyThemePath(String path)
	{
		if(path == null)
		{
			bodyTheme = null;
		}
		else
		{
			bodyTheme = jarPath + MOD_ROOT_DIR + File.separator + MOD_BODY_DIR + File.separator + path + File.separator;
		}
	}

	// 閭梧勹隱ｭ縺ｿ霎ｼ縺ｿ
	public static Image loadBackImage(ClassLoader loader, String fileName) throws IOException
	{
		return loadImage(loader, backTheme, fileName);
	}	
	
	// 驕灘�隱ｭ縺ｿ霎ｼ縺ｿ
	public static Image loadItemImage(ClassLoader loader, String fileName) throws IOException
	{
		return loadImage(loader, itemTheme, fileName);
	}

	private static Image loadImage(ClassLoader loader, String root, String fileName) throws IOException
	{
		Image img = null;

		if(root == null)
		{
			// jar繝ｪ繧ｽ繝ｼ繧ｹ蜀��繝�ヵ繧ｩ繝ｫ繝医ヵ繧｡繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
			img = loadJarImage(loader, fileName);
		}
		else
		{
			// 螟夜Κ繝輔ぃ繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
			try
			{
				img = loadModImage(root, fileName);
			}
			catch(IOException e)
			{
				// 繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ縺ｫ螟ｱ謨励＠縺溘ｉ繝�ヵ繧ｩ繝ｫ繝医↓蛻�ｊ譖ｿ縺�
				img = loadJarImage(loader, fileName);
			}
		}
		return img;
	}
	
	// 螟夜Κ繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ
	private static Image loadModImage(String root, String fileName) throws IOException
	{
		File file = new File(root + fileName);
//System.out.println("FileLoad : " + file.getAbsolutePath());
		return ImageIO.read(file);
	}
	
	// jar繝ｪ繧ｽ繝ｼ繧ｹ縺九ｉ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ
	private static Image loadJarImage(ClassLoader loader, String fileName) throws IOException
	{
//System.out.println("JarLoad : " + DEFAULT_IMG_ROOT_DIR + fileName);
		return ImageIO.read(loader.getResourceAsStream(DEFAULT_IMG_ROOT_DIR + fileName));
	}
	
	// 繝｡繝�そ繝ｼ繧ｸ繝輔ぃ繧､繝ｫ繧帝幕縺�
	@SuppressWarnings("resource")
	public static BufferedReader openMessageFile(ClassLoader loader, String name)
	{
		BufferedReader br = null;
		
		File file = new File(jarPath + MOD_ROOT_DIR + File.separator + name);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
//System.out.println("FileLoad : " + file.getAbsolutePath());
		} catch (Exception e) {
			// 繝輔ぃ繧､繝ｫ縺檎┌縺九▲縺溘ｉjar縺九ｉ隱ｭ繧�
			InputStream is = loader.getResourceAsStream(DEFAULT_DATA_ROOT_DIR + name);
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
//System.out.println("JarError! : " + DEFAULT_DATA_ROOT_DIR + name);
			}
//System.out.println("JarLoad : " + DEFAULT_DATA_ROOT_DIR + name);
		}
		return br;
	}
	
	// 繧�▲縺上ｊ縺ｮ繝代�繝�判蜒剰ｪｭ縺ｿ霎ｼ縺ｿ
	public static void loadBodyImagePack(ClassLoader loader, Image[][][] images, String bodyName, ImageObserver io) throws IOException
	{
		int left = Body.LEFT;
		int right = Body.RIGHT;
		int babyIndex = Body.babyIndex;
		int childIndex = Body.childIndex;
		int adultIndex = Body.adultIndex;
		Image[] img = new Image[2];

		MediaTracker mt = new MediaTracker((MyPane)io);

		// left face
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "body", ".png");
		images[Body.BODY][left][adultIndex] = img[0]; images[Body.BODY][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "okazari", ".png");
		images[Body.ACCESSORY][left][adultIndex] = img[0]; images[Body.ACCESSORY][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "pants", ".png");
		images[Body.PANTS][left][adultIndex] =	img[0]; images[Body.PANTS][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "body_cut", ".png");
		images[Body.BODY_CUT][left][adultIndex] = img[0]; images[Body.BODY_CUT][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "damage0", ".png");
		images[Body.DAMAGED0][left][adultIndex] = img[0]; images[Body.DAMAGED0][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "damage1", ".png");
		images[Body.DAMAGED1][left][adultIndex] = img[0]; images[Body.DAMAGED1][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "damage2", ".png");
		images[Body.DAMAGED2][left][adultIndex] = img[0]; images[Body.DAMAGED2][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "sick0", ".png");
		images[Body.SICK0][left][adultIndex] = img[0]; images[Body.SICK0][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "sick1", ".png");
		images[Body.SICK1][left][adultIndex] = img[0]; images[Body.SICK1][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "sick2", ".png");
		images[Body.SICK2][left][adultIndex] = img[0]; images[Body.SICK2][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "foot_bake_0", ".png");
		images[Body.FOOT_BAKE0][left][adultIndex] = img[0]; images[Body.FOOT_BAKE0][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "foot_bake_1", ".png");
		images[Body.FOOT_BAKE1][left][adultIndex] = img[0]; images[Body.FOOT_BAKE1][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "shit", ".png");
		images[Body.STAIN][left][adultIndex] = img[0]; images[Body.STAIN][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "wet", ".png");
		images[Body.WET][left][adultIndex] = img[0]; images[Body.WET][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "lick", ".png");
		images[Body.LICK][left][adultIndex] =	img[0]; images[Body.LICK][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "braid/braid", ".png");
		images[Body.BRAID][left][adultIndex] = img[0]; images[Body.BRAID][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "braid/braid_cut", ".png");
		images[Body.BRAID_CUT][left][adultIndex] = img[0]; images[Body.BRAID_CUT][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "braid/braid_mv_0", ".png");
		images[Body.BRAID_MV0][left][adultIndex] = img[0]; images[Body.BRAID_MV0][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "braid/braid_mv_1", ".png");
		images[Body.BRAID_MV1][left][adultIndex] = img[0]; images[Body.BRAID_MV1][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "braid/braid_mv_2", ".png");
		images[Body.BRAID_MV2][left][adultIndex] = img[0]; images[Body.BRAID_MV2][right][adultIndex] = img[1];
		/////	
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "burn", ".png");
		images[Body.BURN][left][adultIndex] = img[0]; images[Body.BURN][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "burn01", ".png");
		images[Body.BURN01][left][adultIndex] = img[0]; images[Body.BURN01][right][adultIndex] = img[1];
		
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "bodypinned", ".png");
		images[Body.BODYPINNED][left][adultIndex] = img[0]; images[Body.BODYPINNED][right][adultIndex] = img[1];
	

		
		
		// 鬘�
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/cheer", ".png");
		images[Body.CHEER][left][adultIndex] = img[0]; images[Body.CHEER][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/crying", ".png");
		images[Body.CRYING][left][adultIndex] = img[0]; images[Body.CRYING][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/dead", ".png");
		images[Body.DEAD][left][adultIndex] = img[0]; images[Body.DEAD][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/exciting", ".png");
		images[Body.EXCITING][left][adultIndex] = img[0]; images[Body.EXCITING][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/normal", ".png");
		images[Body.NORMAL][left][adultIndex] = img[0]; images[Body.NORMAL][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/pain", ".png");
		images[Body.PAIN][left][adultIndex] = img[0]; images[Body.PAIN][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/puff", ".png");
		images[Body.PUFF][left][adultIndex] = img[0]; images[Body.PUFF][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/refreshed", ".png");
		images[Body.REFRESHED][left][adultIndex] = img[0]; images[Body.REFRESHED][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/rude", ".png");
		images[Body.RUDE][left][adultIndex] = img[0]; images[Body.RUDE][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/sleeping", ".png");
		images[Body.SLEEPING][left][adultIndex] = img[0]; images[Body.SLEEPING][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/smile", ".png");
		images[Body.SMILE][left][adultIndex] = img[0]; images[Body.SMILE][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/surprise", ".png");
		images[Body.SURPRISE][left][adultIndex] = img[0]; images[Body.SURPRISE][right][adultIndex] = img[1];
		img = loadBodyImage(mt, loader, bodyTheme, bodyName, "faces/tired", ".png");
		images[Body.TIRED][left][adultIndex] = img[0]; images[Body.TIRED][right][adultIndex] = img[1];
		// center
		images[Body.BURNED][left][adultIndex] = 		loadBodyImage(loader, bodyTheme, bodyName, "burned1", ".png");
		images[Body.BURNED2][left][adultIndex] = 		loadBodyImage(loader, bodyTheme, bodyName, "burned2", ".png");
		images[Body.CRUSHED][left][adultIndex] =		loadBodyImage(loader, bodyTheme, bodyName, "crushed", ".png");
		images[Body.CRUSHED2][left][adultIndex] =		loadBodyImage(loader, bodyTheme, bodyName, "crushed2", ".png");
		images[Body.ROLL_ACCESSORY][left][adultIndex] =	loadBodyImage(loader, bodyTheme, bodyName, "okazari", ".png");
		images[Body.FRONT_PANTS][left][adultIndex] =	loadBodyImage(loader, bodyTheme, bodyName, "pants", ".png");
		images[Body.FRONT_SHIT][left][adultIndex] =		loadBodyImage(loader, bodyTheme, bodyName, "shit", ".png");
		images[Body.FRONT_SHIT_SEALED][left][adultIndex] =	loadBodyImage(loader, bodyTheme, bodyName, "shit_sealed", ".png");
		images[Body.ROLL_PANTS][left][adultIndex] =	loadBodyImage(loader, bodyTheme, bodyName, "left_pants", ".png");
		images[Body.ROLL_PANTS][right][adultIndex] =	loadBodyImage(loader, bodyTheme, bodyName, "right_pants", ".png");
		images[Body.ROLL_SHIT][left][adultIndex] =		loadBodyImage(loader, bodyTheme, bodyName, "roll_left", ".png");
		images[Body.ROLL_SHIT][right][adultIndex] =		loadBodyImage(loader, bodyTheme, bodyName, "roll_right", ".png");
		images[Body.ROLL_SHIT_SEALED][left][adultIndex] = loadBodyImage(loader, bodyTheme, bodyName, "roll_left_sealed", ".png");
		images[Body.ROLL_SHIT_SEALED][right][adultIndex] = loadBodyImage(loader, bodyTheme, bodyName, "roll_right_sealed", ".png");
		//////////////////////////////////////////
		//eternity
////////////////////////////////////////////////
		int id = 0;
		for(Image[][] array2d : images) {	
			for(Image[] array : array2d) {
				for(Image image : array) {
					mt.addImage(image, id);
					id++;
				}
			}
		}
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int sx, sy;

		for(Image[][] array2d : images) {	
			for(Image[] array : array2d) {
				if(array[adultIndex] == null) {
					continue;
				}
				sx = (int)((float)array[adultIndex].getWidth(io) * Body.bodySize[1]);
				sy = (int)((float)array[adultIndex].getHeight(io) * Body.bodySize[1]);
				array[childIndex] = array[adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
				sx = (int)((float)array[adultIndex].getWidth(io) * Body.bodySize[0]);
				sy = (int)((float)array[adultIndex].getHeight(io) * Body.bodySize[0]);
				array[babyIndex] =  array[adultIndex].getScaledInstance(sx, sy, Image.SCALE_AREA_AVERAGING);
			}
		}
	}
	
	// 逕ｻ蜒上し繧､繧ｺ險ｭ螳�
	public static void setBoundary(Image[][][] bodyImg, Rectangle[] bodyRect, Dimension[] braidRect, ImageObserver io) {
		for(int i = 0; i < 3; i++) {
			bodyRect[i] = new Rectangle();
			bodyRect[i].width = bodyImg[0][0][i].getWidth(io);
			bodyRect[i].height = bodyImg[0][0][i].getHeight(io);
			bodyRect[i].x = bodyRect[i].width >> 1;
			bodyRect[i].y = bodyRect[i].height - 1;

			if(bodyImg[Body.BRAID][0][i] != null) {
				braidRect[i] = new Dimension(bodyImg[Body.BRAID][0][i].getWidth(io), bodyImg[Body.BRAID][0][i].getHeight(io));
			} else if(bodyImg[Body.BRAID][1][i] != null) {
				braidRect[i] = new Dimension(bodyImg[Body.BRAID][1][i].getWidth(io), bodyImg[Body.BRAID][1][i].getHeight(io));
			}
		}
	}

	// 繝懊ョ繧｣繝代�繝�縺､隱ｭ縺ｿ霎ｼ縺ｿ
	private static Image loadBodyImage(ClassLoader loader, String root, String bodyName, String fileName, String fileExt)
	{
		Image img = null;
		String path = null;

		// MOD謖�ｮ壹↑縺�
		if(root == null) {
			try {
				// jar繝ｪ繧ｽ繝ｼ繧ｹ蜀��繝�ヵ繧ｩ繝ｫ繝医ヵ繧｡繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
				path = MOD_BODY_DIR + "/" + bodyName + "/" + fileName + fileExt;
				img = loadJarImage(loader, path);
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				// 螟夜Κ繝輔ぃ繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
				path = root + File.separator + bodyName + File.separator;
				img = loadModImage(path, fileName + fileExt);
			} catch(IOException e) {
				try {
					// 繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ縺ｫ螟ｱ謨励＠縺溘ｉ繝�ヵ繧ｩ繝ｫ繝医↓蛻�ｊ譖ｿ縺�
					path = MOD_BODY_DIR + "/" + bodyName + "/" + fileName + fileExt;
					img = loadJarImage(loader, path);
				} catch(IOException e2) {
					e.printStackTrace();
				}
			}
		}
		return img;
	}

	// 蟾ｦ蜿ｳ蜿崎ｻ｢縺､縺阪�繝�ぅ繝代�繝�縺､隱ｭ縺ｿ霎ｼ縺ｿ
	// left縺ｪ縺� right縺ｪ縺�縺ｩ縺｡繧峨ｂ隱ｭ縺ｿ霎ｼ縺ｾ縺ｪ縺�
	// left縺ゅｊ, right縺ｪ縺�right縺ｯleft縺ｮ蜿崎ｻ｢縺ｧ逕滓�
	// left繝�Α繝ｼ, right縺ｪ縺�縺ｩ縺｡繧峨ｂ隱ｭ縺ｿ霎ｼ縺ｾ縺ｪ縺�

	// left縺ｪ縺� right縺ゅｊ  right縺ｮ縺ｿ
	// left縺ゅｊ, right縺ゅｊ 荳｡譁ｹ隱ｭ縺ｿ霎ｼ縺ｿ
	// left繝�Α繝ｼ, right縺ゅｊ right縺ｮ縺ｿ

	// left縺ｪ縺� right繝�Α繝ｼ 縺ｩ縺｡繧峨ｂ隱ｭ縺ｿ霎ｼ縺ｾ縺ｪ縺�
	// left縺ゅｊ, right繝�Α繝ｼ left縺ｮ縺ｿ
	// left繝�Α繝ｼ, right繝�Α繝ｼ 縺ｩ縺｡繧峨ｂ隱ｭ縺ｿ霎ｼ縺ｾ縺ｪ縺�
	private static Image[] loadBodyImage(MediaTracker mt, ClassLoader loader, String root, String bodyName, String fileName, String fileExt)
	{
		Image img[] = new Image[2];
		String path = null;
		File file = null;

		img[0] = null;
		img[1] = null;
		if(root == null) {
			// 蟾ｦ蛛ｴ隱ｭ縺ｿ霎ｼ縺ｿ
			// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
			path = MOD_BODY_DIR + "/" + bodyName + "/left/" + fileName;
			if(loader.getResource(DEFAULT_IMG_ROOT_DIR + path + ".txt") == null) {
				try {
					// jar繝ｪ繧ｽ繝ｼ繧ｹ蜀��繝�ヵ繧ｩ繝ｫ繝医ヵ繧｡繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
					img[0] = loadJarImage(loader, path + fileExt);
					mt.addImage(img[0], 0);
					try {
						mt.waitForAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch(IOException e) {
//					e.printStackTrace();
				} catch(IllegalArgumentException e) {
//					e.printStackTrace();
				}
			}
			// 蜿ｳ蛛ｴ隱ｭ縺ｿ霎ｼ縺ｿ
			// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
			path = MOD_BODY_DIR + "/" + bodyName + "/right/" + fileName;
			if(loader.getResource(DEFAULT_IMG_ROOT_DIR + path + ".txt") == null) {
				try {
					// jar繝ｪ繧ｽ繝ｼ繧ｹ蜀��繝�ヵ繧ｩ繝ｫ繝医ヵ繧｡繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
					img[1] = loadJarImage(loader, path + fileExt);
				} catch(IOException e) {
					// 逕ｻ蜒上′辟｡縺丞ｷｦ縺後≠繧後�蟾ｦ蜿ｳ蜿崎ｻ｢
					if(img[0] != null) {
						img[1] = flipImage(img[0]);
					}
				} catch(IllegalArgumentException e) {
					// 逕ｻ蜒上′辟｡縺丞ｷｦ縺後≠繧後�蟾ｦ蜿ｳ蜿崎ｻ｢
					if(img[0] != null) {
						img[1] = flipImage(img[0]);
					}
				}
			}
		} else {
			// 蟾ｦ蛛ｴ
			// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
			file = new File(root + File.separator + bodyName + File.separator + "left" + File.separator + fileName + ".txt");
			if(!file.exists()) {
				try {
					// 螟夜Κ繝輔ぃ繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
					path = root + bodyName + File.separator + "left" + File.separator;
					img[0] = loadModImage(path, fileName + fileExt);
					mt.addImage(img[0], 0);
					try {
						mt.waitForAll();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch(IOException e) {
					// 繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ縺ｫ螟ｱ謨励＠縺溘ｉ繝�ヵ繧ｩ繝ｫ繝医↓蛻�ｊ譖ｿ縺�
					// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
					file = new File(MOD_BODY_DIR + "/" + bodyName + "/left/" + fileName + ".txt");
					if(!file.exists()) {
						try {
							path = MOD_BODY_DIR + "/" + bodyName + "/left/" + fileName + fileExt;
							img[0] = loadJarImage(loader, path);
							mt.addImage(img[0], 0);
							try {
								mt.waitForAll();
							} catch (InterruptedException e2) {
								e2.printStackTrace();
							}
						} catch(IOException e2) {
//							e2.printStackTrace();
						} catch(IllegalArgumentException e2) {
//							e2.printStackTrace();
						}
					}
				}
			}
			// 蜿ｳ蛛ｴ
			// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
			file = new File(root + File.separator + bodyName + File.separator + "right" + File.separator + fileName + ".txt");
			if(!file.exists()) {
				try {
					// 螟夜Κ繝輔ぃ繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
					path = root + bodyName + File.separator + "right" + File.separator;
					img[1] = loadModImage(path, fileName + fileExt);
				} catch(IOException e) {
					// 繝輔ぃ繧､繝ｫ縺ｮ隱ｭ縺ｿ霎ｼ縺ｿ縺ｫ螟ｱ謨励＠縺溘ｉ繝�ヵ繧ｩ繝ｫ繝医↓蛻�ｊ譖ｿ縺�
					// 繝�Α繝ｼ繝輔ぃ繧､繝ｫ縺ｮ蟄伜惠繝√ぉ繝�け
					file = new File(MOD_BODY_DIR + "/" + bodyName + "/right/" + fileName + ".txt");
					if(!file.exists()) {
						try {
							path = MOD_BODY_DIR + "/" + bodyName + "/right/" + fileName + fileExt;
							img[1] = loadJarImage(loader, path);
						} catch(IOException e2) {
							// 逕ｻ蜒上′辟｡縺丞ｷｦ縺後≠繧後�蟾ｦ蜿ｳ蜿崎ｻ｢
							if(img[0] != null) {
								img[1] = flipImage(img[0]);
							}
						} catch(IllegalArgumentException e2) {
							// 逕ｻ蜒上′辟｡縺丞ｷｦ縺後≠繧後�蟾ｦ蜿ｳ蜿崎ｻ｢
							if(img[0] != null) {
								img[1] = flipImage(img[0]);
							}
						}
					}
				}
			}
		}
		return img;
	}

	public static Image flipImage(Image img) {
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

