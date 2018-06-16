package ru.android.cyfral.servisnik.model;

/**
 * Created by joe on 30.04.2018.
 */

public class Constants {
    public static final class HTTP {
        public static final String BASE_URL_TOKEN = "https://testauth.cyfral-group.ru/";
        public static final String BASE_URL_REQUEST = "https://testservice.cyfral-group.ru/";
    }

    public static final class DATABASE {

        public static final String DB_NAME = "datas";
        public static final int DB_VERSION =10;
        public static final String TABLE_NAME_DATAS = "data";
        public static final String TABLE_NAME_CONTACTS = "contacts";
        public static final String TABLE_NAME_ORDER_CARD = "order_card";
        public static final String TABLE_NAME_INFO_ENTRANCE = "info_entrance";
        public static final String TABLE_NAME_ENTRANCE_TO = "entrance_to";

        public static final String DROP_QUERY_DATAS = "DROP TABLE IF EXISTS " + TABLE_NAME_DATAS;
        public static final String DROP_QUERY_CONTACTS = "DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS;
        public static final String DROP_QUERY_ORDER_CARD = "DROP TABLE IF EXISTS " + TABLE_NAME_ORDER_CARD;
        public static final String DROP_QUERY_INFO_ENTRANCE = "DROP TABLE IF EXISTS " + TABLE_NAME_INFO_ENTRANCE;
        public static final String DROP_QUERY_ENTRANCE_TO = "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRANCE_TO;

        public static final String DELETE_DATA_ENTRANCE_TO = "DELETE FROM "+TABLE_NAME_ENTRANCE_TO;

        public static final String GET_DATAS_QUERY = "SELECT * FROM " + TABLE_NAME_DATAS;



        //ID
        public static final String DATA_ID = "data_id";
        //ADRESS
        public static final String BUILDING = "building";
        public static final String FLOOR = "floor";
        public static final String LETTER = "letter";
        public static final String CITYTYPE = "cityType";
        public static final String STREET = "street";
        public static final String APARTMENT = "apartment";
        public static final String NUMBER = "number_appartament";
        public static final String STREETTYPE = "streetType";
        public static final String ENTRANCE = "entrance";
        public static final String ROOM = "room";
        public static final String CITY = "city";

        public static final String ISVIEWED = "isViewed";
        public static final String NUMBER_ = "number_data";
        public static final String DEADLINE = "deadline";
        public static final String PHONEFORSEARCH = "phones";

        //WORKS
        public static final String ELEMENT ="element_works";
        public static final String TYPE_WORKS ="type_works" ;
        public static final String GROUP = "group_works";

        //CONTACTS
        public static final String CONTACTS_ID = "contact_id";
        public static final String MIDDLENAME = "middleName";
        public static final String NAME = "name";
        public static final String FAMILYNAME = "familyName";
        public static final String TYPE = "type";
        public static final String PHONES = "phones";


        //Order Card---------------------------------------------------------------------------START
        public static final String ID_GUID_ORDER_CARD = "id_guid";
        public static final String JSON_ORDER_CARD = "json_body";
        //Info Entrance--------------------------------------------------------------------------END


        //Info Entrance------------------------------------------------------------------------START
        public static final String ID_GUID_INFO_ENTRANCE = "id_guid";
        public static final String JSON_INFO_ENTRANCE = "json_body";
        //Info Entrance--------------------------------------------------------------------------END

        //Entrance to--------------------------------------------------------------------------START
        public static final String ID_GUID_ENTRANCE_TO = "id_guid";
        public static final String JSON_ENTRANCE_TO = "json_body";
        public static final String NAME_STREET_ENTRANCE_TO = "name_street";
        //Entrance to----------------------------------------------------------------------------END

        //создание таблицы для Entrance To
        public static final String CREATE_TABLE_ENTRANCE_TO = "CREATE TABLE "
                + TABLE_NAME_ENTRANCE_TO
                + "(" + ID_GUID_ENTRANCE_TO+ " TEXT, "
                + " " + JSON_ENTRANCE_TO+ " TEXT, "
                + " " + NAME_STREET_ENTRANCE_TO + " TEXT)";

        //создание таблицы для Info Entrance
        public static final String CREATE_TABLE_QUERY_INFO_ENTRANCE = "CREATE TABLE "
                + TABLE_NAME_INFO_ENTRANCE
                + "(" + ID_GUID_INFO_ENTRANCE+" TEXT, " +
                " " +JSON_INFO_ENTRANCE+" TEXT)";

        //создание таблицы для Order Card
        public static final String CREATE_TABLE_QUERY_ORDER_CARD = "CREATE TABLE "
                + TABLE_NAME_ORDER_CARD
                + "(" + ID_GUID_ORDER_CARD+" TEXT, " +
                " " +JSON_ORDER_CARD+" TEXT)";

        //получить объект Order Card по ID
        public static final String GET_DATAS_QUERY_ORDER_CARD = "SELECT * FROM "+TABLE_NAME_ORDER_CARD+" where "+ID_GUID_ORDER_CARD+ " like '";

        //получить объект Info Entrance по ID
        public static final String GET_DATAS_QUERY_INFO_ENTRANCE = "SELECT * FROM "+TABLE_NAME_INFO_ENTRANCE+" where "+ID_GUID_INFO_ENTRANCE+ " like '";

        //получить объекты Entrance to по ID
        public static final String GET_DATAS_QUERY_ENTRANCE_TO = "SELECT * FROM "+TABLE_NAME_ENTRANCE_TO;

        public static final String GET_CONTACTS_QUERY = "SELECT * FROM " + TABLE_NAME_CONTACTS + " WHERE "+CONTACTS_ID+" like '";
        public static final String GET_DATAS_QUERY_FOR_STREET = "SELECT * FROM " + TABLE_NAME_DATAS + " where "+STREET+ " like '%";
        public static final String GET_DATAS_QUERY_FOR_PHONE = "SELECT * FROM " + TABLE_NAME_DATAS + " where "+PHONEFORSEARCH+ " like '%";
        public static final String GET_DATAS_QUERY_FOR_NUMBER_ZN = "SELECT * FROM " + TABLE_NAME_DATAS + " where "+NUMBER_+ " like '%";

        //удаление объекта ORDER CARD
        public static final String DELETE_DATAS_ORDER_CARD = "DELETE FROM "+TABLE_NAME_ORDER_CARD + " where "+ID_GUID_ORDER_CARD + " like '";

        //удаление объекта INFO ENTRANCE
        public static final String DELETE_DATAS_INFO_ENTRANCE = "DELETE FROM "+TABLE_NAME_INFO_ENTRANCE + " where "+ID_GUID_INFO_ENTRANCE + " like '";

        //удаление объкта ENTRANCES_TO
        public static final String DELETE_DATAS_ENTRANCE_TO = "DELETE FROM "+TABLE_NAME_ENTRANCE_TO + " where "+ ID_GUID_ENTRANCE_TO + " like '";


        public static final String CREATE_TABLE_QUERY_DATAS = "CREATE TABLE " + TABLE_NAME_DATAS + "" +
                "(" + DATA_ID + " TEXT," +
                BUILDING + " TEXT," +
                FLOOR + " TEXT," +
                LETTER + " TEXT," +
                CITYTYPE + " TEXT," +
                STREET + " TEXT," +
                APARTMENT+ " TEXT," +
                NUMBER+ " TEXT," +
                STREETTYPE+ " TEXT," +
                ENTRANCE+ " TEXT," +
                ROOM+ " TEXT," +
                CITY+ " TEXT," +
                ISVIEWED+ " TEXT," +
                NUMBER_+ " TEXT," +
                DEADLINE+ " datetime," +
                PHONEFORSEARCH+ " TEXT, " +
                ELEMENT+ " TEXT, " +
                TYPE_WORKS+ " TEXT, " +
                GROUP+ " TEXT)";


        public static final String CREATE_TABLE_QUERY_CONTACST = "CREATE TABLE " + TABLE_NAME_CONTACTS + "" +
                "(" + CONTACTS_ID + " TEXT," +
                MIDDLENAME + " TEXT," +
                NAME + " TEXT," +
                FAMILYNAME + " TEXT," +
                TYPE + " TEXT," +
                PHONES + " TEXT)";

    }

    public static final class SEARCH {
        public static final String NAME_STREET = "Название улицы";
        public static final String NUMBER_ZN = "№ заказ-наряда";
        public static final String NUMBER_PHONE = "Номер телефона";

    }
    public static final class SETTINGS {
        public static final String GUID = "guid";
        public static final String MY_PREFS = "myPrefs";
        public static final String TOKEN = "token";
        public static final String REFRESH_TOKEN = "token_refresh";
        public static final String DATE_TOKEN = "date_token";
        public static final String DATE_REFRESH_TOKEN = "date_refresh_token";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE  = "longitude";
        public static final long ONE_SECUNDE_IN_MILLIS = 1000;

    }

    public static final class FIRST_LOAD_APP {

        public static boolean ENTRANCE_TO_FIRST = true;
        public static int TAB_GENERAL_APP = 0;

    }

}
