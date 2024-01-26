package blockchain.chain;

import blockchain.transaction.Transaction;
import blockchain.transaction.TransactionOutput;
import blockchain.user.Wallet;

import java.util.*;

public class Blockchain {
    private final List<Block> blocks = new ArrayList<>();
    public static HashMap<String, TransactionOutput> transactionOutputs = new HashMap<>();
    public static final Wallet coinbase = new Wallet("Coinbase");
    private static Blockchain blockchain;
    public static float minimumTransaction = 0.1f;

    public static Queue<Transaction> transactionPool = new LinkedList<>();

    private static int difficulty = 0;

    public static int getDifficulty() {
        return difficulty;
    }

    private Blockchain() {
    }

    public static Blockchain getInstance() {
        if (blockchain == null)
            blockchain = new Blockchain();
        return blockchain;
    }

    public void addBlock(Block block) {
        blocks.add(block);
        System.out.println(block);
        if ((block.getGenerationTime() / 1000) / (difficulty * 10L + 1) < 1) {
            difficulty++;
            System.out.println("N was increased to " + difficulty);
        }
        else if ((block.getGenerationTime() / 1000) / (difficulty * 10L + 1) > 1) {
            difficulty--;
            System.out.println("N was decreased by 1");
        } else {
            System.out.println("N stays the same");
        }
    }

    public String getLatestHash() {
        return blocks.size() != 0 ?blocks.get(blocks.size() - 1).getBlockHash() : "0";
    }

    public List<Block> getBlocks() {
        return blocks;
    }

}
