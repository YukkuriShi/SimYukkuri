package src.system;


import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.SimYukkuri;

public class JarGuard {
	private static boolean authenticationResult = false;
	private static Image blockPlayImage = null;
	private static Image errorIconImage = null;
	private static int error_num = 0;
	public static void authentication(){
		try {
			authenticationResult = false;
			error_num = 0;
			blockPlayImage = ImageIO.read(SimYukkuri.mypane.getImageLoader().getResourceAsStream("images/etc/blockPlay.bmp"));
			errorIconImage = ImageIO.read(SimYukkuri.mypane.getImageLoader().getResourceAsStream("images/etc/error_icon.png"));
			/*
			File file = new File("remirya/left/cap.png");
			if (!file.exists()){
				error_num++;
			}else{
				file = new File("patch/left/cap.png");
				if (!file.exists()){
				error_num++;
				}
			}

			file = new File("1_3Image/marisa3-2.png");
			if (!file.exists()){
				error_num++;
			}else{
				file = new File("1_3Image/marisa11.png");
				if (!file.exists()){
					error_num++;
				}else{
					file = new File("1_3Image/reimu3-2.png");
					if (!file.exists()){
						error_num++;
					}else{
						file = new File("1_3Image/reimu11.png");
						if (!file.exists()){
							error_num++;
						}
					}
				}
			}
			
			file = new File("renko.txt");
			if (!file.exists()){
				error_num++;
			}*/
			
			if ( error_num == 0 ){
				authenticationResult = true;
			}
		} catch (IOException e1) {
			error_num++;
			authenticationResult = false;
		}
	}
	public static int getAuthenticationResult(){
		if ( authenticationResult = true ) {
			//return error_num;
			return 0;
		}else{
			//return -1;
			return 0;
		}
	}
	public static Image getBlockPlayImage(){
		return blockPlayImage;
	}
	
	public static Image getErrorIconImage(){
		return errorIconImage;
	}
}