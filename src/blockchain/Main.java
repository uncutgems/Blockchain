package blockchain;

import blockchain.chain.Blockchain;
import blockchain.chain.HashlessBlock;
import blockchain.transaction.Transaction;
import blockchain.user.Miner;
import blockchain.user.Wallet;
import blockchain.utility.TransactionUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Blockchain blockchain = Blockchain.getInstance();
    private static final Queue<Transaction> transactions = Blockchain.transactionPool;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Wallet wallet1 = new Wallet("miner 1");
        Wallet wallet2 = new Wallet("miner 2");
        Wallet wallet3 = new Wallet("miner 3");
        Wallet wallet4 = new Wallet("miner 4");
        Wallet wallet5 = new Wallet("miner 5");
        Wallet wallet6 = new Wallet("miner 6");
        Wallet wallet7 = new Wallet("miner 7");

        Wallet CarShop = new Wallet("CarShop");
        Wallet Worker1 = new Wallet("Worker1");
        Wallet Worker2 = new Wallet("Worker2");
        Wallet Worker3 = new Wallet("Worker3");
        Wallet Director1 = new Wallet("Director1");
        Wallet CarPartShop = new Wallet("CarPartsShop");

        Wallet Nick = new Wallet("Nick");
        Wallet Alice = new Wallet("Alice");
        Wallet Bob = new Wallet("Bob");

        Wallet ShoesShop = new Wallet("ShoesShop");
        Wallet FastFood = new Wallet("FastFood");
        Wallet GamingShop = new Wallet("GamingShop");
        Wallet BeautyShop = new Wallet("BeautyShop");

        Miner miner1 = new Miner(wallet1, 0);
        Miner miner2 = new Miner(wallet2, 1);
        Miner miner3 = new Miner(wallet3, 2);
        Miner miner4 = new Miner(wallet4, 3);
        Miner miner5 = new Miner(wallet5, 4);
        Miner miner6 = new Miner(wallet6, 5);
        Miner miner7 = new Miner(wallet7, 6);
        List<Miner> miners = new ArrayList<>();
        miners.add(miner1);
        miners.add(miner2);
        miners.add(miner3);
        miners.add(miner4);
        miners.add(miner5);
        miners.add(miner6);
        miners.add(miner7);

        giveMoney(wallet1, wallet2, wallet3, wallet4, wallet5, wallet6, wallet7, CarShop, Worker1, Worker2);
        giveMoney(Worker3, Director1, CarPartShop, Nick, Alice, Bob, ShoesShop, FastFood, GamingShop, BeautyShop);
        mining(miners);


        // Transaction enters blockchain
        transactions.add(wallet1.sendFunds(wallet2, 30f));
        transactions.add(wallet1.sendFunds(wallet3, 30f));
        transactions.add(wallet1.sendFunds(Nick, 30f));
        mining(miners);


        // Transaction enters blockchain
        transactions.add(CarShop.sendFunds(Worker1, 10f));
        transactions.add(CarShop.sendFunds(Worker2, 10f));
        mining(miners);


        transactions.add(CarShop.sendFunds(Worker3, 10f));
        transactions.add(CarShop.sendFunds(Director1, 30));
        mining(miners);


        transactions.add(CarShop.sendFunds(CarPartShop, 45));
        transactions.add(Bob.sendFunds(GamingShop, 5));
        transactions.add(Alice.sendFunds(BeautyShop, 5));
        mining(miners);
    }

    private static void giveMoney(Wallet wallet1, Wallet wallet2, Wallet wallet3, Wallet wallet4, Wallet wallet5, Wallet wallet6, Wallet wallet7, Wallet carShop, Wallet worker1, Wallet worker2) {
        TransactionUtility.makeGenesisTransaction(wallet1);
        TransactionUtility.makeGenesisTransaction(wallet2);
        TransactionUtility.makeGenesisTransaction(wallet3);
        TransactionUtility.makeGenesisTransaction(wallet4);
        TransactionUtility.makeGenesisTransaction(wallet5);
        TransactionUtility.makeGenesisTransaction(wallet6);
        TransactionUtility.makeGenesisTransaction(wallet7);
        TransactionUtility.makeGenesisTransaction(carShop);
        TransactionUtility.makeGenesisTransaction(worker1);
        TransactionUtility.makeGenesisTransaction(worker2);
    }

    public static void mining(List<Miner> miners) throws ExecutionException, InterruptedException {
        HashlessBlock hashlessBlock = new HashlessBlock(blockchain.getBlocks().size() + 1, blockchain.getLatestHash());
        Iterator<Transaction> transactionIterator = transactions.iterator();

        while (transactionIterator.hasNext()) {
            hashlessBlock.addTransaction(transactions.remove());
        }

        for (Miner miner : miners)
            miner.setUnfinishedBlock(hashlessBlock);
        blockchain.addBlock(executorService.invokeAny(miners));
    }
}
