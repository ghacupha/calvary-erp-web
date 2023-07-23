package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.BalanceSheetItemValue;
import io.github.calvary.repository.BalanceSheetItemValueRepository;
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
 * Spring Data Elasticsearch repository for the {@link BalanceSheetItemValue} entity.
 */
public interface BalanceSheetItemValueSearchRepository
    extends ElasticsearchRepository<BalanceSheetItemValue, Long>, BalanceSheetItemValueSearchRepositoryInternal {}

interface BalanceSheetItemValueSearchRepositoryInternal {
    Page<BalanceSheetItemValue> search(String query, Pageable pageable);

    Page<BalanceSheetItemValue> search(Query query);

    @Async
    void index(BalanceSheetItemValue entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BalanceSheetItemValueSearchRepositoryInternalImpl implements BalanceSheetItemValueSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BalanceSheetItemValueRepository repository;

    BalanceSheetItemValueSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        BalanceSheetItemValueRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<BalanceSheetItemValue> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<BalanceSheetItemValue> search(Query query) {
        SearchHits<BalanceSheetItemValue> searchHits = elasticsearchTemplate.search(query, BalanceSheetItemValue.class);
        List<BalanceSheetItemValue> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(BalanceSheetItemValue entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), BalanceSheetItemValue.class);
    }
}
