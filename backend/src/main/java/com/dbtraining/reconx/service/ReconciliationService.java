package com.dbtraining.reconx.service;

import com.dbtraining.reconx.observability.ReconMetrics;
import com.dbtraining.reconx.model.ReconResult;
import com.dbtraining.reconx.model.TradeType;
import com.dbtraining.reconx.model.ReconciliationRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconciliationService {

    private final ReconciliationEngine engine;
    private final ReconMetrics reconMetrics;

    public ReconciliationService(ReconciliationEngine engine, ReconMetrics reconMetrics) {
        this.engine = engine;
        this.reconMetrics = reconMetrics;
    }

    public List<ReconResult> runReconciliation(List<TradeType> internal,
                                               List<TradeType> external,
                                               ReconciliationRule rule) {

        return reconMetrics.reconciliationTimer()
                .record(() -> engine.reconcile(internal, external, rule));
    }
}
