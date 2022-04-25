package pl.edu.pjatk.lnpayments.webservice.wallet.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.CreateWalletRequest;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.WalletDetails;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import javax.validation.Valid;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.*;

@RestController
@RequestMapping(WALLET_PATH)
class WalletResource {

    private final WalletService walletService;

    @Autowired
    public WalletResource(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    ResponseEntity<?> createWallet(@Valid @RequestBody CreateWalletRequest createWalletRequest) {
        walletService.createWallet(createWalletRequest.getAdminEmails(), createWalletRequest.getMinSignatures());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    ResponseEntity<WalletDetails> getWalletDetails() {
        WalletDetails walletDetails = walletService.getDetails();
        return ResponseEntity.ok(walletDetails);
    }

    @PostMapping(CLOSE_CHANNELS_PATH)
    ResponseEntity<?> closeAllChannels(@RequestParam(required = false) boolean withForce) {
        walletService.closeAllChannels(withForce);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TRANSFER_PATH)
    ResponseEntity<?> transferFunds() {
        walletService.transferToWallet();
        return ResponseEntity.ok().build();
    }

}
