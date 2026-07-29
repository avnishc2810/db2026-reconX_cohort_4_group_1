package com.dbtraining.reconx.model;

import java.util.Comparator;
import java.time.LocalDate;

public sealed interface TradeType
        extends Comparable<TradeType>
        permits EquityTrade, FXTrade, BondTrade, DerivativeTrade {

    TradeRef tradeRef();

    Money notional();

    LocalDate tradeDate();

    AssetClass assetClass();
        Comparator<TradeType> NATURAL = Comparator
            .comparing(TradeType::tradeDate).reversed()
            .thenComparing(t -> t.tradeRef().value());

    @Override
    default int compareTo(TradeType other) {
        return NATURAL.compare(this, other);
    }

    enum AssetClass {
        EQUITY,
        FX,
        BOND,
        DERIVATIVE
    }
}