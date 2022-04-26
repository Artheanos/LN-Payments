import React, { useState } from 'react'
import { Button } from '@mui/material'
import { ConfirmationModal } from '../Modals/ConfirmationModal'
import { useTranslation } from 'react-i18next'

type Props = {
  text: string
  action: () => void
  modalMessage?: string
  disabled?: boolean
}

export const ActionButton: React.FC<Props> = ({
  text,
  action,
  modalMessage,
  disabled
}) => {
  const { t } = useTranslation('wallet')
  const [openModal, setOpenModal] = useState(false)

  return (
    <>
      <Button
        variant="contained"
        onClick={() => (modalMessage ? setOpenModal(true) : action())}
        disabled={disabled}
        sx={{
          borderRadius: '0.75rem',
          textTransform: 'none'
        }}
      >
        {text}
      </Button>
      {modalMessage && (
        <ConfirmationModal
          confirmButtonContent={t('actions.yes')}
          message={modalMessage}
          onConfirm={action}
          open={openModal}
          setOpen={setOpenModal}
        />
      )}
    </>
  )
}
