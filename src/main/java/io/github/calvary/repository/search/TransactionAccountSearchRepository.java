package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.repository.TransactionAccountRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransactionAccount} entity.
 */
public interface TransactionAccountSearchRepository
    extends ElasticsearchRepository<TransactionAccount, Long>, TransactionAccountSearchRepositoryInternal {}

interface TransactionAccountSearchRepositoryInternal {
    Page<TransactionAccount> search(String query, Pageable pageable);

    Page<TransactionAccount> search(Query query);

    @Async
    void index(TransactionAccount entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TransactionAccountSearchRepositoryInternalImpl implements TransactionAccountSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionAccountRepository repository;

    TransactionAccountSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TransactionAccountRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionAccount> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionAccount> search(Query query) {
        SearchHits<TransactionAccount> searchHits = elasticsearchTemplate.search(query, TransactionAccount.class);
        List<TransactionAccount> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionAccount entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TransactionAccount.class);
    }
}
