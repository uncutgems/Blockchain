package blockchain.chain;


import blockchain.transaction.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable{
    private final int id;
    private final long timeStamp;
    private final String previousHash;
    private final ArrayList<Transaction> transactions;
    private final int nonce;
    private final long generationTime;
    private final int minerId;
    private final String blockHash;

    private String transactionsDisplay() {
        StringBuilder listOfTransaction = new StringBuilder();
        for (Transaction transaction : transactions) {
            listOfTransaction.append(transaction.toString());
        }
        return transactions.size() != 0? listOfTransaction.toString() : "No transactions";
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

    public int getNonce() {
        return nonce;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public int getMinerId() {
        return minerId;
    }

    public String getBlockHash() {
        return blockHash;
    }


    @Override
    public String toString() {
        return "\nBlock: " +
                "\nCreated by miner # " + minerId +
                "\nId: " + id +
                "\nTimestamp: " + timeStamp +
                "\nMagic number: " + nonce +
                "\nHash of the previous block: \n" + previousHash +
                "\nHash of the block: \n" + blockHash +
                "\nBlock data: \n" + transactionsDisplay() +
                "\nBlock was generating for " + generationTime / 1000 + " seconds";
    }

    public Block(HashlessBlock hashlessBlock, int nonce, long generationTime, int minerId, String blochHash) {
        this.id = hashlessBlock.getId();
        this.timeStamp = hashlessBlock.getTimeStamp();
        this.previousHash = hashlessBlock.getPreviousHash();
        this.nonce = nonce;
        this.generationTime = generationTime;
        this.minerId = minerId;
        this.blockHash = blochHash;
        this.transactions = hashlessBlock.getTransactions();
    }
}
