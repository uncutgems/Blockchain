package blockchain.user;

import blockchain.chain.Blockchain;
import blockchain.transaction.Transaction;
import blockchain.transaction.TransactionInput;
import blockchain.transaction.TransactionOutput;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final String owner;

    public HashMap<String,TransactionOutput> UTXOs = new HashMap<>();

    public Wallet(String owner) {
        //for testing convenience, every account will receive money when they open a blockchain wallet
        this.owner = owner;
        generateKeyPair();
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Blockchain.transactionOutputs.entrySet()){
            TransactionOutput transactionOutput = item.getValue();
            if(transactionOutput.checkOwnerShip(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(transactionOutput.id,transactionOutput); //add it to our list of unspent transactions.
                total += transactionOutput.value ;
            }
        }
        return total;
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            //ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(1024, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getOwner() {
        return owner;
    }

    public Transaction sendFunds(Wallet recipient, float value ) {
        if(getBalance() < value) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(this, recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }


}
