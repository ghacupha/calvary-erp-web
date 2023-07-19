package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.DealerType;
import io.github.calvary.repository.DealerTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link DealerType} entity.
 */
public interface DealerTypeSearchRepository extends ElasticsearchRepository<DealerType, Long>, DealerTypeSearchRepositoryInternal {}

interface DealerTypeSearchRepositoryInternal {
    Page<DealerType> search(String query, Pageable pageable);

    Page<DealerType> search(Query query);

    @Async
    void index(DealerType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DealerTypeSearchRepositoryInternalImpl implements DealerTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DealerTypeRepository repository;

    DealerTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DealerTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<DealerType> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<DealerType> search(Query query) {
        SearchHits<DealerType> searchHits = elasticsearchTemplate.search(query, DealerType.class);
        List<DealerType> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(DealerType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), DealerType.class);
    }
}
