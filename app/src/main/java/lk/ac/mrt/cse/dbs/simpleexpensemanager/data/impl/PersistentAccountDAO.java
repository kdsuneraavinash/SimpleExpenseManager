package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteDatabase database;

    public PersistentAccountDAO(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public List<String> getAccountNumbersList() {
        // TODO: User String.format
        Cursor cursor = database.rawQuery("SELECT accountno from account;", null);
        ArrayList<String> accountNumbers = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor cursor = database.rawQuery("SELECT * from account;", null);
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
        Cursor cursor = database.rawQuery("SELECT * from account WHERE accountno='" + accountNo + "';", null);
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
        ContentValues values = new ContentValues();
        values.put("accountno", account.getAccountNo());
        values.put("bankname", account.getBankName());
        values.put("holdername", account.getAccountHolderName());
        values.put("balance", account.getBalance());

        database.insert("account", null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = database.rawQuery("SELECT * from account WHERE accountno='" + accountNo + "';", null);
        if (cursor.moveToFirst()) {
            database.delete("account", "accountno = ?", new String[]{accountNo});
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);

        if (account != null) {

            double newAmount;
            if (expenseType.equals(ExpenseType.EXPENSE)) {
                newAmount = account.getBalance() - amount;
            } else if (expenseType.equals(ExpenseType.INCOME)) {
                newAmount = account.getBalance() + amount;
            } else {
                throw new InvalidAccountException("Unknown Expense Type");
            }

            database.execSQL("UPDATE account SET balance = " + newAmount + " WHERE accountno = " + accountNo);
        } else {
            throw new InvalidAccountException("Invalid account ID");
        }
    }
}
