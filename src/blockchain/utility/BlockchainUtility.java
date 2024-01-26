package blockchain.utility;

import blockchain.chain.Block;
import blockchain.chain.Blockchain;
import blockchain.transaction.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class BlockchainUtility {

    public static String applySha256(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte elem: hash) {
                String hex = Integer.toHexString(0xff & elem);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHash(String hashOfPreviousBlock, int id, long timestamp, int nonce, int minerId,
                                    String merkleRoot) {
        String input = hashOfPreviousBlock + id + timestamp + minerId + nonce + merkleRoot;
        return applySha256(input);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        Blockchain blockchain = Blockchain.getInstance();
        String hashTarget = new String(new char[Blockchain.getDifficulty()]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.getBlocks().size(); i++) {
            currentBlock = blockchain.getBlocks().get(i);
            previousBlock = blockchain.getBlocks().get(i - 1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getBlockHash().equals(createHash(currentBlock.getPreviousHash(), currentBlock.getId(),
                    currentBlock.getTimeStamp(), currentBlock.getNonce(), currentBlock.getMinerId(),
                    TransactionUtility.getMerkleRoot(currentBlock.getTransactions()))) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getBlockHash().equals(createHash(currentBlock.getPreviousHash(), currentBlock.getId(),
                    currentBlock.getTimeStamp(), currentBlock.getNonce(), currentBlock.getMinerId(),
                    TransactionUtility.getMerkleRoot(currentBlock.getTransactions()))) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getBlockHash().substring( 0, Blockchain.getDifficulty()).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
