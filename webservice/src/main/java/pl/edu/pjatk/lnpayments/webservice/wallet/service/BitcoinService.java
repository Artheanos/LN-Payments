package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;

import java.util.HexFormat;
import java.util.List;

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
                .build();
    }

    private List<ECKey> mapToKeys(List<AdminUser> admins) {
        return admins.stream()
                .map(AdminUser::getPublicKey)
                .map(e -> ECKey.fromPublicOnly(HexFormat.of().parseHex(e)))
                .sorted(ECKey.PUBKEY_COMPARATOR)
                .toList();
    }
}
