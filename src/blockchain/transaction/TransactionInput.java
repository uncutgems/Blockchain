package blockchain.transaction;

public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput unspent;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
