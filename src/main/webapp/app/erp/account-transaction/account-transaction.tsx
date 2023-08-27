import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate, TextFormat, getPaginationState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { searchEntities, getEntities } from './account-transaction.reducer';

export const AccountTransaction = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );

  const accountTransactionList = useAppSelector(state => state.accountTransaction.entities);
  const loading = useAppSelector(state => state.accountTransaction.loading);
  const totalItems = useAppSelector(state => state.accountTransaction.totalItems);

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

  const startSearching = e => {
    if (search) {
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
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
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
      <h2 id="account-transaction-heading" data-cy="AccountTransactionHeading">
        <Translate contentKey="calvaryErpApp.accountTransaction.home.title">Account Transactions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="calvaryErpApp.accountTransaction.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/account-transaction/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="calvaryErpApp.accountTransaction.home.createLabel">Create new Account Transaction</Translate>
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
                  placeholder={translate('calvaryErpApp.accountTransaction.home.search')}
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
        {accountTransactionList && accountTransactionList.length > 0 ? (
          <Table responsive>
            <thead>
            <tr>
              <th className="hand" onClick={sort('id')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.id">ID</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
              </th>
              <th className="hand" onClick={sort('transactionDate')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.transactionDate">Transaction Date</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('transactionDate')} />
              </th>
              <th className="hand" onClick={sort('description')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.description">Description</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
              </th>
              <th className="hand" onClick={sort('referenceNumber')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.referenceNumber">Reference Number</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('referenceNumber')} />
              </th>
              <th className="hand" onClick={sort('wasProposed')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.wasProposed">Was Proposed</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('wasProposed')} />
              </th>
              <th className="hand" onClick={sort('wasPosted')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.wasPosted">Was Posted</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('wasPosted')} />
              </th>
              <th className="hand" onClick={sort('wasDeleted')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.wasDeleted">Was Deleted</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('wasDeleted')} />
              </th>
              <th className="hand" onClick={sort('wasApproved')}>
                <Translate contentKey="calvaryErpApp.accountTransaction.wasApproved">Was Approved</Translate>{' '}
                <FontAwesomeIcon icon={getSortIconByFieldName('wasApproved')} />
              </th>
              <th />
            </tr>
            </thead>
            <tbody>
            {accountTransactionList.map((accountTransaction, i) => (
              <tr key={`entity-${i}`} data-cy="entityTable">
                <td>
                  <Button tag={Link} to={`/account-transaction/${accountTransaction.id}`} color="link" size="sm">
                    {accountTransaction.id}
                  </Button>
                </td>
                <td>
                  {accountTransaction.transactionDate ? (
                    <TextFormat type="date" value={accountTransaction.transactionDate} format={APP_LOCAL_DATE_FORMAT} />
                  ) : null}
                </td>
                <td>{accountTransaction.description}</td>
                <td>{accountTransaction.referenceNumber}</td>
                <td>{accountTransaction.wasProposed ? 'true' : 'false'}</td>
                <td>{accountTransaction.wasPosted ? 'true' : 'false'}</td>
                <td>{accountTransaction.wasDeleted ? 'true' : 'false'}</td>
                <td>{accountTransaction.wasApproved ? 'true' : 'false'}</td>
                <td className="text-end">
                  <div className="btn-group flex-btn-group-container">
                    <Button
                      tag={Link}
                      to={`/account-transaction/${accountTransaction.id}`}
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
                      to={`/account-transaction/${accountTransaction.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                      to={`/account-transaction/${accountTransaction.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                      color="danger"
                      size="sm"
                      data-cy="entityDeleteButton"
                    >
                      <FontAwesomeIcon icon="trash" />{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                    </Button>
                    <Button
                      tag={Link}
                      to={`/account-transaction/${accountTransaction.id}/post?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                      color="danger"
                      size="sm"
                      data-cy="entityDeleteButton"
                    >
                      <FontAwesomeIcon icon="signs-post" />{' '}
                      <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.post">Posting</Translate>
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
              <Translate contentKey="calvaryErpApp.accountTransaction.home.notFound">No Account Transactions found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={accountTransactionList && accountTransactionList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default AccountTransaction;
