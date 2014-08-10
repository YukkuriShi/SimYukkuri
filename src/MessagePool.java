package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import src.system.ModLoader;
import src.yukkuriBody.Body;
import src.yukkuriBody.ConstantValues.*;

/*****************************************************
	蜈ｨ繧ｭ繝｣繝ｩ縺ｮ繝｡繝�そ繝ｼ繧ｸ邂｡逅�
*/

public class MessagePool
{
	// 繧｢繧ｯ繧ｷ繝ｧ繝ｳ蜷榊ｮ夂ｾｩ
	public enum Action {
		WantFood,		// 鬢檎匱隕�
		NoFood,		// 鬢後↑縺�
		Excite,		// 逋ｺ諠�
		Relax,		// 繝ｪ繝ｩ繝�け繧ｹ
		FuriFuri,		// 蟆ｻ謖ｯ繧�
		Wakeup,		// 襍ｷ蠎�
		Sleep,		// 逹｡逵�
		Scream,		// 謔ｲ魑ｴ
		Scare,		// 諤ｯ縺�
		Alarm,		// 隴ｦ謌�
		Dying,		// 轢墓ｭｻ
		Dead,		// 豁ｻ莠｡
		Eating,		// 鬟滉ｺ�
		EatingShit,		// 鬟滉ｺ�縺�ｓ縺�ｓ)
		EatingBitter,		// 鬟滉ｺ�豈�
		Full,		// 貅��
		Healing,		// 蝗槫ｾｩ
		Sukkiri,		// 縺吶▲縺阪ｊ
		RaperSukkiri,	// 繝ｬ繧､繝代�縺ｨ縺吶▲縺阪ｊ
		Birth,		// 隱慕函
		Shit,		// 謗呈ｳ�
		Shit2,		// 謗呈ｳ�
		SuriSuri,		// 縺吶ｊ縺吶ｊ
		PeroPero,		// 縺ｺ繧阪⊆繧�
		Breed,		// 蜃ｺ逕｣
		Breed2,		// 逕｣蠕�
		HateShit,		// 縺�ｓ縺�ｓ逋ｺ隕�
		Hungry,		// 遨ｺ閻ｹ
		NoAccessory,		// 縺翫°縺悶ｊ豐｡蜿�
		Flying,		// 謖√■荳翫￡
		SadnessForChild,		// 蟄蝉ｾ帶ｭｻ莠｡
		SadnessForPartner,		// 縺､縺後＞豁ｻ莠｡
		SadnessForEldersister,		// 蟋画ｭｻ莠｡
		SadnessForSister,		// 螯ｹ豁ｻ莠｡
		BlockedByWall,		// 螢∫匱隕�
		GetTreasure,		// 縺翫ｂ縺｡繧�匱隕�
		LostTreasure,		// 縺翫ｂ縺｡繧�ｴ帛､ｱ
		Inflation,		// 閹ｨ蠑ｵ
		AbuseBaby,		// 蟄占剞蠕�
		Nobinobi,		// 縺ｮ縺ｳ縺ｮ縺ｳ
		NearToBirth,		// 蜃ｺ逕｣蜑�
		PoisonDamage,		// 豈�
		CantShit,		// 謗呈ｳ�ｸ榊庄
		NoPregnancy,	// 螯雁ｨ�､ｱ謨�
		Fear,			// 諱先�縺ｧ繝代ル繝�け迥ｶ諷�
		Burning,		// 轣ｫ縺後▽縺�※繧狗憾諷�
		Wet,			// 豌ｴ繧偵°縺代ｉ繧後◆
		AnalSealed,		// 縺ゅ↓繧�ｋ髢蛾事
		NeedleStick,	// 驥昴′蛻ｺ縺輔▲縺滓凾
		NeedleScream,	// 驥昴′蛻ｺ縺輔▲縺ｦ縺�ｋ譎�
		ExtractingNeedlePartner, // 縺､縺後＞縺ｮ驥昴ｒ縺舌�繧翫＄繧翫☆繧�
		ExtractingNeedleChild, // 蟄舌�驥昴ｒ縺舌�繧翫＄繧翫☆繧�
		NeedlePain,		// 陲ｫ縺舌�繧翫＄繧頑凾
		NeedleRemove,	// 驥昴ｒ謚懊＞縺滓凾
		WantAmaama,		// 縺ゅ∪縺ゅ∪隕∵ｱ�
		CantMove,		// 遘ｻ蜍穂ｸ崎�1
		CantMove2,		// 遘ｻ蜍穂ｸ崎�2
		Pull,			// 蠑輔▲蠑ｵ繧�
		Pull2,			// 蠑輔▲蠑ｵ繧�
		Press,			// 縺､縺ｶ縺�
		Press2,			// 縺､縺ｶ縺�
		FindAmaama,		// 縺ゅ∪縺ゅ∪逋ｺ隕�
		EatingAmaama,	// 縺ゅ∪縺ゅ∪鬟滉ｺ�
		Vomit,			// 蜷宣､｡
		Dying2,			// 豎守畑縲∫�豁ｻ邯咏ｶ�
		Surprise,		// 豎守畑縲�ｩ壹″
		RevengeAttack,	// 豎守畑縲∽ｻ悶�繧�▲縺上ｊ縺ｫ菴輔°縺輔ｌ縺溘→縺阪↓蟇ｾ縺吶ｋ謾ｻ謦��蜿肴茶
		BraidCut,		// 縺翫＆縺偵�蟆ｻ蟆ｾ縲∫ｾｽ繧偵■縺弱ｋ
		EatenByBody,	// 豎守畑縲∽ｻ悶�繧�▲縺上ｊ縺ｫ逕溘″縺溘∪縺ｾ鬟溘∋繧峨ｌ繧�
		EatenByBody2,	// 豎守畑縲∽ｻ悶�繧�▲縺上ｊ縺ｫ逕溘″縺溘∪縺ｾ蜷ｸ繧上ｌ繧�
		EscapeFromRemirya, // 繧後∩繧翫ｃ縺九ｉ騾�￡繧�
		// 繧､繝吶Φ繝育ｳｻ繝｡繝�そ繝ｼ繧ｸ
		HateYukkuri,		// 縺翫°縺悶ｊ縺ｮ縺ｪ縺�ｆ縺｣縺上ｊ逋ｺ隕�
		ConcernAboutPartner, // 縺､縺後＞繧呈ｰ鈴▲縺�
		ConcernAboutChild, // 蟄舌ｒ豌鈴▲縺�
		ConcernAboutEldersister, // 蟋峨ｒ豌鈴▲縺�
		ConcernAboutSister, // 螯ｹ繧呈ｰ鈴▲縺�
		RootForPartner,	// 縺､縺後＞蜃ｺ逕｣譎�	
		FirstGreeting,	// 蟄舌∈縺ｮ譛��縺ｮ謖ｨ諡ｶ
		ScareRapist,		// 繝ｬ繧､繝代�諤ｯ縺�
		AttackRapist,	// 繝ｬ繧､繝代�縺ｫ謾ｻ謦�
		CounterRapist,	// 繝ｬ繧､繝代�縺ｫ蜿肴茶蜻ｽ莉､
		YukkuringSui,	// 繧�▲縺上ｊ縺励※縺�ｋ縺吶＜繝ｼ繧定ｦ九※
		WantingSui,		// 縺吶＜繝ｼ繧呈ｬｲ縺励′繧�
		WantingSuiPartner,		// 縺吶＜繝ｼ繧呈ｬｲ縺励′縺｣縺ｦ縺�ｋ繧�▲縺上ｊ縺ｮ繝代�繝医リ繝ｼ
		WantingSuiParent,	// 縺吶＜繝ｼ繧呈ｬｲ縺励′縺｣縺ｦ縺�ｋ繧�▲縺上ｊ縺ｮ隕ｪ
		FindSui,		// 縺吶＜繝ｼ繧定ｦ九▽縺代ｋ
		GetSui,			// 縺吶＜繝ｼ繧定�蛻��繧ゅ�縺ｫ縺吶ｋ
		FindGetSui,			// 閾ｪ蛻��縺吶＜繝ｼ縺ｫ縺ｮ繧翫↓縺�￥
		FindGetSuiOtner,	// 閾ｪ蛻�ｻ･螟悶�縺吶＜繝ｼ縺ｫ縺ｮ繧翫↓縺�￥
		WantRideSuiOtner,	// 閾ｪ蛻�ｻ･螟悶�縺吶＜繝ｼ縺ｫ縺ｮ繧翫↓縺�￥縺ｨ縺阪↓蜻ｼ縺ｶ
		RideSui,		// 縺吶＜繝ｼ縺ｫ縺ｮ繧�
		RidingSui,		// 縺吶＜繝ｼ縺ｫ縺ｮ縺｣縺ｦ縺�ｋ
		DrivingSui,		// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ
		DrivingSuiPartner,		// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ繝代�繝医リ繝ｼ繧偵∩縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		DrivingSuiChild,	// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ縺薙←繧ゅｒ縺ｿ縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		DrivingSuiPAPA,	// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ辷ｶ隕ｪ繧偵∩縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		DrivingSuiMAMA,	// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ豈崎ｦｪ繧偵∩縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		DrivingSuiOldSister,	// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ蟋峨ｒ縺ｿ縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		DrivingSuiYoungSister,	// 縺吶＜繝ｼ繧帝°霆｢縺吶ｋ螯ｹ繧偵∩縺ｦ�郁�蛻��縺ｮ縺｣縺ｦ縺�↑縺�ｼ�
		RideOffSui,		// 縺吶＜繝ｼ縺九ｉ髯阪ｊ繧�
		hasSui,			// 謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺吶ｋ
		hasSuiPartner,	// 謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧九ヱ繝ｼ繝医リ繝ｼ
		hasSuiChild,	// 蟄舌←繧ゅ�謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧玖ｦｪ
		hasSuiPAPAChild,	// 辷ｶ隕ｪ縺ｮ謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧九％縺ｩ繧�
		hasSuiMAMAChild,	// 豈崎ｦｪ縺ｮ謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧九％縺ｩ繧�
		hasSuiOldSister,	// 蟋峨�謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧�
		hasSuiYoungSister,	// 螯ｹ縺ｮ謖√▲縺ｦ縺�ｋ縺吶＜繝ｼ繧定�諷｢縺輔ｌ繧�
	}

	// 蝓九ａ霎ｼ縺ｿ譁�ｭ怜ｮ夂ｾｩ
	public enum Replace {
		name,
		name2,
		partner,
		dummy
	}

	private static Random rnd = new Random();
	private static HashMap<String, MessageMap>[] pool_j = null;

	// 蜈ｨ繝｡繝�そ繝ｼ繧ｸ隱ｭ縺ｿ霎ｼ縺ｿ
	@SuppressWarnings("unchecked")
	public static final void loadMessage(ClassLoader loader)
	{
		BufferedReader br = null;
		
		YukkuriUtil.YukkuriType[] yk = YukkuriUtil.YukkuriType.values();
		pool_j = new HashMap[yk.length];

		for(int i = 0; i < yk.length; i++)
		{
			pool_j[i] = new HashMap<String, MessageMap>();
			// 豎守畑繝｡繝�そ繝ｼ繧ｸ
			br = ModLoader.openMessageFile(loader, yk[i].messageFileName + "_j.txt");
			try {
				readMessageMap(br, pool_j[i]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 繧､繝吶Φ繝医Γ繝�そ繝ｼ繧ｸ
			br = ModLoader.openMessageFile(loader, yk[i].messageFileName + "_ev_j.txt");
			try {
				readMessageMap(br, pool_j[i]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 1繝輔ぃ繧､繝ｫ隱ｭ縺ｿ霎ｼ縺ｿ
	private static final void readMessageMap(BufferedReader br, HashMap<String, MessageMap> map) throws IOException
	{
		String actName = null;
		MessageMap act = null;
		String line = null;
		ArrayList<String> msg = null;
		boolean[] flags = null;
		String tagName = null;

		do
		{
			line = br.readLine();
			if(line != null) line = line.trim();
			
			if(line == null || "".equals(line)) continue;
			
			// 蜈磯�譁�ｭ怜愛螳�
			String head1 = line.substring(0, 1);
			if("#".equals(head1)) continue;

			if("[".equals(head1)) // 繧｢繧ｯ繧ｷ繝ｧ繝ｳ
			{
				String head2 = line.substring(1, 2);
				// 繧｢繧ｯ繧ｷ繝ｧ繝ｳ繧帝哩縺倥ｋ
				if("/".equals(head2))
				{
					if(actName != null && act != null)
					{
						map.put(actName, act);
						actName = null;
						act = null;
						msg = null;
						flags = null;
						tagName = null;
					}
					continue;
				}
				// 繧｢繧ｯ繧ｷ繝ｧ繝ｳ髢句ｧ�
				int st = line.indexOf("[") + 1;
				int ed = line.indexOf("]");
				actName = line.substring(st, ed);
			}
			else if("<".equals(head1)) // 繧ｵ繝悶ち繧ｰ
			{
				if(actName == null) continue;
				
				// 迴ｾ蝨ｨ縺ｾ縺ｧ縺ｮ繝｡繝�そ繝ｼ繧ｸ繧堤匳骭ｲ
				if(msg != null && msg.size() > 0)
				{
					String key = createTagKey(flags);
					if(key != null)
					{
						act.map.put(key, (String[]) msg.toArray(new String[]{}));
					}
					msg = new ArrayList<String>();
				}
				
				// 繧ｿ繧ｰ繧帝哩縺倥ｋ
				String head2 = line.substring(1, 2);
				if("/".equals(head2))
				{
					int st = line.indexOf("/") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);
					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					flags[tag.ordinal()] = false;
					
					// normal, rude繧ｿ繧ｰ縺ｮ蛹ｺ蛻�ｊ
					if(MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag))
					{
						flags = null;
						msg = null;
					}
				}
				else
				{
					// 繧ｿ繧ｰ髢句ｧ�
					int st = line.indexOf("<") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);

					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					if(tag != null)
					{
						if(MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag))
						{
							if(act == null)
							{
								act = new MessageMap();
							}
							msg = new ArrayList<String>();
							flags = new boolean[MessageMap.Tag.values().length];
							if(MessageMap.Tag.rude.equals(tag))
							{
								act.rudeFlag = true;
							} else {
								act.normalFlag = true;
							}
						}
						flags[tag.ordinal()] = true;
						if(flags[MessageMap.Tag.normal.ordinal()])
						{
							act.normalTag[tag.ordinal()] = true;
						}
						else if(flags[MessageMap.Tag.rude.ordinal()])
						{
							act.rudeTag[tag.ordinal()] = true;
						}
					}
				}
			}
			else // 繧ｻ繝ｪ繝�
			{
				msg.add(line);
			}
		}while(line != null);
	}
	
	// 繝輔Λ繧ｰ縺九ｉ繝槭ャ繝励く繝ｼ菴懈�
	private static final String createTagKey(boolean[] flags)
	{
		StringBuffer key = new StringBuffer("");
		MessageMap.Tag[] tags = MessageMap.Tag.values(); 

		for(int i = 0; i < tags.length; i++)
		{
			if(flags[i])
			{
				key.append(tags[i].name());
				key.append("_");
			}
		}
		if(key.length() == 0) return null;
		
		return key.toString();
	}
	
	// 繝｡繝�そ繝ｼ繧ｸ蜿門ｾ�
	public static final String getMessage(Body body, Action action)
	{
		HashMap<String, MessageMap> map = null;
		MessageMap act = null;
		String name = "";
		String name2 = "";
		String partnerName = "";
		
		map = pool_j[body.getMsgType().ordinal()];
		name = body.getNameE();
		name2 = body.getNameE2();
		if(body.getPartner() != null) partnerName = body.getPartner().getNameE();
		
		if(map == null) return "NO MESSAGE FILE";

		act = map.get(action.name());

		if(act == null) return "NO ACTION [" + action.name() + "]";

		boolean[] flags = null;
		StringBuilder key = null;
		
		// 繧ｲ繧ｹ繝√ぉ繝�け
		if((body.isRude() && act.rudeFlag) || !act.normalFlag)
		{
			flags = act.rudeTag;
			key = new StringBuilder(MessageMap.Tag.rude.name() + "_");
		}
		else
		{
			flags = act.normalTag;
			key = new StringBuilder(MessageMap.Tag.normal.name() + "_");
		}
		// 蟷ｴ鮨｢繝√ぉ繝�け
		if(body.getMindAgeState() == AgeState.BABY && flags[MessageMap.Tag.baby.ordinal()])
		{
			key.append(MessageMap.Tag.baby.name() + "_");
		}
		else if(body.getMindAgeState() == AgeState.CHILD && flags[MessageMap.Tag.child.ordinal()])
		{
			key.append(MessageMap.Tag.child.name() + "_");
		}
		else if(body.getMindAgeState() == AgeState.ADULT && flags[MessageMap.Tag.adult.ordinal()])
		{
			key.append(MessageMap.Tag.adult.name() + "_");
		}
		// 繝�Γ繝ｼ繧ｸ繝√ぉ繝�け
		if(body.isDamaged() && flags[MessageMap.Tag.damage.ordinal()])
		{
			key.append(MessageMap.Tag.damage.name() + "_");
		}
		// 雜ｳ辟ｼ縺阪メ繧ｧ繝�け
		if(body.getFootBakeLevel() == FootBake.CRITICAL && flags[MessageMap.Tag.footbake.ordinal()])
		{
			key.append(MessageMap.Tag.footbake.name() + "_");
		}
		// 縺翫￥繧九∩繝√ぉ繝�け
		if(body.hasPants() && flags[MessageMap.Tag.pants.ordinal()])
		{
			key.append(MessageMap.Tag.pants.name() + "_");
		}
		
		String[] msg = act.map.get(key.toString());

		if(msg == null) return "NO TAG <" + key.toString() + ">";
		
		StringBuffer ret = new StringBuffer(msg[rnd.nextInt(msg.length)]);
		// 蝓九ａ霎ｼ縺ｿ譁�ｭ励�鄂ｮ縺肴鋤縺�
		if(ret.indexOf("%") != -1)
		{
			if(ret.indexOf("%" + Replace.dummy.name()) != -1) return null;
			int st;
//			do
//			{
				st = ret.indexOf("%" + Replace.name.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.name.name().length() + 1, name);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"name");}
				}
//			}while(st != -1);
//			do
//			{
				st = ret.indexOf("%" + Replace.name2.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.name2.name().length() + 1, name2);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"name2");}
				}
//			}while(st != -1);
//			do
//			{
				st = ret.indexOf("%" + Replace.partner.name());
				if(st != -1)
				{
					try {
						ret.replace(st, st + Replace.partner.name().length() + 1, partnerName);
					}catch(StringIndexOutOfBoundsException e) {System.out.println("["+action+"]"+ret+"("+st+")"+"partnerName");}
				}
//			}while(st != -1);
		}

		return ret.toString();
	}

/*
	private static final void test(ClassLoader loader)
	{
		try {
			InputStream is = loader.getResourceAsStream("data/marisa_j.xls");
			Workbook wb;
			wb = WorkbookFactory.create(is);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);
			System.out.println(cell.getStringCellValue());
		} catch (InvalidFormatException e) {
			// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝�け
			e.printStackTrace();
		}
	}
*/
}

