package handling;

import constants.ServerConfig;
import tools.FileoutputUtil;
import tools.HexTool;

public enum SendPacketOpcode implements WritableIntValueHolder {

    // General
    PING((short) 0x11),//-1
    AUTH_RESPONSE((short) 0x15),//-1
    // Login
    LOGIN_STATUS((short) 0x00),
    SEND_LINK((short) 0x01),
    LOGIN_SECOND((short) 0x02),
    SERVERSTATUS((short) 0x04),
    GENDER_SET((short) 0x05),
    PIN_OPERATION((short) 0x06),
    PIN_ASSIGNED((short) 0x07),
    ALL_CHARLIST((short) 0x08),
    SERVERLIST((short) 0x09),
    CHARLIST((short) 0x0A),
    SERVER_IP((short) 0x0B),
    CHAR_NAME_RESPONSE((short) 0x0C),
    ADD_NEW_CHAR_ENTRY((short) 0x0D),
    DELETE_CHAR_RESPONSE((short) 0x0E),
    CHANGE_CHANNEL((short) 0x10),
    CS_USE((short) 0x13),// updated to v149.2
    REGISTER_PIC_RESPONSE((short) 0x1A),
    ENABLE_RECOMMENDED((short) 0x1C), // -1
    SEND_RECOMMENDED((short) 0x1D), // -1
    PART_TIME((short) 0x1E), // -1
    CHANNEL_SELECTED((short) 0x20),// guess -1
    EXTRA_CHAR_INFO((short) 0x21), // guess -1
    SPECIAL_CREATION((short) 0x22),// guess -1
    SECONDPW_ERROR((short) 0x23),// guess -1
    CHANGE_BACKGROUND((short) 0x13F),// -1
    
    // Channel
    INVENTORY_OPERATION((short) 0x24),//26
    INVENTORY_GROW((short) 0x25),//27
    UPDATE_STATS((short) 0x26),//28
    GIVE_BUFF((short) 0x27),//29
    CANCEL_BUFF((short) 0x28),//2A
    TEMP_STATS((short) 0x29),//2B
    TEMP_STATS_RESET((short) 0x2A),//2C
    UPDATE_SKILLS((short) 0x2B),//2D
    UPDATE_STOLEN_SKILLS((short) 0x2D),//2E
    TARGET_SKILL((short) 0x2E),//2F
    FAME_RESPONSE((short) 0x31),//v149
    SHOW_STATUS_INFO((short) 0x32),//34
    FULL_CLIENT_DOWNLOAD((short) 0x33),
    SHOW_NOTES((short) 0x34),//36//-2
    TROCK_LOCATIONS((short) 0x35),
    LIE_DETECTOR((short) 0x36),//37
    REPORT_RESPONSE((short) 0x3A),//39
    REPORT_TIME((short) 0x3B),//3A
    REPORT_STATUS((short) 0x3C),//3B
    UPDATE_MOUNT((short) 0x3D),//3E
    SHOW_QUEST_COMPLETION((short) 0x3E),//3F
    SEND_TITLE_BOX((short) 0x40),//3F
    USE_SKILL_BOOK((short) 0x41),//40
    SP_RESET((short) 0x42),//41
    AP_RESET((short) 0x43),//42
    DISTRIBUTE_ITEM((short) 0x44),//43
    EXPAND_CHARACTER_SLOTS((short) 0x45),//44
    FINISH_GATHER((short) 0x4B),//v145
    FINISH_SORT((short) 0x4C),//v145
    EXP_POTION((short) 0x43),
    REPORT_RESULT((short) 0x4E),//v145
    TRADE_LIMIT((short) 0x49),//v145
    UPDATE_GENDER((short) 0x50),//50
    BBS_OPERATION((short) 0x51),//51
    CHAR_INFO((short) 0x53),//v143
    PARTY_OPERATION((short) 0x54),//55
    MEMBER_SEARCH((short) 0x56),//5A
    PARTY_SEARCH((short) 0x57),//5A
    BOOK_INFO((short) 0x58),//5A
    CODEX_INFO_RESPONSE((short) 0x5A),//v149.3
    EXPEDITION_OPERATION((short) 0x5B),//v149.3
    BUDDYLIST((short) 0x5C),//v149.3
    GUILD_OPERATION((short) 0x5E),//v149.3
    ALLIANCE_OPERATION((short) 0x5F),//v149.3
    SPAWN_PORTAL((short) 0x61),//61
    MECH_PORTAL((short) 0x62),//63
    ECHO_MESSAGE((short) 0x63),//64
    SERVERMESSAGE((short) 0x63),//-1 v149.2
    ITEM_OBTAIN((short) 0x6A),//68
    PIGMI_REWARD((short) 0x6E),//6C
    OWL_OF_MINERVA((short) 0x6F),//6D
    OWL_RESULT((short) 0x70),//6E
    ENGAGE_REQUEST((short) 0x71),//6F
    ENGAGE_RESULT((short) 0x72),//70
    WEDDING_GIFT((short) 0x73),//71
    WEDDING_MAP_TRANSFER((short) 0x74),//72
    USE_CASH_PET_FOOD((short) 0x76),//74
    YELLOW_CHAT((short) 0x74),//78
    SHOP_DISCOUNT((short) 0x75),//76
    CATCH_MOB((short) 0x76),//77
    MAKE_PLAYER_NPC((short) 0x77),//78
    PLAYER_NPC((short) 0x78),//79
    DISABLE_NPC((short) 0x79),//7A
    GET_CARD((short) 0x7A),//7B
    CARD_UNK((short) 0x7B),//new143
    CARD_SET((short) 0x7C),//7D
    BOOK_STATS((short) 0x7D),//7E
    UPDATE_CODEX((short) 0x7E),//7F
    CARD_DROPS((short) 0x7F),//80
    FAMILIAR_INFO((short) 0x80),//81
    CHANGE_HOUR((short) 0x81),//83
    RESET_MINIMAP((short) 0x82),//87
    CONSULT_UPDATE((short) 0x83),//88
    CLASS_UPDATE((short) 0x84),//89
    WEB_BOARD_UPDATE((short) 0x85),//8A
    SESSION_VALUE((short) 0x86),//8B
    MAP_VALUE((short) 0x87),//v145
    EXP_BONUS((short) 0x88),//v145
    PARTY_VALUE((short) 0x999),//v145
    POTION_BONUS((short) 0x999),//8D
    SEND_PEDIGREE((short) 0x8D),//91
    OPEN_FAMILY((short) 0x8E),//92
    FAMILY_MESSAGE((short) 0x89),//-1
    FAMILY_INVITE((short) 0x90),//-1
    FAMILY_JUNIOR((short) 0x91),//-1
    SENIOR_MESSAGE((short) 0x92),//-1
    FAMILY((short) 0x93),//-1
    REP_INCREASE((short) 0x98),//95
    FAMILY_LOGGEDIN((short) 0x99),//96
    FAMILY_BUFF((short) 0x9A),//97
    FAMILY_USE_REQUEST((short) 0x9B),//98
    LEVEL_UPDATE((short) 0x9C),//99
    MARRIAGE_UPDATE((short) 0x9D),//9A
    JOB_UPDATE((short) 0x9E),//9B
    MAPLE_TV_MSG((short) 0x8D),
    LUCKY_LUCKY_MONSTORY((short) 0x103),//new v147
    AVATAR_MEGA_RESULT((short) 0x107),//FF
    AVATAR_MEGA((short) 0x108),//100
    AVATAR_MEGA_REMOVE((short) 0x109),//101
    POPUP2((short) 0x9D),
    CANCEL_NAME_CHANGE((short) 0x9E),
    CANCEL_WORLD_TRANSFER((short) 0x9F),
    CLOSE_HIRED_MERCHANT((short) 0xA3),//A0
    GM_POLICE((short) 0xA4),//A1
    TREASURE_BOX((short) 0xA5),//A2
    NEW_YEAR_CARD((short) 0xA6),//A3
    RANDOM_MORPH((short) 0xA7),//A4
    CANCEL_NAME_CHANGE_2((short) 0x999),//A9
    SLOT_UPDATE((short) 0xA9),//AC
    FOLLOW_REQUEST((short) 0xAB),//AD
    TOP_MSG((short) 0xAB),//AC
    NEW_TOP_MSG((short) 0xB0),//new148
    MID_MSG((short) 0xB1),//B2
    CLEAR_MID_MSG((short) 0xB2), //B3
    SPECIAL_MSG((short) 0xB3), //B4
    MAPLE_ADMIN_MSG((short) 0xB4), //B5
    CAKE_VS_PIE_MSG((short) 0x999),
    GM_STORY_BOARD((short) 0xB3), //B7
    INVENTORY_FULL((short) 0xB2),//B6
    ZERO_STATS((short) 0xB8),
    UPDATE_JAGUAR((short) 0xB9),
    YOUR_INFORMATION((short) 0xB9),
    FIND_FRIEND((short) 0xBA),
    VISITOR((short) 0xBB),
    PINKBEAN_CHOCO((short) 0xBC),
    PAM_SONG((short) 0xBD),
    AUTO_CC_MSG((short) 0xBE),
    DISALLOW_DELIVERY_QUEST((short) 0xC1),//bb
    ULTIMATE_EXPLORER((short) 0xC2),//BC
    SPECIAL_STAT((short) 0xC3),
    UPDATE_IMP_TIME((short) 0xC5),//BE
    ITEM_POT((short) 0xC6),//BF
    MULUNG_MESSAGE((short) 0xC9),//C2
    GIVE_CHARACTER_SKILL((short) 0xCA),//C3
    MULUNG_DOJO_RANKING((short) 0xCF),//C8
    UPDATE_INNER_ABILITY((short) 0xD5),//D4
    EQUIP_STOLEN_SKILL((short) 0xD5),//D5
    REPLACE_SKILLS((short) 0xD6),//CE
    INNER_ABILITY_MSG((short) 0xD7),//D6
    ENABLE_INNER_ABILITY((short) 0xD8),//d7
    DISABLE_INNER_ABILITY((short) 0xD9),//D8
    UPDATE_HONOUR((short) 0xDA),//D9
    AZWAN_UNKNOWN((short) 0xDC),//DB
    AZWAN_RESULT((short) 0xDD),//DC
    AZWAN_KILLED((short) 0xDE),//DD
    CIRCULATOR_ON_LEVEL((short) 0xDF),//D6
    SILENT_CRUSADE_MSG((short) 0xE),//D7
    SILENT_CRUSADE_SHOP((short) 0xEA),//DF
    CASSANDRAS_COLLECTION((short) 0xEB),//new v145
    MAPLE_POINT((short) 0xEC),//E6
    SET_OBJECT_STATE((short) 0xED),//E8
    POPUP((short) 0xEE),//E9
    MINIMAP_ARROW((short) 0xF4),//ED
    UNLOCK_CHARGE_SKILL((short) 0xFA),//F2
    LOCK_CHARGE_SKILL((short) 0xFB),//F3
    CANDY_RANKING((short) 0xFF),//F8
    ATTENDANCE((short) 0x10A),//102
    MESSENGER_OPEN((short) 0x10B),//103
    EVENT_CROWN((short) 0x110),//Updated to v149.2 -1
    RANDOM_RESPONSE((short) 0x121),
    MAGIC_WHEEL((short) 0x137),//v149.3
    REWARD((short) 0x138),//135 updated v149.3
    SKILL_MACRO((short) 0x139),//V149.3
    WARP_TO_MAP((short) 0x13A),//v149.2
    FARM_OPEN((short) 0x138),//129
    MTS_OPEN((short) 0x139),//12A
    CS_OPEN((short) 0x13D),//updated to v149.3
    REMOVE_BG_LAYER((short) 0x13D),//12E
    SET_MAP_OBJECT_VISIBLE((short) 0x13E),//12F
    RESET_SCREEN((short) 0x13F),//130
    MAP_BLOCKED((short) 0x143),//131
    SERVER_BLOCKED((short) 0x144),//132
    PARTY_BLOCKED((short) 0x145),//133
    SHOW_EQUIP_EFFECT((short) 0x146),//134
    MULTICHAT((short) 0x148),//136
    WHISPER((short) 0x149),//135
    SPOUSE_CHAT((short) 0x14B),//137
    BOSS_ENV((short) 0x14C),//13A
    MOVE_ENV((short) 0x14D),//13B
    UPDATE_ENV((short) 0x14E),//13C
    MAP_EFFECT((short) 0x150),//13E
    CASH_SONG((short) 0x151),//13F
    GM_EFFECT((short) 0x152),
    OX_QUIZ((short) 0x153),
    GMEVENT_INSTRUCTIONS((short) 0x152),//
    CLOCK((short) 0x153),//125
    BOAT_MOVE((short) 0x154),//126
    BOAT_STATE((short) 0x156),//128
    STOP_CLOCK((short) 0xFFF),//12D
    ARIANT_SCOREBOARD((short) 0xFFF),//12F
    PYRAMID_UPDATE((short) 0x14C),//131
    PYRAMID_RESULT((short) 0x14D),//132
    QUICK_SLOT((short) 0xFFF),//150
    MOVE_PLATFORM((short) 0xFFF),//153
    PVP_INFO((short) 0xFFF),//154
    PYRAMID_KILL_COUNT((short) 0x158),//155
    DIRECTION_STATUS((short) 0x16B),//168
    GAIN_FORCE((short) 0x16C),//15A
    ACHIEVEMENT_RATIO((short) 0x16A),//15B
    QUICK_MOVE((short) 0x16B),//15C
    SPAWN_PLAYER((short) 0x17C),//167
    REMOVE_PLAYER_FROM_MAP((short) 0x17D),//168
    CHATTEXT((short) 0x17E),//169
    CHATTEXT_1((short) 0x17F),//16A
    CHALKBOARD((short) 0x18C),//17C
    UPDATE_CHAR_BOX((short) 0x17D),//16C
    SHOW_CONSUME_EFFECT((short) 0x16D),//16D
    SHOW_SCROLL_EFFECT((short) 0x183),//16E
    SHOW_MAGNIFYING_EFFECT((short) 0x185),//184
    SHOW_POTENTIAL_RESET((short) 0x186),//v149
    SHOW_FIREWORKS_EFFECT((short) 0x187),//v149
    SHOW_NEBULITE_EFFECT((short) 0x188),//v149
    SHOW_FUSION_EFFECT((short) 0x189),//v149
    PVP_ATTACK((short) 0x140),
    PVP_MIST((short) 0x141),
    PVP_COOL((short) 0x142),
    TESLA_TRIANGLE((short) 0x999),//0x15C
    FOLLOW_EFFECT((short) 0x15D),
    SHOW_PQ_REWARD((short) 0x15E),
    CRAFT_EFFECT((short) 0x182),//15F
    CRAFT_COMPLETE((short) 0x183),//160
    HARVESTED((short) 0x185),//161
    PLAYER_DAMAGED((short) 0x165),
    NETT_PYRAMID((short) 0x166),
    SET_PHASE((short) 0x167),
    PAMS_SONG((short) 0x168),
    SPAWN_PET((short) 0x1AA),//16B
    SPAWN_PET_2((short) 0x192),//16D
    MOVE_PET((short) 0x193),//16E
    PET_CHAT((short) 0x194),//16F
    PET_NAMECHANGE((short) 0x195),//170
    PET_EXCEPTION_LIST((short) 0x196),//171
    PET_COLOR((short) 0x197),//172
    PET_SIZE((short) 0x198),//173
    PET_COMMAND((short) 0x199),//174
    DRAGON_SPAWN((short) 0x19A),//175
    INNER_ABILITY_RESET_MSG((short) 0x999),//173
    DRAGON_MOVE((short) 0x19B),//176
    DRAGON_REMOVE((short) 0x19C),//177
    ANDROID_SPAWN((short) 0x19D),//178
    ANDROID_MOVE((short) 0x19E),//179
    ANDROID_EMOTION((short) 0x19F),//17A
    ANDROID_UPDATE((short) 0x1A0),//17B
    ANDROID_DEACTIVATED((short) 0x1A1), //17C 
    SPAWN_FAMILIAR((short) 0x1A8),//183
    MOVE_FAMILIAR((short) 0x1A9),//184
    TOUCH_FAMILIAR((short) 0x1AA),//185
    ATTACK_FAMILIAR((short) 0x1AB),//186
    RENAME_FAMILIAR((short) 0x1AC),//187
    SPAWN_FAMILIAR_2((short) 0x1AD),//188
    UPDATE_FAMILIAR((short) 0x1AE),//189
    HAKU_CHANGE_1((short) 0x1A2),//18A
    HAKU_CHANGE_0((short) 0x1A5),//18B
    HAKU_MOVE((short) 0x1B0),//18B
    HAKU_UNK((short) 0x1B1),//18C
    HAKU_CHANGE((short) 0x1B2),//18D
    SPAWN_HAKU((short) 0x1B5),//190
    MOVE_PLAYER((short) 0x1B8),//193
    CLOSE_RANGE_ATTACK((short) 0x999),//1BA // 0x271 guess
    RANGED_ATTACK((short) 0x999),//1BB // was 0x1BB
    MAGIC_ATTACK((short) 0x999),//1BE // PAST 0X1BC
    ENERGY_ATTACK((short) 0x1BD),//198
    SKILL_EFFECT((short) 0x1BE),//199 // past 0x1BE
    MOVE_ATTACK((short) 0x1BF),//19A
    CANCEL_SKILL_EFFECT((short) 0x1C0),//19B
    DAMAGE_PLAYER((short) 0x1C1),//19C
    FACIAL_EXPRESSION((short) 0x1C2),//19D
    SHOW_EFFECT((short) 0x1C4),//19F
    SHOW_TITLE((short) 0x1C6),//1A1
    ANGELIC_CHANGE((short) 0x1C7),//1A2
    SHOW_CHAIR((short) 0x1CA),//1A5
    UPDATE_CHAR_LOOK((short) 0x183),//1CB // guess
    SHOW_FOREIGN_EFFECT((short) 0x1B6),//v149.3
    GIVE_FOREIGN_BUFF((short) 0x1CD),//1CD
    CANCEL_FOREIGN_BUFF((short) 0x1CE),//1CE
    UPDATE_PARTYMEMBER_HP((short) 0x1CF),//1CF
    LOAD_GUILD_NAME((short) 0x1D0),//1AB
    LOAD_GUILD_ICON((short) 0x1D1),//1AC
    LOAD_TEAM((short) 0x1D2),//1AD
    SHOW_HARVEST((short) 0x999),//1AE
    PVP_HP((short) 0x1D7),//1B0
    CANCEL_CHAIR((short) 0x1FF),//1E3
    DIRECTION_FACIAL_EXPRESSION((short) 0x1F8),//1E4
    MOVE_SCREEN((short) 0x1F9),//1E5
    SHOW_SPECIAL_EFFECT((short) 0x202),//1FA
    CURRENT_MAP_WARP((short) 0x203),//1EA
    MESOBAG_SUCCESS((short) 0x205),//1EC
    MESOBAG_FAILURE((short) 0x1ED),//1EA
    R_MESOBAG_SUCCESS((short) 0x1EE),//1EB
    R_MESOBAG_FAILURE((short) 0x1EF),//1EC
    MAP_FADE((short) 0x200),//201
    MAP_FADE_FORCE((short) 0x201),//202
    UPDATE_QUEST_INFO((short) 0x20B),//20B
    HP_DECREASE((short) 0x20C),//1F3
    PLAYER_HINT((short) 0x20E),//1F5
    PLAY_EVENT_SOUND((short) 0x207),//1F6
    PLAY_MINIGAME_SOUND((short) 0x208),//1F7
    MAKER_SKILL((short) 0x209),//1F8
    OPEN_UI((short) 0x20B),//1FB
    OPEN_UI_OPTION((short) 0x20D),//1FD
    INTRO_LOCK((short) 0x20E),//1FE
    INTRO_ENABLE_UI((short) 0x218),//210
    INTRO_DISABLE_UI((short) 0x219),//211
    SUMMON_HINT((short) 0x220),//212
    SUMMON_HINT_MSG((short) 0x221),//213 // guess
    ARAN_COMBO((short) 0x214),//203
    ARAN_COMBO_RECHARGE((short) 0x215),//204
    RANDOM_EMOTION((short) 0x216),//205
    RADIO_SCHEDULE((short) 0x217),//206
    OPEN_SKILL_GUIDE((short) 0x218),//207
    GAME_MSG((short) 0x21A),//209
    GAME_MESSAGE((short) 0x223),//21B
    BUFF_ZONE_EFFECT((short) 0x225),//20C
    GO_CASHSHOP_SN((short) 0x226),//20D
    DAMAGE_METER((short) 0x227),//20E
    TIME_BOMB_ATTACK((short) 0x999),//20F
    FOLLOW_MOVE((short) 0x999),//20D
    FOLLOW_MSG((short) 0x999),//211
    AP_SP_EVENT((short) 0x999),//215
    QUEST_GUIDE_NPC((short) 0x999),//214
    REGISTER_FAMILIAR((short) 0x999),//218
    FAMILIAR_MESSAGE((short) 0x999),//219
    CREATE_ULTIMATE((short) 0x999),//21A
    HARVEST_MESSAGE((short) 0x999),//21C
    SHOW_MAP_NAME((short) 0x999),
    OPEN_BAG((short) 0x999),//21D
    DRAGON_BLINK((short) 0x999),//21E
    PVP_ICEGAGE((short) 0x999),//21F
    DIRECTION_INFO((short) 0x23C),//223
    REISSUE_MEDAL((short) 0x235),//224
    PLAY_MOVIE((short) 0x238),//227
    BOSS_ARENA((short) 0x225),
    CAKE_VS_PIE((short) 0x228),//225
    PHANTOM_CARD((short) 0x229),//226
    LUMINOUS_COMBO((short) 0x22A),//229
    MOVE_SCREEN_X((short) 0x199),//199
    MOVE_SCREEN_DOWN((short) 0x19A),//19A
    CAKE_PIE_INSTRUMENTS((short) 0x19B),//19B
    SEALED_BOX((short) 0x218),//212
    
    COOLDOWN((short) 0x280),    
    SPAWN_SUMMON((short) 0x282),
    REMOVE_SUMMON((short) 0x283),
    MOVE_SUMMON((short) 0x284),
    SUMMON_ATTACK((short) 0xFFF),
    PVP_SUMMON((short) 0xFFF),
    SUMMON_SKILL((short) 0xFFF),
    DAMAGE_SUMMON((short) 0xFFF),
    
    SPAWN_MONSTER((short) 0x298),//277
    KILL_MONSTER((short) 0x299),//278
    SPAWN_MONSTER_CONTROL((short) 0x29A),//279
    MOVE_MONSTER((short) 0x29C),//27B
    MOVE_MONSTER_RESPONSE((short) 0x29D),//v149.2
    APPLY_MONSTER_STATUS((short) 0x295),//27E
    CANCEL_MONSTER_STATUS((short) 0x296),//27F
    MONSTER_SKILL((short) 0x298),//281
    DAMAGE_MONSTER((short) 0x299),//282
    SKILL_EFFECT_MOB((short) 0x29A),//283
    TELE_MONSTER((short) 0x999),
    MONSTER_CRC_CHANGE((short) 0x29C),//285
    SHOW_MONSTER_HP((short) 0x29D),//286
    SHOW_MAGNET((short) 0x29E),//287
    ITEM_EFFECT_MOB((short) 0x29F),//288
    CATCH_MONSTER((short) 0x289),
    MONSTER_PROPERTIES((short) 0x1BF),
    REMOVE_TALK_MONSTER((short) 0x1C0),
    TALK_MONSTER((short) 0x28A),
    CYGNUS_ATTACK((short) 0x999),
    MOB_TO_MOB_DAMAGE((short) 0x999),
    MONSTER_RESIST((short) 0x999),
    AZWAN_MOB_TO_MOB_DAMAGE((short) 0x999),
    AZWAN_SPAWN_MONSTER((short) 0x999),
    AZWAN_KILL_MONSTER((short) 0x999),
    AZWAN_SPAWN_MONSTER_CONTROL((short) 0x999),
    SPAWN_NPC((short) 0x2C4),//2A2
    REMOVE_NPC((short) 0x2C5),//2A3
    UNK((short) 0x2C6),
    SPAWN_NPC_REQUEST_CONTROLLER((short) 0x2C7),//2A5
    NPC_ACTION((short) 0x2BE),//2A6
    NPC_TOGGLE_VISIBLE((short) 0x2C2),//2AA
    INITIAL_QUIZ((short) 0x2C4),//2AC
    NPC_UPDATE_LIMITED_INFO((short) 0x2C5),//2AD
    NPC_SET_SPECIAL_ACTION((short) 0x2C6),//2AE
    //
    NPC_SCRIPTABLE((short) 0x2CA),//v149
    RED_LEAF_HIGH((short) 0x2CB),//2CB
    SPAWN_HIRED_MERCHANT((short) 0x2D3),//updated to v149.3
    DESTROY_HIRED_MERCHANT((short) 0x2D4),//updated to v149.3 
    UPDATE_HIRED_MERCHANT((short) 0x2D5),//updated to v149.3
    DROP_ITEM_FROM_MAPOBJECT((short) 0x2D6),//updated to v149.3
    REMOVE_ITEM_FROM_MAP((short) 0x2D8),//2B6
    // UPDATED UNTIL HERE V149
    SPAWN_KITE_ERROR((short) 0x2CF),//2B7
    SPAWN_KITE((short) 0x2B8),
    DESTROY_KITE((short) 0x2B9),
    SPAWN_MIST((short) 0x999),
    REMOVE_MIST((short) 0x999),
    SPAWN_DOOR((short) 0x999),
    REMOVE_DOOR((short) 0x999),
    MECH_DOOR_SPAWN((short) 0x999),
    MECH_DOOR_REMOVE((short) 0x999),
    REACTOR_HIT((short) 0x2E2),//2D8
    REACTOR_MOVE((short) 0x2E3),//2C1
    REACTOR_SPAWN((short) 0x2E4),//2DA // guess
    REACTOR_DESTROY((short) 0x2E6),//2C4
    SPAWN_EXTRACTOR((short) 0x2DD),//2C5
    REMOVE_EXTRACTOR((short) 0x2DE),//2C6
    ROLL_SNOWBALL((short) 0x2DF),//2C7
    HIT_SNOWBALL((short) 0x999),
    SNOWBALL_MESSAGE((short) 0x999),
    LEFT_KNOCK_BACK((short) 0x999),
    HIT_COCONUT((short) 0x999),
    COCONUT_SCORE((short) 0x999),
    MOVE_HEALER((short) 0x2CD),
    PULLEY_STATE((short) 0x2CE),
    MONSTER_CARNIVAL_START((short) 0x2CF),//2C9
    MONSTER_CARNIVAL_OBTAINED_CP((short) 0x2D0),//2CA
    MONSTER_CARNIVAL_STATS((short) 0x2D1),////2CB
    MONSTER_CARNIVAL_SUMMON((short) 0x2D3),//2CD
    MONSTER_CARNIVAL_MESSAGE((short) 0x2D4),//2CE
    MONSTER_CARNIVAL_DIED((short) 0x2D5),//2CF
    MONSTER_CARNIVAL_LEAVE((short) 0x999),//2D0
    MONSTER_CARNIVAL_RESULT((short) 0x2D7),//2D1
    MONSTER_CARNIVAL_RANKING((short) 0x999),//2D8
    ARIANT_SCORE_UPDATE((short) 0x300),
    SHEEP_RANCH_INFO((short) 0x301),
    SHEEP_RANCH_CLOTHES((short) 0x999),//0x302
    WITCH_TOWER((short) 0x999),//0x303
    EXPEDITION_CHALLENGE((short) 0x999),//0x304
    ZAKUM_SHRINE((short) 0x999),
    CHAOS_ZAKUM_SHRINE((short) 0x999),
    PVP_TYPE((short) 0x307),
    PVP_TRANSFORM((short) 0x308),
    PVP_DETAILS((short) 0x309),
    PVP_ENABLED((short) 0x30A),
    PVP_SCORE((short) 0x30B),
    PVP_RESULT((short) 0x30C),
    PVP_TEAM((short) 0x30D),
    PVP_SCOREBOARD((short) 0x30E),
    PVP_POINTS((short) 0x310),
    PVP_KILLED((short) 0x311),
    PVP_MODE((short) 0x312),
    PVP_ICEKNIGHT((short) 0x313),//
    HORNTAIL_SHRINE((short) 0x999),
    CAPTURE_FLAGS((short) 0x2E2),
    CAPTURE_POSITION((short) 0x2E3),
    CAPTURE_RESET((short) 0x2E4),
    PINK_ZAKUM_SHRINE((short) 0x999),
    NPC_TALK((short) 0x375),//v149.3
    OPEN_NPC_SHOP((short) 0x376),//v149.3
    CONFIRM_SHOP_TRANSACTION((short) 0x377),//v149.3
    OPEN_STORAGE((short) 0x38D),//37A
    MERCH_ITEM_MSG((short) 0x38F),//345
    MERCH_ITEM_STORE((short) 0x390),//346
    RPS_GAME((short) 0x391),//347
    MESSENGER((short) 0x392),////2F5
    PLAYER_INTERACTION((short) 0x393),//2F6
    VICIOUS_HAMMER((short) 0x2F4),
    LOGOUT_GIFT((short) 0x2FB),
    TOURNAMENT((short) 0x236),
    TOURNAMENT_MATCH_TABLE((short) 0x237),
    TOURNAMENT_SET_PRIZE((short) 0x238),
    TOURNAMENT_UEW((short) 0x239),
    TOURNAMENT_CHARACTERS((short) 0x23A),
    WEDDING_PROGRESS((short) 0x236),
    WEDDING_CEREMONY_END((short) 0x237),
    PACKAGE_OPERATION((short) 0x389),
    CS_CHARGE_CASH((short) 0x2CA),
    CS_EXP_PURCHASE((short) 0x23B),
    GIFT_RESULT((short) 0x23C),
    CHANGE_NAME_CHECK((short) 0x23D),
    CHANGE_NAME_RESPONSE((short) 0x23E),
    
    CS_UPDATE((short) 0x39F), // updated to v149
    CS_OPERATION((short) 0x3A0), // updated to v149
    CS_MESO_UPDATE((short) 0x3A1), // updated to v149
    CASH_SHOP((short) 0x3B7), // updated to v149
    CASH_SHOP_UPDATE((short) 0x3B8), // updated to v149
    
    GACHAPON_STAMPS((short) 0x253),
    FREE_CASH_ITEM((short) 0x254),
    CS_SURPRISE((short) 0x255),
    XMAS_SURPRISE((short) 0x256),
    ONE_A_DAY((short) 0x258),
    NX_SPEND_GIFT((short) 0x999),
    RECEIVE_GIFT((short) 0x25A),//new v145
    RANDOM_CHECK((short) 0x274),//25E
    KEYMAP((short) 0x3D1),//37C
    PET_AUTO_HP((short) 0x37D),//377
    PET_AUTO_MP((short) 0x37E),//378
    PET_AUTO_CURE((short) 0x37F),//379
    START_TV((short) 0x380),//37A
    REMOVE_TV((short) 0x381),//37B
    ENABLE_TV((short) 0x37C),//37C
    GM_ERROR((short) 0x26D),
    ALIEN_SOCKET_CREATOR((short) 0x341),
    GOLDEN_HAMMER((short) 0x279),
    BATTLE_RECORD_DAMAGE_INFO((short) 0x27A),
    CALCULATE_REQUEST_RESULT((short) 0x27B),
    BOOSTER_PACK((short) 0x999),
    BOOSTER_FAMILIAR((short) 0x999),
    BLOCK_PORTAL((short) 0x999),
    NPC_CONFIRM((short) 0x999),
    RSA_KEY((short) 0x999),
    LOGIN_AUTH((short) 0x999),
    PET_FLAG_CHANGE((short) 0x999),
    BUFF_BAR((short) 0x999),
    GAME_POLL_REPLY((short) 0x999),
    GAME_POLL_QUESTION((short) 0x999),
    ENGLISH_QUIZ((short) 0x999),
    FISHING_BOARD_UPDATE((short) 0x999),
    BOAT_EFFECT((short) 0x999),
    FISHING_CAUGHT((short) 0x999),
    SIDEKICK_OPERATION((short) 0x999),
    FARM_PACKET1((short) 0x35C),
    FARM_ITEM_PURCHASED((short) 0x35D),
    FARM_ITEM_GAIN((short) 0x358),
    HARVEST_WARU((short) 0x35A),
    FARM_MONSTER_GAIN((short) 0x999),
    FARM_INFO((short) 0x999),
    FARM_MONSTER_INFO((short) 0x369),
    FARM_QUEST_DATA((short) 0x36A),
    FARM_QUEST_INFO((short) 0x36B),
    FARM_MESSAGE((short) 0x999),//36C
    UPDATE_MONSTER((short) 0x36D),
    AESTHETIC_POINT((short) 0x36E),
    UPDATE_WARU((short) 0x36F),
    FARM_EXP((short) 0x374),
    FARM_PACKET4((short) 0x375),
    QUEST_ALERT((short) 0x377),
    FARM_PACKET8((short) 0x378),
    FARM_FRIENDS_BUDDY_REQUEST((short) 0x37B),
    FARM_FRIENDS((short) 0x37C),
    FARM_USER_INFO((short) 0x3F0),//388
    FARM_AVATAR((short) 0x3F2),//38A
    FRIEND_INFO((short) 0x3F5),//38D
    FARM_RANKING((short) 0x3F7),//38F
    SPAWN_FARM_MONSTER1((short) 0x3FB),//393
    SPAWN_FARM_MONSTER2((short) 0x3FC),//394
    RENAME_MONSTER((short) 0x3FD),//395
    STRENGTHEN_UI((short) 0x408),//402
    //Unplaced:
    DEATH_COUNT((short) 0x206),

    REDIRECTOR_COMMAND((short) 0x1337);

    private short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public short getValue() {
        return getValue(true);
    }

    public short getValue(boolean show) {
            String tab = "";
            for (int i = 4; i > this.name().length() / 8; i--) {
                tab += "\t";
            }
            System.out.println("[Send]\t" + this.name() + tab + "|\t" + this.code + "\t|\t" + HexTool.getOpcodeToString(this.code)/* + "\r\nCaller: " + Thread.currentThread().getStackTrace()[2] */);
            FileoutputUtil.log("PacketLog.txt", "\r\n\r\n[Send]\t" + this.name() + tab + "|\t" + this.code + "\t|\t" + HexTool.getOpcodeToString(this.code) + "\r\n\r\n");
        return code;
    }

    private SendPacketOpcode(short code) {
        this.code = code;
    }

    public String getType(short code) {
        String type = null;
        if (code >= 0 && code < 0xE || code >= 0x17 && code < 0x21) {
            type = "CLogin";
        } else if (code >= 0xE && code < 0x17) {
            type = "LoginSecure";
        } else if (code >= 0x21 && code < 0xCB) {
            type = "CWvsContext";
        } else if (code >= 0xD2) {
            type = "CField";
        }
        return type;
    }

    public static String getOpcodeName(int value) {
        for (SendPacketOpcode opcode : SendPacketOpcode.values()) {
            if (opcode.getValue(false) == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

    public boolean isSpamHeader(SendPacketOpcode opcode) {
        switch (opcode) {
            case PING:
            case AUTH_RESPONSE:
            case SERVERLIST:
            case UPDATE_STATS:
            case MOVE_PLAYER:
            case SPAWN_NPC:
            case SPAWN_NPC_REQUEST_CONTROLLER:
            case REMOVE_NPC:
            case MOVE_MONSTER:
            case MOVE_MONSTER_RESPONSE:
            case SPAWN_MONSTER:
            case SPAWN_MONSTER_CONTROL:
            case HAKU_MOVE:
            /*case MOVE_SUMMON:
             case MOVE_FAMILIAR:
            
             case ANDROID_MOVE:
             case INVENTORY_OPERATION:*/
            case MOVE_PET:
            case SHOW_SPECIAL_EFFECT:
            case DROP_ITEM_FROM_MAPOBJECT:
            case REMOVE_ITEM_FROM_MAP:
            //case UPDATE_PARTYMEMBER_HP:
            case DAMAGE_PLAYER:
            case SHOW_MONSTER_HP:
            case CLOSE_RANGE_ATTACK:
            case RANGED_ATTACK:
            //case ARAN_COMBO:
            case REMOVE_BG_LAYER:
            case SPECIAL_STAT:
            case TOP_MSG:
            case NPC_ACTION:
//            case ANGELIC_CHANGE:
            case UPDATE_CHAR_LOOK:
            case KILL_MONSTER:
                return true;
        }
        return false;
    }
}
