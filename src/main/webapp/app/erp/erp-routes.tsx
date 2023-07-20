import ErrorBoundaryRoutes from "app/shared/error/error-boundary-routes";
import {Route} from "react-router-dom";
import AccountTransaction from "app/erp/account-transaction";
import React from "react";

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="account-transaction/*" element={<AccountTransaction />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
