package io.github.calvary.repository.search;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.queryStringQuery;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.repository.TransactionEntryRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link TransactionEntry} entity.
 */
public interface TransactionEntrySearchRepository
    extends ElasticsearchRepository<TransactionEntry, Long>, TransactionEntrySearchRepositoryInternal {}

interface TransactionEntrySearchRepositoryInternal {
    Page<TransactionEntry> search(String query, Pageable pageable);

    Page<TransactionEntry> search(Query query);

    @Async
    void index(TransactionEntry entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TransactionEntrySearchRepositoryInternalImpl implements TransactionEntrySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionEntryRepository repository;

    TransactionEntrySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TransactionEntryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionEntry> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionEntry> search(Query query) {
        SearchHits<TransactionEntry> searchHits = elasticsearchTemplate.search(query, TransactionEntry.class);
        List<TransactionEntry> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionEntry entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TransactionEntry.class);
    }
}
