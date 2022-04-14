package pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelsBalance {

    private long totalBalance;
    private int openedChannels;
    private long autoChannelCloseLimit;
}
