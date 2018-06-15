package ru.android.cyfral.servisnik.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.DataFetchEntranceTo;
import ru.android.cyfral.servisnik.model.DataFetchInfoEntranceListener;
import ru.android.cyfral.servisnik.model.DataFetchListener;
import ru.android.cyfral.servisnik.model.DataFetchSearchActivity;
import ru.android.cyfral.servisnik.model.entranceto.EntranceTo;
import ru.android.cyfral.servisnik.model.infoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.repairRequests.Address;
import ru.android.cyfral.servisnik.model.repairRequests.Contacts;
import ru.android.cyfral.servisnik.model.repairRequests.Data;
import ru.android.cyfral.servisnik.model.repairRequests.Works;

public class DataDatabase extends SQLiteOpenHelper {

    private static final String TAG = DataDatabase.class.getSimpleName();

    public DataDatabase(Context context) {
        super(context, Constants.DATABASE.DB_NAME, null, Constants.DATABASE.DB_VERSION);
    }

    //создание БД
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY_DATAS);
            sqLiteDatabase.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY_CONTACST);
            sqLiteDatabase.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY_ORDER_CARD);
            sqLiteDatabase.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY_INFO_ENTRANCE);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    //обновление БД
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Constants.DATABASE.DROP_QUERY_DATAS);
        sqLiteDatabase.execSQL(Constants.DATABASE.DROP_QUERY_CONTACTS);
        sqLiteDatabase.execSQL(Constants.DATABASE.DROP_QUERY_ORDER_CARD);
        sqLiteDatabase.execSQL(Constants.DATABASE.DROP_QUERY_INFO_ENTRANCE);
        sqLiteDatabase.execSQL(Constants.DATABASE.DROP_QUERY_ENTRANCE_TO);
        this.onCreate(sqLiteDatabase);
    }

    //Отчистка БД
    public void clearDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Constants.DATABASE.DROP_QUERY_DATAS);
        db.execSQL(Constants.DATABASE.DROP_QUERY_CONTACTS);
        db.execSQL(Constants.DATABASE.DROP_QUERY_ORDER_CARD);
        db.execSQL(Constants.DATABASE.DROP_QUERY_INFO_ENTRANCE);
        db.execSQL(Constants.DATABASE.DROP_QUERY_ENTRANCE_TO);
        this.onCreate(db);
    }

    //сохранение в БД ENTRANCE_TO
    public void addDataEntranceTo(EntranceTo entranceTo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorData = null;
        cursorData = db.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_INFO_ENTRANCE
                +entranceTo.getId()+"' ", null);
        //Если запись есть - удаляем
        if (cursorData.getCount() > 0)  {
            db.execSQL(Constants.DATABASE.DELETE_DATAS_INFO_ENTRANCE+entranceTo.getId()+ "'");
        }
        //записи нет - создаем
        ContentValues valuesDatas = new ContentValues();
        valuesDatas.put(Constants.DATABASE.ID_GUID_ENTRANCE_TO, entranceTo.getId());
        valuesDatas.put(Constants.DATABASE.JSON_ENTRANCE_TO, entranceTo.toString());
        try {

            db.insert(Constants.DATABASE.TABLE_NAME_ENTRANCE_TO, null, valuesDatas);
        } catch (Exception e) {
            Log.d("TABLE_ENTRANCE_TO", e.fillInStackTrace().toString());
        }

    }


    //сохранение в БД одного объекта InfoEntrance
    public void addDataInfoEntrance(InfoEntrance infoEntrance) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorData = null;
        cursorData = db.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_INFO_ENTRANCE
                +infoEntrance.getData().getId()+"' ", null);
        //Если запись есть - удаляем
        if (cursorData.getCount() > 0) {
            db.execSQL(Constants.DATABASE.DELETE_DATAS_INFO_ENTRANCE+infoEntrance.getData().getId()+ "'");
        }
        //записи нет - создаем
        ContentValues valuesDatas = new ContentValues();
        valuesDatas.put(Constants.DATABASE.ID_GUID_INFO_ENTRANCE, infoEntrance.getData().getId());
        valuesDatas.put(Constants.DATABASE.JSON_INFO_ENTRANCE, infoEntrance.toString());

        try {

            db.insert(Constants.DATABASE.TABLE_NAME_INFO_ENTRANCE, null, valuesDatas);
        } catch (Exception e) {
            Log.d("TABLE_INFO_ENTRANCE", e.fillInStackTrace().toString());
        }
    }


    //сохранние в БД одного объекта ORDER CARD
    public void addDataOrderCard(OrderCard orderCard) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursorData = null;
        cursorData = db.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_ORDER_CARD
                +orderCard.getData().getId()+"' ", null);
        //запись есть - удаляем
        if (cursorData.getCount() > 0) {
            db.execSQL(Constants.DATABASE.DELETE_DATAS_ORDER_CARD+orderCard.getData().getId()+ "'");

        }
            //записи нет - создаем
            ContentValues valuesDatas = new ContentValues();
            valuesDatas.put(Constants.DATABASE.ID_GUID_ORDER_CARD, orderCard.getData().getId());
            valuesDatas.put(Constants.DATABASE.JSON_ORDER_CARD, orderCard.toString());
            try {
                db.insert(Constants.DATABASE.TABLE_NAME_ORDER_CARD, null, valuesDatas);
            } catch (Exception e) {
                Log.d("TABLE_ORDER_CARD", e.fillInStackTrace().toString());
            }
    }

    //Сохранения всего списка Request Query
    public void addDataRequest(List<Data> data) {
        SQLiteDatabase db = this.getWritableDatabase();
      //  Log.d(TAG, "Values Got " + data.size());
        for (int z =0; z<data.size(); z ++) {
            ContentValues valuesDatas = new ContentValues();
            //Вставляем Data
            valuesDatas.put(Constants.DATABASE.DATA_ID, data.get(z).getId());
            valuesDatas.put(Constants.DATABASE.BUILDING, data.get(z).getAddress().getBuilding());
            valuesDatas.put(Constants.DATABASE.FLOOR, data.get(z).getAddress().getFloor());
            valuesDatas.put(Constants.DATABASE.LETTER, data.get(z).getAddress().getLetter());
            valuesDatas.put(Constants.DATABASE.CITYTYPE, data.get(z).getAddress().getCityType());
            valuesDatas.put(Constants.DATABASE.STREET, data.get(z).getAddress().getStreet());
            valuesDatas.put(Constants.DATABASE.APARTMENT, data.get(z).getAddress().getApartment());
            valuesDatas.put(Constants.DATABASE.NUMBER, data.get(z).getAddress().getNumber());
            valuesDatas.put(Constants.DATABASE.STREETTYPE, data.get(z).getAddress().getStreetType());
            valuesDatas.put(Constants.DATABASE.ENTRANCE, data.get(z).getAddress().getEntrance());
            valuesDatas.put(Constants.DATABASE.ROOM, data.get(z).getAddress().getRoom());
            valuesDatas.put(Constants.DATABASE.CITY, data.get(z).getAddress().getCity());
            valuesDatas.put(Constants.DATABASE.ISVIEWED, data.get(z).getIsViewed());
            valuesDatas.put(Constants.DATABASE.NUMBER_, data.get(z).getNumber());
            valuesDatas.put(Constants.DATABASE.DEADLINE, data.get(z).getDeadline());
            valuesDatas.put(Constants.DATABASE.ELEMENT, data.get(z).getWorks().getElement());
            valuesDatas.put(Constants.DATABASE.TYPE_WORKS, data.get(z).getWorks().getType());
            valuesDatas.put(Constants.DATABASE.GROUP, data.get(z).getWorks().getGroup());

            String phonesForSearch = "";

            try {
                for (int i0=0; i0 <data.get(z).getContacts().size(); i0++) {
                    for(int i1 = 0; i1 < data.get(z).getContacts().get(i0).getPhones().size(); i1++) {
                        phonesForSearch = phonesForSearch + ","+data.get(z).getContacts().get(i0).getPhones().get(i1);
                    }
                }
                valuesDatas.put(Constants.DATABASE.PHONEFORSEARCH, phonesForSearch);
            } catch (Exception ex) {

            }

            try {
                db.insert(Constants.DATABASE.TABLE_NAME_DATAS, null, valuesDatas);
            } catch (Exception e) {
                Log.d("TABLE_NAME_DATAS_error", Constants.DATABASE.TABLE_NAME_DATAS);
            }

            //Вставляем контакты
            try {

                for (int i = 0; i< data.get(z).getContacts().size(); i++) {
                    ContentValues valuesContacts = new ContentValues();
                    valuesContacts.put(Constants.DATABASE.CONTACTS_ID,  data.get(z).getId());
                    valuesContacts.put(Constants.DATABASE.MIDDLENAME,  data.get(z).getContacts().get(i).getMiddleName());
                    valuesContacts.put(Constants.DATABASE.NAME,  data.get(z).getContacts().get(i).getName());
                    valuesContacts.put(Constants.DATABASE.FAMILYNAME,  data.get(z).getContacts().get(i).getFamilyName());
                    valuesContacts.put(Constants.DATABASE.TYPE,  data.get(z).getContacts().get(i).getType());
                    String phones = "";
                    for (int x=0; x<data.get(z).getContacts().get(i).getPhones().size(); x++) {
                        if (x == 0) {
                            phones = data.get(z).getContacts().get(i).getPhones().get(x);
                        } else {
                            phones = phones + "," + data.get(z).getContacts().get(i).getPhones().get(x);
                        }
                    }
                    valuesContacts.put(Constants.DATABASE.PHONES,  phones);
                    try {
                        db.insert(Constants.DATABASE.TABLE_NAME_CONTACTS, null, valuesContacts);
                    } catch (Exception e) {
                        Log.d("TABLE_NAME_CONTACTS_ER", Constants.DATABASE.TABLE_NAME_CONTACTS);
                    }

                }
            } catch (Exception ex) {
                Log.d("CONTACT", "Отсутсвует контакт");
            }

        }

       // db.close();
    }


    //получение объекта EntranceTo DataFetchEntranceTo

    public void fetchDatasForEntranceTo(DataFetchEntranceTo listener, String guid) {
        DataFetcherEntranceTo fetcher = new DataFetcherEntranceTo(listener,this.getWritableDatabase(),guid);
        fetcher.start();
    }

    //получение объекта Order CArd по ID
    public void fetchDatasForOrderCard(DataFetchListener listener, String guid) {
        DataFetcherForOrderCard fetcher = new DataFetcherForOrderCard(listener, this.getWritableDatabase(), guid);
        fetcher.start();
    }

    //получение объета INFO ENTRANCE по ID
    public void fethcDatasForInfoEntrance(DataFetchInfoEntranceListener listener,String guid) {
        DataFetcherInfoEntrance fetcher = new DataFetcherInfoEntrance(listener, this.getWritableDatabase(), guid);
        fetcher.start();
    }


    //получение списка repair request с фильтром
    public void fetchDatasForFiltr(DataFetchSearchActivity listener, String filtr, String filtrType) {
        DataFetcherForFiltr fetcher = new DataFetcherForFiltr(listener, this.getWritableDatabase(), filtr, filtrType);
        fetcher.start();
    }

    //получение кешированного списка Repair Request для главного экрана список ЗН
    public void fetchDatas(DataFetchSearchActivity listener) {
        DataFetcher fetcher = new DataFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }


    public class DataFetcherEntranceTo extends Thread {
        DataFetchEntranceTo mListener;
        private final SQLiteDatabase mDb;
        private String guid = "";

        public DataFetcherEntranceTo(DataFetchEntranceTo listener, SQLiteDatabase db, String guid) {
            mListener = listener;
            mDb = db;
            this.guid = guid;
        }
        @Override
        public void run(){
            Cursor cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_ENTRANCE_TO+guid+"'", null);
            EntranceTo entranceTo = new EntranceTo();

                if (cursorData.getCount() > 0) {
                    if (cursorData.moveToFirst()) {
                        do {
                            Gson gson = new Gson();
                            // Convert JSON to Java Object

                            entranceTo = gson.fromJson(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.JSON_ENTRANCE_TO)), EntranceTo.class);
                            // Convert JSON to JsonElement, and later to String
                            publishData(entranceTo);

                        } while (cursorData.moveToNext());
                    }
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //      mListener.onDeliverAllDatas(dataList);
                        //       mListener.onHideDialog();
                    }
                });

        }
        public void publishData(final EntranceTo data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverData(data);
                }
            });
        }
    }

    public class DataFetcherInfoEntrance extends Thread {
        DataFetchInfoEntranceListener mListener;
        private final SQLiteDatabase mDb;
        private String guid = "";

        public DataFetcherInfoEntrance(DataFetchInfoEntranceListener listener, SQLiteDatabase db, String guid) {
            mListener = listener;
            mDb = db;
            this.guid = guid;
        }
        @Override
        public void run(){
            Cursor cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_INFO_ENTRANCE+guid+"'", null);
            InfoEntrance infoEntrance = new InfoEntrance();
                if (cursorData.getCount() > 0) {
                    if (cursorData.moveToFirst()) {
                        do {
                            Gson gson = new Gson();
                            // Convert JSON to Java Object

                            infoEntrance = gson.fromJson(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.JSON_INFO_ENTRANCE)), InfoEntrance.class);
                            // Convert JSON to JsonElement, and later to String
                            publishData(infoEntrance);

                        } while (cursorData.moveToNext());
                    }
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //      mListener.onDeliverAllDatas(dataList);
                        //       mListener.onHideDialog();
                    }
                });
        }
        public void publishData(final InfoEntrance data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverData(data);
                }
            });
        }
    }


    //палучение объекта ORDER CARD
    public class DataFetcherForOrderCard extends Thread {

        private final DataFetchListener mListener;
        private final SQLiteDatabase mDb;
        private String guid = "";

        public DataFetcherForOrderCard(DataFetchListener listener, SQLiteDatabase db, String guid) {
            mListener = listener;
            mDb = db;
            this.guid = guid;
        }

        @Override
        public void run() {
            Cursor cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_ORDER_CARD+guid+"'", null);
            OrderCard orderCard = new OrderCard();


            if (cursorData.getCount() > 0) {

                if (cursorData.moveToFirst()) {
                    do {
                        Gson gson = new Gson();
                        // Convert JSON to Java Object
                        orderCard = gson.fromJson(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.JSON_ORDER_CARD)), OrderCard.class);
/*                        Log.d("orderCard_json_sss", cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.JSON_ORDER_CARD)));
                        Log.d("orderCard_json_xxx",orderCard.toString());
                        Log.d("orderCard_json_xxx_uid",cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ID_GUID_ORDER_CARD)));*/
                       // Convert JSON to JsonElement, and later to String
                        publishData(orderCard);

                    } while (cursorData.moveToNext());
                }
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
              //      mListener.onDeliverAllDatas(dataList);
             //       mListener.onHideDialog();
                }
            });

        }
        public void publishData(final OrderCard data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverOrderCard(data);
                }
            });
        }

    }

    public class DataFetcherForFiltr extends Thread {

        private final DataFetchSearchActivity mListener;
        private final SQLiteDatabase mDb;
        private String filtr = "";
        private String filtrType = "";

        public DataFetcherForFiltr(DataFetchSearchActivity listener, SQLiteDatabase db, String filtr, String filtrType) {
            mListener = listener;
            mDb = db;
            this.filtr = filtr;
            this.filtrType = filtrType;
        }

        @Override
        public void run() {
            Cursor cursorData = null;
            if (filtrType.equals(Constants.SEARCH.NAME_STREET)) {
                cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_FOR_STREET+filtr
                        +"%' order by "+Constants.DATABASE.DEADLINE+ ", "
                        +Constants.DATABASE.STREET +" desc", null);
            } else if (filtrType.equals(Constants.SEARCH.NUMBER_PHONE)) {
                cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_FOR_PHONE+filtr
                        +"%' order by "+Constants.DATABASE.DEADLINE+ ", "
                        +Constants.DATABASE.STREET +" desc", null);
            } else if (filtrType.equals(Constants.SEARCH.NUMBER_ZN)) {
                cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY_FOR_NUMBER_ZN+filtr
                        +"%' order by "+Constants.DATABASE.DEADLINE+ ", "
                        +Constants.DATABASE.STREET +" desc", null);
            }

           // Log.d("Constants.DATABASE",Constants.DATABASE.GET_DATAS_QUERY_FOR_STREET+filtr+"'");
            final List<Data> dataList = new ArrayList<>();
            final List<Contacts> constantsList = new ArrayList<>();
            if (cursorData.getCount() > 0) {

                if (cursorData.moveToFirst()) {
                    do {
                        Data data = new Data();
                        Address address = new Address();
                        data.setId(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.DATA_ID)));

                        address.setBuilding(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.BUILDING)));
                        address.setFloor(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.FLOOR)));
                        address.setLetter(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.LETTER)));
                        address.setCityType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.CITYTYPE)));
                        address.setStreet(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.STREET)));
                        address.setApartment(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.APARTMENT)));
                        address.setNumber(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.NUMBER)));
                        address.setStreetType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.STREETTYPE)));
                        address.setEntrance(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ENTRANCE)));
                        address.setRoom(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ROOM)));
                        address.setCity(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.CITY)));
                        data.setAddress(address);
                        data.setIsViewed(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ISVIEWED)));
                        data.setNumber(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.NUMBER_)));
                        data.setDeadline(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.DEADLINE)));

                        Works works = new Works();
                        works.setElement(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ELEMENT)));
                        works.setGroup(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.GROUP)));
                        works.setType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.TYPE_WORKS)));
                        data.setWorks(works);

                        //Контакты
                        Cursor cursorContacts = mDb.rawQuery(Constants.DATABASE.GET_CONTACTS_QUERY+data.getId()+"'", null);
                        if (cursorContacts.getCount() > 0) {
                            if (cursorContacts.moveToFirst()) {
                                do {
                                    Contacts contacts = new Contacts();
                                    contacts.setFamilyName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.FAMILYNAME)));
                                    contacts.setMiddleName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.MIDDLENAME)));
                                    contacts.setName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.NAME)));
                                    contacts.setType(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.TYPE)));
                                    String[] phones = cursorContacts.getString(cursorContacts.getColumnIndex
                                            (Constants.DATABASE.PHONES)).split(",");
                             //   Log.d("getPOst_number", phones.toString());
                                    List<String> itemListPhone = Arrays.asList(phones);
                           //         Log.d("getPOst_number", itemListPhone.toString());
                                    contacts.setPhones(itemListPhone);
                                    constantsList.add(contacts);
                                } while (cursorContacts.moveToNext());
                            }
                        }
                        data.setContacts(constantsList);
                        dataList.add(data);
                        //publishData(data);

                    } while (cursorData.moveToNext());
                }
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllDatas(dataList);
                 //   mListener.onHideDialog();
                }
            });

        }
/*        public void publishData(final Data data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverData(data);
                }
            });
        }*/

    }


    public class DataFetcher extends Thread {

        private final DataFetchSearchActivity mListener;
        private final SQLiteDatabase mDb;

        public DataFetcher(DataFetchSearchActivity listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;
        }

        @Override
        public void run() {
            Cursor cursorData = mDb.rawQuery(Constants.DATABASE.GET_DATAS_QUERY +" order by "
                    +Constants.DATABASE.DEADLINE + ", "+Constants.DATABASE.STREET +" desc", null);
            final List<Data> dataList = new ArrayList<>();
            final List<Contacts> constantsList = new ArrayList<>();
            if (cursorData.getCount() > 0) {

                if (cursorData.moveToFirst()) {
                    do {
                        Data data = new Data();
                        Address address = new Address();
                        data.setId(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.DATA_ID)));

                        address.setBuilding(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.BUILDING)));
                        address.setFloor(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.FLOOR)));
                        address.setLetter(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.LETTER)));
                        address.setCityType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.CITYTYPE)));
                        address.setStreet(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.STREET)));
                        address.setApartment(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.APARTMENT)));
                        address.setNumber(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.NUMBER)));
                        address.setStreetType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.STREETTYPE)));
                        address.setEntrance(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ENTRANCE)));
                        address.setRoom(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ROOM)));
                        address.setCity(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.CITY)));
                        data.setAddress(address);
                        data.setIsViewed(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ISVIEWED)));
                        data.setNumber(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.NUMBER_)));
                        data.setDeadline(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.DEADLINE)));

                        Works works = new Works();
                        works.setElement(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.ELEMENT)));
                        works.setGroup(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.GROUP)));
                        works.setType(cursorData.getString(cursorData.getColumnIndex(Constants.DATABASE.TYPE_WORKS)));
                        data.setWorks(works);

                        //Контакты
                        Cursor cursorContacts = mDb.rawQuery(Constants.DATABASE.GET_CONTACTS_QUERY+data.getId()+"'", null);
                        if (cursorContacts.getCount() > 0) {
                            if (cursorContacts.moveToFirst()) {
                                do {
                                    Contacts contacts = new Contacts();
                                    contacts.setFamilyName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.FAMILYNAME)));
                                    contacts.setMiddleName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.MIDDLENAME)));
                                    contacts.setName(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.NAME)));
                                    contacts.setType(cursorContacts.getString(cursorContacts.getColumnIndex(Constants.DATABASE.TYPE)));
                                    String[] phones = cursorContacts.getString(cursorContacts.getColumnIndex
                                            (Constants.DATABASE.PHONES)).split(",");
                               //     Log.d("getPOst_number", phones.toString());
                                    List<String> itemListPhone = Arrays.asList(phones);
                               //     Log.d("getPOst_number", itemListPhone.toString());
                                    contacts.setPhones(itemListPhone);
                                    constantsList.add(contacts);
                                } while (cursorContacts.moveToNext());
                            }
                        }
                        data.setContacts(constantsList);
                        dataList.add(data);
                        publishData(data);

                    } while (cursorData.moveToNext());
                }
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllDatas(dataList);

                }
            });

        }
        public void publishData(final Data data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                   // mListener.onDeliverData(data);
                }
            });
        }

    }

}
