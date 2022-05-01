package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;

import java.util.HexFormat;
import java.util.List;

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

    public Transaction createTransaction(String sourceAddress, String targetAddress, long amount) {
        NetworkParameters params = walletAppKit.params();
        Address fromAddress = Address.fromString(params, sourceAddress);
        Address toAddress = Address.fromString(params, targetAddress);
        BitcoinWalletBalance balance = getBalance();
        org.bitcoinj.core.Transaction payingToMultisigTx = buildTxWithInputs(params, fromAddress);
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
        return Transaction.builder()
                .rawTransaction(payingToMultisigTx.toHexString())
                .sourceAddress(sourceAddress)
                .targetAddress(targetAddress)
                .fee(payingToMultisigTx.getFee().value)
                .inputValue(amount)
                .build();
    }

    private org.bitcoinj.core.Transaction buildTxWithInputs(NetworkParameters params, Address fromAddress) {
        org.bitcoinj.core.Transaction payingToMultisigTx = new org.bitcoinj.core.Transaction(params);
        walletAppKit.wallet()
                .getWatchedOutputs(true)
                .stream()
                .filter(output -> output.getScriptPubKey().getToAddress(params).equals(fromAddress))
                .forEach(payingToMultisigTx::addInput);
        return payingToMultisigTx;
    }

    private List<ECKey> mapToKeys(List<AdminUser> admins) {
        return admins.stream()
                .map(AdminUser::getPublicKey)
                .map(e -> ECKey.fromPublicOnly(HexFormat.of().parseHex(e)))
                .sorted(ECKey.PUBKEY_COMPARATOR)
                .toList();
    }
}
