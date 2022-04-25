import React from 'react'

import Panel from 'components/common/Panel'
import { History } from 'components/History'

export const HistoryPage: React.FC = () => (
  <Panel.Container>
    <Panel.Header title="History" />
    <Panel.Body table>
      <History />
    </Panel.Body>
  </Panel.Container>
)
