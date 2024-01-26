package blockchain.transaction;

import blockchain.chain.Blockchain;
import blockchain.user.Wallet;
import blockchain.utility.BlockchainUtility;
import blockchain.utility.TransactionUtility;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

    private String transactionId;
    private final Wallet sender;
    private final Wallet recipient;
    private final float value;
    private byte[] signature;

    public ArrayList<TransactionInput> inputs;
    public final ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public boolean processTransaction() {

        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return true;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput transactionInput : inputs) {
            transactionInput.unspent = Blockchain.transactionOutputs.get(transactionInput.transactionOutputId);
        }

        //check if transaction is valid:
        if(getInputsValue() < Blockchain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return true;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the leftover change:
        this.transactionId = calculateHash();
        outputs.add(new TransactionOutput( this.recipient.getPublicKey(), value,transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender.getPublicKey(), leftOver,transactionId)); //send leftover to sender

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            Blockchain.transactionOutputs.put(o.id , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for(TransactionInput transactionInput : inputs) {
            if(transactionInput.unspent == null) continue; //if Transaction can't be found skip it
            Blockchain.transactionOutputs.remove(transactionInput.unspent.id);
        }

        return false;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput transactionInput : inputs) {
            if(transactionInput.unspent == null) continue; //if Transaction can't be found skip it
            total += transactionInput.unspent.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput transactionOutput : outputs) {
            total += transactionOutput.value;
        }
        return total;
    }

    private String calculateHash() {
        sequence++;
        return BlockchainUtility.applySha256(TransactionUtility.getStringFromKey(sender.getPublicKey()) +
                TransactionUtility.getStringFromKey(recipient.getPublicKey()) + value + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = TransactionUtility.getStringFromKey(sender.getPublicKey()) +
                TransactionUtility.getStringFromKey(recipient.getPublicKey()) + value;
        signature = TransactionUtility.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = TransactionUtility.getStringFromKey(sender.getPublicKey())
                + TransactionUtility.getStringFromKey(recipient.getPublicKey()) + value;
        return TransactionUtility.verifyECDSASig(sender.getPublicKey(), data, signature);
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String toString() {
        return sender.getOwner() + "sent " + value + " VC to " + recipient.getOwner() + "\n";
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Wallet getSender() {
        return sender;
    }

    public Wallet getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }

    public Transaction(Wallet from, Wallet to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }
}