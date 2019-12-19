package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "170081L";
    private final static int DATABASE_VERSION = 1;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        if (tableMissing(database, "account")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS account(" +
                    "accountno VARCHAR(15) PRIMARY KEY," +
                    "bankname VARCHAR(255)," +
                    "holdername VARCHAR(255)," +
                    "balance NUMERIC(12, 2)" +
                    ");");
        }

        if (tableMissing(database, "expense_type")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS expense_type(type VARCHAR(31) PRIMARY KEY);");
            database.execSQL("INSERT INTO expense_type(type) VALUES ('EXPENSE');");
            database.execSQL("INSERT INTO expense_type(type) VALUES ('INCOME');");
        }

        if (tableMissing(database, "transaction_log")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS transaction_log(" +
                    "id INTEGER PRIMARY KEY," +
                    "accountno VARCHAR(15) NOT NULL," + // 2
                    "type VARCHAR(31) NOT NULL," + // 3
                    "date TIMESTAMP NOT NULL," +  //1
                    "amount NUMERIC(12, 2) NOT NULL," + // 4
                    "FOREIGN KEY (accountno) REFERENCES account(accountno)," +
                    "FOREIGN KEY (type) REFERENCES expense_type(type)" +
                    ");");
        }
    }

    private boolean tableMissing(SQLiteDatabase database, String tableName) {
        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'";
        try (Cursor cursor = database.rawQuery(query, null)) {
            if (cursor != null) {
                return cursor.getCount() <= 0;
            }
            return true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(), "Upgrading db from "+oldVersion+" to "+newVersion);
        database.execSQL("DROP TABLE IF EXISTS transaction_log;");
        database.execSQL("DROP TABLE IF EXISTS expense_type;");
        database.execSQL("DROP TABLE IF EXISTS account;");
        onCreate(database);
    }
}