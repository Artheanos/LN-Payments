import React, { ReactElement, useCallback, useEffect, useState } from 'react'

import { Response } from 'webService/requests'
import { PageRequest, Pageable } from 'webService/interface/pageable'
import { PageableTable } from './PageableTable'

interface Props<T> {
  apiRequest: (params: PageRequest) => Promise<Response<Pageable<T>>>
  mapper: (value: T, key: number, highlighted?: boolean) => ReactElement
  headers: string[]
  reloadDependency?: unknown
}

export const ApiPageableTable = <T,>({
  apiRequest,
  headers,
  mapper,
  reloadDependency
}: Props<T>) => {
  const [elements, setElements] = useState<Pageable<T>>()
  const [loading, setLoading] = useState(true)

  const queryElements = useCallback(
    async (page = 0, size = 10) => {
      setLoading(true)
      const { data } = await apiRequest!({ page, size })
      if (data) {
        setElements(data)
      }
      setLoading(false)
    },
    [apiRequest]
  )

  useEffect(() => {
    queryElements()
  }, [queryElements, reloadDependency])

  return (
    <PageableTable
      loading={loading}
      pageElements={elements}
      mapper={mapper}
      headers={headers}
      queryElements={queryElements}
    />
  )
}
