package io.github.calvary.repository;

import io.github.calvary.domain.AccountingEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccountingEvent entity.
 */
@Repository
public interface AccountingEventRepository extends JpaRepository<AccountingEvent, Long>, JpaSpecificationExecutor<AccountingEvent> {
    default Optional<AccountingEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AccountingEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AccountingEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer",
        countQuery = "select count(accountingEvent) from AccountingEvent accountingEvent"
    )
    Page<AccountingEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer"
    )
    List<AccountingEvent> findAllWithToOneRelationships();

    @Query(
        "select accountingEvent from AccountingEvent accountingEvent left join fetch accountingEvent.eventType left join fetch accountingEvent.dealer where accountingEvent.id =:id"
    )
    Optional<AccountingEvent> findOneWithToOneRelationships(@Param("id") Long id);
}
