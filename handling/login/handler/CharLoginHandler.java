package handling.login.handler;

import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import clientside.MapleCharacter;
import clientside.MapleCharacterUtil;
import clientside.MapleClient;
import clientside.PartTimeJob;
import clientside.Skill;
import clientside.SkillEntry;
import clientside.SkillFactory;
import constants.GameConstants;
import constants.JobConstants;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.TespiaWorldOption;
import constants.WorldConstants.WorldOption;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.World;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;
import tools.packet.PacketHelper;

public class CharLoginHandler {

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 4;
    }

    public static void handleAuthRequest(final LittleEndianAccessor slea, final MapleClient c) {
        //System.out.println("Sending response to client.");
        int request = slea.readInt();
        int response;

        response = ((request >> 5) << 5) + (((((request & 0x1F) >> 3) ^ 2) << 3) + (7 - (request & 7)));
        response |= ((request >> 7) << 7);
        response -= 2; //-1 again on v143 ~ changed to -2 in v149

        c.getSession().write(LoginPacket.sendAuthResponse(response));
    }

    public static final void login(final LittleEndianAccessor slea, final MapleClient c) {
        String pwd = slea.readMapleAsciiString();
        String login = slea.readMapleAsciiString().replace("NP12:auth06:5:0:","");

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.login(login, pwd, ipBan || macBan);
        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.getSession().write(LoginPacket.getLoginFailed(loginok));
            } else {
                c.getSession().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.getSession().write(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.getSession().close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static void ServerListRequest(final MapleClient c) {      
        List<Triple<String, Integer, Boolean>> backgrounds = new LinkedList<>(); //boolean for randomize
        backgrounds.addAll(Arrays.asList(ServerConstants.backgrounds));
        c.getSession().write(LoginPacket.changeBackground(backgrounds));

        if (ServerConstants.TESPIA) {
            for (TespiaWorldOption tespiaservers : TespiaWorldOption.values()) {
                if (TespiaWorldOption.getById(tespiaservers.getWorld()).show() && TespiaWorldOption.getById(tespiaservers.getWorld()) != null) {
                    c.getSession().write(LoginPacket.getServerList(Integer.parseInt(tespiaservers.getWorld().replace("t", "")), LoginServer.getLoad()));
                }
            }
        } else {
            for (WorldOption servers : WorldOption.values()) {
                if (WorldOption.getById(servers.getWorld()).show() && servers != null) {
                    c.getSession().write(LoginPacket.getServerList(servers.getWorld(), LoginServer.getLoad()));
                }
            }
        }
        c.getSession().write(LoginPacket.getEndOfServerList());
        c.getSession().write(LoginPacket.sendRecommended(WorldOption.recommended, WorldOption.recommendedmsg));
    }

    public static void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }

    public static void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        slea.readByte(); //2?
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        if (!World.isChannelAvailable(channel, server) || !WorldOption.isExists(server)) {
            c.getSession().write(LoginPacket.getLoginFailed(10)); //cannot process so many
            return;
        }

        if (!WorldOption.getById(server).isAvailable() && !(c.isGm() && server == WorldConstants.gmserver)) {
            c.getSession().write(CWvsContext.broadcastMsg(1, "We are sorry, but " + WorldConstants.getNameById(server) + " is currently not available. \r\nPlease try another world."));
            c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, but it is used to unstuck
            return;
        }

        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            //c.getSession().write(LoginPacket.getSecondAuthSuccess(c));
            //c.getSession().write(LoginPacket.getChannelSelected());
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword(), chars));
        } else {
            c.getSession().close();
        }
    }

    public static void updateCCards(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() != 36 || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = slea.readInt();
            if ((!c.login_Auth(charId) && charId != 0) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
                c.getSession().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    public static void CheckCharName(final String name, final MapleClient c) {
        LoginInformationProvider li = LoginInformationProvider.getInstance();
        boolean nameUsed = true;
        if (MapleCharacterUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.getSession().write(LoginPacket.charNameResponse(name, nameUsed));
    }

    public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        String name;
        byte gender, skin, unk;
        short subcategory;
        int keysettings;
        int face, hair, hairColor = -1, hat = -1, top, bottom = -1, shoes, weapon, cape = -1, faceMark = -1, ears = -1, tail = -1, shield = -1;
        JobType job;
        name = slea.readMapleAsciiString();
        if (!MapleCharacterUtil.canCreateChar(name, false)) {
            System.out.println("char name hack: " + name);
            return;
        }
        keysettings = slea.readInt(); // new in v149
        slea.readInt(); //-1
        int job_type = slea.readInt();
        job = JobType.getByType(job_type);
        if (job == null) {
            System.out.println("New job type found: " + job_type);
            return;
        }
        for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
            if (j.getJobType() == job_type) {
                if (j.getFlag() != JobConstants.LoginJob.JobFlag.ENABLED.getFlag()) {
                    System.out.println("job was tried to be created while not enabled");
                    return;
                }
            }
        }
        subcategory = slea.readShort();
        gender = slea.readByte();
        skin = slea.readByte();
        unk = slea.readByte(); //6/7/8/9
        face = slea.readInt();
        hair = slea.readInt();

        if (job.hairColor) {
            hairColor = slea.readInt();
        }
        if (job.skinColor) {
            slea.readInt();
        }
        if (job.faceMark) {
            faceMark = slea.readInt();
        }
        if (job.ears) {
            ears = slea.readInt();
        }
        if (job.tail) {
            tail = slea.readInt();
        }
        if (job.hat) {
            hat = slea.readInt();
        }
        top = slea.readInt();
        if (job.bottom) {
            bottom = slea.readInt();
        }
        if (job.cape) {
            cape = slea.readInt();
        }
        shoes = slea.readInt();
        weapon = slea.readInt();
        if (slea.available() >= 4) {
            shield = slea.readInt();
        }
        int index = 0;
        boolean noSkin = job == JobType.Demon || job == JobType.Mercedes || job == JobType.Jett;
        int[] items = new int[]{face, hair, hairColor, noSkin ? -1 : skin, faceMark, hat, top, bottom, cape, shoes, weapon, shield};
        //for (int i : items) {
        //  if (i > -1) {
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, index, job.type, i)) {
        //  System.out.println(gender + " | " + index + " | " + job.type + " | " + i);
        //  return;
        //   }
        //     index++;
        // }
        //        
        MapleCharacter newchar = MapleCharacter.getDefault(c, job);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face);
        newchar.setSecondFace(face);
        if (hairColor < 0) {
            hairColor = 0;
        }
        if (job != JobType.Mihile) {
            hair += hairColor;
        }
        newchar.setHair(hair);
        newchar.setSecondHair(hair);
        if (job == JobType.AngelicBuster) {
            newchar.setSecondFace(21173);
            newchar.setSecondHair(37141);
        } else if (job == JobType.Zero) {
            newchar.setSecondFace(21290);
            newchar.setSecondHair(37623);
        }
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skin);
        if (faceMark < 0) {
            faceMark = 0;
        }
        newchar.setFaceMarking(faceMark);
        int[] wrongEars = {1004062, 1004063, 1004064};
        int[] correctEars = {5010116, 5010117, 5010118};
        int[] wrongTails = {1102661, 1102662, 1102663};
        int[] correctTails = {5010119, 5010120, 5010121};
        for (int i = 0; i < wrongEars.length; i++) {
            if (ears == wrongEars[i]) {
                ears = correctEars[i];
            }
        }
        for (int i = 0; i < wrongTails.length; i++) {
            if (tail == wrongTails[i]) {
                tail = correctTails[i];
            }
        }
        if (ears < 0) {
            ears = 0;
        }
        newchar.setEars(ears);
        if (tail < 0) {
            tail = 0;
        }
        newchar.setTail(tail);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        Item item;
        //-1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Topwear 
        //-6 Bottom | -7 Shoes | -9 Cape | -10 Shield | -11 Weapon
        //todo check zero's beta weapon slot
        int[][] equips = new int[][]{{hat, -1}, {top, -5}, {bottom, -6}, {cape, -9}, {shoes, -7}, {weapon, -11}, {shield, -10}};
        for (int[] i : equips) {
            if (i[0] > 0) {
                item = li.getEquipById(i[0]);
                item.setPosition((byte) i[1]);
                item.setGMLog("Character Creation");
                equip.addFromDB(item);
            }
        }
        // Additional skills for all first job classes. Some skills are not added by default,
        // so adding the skill ID here between the {}, will give the skills you entered to the desired job.
        int[][] skills = new int[][]{
            {80001152},//Resistance
            {80001152, 1281},//Explorer
            {10001244, 10000252, 80001152},//Cygnus
            {20000194},//Aran
            {20010022, 20010194},//Evan
            {20020109, 20021110, 20020111, 20020112}, //Mercedes
            {},//Demon
            {},//Phantom
            {},//Dualblade
            {50001214},//Mihile
            {20040216, 20040217, 20040218, 20040219, 20040220, 20040221, 20041222},//Luminous
            {},//Kaiser
            {60011216, 60010217, 60011218, 60011219, 60011220, 60011221, 60011222},//AngelicBuster
            {},//Cannoneer
            {30020232, 30020233, 30020234, 30020240, 30021238},//Xenon
            {100000279, 100000282, 100001262, 100001263, 100001264, 100001265, 100001266, 100001268},//Zero
            {228, 80001151},//Jett
            {},//Hayato
            {40020000, 40020001, 40020002, 40021023, 40020109},//Kanna
            {80001152, 110001251}//BeastTamer
        };
        if (skills[job.type].length > 0) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            Skill s;
            for (int i : skills[job.type]) {
                s = SkillFactory.getSkill(i);
                int maxLevel = s.getMaxLevel();
                if (maxLevel < 1) {
                    maxLevel = s.getMasterLevel();
                }
                ss.put(s, new SkillEntry((byte) 1, (byte) maxLevel, -1));
            }
            if (job == JobType.Zero) {
                ss.put(SkillFactory.getSkill(101000103), new SkillEntry((byte) 8, (byte) 10, -1));
                ss.put(SkillFactory.getSkill(101000203), new SkillEntry((byte) 8, (byte) 10, -1));
            }
            if (job == JobType.BeastTamer) {
                ss.put(SkillFactory.getSkill(110001511), new SkillEntry((byte) 0, (byte) 30, -1));
                ss.put(SkillFactory.getSkill(110001512), new SkillEntry((byte) 0, (byte) 5, -1));
                ss.put(SkillFactory.getSkill(110000513), new SkillEntry((byte) 0, (byte) 30, -1));
                ss.put(SkillFactory.getSkill(110000515), new SkillEntry((byte) 0, (byte) 10, -1));
            }
            newchar.changeSkillLevel_Skip(ss, false);
        }
        int[][] guidebooks = new int[][]{{4161001, 0}, {4161047, 1}, {4161048, 2000}, {4161052, 2001}, {4161054, 3}, {4161079, 2002}};
        int guidebook = 0;
        for (int[] i : guidebooks) {
            if (newchar.getJob() == i[1]) {
                guidebook = i[0];
            } else if (newchar.getJob() / 1000 == i[1]) {
                guidebook = i[0];
            }
        }
        if (guidebook > 0) {
            newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(guidebook, (byte) 0, (short) 1, (byte) 0));
        }

        if (job == JobType.Phantom) {
            newchar.getStat().maxhp = 850;
            newchar.getStat().hp = 140;
            newchar.getStat().maxmp = 805;
            newchar.getStat().mp = 38;
        }

        if (job == JobType.Zero) {
            newchar.setLevel((short) 100);
            newchar.getStat().str = 518;
            newchar.getStat().maxhp = 6910;
            newchar.getStat().hp = 6910;
            newchar.getStat().maxmp = 100;
            newchar.getStat().mp = 100;
            newchar.setRemainingSp(3, 0); //alpha
            newchar.setRemainingSp(3, 1); //beta
        }

        if (job == JobType.BeastTamer) {
            newchar.setLevel((short) 10);
            newchar.getStat().maxhp = 567;
            newchar.getStat().hp = 551;
            newchar.getStat().maxmp = 270;
            newchar.getStat().mp = 263;
            newchar.setRemainingAp(45);
            newchar.setRemainingSp(3, 0);
        }

        /*Item eq_top = MapleItemInformationProvider.getInstance().getEquipById(1042180);
        eq_top.setPosition((byte) -5);
        equip.addFromDB(eq_top);
        if (newchar.getGender() == 0) {
            Item eq_bottom = MapleItemInformationProvider.getInstance().getEquipById(1060138);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
        } else if (newchar.getGender() == 1) {
            Item eq_bottom = MapleItemInformationProvider.getInstance().getEquipById(1061161);
            eq_bottom.setPosition((byte) -6);
            equip.addFromDB(eq_bottom);
        }

        Item eq_shoes = MapleItemInformationProvider.getInstance().getEquipById(1072678);
        eq_shoes.setPosition((byte) -7);
        equip.addFromDB(eq_shoes);
        Item eq_weapon = MapleItemInformationProvider.getInstance().getEquipById(1302000);
        eq_weapon.setPosition((byte) -11);
        equip.addFromDB(eq_weapon);
        Item pHat = MapleItemInformationProvider.getInstance().getEquipById(1003104);
        pHat.setPosition((byte) -101);
        equip.addFromDB(pHat);*/

        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) && (c.isGm() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, job, subcategory);
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
            //newchar.newCharRewards();
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isGM() && (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || c.getPlayer().getQuestStatus(20734) != 0 || c.getPlayer().getQuestStatus(20616) != 2 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld()))) {
            c.getSession().write(CField.createUltimate(2));
            //Character slots are full. Please purchase another slot from the Cash Shop.
            return;
        }
        //System.out.println(slea.toString());
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt(); //job ID

        final int face = slea.readInt();
        final int hair = slea.readInt();

        //No idea what are these used for:
        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();

        //JobType errorCheck = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, errorCheck.type, face)) {
        //    c.getSession().write(CWvsContext.enableActions());
        //    return;
        //}
        JobType jobType = JobType.UltimateAdventurer;

        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld(c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                break;
            default:
                return;
        }

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        //TODO: Make this GMS - Like
        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }

        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 200, (byte) 0));
        if (MapleCharacterUtil.canCreateChar(name, c.isGm()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm())) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.getSession().write(CField.createUltimate(0));
        } else if (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGm()) {
            c.getSession().write(CField.createUltimate(3)); //"You cannot use this name."
        } else {
            c.getSession().write(CField.createUltimate(1));
        }
    }

    public static void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        String Secondpw_Client = GameConstants.GMS ? slea.readMapleAsciiString() : null;
        if (Secondpw_Client == null) {
            if (slea.readByte() > 0) { // Specific if user have second password or not
                Secondpw_Client = slea.readMapleAsciiString();
            }
            slea.readMapleAsciiString();
        }

        final int Character_ID = slea.readInt();

        if (!c.login_Auth(Character_ID) || !c.isLoggedIn() || loginFailCount(c)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (Secondpw_Client == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(Secondpw_Client)) { // Wrong Password
                    state = 20;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static void Character_WithoutSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean haspic, final boolean view) {
        slea.readByte(); // 1?
        slea.readByte(); // 1?
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        final String currentpw = c.getSecondPassword();
        if (!c.isLoggedIn() || loginFailCount(c) || (currentpw != null && (!currentpw.equals("") || haspic)) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
            c.getSession().close();
            return;
        }
        c.updateMacs(slea.readMapleAsciiString());
        slea.readMapleAsciiString();
        if (slea.available() != 0) {
            final String setpassword = slea.readMapleAsciiString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
                return;
            }
        } else if (haspic) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        c.getSession().write(LoginPacket.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }

    public static void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
            c.getSession().close();
            return;
        }
        c.updateMacs(slea.readMapleAsciiString());

        if (c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16 || c.isGm()) {

            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }

            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
            c.getSession().write(LoginPacket.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.getSession().write(LoginPacket.secondPwError((byte) 0x14));
        }
    }

    public static void partTimeJob(final LittleEndianAccessor slea, final MapleClient c) {
        System.out.println("[Part Time Job] data: " + slea);
        byte mode = slea.readByte(); //1 = start 2 = end
        int cid = slea.readInt(); //character id
        byte job = slea.readByte(); //part time job
        if (mode == 0) {
            LoginPacket.partTimeJob(cid, (byte) 0, System.currentTimeMillis());
        } else if (mode == 1) {
            LoginPacket.partTimeJob(cid, job, System.currentTimeMillis());
        }
    }

    public static void PartJob(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() != null || !c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        final byte mode = slea.readByte();
        final int cid = slea.readInt();
        if (mode == 1) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            final byte job = slea.readByte();
            if (/*chr.getLevel() < 30 || */job < 0 || job > 5 || partTime.getReward() > 0
                    || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.getSession().close();
                return;
            }
            partTime.setTime(System.currentTimeMillis());
            partTime.setJob(job);
            c.getSession().write(LoginPacket.updatePartTimeJob(partTime));
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            if (/*chr.getLevel() < 30 || */partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.getSession().close();
                return;
            }
            final long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
            c.getSession().write(LoginPacket.updatePartTimeJob(partTime));
        }
    }
}
