package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.TransactionCurrency;
import io.github.calvary.repository.TransactionCurrencyRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransactionCurrency} entity.
 */
public interface TransactionCurrencySearchRepository
    extends ElasticsearchRepository<TransactionCurrency, Long>, TransactionCurrencySearchRepositoryInternal {}

interface TransactionCurrencySearchRepositoryInternal {
    Page<TransactionCurrency> search(String query, Pageable pageable);

    Page<TransactionCurrency> search(Query query);

    @Async
    void index(TransactionCurrency entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TransactionCurrencySearchRepositoryInternalImpl implements TransactionCurrencySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionCurrencyRepository repository;

    TransactionCurrencySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TransactionCurrencyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionCurrency> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionCurrency> search(Query query) {
        SearchHits<TransactionCurrency> searchHits = elasticsearchTemplate.search(query, TransactionCurrency.class);
        List<TransactionCurrency> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionCurrency entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TransactionCurrency.class);
    }
}
