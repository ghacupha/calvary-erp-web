package io.github.calvary.repository;

import io.github.calvary.domain.BalanceSheetItemValue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BalanceSheetItemValue entity.
 */
@Repository
public interface BalanceSheetItemValueRepository
    extends JpaRepository<BalanceSheetItemValue, Long>, JpaSpecificationExecutor<BalanceSheetItemValue> {
    default Optional<BalanceSheetItemValue> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BalanceSheetItemValue> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BalanceSheetItemValue> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select balanceSheetItemValue from BalanceSheetItemValue balanceSheetItemValue left join fetch balanceSheetItemValue.itemType",
        countQuery = "select count(balanceSheetItemValue) from BalanceSheetItemValue balanceSheetItemValue"
    )
    Page<BalanceSheetItemValue> findAllWithToOneRelationships(Pageable pageable);

    @Query("select balanceSheetItemValue from BalanceSheetItemValue balanceSheetItemValue left join fetch balanceSheetItemValue.itemType")
    List<BalanceSheetItemValue> findAllWithToOneRelationships();

    @Query(
        "select balanceSheetItemValue from BalanceSheetItemValue balanceSheetItemValue left join fetch balanceSheetItemValue.itemType where balanceSheetItemValue.id =:id"
    )
    Optional<BalanceSheetItemValue> findOneWithToOneRelationships(@Param("id") Long id);
}
