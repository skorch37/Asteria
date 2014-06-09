package tools.packet;

import client.inventory.*;
import clientside.InnerSkillValueHolder;
import clientside.MapleCharacter;
import clientside.MapleClient;
import clientside.MapleCoolDownValueHolder;
import clientside.MapleCoreAura;
import clientside.MapleQuestStatus;
import clientside.MapleTrait;
import clientside.PartTimeJob;
import clientside.Skill;
import clientside.SkillEntry;
import constants.GameConstants;
import handling.Buffstat;
import handling.world.MapleCharacterLook;
import java.util.*;
import java.util.Map.Entry;
import server.CashItem;
import server.MapleItemInformationProvider;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import server.shops.MapleShopItem;
import server.stores.AbstractPlayerStore;
import server.stores.IMaplePlayerShop;
import tools.BitTools;
import tools.HexTool;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class PacketHelper {

    public static final long FT_UT_OFFSET = 116444592000000000L;
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static long getTime(long realTimestamp) {
		return realTimestamp == -1L ? MAX_TIME : realTimestamp == -1L ? ZERO_TIME : realTimestamp == -3L ? PERMANENT : realTimestamp * 10000L + 116444592000000000L;
    }

    public static long decodeTime(long fakeTimestamp) {
	return fakeTimestamp == 150842304000000000L ? (long) -1L : fakeTimestamp == 94354848000000000L
	       ? (long) -2L : fakeTimestamp == 150841440000000000L ? (long) -3L : fakeTimestamp - 116444592000000000L / 10000L;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (TimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        
		long time = roundToMinutes ? timeStampinMillis / 1000L / 60L * 600000000L
		            : timeStampinMillis * 10000L;
					
        return time + 116444592000000000L;
    }

    public static void addImageInfo(MaplePacketLittleEndianWriter mplew, byte[] image) {
        mplew.writeInt(image.length);
        mplew.write(image);
    }

    public static void addStartedQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(1);
        final List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i$ = q.getMobKills().values().iterator(); i$.hasNext();) {
                    int kills = ((Integer) i$.next()).intValue();
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                mplew.writeMapleAsciiString(sb.toString());
            } else {
                mplew.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
            }
        }
        addNXQuestInfo(mplew, chr);
    }

    public static void addNXQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
         mplew.writeShort(0);
    }

    public static void addCompletedQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(1);
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeInt(KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
            //v139 changed from long to int
        }
    }

public static final void addSkillInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.write(1);
        mplew.writeShort(0);
        /*final Map<Skill, SkillEntry> skills = chr.getSkills();
        boolean useOld = skills.size() < 500;

        mplew.write(useOld ? 1 : 0);
        if (useOld) {
            mplew.writeShort(skills.size());
            for (final Map.Entry skill : skills.entrySet()) {
                mplew.writeInt(((Skill) skill.getKey()).getId());
                mplew.writeInt(((SkillEntry) skill.getValue()).skillevel);
                addExpirationTime(mplew, ((SkillEntry) skill.getValue()).expiration);

                if (((Skill) skill.getKey()).isFourthJob()) {
                    mplew.writeInt(((SkillEntry) skill.getValue()).masterlevel);
                }
            }
        } else {
            final Map<Integer, Integer> skillsWithoutMax = new LinkedHashMap<>();
            final Map<Integer, Long> skillsWithExpiration = new LinkedHashMap<>();
            final Map<Integer, Byte> skillsWithMax = new LinkedHashMap<>();

            for (final Entry<Skill, SkillEntry> skill : skills.entrySet()) {
                skillsWithoutMax.put(((Skill) skill.getKey()).getId(), ((SkillEntry) skill.getValue()).skillevel);
                if (((SkillEntry) skill.getValue()).expiration > 0L) {
                    skillsWithExpiration.put(((Skill) skill.getKey()).getId(), ((SkillEntry) skill.getValue()).expiration);
                }
                if (((Skill) skill.getKey()).isFourthJob()) {
                    skillsWithMax.put(((Skill) skill.getKey()).getId(), ((SkillEntry) skill.getValue()).masterlevel);
                }
            }

            int amount = skillsWithoutMax.size();
            mplew.writeShort(amount);
            for (final Entry<Integer, Integer> x : skillsWithoutMax.entrySet()) {
                mplew.writeInt((x.getKey()));
                mplew.writeInt((x.getValue()));
            }
            mplew.writeShort(0);

            amount = skillsWithExpiration.size();
            mplew.writeShort(amount);
            for (final Entry<Integer, Long> x : skillsWithExpiration.entrySet()) {
                mplew.writeInt((x.getKey()));
                mplew.writeLong((x.getValue()));
            }
            mplew.writeShort(0);

            amount = skillsWithMax.size();
            mplew.writeShort(amount);
            for (final Entry<Integer, Byte> x : skillsWithMax.entrySet()) {
                mplew.writeInt((x.getKey()));
                mplew.writeInt((x.getValue()));
            }
            mplew.writeShort(0);
        }*/
    }

//    public static void addSingleSkill(MaplePacketLittleEndianWriter mplew, Skill skill, SkillEntry ske) {
//        try {
//            // if (skill.getId() != 1001008) return;
//
//            MaplePacketLittleEndianWriter mplew1 = new MaplePacketLittleEndianWriter();
//
//            mplew1.writeInt(skill.getId());
//            mplew1.writeInt(ske.skillevel);
//            addExpirationTime(mplew1, ske.expiration);
//
//            if (GameConstants.isHyperSkill(skill)) {
//                //System.out.println("HYPER: " + ((Skill) skill.getKey()).getId());
//                mplew1.writeInt(0);
//            } else if (((Skill) skill).isFourthJob()) {
//                mplew1.writeInt(((SkillEntry) ske).masterlevel);
//            }
//            if (skill.getId() == 1001008) {
//                System.out.println(HexTool.toString(mplew1.getPacket()));
//            }
//            mplew.write(mplew1.getPacket());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public static void addCoolDownInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();

        mplew.writeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            mplew.writeInt(cooling.skillId);
            mplew.writeInt((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
        if (cd.isEmpty()) {
            mplew.writeShort(0);
        }
    }

    public static void addRocksInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(mapz[i]);
        }

        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
        }

        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
    }

    public static void addUnk400Info(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        short size = 0;
        mplew.writeShort(size);
        for (int i = 0; i < size; i++) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        mplew.writeShort(cRing.size());
        for (MapleRing ring : cRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        mplew.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
        List<MapleRing> mRing = aRing.getRight();
        mplew.writeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(chr.getName(), 13);
            mplew.writeAsciiString(ring.getPartnerName(), 13);
        }
    }

    public static void addMoneyInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeLong(chr.getMeso());
    }
  
    public static void addInventoryInfoNew(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00 00 00 00 00 66 31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 40 40 40 60 00 40 E0 FD 3B 37 4F 01 00 05 00 01 82 DE 0F 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 04 00 00 07 03 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 01 A2 2C 10 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 04 00 00 07 02 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 01 A6 5B 10 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 04 00 00 05 02 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0A 00 01 10 C1 10 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 00 0C 08 00 0A 00 05 00 01 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 01 F0 DD 13 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 01 00 00 07 11 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 37 00 01 20 E2 11 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 3C 00 00 00 01 00 01 00 01 00 01 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 60 4A 0F 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 14 00 00 07 09 00 02 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 01 00 EE FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 01 8E 4A 0F 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 04 00 00 07 0A 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 01 3B 4A 0F 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 04 00 00 07 0A 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 01 60 4A 0F 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 14 00 00 07 07 00 01 00 1C 00 00 00 FF BC E9 AE 7B 46 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF 00 40 E0 FD 3B 37 4F 01 FF FF FF FF 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 02 85 84 1E 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 19 00 00 00 00 00 02 02 E4 16 25 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 03 02 83 84 1E 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 00 00 01 02 AA 8A 3D 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 02 02 AB 8A 3D 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 03 02 13 09 3D 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 02 00 00 00 00 00 04 02 00 09 3D 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 06 00 00 00 00 00 05 02 A0 71 43 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 06 02 32 C4 41 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 01 00 00 00 00 00 00 01 02 80 64 4D 00 01 1A B6 01 00 00 00 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 0F 00 00 00 00 00 02 02 20 74 4D 00 01 1B B6 01 00 00 00 00 00 00 80 05 BB 46 E6 17 02 FF FF FF FF 0A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
    }
    
    public static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(0);
        //addPotionPotInfo(mplew, chr);
        mplew.writeInt(0); // potion pot info 
        mplew.writeInt(0);
        mplew.writeInt(chr.getId());
        mplew.writeZeroBytes(31);
        mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            mplew.writeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            mplew.writeLong(getTime(-2L));
        }
        mplew.write(0); // the fix for the dc lolz
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED); // Regular Equipped Items
        final List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        
        for (Item item : equipped) {
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // Regular items 
        
        for (Item item : equipped) {
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) { // Cash Items
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // Cash Items
        
        iv = chr.getInventory(MapleInventoryType.EQUIP); // Equip items
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.writeShort(0); // // Equip items
        
        
        for (Item item : equipped) {
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) { // Unknown Items
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // // Unknown Items
        
        
        for (Item item : equipped) {
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) { // Dragon Items
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // Dragon Items
        
        
        for (Item item : equipped) {
            if (item.getPosition() <= -1200) { // Mechanic Items
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }

        }

        mplew.writeShort(0); // Android items
        mplew.writeShort(0); // 1
        mplew.writeShort(0); // 2
        for (Item item : equipped) {
            if ((item.getPosition() <= -5000) && (item.getPosition() >= -5002)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // 3
        mplew.writeShort(0); // 4
        mplew.writeShort(0); // 5
        mplew.writeShort(0); // 6
        mplew.writeShort(0); // 7

        //new143 idk where hai hemmi idk too
        //mplew.writeShort(0); //8 
        mplew.writeShort(0); //9

        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0); // use
        
        
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
        
        
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.write(0);
        
        
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);

        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
            mplew.writeInt(i);
            mplew.writeInt(chr.getExtendedSlot(i));
            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
                if ((item.getPosition() > i * 100 + 100) && (item.getPosition() < i * 100 + 200)) {
                    addItemPosition(mplew, item, false, true);
                    addItemInfo(mplew, item, chr);
                }
            }
            mplew.writeInt(-1);
        }
        mplew.writeZeroBytes(17);//was17
        System.out.println("[OnPacket::InventroyInfo]\r\n"+mplew.toString());
    }

    public static void addPotionPotInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (chr.getPotionPots() == null) {
            mplew.writeInt(0);
            return;
        }
        mplew.writeInt(chr.getPotionPots().size());
        for (MaplePotionPot p : chr.getPotionPots()) {
            mplew.writeInt(p.getId());
            mplew.writeInt(p.getMaxValue());
            mplew.writeInt(p.getHp());
            mplew.writeInt(0);
            mplew.writeInt(p.getMp());

            mplew.writeLong(PacketHelper.getTime(p.getStartDate()));
            mplew.writeLong(PacketHelper.getTime(p.getEndDate()));
        }
    }
    
    public static void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeAsciiString(chr.getName(), 13);
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        chr.getStat().connectData(mplew);
        mplew.writeShort(chr.getRemainingAp());
        if (GameConstants.isSeparatedSp(chr.getJob())) {
            System.out.println(chr.getName() + "has separated SP");
            int size = chr.getRemainingSpSize();
            mplew.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    mplew.write(i + 1);
                    mplew.writeInt(chr.getRemainingSp(i));
                }
            }
        } else {
            mplew.writeShort(chr.getRemainingSp());
        }
        mplew.writeLong(chr.getExp());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getGachExp());
        mplew.writeInt(0); // Migration Data
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeInt(0); //new v148
        mplew.writeShort(chr.getSubcategory());
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
            mplew.writeInt(chr.getFaceMarking());
        }
        mplew.write(chr.getFatigue());
        mplew.writeInt(GameConstants.getCurrentDate());
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeInt(chr.getTrait(t).getTotalExp());
        }
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeShort(0);
        }
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeInt(chr.getStat().pvpExp);
        mplew.write(chr.getStat().pvpRank);
        mplew.writeInt(chr.getBattlePoints());
        mplew.write(5);
        mplew.write(6);
        mplew.writeInt(0);
        addPartTimeJob(mplew, MapleCharacter.getPartTime(chr.getId()));
        chr.getCharacterCard().connectData(mplew);
        mplew.writeReversedLong(getTime(-2L));
    }

    public static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacterLook chr, boolean mega, boolean second) {
        // meage = true second = false
        mplew.write(second ? chr.getSecondGender() : chr.getGender());
        mplew.write(second ? chr.getSecondSkinColor() : chr.getSkinColor());
        if (GameConstants.isLuminous(chr.getJob())) {
            mplew.writeInt(64992);
        }
        mplew.writeInt(second ? chr.getSecondFace() : chr.getFace());
        mplew.writeInt(chr.getJob());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(second ? chr.getSecondHair() : chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> totemEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> equip = second ? chr.getSecondEquips(true) : chr.getEquips(true);
        for (final Entry<Byte, Integer> item : equip.entrySet()) {
            if ((item.getKey()) < -127) {
                continue;
            }
            byte pos = (byte) ((item.getKey()) * -1);

            if ((pos < 100) && (myEquip.get(pos) == null)) {
                myEquip.put(pos, item.getValue());
            } else if ((pos > 100) && (pos != 111)) {
                pos = (byte) (pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getValue());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getValue());
            }
        }
        for (final Entry<Byte, Integer> totem : chr.getTotems().entrySet()) {
            byte pos = (byte) ((totem.getKey()) * -1);
            if (pos < 0 || pos > 2) { //3 totem slots
                continue;
            }
            if (totem.getValue() < 1200000 || totem.getValue() >= 1210000) {
                continue;
            }
            System.out.println(pos);
            System.out.println(totem.getValue());
            totemEquip.put(pos, totem.getValue());
        }

        for (Map.Entry entry : myEquip.entrySet()) {
            int weapon = ((Integer) entry.getValue());
            if (GameConstants.getWeaponType(weapon) == (second ? MapleWeaponType.LONG_SWORD : MapleWeaponType.BIG_SWORD)) {
                continue;
            }
            mplew.write(((Byte) entry.getKey()));
            mplew.writeInt(((Integer) entry.getValue()));
        }
        mplew.write(255);

        for (Map.Entry entry : maskedEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()));
            mplew.writeInt(((Integer) entry.getValue()));
        }
        mplew.write(255);

        for (Map.Entry entry : totemEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()));
            mplew.writeInt(((Integer) entry.getValue()));
        }
        mplew.write(255); //new v140

        Integer cWeapon = equip.get(Byte.valueOf((byte) -111));
        mplew.writeInt(cWeapon != null ? cWeapon : 0);
        Integer Weapon = equip.get(Byte.valueOf((byte) -11));
        mplew.writeInt(Weapon != null ? Weapon : 0);
        boolean zero = GameConstants.isZero(chr.getJob());
        Integer Shield = equip.get(Byte.valueOf((byte) -10));
        mplew.writeInt(!zero && Shield != null ? Shield : 0);
        if (GameConstants.isMercedes(chr.getJob())) {
            mplew.write(1); // mercedes ears enabled for mercedes
        } else {
            mplew.write(0); // mercedes ears disabled for other classes then mercedes
        }
        mplew.writeZeroBytes(12);
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
            mplew.writeInt(chr.getFaceMarking());
        } else if (GameConstants.isZero(chr.getJob())) {
            mplew.write(1);
        }
        if (GameConstants.isBeastTamer(chr.getJob())) {
            mplew.write(1);
            mplew.writeInt(chr.getEars());
            mplew.write(1);
            mplew.writeInt(chr.getTail());
        }
    }

    public static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time) {
        mplew.writeLong(getTime(time));
    }

    public static void addItemPosition(MaplePacketLittleEndianWriter mplew, Item item, boolean trade, boolean bagSlot) {
        if (item == null) {
            mplew.write(0);
            return;
        }
        short pos = item.getPosition();
        if (pos <= -1) {
            pos = (short) (pos * -1);
            if ((pos > 100) && (pos < 1000)) {
                pos = (short) (pos - 100);
            }
        }
        if (bagSlot) {
            mplew.writeInt(pos % 100 - 1);
        } else if ((!trade) && (item.getType() == 1)) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
    }

    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, Item item) {
        addItemInfo(mplew, item, null);
    }

    public static void addItemInfo(final MaplePacketLittleEndianWriter mplew, final Item item, final MapleCharacter chr) {
        mplew.write(item.getPet() != null ? 3 : item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0 && !GameConstants.isMarriageRing(item.getItemId()) && item.getItemId() / 10000 != 166;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        mplew.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) { // Pet
            addPetItemInfo(mplew, item, item.getPet(), true);
        } else {
            addExpirationTime(mplew, item.getExpiration());
            mplew.writeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(item.getItemId()));
            if (item.getType() == 1) {
                final Equip equip = Equip.calculateEquipStats((Equip) item);
                //final Equip equip = Equip.calculateEquipStatsTest((Equip) item);
                addEquipStats(mplew, equip);
                //addEquipStatsTest(mplew, equip);
                addEquipBonusStats(mplew, equip, hasUniqueId);
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                mplew.writeShort(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()) || item.getItemId() / 10000 == 287) {
                    mplew.writeLong(item.getInventoryId() <= 0 ? -1 : item.getInventoryId());
                }
            }
        }
    }

    public static void addEquipStatsTest(MaplePacketLittleEndianWriter mplew, Equip equip) {
        int mask;
        int masklength = 2;
        for (int i = 1; i <= masklength; i++) {
            mask = 0;
            if (equip.getStatsTest().size() > 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getPosition() == i) {
                        mask += stat.getValue();
                    }
                }
            }
            mplew.writeInt(mask);
            if (mask != 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getDatatype() == 8) {
                        mplew.writeLong(equip.getStatsTest().get(stat));
                    } else if (stat.getDatatype() == 4) {
                        mplew.writeInt(equip.getStatsTest().get(stat).intValue());
                    } else if (stat.getDatatype() == 2) {
                        mplew.writeShort(equip.getStatsTest().get(stat).shortValue());
                    } else if (stat.getDatatype() == 1) {
                        mplew.write(equip.getStatsTest().get(stat).byteValue());
                    }
                }
            }
        }
    }

    public static void addEquipStats(MaplePacketLittleEndianWriter mplew, Equip equip) {
        int head = 0;
        if (equip.getStats().size() > 0) {
            for (EquipStat stat : equip.getStats()) {
                head |= stat.getValue();
            }
        }
        mplew.writeInt(head);
        if (head != 0) {
            if (equip.getStats().contains(EquipStat.SLOTS)) {
                mplew.write(equip.getUpgradeSlots());
            }
            if (equip.getStats().contains(EquipStat.LEVEL)) {
                mplew.write(equip.getLevel());
            }
            if (equip.getStats().contains(EquipStat.STR)) {
                mplew.writeShort(equip.getStr());
            }
            if (equip.getStats().contains(EquipStat.DEX)) {
                mplew.writeShort(equip.getDex());
            }
            if (equip.getStats().contains(EquipStat.INT)) {
                mplew.writeShort(equip.getInt());
            }
            if (equip.getStats().contains(EquipStat.LUK)) {
                mplew.writeShort(equip.getLuk());
            }
            if (equip.getStats().contains(EquipStat.MHP)) {
                mplew.writeShort(equip.getHp());
            }
            if (equip.getStats().contains(EquipStat.MMP)) {
                mplew.writeShort(equip.getMp());
            }
            if (equip.getStats().contains(EquipStat.WATK)) {
                mplew.writeShort(equip.getWatk());
            }
            if (equip.getStats().contains(EquipStat.MATK)) {
                mplew.writeShort(equip.getMatk());
            }
            if (equip.getStats().contains(EquipStat.WDEF)) {
                mplew.writeShort(equip.getWdef());
            }
            if (equip.getStats().contains(EquipStat.MDEF)) {
                mplew.writeShort(equip.getMdef());
            }
            if (equip.getStats().contains(EquipStat.ACC)) {
                mplew.writeShort(equip.getAcc());
            }
            if (equip.getStats().contains(EquipStat.AVOID)) {
                mplew.writeShort(equip.getAvoid());
            }
            if (equip.getStats().contains(EquipStat.HANDS)) {
                mplew.writeShort(equip.getHands());
            }
            if (equip.getStats().contains(EquipStat.SPEED)) {
                mplew.writeShort(equip.getSpeed());
            }
            if (equip.getStats().contains(EquipStat.JUMP)) {
                mplew.writeShort(equip.getJump());
            }
            if (equip.getStats().contains(EquipStat.FLAG)) {
                mplew.writeShort(equip.getFlag());
            }
            if (equip.getStats().contains(EquipStat.INC_SKILL)) {
                mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
            }
            if (equip.getStats().contains(EquipStat.ITEM_LEVEL)) {
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
            }
            if (equip.getStats().contains(EquipStat.ITEM_EXP)) {
                mplew.writeLong(equip.getExpPercentage() * 100000); // Item Exp... 10000000 = 100%
            }
            if (equip.getStats().contains(EquipStat.DURABILITY)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.VICIOUS_HAMMER)) {
                mplew.writeInt(equip.getViciousHammer());
            }
            if (equip.getStats().contains(EquipStat.PVP_DAMAGE)) {
                mplew.writeShort(equip.getPVPDamage());
            }
            if (equip.getStats().contains(EquipStat.ENHANCT_BUFF)) {
                mplew.writeShort(equip.getEnhanctBuff());
            }
            if (equip.getStats().contains(EquipStat.DURABILITY_SPECIAL)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.REQUIRED_LEVEL)) {
                mplew.write(equip.getReqLevel());
            }
            if (equip.getStats().contains(EquipStat.YGGDRASIL_WISDOM)) {
                mplew.write(equip.getYggdrasilWisdom());
            }
            if (equip.getStats().contains(EquipStat.FINAL_STRIKE)) {
                mplew.write(equip.getFinalStrike());
            }
            if (equip.getStats().contains(EquipStat.BOSS_DAMAGE)) {
                mplew.write(equip.getBossDamage());
            }
            if (equip.getStats().contains(EquipStat.IGNORE_PDR)) {
                mplew.write(equip.getIgnorePDR());
            }
        } else {
            /*
             *   if ( v3 >= 0 )
             *     v36 = 0;
             *   else
             *     v36 = (unsigned __int8)CInPacket::Decode1(a2);
             */
//            mplew.write(0); //unknown
        }
        addEquipSpecialStats(mplew, equip);
    }

    public static void addEquipSpecialStats(MaplePacketLittleEndianWriter mplew, Equip equip) {
        int head = 0;
        if (equip.getSpecialStats().size() > 0) {
            for (EquipSpecialStat stat : equip.getSpecialStats()) {
                head |= stat.getValue();
            }
        }
        mplew.write(HexTool.getByteArrayFromHexString("04 00 00 00 FF"));
    }

//    public static void addEquipBonusStats(MaplePacketLittleEndianWriter mplew, Equip equip, boolean hasUniqueId) {
//        mplew.writeMapleAsciiString(equip.getOwner());
//        mplew.write(equip.getState()); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
//        mplew.write(equip.getEnhance());
//        mplew.writeShort(equip.getPotential1());
//        mplew.writeShort(equip.getPotential2());
//        mplew.writeShort(equip.getPotential3());
//        mplew.writeShort(equip.getBonusPotential1());
//        mplew.writeShort(equip.getBonusPotential2());
//        mplew.writeShort(equip.getBonusPotential3());
//        mplew.writeShort(equip.getFusionAnvil() % 100000);
//        mplew.writeShort(equip.getSocketState());
//        mplew.writeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
//        mplew.writeShort(equip.getSocket2() % 10000);
//        mplew.writeShort(equip.getSocket3() % 10000);
//        if (!hasUniqueId) {
//            mplew.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
//        }
//        mplew.writeLong(getTime(-2));
//        mplew.writeInt(-1); //?
//        
//    }
    public static void addEquipBonusStats(MaplePacketLittleEndianWriter mplew, Equip equip, boolean hasUniqueId) {
        mplew.writeMapleAsciiString(equip.getOwner());
        mplew.write(equip.getState()); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
        mplew.write(equip.getEnhance());
        mplew.writeShort(equip.getPotential1());
        mplew.writeShort(equip.getPotential2());
        mplew.writeShort(equip.getPotential3());
        mplew.writeShort(equip.getBonusPotential1());
        mplew.writeShort(equip.getBonusPotential2());
        mplew.writeShort(equip.getBonusPotential3());
        mplew.writeShort(equip.getFusionAnvil() % 100000);
        mplew.writeShort(equip.getSocketState());
        mplew.writeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
        mplew.writeShort(equip.getSocket2() % 10000);
        mplew.writeShort(equip.getSocket3() % 10000);
        if (!hasUniqueId) {
            mplew.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
        }
        mplew.writeLong(getTime(-2));
        mplew.writeInt(-1); //?
        // new 142
        mplew.writeLong(0);
        mplew.writeLong(getTime(-2));
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeZeroBytes(6); //v149
    }

    public static void serializeMovementList(MaplePacketLittleEndianWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if ((chr.getPlayerShop() != null) && (chr.getPlayerShop().isOwner(chr)) && (chr.getPlayerShop().getShopType() != 1) && (chr.getPlayerShop().isAvailable())) {
            addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static void addInteraction(MaplePacketLittleEndianWriter mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        mplew.write(shop.getItemId() % 10);
        mplew.write(shop.getSize());
        mplew.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static void addCharacterInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        long mask = 0xFF_FF_FF_FF_FF_FF_FF_FFL; //FF FF FF FF FF FF DF FF v148+
        mplew.writeLong(mask);
        mplew.write(0);
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(0);
        }
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(0);

        if ((mask & 1) != 0) {
            addCharStats(mplew, chr);
            mplew.write(chr.getBuddylist().getCapacity());
            mplew.write(chr.getBlessOfFairyOrigin() != null);
            if (chr.getBlessOfFairyOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
            }
            mplew.write(chr.getBlessOfEmpressOrigin() != null);
            if (chr.getBlessOfEmpressOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
            }
            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
            mplew.write((ultExplorer != null) && (ultExplorer.getCustomData() != null));
            if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
                mplew.writeMapleAsciiString(ultExplorer.getCustomData());
            }
        }
        if ((mask & 2) != 0) {
            addMoneyInfo(mplew, chr);
        }
        if ((mask & 8) != 0) {
            addInventoryInfo(mplew, chr);
        }
        if ((mask & 0x100) != 0) {
            addSkillInfo(mplew, chr);
        }
        if ((mask & 0x8000) != 0) {
            addCoolDownInfo(mplew, chr);
        }
        if ((mask & 0x200) != 0) {
            addStartedQuestInfo(mplew, chr);
        }
        if ((mask & 0x4000) != 0) {
            addCompletedQuestInfo(mplew, chr);
        }
        if ((mask & 0x400) != 0) {
            //mplew.writeShort(0);
            addUnk400Info(mplew, chr);
        }
        if ((mask & 0x800) != 0) {
            addRingInfo(mplew, chr);
        }
        if ((mask & 0x1000) != 0) {
            addRocksInfo(mplew, chr);
        }
        if ((mask & 0x20000) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 0x10000) != 0) {
            addMonsterBookInfo(mplew, chr);
        }
        //mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 64 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 DF 20 D9 C5 83 CF 01 00 00 00 00 CD 02 00 00 66 31 00 00 04 00 00 00 00 00 00 00 75 96 8F 00 00 00 00 00 76 96 8F 00 00 00 00 00 77 96 8F 00 00 00 00 00 78 96 8F 00 00 00 00 00"));
        mplew.writeShort(0);
        mplew.writeShort(0);
        if ((mask & 0x40000) != 0) {
                chr.QuestInfoPacket(mplew);
                mplew.writeShort(0);//new143//removed for BT v148
        }
        if ((mask & 0x200000) != 0) {
            if ((chr.getJob() >= 3300) && (chr.getJob() <= 3312)) {
                addJaguarInfo(mplew, chr);
            }
        }
        if (GameConstants.isZero(chr.getJob())) {
            addZeroInfo(mplew, chr);
        }
        mplew.writeShort(0);
        mplew.writeShort(0);

        if ((mask & 0x10000000) != 0) {
            addStealSkills(mplew, chr);
        }
        if ((mask & 0x80000000) != 0) {
            addAbilityInfo(mplew, chr);
        }
        mplew.writeInt(0); //new v134
        mplew.write(0);
        mplew.writeLong(0);//new v148
        mplew.writeShort(0);
        
        addHonorInfo(mplew, chr);
        if (GameConstants.isAngelicBuster(chr.getJob())) {
            mplew.writeInt(1);
            mplew.writeInt(21173); //face
            mplew.writeInt(37141); //hair
            mplew.writeInt(1051291); // dressup suit cant unequip
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
        } else {
            mplew.writeLong(1);
            mplew.writeZeroBytes(17); // Was 17 Before
        }
        mplew.writeLong(getTime(-2));

        addEvolutionInfo(mplew, chr);
        mplew.writeZeroBytes(3);//new 144
        mplew.writeZeroBytes(6); // v149
        mplew.write(0); //farm monsters length
        addFarmInfo(mplew, chr.getClient(), 0);
        mplew.writeInt(5);//v148
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeLong(getTime(-2));
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeShort(1);
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(100);//v149 changed to 100
        mplew.writeLong(getTime(-2));
        mplew.writeInt(0);
        mplew.writeLong(0);//new v146
        mplew.write(0);//new v146
        if ((mask & 0x2000) != 0) {
            addCoreAura(mplew, chr);
        }
        mplew.writeShort(0); //00 9A
        mplew.writeInt(chr.getClient().getAccID()); //22 71 08 D3
        mplew.writeInt(chr.getId()); //D5 69 00 00
        mplew.writeInt(4);
        mplew.writeInt(0); //00 00 00 75
        addRedLeafInfo(mplew, chr);
        
    }

    public static int getSkillBook(final int i) {
        return i == 1 || i == 2 ? 4 : i == 3 ? 3 : i == 4 ? 2 : 0;
//        switch (i) {
//            case 1:
//            case 2:
//                return 4;
//            case 3:
//                return 3;
//            case 4:
//                return 2;
//            default:
//                return 0;
//        }
    }

    public static void addAbilityInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        final List<InnerSkillValueHolder> skills = chr.getInnerSkills();
        final int skills_size = skills.size();
        mplew.writeShort(skills_size == 0 ? 0 : skills_size); //TODO: change the zero to the actually thing..
        for (int i = 0; i < skills.size(); ++i) {
            mplew.write(i + 1); // key
            mplew.writeInt(skills.get(i).getSkillId()); //d 7000000 id ++, 71 = char cards
            mplew.write(skills.get(i).getSkillLevel()); // level
            mplew.write(skills.get(i).getRank()); //rank, C, B, A, and S
        }

    }

    public static void addHonorInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getHonorLevel()); //honor lvl
        mplew.writeInt(chr.getHonourExp()); //honor exp
    }

    public static void addEvolutionInfo(final MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0); //was 0 || 1
        mplew.writeShort(0);
    }

    public static void addCoreAura(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        MapleCoreAura aura = chr.getCoreAura();
        mplew.writeInt(aura.getId()); //nvr change
        mplew.writeInt(chr.getId());
        int level = chr.getSkillLevel(80001151) > 0 ? chr.getSkillLevel(80001151) : chr.getSkillLevel(1214);
        mplew.writeInt(level);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(aura.getExpire());//timer
        mplew.writeInt(0);
        mplew.writeInt(aura.getAtt());//wep att
        mplew.writeInt(aura.getDex());//dex
        mplew.writeInt(aura.getLuk());//luk
        mplew.writeInt(aura.getMagic());//magic att
        mplew.writeInt(aura.getInt());//int
        mplew.writeInt(aura.getStr());//str
        mplew.writeInt(aura.getTotal());//max
        mplew.writeLong(getTime(System.currentTimeMillis() + 86400000L));
        mplew.write(0);
        mplew.write(0);
    }

    public static void addStolenSkills(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, int jobNum, boolean writeJob) {
        if (writeJob) {
            mplew.writeInt(jobNum);
        }
        int count = 0;
        if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobNum) {
                    mplew.writeInt(sk.left);
                    count++;
                    if (count >= GameConstants.getNumSteal(jobNum)) {
                        break;
                    }
                }
            }
        }
        while (count < GameConstants.getNumSteal(jobNum)) { //for now?
            mplew.writeInt(0);
            count++;
        }
    }

    public static void addChosenSkills(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
                for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == i && sk.right) {
                        mplew.writeInt(sk.left);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                mplew.writeInt(0);
            }
        }
    }

    public static void addStealSkills(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            addStolenSkills(mplew, chr, i, false); // 52
        }
        addChosenSkills(mplew, chr); // 16
    }

    public static void addMonsterBookInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(mplew);
        } else {
            chr.getMonsterBook().writeUnfinished(mplew);
        }

        mplew.writeInt(chr.getMonsterBook().getSet());
    }

    public static void addPetItemInfo(MaplePacketLittleEndianWriter mplew, Item item, MaplePet pet, boolean active) {
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeInt(-1);
        mplew.writeAsciiString(pet.getName(), 13);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeShort(0);
        mplew.writeShort(pet.getFlags());
        mplew.writeInt((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        mplew.writeShort(0);
        mplew.write(active ? 0 : pet.getSummoned() ? pet.getSummonedValue() : 0);
        for (int i = 0; i < 4; i++) {
            mplew.write(0);
        }
        mplew.writeInt(-1); //new v140
        mplew.writeShort(100); //new v140
    }

    public static void addShopInfo(MaplePacketLittleEndianWriter mplew, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(shop.getRanks().size() > 0 ? 1 : 0);

        if (shop.getRanks().size() > 0) {
            mplew.write(shop.getRanks().size());
            for (Pair s : shop.getRanks()) {
                mplew.writeInt(((Integer) s.left));
                mplew.writeMapleAsciiString((String) s.right);
            }
        }
        mplew.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(mplew, item, shop, ii, null, c.getPlayer());
        }
        for (Item i : c.getPlayer().getRebuy()) {
            addShopItemInfo(mplew, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity(), i.getPosition()), shop, ii, i, c.getPlayer());
        }
    }

    /*
     * Categories:
     * 0 - No Tab
     * 1 - Equip
     * 2 - Use
     * 3 - Setup
     * 4 - Etc
     * 5 - Recipe
     * 6 - Scroll
     * 7 - Special
     * 8 - 8th Anniversary
     * 9 - Button
     * 10 - Invitation Ticket
     * 11 - Materials
     * 12 - Maple
     * 13 - Homecoming
     * 14 - Cores
     * 80 - JoeJoe
     * 81 - Hermoninny
     * 82 - Little Dragon
     * 83 - Ika
     */
    public static void addShopItemInfo(MaplePacketLittleEndianWriter mplew, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item i, MapleCharacter chr) {
        mplew.writeInt(item.getItemId());
        mplew.writeInt(item.getPrice());
        mplew.write(0); //Discount
        mplew.writeInt(item.getReqItem());
        mplew.writeInt(item.getReqItemQ());
        mplew.writeInt(1440 * item.getExpiration());
        mplew.writeInt(item.getMinLevel());
        mplew.writeInt(0);
        //mplew.writeLong(0L);//new v148
        //mplew.writeInt(0);//new v148
        mplew.writeLong(getTime(-2L)); //new v140 1900
        mplew.writeLong(getTime(-1L)); //new v140 2079
        mplew.writeInt(item.getCategory());
        if (GameConstants.isEquip(item.getItemId())) {
            mplew.write(item.hasPotential() ? 1 : 0);
        } else {
            mplew.write(0);
        }
        mplew.writeInt(item.getExpiration() > 0 ? 1 : 0);
        mplew.write(0);//new 144
        if ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId()))) {
            mplew.writeShort(item.getQuantity()); //quantity of item to buy
            mplew.writeShort(item.getBuyable()); //buyable
        } else {
            mplew.writeAsciiString("333333");
            mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
//            mplew.writeShort(ItemInformation.getInstance().getSlotMax(c, item.getItemId()));
            mplew.writeShort(ii.getSlotMax(item.getItemId()));
            /*
             mplew.writeInt(0);
             mplew.writeShort(0);
             mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
             */
//            mplew.writeZeroBytes(8);
//            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        }

        mplew.write(i == null ? 0 : 1);
        if (i != null) {
            addItemInfo(mplew, i);
        }
        if (shop.getRanks().size() > 0) {
            mplew.write(item.getRank() >= 0 ? 1 : 0);
            if (item.getRank() >= 0) {
                mplew.write(item.getRank());
            }
        }
        for (int j = 0; j < 4; j++) {
            mplew.writeInt(0); //red leaf high price probably
        }
        addRedLeafInfo(mplew, chr);
    }

    public static void addJaguarInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(chr.getIntNoRecord(GameConstants.JAGUAR));
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(0);
        }
    }

    public static void addZeroInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        short mask = 0;
        mplew.writeShort(mask);
        if ((mask & 1) != 0) {
            mplew.write(0); //bool
        }
        if ((mask & 2) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 4) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 8) != 0) {
            mplew.write(0);
        }
        if ((mask & 10) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 20) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 40) != 0) {
            mplew.writeInt(0);
        }
        if (mask < 0) {
            mplew.writeInt(0);
        }
        if ((mask & 100) != 0) {
            mplew.writeInt(0);
        }
    }

    public static void addBeastTamerInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        int beast = GameConstants.isBeastTamer(chr.getJob()) ? 1 : 0;
        String ears = Integer.toString(chr.getEars());
        String tail = Integer.toString(chr.getTail());

        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 16 00 0D 47 14 00 65 54 69 6D 65 3D 31 32 2F 31 32 2F 33 31 2F 30 30 2F 30 30 1D 47 06 00 73 74 65 70 3D 30 B6 46 07 00 63 6F 75 6E 74 3D 30 87 46 1E 00 52 47 3D 30 3B 53 4D 3D 30 3B 41 4C 50 3D 30 3B 44 42 3D 30 3B 43 44 3D 30 3B 4D 48 3D 30 16 47 31 00 52 48 3D 30 3B 47 54 3D 30 3B 57 4D 3D 30 3B 46 41 3D 30 3B 45 43 3D 30 3B 43 48 3D 30 3B 4B 44 3D 30 3B 49 4B 3D 30 3B 50 44 3D 30 3B 50 46 3D 30 9F 46 1C 00 69 6E 64 65 78 3D 31 3B 6C 61 73 74 52 3D 31 33 2F 31 32 2F 31 31 3B 73 6E 31 3D 30 A0 46 05 00 6E 75 6D 3D 30 A4 E7 2B 00"));
        mplew.writeAsciiString("bTail=" + beast + ";");
        mplew.writeAsciiString("bEar=" + beast + ";");
        mplew.writeAsciiString("TailID=" + tail + ";");
        mplew.writeAsciiString("EarID=" + ears);
        mplew.write(HexTool.getByteArrayFromHexString("40 47 2F 00 63 6F 75 6E 74 3D 30 3B 64 6F 31 3D 30 3B 64 6F 32 3D 30 3B 64 61 69 6C 79 46 50 3D 30 3B 6C 61 73 74 44 61 74 65 3D 32 30 31 34 30 33 32 39 10 47 06 00 76 61 6C 32 3D 30 B9 46 1B 00 64 3D 32 30 31 34 30 32 31 39 3B 69 3D 31 32 31 31 32 31 30 30 30 30 30 30 30 30 9A 46 1A 00 63 6F 75 6E 74 30 3D 31 3B 63 6F 75 6E 74 31 3D 31 3B 63 6F 75 6E 74 32 3D 31 22 47 17 00 63 6F 6D 70 3D 31 3B 69 3D 32 33 30 30 30 30 30 30 30 30 30 30 30 30 0A 47 03 00 45 3D 31 12 47 40 00 4D 4C 3D 30 3B 4D 4D 3D 30 3B 4D 41 3D 30 3B 4D 42 3D 30 3B 4D 43 3D 30 3B 4D 44 3D 30 3B 4D 45 3D 30 3B 4D 46 3D 30 3B 4D 47 3D 30 3B 4D 48 3D 30 3B 4D 49 3D 30 3B 4D 4A 3D 30 3B 4D 4B 3D 30 FA 46 20 00 63 6F 75 6E 74 3D 35 3B 74 69 6D 65 3D 32 30 31 33 2F 31 32 2F 31 31 20 31 31 3A 35 34 3A 30 30 23 47 09 00 62 41 74 74 65 6E 64 3D 30 B4 46 07 00 63 6F 75 6E 74 3D 30 85 46 17 00 31 3D 30 3B 32 3D 30 3B 33 3D 30 3B 34 3D 30 3B 35 3D 30 3B 36 3D 30 2C 47 07 00 4C 6F 67 69 6E 3D 31 64 47 04 00 41 51 3D 30 B5 46 07 00 63 6F 75 6E 74 3D 30"));
    }

    public static void addFarmInfo(MaplePacketLittleEndianWriter mplew, MapleClient c, int idk) {
        mplew.writeMapleAsciiString(c.getFarm().getName());
        mplew.writeInt(c.getFarm().getWaru());
        mplew.writeInt(c.getFarm().getLevel());
        mplew.writeInt(c.getFarm().getExp());
        mplew.writeInt(c.getFarm().getAestheticPoints());
        mplew.writeInt(0); //gems 

        mplew.write((byte) idk);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(1);
    }

    public static void addRedLeafInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        for (int i = 0; i < 4; i++) {
            mplew.writeInt(9410165 + i);//v146 -2
            mplew.writeInt(chr.getFriendShipPoints()[i]);
        }
    }

    public static void addLuckyLogoutInfo(MaplePacketLittleEndianWriter mplew, boolean enable, CashItem item0, CashItem item1, CashItem item2) {
        mplew.writeInt(enable ? 1 : 0);
        if (enable) {
            CSPacket.addCSItemInfo(mplew, item0);
            CSPacket.addCSItemInfo(mplew, item1);
            CSPacket.addCSItemInfo(mplew, item2);
        }
    }

    public static void addPartTimeJob(MaplePacketLittleEndianWriter mplew, PartTimeJob parttime) {
        mplew.write(parttime.getJob());
        mplew.writeReversedLong(getTime(System.currentTimeMillis()));
        mplew.writeInt(parttime.getReward());
        mplew.write(parttime.getReward() > 0);
    }

    public static <E extends Buffstat> void writeSingleMask(MaplePacketLittleEndianWriter mplew, E statup) {
        for (int i = GameConstants.MAX_BUFFSTAT; i >= 1; i--) {
            mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }

    public static <E extends Buffstat> void writeMask(MaplePacketLittleEndianWriter mplew, Collection<E> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        //if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
        //    mask = new int[11];
        //}
        for (Buffstat statup : statups) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        //if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
        //    mask = new int[11];
        //}
        for (Pair statup : statups) {
            mask[(((Buffstat) statup.left).getPosition() - 1)] |= ((Buffstat) statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Map<E, Integer> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        //if (!statups.containsKey(MapleBuffStat.MONSTER_RIDING)) {
        //    mask = new int[11];
        //}
        for (Buffstat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }
}
