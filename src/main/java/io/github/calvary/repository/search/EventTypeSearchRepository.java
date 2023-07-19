package io.github.calvary.repository.search;


import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.github.calvary.domain.EventType;
import io.github.calvary.repository.EventTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link EventType} entity.
 */
public interface EventTypeSearchRepository extends ElasticsearchRepository<EventType, Long>, EventTypeSearchRepositoryInternal {}

interface EventTypeSearchRepositoryInternal {
    Page<EventType> search(String query, Pageable pageable);

    Page<EventType> search(Query query);

    @Async
    void index(EventType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EventTypeSearchRepositoryInternalImpl implements EventTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EventTypeRepository repository;

    EventTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EventTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<EventType> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<EventType> search(Query query) {
        SearchHits<EventType> searchHits = elasticsearchTemplate.search(query, EventType.class);
        List<EventType> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(EventType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), EventType.class);
    }
}
