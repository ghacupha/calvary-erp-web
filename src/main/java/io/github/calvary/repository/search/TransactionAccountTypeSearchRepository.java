package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link TransactionAccountType} entity.
 */
public interface TransactionAccountTypeSearchRepository
    extends ElasticsearchRepository<TransactionAccountType, Long>, TransactionAccountTypeSearchRepositoryInternal {}

interface TransactionAccountTypeSearchRepositoryInternal {
    Page<TransactionAccountType> search(String query, Pageable pageable);

    Page<TransactionAccountType> search(Query query);

    @Async
    void index(TransactionAccountType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TransactionAccountTypeSearchRepositoryInternalImpl implements TransactionAccountTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionAccountTypeRepository repository;

    TransactionAccountTypeSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        TransactionAccountTypeRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionAccountType> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionAccountType> search(Query query) {
        SearchHits<TransactionAccountType> searchHits = elasticsearchTemplate.search(query, TransactionAccountType.class);
        List<TransactionAccountType> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TransactionAccountType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TransactionAccountType.class);
    }
}
