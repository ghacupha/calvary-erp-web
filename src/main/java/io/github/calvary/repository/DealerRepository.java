package io.github.calvary.repository;

import io.github.calvary.domain.Dealer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Dealer entity.
 */
@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long>, JpaSpecificationExecutor<Dealer> {
    default Optional<Dealer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Dealer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Dealer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select dealer from Dealer dealer left join fetch dealer.dealerType",
        countQuery = "select count(dealer) from Dealer dealer"
    )
    Page<Dealer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select dealer from Dealer dealer left join fetch dealer.dealerType")
    List<Dealer> findAllWithToOneRelationships();

    @Query("select dealer from Dealer dealer left join fetch dealer.dealerType where dealer.id =:id")
    Optional<Dealer> findOneWithToOneRelationships(@Param("id") Long id);
}
