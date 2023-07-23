package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import java.util.List;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link BalanceSheetItemType} entity.
 */
public interface BalanceSheetItemTypeSearchRepository
    extends ElasticsearchRepository<BalanceSheetItemType, Long>, BalanceSheetItemTypeSearchRepositoryInternal {}

interface BalanceSheetItemTypeSearchRepositoryInternal {
    Page<BalanceSheetItemType> search(String query, Pageable pageable);

    Page<BalanceSheetItemType> search(Query query);

    @Async
    void index(BalanceSheetItemType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BalanceSheetItemTypeSearchRepositoryInternalImpl implements BalanceSheetItemTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BalanceSheetItemTypeRepository repository;

    BalanceSheetItemTypeSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        BalanceSheetItemTypeRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<BalanceSheetItemType> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<BalanceSheetItemType> search(Query query) {
        SearchHits<BalanceSheetItemType> searchHits = elasticsearchTemplate.search(query, BalanceSheetItemType.class);
        List<BalanceSheetItemType> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(BalanceSheetItemType entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), BalanceSheetItemType.class);
    }
}
