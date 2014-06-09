package handling.channel.handler;

import clientside.MapleCharacter;
import clientside.MapleClient;
import clientside.MapleQuestStatus;
import clientside.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.CashShopOperation;
import handling.channel.ChannelServer;
import handling.farm.FarmServer;
import handling.farm.handler.FarmOperation;
import handling.login.LoginServer;
import handling.world.*;
import handling.world.exped.MapleExpedition;
import handling.world.guild.*;
import java.util.ArrayList;
import java.util.List;
import scripting.NPCScriptManager;
import server.*;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.FarmPacket;
import tools.packet.JobPacket.AvengerPacket;

public class InterServerHandler {

    public static void EnterCS(final MapleClient c, final MapleCharacter chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(chr.getId());
            if (farmtransfer != null) {
                c.getSession().write(FarmPacket.farmMessage("You cannot move into Cash Shop while visiting your farm, yet."));
            }
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.getSession().write(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static void EnterFarm(final MapleClient c, final MapleCharacter chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -30);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.getSession().write(CField.getChannelChange(c, Integer.parseInt(FarmServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static void Loggedin(final int playerid, final MapleClient c) {
        try {
            MapleCharacter player;
            CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
            if (transfer != null) {
//            c.getSession().write(CWvsContext.BuffPacket.cancelBuff());
                CashShopOperation.EnterCS(transfer, c);
                return;
            }
            CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(playerid);
            if (farmtransfer != null) {
                FarmOperation.EnterFarm(farmtransfer, c);
                return;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                transfer = cserv.getPlayerStorage().getPendingCharacter(playerid);
                if (transfer != null) {
                    c.setChannel(cserv.getChannel());
                    break;
                }
            }

            if (transfer == null) { // Player isn't in storage, probably isn't CC
                Triple<String, String, Integer> ip = LoginServer.getLoginAuth(playerid);
                String s = c.getSessionIPAddress();
                if (ip == null || !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.left)) {
                    if (ip != null) {
                        LoginServer.putLoginAuth(playerid, ip.left, ip.mid, ip.right);
                    }
                    c.getSession().close();
                    return;
                }
                c.setTempIP(ip.mid);
                c.setChannel(ip.right);
                player = MapleCharacter.loadCharFromDB(playerid, c, true);
            } else {
                player = MapleCharacter.ReconstructChr(transfer, c, true);
            }
            final ChannelServer channelServer = c.getChannelServer();
            c.setPlayer(player);
            c.setAccID(player.getAccountID());

            if (!c.CheckIPAddress()) { // Remote hack
                c.getSession().close();
                return;
            }
            final int state = c.getLoginState();
            boolean allowLogin = false;
            if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOTLOGGEDIN) {
                allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
            }
            if (!allowLogin) {
                c.setPlayer(null);
                c.getSession().close();
                return;
            }
            c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
            channelServer.addPlayer(player);

            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));

            c.getSession().write(CField.getCharInfo(player));
            //PlayersHandler.calcHyperSkillPointCount(c);
            c.getSession().write(CSPacket.Use_CS(true));
            player.getMap().addPlayer(player);
            
            try {
                // Start of buddylist
                final int buddyIds[] = player.getBuddylist().getBuddyIds();
                World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
                if (player.getParty() != null) {
                    final MapleParty party = player.getParty();
                    World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

                    if (party != null && party.getExpeditionId() > 0) {
                        final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if (me != null) {
                            c.getSession().write(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
                        }
                    }
                }
                final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
                for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                    player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
                }
                c.getSession().write(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

                // Start of Messenger
                final MapleMessenger messenger = player.getMessenger();
                if (messenger != null) {
                    World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                    World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
                }

                // Start of Guild and alliance
                if (player.getGuildId() > 0) {
                    World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                    c.getSession().write(GuildPacket.showGuildInfo(player));
                    final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                    if (gs != null) {
                        final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                        if (packetList != null) {
                            for (byte[] pack : packetList) {
                                if (pack != null) {
                                    c.getSession().write(pack);
                                }
                            }
                        }
                    } else { //guild not found, change guild id
                        player.setGuildId(0);
                        player.setGuildRank((byte) 5);
                        player.setAllianceRank((byte) 5);
                        player.saveGuildStatus();
                    }
                }
                if (player.getFamilyId() > 0) {
                    World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
                }
                c.getSession().write(CWvsContext.FamilyPacket.getFamilyData());
                c.getSession().write(CWvsContext.FamilyPacket.getFamilyInfo(player));
            } catch (Exception e) {
                FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
            }
            player.getClient().getSession().write(CWvsContext.broadcastMsg(channelServer.getServerMessage()));
            player.showNote();
            player.sendImp();
            player.updatePartyMemberHP();
            player.startFairySchedule(false);
            c.getSession().write(CField.getKeymap(player.getKeyLayout()));
            player.updatePetAuto();
            player.baseSkills(); //fix people who've lost skills.
            if (GameConstants.isZero(player.getJob())) {
                c.getSession().write(CWvsContext.updateSkills(player.getSkills(), false));
            }
            player.expirationTask(true, transfer == null);
            c.getSession().write(CWvsContext.updateMaplePoint(player.getCSPoints(2)));
            if (player.getJob() == 132) { // DARKKNIGHT
                player.checkBerserk();
            }
            if (GameConstants.isXenon(player.getJob())) {
                player.startXenonSupply();
            }
            if (GameConstants.isDemonAvenger(player.getJob())) {
                c.getSession().write(AvengerPacket.giveAvengerHpBuff(player.getStat().getHp()));
            }
            player.spawnClones();
            player.spawnSavedPets();
            if (player.getStat().equippedSummon > 0) {
                SkillFactory.getSkill(player.getStat().equippedSummon + (GameConstants.getBeginnerJob(player.getJob()) * 1000)).getEffect(1).applyTo(player);
            }
            MapleQuestStatus stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            //c.getSession().write(CWvsContext.pendantSlot(stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) > System.currentTimeMillis()));
            player.sendMacros();
            stat = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));
            //c.getSession().write(CField.quickSlot((stat != null) && (stat.getCustomData() != null) ? stat.getCustomData() : null));
            //c.getSession().write(CWvsContext.getFamiliarInfo(player));
            c.getSession().write(CField.getInventoryStatus());
            // c.getSession().write(CWvsContext.getFamiliarInfo(player));
            MapleInventory equipped = player.getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
            List<Short> slots = new ArrayList<>();
            for (Item item : equipped.newList()) {
                slots.add(item.getPosition());
            }
            for (short slot : slots) {
                if (GameConstants.isIllegalItem(equipped.getItem(slot).getItemId())) {
                    MapleInventoryManipulator.removeFromSlot(player.getClient(), MapleInventoryType.EQUIPPED, slot, (short) 1, false);
                }
            }
            //c.getSession().write(CWvsContext.shopDiscount(ServerConstants.SHOP_DISCOUNT));
            //List<Pair<Integer, String>> npcs = new ArrayList<>();
            //npcs.add(new Pair<>(9070006, "Why...why has this happened to me? My knightly honor... My knightly pride..."));
            //npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));
            //c.getSession().write(NPCPacket.setNpcScriptable(npcs));
            //c.getSession().write(NPCPacket.setNPCScriptable());
            player.updateReward();
            //        player.setDeathCount(99);
            //        c.getSession().write(CField.EffectPacket.updateDeathCount(99)); //for fun
            Thread.sleep(3100);
            if (c.getPlayer().getLevel() < 11) {
                //NPCScriptManager.getInstance().start(c, 2500002, "ThalesLogin");
            } 
        } catch (InterruptedException e) {
        }
        System.out.println(c.toString());
    }

    public static final void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in less than a minute.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        chr.updateTick(slea.readInt());
        if (!World.isChannelAvailable(chc, chr.getWorld())) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.getSession().write(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "Request denied due to an unknown error.");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
    }
}
