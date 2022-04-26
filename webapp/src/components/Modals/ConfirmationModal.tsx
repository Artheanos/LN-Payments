import React from 'react'
import { Box, Button, Modal } from '@mui/material'
import { useTranslation } from 'react-i18next'

interface Props {
  confirmButtonContent?: string
  cancelButtonContent?: string
  message: string
  onConfirm?: () => void
  open: boolean
  setOpen: (open: false) => void
}

export const ConfirmationModal: React.FC<Props> = ({
  confirmButtonContent,
  cancelButtonContent,
  message,
  onConfirm,
  open,
  setOpen
}) => {
  const { t } = useTranslation('common')

  const hideModal = () => setOpen(false)

  const confirmHandler = () => {
    hideModal()
    onConfirm?.()
  }

  return (
    <Modal
      open={open}
      onClose={() => hideModal()}
      aria-labelledby="child-modal-title"
      aria-describedby="child-modal-description"
    >
      <Box className="p-7 bg-white rounded-xl absolute-center">
        <p className="mb-5">{message}</p>
        <div className="flex gap-5 justify-end">
          {cancelButtonContent && (
            <Button
              color="warning"
              variant="contained"
              className="bg-purple-300"
              onClick={() => hideModal()}
            >
              {cancelButtonContent}
            </Button>
          )}
          <Button
            variant="contained"
            className="bg-purple-300"
            onClick={() => confirmHandler()}
          >
            {confirmButtonContent || t('confirm')}
          </Button>
        </div>
      </Box>
    </Modal>
  )
}
