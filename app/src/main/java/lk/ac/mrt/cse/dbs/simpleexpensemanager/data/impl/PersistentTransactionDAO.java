package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseConstants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteOpenHelper helper;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",
            Locale.getDefault());

    public PersistentTransactionDAO(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if (accountNo == null) return;

        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.TRANSACTION_DATE, dateFormat.format(date));
        values.put(DatabaseConstants.TRANSACTION_ACCOUNTNO, accountNo);
        values.put(DatabaseConstants.TRANSACTION_TYPE, expenseType.toString());
        values.put(DatabaseConstants.TRANSACTION_AMOUNT, amount);
        database.insert(DatabaseConstants.TRANSACTION_TABLE, null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DatabaseConstants.TRANSACTION_TABLE +
                " order by " + DatabaseConstants.TRANSACTION_ID + " desc ", null);
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expenseTypeStr = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.EXPENSE;
                if (expenseTypeStr.equals(DatabaseConstants.TYPE_INCOME)) {
                    expenseType = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expenseType, cursor.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException ignored) {
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DatabaseConstants.TRANSACTION_TABLE +
                " order by " + DatabaseConstants.TRANSACTION_ID + " desc " +
                " limit ?;", new String[]{Integer.toString(limit)});
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expenseTypeStr = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.EXPENSE;
                if (expenseTypeStr.equals(DatabaseConstants.TYPE_INCOME)) {
                    expenseType = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expenseType, cursor.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException ignored) {
            }
        }
        cursor.close();
        return transactions;
    }
}
