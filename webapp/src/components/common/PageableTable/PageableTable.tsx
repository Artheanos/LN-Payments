import React, {
  ChangeEvent,
  ReactElement,
  useCallback,
  useEffect,
  useState
} from 'react'
import {
  Box,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableFooter,
  TableHead,
  TablePagination,
  TableRow
} from '@mui/material'
import { useTranslation } from 'react-i18next'

import { Response } from 'api'
import {
  PageRequest,
  Pageable
} from 'common-ts/dist/webServiceApi/interface/pageable'

interface Props<T> {
  apiRequest: (params: PageRequest) => Promise<Response<Pageable<T>>>
  mapper: (value: T, key: number) => ReactElement
  headers: string[]
}

export const PageableTable = <T,>({
  apiRequest,
  mapper,
  headers
}: Props<T>) => {
  const { t } = useTranslation('common')
  const [elements, setElements] = useState<Pageable<T>>()
  const [loading, setLoading] = useState(true)

  const queryElements = useCallback(
    async (page = 0, size = 10) => {
      setLoading(true)
      const { data } = await apiRequest({ page, size })
      if (data) {
        setElements(data)
      }
      setLoading(false)
    },
    [apiRequest]
  )

  useEffect(() => {
    queryElements()
  }, [queryElements])

  const handleChangePage = (
    event: React.MouseEvent<HTMLButtonElement, MouseEvent> | null,
    newPage: number
  ) => {
    if (elements) {
      queryElements(newPage, elements?.pageable.pageSize)
    }
  }

  const handleChangeRowsPerPage = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    if (elements) {
      queryElements(0, parseInt(event!.target.value))
    }
  }

  return (
    <div className="overflow-y-auto grow text-center">
      {loading ? (
        <CircularProgress />
      ) : !elements || elements.empty ? (
        <p className="italic text-gray-500">{t('noEntries')}</p>
      ) : (
        <TableContainer component={Box}>
          <Table>
            <TableHead>
              <TableRow
                className="bg-gray-100"
                sx={{
                  ['& .MuiTableCell-root']: { fontWeight: 'bold' }
                }}
              >
                {headers.map((header, key) => (
                  <TableCell key={key}>{header}</TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {elements?.content.map((element: T, key: number) =>
                mapper(element, key)
              )}
            </TableBody>
            {elements && (
              <TableFooter>
                <TableRow>
                  <TablePagination
                    rowsPerPageOptions={[
                      2,
                      10,
                      20,
                      { label: 'All', value: -1 }
                    ]}
                    count={elements!.totalElements}
                    rowsPerPage={elements!.pageable.pageSize}
                    page={elements!.pageable.pageNumber}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                  />
                </TableRow>
              </TableFooter>
            )}
          </Table>
        </TableContainer>
      )}
    </div>
  )
}
