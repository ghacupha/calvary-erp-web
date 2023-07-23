import React, { useState, useEffect } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { searchEntities, getEntities, reset } from './balance-sheet-item-type.reducer';

export const BalanceSheetItemType = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );
  const [sorting, setSorting] = useState(false);

  const balanceSheetItemTypeList = useAppSelector(state => state.balanceSheetItemType.entities);
  const loading = useAppSelector(state => state.balanceSheetItemType.loading);
  const links = useAppSelector(state => state.balanceSheetItemType.links);
  const updateSuccess = useAppSelector(state => state.balanceSheetItemType.updateSuccess);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    }
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(reset());
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        })
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    dispatch(reset());
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting, search]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  return (
    <div>
      <h2 id="balance-sheet-item-type-heading" data-cy="BalanceSheetItemTypeHeading">
        <Translate contentKey="calvaryErpApp.balanceSheetItemType.home.title">Balance Sheet Item Types</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="calvaryErpApp.balanceSheetItemType.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/balance-sheet-item-type/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="calvaryErpApp.balanceSheetItemType.home.createLabel">Create new Balance Sheet Item Type</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('calvaryErpApp.balanceSheetItemType.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={balanceSheetItemTypeList ? balanceSheetItemTypeList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {balanceSheetItemTypeList && balanceSheetItemTypeList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('itemSequence')}>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.itemSequence">Item Sequence</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('itemSequence')} />
                  </th>
                  <th className="hand" onClick={sort('itemNumber')}>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.itemNumber">Item Number</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('itemNumber')} />
                  </th>
                  <th className="hand" onClick={sort('shortDescription')}>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.shortDescription">Short Description</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('shortDescription')} />
                  </th>
                  <th>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.transactionAccount">Transaction Account</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="calvaryErpApp.balanceSheetItemType.parentItem">Parent Item</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {balanceSheetItemTypeList.map((balanceSheetItemType, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/balance-sheet-item-type/${balanceSheetItemType.id}`} color="link" size="sm">
                        {balanceSheetItemType.id}
                      </Button>
                    </td>
                    <td>{balanceSheetItemType.itemSequence}</td>
                    <td>{balanceSheetItemType.itemNumber}</td>
                    <td>{balanceSheetItemType.shortDescription}</td>
                    <td>
                      {balanceSheetItemType.transactionAccount ? (
                        <Link to={`/transaction-account/${balanceSheetItemType.transactionAccount.id}`}>
                          {balanceSheetItemType.transactionAccount.accountName}
                        </Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td>
                      {balanceSheetItemType.parentItem ? (
                        <Link to={`/balance-sheet-item-type/${balanceSheetItemType.parentItem.id}`}>
                          {balanceSheetItemType.parentItem.itemNumber}
                        </Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          tag={Link}
                          to={`/balance-sheet-item-type/${balanceSheetItemType.id}`}
                          color="info"
                          size="sm"
                          data-cy="entityDetailsButton"
                        >
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          tag={Link}
                          to={`/balance-sheet-item-type/${balanceSheetItemType.id}/edit`}
                          color="primary"
                          size="sm"
                          data-cy="entityEditButton"
                        >
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          tag={Link}
                          to={`/balance-sheet-item-type/${balanceSheetItemType.id}/delete`}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="calvaryErpApp.balanceSheetItemType.home.notFound">No Balance Sheet Item Types found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default BalanceSheetItemType;
