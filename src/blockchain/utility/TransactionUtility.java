package blockchain.utility;

import blockchain.chain.Blockchain;
import blockchain.transaction.Transaction;
import blockchain.transaction.TransactionOutput;
import blockchain.user.Wallet;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class TransactionUtility {
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("DSA", "SUN");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("DSA", "SUN");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(BlockchainUtility.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    public static void makeGenesisTransaction(Wallet wallet) {
        Transaction genesisTransaction = new Transaction(Blockchain.coinbase, wallet, 100f, null);
        genesisTransaction.generateSignature(Blockchain.coinbase.getPrivateKey());
        genesisTransaction.setTransactionId(wallet.getOwner());
        ArrayList<TransactionOutput> outputs = genesisTransaction.outputs;
        outputs.add(new TransactionOutput(genesisTransaction.getRecipient().getPublicKey(),
                genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
        Blockchain.transactionOutputs.put(outputs.get(outputs.size() - 1).id, outputs.get(outputs.size() - 1));
    }
}
