package blockchain.user;

import blockchain.chain.Block;
import blockchain.chain.Blockchain;
import blockchain.chain.HashlessBlock;
import blockchain.utility.BlockchainUtility;
import blockchain.utility.TransactionUtility;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class Miner implements Callable<Block> {
    private HashlessBlock unfinishedBlock;
    private final Wallet wallet;
    private final int minerId;

    public Block miningNewBlock() {
        String merkleRoot = TransactionUtility.getMerkleRoot(unfinishedBlock.getTransactions());
        Block result = null;
        if (unfinishedBlock != null && BlockchainUtility.isChainValid()){
            ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
            int nonce;
            String blockHash;
            String target = new String(new char[Blockchain.getDifficulty()]).replace('\0', '0');
            do {
                nonce = threadLocalRandom.nextInt();
                blockHash = BlockchainUtility.createHash(unfinishedBlock.getPreviousHash(), unfinishedBlock.getId(),
                        unfinishedBlock.getTimeStamp(), nonce, minerId, merkleRoot);
            }while(!blockHash.substring(0, Blockchain.getDifficulty()).equals(target));
            long generationTime = new Date().getTime() - unfinishedBlock.getTimeStamp();
            result = new Block(unfinishedBlock, nonce, generationTime, minerId, blockHash);
        }
        TransactionUtility.makeGenesisTransaction(this.wallet);
        return result;
    }

    @Override
    public Block call() {
        return miningNewBlock();
    }

    public void setUnfinishedBlock(HashlessBlock unfinishedBlock) {
        this.unfinishedBlock = unfinishedBlock;
    }

    public HashlessBlock getUnfinishedBlock() {
        return unfinishedBlock;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public int getMinerId() {
        return minerId;
    }

    public Miner(Wallet wallet, int minerId) {
        this.wallet = wallet;
        this.minerId = minerId;
    }
}
