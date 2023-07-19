package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.AccountingEvent;
import io.github.calvary.repository.AccountingEventRepository;
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
 * Spring Data Elasticsearch repository for the {@link AccountingEvent} entity.
 */
public interface AccountingEventSearchRepository
    extends ElasticsearchRepository<AccountingEvent, Long>, AccountingEventSearchRepositoryInternal {}

interface AccountingEventSearchRepositoryInternal {
    Page<AccountingEvent> search(String query, Pageable pageable);

    Page<AccountingEvent> search(Query query);

    @Async
    void index(AccountingEvent entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AccountingEventSearchRepositoryInternalImpl implements AccountingEventSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AccountingEventRepository repository;

    AccountingEventSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AccountingEventRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AccountingEvent> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AccountingEvent> search(Query query) {
        SearchHits<AccountingEvent> searchHits = elasticsearchTemplate.search(query, AccountingEvent.class);
        List<AccountingEvent> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AccountingEvent entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AccountingEvent.class);
    }
}
