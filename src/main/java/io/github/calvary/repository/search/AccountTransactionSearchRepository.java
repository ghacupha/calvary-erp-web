package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.repository.AccountTransactionRepository;
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
 * Spring Data Elasticsearch repository for the {@link AccountTransaction} entity.
 */
public interface AccountTransactionSearchRepository
    extends ElasticsearchRepository<AccountTransaction, Long>, AccountTransactionSearchRepositoryInternal {}

interface AccountTransactionSearchRepositoryInternal {
    Page<AccountTransaction> search(String query, Pageable pageable);

    Page<AccountTransaction> search(Query query);

    @Async
    void index(AccountTransaction entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AccountTransactionSearchRepositoryInternalImpl implements AccountTransactionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AccountTransactionRepository repository;

    AccountTransactionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AccountTransactionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AccountTransaction> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AccountTransaction> search(Query query) {
        SearchHits<AccountTransaction> searchHits = elasticsearchTemplate.search(query, AccountTransaction.class);
        List<AccountTransaction> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AccountTransaction entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AccountTransaction.class);
    }
}
