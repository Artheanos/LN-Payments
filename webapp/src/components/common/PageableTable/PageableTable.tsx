import React, { ChangeEvent, ReactElement } from 'react'
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

import { Pageable } from 'webService/interface/pageable'

interface Props<T> {
  pageElements: Pageable<T> | undefined
  distinguishedData?: T
  mapper: (value: T, key: number, highlighted?: boolean) => ReactElement
  headers: string[]
  queryElements: (page?: number, size?: number) => void
  loading: boolean
}

export const PageableTable = <T,>({
  mapper,
  pageElements,
  queryElements,
  distinguishedData,
  headers,
  loading
}: Props<T>) => {
  const { t } = useTranslation('common')

  const handleChangePage = (
    event: React.MouseEvent<HTMLButtonElement, MouseEvent> | null,
    newPage: number
  ) => {
    if (pageElements) {
      queryElements(newPage, pageElements?.pageable.pageSize)
    }
  }

  const handleChangeRowsPerPage = (
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    if (pageElements) {
      queryElements(0, parseInt(event!.target.value))
    }
  }

  return (
    <div className="overflow-y-auto grow text-center">
      {loading ? (
        <CircularProgress />
      ) : (!pageElements || pageElements.empty) && !distinguishedData ? (
        <p className="pb-10 italic text-gray-500">{t('error.noEntries')}</p>
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
              {distinguishedData && mapper(distinguishedData, 0, true)}
              {pageElements?.content.map((element: T, key: number) =>
                mapper(element, key)
              )}
            </TableBody>
            {pageElements && (
              <TableFooter>
                <TableRow>
                  <TablePagination
                    rowsPerPageOptions={[
                      2,
                      10,
                      20,
                      { label: 'All', value: -1 }
                    ]}
                    count={pageElements!.totalElements}
                    rowsPerPage={pageElements!.pageable.pageSize}
                    page={pageElements!.pageable.pageNumber}
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
