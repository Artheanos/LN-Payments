@Override
public void proceed(String... args) {
    try {
        api.listChannels(new ListChannelsRequest())
            .getChannels()
            .stream()
            .map(Channel::getChannelPoint)
            .forEach(this::closeChannel);
    } catch (StatusException | ValidationException e) {
        e.printStackTrace();
    }
}

private void closeChannel(String channelPoint) {
    CloseChannelRequest closeChannelRequest = new CloseChannelRequest();
    ChannelPoint point = new ChannelPoint();
    String[] pointArr = channelPoint.split(":");
    point.setFundingTxidStr(pointArr[0]);
    point.setOutputIndex(Integer.parseInt(pointArr[1]));
    closeChannelRequest.setChannelPoint(point);
    try {
        api.closeChannel(closeChannelRequest);
    } catch (StatusException | ValidationException e) {
        e.printStackTrace();
    }
}