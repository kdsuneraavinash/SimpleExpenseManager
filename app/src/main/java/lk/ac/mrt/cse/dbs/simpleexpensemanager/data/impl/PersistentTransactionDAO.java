package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase database;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",
            Locale.getDefault());

    public PersistentTransactionDAO(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        ContentValues values = new ContentValues();
        values.put("date", dateFormat.format(date));
        values.put("accountno", accountNo);
        values.put("type", expenseType.toString());
        values.put("amount", amount);

        database.insert("transaction_log", null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor cursor = database.rawQuery("SELECT * from transaction_log;", null);
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expenseTypeStr =  cursor.getString(2);
                ExpenseType expenseType = ExpenseType.EXPENSE;
                if  (expenseTypeStr.equals("INCOME")){
                    expenseType = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expenseType, cursor.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor cursor = database.rawQuery("SELECT * from transaction_log order by id limit ?;", new String[]{Integer.toString(limit)});
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            try {
                String expenseTypeStr =  cursor.getString(2);
                ExpenseType expenseType = ExpenseType.EXPENSE;
                if  (expenseTypeStr.equals("INCOME")){
                    expenseType = ExpenseType.INCOME;
                }
                Transaction transaction = new Transaction(dateFormat.parse(cursor.getString(3)),
                        cursor.getString(1), expenseType, cursor.getDouble(4));
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return transactions;
    }
}
