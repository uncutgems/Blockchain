package blockchain.transaction;

import blockchain.utility.BlockchainUtility;
import blockchain.utility.TransactionUtility;

import java.security.PublicKey;

public class TransactionOutput {
    public final String id;
    public final PublicKey recipient;
    public final float value;
    public final String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = BlockchainUtility.applySha256(TransactionUtility.getStringFromKey(recipient)+
                value +parentTransactionId);
    }

    public boolean checkOwnerShip(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
