package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.SendCoinsRequest;
import org.lightningj.lnd.wrapper.message.SendCoinsResponse;
import org.lightningj.lnd.wrapper.message.WalletBalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.LightningWalletBalance;

@Service
public class LightningWalletService {

    private final SynchronousLndAPI lndAPI;
    private final PropertyService propertyService;

    @Autowired
    public LightningWalletService(SynchronousLndAPI lndAPI, PropertyService propertyService) {
        this.lndAPI = lndAPI;
        this.propertyService = propertyService;
    }

    public String transfer(String toAddress) {
        try {
            if (lndAPI.walletBalance().getConfirmedBalance() == 0) {
                throw new InconsistentDataException("Not funds in the wallet");
            }
            SendCoinsRequest req = new SendCoinsRequest();
            req.setSendAll(true);
            req.setAddr(toAddress);
            SendCoinsResponse sendCoinsResponse = lndAPI.sendCoins(req);
            return sendCoinsResponse.getTxid();
        } catch (StatusException | ValidationException e) {
            throw new LightningException("Unable to transfer funds", e);
        }
    }

    public LightningWalletBalance getBalance() {
        try {
            WalletBalanceResponse walletBalance = lndAPI.walletBalance();
            return LightningWalletBalance.builder()
                    .availableBalance(walletBalance.getConfirmedBalance())
                    .unconfirmedBalance(walletBalance.getUnconfirmedBalance())
                    .autoTransferLimit(propertyService.getAutoTransferLimit())
                    .build();
        } catch (StatusException | ValidationException e) {
            throw new LightningException("Unable to obtain wallet balance", e);
        }
    }

}
