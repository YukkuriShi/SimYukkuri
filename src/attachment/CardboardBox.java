package src.attachment;


import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.object.Attachment;
import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.*;


/****************************************
 *  繧ｪ繝ｬ繝ｳ繧ｸ繧｢繝ｳ繝励Ν
 * 
 */
public class CardboardBox extends Attachment {

	private static final long serialVersionUID = 1L;

	private static Image[][] images;		// [蟷ｴ鮨｢][蟾ｦ蜿ｳ蜿崎ｻ｢]
	private static int[] imgW;
	private static int[] imgH;
	private static int[] pivX;
	private static int[] pivY;
	private static final int[] property = {
		1,		// 襍､繧�畑逕ｻ蜒上し繧､繧ｺ 蜴溽判繧偵％縺ｮ蛟､縺ｧ蜑ｲ繧�
		1,		// 蟄舌ｆ逕ｨ逕ｻ蜒上し繧､繧ｺ
		1,		// 謌舌ｆ逕ｨ逕ｻ蜒上し繧､繧ｺ
		0,	// 繧｢繧ｿ繝�メ繝｡繝ｳ繝亥叙繧贋ｻ倥￠蠎ｧ讓�
		0,
		1,		// 隕ｪ繧ｪ繝悶ず繧ｧ繧ｯ繝医�菴咲ｽｮ蝓ｺ貅�0:鬘斐�縺企｣ｾ繧雁髄縺代�蜈�し繧､繧ｺ 1:螯雁ｨ�↑縺ｩ縺ｮ閹ｨ繧峨∩繧ょ性繧�し繧､繧ｺ
		0,		// 繧｢繝九Γ騾溷ｺｦ
		0,		// 繧｢繝九Γ繝ｫ繝ｼ繝怜屓謨ｰ
		1		// 繧｢繝九Γ逕ｻ蜒乗椢謨ｰ
	};
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new Image[3][2];
		
		images[adult][0] = ModLoader.loadItemImage(loader, "general/cardboardBox.png");

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		images[child][0] = images[adult][0].getScaledInstance(w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
		images[baby][0] = images[adult][0].getScaledInstance(w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()], Image.SCALE_AREA_AVERAGING);
			
		images[adult][1] = ModLoader.flipImage(images[adult][0]);
		images[child][1] = ModLoader.flipImage(images[child][0]);
		images[baby][1] = ModLoader.flipImage(images[baby][0]);

		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	public int getOfsX() {
		return (parent.getW() >> 1);// + (int)(parent.getW() * 0.3f);
	}

	public int getOfsY() {
		return (int)(parent.getH());// * 0.5f);
	}
	
	protected Event update() {
		if (parent.isDead() && !parent.isCrashed() && !parent.isBurned() ){
			parent.revival();
		}
		parent.addDamage(-TICK * 200);
		return Event.DONOTHING;
	}

	public Image getImage(Body b) {
		if(b.getDirection() == Direction.RIGHT) {
			return images[parent.getAgeState().ordinal()][1];
		}
		return images[parent.getAgeState().ordinal()][0];
	}

	public CardboardBox(Body body) {
		super(body);
		setAttachProperty(property);
		setBoundary(pivX[parent.getAgeState().ordinal()],
					pivY[parent.getAgeState().ordinal()],
					imgW[parent.getAgeState().ordinal()],
					imgH[parent.getAgeState().ordinal()]);
		value = 5000;
		cost = 0;
	}
}
