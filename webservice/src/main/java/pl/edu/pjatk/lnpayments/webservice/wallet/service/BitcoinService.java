package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.exception.BroadcastException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;

import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.bitcoinj.core.Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;

@Service
public class BitcoinService {

    private final WalletAppKit walletAppKit;

    @Autowired
    public BitcoinService(WalletAppKit walletAppKit) {
        this.walletAppKit = walletAppKit;
    }

    public Wallet createWallet(List<AdminUser> admins, int minNumberOfConfirmations) {
        List<ECKey> keys = mapToKeys(admins);
        Script redeemScript = ScriptBuilder.createRedeemScript(minNumberOfConfirmations, keys);
        Script scriptPubKey = ScriptBuilder.createP2SHOutputScript(redeemScript);
        Address address = scriptPubKey.getToAddress(walletAppKit.params());
        walletAppKit.wallet().addWatchedAddress(address);
        return Wallet.builder()
                .address(address.toString())
                .redeemScript(HexFormat.of().formatHex(redeemScript.getProgram()))
                .scriptPubKey(HexFormat.of().formatHex(scriptPubKey.getProgram()))
                .users(admins)
                .minSignatures(minNumberOfConfirmations)
                .build();
    }

    public BitcoinWalletBalance getBalance() {
        org.bitcoinj.wallet.Wallet wallet = walletAppKit.wallet();
        Coin availableBalance = wallet.getBalance(BalanceType.AVAILABLE);
        Coin estimatedBalance = wallet.getBalance(BalanceType.ESTIMATED);
        Coin unconfirmedBalance = availableBalance.minus(estimatedBalance);
        return BitcoinWalletBalance.builder()
                .availableBalance(availableBalance.value)
                .unconfirmedBalance(unconfirmedBalance.value)
                .currentReferenceFee(REFERENCE_DEFAULT_MIN_TX_FEE.value)
                .build();
    }

    public pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction createTransaction(
            String sourceAddress, String targetAddress, long amount) {
        NetworkParameters params = walletAppKit.params();
        Address fromAddress = Address.fromString(params, sourceAddress);
        Address toAddress = Address.fromString(params, targetAddress);
        BitcoinWalletBalance balance = getBalance();
        Transaction payingToMultisigTx = buildTxWithInputs(params, fromAddress);
        Coin value = Coin.valueOf(amount);
        Coin inputSum = payingToMultisigTx.getInputSum();
        Coin finalValue = inputSum.subtract(REFERENCE_DEFAULT_MIN_TX_FEE).subtract(value);
        if (value.add(REFERENCE_DEFAULT_MIN_TX_FEE).isGreaterThan(Coin.valueOf(balance.getAvailableBalance()))) {
            throw new InconsistentDataException("Transaction amount is higher than balance with fees");
        }
        TransactionOutput output = new TransactionOutput(params, payingToMultisigTx, value, toAddress);
        payingToMultisigTx.addOutput(output);
        TransactionOutput returnRest = new TransactionOutput(params, payingToMultisigTx, finalValue, fromAddress);
        payingToMultisigTx.addOutput(returnRest);
        return pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction.builder()
                .rawTransaction(payingToMultisigTx.toHexString())
                .sourceAddress(sourceAddress)
                .targetAddress(targetAddress)
                .fee(payingToMultisigTx.getFee().value)
                .inputValue(amount)
                .build();
    }

    public pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction createTransaction(
            String sourceAddress, String targetAddress) {
        NetworkParameters params = walletAppKit.params();
        Address fromAddress = Address.fromString(params, sourceAddress);
        Address toAddress = Address.fromString(params, targetAddress);
        Transaction payingToMultisigTx = buildTxWithInputs(params, fromAddress);
        Coin inputSum = payingToMultisigTx.getInputSum();
        Coin finalSum = inputSum.subtract(REFERENCE_DEFAULT_MIN_TX_FEE);
        TransactionOutput output = new TransactionOutput(params, payingToMultisigTx, finalSum, toAddress);
        payingToMultisigTx.addOutput(output);
        return pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction.builder()
                .rawTransaction(payingToMultisigTx.toHexString())
                .sourceAddress(sourceAddress)
                .targetAddress(targetAddress)
                .fee(payingToMultisigTx.getFee().value)
                .inputValue(finalSum.value)
                .build();
    }

    private Transaction buildTxWithInputs(NetworkParameters params, Address fromAddress) {
        Transaction payingToMultisigTx = new Transaction(params);
        walletAppKit.wallet()
                .getWatchedOutputs(true)
                .stream()
                .filter(output -> output.getScriptPubKey().getToAddress(params).equals(fromAddress))
                .forEach(payingToMultisigTx::addInput);
        return payingToMultisigTx;
    }

    public boolean verifySignature(String transactionHex, String keyHex) {
        ECKey key = ECKey.fromPublicOnly(HexFormat.of().parseHex(keyHex));
        Transaction transaction = new Transaction(walletAppKit.params(), HexFormat.of().parseHex(transactionHex));
        List<TransactionInput> inputs = transaction.getInputs();
        for (int index = 0; index < inputs.size(); index++) {
            TransactionInput transactionInput = inputs.get(index);
            Script scriptSig = transactionInput.getScriptSig();
            List<ScriptChunk> chunks = scriptSig.getChunks();
            if (chunks.size() > 2) {
                ScriptChunk scriptChunk = chunks.get(chunks.size() - 1);
                Script redeemScript = new Script(Objects.requireNonNull(scriptChunk.data));
                List<ECKey> keys = redeemScript.getPubKeys();
                Sha256Hash msg = transaction.hashForSignature(index, redeemScript, Transaction.SigHash.ALL, false);
                if (keys.contains(key)) {
                    for (int chunkIndex = 1; chunkIndex < chunks.size() - 1; chunkIndex++) {
                        try {
                            byte[] signature = chunks.get(chunkIndex).data;
                            ECKey.ECDSASignature ecdsaSignature = TransactionSignature
                                    .decodeFromDER(Objects.requireNonNull(signature));
                            boolean verify = key.verify(msg, ecdsaSignature);
                            if (verify) return true;
                        } catch (SignatureDecodeException ignored) {
                        }
                    }
                }
            }
        }
        return false;
    }

    public void broadcast(String transactionHex, String redeemScriptHex) throws BroadcastException {
        Transaction transaction = new Transaction(walletAppKit.params(), HexFormat.of().parseHex(transactionHex));
        try {
            List<TransactionInput> inputs = transaction.getInputs();
            for (int index = 0; index < inputs.size(); index++) {
                TransactionInput transactionInput = inputs.get(index);
                Script scriptSig = transactionInput.getScriptSig();
                Script redeemScript = new Script(HexFormat.of().parseHex(redeemScriptHex));
                Script scriptPubKey = ScriptBuilder.createP2SHOutputScript(redeemScript);
                scriptSig.correctlySpends(transaction, index, null,
                        null, scriptPubKey, Script.ALL_VERIFY_FLAGS);
            }
            walletAppKit.peerGroup().broadcastTransaction(transaction).broadcast().get(1L, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | ScriptException | TimeoutException e) {
            throw new BroadcastException("Failed to broadcast transaction!: " + transactionHex, e);
        }
    }

    private List<ECKey> mapToKeys(List<AdminUser> admins) {
        return admins.stream()
                .map(AdminUser::getPublicKey)
                .map(e -> ECKey.fromPublicOnly(HexFormat.of().parseHex(e)))
                .sorted(ECKey.PUBKEY_COMPARATOR)
                .toList();

    }
}
