package constants;

public class ServerConfig {

    public static boolean adminOnly = false;
    public static boolean logPackets = true;
    public static final int flags = 3;
    public static final String serverName = "MechDEV v"+ServerConstants.MAPLE_VERSION+"."+ServerConstants.MAPLE_PATCH;
    public static final String eventMessage = "#bWelcome to MechDEV v"+ServerConstants.MAPLE_VERSION+"."+ServerConstants.MAPLE_PATCH+"!#k";
    public static final String scrollingMessage = "MechDEV v"+ServerConstants.MAPLE_VERSION+"."+ServerConstants.MAPLE_PATCH;
    public static boolean BoxOnStart = true;
    public static final String BoxTitle = "[MechDEV]";
    public static final String[] content = {"Reminder! You may vote for the server every 6 hours.", "By voting you can obtain cool rewards!", "Go to mechdev.com to vote."};
    public static final int flag = 3;
    public static final int maxCharacters = 3;
    public static final int userLimit = 1500;
    public static String interface_ = "127.0.0.1";
    public static final int channelCount = 3;
    public static final String events = "PinkZakumEntrance,PVP,CygnusBattle,ScarTarBattle,BossBalrog_EASY,BossBalrog_NORMAL,HorntailBattle,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,2095_tokyo,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,WitchTower_EASY,WitchTower_Med,WitchTower_Hard,Vergamot,ChaosHorntail,ChaosZakum,CoreBlaze,BossQuestEASY,BossQuestMed,BossQuestHARD,BossQuestHELL,BossQuestCHAOS,Ravana_EASY,Ravana_HARD,Ravana_MED,GuildQuest,Aufhaven,Dragonica,Rex,MonsterPark,KentaPQ,ArkariumBattle,AswanOffSeason,HillaBattle,The Dragon Shout,VonLeonBattle,Ghost,OrbisPQ,Romeo,Juliet,Pirate,Amoria,Ellin,CWKPQ,DollHouse,Kenta,Prison,Azwan,HenesysPQ,jett2ndjob";
    /*Anti-Sniff*/
    public static boolean USE_FIXED_IV = false;
    public static final byte[] Static_LocalIV = new byte[]{71, 113, 26, 44};
    public static final byte[] Static_RemoteIV = new byte[]{70, 112, 25, 43};

    public static enum Events {

        EVENT1("PinkZakumEntrance"),
        EVENT2("PVP"),
        EVENT3("CygnusBattle"),
        EVENT4("ScarTarBattle"),
        EVENT5("BossBalrog_EASY"),
        EVENT6("BossBalrog_NORMAL"),
        EVENT7("HorntailBattle"),
        EVENT8("Nibergen"),
        EVENT9("PinkBeanBattle"),
        EVENT10("ZakumBattle"),
        EVENT11("NamelessMagicMonster"),
        EVENT12("Dunas"),
        EVENT13("Dunas2"),
        EVENT14("2095_tokyo"),
        EVENT15("ZakumPQ"),
        EVENT16("LudiPQ"),
        EVENT17("KerningPQ"),
        EVENT18("ProtectTylus"),
        EVENT19("WitchTower_EASY"),
        EVENT20("WitchTower_Med"),
        EVENT21("WitchTower_Hard"),
        EVENT22("Vergamot"),
        EVENT23("ChaosHorntail"),
        EVENT24("ChaosZakum"),
        EVENT25("CoreBlaze"),
        EVENT26("BossQuestEASY"),
        EVENT27("BossQuestMed"),
        EVENT28("BossQuestHARD"),
        EVENT29("BossQuestHELL"),
        EVENT30("Ravana_EASY"),
        EVENT31("Ravana_HARD"),
        EVENT32("Ravana_MED"),
        EVENT33("GuildQuest"),
        EVENT34("Aufhaven"),
        EVENT35("Dragonica"),
        EVENT36("Rex"),
        EVENT37("MonsterPark"),
        EVENT38("KentaPQ"),
        EVENT39("ArkariumBattle"),
        EVENT40("AswanOffSeason"),
        EVENT41("HillaBattle"),
        EVENT42("The Dragon Shout"),
        EVENT43("VonLeonBattle"),
        EVENT44("Ghost"),
        EVENT45("OrbisPQ"),
        EVENT46("Romeo"),
        EVENT47("Juliet"),
        EVENT48("Pirate"),
        EVENT49("Amoria"),
        EVENT50("Ellin"),
        EVENT51("CWKPQ"),
        EVENT52("DollHouse"),
        EVENT53("Kenta"),
        EVENT54("Prison"),
        EVENT55("Azwan");
        private final String name;

        Events(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String[] getEvents() {
        String[] eventlist = new String[Events.values().length];
        int arrayLocation = 0;
        for (Events event : Events.values()) {
            eventlist[arrayLocation] += event.getName();
            arrayLocation++;
        }
        return eventlist;
    }

    public static String getEventList() {
        String eventlist = new String();
        for (Events event : Events.values()) {
            eventlist += event.getName();
            eventlist += ", ";
        }
        eventlist += "@";
        eventlist = eventlist.replaceAll(", @", "");
        return eventlist;
    }
}
