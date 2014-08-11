package src;

import java.awt.Rectangle;
import java.io.*;
import java.util.*;

import src.YukkuriUtil.YukkuriType;
import src.effect.*;
import src.item.*;
import src.object.Effect;
import src.object.Obj;
import src.object.ObjEX;
import src.object.Shit;
import src.object.Stalk;
import src.object.Vomit;
import src.system.Numbering;
import src.system.Translate;
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
import src.yukkuriBody.BodyLogic;
import src.yukkuriBody.ConstantValues.*;
import src.yukkuriLogic.BedLogic;
import src.yukkuriLogic.EventLogic;
import src.yukkuriLogic.FoodLogic;
import src.yukkuriLogic.ToiletLogic;
import src.yukkuriLogic.ToyLogic;


public class Terrarium {
	public static final int MAP_BABY = 1;
	public static final int MAP_CHILD = 2;
	public static final int MAP_ADULT = 4;
	public static final int MAP_WATER = 8;
	public static final int[] MAP_BODY = {MAP_BABY, MAP_CHILD, MAP_ADULT};

	public static final int BARRIER_GAP_MINI = MAP_BABY;
	public static final int BARRIER_GAP_BIG = MAP_BABY + MAP_CHILD;
	public static final int BARRIER_NET_MINI = MAP_CHILD + MAP_ADULT;
	public static final int BARRIER_NET_BIG = MAP_ADULT;
	public static final int BARRIER_WALL = MAP_BABY + MAP_CHILD + MAP_ADULT;
	public static final int BARRIER_WATER = MAP_WATER;

	public static ArrayList<Body> bodyList = new ArrayList<Body>();
	public static ArrayList<Shit> shitList = new ArrayList<Shit>();
	public static ArrayList<Vomit> vomitList = new ArrayList<Vomit>();
	public static ArrayList<Barrier> barrierList = new ArrayList<Barrier>();

	public static ArrayList<EventPacket> eventList = new ArrayList<EventPacket>();

	public static ArrayList<Effect> sortLightEffectList = new ArrayList<Effect>();
	public static ArrayList<Effect> frontLightEffectList = new ArrayList<Effect>();

	public static int windowType;
	public static int terrariumSizeIndex;
	public static int terrariumSizeParcent;
	public static int MAX_X;
	public static int MAX_Y;
	public static int MAX_Z;
	public static int operationTime = 0;
	public static final int dayTime = 100*24 *2/3;
	public static final int nightTime = 100*24 - dayTime;
	public static enum DayState { MORNING,DAY,EVENING,NIGHT };
	public static enum WeatherState { SNOWING, NORMAL, RAINING, HEATWAVE};
	public static enum SeasonState { SPRING, SUMMER, FALL, WINTER};
	
	public static int daycount = 0;
	public static int seasoncount = 0;
	public static int dayTimer = 1200;
	////climate
	public static boolean isSnowing = false;
	public static boolean isHeatwave = false;

	public static boolean humid = false;
	public static boolean antifungalSteam = false;
	public static boolean orangeSteam = false;
	public static boolean ageBoostSteam = false;
	public static boolean ageStopSteam = false;
	public static boolean antidosSteam = false;
	public static boolean poisonSteam = false;

	public static final int TICK = 1;
	
	private static int wallMap[][] = new int[MAX_X+1][MAX_Y+1];
	private static int floorMap[][] = new int[MAX_X+1][MAX_Y+1];
	private static Random rnd = new Random();
	private static int alarmPeriod = 0;
	private static boolean alarm = false;
	private static ArrayList<Body> babyList = new ArrayList<Body>();
	private final static int ALARM_PERIOD = 300; // 30 seconds
	private static int intervalCount = 0;
	private static Rectangle tmpRect = new Rectangle();
	
	public static void saveState(File file) throws IOException {
		ObjectOutputStream out =
				new ObjectOutputStream(
						new BufferedOutputStream(
								new FileOutputStream(file)));
		try {
			out.writeUTF(Terrarium.class.getCanonicalName());
			out.writeLong(Cash.getCash());
			out.writeInt(windowType);
			out.writeInt(terrariumSizeIndex);
			out.writeInt(alarmPeriod);
			out.writeBoolean(alarm);
			out.writeObject(rnd);
			out.writeObject(bodyList);
			out.writeObject(shitList);
			out.writeObject(vomitList);
			out.writeObject(barrierList);

			out.writeObject(Food.objEXList);
			out.writeObject(Toilet.objEXList);
			out.writeObject(Bed.objEXList);
			out.writeObject(Toy.objEXList);
			out.writeObject(Beltconveyor.objEXList);
			out.writeObject(BreedingPool.objEXList);
			out.writeObject(GarbageChute.objEXList);
			out.writeObject(FoodMaker.objEXList);
			out.writeObject(OrangePool.objEXList);
			out.writeObject(ProductChute.objEXList);
			out.writeObject(StickyPlate.objEXList);
			out.writeObject(HotPlate.objEXList);
			out.writeObject(Mixer.objEXList);
			out.writeObject(AutoFeeder.objEXList);
			out.writeObject(MachinePress.objEXList);
			out.writeObject(Stalk.objEXList);
			out.writeObject(Diffuser.objEXList);
			out.writeObject(Yunba.objEXList);
			out.writeObject(Sui.objEXList);
			out.writeObject(Trash.objEXList);

			out.writeObject(wallMap);
			out.writeObject(floorMap);

			out.writeObject(Numbering.INSTANCE.getYukkuriID());

			out.writeObject(eventList);

			out.flush();
		} finally {
			out.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadState(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream in =
				new ObjectInputStream(
						new BufferedInputStream(
								new FileInputStream(file)));
		int alarmPeriod;
		boolean alarm;
		Random rnd;
		ArrayList<Body> bodyList;
		ArrayList<Shit> shitList;
		ArrayList<Vomit> vomitList;
		ArrayList<Barrier> barrierList;

		ArrayList<ObjEX> foodList;
		ArrayList<ObjEX> toiletList;
		ArrayList<ObjEX> bedList;
		ArrayList<ObjEX> toyList;
		ArrayList<ObjEX> beltconveyorList;
		ArrayList<ObjEX> breedingPoolList;
		ArrayList<ObjEX> garbageChuteList;
		ArrayList<ObjEX> foodMakerList;
		ArrayList<ObjEX> orangePoolList;
		ArrayList<ObjEX> productChuteList;
		ArrayList<ObjEX> stickyPlateList;
		ArrayList<ObjEX> hotPlateList;
		ArrayList<ObjEX> mixerList;
		ArrayList<ObjEX> feederList;
		ArrayList<ObjEX> machinePressList;
		ArrayList<ObjEX> stalkList;
		ArrayList<ObjEX> diffuserList;
		ArrayList<ObjEX> yunbaList;
		ArrayList<ObjEX> suiList;
		ArrayList<ObjEX> trashList;

		ArrayList<EventPacket> eventList;

		int map1[][];
		int map2[][];
		try {
			String s = in.readUTF();
			if(!Terrarium.class.getCanonicalName().equals(s)) {
				String errMsg = "Bad save: "+s;
				throw new IOException(errMsg);
			}
			Cash.setCash(in.readLong());
			Terrarium.windowType = in.readInt();
			Terrarium.terrariumSizeIndex = in.readInt();
			alarmPeriod = in.readInt();
			alarm = in.readBoolean();
			rnd = (Random)in.readObject();
			bodyList = (ArrayList<Body>)in.readObject();
			shitList = (ArrayList<Shit>)in.readObject();
			vomitList = (ArrayList<Vomit>)in.readObject();
			barrierList = (ArrayList<Barrier>)in.readObject();

			foodList = (ArrayList<ObjEX>)in.readObject();
			toiletList = (ArrayList<ObjEX>)in.readObject();
			bedList = (ArrayList<ObjEX>)in.readObject();
			toyList = (ArrayList<ObjEX>)in.readObject();
			beltconveyorList = (ArrayList<ObjEX>)in.readObject();
			breedingPoolList = (ArrayList<ObjEX>)in.readObject();
			garbageChuteList = (ArrayList<ObjEX>)in.readObject();
			foodMakerList = (ArrayList<ObjEX>)in.readObject();
			orangePoolList = (ArrayList<ObjEX>)in.readObject();
			productChuteList = (ArrayList<ObjEX>)in.readObject();
			stickyPlateList = (ArrayList<ObjEX>)in.readObject();
			hotPlateList = (ArrayList<ObjEX>)in.readObject();
			mixerList = (ArrayList<ObjEX>)in.readObject();
			feederList = (ArrayList<ObjEX>)in.readObject();
			machinePressList = (ArrayList<ObjEX>)in.readObject();
			stalkList = (ArrayList<ObjEX>)in.readObject();
			diffuserList = (ArrayList<ObjEX>)in.readObject();
			yunbaList = (ArrayList<ObjEX>)in.readObject();
			suiList = (ArrayList<ObjEX>)in.readObject();
			trashList = (ArrayList<ObjEX>)in.readObject();

			map1 = (int [][])in.readObject();
			map2 = (int [][])in.readObject();

			Numbering.INSTANCE.setYukkuriID(((Integer)in.readObject()).intValue());

			eventList = (ArrayList<EventPacket>)in.readObject();
		} finally {
			in.close();
		}
		Terrarium.changeTerrariumSize(SimYukkuri.DEFAULT_MAP_X[Terrarium.windowType],
										SimYukkuri.DEFAULT_MAP_Y[Terrarium.windowType],
										SimYukkuri.DEFAULT_MAP_Z[Terrarium.windowType],
										SimYukkuri.fieldScaleData[Terrarium.terrariumSizeIndex]);
		Terrarium.rnd = rnd;
		Terrarium.alarmPeriod = alarmPeriod;
		Terrarium.alarm = alarm;
		Terrarium.bodyList = bodyList;
		Terrarium.shitList = shitList;
		Terrarium.vomitList = vomitList;
		Terrarium.barrierList = barrierList;

		Food.objEXList = foodList;
		Toilet.objEXList = toiletList;
		Bed.objEXList = bedList;
		Toy.objEXList = toyList;
		Beltconveyor.objEXList = beltconveyorList;
		BreedingPool.objEXList = breedingPoolList;
		GarbageChute.objEXList = garbageChuteList;
		FoodMaker.objEXList		= foodMakerList;
		OrangePool.objEXList	= orangePoolList;
		ProductChute.objEXList	= productChuteList;
		StickyPlate.objEXList	= stickyPlateList;
		HotPlate.objEXList		= hotPlateList;
		Mixer.objEXList			= mixerList;
		AutoFeeder.objEXList	= feederList;
		MachinePress.objEXList	= machinePressList;
		Stalk.objEXList	= stalkList;
		Diffuser.objEXList	= diffuserList;
		Yunba.objEXList	= yunbaList;
		Sui.objEXList = suiList;
		Trash.objEXList = trashList;

		Terrarium.wallMap = map1;
		Terrarium.floorMap = map2;
		
		Terrarium.eventList = eventList;

		if ( Terrarium.windowType != 2 ){
			SimYukkuri.simYukkuri.setWindowMode(Terrarium.windowType, Terrarium.terrariumSizeIndex);
		}else{
			SimYukkuri.simYukkuri.setFullScreenMode(Terrarium.terrariumSizeIndex);
		}
//		SimYukkuri.mypane.fitScaleBackground(Translate.getCanvasW(), Translate.getCanvasH());
		SimYukkuri.mypane.createBackBuffer();
		Translate.createTransTable();
		
		for(Body b :bodyList) {
			if(b.getType() == HybridYukkuri.type) {
				HybridYukkuri hb = (HybridYukkuri)b;
				SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(0).getClass().getSimpleName()));
				SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(1).getClass().getSimpleName()));
				SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(2).getClass().getSimpleName()));
				SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(hb.getBaseBody(3).getClass().getSimpleName()));
			} else {
				SimYukkuri.mypane.loadBodyImage(YukkuriUtil.getYukkuriType(b.getClass().getSimpleName()));
			}
		}
		System.gc();
	}

	
	public static List<Obj> getObjList(){
		List <Obj>objList = new ArrayList<Obj>();
		objList.addAll(Terrarium.bodyList);
		objList.addAll(Terrarium.shitList);
		objList.addAll(Terrarium.vomitList);
		return objList;
	 }
	

	public static List<ObjEX> getPlatformList(){
		List <ObjEX>objList = new ArrayList<ObjEX>();
		objList.addAll(Toilet.objEXList);
		objList.addAll(Bed.objEXList);
		objList.addAll(Beltconveyor.objEXList);
		objList.addAll(BreedingPool.objEXList);
		objList.addAll(GarbageChute.objEXList);
		objList.addAll(FoodMaker.objEXList);
		objList.addAll(OrangePool.objEXList);
		objList.addAll(ProductChute.objEXList);
		objList.addAll(StickyPlate.objEXList);
		objList.addAll(HotPlate.objEXList);
		objList.addAll(Mixer.objEXList);
		objList.addAll(AutoFeeder.objEXList);
		return objList;
	}
	
	public static List<ObjEX> getFixObjList(){
		List <ObjEX>objList = new ArrayList<ObjEX>();
		objList.addAll(MachinePress.objEXList);
		return objList;
	}
	
	public static List<ObjEX> getObjectList(){
		List <ObjEX>objList = new ArrayList<ObjEX>();
		objList.addAll(Food.objEXList);
		objList.addAll(Toy.objEXList);
		objList.addAll(Stalk.objEXList);
		objList.addAll(Diffuser.objEXList);
		objList.addAll(Yunba.objEXList);
		objList.addAll(Sui.objEXList);
		objList.addAll(Trash.objEXList);
		return objList;
	}

	private static void setLine(int x1, int y1, int x2, int y2, boolean setFlag, int attribute) {
		int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double)(x2 - x1)/(double)distance;
		double deltaY = (double)(y2 - y1)/(double)distance;
		int sX = x1;
		int sY = y1;
		for (int t = 0; t <= distance; t++) {
			int x = sX + (int)(deltaX * t);
			int y = sY + (int)(deltaY * t);
			setPoint(x, y, setFlag, attribute);
			setPoint(Math.min(x+1, MAX_X), y, setFlag, attribute);
			setPoint(x, Math.min(y+1, MAX_Y), setFlag, attribute);
		}		
	}

	private static void setPoint(int x, int y, boolean setFlag, int attribute) {
		if (setFlag) {
			wallMap[x][y] = wallMap[x][y] | attribute;
		} else {
			wallMap[x][y] = wallMap[x][y] & (~attribute);
//			map[x][y]--;
//			if (map[x][y] < 0) {
//				map[x][y] = 0;
//			}
		}
	}

	public static void setBarrier(int x1, int y1, int x2, int y2, int type) {
		x1 = Math.max(0, Math.min(x1, MAX_X));
		x2 = Math.max(0, Math.min(x2, MAX_X));
		y1 = Math.max(0, Math.min(y1, MAX_Y));
		y2 = Math.max(0, Math.min(y2, MAX_Y));
		setLine(x1, y1, x2, y2, true, type);
		barrierList.add(new Barrier(x1, y1, x2, y2, type));
	}
	
	public static void clearBarrier(Barrier b) {
		int x1 = b.getSX();
		int y1 = b.getSY();
		int x2 = b.getEX();
		int y2 = b.getEY();
		setLine(x1, y1, x2, y2, false, b.getAttribute());
		barrierList.remove(b);
	}
	
	public static ArrayList<Barrier> getBarriers() {
		return barrierList;
	}
	
	public static boolean onBarrier(int cx, int cy, int thx, int thy, int attr) {
		int sx = Math.max(0, cx - thx/2);
		int sy = Math.max(0, cy - thy/2);
		int ex = Math.min(cx + thx/2, MAX_X);
		int ey = Math.min(cy + thy/2, MAX_Y);
		for (int x = sx; x < ex; x++) {
			for (int y = sy; y < ey; y++) {
				if ((wallMap[x][y] & attr) != 0) {
					return true;
				}
			}
		}
		return false;		
	}

//	public static boolean onBarrier(int cx, int cy, int thickness, int attr) {
//		return onBarrier(cx, cy, thickness, thickness, attr);
//	}
	
	public static Barrier getBarrier(int cx, int cy, int thickness) {
		for (Barrier b: barrierList) {
			int x1 = b.getSX();
			int y1 = b.getSY();
			int x2 = b.getEX();
			int y2 = b.getEY();
			int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
			double deltaX = (double)(x2 - x1)/(double)distance;
			double deltaY = (double)(y2 - y1)/(double)distance;
			int sX = x1;
			int sY = y1;
			for (int t = 0; t <= distance; t++) {
				int x = sX + (int)(deltaX * t);
				int y = sY + (int)(deltaY * t);
				if ((Math.abs(x - cx) <= thickness) && (Math.abs(y - cy) <= thickness)) {
					return b;
				}
			}
		}
		return null;
	}

	public static boolean acrossBarrier(int x1, int y1, int x2, int y2, int attr) {
		x1 = Math.max(0, Math.min(x1, MAX_X));
		x2 = Math.max(0, Math.min(x2, MAX_X));
		y1 = Math.max(0, Math.min(y1, MAX_Y));
		y2 = Math.max(0, Math.min(y2, MAX_Y));
		int distance = (int)Math.sqrt(Translate.distance(x1, y1, x2, y2));
		double deltaX = (double)(x2 - x1)/(double)distance;
		double deltaY = (double)(y2 - y1)/(double)distance;
		int sX = x1;
		int sY = y1;
		for (int t = 0; t <= distance; t++) {
			int x = sX + (int)(deltaX * t);
			int y = sY + (int)(deltaY * t);
			if ((wallMap[x][y] & attr) != 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void clearMap() {
		for(int x = 0; x < wallMap.length; x++) {
			for(int y = 0; y < wallMap[x].length; y++) {
				wallMap[x][y] = 0;
			}
		}
	}

	// 繝代ル繝�け譎ゅ�謖吝虚
	private void checkPanic(Body b) {
		if (b.isDead()) {
			return;
		}
		int minDistance;

		// 蜈ｨ繧�▲縺上ｊ縺ｫ蟇ｾ縺励※繝√ぉ繝�け
		for (Body p:bodyList) {
			// 閾ｪ蛻�酔螢ｫ縺ｮ繝√ぉ繝�け縺ｯ辟｡諢丞袖縺ｪ縺ｮ縺ｧ繧ｹ繧ｭ繝��
			if (p == b) {
				continue;
			}
			// 逶ｸ謇九→縺ｮ髢薙↓螢√′縺ゅｌ縺ｰ繧ｹ繧ｭ繝��
			if (acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), MAP_BODY[b.getAgeState().ordinal()])) {
				continue;
			}
			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());

			// 逶ｸ謇九′螳吶↓豬ｮ縺�※縺溘ｉ辟｡隕�
			if (p.getZ() != 0) {
				continue;
			}
				
			// 繝代ル繝�け縺ｮ莨晄眺
			if (minDistance <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
				// 謗･隗ｦ迥ｶ諷九〒閾ｪ蛻�′辯�∴縺ｦ縺�◆繧蛾｣帙�轣ｫ
				if(b.getPanicType() == PanicType.BURN) {
					p.giveFire();
				}
			} else {
				// 諱先�蜷悟｣ｫ縺ｧ莨晄眺縺ｮ辟｡髯舌Ν繝ｼ繝励↓蜈･繧峨↑縺�ｈ縺�↓蛻ｶ髯�
				if(b.getPanicType() == PanicType.BURN) {
					p.setPanic(true, PanicType.FEAR);
				}
			}
		}
	}

	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size()-1).kick(0,5,-2);
		
	}
	
	private void addBaby(int x, int y, int z, Dna dna, Body p1, Body p2, Stalk stalk) {
		babyList.add(makeBody(x, y, z, dna, AgeState.BABY, p1, p2));
		Body b = babyList.get( babyList.size()-1 );
		stalk.setBindBaby( b );
		b.setBindStalk( stalk );
		b.setUnBirth( true );
	}
	
	private void addBaby(int x, int y, int z, int vx, int vy, int vz, Dna dna, Body p1, Body p2) {
		babyList.add(makeBody(x, y, z + 1, dna, AgeState.BABY, p1, p2));
		babyList.get(babyList.size()-1).kick(vx,vy,vz);
		
	}

	public Body makeBody(int x, int y, int z, Dna dna, AgeState age, Body p1, Body p2) {
		return makeBody(x, y, z, dna.type, dna, age, p1, p2);
	}

	public Body makeBody(int x, int y, int z, int type, Dna dna, AgeState age, Body p1, Body p2) {
		Body b;
		switch (type) {
		case Marisa.type:
			b = new Marisa(x, y, z, age, p1, p2);
			break;
		case Reimu.type:
			b = new Reimu(x, y, z, age, p1, p2);
			break;
		case Alice.type:
			b = new Alice(x, y, z, age, p1, p2);
			break;
		case Patch.type:
			b = new Patch(x, y, z, age, p1, p2);
			break;
		case Chen.type:
			b = new Chen(x, y, z, age, p1, p2);
			break;
		case Myon.type:
			b = new Myon(x, y, z, age, p1, p2);
			break;
		case WasaReimu.type:
			b = new WasaReimu(x, y ,z, age, p1, p2);
			break;
		case MarisaTsumuri.type:
			b = new MarisaTsumuri(x, y ,z, age, p1, p2);
			break;
		case MarisaKotatsumuri.type:
			b = new MarisaKotatsumuri(x, y ,z, age, p1, p2);
			break;
		case Deibu.type:
			b = new Deibu(x, y ,z, age, p1, p2);
			break;
		case DosMarisa.type:
			b = new DosMarisa(x, y ,z, age, p1, p2);
			break;
		case Tarinai.type:
			b = new Tarinai(x, y, z, age, p1, p2);
			break;
		case MarisaReimu.type:
			b = new MarisaReimu(x, y, z, age, p1, p2);
			break;
		case ReimuMarisa.type:
			b = new ReimuMarisa(x, y, z, age, p1, p2);
			break;
		case HybridYukkuri.type:
			b = new HybridYukkuri(x, y, z, age, p1, p2);
			break;
		case Remirya.type:
			b = new Remirya(x, y, z, age, p1, p2);
			break;
		case Fran.type:
			b = new Fran(x, y, z, age, p1, p2);
			break;
		case Ayaya.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.AYAYA);
			b = new Ayaya(x, y, z, age, p1, p2);
			break;
		case Chiruno.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.CHIRUNO);
			b = new Chiruno(x, y, z, age, p1, p2);
			break;
		case Eiki.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.EIKI);
			b = new Eiki(x, y, z, age, p1, p2);
			break;
		case Kimeemaru.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.KIMEEMARU);
			b = new Kimeemaru(x, y, z, age, p1, p2);
			break;
		case Meirin.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.MEIRIN);
			b = new Meirin(x, y, z, age, p1, p2);
			break;
		case Nitori.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.NITORI);
			b = new Nitori(x, y, z, age, p1, p2);
			break;
		case Ran.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.RAN);
			b = new Ran(x, y, z, age, p1, p2);
			break;
		case Suwako.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.SUWAKO);
			b = new Suwako(x, y, z, age, p1, p2);
			break;
		case Tenko.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.TENKO);
			b = new Tenko(x, y, z, age, p1, p2);
			break;
		case Udonge.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.UDONGE);
			b = new Udonge(x, y, z, age, p1, p2);
			break;
		case Yurusanae.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YURUSANAE);
			b = new Yurusanae(x, y, z, age, p1, p2);
			break;
		case Yuyuko.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YUYUKO);
			b = new Yuyuko(x, y, z, age, p1, p2);
			break;
		case Yuuka.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.YUUKA);
			b = new Yuuka(x, y, z, age, p1, p2);
			break;
		case Sakuya.type:
			SimYukkuri.mypane.loadBodyImage(YukkuriType.SAKUYA);
			b = new Sakuya(x, y, z, age, p1, p2);
			break;
		default:
			throw new RuntimeException("Unknown yukkuri type.");
		}
		
		// DNA諠��縺梧ｸ｡縺輔ｌ縺ｦ縺溘ｉ繧ｹ繝��繧ｿ繧ｹ荳頑嶌縺�
		if(dna != null) {
			if(dna.attitude != null) b.setAttitude(dna.attitude);
			if(dna.intelligence != null) b.setIntelligence(dna.intelligence);
		}
		
		// 逕溘＞遶九■縺ｮ險ｭ螳�p1=mama p2=papa
		
		return b;
	}

	public Body addBody(int x, int y, int z, int type, AgeState age, Body p1, Body p2) {
		Body ret = makeBody(x, y, z, type, null, age, p1, p2);
		bodyList.add(ret);
		return ret;
	}
	
	public void addBody(Body b) {
		bodyList.add(b);
	}

	public void addShit(int x, int y, int z, Body b, YukkuriType type) {
		shitList.add(new Shit(x, y, z, b, type));
	}

	public void addCrushedShit(int x, int y, int z, Body b, YukkuriType type) {
		Shit s = new Shit(x, y, z, b, type);
		s.crushShit();
		shitList.add(s);
	}

	public Vomit addVomit(int x, int y, int z, AgeState ageState, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, ageState, type);
		vomitList.add(v);
		return v;
	}

	public void addCrushedVomit(int x, int y, int z, AgeState ageState, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, ageState, type);
		v.crushVomit();
		vomitList.add(v);
	}

	public Effect addEffect(Effect.EffectType type, int x, int y, int z, int vx, int vy, int vz,
			boolean invert, int life, int loop, boolean end, boolean grav, boolean front) {
		Effect ret = null;
		switch(type) {
			case BAKE:
				ret = new BakeSmoke(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
				break;
			case HIT:
				ret = new Hit(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
				break;
			case MIX:
				ret = new Mix(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
				break;
			case STEAM:
				ret = new Steam(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
				break;
		}
		return ret;
	}

	public ObjEX addObjEX(ObjEX.ObjEXType addObjEX, int x, int y, int initOption) {
		ObjEX objEX = null;
		switch(addObjEX){
			case FOOD:
				objEX = new Food(x, y, initOption);
				break;
			case TOILET:
				objEX = new Toilet(x, y, initOption);
				break;
			case BED:
				objEX = new Bed(x, y, initOption);
				break;
			case TOY:
				objEX = new Toy(x, y, initOption);
				break;
			case BELTCONVEYOR:
				objEX = new Beltconveyor(x, y, initOption);
				break;
			case BREEDINGPOOL:
				objEX = new BreedingPool(x, y, initOption);
				break;
			case GARBAGECHUTE:
				objEX = new GarbageChute(x, y, initOption);
				break;
			case MACHINEPRESS:
				objEX = new MachinePress(x, y, initOption);
				break;
			case FOODMAKER:
				objEX = new FoodMaker(x, y, initOption);
				break;
			case MIXER:
				objEX = new Mixer(x, y, initOption);
				break;
			case ORANGEPOOL:
				objEX = new OrangePool(x, y, initOption);
				break;
			case PRODUCTCHUTE:
				objEX = new ProductChute(x, y, initOption);
				break;
			case STALK:
				objEX = new Stalk(x, y, initOption);
				break;
			case DIFFUSER:
				objEX = new Diffuser(x, y, initOption);
				break;
			case YUNBA:
				objEX = new Yunba(x, y, initOption);
				break;
			case STICKYPLATE:
				objEX = new StickyPlate(x, y, initOption);
				break;
			case HOTPLATE:
				objEX = new HotPlate(x, y, initOption);
				break;
			case AUTOFEEDER:
				objEX = new AutoFeeder(x, y, initOption);
				break;
			case SUI:
				objEX = new Sui(x, y, initOption);
				break;
			case TRASH:
				objEX = new Trash(x, y, initOption);
				break;
			default:
				break;
		}
		if(!objEX.isRemoved()) {
			Cash.buyItem(objEX);
		}
		return objEX;
	}

	public static void setAlarm() {
		alarm = true;
		alarmPeriod = ALARM_PERIOD;
	}

	public static boolean getAlarm() {
		return alarm;
	}

	public static void cleanYukkuri() {
		for (Body b: bodyList) {
			if (!b.isDead()) b.setCleaning();
		}
	}
	
	public static void cleanBody() {
		for (Body b: bodyList) {
			if (b.isDead())
				b.remove();
		}
	}
	
	public static void cleanShit() {
		for (Shit s: shitList) {
			s.remove();
		}
		for (Vomit v: vomitList) {
			v.remove();
		}
	}
	
	public static void cleanEtc() {
		for (ObjEX oex: Food.objEXList) {
			if (((Food)oex).isEmpty())
				oex.remove();
		}
		for (ObjEX oex: Stalk.objEXList) {
			Stalk st = (Stalk)oex;
			if (st.getPlantYukkuri() == null){
				st.remove();
			}
		}
	}
	
	public static void cleanAll() {
		for (Shit s: shitList) {
			s.remove();
		}
		for (Vomit v: vomitList) {
			v.remove();
		}
		for (ObjEX oex: Food.objEXList) {
			if (((Food)oex).isEmpty())
				oex.remove();
		}
		for (Body b: bodyList) {
			if (b.isDead())
				b.remove();
		}
		for (ObjEX oex: Stalk.objEXList) {
			Stalk st = (Stalk)oex;
			if (st.getPlantYukkuri() == null){
				st.remove();
			}
		}
	}

	// 螢ｰ謗帙￠
	public static void voice(int type) {
		for (Body b: bodyList) {
			b.voiceReaction(type);
		}
	}	

	public static DayState getDayState(){
		if ( operationTime % ( dayTime + nightTime ) < nightTime / 5 ) {
			return DayState.MORNING;
		}else if ( operationTime % ( dayTime + nightTime ) < dayTime - nightTime / 5 ){
			return DayState.DAY;
		}else if ( operationTime % ( dayTime + nightTime ) < dayTime){
			return DayState.EVENING;
		}else{
			return DayState.NIGHT;
		}
	}
	
	// Season Handling
	
	public static SeasonState getSeasonState(){
		dayTimer ++;
		if (dayTimer == 2400){
		daycount = daycount + 1;
		dayTimer = 0;
	
	
		}
		if (daycount > 1 ) {
			daycount = 0;
			seasoncount = seasoncount + 1;
			if (seasoncount > 3){
				seasoncount = 0;
			}
		}
		if (seasoncount == 0 ) {
			isSnowing = false;
			return SeasonState.SPRING;
		} if ( seasoncount == 1 ){
			isSnowing = false;
			return SeasonState.SUMMER;
		} if ( seasoncount ==2 ){
			return SeasonState.FALL;
		}if (seasoncount == 3){
			return SeasonState.WINTER;
		}
		else 
			return SeasonState.SUMMER;
	}

	public SeasonState getSeason()
	{
		return getSeasonState();
	}
	
	//Season Parameters
	// Order is Spring, Summer, Fall, Winter, Spring
	public int applySeason()
	{
		int ambientTemp = 60;
		int randomizer = (rnd.nextInt(30) - 15);  // plus or minus up to 15 degrees
		switch (getSeasonState()){
		case SPRING: 
			 ambientTemp = 65 + randomizer;
			
		break;
		
		case SUMMER:

			ambientTemp = 75 + randomizer;
			isHeatwave = rnd.nextInt(5) == 1;
			if (isHeatwave) {
			 ambientTemp = 100 + randomizer;
			}

			
		break;
		
		case FALL:
			 ambientTemp = 45 + randomizer;
			
		break;
		
		case WINTER:
			 ambientTemp = 30 + randomizer;
			isSnowing = rnd.nextInt(3) == 1;
			if (isSnowing)
			{
				ambientTemp = 0 + randomizer;
				if (ambientTemp < 0)
				{
					ambientTemp = 0;
				}
			}
			
			
		break;
		
		}
		return ambientTemp;
	}
	
	public boolean getHeatwave()
	{
		return applySeason() > 105;
	}
	
	public boolean getSnow()
	{
		return applySeason() < 15;
	}
	
	public int applyWeather()
	{
		if (getHeatwave()){  //Apply Heat Overlay
			return 1;
		}
		if (getSnow()){ //Apply Snow Overlay
			return 2;
		}
		else
			return 3;  //NoWeather
	}
	
	///////
	
	public static void resetTerrariumEnvironment(){
		antifungalSteam = false;
		humid = false;
		orangeSteam = false;
		ageBoostSteam = false;
		ageStopSteam = false;
		antidosSteam = false;
		poisonSteam = false;
	}

	public static void changeTerrariumSize(int x, int y, int z, int terrariumSize ) {
		terrariumSizeParcent = terrariumSize;
		MAX_X = x * terrariumSizeParcent / 100;
		MAX_Y = y * terrariumSizeParcent / 100;
		MAX_Z = z * terrariumSizeParcent / 100;
		wallMap = new int[MAX_X+1][MAX_Y+1];
		
		Translate.setMapSize(MAX_X, MAX_Y, MAX_Z);
	}
	
	public static int getInterval() {
		return intervalCount;
	}

	public void run() {
		
		intervalCount = (++intervalCount) & 255;
//		if(intervalCount == 0) {
	//		System.gc();
//		}
		
		if (alarmPeriod >= 0) {
			alarmPeriod--;
			if (alarmPeriod <= 0) {
				alarmPeriod = 0;
				alarm = false;
			}
		}
		Obj.Event ret = Obj.Event.DONOTHING;
		// Update Platform state.		蠎顔ｽｮ縺榊ｼ上�迚ｩ繧呈峩譁ｰ
		List <ObjEX>platformList = getPlatformList();
		platformList.addAll(Sui.objEXList); // 縺吶＜繝ｼ繧ゅ≠縺溘ｊ蛻､螳壹ｒ陦後≧縲�
		// 豈碑ｼ�ｯｾ雎｡縺ｮ繝ｪ繧ｹ繝井ｽ懈�
		List <Obj>objList = getObjList();
		objList.addAll(getObjectList());

		for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			ret = oex.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
				continue;
			}
			
			if(!oex.getEnabled()) continue;
			if(oex.getHitCheckObjType() == 0) continue;
			if(!oex.enableHitCheck()) continue;
			
			// 豈弱ヵ繝ｬ繝ｼ繝�メ繧ｧ繝�け縺ｯ驥阪＞縺ｮ縺ｧ繧､繝ｳ繧ｿ繝ｼ繝舌Ν縺ｧ謨ｰ繝輔Ξ繝ｼ繝�↓荳�ｺｦ縺ｮ繝√ぉ繝�け縺ｫ縺吶ｋ
			if(!oex.checkInterval(intervalCount)) continue;
			
			oex.getCollisionRect(tmpRect);

			for (Obj o : objList) {
				int objType = 0;
				switch(o.getObjType()){
					case YUKKURI:
						objType = ObjEX.YUKKURI;
						break;
					case SHIT:
						objType = ObjEX.SHIT;
						break;
					case PLATFORM:
						objType = ObjEX.PLATFORM;
						break;
					case FIX_OBJECT:
						objType = ObjEX.FIX_OBJECT;
						break;
					case OBJECT:
						if(o instanceof Food) objType = ObjEX.FOOD;
						else if(o instanceof Toilet) objType = ObjEX.TOILET;
						else if(o instanceof Toy) objType = ObjEX.TOY;
						else objType = ObjEX.OBJECT;
						break;
					case VOMIT:
						objType = ObjEX.VOMIT;
						break;
					default:
						break;
				}
				if ((objType & oex.getHitCheckObjType()) != 0){
					oex.checkHitObj(tmpRect, o);
				}
			}
		}		

		// Update FixObj state.		鄂ｮ迚ｩ繧呈峩譁ｰ
		List <ObjEX>fixObjList = getFixObjList();
		List <Obj>ykList = new ArrayList<Obj>();
		ykList.addAll(Terrarium.bodyList);
		for (Iterator<ObjEX> i = fixObjList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			ret = oex.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
				continue;
			}
			if(!oex.getEnabled()) continue;
			oex.getCollisionRect(tmpRect);
			for (Obj o : ykList) {
				oex.checkHitObj(tmpRect, o);
			}
		}
		
		// Update Object state.		迚ｩ繧呈峩譁ｰ
		List <ObjEX>objectList = getObjectList();
		resetTerrariumEnvironment();
		for (Iterator<ObjEX> i = objectList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			ret = oex.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
			}
			if( oex.getObjEXType() == ObjEX.ObjEXType.DIFFUSER && oex.getEnabled()) {
				boolean[] flags = ((Diffuser)oex).getSteamType();
				
				if(flags[Diffuser.SteamType.ANTI_FUNGAL.ordinal()]) antifungalSteam = true;
				if(flags[Diffuser.SteamType.STEAM.ordinal()]) humid = true;
				if(flags[Diffuser.SteamType.ORANGE.ordinal()]) orangeSteam = true;
				if(flags[Diffuser.SteamType.AGE_BOOST.ordinal()]) ageBoostSteam = true;
				if(flags[Diffuser.SteamType.AGE_STOP.ordinal()]) ageStopSteam = true;
				if(flags[Diffuser.SteamType.ANTI_DOS.ordinal()]) antidosSteam = true;
				if(flags[Diffuser.SteamType.ANTI_YU.ordinal()]) poisonSteam = true;
			}
		}

		// Update shit state.
		for (Iterator<Shit> i = shitList.iterator(); i.hasNext();) {
			Shit s = i.next();
			ret = s.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
			}
		}
		// Update vomit state.
		for (Iterator<Vomit> i = vomitList.iterator(); i.hasNext();) {
			Vomit v = i.next();
			ret = v.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
			}
		}
		// update body state
		boolean transCheck = (operationTime % 60 == 0);
		Body transBody = null;
		// 繧�▲縺上ｊ縺ｮ蟇�ｺｦ繧堤腸蠅�せ繝医Ξ繧ｹ縺ｨ縺励※邂怜�
		for (Iterator<Body> i = bodyList.iterator(); i.hasNext();) {
			Body b = i.next();		
			
			// update all body variables
			ret = b.clockTick();
				
			switch (ret) {
			case DEAD:
				if (b.isInfration()) {
					int burstPower =  (b.getSize() - b.getOriginSize())*3/4;
					for (Dna babyTypes: b.getBabyTypes()) {
						addBaby(b.getX(), b.getY(), b.getZ()+b.getSize()/20, rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/5+1)-burstPower/10-1, babyTypes, b, b.getPartner());
					}
					b.getBabyTypes().clear();
					for ( Stalk s:b.getStalks() ){
						if ( s != null ) {
							s.kick(rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/5+1)-burstPower/10-1);
						}
					}
					b.disPlantStalks();
					if ( b.getShit() > b.SHITLIMIT[b.getAgeState().ordinal()] ){
						for ( int j = 0; b.getShit() / b.SHITLIMIT[b.getAgeState().ordinal()] > j; j++ ){
							addShit(b.getX(), b.getY(), b.getZ()+b.getSize()/15, b, b.getShitType());
							shitList.get(shitList.size()-1).kick(rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/4+1)-burstPower/8, rnd.nextInt(burstPower/5+1)-burstPower/10-1);
						}
					}
					b.setShit(0);
					if ( b.isCrashed() == false ) {
						b.strikeByPress();
					}
				} else if(b.isCrashed()) {
					b.disPlantStalks();
				}
				b.upDate();
//				b.setForcePanicClear();
				continue;
			case BIRTHBABY:
				if ( b.getAge() % 10 == 0 ){
					if (!b.isHasPants()) {
						Dna babyType = b.getBabyTypesDequeue();
						if ( babyType != null ){
							addBaby(b.getX(), b.getY(), b.getZ()+b.getSize()/15, babyType, b, b.getPartner());
						}
					}
				}
				for ( Stalk s:b.getStalks() ){
					if ( s != null ) {
						for ( Body ba:s.getBindBaby() ){
							if ( ba != null ){
								ba.setUnBirth( false );
								ba.setBindStalk( null );
								ba.kick(0,0,0);
							}
						}
						s.getBindBaby().clear();
						s.setPlantYukkuri( null );
					}
					// 豁｣蟶ｸ縺ｪ蜃ｺ逕｣譎ゅ�闌弱ｒ繝輔�繝牙喧
					int fx, fy;
					for(int f = 0; f < 5; f++) {
						fx = s.getX() - 6 + (f * 7);
						fy = s.getY() - 5 + rnd.nextInt(10);
						fx = Math.max(0, fx);
						fx = Math.min(fx, MAX_X);
						fy = Math.max(0, fy);
						fy = Math.min(fy, MAX_Y);
						addObjEX(ObjEX.ObjEXType.FOOD, fx, fy, Food.type.STALK.ordinal());
					}
					s.remove();
				}
				b.getStalks().clear();
				break;
			case DOSHIT:
				addShit(b.getX(), b.getY(), b.getZ()+b.getSize()/15, b, b.getShitType());
				shitList.get(shitList.size()-1).kick(0,1,1);
				break;
			case DOVOMIT:
				addVomit(b.getX(), b.getY(), b.getZ(), b.getAgeState(), b.getShitType());
				break;
			case REMOVED:
				i.remove();
				b.upDate();
				continue;
			default:
				break;
			}
			
			// 繝代ル繝�け譎ゅ�蛻･蜃ｦ逅�
			if(b.getPanicType() != null) {
				checkPanic(b);
			} else {
				// 繧､繝吶Φ繝亥�逅�
				if(b.getCurrentEvent() != null) {
					EventLogic.eventUpdate(b);
				}
				
				// control frequency
				if(operationTime%2 == 0)
				{
					// Maslow's hierarchy of needs
					boolean maslowControl = true;
					if(b.isDead() || b.isSleeping())
						maslowControl = false;
					
					int maslowCounter = 0;
					if(b.isFull())
						maslowCounter = 1;
					
					while(maslowControl)
					{
						switch(++maslowCounter)
						{	// check food
							case 1 : maslowControl = !FoodLogic.checkFood(b); break;
							// check sukkuri
							case 2 : maslowControl = !BodyLogic.checkPartner(b); break;
							// check toilet
							case 3 : maslowControl = !ToiletLogic.checkToilet(b); break;
							// check sleep
							case 4 : maslowControl = !BedLogic.checkBed(b); break;
							// check toy
							case 5 : if(!ToyLogic.checkToy(b))
										ToyLogic.checkSui(b);
										break;
							default: maslowControl = false; break;
						}
					}
				}
			}
			if ( b.getStalkBabyTypes().size() > 0 ){
				int j = 0;
				Stalk currentStalk = null;
				for (Dna babyTypes: b.getStalkBabyTypes()) {
					if ( j % 5 == 0 ) {
						SimYukkuri.mypane.terrarium.addObjEX( ObjEX.ObjEXType.STALK, b.getX(), b.getY(), b.getDirection().ordinal() );
						currentStalk = (Stalk)Stalk.objEXList.get( Stalk.objEXList.size()-1 );
						b.getStalks().add( currentStalk );
						currentStalk.setPlantYukkuri( b );
					}
					if ( babyTypes != null ) {
						addBaby(b.getX(), b.getY(), 0, babyTypes, b, b.getPartner(), currentStalk);
						babyList.get( babyList.size()-1 ).setBindStalk(currentStalk);
					}else{
						currentStalk.setBindBaby( null );
					}
					j++;
				}
				b.getStalkBabyTypes().clear();
			}
			b.upDate();	

			// 遯∫┯螟臥焚繝√ぉ繝�け
			// 繝ｫ繝ｼ繝怜�縺ｧ繝ｪ繧ｹ繝医ｒ縺�§繧九→萓句､悶′蜃ｺ繧九�縺ｧ縺薙％縺ｧ縺ｯ蛟呵｣懊�蜿悶ｊ蜃ｺ縺励�縺ｿ
			if(transCheck && transBody == null) {
				transBody = YukkuriUtil.checkTransform(b);
			}
		}
		// add babies.
		if (!babyList.isEmpty()) {
			bodyList.addAll(babyList);
			babyList.clear();
		}

		// 繧ｨ繝輔ぉ繧ｯ繝�
		for (Iterator<Effect> i = Terrarium.sortLightEffectList.iterator(); i.hasNext();) {
			Effect ef = i.next();
			ret = ef.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
			}
		}
		for (Iterator<Effect> i = Terrarium.frontLightEffectList.iterator(); i.hasNext();) {
			Effect ef = i.next();
			ret = ef.clockTick();
			if (ret == Obj.Event.REMOVED) {
				i.remove();
			}
		}
		// 繧､繝吶Φ繝医Μ繧ｹ繝医�譛牙柑譛滄俣繝√ぉ繝�け
		EventLogic.clockWorldEvent();
		
		// 遯∫┯螟臥焚螳溯｡�
		if(transBody != null) {
			YukkuriUtil.execTransform(transBody);
		}

		operationTime += TICK;
	
	}
}
