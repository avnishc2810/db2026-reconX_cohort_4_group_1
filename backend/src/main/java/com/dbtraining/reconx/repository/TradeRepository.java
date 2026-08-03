package com.dbtraining.reconx.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbtraining.reconx.repository.entity.Trade;

/**
 * ============================================================================
 * TICKET-ADV055 — Custom JPQL filter query
 * TICKET-ADV056 — Specification-based dynamic queries (JpaSpecificationExecutor)
 * TICKET-ADV057 — Pageable / Page<T> for paginated list endpoints
 * ============================================================================
 */
// public interface TradeRepository
//         extends JpaRepository<Trade, Long>, JpaSpecificationExecutor<Trade> {

//     Optional<Trade> findByTradeRef(String tradeRef);

//     @Query("""
//         SELECT t FROM Trade t
//         WHERE t.tradeDate BETWEEN :from AND :to
//           AND (:status IS NULL OR t.status = :status)
//           AND (:counterpartyId IS NULL OR t.counterparty.id = :counterpartyId)
//         """)
//     Page<Trade> findByFilters(@Param("from") LocalDate from,
//                               @Param("to") LocalDate to,
//                               @Param("status") String status,
//                               Pageable pageable);

//     long countByStatus(String status);
// }

public interface TradeRepository
        extends JpaRepository<Trade, Long>,
                JpaSpecificationExecutor<Trade> {

    Optional<Trade> findByTradeRef(String tradeRef);

    /**
     * Locks available opposite-side legs while a new trade is reconciled, so
     * concurrent creates cannot match the same trade twice.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Trade> findByInstrument_IdAndSideAndStatusInOrderByCreatedAtAscIdAsc(
            Long instrumentId, String side, Collection<String> statuses);

    @EntityGraph(attributePaths = {
            "instrument",
            "counterparty"
    })
    @Override
    Page<Trade> findAll(
            Specification<Trade> spec,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Trade t
        WHERE t.tradeDate BETWEEN :from AND :to
          AND (:status IS NULL OR t.status = :status)
          AND (:counterpartyId IS NULL OR t.counterparty.id = :counterpartyId)
        """)
    Page<Trade> findByFilters(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") String status,
            Pageable pageable
    );

    long countByStatus(String status);
}
