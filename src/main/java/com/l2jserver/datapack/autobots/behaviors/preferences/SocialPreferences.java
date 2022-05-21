package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SocialPreferences {
    @JsonProperty("townAction")
    public TownAction townAction = TownAction.None;
    @JsonProperty("tradingAction")
    public TradingAction tradingAction;

    public SocialPreferences(){

    }

    public SocialPreferences(TownAction townAction) {
        this.townAction = townAction;
    }

    public SocialPreferences(TradingAction tradingAction) {
        this.tradingAction = tradingAction;
    }

    public static class TradingAction {
        @JsonProperty("looksFor")
        public List<TradingItem> looksForItems;
        @JsonProperty("offers")
        public List<TradingItem> offersItems;
    }


    public static final class TradingItem {
        @JsonProperty("itemId")
        public int itemId;
        @JsonProperty("itemCount")
        public int itemCount;
    }

    public enum TownAction {
        None,
        TeleToRandomLocation,
        TeleToSpecificLocation,
        Trade
    }

}

    
