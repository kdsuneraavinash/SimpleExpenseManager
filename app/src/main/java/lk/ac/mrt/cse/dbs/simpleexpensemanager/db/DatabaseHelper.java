package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "170081L";
    private final static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseConstants.ACCOUNT_TABLE + "(" +
                DatabaseConstants.ACCOUNT_ACCOUNTNO + " VARCHAR PRIMARY KEY," +
                DatabaseConstants.ACCOUNT_BANKNAME + " VARCHAR," +
                DatabaseConstants.ACCOUNT_HOLDERNAME + " VARCHAR," +
                DatabaseConstants.ACCOUNT_BALANCE + " NUMERIC" +
                ");");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseConstants.EXPENSE_TYPE_TABLE +
                "(" + DatabaseConstants.EXPENSE_TYPE_TYPE + " VARCHAR(31) PRIMARY KEY);");
        database.execSQL("INSERT INTO " + DatabaseConstants.EXPENSE_TYPE_TABLE +
                "(" + DatabaseConstants.EXPENSE_TYPE_TYPE + ") VALUES (" + DatabaseConstants.TYPE_EXPENSE +");");
        database.execSQL("INSERT INTO " + DatabaseConstants.EXPENSE_TYPE_TABLE +
                "(" + DatabaseConstants.EXPENSE_TYPE_TYPE + ") VALUES (" + DatabaseConstants.TYPE_INCOME +");");

        database.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseConstants.TRANSACTION_TABLE + "(" +
                DatabaseConstants.TRANSACTION_ID + " INTEGER PRIMARY KEY," +
                DatabaseConstants.TRANSACTION_ACCOUNTNO + " VARCHAR NOT NULL," +
                DatabaseConstants.TRANSACTION_TYPE + " VARCHAR NOT NULL," +
                DatabaseConstants.TRANSACTION_DATE + " TIMESTAMP NOT NULL," +
                DatabaseConstants.TRANSACTION_AMOUNT + " NUMERIC NOT NULL," +
                "FOREIGN KEY (" + DatabaseConstants.TRANSACTION_ACCOUNTNO + ") REFERENCES "
                + DatabaseConstants.ACCOUNT_TABLE + "(" + DatabaseConstants.ACCOUNT_ACCOUNTNO + ")," +
                "FOREIGN KEY (" + DatabaseConstants.TRANSACTION_TYPE + ") REFERENCES "
                + DatabaseConstants.EXPENSE_TYPE_TABLE + "(" + DatabaseConstants.EXPENSE_TYPE_TYPE + ")" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String message = String.format("Upgrading db from %s to %s", Integer.toString(oldVersion), Integer.toString(newVersion));
        Log.w(this.getClass().getName(), message);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TRANSACTION_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.EXPENSE_TYPE_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.ACCOUNT_TABLE);
        onCreate(database);
    }
}