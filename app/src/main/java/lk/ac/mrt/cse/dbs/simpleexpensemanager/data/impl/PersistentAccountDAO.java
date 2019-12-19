package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseConstants;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteOpenHelper helper;

    public PersistentAccountDAO(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT " + DatabaseConstants.ACCOUNT_ACCOUNTNO + " from "
                + DatabaseConstants.ACCOUNT_TABLE, null);
        ArrayList<String> accountNumbers = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DatabaseConstants.ACCOUNT_TABLE, null);
        ArrayList<Account> accounts = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Account account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
            accounts.add(account);
        }
        cursor.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DatabaseConstants.ACCOUNT_TABLE + " WHERE " + DatabaseConstants.ACCOUNT_ACCOUNTNO + "=?;", new String[]{accountNo});
        Account account;
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getDouble(3));
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.ACCOUNT_ACCOUNTNO, account.getAccountNo());
        values.put(DatabaseConstants.ACCOUNT_BANKNAME, account.getBankName());
        values.put(DatabaseConstants.ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        values.put(DatabaseConstants.ACCOUNT_BALANCE, account.getBalance());

        database.insert(DatabaseConstants.ACCOUNT_TABLE, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * from " + DatabaseConstants.ACCOUNT_TABLE + " WHERE " + DatabaseConstants.ACCOUNT_ACCOUNTNO + "=?;", new String[]{accountNo});
        if (cursor.moveToFirst()) {
            database.delete(DatabaseConstants.ACCOUNT_TABLE, DatabaseConstants.ACCOUNT_ACCOUNTNO + " = ?", new String[]{accountNo});
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = helper.getWritableDatabase();

        if (accountNo == null) throw new InvalidAccountException("Account was not selected");

        database.beginTransaction();
        Account account = getAccount(accountNo);

        if (account != null) {
            double newAmount;
            if (expenseType == ExpenseType.EXPENSE) {
                newAmount = account.getBalance() - amount;
            } else if (expenseType == ExpenseType.INCOME) {
                newAmount = account.getBalance() + amount;
            } else {
                throw new InvalidAccountException("Unknown Expense Type");
            }

            if (newAmount < 0){
                throw  new InvalidAccountException("Insufficient balance. (" + account.getBalance() + " in the account)");
            }

            database.execSQL("UPDATE " + DatabaseConstants.ACCOUNT_TABLE + " SET "
                            + DatabaseConstants.ACCOUNT_BALANCE + " = ? WHERE " +
                            DatabaseConstants.ACCOUNT_ACCOUNTNO + " = ?",
                    new String[]{Double.toString(newAmount), accountNo});
            database.endTransaction();
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
    }
}
