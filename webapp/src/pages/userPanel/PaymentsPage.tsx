import React from 'react'

import Panel from 'components/common/Panel'
import { HistoryAll } from 'components/History'

export const PaymentsPage: React.FC = () => (
  <Panel.Container>
    <Panel.Header title="Payments" />
    <Panel.Body table>
      <HistoryAll />
    </Panel.Body>
  </Panel.Container>
)
