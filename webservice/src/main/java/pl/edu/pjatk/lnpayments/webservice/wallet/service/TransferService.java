package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.SendCoinsRequest;
import org.lightningj.lnd.wrapper.message.SendCoinsResponse;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

@Service
public class TransferService {

    private final SynchronousLndAPI lndAPI;

    public TransferService(SynchronousLndAPI lndAPI) {
        this.lndAPI = lndAPI;
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

}
