package handling;

public enum RecvPacketOpcode implements WritableIntValueHolder {
    // General
    RSA_KEY(false),
    STRANGE_DATA,
    MAPLETV,
    LOGIN_REDIRECTOR(false, (short) 0x01),
    CRASH_INFO(false, (short) 0x2E),
    PONG(false, (short) 0x46),//145
    // Login
    GUEST_LOGIN(true, (short) 0x16),
    SERVERSTATUS_REQUEST(false, (short) 0x1D),
    TOS(true, (short) 0x1E),
    VIEW_SERVERLIST(false, (short) 0x21),
    SERVERLIST_REQUEST(false, (short) 0x22),
    REDISPLAY_SERVERLIST(true, (short) 0x23),
    CHAR_SELECT_NO_PIC(false, (short) 0x25),
    PLAYER_LOGGEDIN(false, (short) 0x27),
    CHECK_CHAR_NAME(true, (short) 0x28),
    DELETE_CHAR(true, (short) 0x2C),
    AUTH_REQUEST(false, (short) 0x30),
    CHAR_SELECT(true, (short) 0x31),
    VIEW_REGISTER_PIC(true, (short) 0x32),
    VIEW_SELECT_PIC(true, (short) 0x35),
    CLIENT_START(false, (short) 0x38),
    CLIENT_FAILED(false, (short) 0x39),
    PART_TIME_JOB(true, (short) 0x3B),
    CHARACTER_CARD(true, (short) 0x3C),
    ENABLE_LV50_CHAR(true, (short) 0x3D),
    CREATE_LV50_CHAR(true, (short) 0x3E),
    ENABLE_SPECIAL_CREATION(true, (short) 0x3E),
    CLIENT_HELLO(false, (short) 0x3F),
    LOGIN_PASSWORD(false, (short) 0x40),
    CREATE_SPECIAL_CHAR(true, (short) 0x41),
    CHARLIST_REQUEST(false, (short) 0x43),
    CREATE_CHAR(false, (short) 0x45),
    CREATE_ULTIMATE(false, (short) 0x999),
    AUTH_SECOND_PASSWORD(true, (short) 0x47),
    CLIENT_ERROR(false, (short) 0x4A),
    /*
     * Channel Opcodes.
     * Used for in-game packets.
     */
    CHANGE_MAP(true, (short) 0x4F),//51
    CHANGE_CHANNEL(true, (short) 0x50),//52
    ENTER_CASH_SHOP(true, (short) 0x52),
    ENTER_FARM(true, (short) 0x57),
    ENTER_AZWAN(true, (short) 0x4D),
    ENTER_AZWAN_EVENT(true, (short) 0x999),
    LEAVE_AZWAN(true, (short) 0x4B),
    ENTER_PVP(true, (short) 0x50),
    ENTER_PVP_PARTY(true, (short) 0x999),
    LEAVE_PVP(true, (short) 0x999),
    MOVE_PLAYER(true, (short) 0x5C),//5E
    CANCEL_CHAIR(true, (short) 0x5E),//60
    USE_CHAIR(true, (short) 0x5F),//61
    CLOSE_RANGE_ATTACK(true, (short) 0x60),//62
    RANGED_ATTACK(true, (short) 0x61),//63
    MAGIC_ATTACK(true, (short) 0x62),//64
    PASSIVE_ENERGY(true, (short) 0x63),//65
    TAKE_DAMAGE(true, (short) 0x66),//68
    PVP_ATTACK(true, (short) 0x69),
    GENERAL_CHAT(true, (short) 0x68),//6A
    CLOSE_CHALKBOARD(true, (short) 0x69),//6B
    FACE_EXPRESSION(true, (short) 0x6A),//6C
    FACE_ANDROID(true, (short) 0x6B),//6D
    USE_ITEMEFFECT(true, (short) 0x6C),//6E
    WHEEL_OF_FORTUNE(true, (short) 0x6D),//6F
    USE_TITLE(true, (short) 0x6F),
    ANGELIC_CHANGE(true, (short) 0x70),
    CHANGE_CODEX_SET(true, (short) 0x999),
    CODEX_UNK(true, (short) 0x999),
    MONSTER_BOOK_DROPS(true, (short) 0x999),
    NPC_TALK(true, (short) 0x7D),
    NPC_TALK_MORE(true, (short) 0x7F),
    NPC_SHOP(true, (short) 0x80),
    STORAGE(true, (short) 0x82),
    USE_HIRED_MERCHANT(true, (short) 0x83), // 85
    MERCH_ITEM_STORE(true, (short) 0x84),// 86
    PACKAGE_OPERATION(true, (short) 0xFFF),
    MECH_CANCEL(true, (short) 0x87), // guess
    OWL(true, (short) 0x8C),
    OWL_WARP(true, (short) 0x8D),
    ITEM_SORT(true, (short) 0x90),
    ITEM_GATHER(true, (short) 0x91),
    ITEM_MOVE(true, (short) 0x92),
    MOVE_BAG(true, (short) 0x93),
    SWITCH_BAG(true, (short) 0x94),
    USE_ITEM(true, (short) 0x96),
    CANCEL_ITEM_EFFECT(true, (short) 0x97),
    USE_SUMMON_BAG(true, (short) 0x9B),
    PET_FOOD(true, (short) 0x9A),
    USE_MOUNT_FOOD(true, (short) 0x9B),
    USE_SCRIPTED_NPC_ITEM(true, (short) 0x9C), //v149
    USE_RECIPE(true, (short) 0x9B),
    USE_NEBULITE(true, (short) 0x9C),//9E
    USE_ALIEN_SOCKET(true, (short) 0x9D),
    USE_ALIEN_SOCKET_RESPONSE(true, (short) 0xA0),
    USE_NEBULITE_FUSION(true, (short) 0xA1),
    USE_CASH_ITEM(true, (short) 0xA2),//A4 // updated to v149
    USE_CATCH_ITEM(true, (short) 0xA4),//A4
    USE_SKILL_BOOK(true, (short) 0xAB),//A9
    USE_EXP_POTION(true, (short) 0xAC),//A8
    TOT_GUIDE(true, (short) 0xB4),//B6
    USE_OWL_MINERVA(true, (short) 0xBB),//BA
    USE_TELE_ROCK(true, (short) 0xEA),//v149
    USE_RETURN_SCROLL(true, (short) 0xBC),//v149
    USE_UPGRADE_SCROLL(true, (short) 0xBE),//v149
    USE_FLAG_SCROLL(true, (short) 0xBF),//v149
    USE_EQUIP_SCROLL(true, (short) 0xC0),////v149
    USE_POTENTIAL_SCROLL(true, (short) 0xC4),//v149
    USE_ABYSS_SCROLL(true, (short) 0xC5),//v149
    USE_CARVED_SEAL(true, (short) 0xC6),//v149
    USE_BAG(true, (short) 0xC7),//v149
    USE_CRAFTED_CUBE(true, (short) 0xCA), //v149
    USE_MAGNIFY_GLASS(true, (short) 0xCD),//CC
    DISTRIBUTE_AP(true, (short) 0xD2),//D0
    AUTO_ASSIGN_AP(true, (short) 0xD3),//D1
    HEAL_OVER_TIME(true, (short) 0xD4),//D2
    DISTRIBUTE_SP(true, (short) 0xD7),//D5
    SPECIAL_MOVE(true, (short) 0xD8),//D6
    CANCEL_BUFF(true, (short) 0xD9),//D7
    SKILL_EFFECT(true, (short) 0xDA),//D5
    MESO_DROP(true, (short) 0xDB),//D6
    GIVE_FAME(true, (short) 0xDC),//v149
    CHAR_INFO_REQUEST(true, (short) 0xDD),//D9
    SPAWN_PET(true, (short) 0xDF),//DA
    GET_BOOK_INFO(true, (short) 0xE0),//DC
    USE_FAMILIAR(true, (short) 0xE1),//DD
    SPAWN_FAMILIAR(true, (short) 0xE2),//DE
    RENAME_FAMILIAR(true, (short) 0xE3),//DF
    PET_BUFF(true, (short) 0xE4),//E0
    CANCEL_DEBUFF(true, (short) 0xE5),//E1
    CHANGE_MAP_SPECIAL(true, (short) 0xE6),//e2
    USE_INNER_PORTAL(true, (short) 0xE7),//E3
    TROCK_ADD_MAP(true, (short) 0xE8),//e4
    LIE_DETECTOR(true, (short) 0x999),//E5
    LIE_DETECTOR_SKILL(true, (short) 0x999),//E6
    LIE_DETECTOR_RESPONSE(true, (short) 0x999),//E7
    REPORT(true, (short) 0xED),//E9
    QUEST_ACTION(true, (short) 0xEE),//EA
    REISSUE_MEDAL(true, (short) 0xEF),//EB
//    BUFF_RESPONSE(true, (short) 0xEF),//EC
    SKILL_MACRO(true, (short) 0xF3),//F0
    REWARD_ITEM(true, (short) 0xF5),//F2
    ITEM_MAKER(true, (short) 0x999),
    REPAIR_ALL(true, (short) 0xFE),//C7
    REPAIR(true, (short) 0xFF),//C8
    SOLOMON(true, (short) 0x999),//C9
    GACH_EXP(true, (short) 0xCB),//CA
    FOLLOW_REQUEST(true, (short) 0x102),//FD
    PQ_REWARD(true, (short) 0x103),//FE
    FOLLOW_REPLY(true, (short) 0x106),//101
    AUTO_FOLLOW_REPLY(true, (short) 0x999),
    USE_TREASURE_CHEST(true, (short) 0x999),
    PROFESSION_INFO(true, (short) 0x102),
    USE_POT(true, (short) 0x999),//D6
    CLEAR_POT(true, (short) 0xD7),
    FEED_POT(true, (short) 0xD8),
    CURE_POT(true, (short) 0xD9),
    REWARD_POT(true, (short) 0xDA),
    AZWAN_REVIVE(true, (short) 0xDB),
    USE_COSMETIC(true, (short) 0x999),
    INNER_CIRCULATOR(true, (short) 0xDF),
    PVP_RESPAWN(true, (short) 0xE0),
    GAIN_FORCE(true, (short) 0xE1),
    ADMIN_CHAT(true, (short) 0x12B), // updated to v149.3
    PARTYCHAT(true, (short) 0x12C), // updated to v149.3
    COMMAND(true, (short) 0x12D), // updated to v149.3
    SPOUSE_CHAT(true, (short) 0x12E), // updated to v149.3
    MESSENGER(true, (short) 0x12F), // updated to v149.3
    PLAYER_INTERACTION(true, (short) 0x130), // updated to v149.3
    PARTY_OPERATION(true, (short) 0x131), // updated to v149.3
    DENY_PARTY_REQUEST(true, (short) 0x132), // updated to v149.3
    ALLOW_PARTY_INVITE(true, (short) 0x999),//12F
    EXPEDITION_OPERATION(true, (short) 0x999),//130
    EXPEDITION_LISTING(true, (short) 0x131),
    GUILD_OPERATION(true, (short) 0x136),//132. v149.3
    DENY_GUILD_REQUEST(true, (short) 0x137),//v149.3
    ADMIN_COMMAND(true, (short) 0x138),//149.3
    ADMIN_LOG(true, (short) 0x139),//149.3
    BUDDYLIST_MODIFY(true, (short) 0x13A), //149.3
    NOTE_ACTION(true, (short) 0x13B),//149.3
    USE_DOOR(true, (short) 0x13E),//149.3
    USE_MECH_DOOR(true, (short) 0x13F),//149.3
    CHANGE_KEYMAP(true, (short) 0x13E), //134
    RPS_GAME(true, (short) 0x135),
    RING_ACTION(true, (short) 0x136),
    WEDDING_ACTION(true, (short) 0x137),
    ALLIANCE_OPERATION(true, (short) 0x144),//13B
    DENY_ALLIANCE_REQUEST(true, (short) 0x145),//13C
    REQUEST_FAMILY(true, (short) 0x146),//13D
    OPEN_FAMILY(true, (short) 0x147),//13E
    FAMILY_OPERATION(true, (short) 0x148),//13F
    DELETE_JUNIOR(true, (short) 0x149),//140
    DELETE_SENIOR(true, (short) 0x14A),//141
    ACCEPT_FAMILY(true, (short) 0x14B),//142
    USE_FAMILY(true, (short) 0x14C),//143
    FAMILY_PRECEPT(true, (short) 0x14D),//144
    FAMILY_SUMMON(true, (short) 0x14E),//145
    BBS_OPERATION(true, (short) 0x15B),//150
    SOLOMON_EXP(true, (short) 0x15C),//151
    NEW_YEAR_CARD(true, (short) 0x11E),
    XMAS_SURPRISE(true, (short) 0x111),
    TWIN_DRAGON_EGG(true, (short) 0x112),
    ARAN_COMBO(true, (short) 0x15E),//0x152
    TRANSFORM_PLAYER(true, (short) 0x999),
    CYGNUS_SUMMON(true, (short) 0x999),
    CRAFT_DONE(true, (short) 0x162),//157
    CRAFT_EFFECT(true, (short) 0x163),//158
    CRAFT_MAKE(true, (short) 0x164),//159
    CHANGE_ROOM_CHANNEL(true, (short) 0x174),//Updated to v149.3
    EVENT_CARD(true, (short) 0x175),//Updated to v149.3
    CHOOSE_SKILL(true, (short) 0x176),//Updated to v149.3
    SKILL_SWIPE(true, (short) 0x177),//Updated to v149.3
    VIEW_SKILLS(true, (short) 0x178),//Updated to v149.3
    CANCEL_OUT_SWIPE(true, (short) 0x179),//Updated to v149.3
    YOUR_INFORMATION(true, (short) 0x180),//Updated to v149.3
    FIND_FRIEND(true, (short) 0x170),//164
    PINKBEAN_CHOCO_OPEN(true, (short) 0x171),//165
    PINKBEAN_CHOCO_SUMMON(true, (short) 0x172),//166
    BUY_SILENT_CRUSADE(true, (short) 0x127),
    CASSANDRAS_COLLECTION(true, (short) 0x178),//new v145
    BUDDY_ADD(true, (short) 0x1A2),
    MOVE_PET(true, (short) 0x1B8),//1A8
    PET_CHAT(true, (short) 0x1B9),//1A9
    PET_COMMAND(true, (short) 0x1BA),//1AA
    PET_LOOT(true, (short) 0x1BB),//1AB
    PET_AUTO_POT(true, (short) 0x1BC),//1AC
    PET_IGNORE(true, (short) 0x1BD),//1AD
    MOVE_HAKU(true, (short) 0x1C1),//1B1
    CHANGE_HAKU(true, (short) 0x1C2),//1B2
    //HAKU_1D8(true, (short) 0x1D8),//test
    //HAKU_1D9(true, (short) 0x1D9),//test
    MOVE_SUMMON(true, (short) 0x1C8),//1b8
    SUMMON_ATTACK(true, (short) 0x1C9),//1B9
    DAMAGE_SUMMON(true, (short) 0x1CA),//1BA
    SUB_SUMMON(true, (short) 0x1CB),//1BB
    REMOVE_SUMMON(true, (short) 0x1CC),//1BC
    PVP_SUMMON(true, (short) 0x1CE),//1BE
    MOVE_DRAGON(true, (short) 0x1CF),//1C0
    USE_ITEM_QUEST(true, (short) 0x1D2),//1C4
    MOVE_ANDROID(true, (short) 0x1D3),//1C5
    UPDATE_QUEST(true, (short) 0x1D5),//1C7//+16
    QUEST_ITEM(true, (short) 0x1D6),//1D6
    MOVE_FAMILIAR(true, (short) 0x1DC),//1DC
    TOUCH_FAMILIAR(true, (short) 0x1DD),//1DD
    ATTACK_FAMILIAR(true, (short) 0x1DE),//1DE
    REVEAL_FAMILIAR(true, (short) 0x1DF),//1DF
    QUICK_SLOT(true, (short) 0x1D0),
    PAM_SONG(true, (short) 0x1D8),
    AUTO_AGGRO(true, (short) 0x21A),//210
    FRIENDLY_DAMAGE(true, (short) 0x21B),//211
    MONSTER_BOMB(true, (short) 0x21C),//212
    HYPNOTIZE_DMG(true, (short) 0x21D),//213
    MOVE_LIFE(true, (short) 0x230),//v149.3
    MOB_BOMB(true, (short) 0x221),//221
    MOB_NODE(true, (short) 0x222),//222
    DISPLAY_NODE(true, (short) 0x223),//223
    MONSTER_CARNIVAL(true, (short) 0x224),//224
    NPC_ACTION(true, (short) 0x999),//230
    ITEM_PICKUP(true, (short) 0x24C),//v149
    DAMAGE_REACTOR(true, (short) 0x238),//22E
    TOUCH_REACTOR(true, (short) 0x239),//22F
    CLICK_REACTOR(true, (short) 0x23A),//230
    MAKE_EXTRACTOR(true, (short) 0x23B),//231
    UPDATE_ENV(true, (short) 0x161),
    SNOWBALL(true, (short) 0x165),
    LEFT_KNOCK_BACK(true, (short) 0x166),
    CANDY_RANKING(true, (short) 0x185),
    COCONUT(true, (short) 0x186),
    LUCKY_LUCKY_MONSTORY(true, (short) 0x198),//new v147
    SHIP_OBJECT(true, (short) 0x999),
    PARTY_SEARCH_START(true, (short) 0x999),//24B
    PARTY_SEARCH_STOP(true, (short) 0x24D),//24C
    START_HARVEST(true, (short) 0x24F),//24E
    STOP_HARVEST(true, (short) 0x250),//24F
    QUICK_MOVE(true, (short) 0x19F),//19E
    CS_UPDATE(true, (short) 0x2C1),// Updated to v149
    BUY_CS_ITEM(true, (short) 0x2C2),// Updated to v149
    COUPON_CODE(true, (short) 0x290),//28F
    CASH_CATEGORY(true, (short) 0x2AD),//294
    PLACE_FARM_OBJECT(false, (short) 0x278),
    FARM_SHOP_BUY(false, (short) 0x27D),
    FARM_COMPLETE_QUEST(false, (short) 0x281),
    FARM_NAME(false, (short) 0x282),
    HARVEST_FARM_BUILDING(false, (short) 0x283),
    USE_FARM_ITEM(false, (short) 0x284),
    RENAME_MONSTER(false, (short) 0x999),
    NURTURE_MONSTER(false, (short) 0x295),
    EXIT_FARM(false, (short) 0x299),
    FARM_QUEST_CHECK(false, (short) 0x29D),
    FARM_FIRST_ENTRY(false, (short) 0x2A8),
    GOLDEN_HAMMER(true, (short) 0x2A4),
    VICIOUS_HAMMER(true, (short) 0x1BD),
    PYRAMID_BUY_ITEM(true, (short) 0x999),
    CLASS_COMPETITION(true, (short) 0x999),
    MAGIC_WHEEL(true, (short) 0x2EB),
    REWARD(true, (short) 0x323), // updated to v149.3
    BLACK_FRIDAY(true, (short) 0x2BE),
    RECEIVE_GIFT_EFFECT(true, (short) 0x2F5),//new v145
    UPDATE_RED_LEAF(true, (short) 0x29C),
    //Not Placed:
    SPECIAL_STAT(false, (short) 0x10C),//107
    UPDATE_HYPER(true, (short) 0x171),//
    RESET_HYPER(true, (short) 0x172),//
    DRESSUP_TIME(true, (short) 0x17F),
    DF_COMBO(true, (short) 0x999),
    BUTTON_PRESSED(true, (short) 0x1F1),//1D3
    OS_INFORMATION(true, (short) 0x1E6),//1D6
    LUCKY_LOGOUT(true, (short) 0x2B6),
    MESSENGER_RANKING(true, (short) 0x1DD);
    private short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public final short getValue() {
        return code;
    }
    private final boolean CheckState;

    private RecvPacketOpcode() {
        this.CheckState = true;
    }

    private RecvPacketOpcode(final boolean CheckState) {
        this.CheckState = CheckState;
    }

    private RecvPacketOpcode(final boolean CheckState, short code) {
        this.CheckState = CheckState;
        this.code = code;
    }

    public final boolean NeedsChecking() {
        return CheckState;
    }
    
    public static String nameOf(short value) {
        for (RecvPacketOpcode header : RecvPacketOpcode.values()) {
            if (header.getValue() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }
}
