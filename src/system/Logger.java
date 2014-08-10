package src.system;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import src.Cash;
import src.SimYukkuri;
import src.Terrarium;
import src.yukkuri.*;
import src.yukkuriBody.Body;



public class Logger {
	public static final int TICK = 1;
	public static final int NUM_OF_NORMAL = 0;
	public static final int NUM_OF_PREDATOR = 1;
	public static final int NUM_OF_RARE = 2;
	public static final int NUM_OF_TARINAI = 3;
	public static final int NUM_OF_HYBRID = 4;
	public static final int NUM_OF_BABY = 5;
	public static final int NUM_OF_CHILD = 6;
	public static final int NUM_OF_ADULT = 7;
	public static final int NUM_OF_SICK = 8;
	public static final int NUM_OF_SHIT = 9;
	public static final int NUM_OF_CASH = 10;
	public static final int NUM_OF_LOGDATA_TYPE = 11;

	private static long[] prevLogData = new long[NUM_OF_LOGDATA_TYPE];
	private static long[] logDataSum = new long[NUM_OF_LOGDATA_TYPE];
	private static List<Long> logList = new ArrayList<Long>();

	private static Color backColor = new Color( 0, 0, 0, 160 );
	private static Color textColor1 = new Color( 255, 255, 255, 255 );
	private static Font textFontTitle = new Font( "Dialog", Font.BOLD, 40);
	private static Font textFonttext = new Font( "Dialog", Font.PLAIN, 20);

	public static void run() {
		
		long logData[] = new long[NUM_OF_LOGDATA_TYPE];

		for ( Body b : Terrarium.bodyList) {
			if ( !b.isDead()) {
				if(b.isPredatorType()) {
					// 謐暮｣溽ｨｮ
					logData[NUM_OF_PREDATOR]++;
				} else if(b.isHybrid()) {
					// 繝上う繝悶Μ繝�ラ
					logData[NUM_OF_HYBRID]++;
				} else if(b.isIdiot()) {
					// 雜ｳ繧翫↑縺�
					logData[NUM_OF_TARINAI]++;
				} else if(b.getType() < 100 || b.getType() == Deibu.type
						 || b.getType() == MarisaTsumuri.type || b.getType() == MarisaKotatsumuri.type
						 || b.getType() == WasaReimu.type) {
					// 騾壼ｸｸ遞ｮ
					logData[NUM_OF_NORMAL]++;
				} else if((b.getType() >= 1000 && b.getType() < 2000) || b.getType() == Kimeemaru.type) {
					// 蟶悟ｰ醍ｨｮ
					logData[NUM_OF_RARE]++;
				}

				switch(b.getBodyAgeState()) {
					case BABY:
						logData[NUM_OF_BABY]++;
						break;
					case CHILD:
						logData[NUM_OF_CHILD]++;
						break;
					default:
					case ADULT:
						logData[NUM_OF_ADULT]++;
						break;
				}

				if ( b.isSick() == true ) {
					logData[NUM_OF_SICK]++;
				}
			}
		}

		logData[NUM_OF_SHIT] = Terrarium.shitList.size();

		logData[NUM_OF_CASH] = Cash.getCash();

		for ( int i = 0 ; i < NUM_OF_LOGDATA_TYPE ; i++ ) {
			logList.add( logData[i] );
			logDataSum[i] += logData[i] - prevLogData[i];
			prevLogData[i] = logData[i];
		}
	}
	
	public static int getNumOfLogData(){
		return logList.size();
	}
	
	public static long[] getLog( int logRecord ) {
		if (( logRecord >= 0 ) && ( NUM_OF_LOGDATA_TYPE * logRecord < logList.size() ) && ( logList.size() != 0 ) ){
			long[] logDataReturn = new long[NUM_OF_LOGDATA_TYPE];
			for ( int i = 0 ; i < NUM_OF_LOGDATA_TYPE ; i++ ) {
				logDataReturn[i] = logList.get(NUM_OF_LOGDATA_TYPE * ( logRecord ) + i );
			}
			return logDataReturn;
		}
		return null;
	}

	public static long[] getNumOfObjSumLog(){
		return logDataSum;
	}
	
	public static long[] getNumOfObjNowLog(){
		return prevLogData;
	}

	public static void clearLog(){
		logList.clear();
		run();
	}

	public static void displayLog(Graphics2D g2, int page) {
		g2.setColor( backColor );
		g2.fillRect( 0, 0, Translate.getCanvasW(), Translate.getCanvasH());

		// 遨ｺ縺阪Γ繝｢繝ｪ陦ｨ遉ｺ
		g2.setColor( textColor1 );
		g2.setFont( textFonttext );

		DecimalFormat f1 = new DecimalFormat("#,###KB");
	    DecimalFormat f2 = new DecimalFormat("##.#");
	    long free = Runtime.getRuntime().freeMemory() / 1024;
	    long total = Runtime.getRuntime().totalMemory() / 1024;
	    long max = Runtime.getRuntime().maxMemory() / 1024;
	    long used = total - free;
	    double ratio = (used * 100 / (double)total);
	    String info = "繝｡繝｢繝ｪ菴ｿ逕ｨ驥�" + f1.format(used) + " (" + f2.format(ratio) + "%)縲�" + "菴ｿ逕ｨ蜿ｯ閭ｽ譛�､ｧ蛟､="+f1.format(max);
	    g2.drawString( info, 20, 40 );

	    g2.setColor( textColor1 );
	    g2.setFont( textFontTitle );
		int numOfLogData = getNumOfLogData();
		int NUM_OF_GRAPH_DATA = 120;
		int GRAPH_WIDTH = 700;
		int GRAPH_HEIGHT = 200;
		int GRAPH_OFFSETX = 100;
		int GRAPH_OFFSETY = 300;
		int LEGEND_OFFSETX = 120;
		int LEGEND_OFFSETY = 140;

		long[] logData = new long[NUM_OF_LOGDATA_TYPE * NUM_OF_GRAPH_DATA];
		long[] numOfObjNowLog = getNumOfObjNowLog();
		long numOfMaxYukkuri = 0;
		long numOfMaxUnun = 0;
		long numOfMaxCash = 0;
		long numOfSumYukkuri = 0;
		long[] logDataTmp;
		int operationTime = Terrarium.operationTime / 10;

		for( int i = 0 ; i < NUM_OF_GRAPH_DATA ; i++ ) { //繧ｰ繝ｩ繝輔�譛�､ｧ蛟､繧呈ｱゅａ繧九◆繧∵緒蜀吶☆繧倶ｸ�ｮ壽凾髢薙�繝��繧ｿ蜃ｦ逅�
			logDataTmp = getLog((Terrarium.operationTime - (SimYukkuri.clearLogTime - (SimYukkuri.clearLogTime % 10))) / 10 - NUM_OF_GRAPH_DATA + i);

			if ( logDataTmp != null ) {
				numOfSumYukkuri = 0;
				for(int j = 0; j < NUM_OF_HYBRID + 1; j++) {
					numOfSumYukkuri += logDataTmp[j];
				}
				if(numOfMaxYukkuri < numOfSumYukkuri ) {
					numOfMaxYukkuri = numOfSumYukkuri;
				}
				if(numOfMaxUnun < logDataTmp[NUM_OF_SHIT]) {
					numOfMaxUnun = logDataTmp[NUM_OF_SHIT];
				}
				
				if(numOfMaxCash < logDataTmp[NUM_OF_CASH]) {
					numOfMaxCash = logDataTmp[NUM_OF_CASH];
				}
				for(int j = 0; j < NUM_OF_LOGDATA_TYPE; j++) {
					logData[NUM_OF_LOGDATA_TYPE * i + j] = logDataTmp[j];
				}
			} else {
				for(int j = 0; j < NUM_OF_LOGDATA_TYPE; j++) {
					logData[NUM_OF_LOGDATA_TYPE * i + j] = 0;
				}
			}
		}
		int[] xp = new int[NUM_OF_GRAPH_DATA * 2];
		int[] yp = new int[NUM_OF_GRAPH_DATA * 2];
		for (int i = 0; i < NUM_OF_GRAPH_DATA; i++) {
			xp[i] = GRAPH_WIDTH * i / (NUM_OF_GRAPH_DATA - 1) + GRAPH_OFFSETX;
			xp[NUM_OF_GRAPH_DATA * 2 - i - 1] = GRAPH_WIDTH * i / (NUM_OF_GRAPH_DATA - 1) + GRAPH_OFFSETX;
			yp[i] = GRAPH_HEIGHT + GRAPH_OFFSETY;
			yp[NUM_OF_GRAPH_DATA + i] = GRAPH_HEIGHT + GRAPH_OFFSETY;
		}

		int   opetmp = operationTime % NUM_OF_GRAPH_DATA;

		switch(page) {
			case 0:
				g2.drawString("迴ｾ蝨ｨ縺ｮ繧�▲縺上ｊ縺ｮ蛟倶ｽ捺焚(遞ｮ譌丞挨)", 100, 100);
				g2.setFont(textFonttext);
				for(int i = 0; i < NUM_OF_HYBRID + 1; i++) {
					g2.setColor(Color.WHITE);
					g2.drawString(Long.toString(numOfObjNowLog[i]), LEGEND_OFFSETX + 130, 30 * i + 140);
					switch(i) {
						case NUM_OF_NORMAL:
							g2.drawString( "騾壼ｸｸ遞ｮ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.LIGHT_GRAY );
							break;
						case NUM_OF_PREDATOR:
							g2.drawString( "謐暮｣溽ｨｮ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.RED );
							break;
						case NUM_OF_RARE:
							g2.drawString( "蟶悟ｰ醍ｨｮ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.YELLOW );
							break;
						case NUM_OF_TARINAI:
							g2.drawString( "雜ｳ繧翫↑縺�ｆ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.ORANGE );
							break;
						case NUM_OF_HYBRID:
							g2.drawString( "繝上う繝悶Μ繝�ラ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.MAGENTA );
							break;
					}
					g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130 , 10, 10);
					for(int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						if(logData[NUM_OF_LOGDATA_TYPE * k + i] != 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * GRAPH_HEIGHT / numOfMaxYukkuri;
						} else {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * GRAPH_HEIGHT / 1;
						}
					}
					g2.fillPolygon(xp , yp, NUM_OF_GRAPH_DATA * 2);	
					for(int k = 0; k < NUM_OF_GRAPH_DATA; k++ ) {
						yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
					}
				}
				g2.setColor( Color.WHITE );
				g2.drawRect( GRAPH_OFFSETX , GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
				g2.drawString( Long.toString( numOfMaxYukkuri ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY );
				g2.drawString( Long.toString( numOfMaxYukkuri  / 2 ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY + GRAPH_HEIGHT / 2 );
				g2.drawString( "0", GRAPH_OFFSETX - 12 , GRAPH_OFFSETY + GRAPH_HEIGHT );
				break;
			
			case 1:
				g2.drawString( "迴ｾ蝨ｨ縺ｮ繧�▲縺上ｊ縺ｮ蛟倶ｽ捺焚(蟷ｴ莉｣蛻･)", 100, 100 );
				g2.setFont( textFonttext );
				for ( int i = 0 ; i < 3 ; i++ ){
					g2.setColor( Color.WHITE );
					g2.drawString( Long.toString( numOfObjNowLog[ i + NUM_OF_BABY ] ), LEGEND_OFFSETX + 130, 30 * i + 140);
					switch ( i ){
						case 0:
							g2.drawString( "襍､繧�", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.ORANGE );
							break;
						case 1:
							g2.drawString( "蟄舌ｆ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.YELLOW );
							break;
						case 2:
							g2.drawString( "謌舌ｆ", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.GREEN );
							break;
					}
					g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130 , 10, 10);
					for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
						if ( logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY  ] != 0 ){
							yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY ] * GRAPH_HEIGHT / numOfMaxYukkuri;
						} else {
							yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY ] * GRAPH_HEIGHT / 1;
						}
					}
					g2.fillPolygon( xp , yp, NUM_OF_GRAPH_DATA * 2);
				
					for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
						yp[ NUM_OF_GRAPH_DATA * 2 - k - 1 ] = yp[ k ];
					}
				}
				g2.setColor( Color.WHITE );
				g2.drawRect( GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
				g2.drawString( Long.toString( numOfMaxYukkuri ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY );
				g2.drawString( Long.toString( numOfMaxYukkuri  / 2 ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY + GRAPH_HEIGHT / 2 );
				g2.drawString( "0", GRAPH_OFFSETX - 12 , GRAPH_OFFSETY + GRAPH_HEIGHT );
				break;
		
			case 2:
				g2.drawString( "繧�°縺ｳ諢滓沒迥ｶ豕�", 100, 100 );
				g2.setFont( textFonttext );
				for ( int i = 0 ; i < 2 ; i++ ) {
					g2.setColor( Color.WHITE );
					switch (i) {
						case 0:
							g2.drawString(Long.toString(numOfObjNowLog[NUM_OF_SICK]), LEGEND_OFFSETX + 130, 30 * i + 140);
							g2.drawString( "逋ｺ逞�諢滓沒", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.GREEN );
							break;
						case 1:
							g2.drawString( Long.toString( numOfSumYukkuri - numOfObjNowLog[ NUM_OF_SICK ] ), LEGEND_OFFSETX + 130, 30 * i + 140);
							g2.drawString( "譛ｪ諢滓沒", LEGEND_OFFSETX, 30 * i + 140);
							g2.setColor( Color.LIGHT_GRAY );
							break;
					}
					g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130 , 10, 10);
					for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
						if ( numOfMaxYukkuri == 0 ){
							if ( i == 0 ){
								yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_SICK ] * GRAPH_HEIGHT / 1;
							} else {
								yp[k] -= (logData[ NUM_OF_LOGDATA_TYPE * k ] + logData[ NUM_OF_LOGDATA_TYPE * k + 1 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 2 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 3 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 4 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 5 ] - logData[ NUM_OF_LOGDATA_TYPE * k+ NUM_OF_SICK ]) * GRAPH_HEIGHT / 1;
							}
						}else{
							if ( i == 0 ){
								yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_SICK ] * GRAPH_HEIGHT / numOfMaxYukkuri;
							} else {
								yp[k] -= (logData[ NUM_OF_LOGDATA_TYPE * k ] + logData[ NUM_OF_LOGDATA_TYPE * k + 1 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 2 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 3 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 4 ] + logData[ NUM_OF_LOGDATA_TYPE * k + 5 ] - logData[ NUM_OF_LOGDATA_TYPE * k+ NUM_OF_SICK ]) * GRAPH_HEIGHT / numOfMaxYukkuri;
							}
						}
					}
					g2.fillPolygon( xp , yp, NUM_OF_GRAPH_DATA * 2);	
					for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
						yp[ NUM_OF_GRAPH_DATA * 2 - k - 1 ] = yp[ k ];
					}
				}
				g2.setColor( Color.WHITE );
				g2.drawRect( GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
				g2.drawString( Long.toString( numOfMaxYukkuri ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY );
				g2.drawString( Long.toString( numOfMaxYukkuri  / 2 ), GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2 , GRAPH_OFFSETY + GRAPH_HEIGHT / 2 );
				g2.drawString( "0", GRAPH_OFFSETX - 12 , GRAPH_OFFSETY + GRAPH_HEIGHT );
				break;

			case 3:
				g2.drawString( "縺�ｓ縺�ｓ縺ｮ謨ｰ", 100, 100 );
				g2.setFont( textFonttext );
				g2.drawString( "縺�ｓ縺�ｓ", LEGEND_OFFSETX, 140);
				g2.drawString( Long.toString( numOfObjNowLog[NUM_OF_SHIT] ), LEGEND_OFFSETX + 130, 140);
				g2.setColor( Color.GRAY );
				for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
					if ( logData[ NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT ] != 0 ){
						yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT ] * GRAPH_HEIGHT / numOfMaxUnun;
					} else {
						yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT ] * GRAPH_HEIGHT / 1;
					}
				}
				g2.fillRect( LEGEND_OFFSETX + 180, 130 , 10, 10);
				g2.fillPolygon( xp , yp, NUM_OF_GRAPH_DATA * 2);	
				for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
					yp[ NUM_OF_GRAPH_DATA * 2 - k - 1 ] = yp[ k ];
				}
				g2.setColor( Color.WHITE );
				g2.drawRect( GRAPH_OFFSETX , GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
				g2.drawString( Long.toString( numOfMaxUnun ), GRAPH_OFFSETX - String.valueOf(numOfMaxUnun).length() * 10 - 2 , GRAPH_OFFSETY );
				g2.drawString( Long.toString( numOfMaxUnun  / 2 ), GRAPH_OFFSETX - String.valueOf(numOfMaxUnun).length() * 10 - 2 , GRAPH_OFFSETY + GRAPH_HEIGHT / 2 );
				g2.drawString( "0", GRAPH_OFFSETX - 12 , GRAPH_OFFSETY + GRAPH_HEIGHT );
				break;
			case 4:
				g2.drawString( "蜿主��･謾ｯ蜃ｺ", 100, 100 );
				g2.setFont( textFonttext );
				for ( int i = 0 ; i < 1 ; i++ ){
					g2.setColor( Color.WHITE );
					g2.drawString( Long.toString( numOfObjNowLog[ i + NUM_OF_CASH ] ), LEGEND_OFFSETX + 130, 30 * i + 140);

					g2.drawString( Long.toString( numOfObjNowLog[ NUM_OF_CASH ] - logData[ NUM_OF_LOGDATA_TYPE * 59 + NUM_OF_CASH ] ), LEGEND_OFFSETX + 130, 30 * (i+1) + 140);
					g2.drawString( "謇�戟驥�", LEGEND_OFFSETX, 30 * i + 140);
					g2.drawString( "蜿守寢(�･/蛻�", LEGEND_OFFSETX, 30 * (i+1) + 140);
					g2.setColor( Color.YELLOW );				
					if ( i == 0 ){
						for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
							if ( logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH  ] > 0 ){
								yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH ] * GRAPH_HEIGHT / numOfMaxCash;
							} else if ( logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH  ] == 0 ) {
								yp[k] -= logData[ NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH ] * GRAPH_HEIGHT / 1;
							}
						}
						g2.fillPolygon( xp , yp, NUM_OF_GRAPH_DATA * 2);	
					}
				
					for( int k = 0 ; k < NUM_OF_GRAPH_DATA ; k++ ){
						yp[ NUM_OF_GRAPH_DATA * 2 - k - 1 ] = yp[ k ];
					}
					g2.setColor( Color.WHITE );
					g2.drawRect( GRAPH_OFFSETX , GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
					g2.drawString( Long.toString( numOfMaxCash ), GRAPH_OFFSETX - String.valueOf(numOfMaxCash).length() * 10 - 2 , GRAPH_OFFSETY );
					g2.drawString( Long.toString( numOfMaxCash  / 2 ), GRAPH_OFFSETX - String.valueOf(numOfMaxCash).length() * 10 - 2 , GRAPH_OFFSETY + GRAPH_HEIGHT / 2 );
					g2.drawString( "0", GRAPH_OFFSETX - 12 , GRAPH_OFFSETY + GRAPH_HEIGHT );
				}
				break;
			default:
				break;
		}
		for ( int i = 0 ; i < NUM_OF_GRAPH_DATA / 30 + 1 ; i++ ){
			int graphx = GRAPH_WIDTH - ( (( GRAPH_WIDTH ) * 30 * i ) / NUM_OF_GRAPH_DATA ) - ( ( opetmp % 30 ) * GRAPH_WIDTH / NUM_OF_GRAPH_DATA ) + GRAPH_OFFSETX;
			if ( GRAPH_WIDTH - ( GRAPH_WIDTH * 30 * i / NUM_OF_GRAPH_DATA + opetmp ) >= 0 && operationTime - 30 * i >= 0 ){
				g2.drawLine(  graphx, GRAPH_OFFSETY, graphx, GRAPH_OFFSETY + GRAPH_HEIGHT);
				g2.drawString(( ( Integer.toString(( operationTime - 30 * i ) / 3600 )) + ":" + ( Integer.toString(( operationTime - 30 * i ) / 60 % 60 )) + ":" + Integer.toString(( operationTime + 30 * i ) % 60 / 30 * 30 )), graphx - String.valueOf( ( ( Integer.toString(( operationTime - 30 * i ) / 3600 )) + ":" + ( Integer.toString(( operationTime - 30 * i ) / 60 % 60 )) + ":" + Integer.toString(( operationTime + 30 * i ) % 60 / 30 * 30 )) ).length() * 5 - 4  , GRAPH_OFFSETY + GRAPH_HEIGHT + 18 );
			}
		}
	}
}
