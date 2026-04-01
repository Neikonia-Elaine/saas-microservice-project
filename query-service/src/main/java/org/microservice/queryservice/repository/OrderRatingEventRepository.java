package org.microservice.queryservice.repository;

import org.microservice.queryservice.entity.OrderRatingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRatingEventRepository extends JpaRepository<OrderRatingEvent, String> {

    // [storeId, totalCount, negativeCount]  (negative = attitude = 'negative')
    @Query("""
            SELECT e.storeId,
                   COUNT(e),
                   SUM(CASE WHEN e.attitude = 'negative' THEN 1 ELSE 0 END)
            FROM OrderRatingEvent e
            WHERE e.tenantId = :tenantId
              AND e.createdAt >= :from
              AND e.createdAt <= :to
            GROUP BY e.storeId
            """)
    List<Object[]> findStoreStats(@Param("tenantId") String tenantId,
                                  @Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(e)
            FROM OrderRatingEvent e
            WHERE e.tenantId = :tenantId
              AND e.createdAt >= :from
              AND e.createdAt <= :to
            """)
    long countTotal(@Param("tenantId") String tenantId,
                    @Param("from") LocalDateTime from,
                    @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(e)
            FROM OrderRatingEvent e
            WHERE e.tenantId = :tenantId
              AND e.createdAt >= :from
              AND e.createdAt <= :to
              AND e.attitude = 'negative'
            """)
    long countNegative(@Param("tenantId") String tenantId,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to);
}
