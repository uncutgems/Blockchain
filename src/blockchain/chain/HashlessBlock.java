package blockchain.chain;

import blockchain.transaction.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class HashlessBlock implements Serializable {
    private final int id;
    private final long timeStamp;
    private final String previousHash;
    private final ArrayList<Transaction> transactions;

    public void addTransaction(Transaction transaction) {
        if(transaction == null) return;
        if((!previousHash.equals("0"))) {
            if((transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
    }

    public int getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public HashlessBlock(int id, String previousHash) {
        this.id = id;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.transactions = new ArrayList<>();
    }
}
